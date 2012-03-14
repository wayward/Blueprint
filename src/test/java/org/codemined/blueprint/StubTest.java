package org.codemined.blueprint;

import mockit.Expectations;
import mockit.Mocked;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.testng.Assert.assertEquals;

@SuppressWarnings("unused")
@Test
public class StubTest {
  final String CHILD_METHOD_NAME = "childMethod";

  @Mocked Deserializer mockDeserializer;

  interface ChildIface {
    int childMethod();
  }
  
  interface BlueprintIface extends ChildIface {
    int aMethod();
  }
  
  @Test
  public void picksUpInheritedMethods()
          throws Throwable {
    new Expectations() {{
      mockDeserializer.deserialize(Integer.class, null, CHILD_METHOD_NAME); result = 42;
    }};
    Stub<BlueprintIface> stub = new Stub<BlueprintIface>(BlueprintIface.class, mockDeserializer);
    Method method = BlueprintIface.class.getMethod(CHILD_METHOD_NAME);
    Object value = stub.invoke(stub.getProxy(), method, null);

    assertEquals(value, 42);
  }

}
