package org.codemined.util;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

@Test
public class TypeUtilTest {

  public void testNull() {
    assertNull(TypeUtil.deprimitivize(null));
  }

  public void testAllPrimitives() {
    assertEquals(TypeUtil.deprimitivize(byte.class), Byte.class);
    assertEquals(TypeUtil.deprimitivize(short.class), Short.class);
    assertEquals(TypeUtil.deprimitivize(int.class), Integer.class);
    assertEquals(TypeUtil.deprimitivize(long.class), Long.class);
    assertEquals(TypeUtil.deprimitivize(float.class), Float.class);
    assertEquals(TypeUtil.deprimitivize(double.class), Double.class);
    assertEquals(TypeUtil.deprimitivize(boolean.class), Boolean.class);
    assertEquals(TypeUtil.deprimitivize(char.class), Character.class);
  }
  
  public void testNonPrimitive() {
    assertEquals(String.class, TypeUtil.deprimitivize(String.class));
  }

}
