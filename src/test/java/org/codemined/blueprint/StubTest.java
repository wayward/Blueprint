package org.codemined.blueprint;

import mockit.Expectations;
import mockit.Mocked;
import org.codemined.Tree;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.testng.Assert.assertEquals;

@Test
public class StubTest {

  @Mocked Deserializer mockDeserializer;

  @Mocked Tree<String,String> mockTree;

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
      mockTree.get("childMethod"); result = mockTree;
      mockDeserializer.deserialize(Integer.class, null, mockTree); result = 42;
    }};
    Stub<BlueprintIface> stub = new Stub<BlueprintIface>(BlueprintIface.class, mockTree, mockDeserializer);
    Method method = BlueprintIface.class.getMethod("childMethod");
    Object value = stub.invoke(stub.getProxy(), method, null);

    assertEquals(value, 42);
  }

}
