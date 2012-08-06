/*
 * Copyright 2012. Zoran Rilak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codemined.blueprint;

import org.codemined.util.Tree;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

/**
 * @author Zoran Rilak
 */
class Deserializer {

  /* keep `valueOf' last to allow overriding it.
  ValueOf is the standard name for a deserializer method in JRE, but we sometimes want to
  supply our own method to convert Strings into instances of our type. One example would be
  deserializing Enums.  Enum.valueOf() recognized only uppercase Enum constants; by adding a
  static method called "fromString", "parse", or "deserialize" to our Enum class, we can
  supply custom deserialization logic. */
  private static final String[] STATIC_DESERIALIZER_METHODS = {
          "fromString",
          "parse",
          "deserialize",
          "valueOf"
  };

  private final ClassLoader classLoader;


  public Deserializer(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }


  @SuppressWarnings("unchecked")
  public <T> T deserialize(Class<?> returnType, Class<?> hintedType, Tree<String,String> tree) {

    /* Maps and collections require a type hint to use as element type. */
    if (Map.class.isAssignableFrom(returnType)) {
      if (hintedType == null) {
        throw new BlueprintException("Maps require a type hint");
      }
      return (T) deserializeMap(hintedType, tree);
    }

    if (Collection.class.isAssignableFrom(returnType)) {
      if (hintedType == null) {
        throw new BlueprintException("Collections require a type hint");
      }
      return (T) deserializeCollection((Class<Collection>)returnType, hintedType, tree);
    }

    /* Other (non-map, non-collection) return types will be superseded by the
    hinted type if given; it the types are not compatible, an exception is thrown. */
    if (hintedType != null) {
      try {
        returnType = hintedType.asSubclass(returnType);
      } catch (ClassCastException e) {
        throw new BlueprintException("Type hint " + hintedType.getCanonicalName() +
                ", is not assignment-compatible with the" +
                " method's return type, " + returnType.getCanonicalName());
      }
    }

    if (returnType.isInterface()) {
      return (T) deserializeInterface(returnType, tree);
    }

    if (Class.class.isAssignableFrom(returnType)) {
      return (T) deserializeClass(tree);
    }

    /* if no special handling applies, deserialize as a simple type
    (through a static factory method or String ctor) */
    return (T) deserializeSimpleType(returnType, tree);
  }


  /**
   *
   * @param elementType Type of values stored in the map.
   * @param tree Configuration tree to deserialize from.
   * @param <E> Element type.
   * @return Deserialized instance of the given map type.
   */
  public <E> Map<String,E> deserializeMap(Class<E> elementType, Tree<String,String> tree) {
    final Map<String,E> targetMap = Reifier.reifyStringMap();
    for (Tree<String,String> subTree : tree) {
      final E element = deserialize(elementType, null, subTree);
      targetMap.put(subTree.getKey(), element);
    }
    return targetMap;
  }


  /**
   *
   * @param type Collection type to deserialize to.
   * @param elementType Type of elements contained in the collection.
   * @param tree Configuration tree to deserialize from.
   * @param <E> Element type.
   * @return Deserialized collection.
   */
  @SuppressWarnings("unchecked")
  private <E, T extends Collection<E>> T deserializeCollection(Class<T> type,
                                                            Class<E> elementType,
                                                            Tree<String,String> tree) {
    final T targetCollection = (T) Reifier.reifyCollection(type);
    // split at commas, ignoring whitespace (multiple commas yield empty elements)
    //TODO provide for custom list splitters (although this is going to be OK 99% of the time)
    String[] elements = tree.getValue().split("\\s*,\\s*");
    for (String eStr : elements) {
      E element = deserializeSimpleTypeFromValue(elementType, eStr);
      targetCollection.add(element);
    }
    return targetCollection;
  }


  /**
   *
   * @param type Interface to deserialize to.
   * @param tree Configuration tree to deserialize from.
   * @param <T> Interface type.
   * @return Deserialized instance of the given interface type.
   */
  private <T> T deserializeInterface(Class<T> type, Tree<String,String> tree) {
    return new Stub<T>(type, tree, this).getProxy();
  }


  /**
   * @param tree Configuration tree to deserialize from.
   * @return Deserialized class object.
   */
  private Class<?> deserializeClass(Tree<String,String> tree) {
    try {
      return classLoader.loadClass(tree.getValue());
    } catch (ClassNotFoundException e) {
      throw new BlueprintException(e);
    }
  }

  /**
   *
   * @param type Simple type to deserialize to.
   * @param tree Configuration tree to deserialize from.
   * @param <T> Simple type (primitive, boxed or String).
   * @return Deserialized instance of a simple type.
   */
  private <T> T deserializeSimpleType(Class<T> type, Tree<String,String> tree) {
    return deserializeSimpleTypeFromValue(type, tree.getValue());
  }


  /**
   *
   * @param type Simple type (String, primitive or boxed) to deserialize to.
   * @param value String to deserialize from.
   * @param <T> Simple type (primitive, boxed or String).
   * @return Deserialized instance of a simple type.
   */
  private <T> T deserializeSimpleTypeFromValue(Class<T> type, String value) {
    T obj;
    for (String methodName : STATIC_DESERIALIZER_METHODS) {
      obj = useStaticDeserializer(type, methodName, value);
      if (obj != null) {
        return obj;
      }
    }
    obj = useConstructorDeserializer(type, value);
    if (obj != null) {
      return obj;
    }

    throw new BlueprintException("No appropriate deserialization method found" +
            " for type " + type.getName());
  }

  /**
   * Attempts to deserialize an object using a static factory method.
   * <p>
   *   The type is queried for a method that meets the following criteria:
   *   <ul>
   *     <li>is public and static;</li>
   *     <li>has the name {@code methodName};</li>
   *     <li>takes one String parameter;</li>
   *     <li>{@code type} is assignable from this method's return type, as per {@link Class#isAssignableFrom(Class)}.</li>
   *   </ul>
   *   If no such method is found, useStaticDeserializer returns null.
   * </p>
   * @param type Type to deserialize into.
   * @param methodName Name of the static factory method to use.
   * @param value String to deserialize from.
   * @param <T> Type to deserialize into.
   * @return Deserialized instance or null if no adequate method is found.
   * @throws BlueprintException if an exception is thrown from inside the deserializer method.
   */
  private <T> T useStaticDeserializer(Class<T> type, String methodName, String value) {
    Method method;

    try {
      method = type.getMethod(methodName, String.class);
      if (! Modifier.isStatic(method.getModifiers()) ||
              ! type.isAssignableFrom(method.getReturnType())) {
        return null;
      }
    } catch (NoSuchMethodException e) {
      return null;
    }

    try {
      return type.cast(method.invoke(null, value));
    } catch (Exception e) {
      throw new BlueprintException("Failed to deserialize configuration item" +
              " as an instance of " + type.getCanonicalName() +
              ", using method " + methodName + "(String)" +
              ", from value \"" + value + "\"", e);
      }
  }


  /**
   * Attempts to deserialize an object using the String constructor.
   * <p>
   *   The type is queried for a constructor that meets the following criteria:
   *   <ul>
   *     <li>is public;</li>
   *     <li>takes one String parameter.</li>
   *   </ul>
   *   If no such constructor is found, useConstructorDeserializer returns null.
   * </p>
   * @param type Type to deserialize into.
   * @param value String to deserialize from.
   * @param <T> Type to deserialize into.
   * @return Deserialized instance or null if no adequate constructor is found.
   * @throws BlueprintException if an exception is thrown from inside the constructor.
   */
  private <T> T useConstructorDeserializer(Class<T> type, String value) {
    Constructor<T> ctor;
    try {
      ctor = type.getConstructor(String.class);
    } catch (NoSuchMethodException e) {
      return null;
    }

    try {
      return ctor.newInstance(value);
    } catch (Exception e) {
      throw new BlueprintException("Failed to deserialize configuration item" +
              " as an instance of " + type.getCanonicalName() +
              ", using constructor(String)" +
              ", from value \"" + value + "\"", e);
    }
  }

}
