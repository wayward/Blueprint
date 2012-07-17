package org.codemined.blueprint;

import org.testng.Assert;
import org.testng.annotations.Test;

/** Shows how to use the {@code UseType} annotation. */
@Test
public class UseTypeTest {

  private interface Iface {
    @UseType(UseTypeTest.class) void aMethod();
  }
  
  public void someTest() {
    Assert.assertNotNull(new Iface() {
      @Override public void aMethod() {
        // no-op
      }});
  }

}
