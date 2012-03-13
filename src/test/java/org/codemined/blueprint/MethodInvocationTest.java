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

import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.testng.Assert.assertEquals;

/**
 * @author Zoran Rilak
 */
@Test
public class MethodInvocationTest {

  interface I {
    int intMethod_noHints();
    int intMethod_paramHint(Class<?> hint);
    @UseType(byte.class) int intMethod_annHint();
    @UseType(byte.class) int intMethod_annAndArgHint(Class<?> hint);
  }

  @Test
  public void boxesPrimitives()
          throws NoSuchMethodException {
    MethodInvocation inv = new MethodInvocation(I.class.getMethod("intMethod_noHints"), null);
    assertEquals(inv.getReturnType(), Integer.class);
  }

  @Test
  void methodWithoutTypeHints()
          throws NoSuchMethodException {
    Method m = I.class.getMethod("intMethod_noHints");
    MethodInvocation inv = new MethodInvocation(m, null);
  
    assertEquals(inv.getReturnType(), Integer.class);
    assertEquals(inv.getHintedType(), null);
  }

  @Test
  void methodWithParameterHint()
          throws NoSuchMethodException {
    Method m = I.class.getMethod("intMethod_paramHint", Class.class);
    MethodInvocation inv_noHint = new MethodInvocation(m, null);
    MethodInvocation inv_byteHint = new MethodInvocation(m, new Object[] { byte.class });

    assertEquals(inv_noHint.getReturnType(), Integer.class);
    assertEquals(inv_noHint.getHintedType(), null);
    assertEquals(inv_byteHint.getReturnType(), Integer.class);
    assertEquals(inv_byteHint.getHintedType(), Byte.class);
  }

  @Test
  void methodWithAnnotationHint()
          throws NoSuchMethodException {
    Method m = I.class.getMethod("intMethod_annHint");
    MethodInvocation inv = new MethodInvocation(m, null);

  }

  @Test
  void methodWithAnnotationAndArgHint()
          throws NoSuchMethodException {
    Method m = I.class.getMethod("intMethod_annAndArgHint", Class.class);
    MethodInvocation inv_noArgHint = new MethodInvocation(m, null);
    MethodInvocation inv_withArgHint = new MethodInvocation(m, new Object[] { long.class });

    assertEquals(inv_noArgHint.getReturnType(), Integer.class);
    assertEquals(inv_noArgHint.getHintedType(), Byte.class);
    assertEquals(inv_withArgHint.getReturnType(), Integer.class);
    assertEquals(inv_withArgHint.getHintedType(), Long.class);
  }
    
}
