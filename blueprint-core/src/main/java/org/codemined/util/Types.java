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

package org.codemined.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Programmatic boxing of primitive types.
 * 
 * @author Zoran Rilak
 */
public class Types {
  private static final Map<Class<?>, Class<?>> translationTable;


  static {
    // We can do this because boxed() and unboxed() check if types are primitives.
    translationTable = new HashMap<Class<?>, Class<?>>();
    translationTable.put(byte.class, Byte.class);
    translationTable.put(Byte.class, byte.class);
    translationTable.put(short.class, Short.class);
    translationTable.put(Short.class, short.class);
    translationTable.put(int.class, Integer.class);
    translationTable.put(Integer.class, int.class);
    translationTable.put(long.class, Long.class);
    translationTable.put(Long.class, long.class);
    translationTable.put(float.class, Float.class);
    translationTable.put(Float.class, float.class);
    translationTable.put(double.class, Double.class);
    translationTable.put(Double.class, double.class);
    translationTable.put(boolean.class, Boolean.class);
    translationTable.put(Boolean.class, boolean.class);
    translationTable.put(char.class, Character.class);
    translationTable.put(Character.class, char.class);
  }


  /** Boxes a primitive type, else passes it unchanged.
   * 
   *  @param type may be null
   */
  @SuppressWarnings("unchecked")
  public static <T> Class<T> boxed(Class<?> type) {
    if (type == null) {
      return null;
    }
    if (type.isPrimitive()) {
      type = translationTable.get(type);
    }
    return (Class<T>) type;
  }

  @SuppressWarnings("unchecked")
  public static <T> Class<T> unboxed(Class<?> type) {
    if (type == null) {
      return null;
    }
    if (! type.isPrimitive() && translationTable.containsKey(type)) {
      type = translationTable.get(type);
    }
    return (Class<T>) type;
  }

}
