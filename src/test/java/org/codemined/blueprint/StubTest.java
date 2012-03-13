package org.codemined.blueprint;

import org.testng.annotations.Test;

@Test
public class StubTest {
/*
  interface Dummy {

    @UseType(Integer.class) int someInteger();

    String someString();
  }
  
  IMocksControl control;
  
  private Deserializer mockDeserializer;

  private Stub<Dummy> stub;
  
  @BeforeMethod
  public void setUp() {
    control = createControl();
    mockDeserializer = control.createMock(Deserializer.class);
    stub = new Stub<Dummy>(Dummy.class, mockDeserializer);
  }
  
  public void testProxyInteger() {
    expect(mockDeserializer.deserialize(Integer.class, Integer.class, "someInteger"))
        .andReturn(42);
    control.replay();
    assertEquals(42, stub.getProxy().someInteger());
    control.verify();
  }

  public void testProxyString() {
    expect(mockDeserializer.deserialize(String.class, null, "someString"))
        .andReturn("hello kitty");
    control.replay();
    assertEquals("hello kitty", stub.getProxy().someString());
    control.verify();
  }
*/
}
