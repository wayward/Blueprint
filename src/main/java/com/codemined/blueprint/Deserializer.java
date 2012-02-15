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

package com.codemined.blueprint;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @author Zoran Rilak
 */
class Deserializer {
  private static final String[] STATIC_DESERIALIZER_METHODS = {
          "valueOf",
          "parse",
          "fromString",
          "deserialize"
  };

  private final Source source;
  private final ClassLoader classLoader;


  public Deserializer(Source source, ClassLoader classLoader) {
    this.source = source;
    this.classLoader = classLoader;
  }


  @SuppressWarnings("unchecked")
  public <T> T deserialize(Class<T> returnType, Class hintedType, String key) {

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

    if (Modifier.isInterface(returnType.getModifiers())) {
      return deserializeInterface(returnType, key);
    }

    if (Class.class.isAssignableFrom(returnType)) {
      return deserializeClass(key);
    }

    // else try to deserialize as a simple type (through a static factory method or ctor)
    // if a type hint is present, try to downcast it to the actual return type
    return deserializeSimpleType(returnType, source.getString(key));
  }


  /**
   *
   * @param elementType
   * @param rootPath
   * @param <E>
   * @return
   */
  public <E> Map<String, E> deserializeMap(Class<E> elementType, String rootPath) {
    final Iterator<String> iter = source.getSubComponents(rootPath);
    final Map<String, E> targetMap = Reifier.reifyStringMap();

    while (iter.hasNext()) {
      final String component = iter.next();
      final String path = source.composePath(rootPath, component);
      final E element = deserialize(elementType, null, path);
      targetMap.put(component, element);
    }
    return targetMap;
  }


  private <E> Collection<E> deserializeCollection(Class<?> type, Class<E> elementType, String key) {
    final Collection<String> values = source.getCollection(key);
    final Collection<E> targetCollection = Reifier.reifyCollection(type);

    for (String v : values) {
      targetCollection.add(deserializeSimpleType(elementType, v));
    }
    return targetCollection;
  }


  private <T> T deserializeInterface(Class<T> type, String key) {
    return new Stub<T>(type, source, key).getProxy();
  }


  /**
   * @param key
   * @param <T>
   * @return
   */
  @SuppressWarnings("unchecked")
  private <T> T deserializeClass(String key) {
    try {
      return (T) classLoader.loadClass(source.getString(key));
    } catch (ClassNotFoundException e) {
      throw new BlueprintException(e);
    }
  }


  private <T> T deserializeSimpleType(Class<T> type, String value) {
    T o;
    for (String methodName : STATIC_DESERIALIZER_METHODS) {
      if ((o = useStaticDeserializer(type, methodName, value)) != null) {
        return o;
      }
    }
    if ((o = useConstructorDeserializer(type, value)) != null) {
      return o;
    }

    throw new BlueprintException("No appropriate deserialization method found" +
            " for type " + type.getName());
  }


  private static <T> T useStaticDeserializer(Class<T> type, String methodName, String value) {
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


  private static <T> T useConstructorDeserializer(Class<T> type, String value) {
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

