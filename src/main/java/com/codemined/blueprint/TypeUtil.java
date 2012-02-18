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

import java.util.HashMap;
import java.util.Map;

/**
 * Programmatic boxing of primitive types.
 * 
 * @author Zoran Rilak
 */
class TypeUtil {
  private static final Map<Class<?>, Class<?>> primitiveTable;


  static {
    primitiveTable = new HashMap<Class<?>, Class<?>>();
    primitiveTable.put(byte.class, Byte.class);
    primitiveTable.put(short.class, Short.class);
    primitiveTable.put(int.class, Integer.class);
    primitiveTable.put(long.class, Long.class);
    primitiveTable.put(float.class, Float.class);
    primitiveTable.put(double.class, Double.class);
    primitiveTable.put(boolean.class, Boolean.class);
    primitiveTable.put(char.class, Character.class);
  }


  /** Boxes a primitive type, else passes it unchanged.
   * 
   *  @param type may be null
   */
  public static <T> Class<T> deprimitivize(Class<?> type) {
    if (type == null) {
      return null;
    }
    if (type.isPrimitive()) {
      type = primitiveTable.get(type);
    }
    @SuppressWarnings("unchecked")  // it came from the primitives table, it must be typesafe.
    Class<T> returnType = (Class<T>) type;
    return returnType;
  }

}
