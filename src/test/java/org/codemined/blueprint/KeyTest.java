package org.codemined.blueprint;

import mockit.Expectations;
import mockit.Mocked;
import org.codemined.InMemoryTree;
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

  class TestTree extends InMemoryTree<String,String> {
    public TestTree(String key, String value) {
      super(key, value, null);
    }
  }

  @Mocked TestTree mockedTree;

  @Test
  public void keyAnnotationOverridesMapping()
          throws Throwable {
    new Expectations() {{
      mockedTree.get("keyOverride"); result = mockedTree;
      mockedTree.value(); result = "42";
    }};
    Deserializer deserializer = new Deserializer(mockedTree, Iface.class.getClassLoader());
    Stub<Iface> stub = new Stub<Iface>(Iface.class, deserializer);
    Method method = Iface.class.getMethod("keyOverridesMethod");
    Object value = stub.invoke(stub.getProxy(), method, null);
    assertEquals(value, 42);
  }

  @Test
  public void namedAnnotationOverridesMapping()
          throws Throwable {
    new Expectations() {{
      mockedTree.get("namedOverride"); result = mockedTree;
      mockedTree.value(); result = "42";
    }};
    Deserializer deserializer = new Deserializer(mockedTree, Iface.class.getClassLoader());
    Stub<Iface> stub = new Stub<Iface>(Iface.class, deserializer);
    Method method = Iface.class.getMethod("namedOverridesMethod");
    Object value = stub.invoke(stub.getProxy(), method, null);
    assertEquals(value, 42);
  }

  @Test
  public void keyAnnotationSupersedesNamedAnnotation()
          throws Throwable {
    new Expectations() {{
      mockedTree.get("keyOverride"); result = mockedTree;
      mockedTree.value(); result = "42";
    }};
    Deserializer deserializer = new Deserializer(mockedTree, Iface.class.getClassLoader());
    Stub<Iface> stub = new Stub<Iface>(Iface.class, deserializer);
    Method method = Iface.class.getMethod("bothOverrideMethod");
    Object value = stub.invoke(stub.getProxy(), method, null);
    assertEquals(value, 42);
  }
  
}
