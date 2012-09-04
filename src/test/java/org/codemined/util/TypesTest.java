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

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

@Test
public class TypesTest {

  public void testNull() {
    assertNull(Types.deprimitivize(null));
  }

  public void testAllPrimitives() {
    assertEquals(Types.deprimitivize(byte.class), Byte.class);
    assertEquals(Types.deprimitivize(short.class), Short.class);
    assertEquals(Types.deprimitivize(int.class), Integer.class);
    assertEquals(Types.deprimitivize(long.class), Long.class);
    assertEquals(Types.deprimitivize(float.class), Float.class);
    assertEquals(Types.deprimitivize(double.class), Double.class);
    assertEquals(Types.deprimitivize(boolean.class), Boolean.class);
    assertEquals(Types.deprimitivize(char.class), Character.class);
  }
  
  public void testNonPrimitive() {
    assertEquals(String.class, Types.deprimitivize(String.class));
  }

}
