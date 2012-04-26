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

import org.codemined.Tree;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @author Zoran Rilak
 */
class Deserializer {
  /* keep `valueOf' last so that we can circumvent this very common
  static method with a custom one from among those above it.
  Very useful for e.g. case-insensitive deserialization of enums. */
  private static final String[] STATIC_DESERIALIZER_METHODS = {
          "fromString",
          "parse",
          "deserialize",
          "valueOf"
  };

  private final Tree<String,String> tree;
  private final ClassLoader classLoader;


  public Deserializer(Tree<String,String> tree, ClassLoader classLoader) {
    this.tree = tree;
    this.classLoader = classLoader;
  }


  @SuppressWarnings("unchecked")
  public <T> T deserialize(Class<T> returnType, @SuppressWarnings("rawtypes") Class hintedType, String key) {

    /* Maps and collections require type hint to determine the element type. */
    if (Map.class.isAssignableFrom(returnType)) {
      if (hintedType == null) {
        throw new BlueprintException("Maps require a type hint");
      }
      return (T) deserializeMap(hintedType, key);
    }

    if (Collection.class.isAssignableFrom(returnType)) {
      if (hintedType == null) {
        throw new BlueprintException("Collections require a type hint");
      }
      return (T) deserializeCollection(returnType, hintedType, key);
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
      return deserializeInterface(returnType, key);
    }

    if (Class.class.isAssignableFrom(returnType)) {
      return deserializeClass(key);
    }

    /* if no special handling applies, deserialize as a simple type
    (through a static factory method or String ctor) */
    return deserializeSimpleType(returnType, key);
  }


  /**
   *
   * @param elementType
   * @param key
   * @param <E>
   * @return
   */
  public <E> Map<String, E> deserializeMap(Class<E> elementType, String key) {
    final Map<String, E> targetMap = Reifier.reifyStringMap();
    final Tree<String,String> subTree = tree.get(key);
    if (subTree != null) {
      for (Tree<String,String> t : subTree) {
        final String k = t.key();
        final E element = deserialize(elementType, null, k);
        targetMap.put(k, element);
      }
    }
    return targetMap;
  }


  private <E> Collection<E> deserializeCollection(Class<?> type, Class<E> elementType, String key) {
    final Collection<E> targetCollection = Reifier.reifyCollection(type);

    for (Tree<String,String> t : tree.get(key)) {
      targetCollection.add(deserializeSimpleTypeFromValue(elementType, t.value()));
    }
    return targetCollection;
  }


  private <T> T deserializeInterface(Class<T> type, String key) {
    // TODO will need a better way to determine which classloader to use for child deserializers
    // TODO won't work for intermediary, nonexistent nodes (i.e. when a.b.c exists but a.b does not)
    final Tree<String,String> subTree = tree.get(key);
    final Deserializer childDeserializer = new Deserializer(subTree, type.getClassLoader());
    return new Stub<T>(type, childDeserializer).getProxy();
  }


  /**
   * @param key
   * @param <T>
   * @return
   */
  @SuppressWarnings("unchecked")
  private <T> T deserializeClass(String key) {
    try {
      return (T) classLoader.loadClass(tree.get(key).value());
    } catch (ClassNotFoundException e) {
      throw new BlueprintException(e);
    }
  }


  private <T> T deserializeSimpleType(Class<T> type, String key) {
    return deserializeSimpleTypeFromValue(type, tree.get(key).value());
  }

  private <T> T deserializeSimpleTypeFromValue(Class<T> type, String value) {
    T o;
    for (String methodName : STATIC_DESERIALIZER_METHODS) {
      o = useStaticDeserializer(type, methodName, value);
      if (o != null) {
        return o;
      }
    }
    o = useConstructorDeserializer(type, value);
    if (o != null) {
      return o;
    }

    throw new BlueprintException("No appropriate deserialization method found" +
            " for type " + type.getName());
  }

  private <T> T useStaticDeserializer(Class<T> type, String methodName, String value) {
    try {
      Method method = type.getMethod(methodName, String.class);
      if (Modifier.isStatic(method.getModifiers()) &&
              type.isAssignableFrom(method.getReturnType())) {
        return type.cast(method.invoke(null, value));
      }
      throw new NoSuchElementException();
    } catch (NoSuchMethodException e) {
      return null;
    } catch (Exception e) {
      throw new BlueprintException("Failed to deserialize configuration item" +
              " as an instance of " + type.getCanonicalName() +
              ", using method " + methodName + "(String)" +
              ", from value \"" + value + "\"", e);
    }
  }


  private <T> T useConstructorDeserializer(Class<T> type, String value) {
    try {
      Constructor<T> ctor = type.getConstructor(String.class);
      return ctor.newInstance(value);
    } catch (NoSuchMethodException e) {
      return null;
    } catch (Exception e) {
      throw new BlueprintException("Failed to deserialize configuration item" +
              " as an instance of " + type.getCanonicalName() +
              ", using constructor(String)" +
              ", from value \"" + value + "\"", e);
    }
  }

}
