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

import org.codemined.blueprint.source.Source;
import org.codemined.util.Types;

import java.lang.reflect.Array;
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

  private final KeyResolver keyResolver;

  private final Source source;


  public Deserializer(ClassLoader classLoader, KeyResolver keyResolver, Source source) {
    this.classLoader = classLoader;
    this.keyResolver = keyResolver;
    this.source = source;
  }


  @SuppressWarnings("unchecked")
  public <T> T deserialize(Class<?> returnType,
                           Class<?> hintedType,
                           Path path) {

    /* Maps and collections require a type hint to use as element type. */
    if (Map.class.isAssignableFrom(returnType)) {
      if (hintedType == null) {
        throw new BlueprintException("Maps require a non-null type hint");
      }
      return (T) deserializeMap(hintedType, path);
    }

    if (Collection.class.isAssignableFrom(returnType)) {
      if (hintedType == null) {
        throw new BlueprintException("Collections require a non-null type hint");
      }
      return (T) deserializeCollection((Class<Collection>)returnType, hintedType, path);
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
      return (T) deserializeInterface(returnType, path);
    }

    if (Class.class.isAssignableFrom(returnType)) {
      return (T) deserializeClass(path);
    }

    if (returnType.isArray()) {
      return (T) deserializeArray(returnType, path);
    }

    /* if no special handling applies, deserialize as a simple type
    (through a static factory method or String ctor) */
    return (T) deserializeSimpleType(returnType, path);
  }


  /**
   *
   * @param elementType Type of values stored in the map.
   * @param path
   * @param <E> Element type.
   * @return Deserialized instance of the given map type.
   */
  public <E> Map<String, E> deserializeMap(Class<E> elementType, Path path) {
    final Map<String, E> map = Reifier.reifyStringMap();
    for (String key : source.getSubKeys(path)) {
      final E element = deserialize(elementType, null, path.toNode(key));
      map.put(key, element);
    }
    return map;
  }


  /**
   *
   * @param type Collection type to deserialize to.
   * @param elementType Type of elements contained in the collection.
   * @param path
   * @param <E> Element type.
   * @return Deserialized collection.
   */
  @SuppressWarnings("unchecked")
  private <E, T extends Collection<E>> T deserializeCollection(Class<T> type,
                                                            Class<E> elementType,
                                                            Path path) {
    final T col = (T) Reifier.reifyCollection(type);
    for (int i = 0; i < source.getListSize(path); i++) {
      col.add(elementType.cast(deserialize(elementType, null, path.toIndex(i))));
      i++;
    }
    return col;
  }


  /**
   *
   * @param type Interface to deserialize to.
   * @param path
   * @param <T> Interface type.
   * @return Deserialized instance of the given interface type.
   */
  private <T> T deserializeInterface(Class<T> type, Path path) {
    return new Stub<T>(type, source, path, keyResolver).getProxy();
  }


  /**
   * @param path
   * @return Deserialized class object.
   */
  private Class<?> deserializeClass(Path path) {
    try {
      return classLoader.loadClass(source.getStringValue(path));
    } catch (ClassNotFoundException e) {
      throw new BlueprintException(e);
    }
  }

  private <T> T deserializeArray(Class<T> type, Path path) {
    Class<?> elementType = type.getComponentType();
    int size = source.getListSize(path);

    Object array = Array.newInstance(elementType, size);
    for (int i = 0; i < source.getListSize(path); i++) {
      if (elementType.isPrimitive()) {
        Array.set(array, i, deserializeSimpleType(Types.boxed(elementType), path.toIndex(i)));
      } else {
        Array.set(array, i, deserialize(elementType, null, path.toIndex(i)));
      }
    }

    return type.cast(array);
  }

  /**
   *
   * @param type Simple type to deserialize to.
   * @param path
   * @param <T> Simple type (primitive, boxed or String).
   * @return Deserialized instance of a simple type.
   */
  private <T> T deserializeSimpleType(Class<T> type, Path path) {
    return deserializeSimpleTypeFromValue(type, source.getStringValue(path));
  }


  /**
   *
   * @param type Simple type (String or boxed) to deserialize to.
   * @param value String to deserialize from.
   * @param <T> Simple type (primitive, boxed or String).
   * @return Deserialized instance of a simple type.
   */
  private <T> T deserializeSimpleTypeFromValue(Class<T> type, String value) {
    T o = null;

    for (String methodName : STATIC_DESERIALIZER_METHODS) {
      o = useStaticDeserializer(type, methodName, value);
      if (o != null) {
        break;
      }
    }
    if (o == null) {
      o = useConstructorDeserializer(type, value);
    }
    if (o == null) {
      throw new BlueprintException("No appropriate deserialization method found" +
              " for type " + type.getName());
    }

    return o;
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
