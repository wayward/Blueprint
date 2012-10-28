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
import org.codemined.blueprint.impl.IdentityKeyResolver;
import org.codemined.util.Path;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.testng.Assert.assertEquals;

@Test
public class StubTest {

  @Mocked Deserializer mockDeserializer;

  @Mocked
  ConfigNode<?> mockNode;

  @SuppressWarnings("unused")
  private interface ChildIface {
    int childMethod();
  }

  @SuppressWarnings("unused")
  private interface BlueprintIface extends ChildIface {
    int aMethod();
  }

  @Test
  public void picksUpInheritedMethods()
          throws Throwable {
    new Expectations() {{
      mockNode.getNode("childMethod"); result = mockNode;
      mockDeserializer.deserialize(Integer.class, null, "childMethod", mockNode); result = 42;
    }};
    Stub<BlueprintIface> stub = new Stub<BlueprintIface>(BlueprintIface.class,
            mockNode, new Path<String>(), mockDeserializer, new IdentityKeyResolver());
    Method method = BlueprintIface.class.getMethod("childMethod");
    Object value = stub.invoke(stub.getProxy(), method, null);

    assertEquals(value, 42);
  }

}
