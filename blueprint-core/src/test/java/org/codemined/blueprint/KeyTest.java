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

import mockit.Expectations;
import mockit.Mocked;
import org.codemined.util.AbstractTree;
import org.codemined.util.Path;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.testng.Assert.assertEquals;

/**
 * Tests for the Key annotation.
 *
 * @author Zoran Rilak
 * @version 0.1
 * @since 0.1
 */
@Test
public class KeyTest {

  @SuppressWarnings("unused")
  private interface Iface {
    @Key("keyOverride") int keyOverridesMethod();
  }

  @Mocked AbstractTree<String,String> mockTree;

  @Test
  public void keyAnnotationOverridesMapping()
          throws Throwable {
    new Expectations() {{
      mockTree.get("keyOverride"); result = mockTree;
      mockTree.getValue(); result = "42";
    }};
    Deserializer deserializer = new Deserializer(Iface.class.getClassLoader());
    Stub<Iface> stub = new Stub<Iface>(Iface.class, mockTree, new Path<String>(), deserializer);
    Method method = Iface.class.getMethod("keyOverridesMethod");
    Object value = stub.invoke(stub.getProxy(), method, null);
    assertEquals(value, 42);
  }

  /*
   Support for javax.inject.Named is dropped for the time being.

  @Test
  public void namedAnnotationOverridesMapping()
          throws Throwable {
    new Expectations() {{
      mockTree.get("namedOverride"); result = mockTree;
      mockTree.getValue(); result = "42";
    }};
    Deserializer deserializer = new Deserializer(Iface.class.getClassLoader());
    Stub<Iface> stub = new Stub<Iface>(Iface.class, mockTree, new Path<String>(), deserializer);
    Method method = Iface.class.getMethod("namedOverridesMethod");
    Object value = stub.invoke(stub.getProxy(), method, null);
    assertEquals(value, 42);
  }

  @Test
  public void keyAnnotationSupersedesNamedAnnotation()
          throws Throwable {
    new Expectations() {{
      mockTree.get("keyOverride"); result = mockTree;
      mockTree.getValue(); result = "42";
    }};
    Deserializer deserializer = new Deserializer(Iface.class.getClassLoader());
    Stub<Iface> stub = new Stub<Iface>(Iface.class, mockTree, new Path<String>(), deserializer);
    Method method = Iface.class.getMethod("bothOverrideMethod");
    Object value = stub.invoke(stub.getProxy(), method, null);
    assertEquals(value, 42);
  }
  */

}
