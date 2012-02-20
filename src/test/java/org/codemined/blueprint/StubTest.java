package org.codemined.blueprint;

import org.easymock.classextension.IMocksControl;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createControl;
import static org.testng.Assert.assertEquals;

@Test
public class StubTest {
  
  interface Dummy {
    @UseType(Integer.class) int someInteger();
    
    String someString();
    
    @UseType(Integer.class) String confusedTypes();
  }
  
  IMocksControl control;
  
  private Deserializer mockDeserializer;

  private Source mockSource;
  private Stub<Dummy> stub;
  
  // For some reason, this annotation doesn't work, so calling setup explicitly in tests.  I've
  // been using testNg for one hour and I already hate it.
  @BeforeTest
  public void setUp() {
    control = createControl();
    mockSource = control.createMock(Source.class);
    mockDeserializer = control.createMock(Deserializer.class);
    stub = new Stub<Dummy>(Dummy.class, mockSource, "bar", mockDeserializer);
  }
  
  public void testProxyInteger() {
    setUp();  // WTF?!
    expect(mockSource.composePath("bar", "someInteger")).andReturn("bar.someInteger");
    expect(mockDeserializer.deserialize(Integer.class, Integer.class, "bar.someInteger"))
        .andReturn(42);
    control.replay();
    assertEquals(42, stub.getProxy().someInteger());
    control.verify();
  }

  public void testProxyString() {
    setUp();
    expect(mockSource.composePath("bar", "someString")).andReturn("bar.someString");
    expect(mockDeserializer.deserialize(String.class, null, "bar.someString"))
        .andReturn("hello kitty");
    control.replay();
    assertEquals("hello kitty", stub.getProxy().someString());
    control.verify();
  }

  public void testWrongHint() {
    // Wrong type hint still succeeds.  Assuming this is by design.
    setUp();
    expect(mockSource.composePath("bar", "confusedTypes")).andReturn("bar.confusedTypes");
    expect(mockDeserializer.deserialize(String.class, Integer.class, "bar.confusedTypes"))
        .andReturn("hello kitty");
    control.replay();
    assertEquals("hello kitty", stub.getProxy().confusedTypes());
    control.verify();
  }
}
