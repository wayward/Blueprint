package org.codemined.blueprint;

import mockit.Expectations;
import mockit.Mocked;
import org.codemined.util.AbstractTree;
import org.testng.annotations.Test;

import javax.inject.Named;
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
    @Named("namedOverride") int namedOverridesMethod();
    @Key("keyOverride") @Named("namedOverride") int bothOverrideMethod();
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
    Stub<Iface> stub = new Stub<Iface>(Iface.class, mockTree, deserializer);
    Method method = Iface.class.getMethod("keyOverridesMethod");
    Object value = stub.invoke(stub.getProxy(), method, null);
    assertEquals(value, 42);
  }

  @Test
  public void namedAnnotationOverridesMapping()
          throws Throwable {
    new Expectations() {{
      mockTree.get("namedOverride"); result = mockTree;
      mockTree.getValue(); result = "42";
    }};
    Deserializer deserializer = new Deserializer(Iface.class.getClassLoader());
    Stub<Iface> stub = new Stub<Iface>(Iface.class, mockTree, deserializer);
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
    Stub<Iface> stub = new Stub<Iface>(Iface.class, mockTree, deserializer);
    Method method = Iface.class.getMethod("bothOverrideMethod");
    Object value = stub.invoke(stub.getProxy(), method, null);
    assertEquals(value, 42);
  }
  
}
