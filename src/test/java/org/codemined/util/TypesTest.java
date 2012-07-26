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
