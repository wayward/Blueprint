package org.codemined.blueprint;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test
public class TypeUtilTest {

  public void testAllPrimitives() {
    assertEquals(Byte.class, TypeUtil.deprimitivize(byte.class));
    assertEquals(Short.class, TypeUtil.deprimitivize(short.class));
    assertEquals(Integer.class, TypeUtil.deprimitivize(int.class));
    assertEquals(Long.class, TypeUtil.deprimitivize(long.class));
    assertEquals(Float.class, TypeUtil.deprimitivize(float.class));
    assertEquals(Double.class, TypeUtil.deprimitivize(double.class));
    assertEquals(Boolean.class, TypeUtil.deprimitivize(boolean.class));
    assertEquals(Character.class, TypeUtil.deprimitivize(char.class));
  }
  
  public void testNonPrimitive() {
    assertEquals(String.class, TypeUtil.deprimitivize(String.class));
  }

}
