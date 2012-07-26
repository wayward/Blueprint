package org.codemined.util;

import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * @author Zoran Rilak
 * @version 0.1
 * @since 0.1
 */
@Test
public class AbstractTreeTest {

  @Test
  public void testPath() throws Exception {
    InMemoryTree<Integer,String> t = new InMemoryTree<Integer,String>(null, 1, "one");
    t.put(2, "two").put(3, "three");
    assertEquals(t.get(2).get(3).getPath(), Arrays.asList(1, 2, 3));
  }

  @Test
  public void getByPath_noSubTree() {
    InMemoryTree<Integer,String> t = new InMemoryTree<Integer,String>(null, 1, "one");
    t.put(2, "two").put(3, "three");
    t.get(2).put(4, "four").put(5, "five");
    assertNull(t.getByPath(new Path<Integer>(100)));
    assertNull(t.getByPath(new Path<Integer>(3)));
    assertNull(t.getByPath(new Path<Integer>(2, 4, 5, 6)));
  }

  @Test
  public void getByPath() throws Exception {
    InMemoryTree<Integer,String> t = new InMemoryTree<Integer,String>(null, 1, "one");
    t.put(2, "two").put(3, "three");
    t.get(2).put(4, "four").put(5, "five");
    assertEquals(t.getByPath(new Path<Integer>(2, 3)).getValue(), "three");
    assertEquals(t.getByPath(new Path<Integer>(2, 4, 5)).getValue(), "five");
  }

  @Test
  public void putByPath_createsSubTree() throws Exception {
    InMemoryTree<Integer,String> t = new InMemoryTree<Integer,String>(null, 1, "one");
    t.putByPath(new Path<Integer>(2, 3), "three");
    assertEquals(t.getByPath(new Path<Integer>(2)).getValue(), null);
    assertEquals(t.getByPath(new Path<Integer>(2, 3)).getValue(), "three");
    t.putByPath(new Path<Integer>(2, 4, 5), "five", "-");
    assertEquals(t.getByPath(new Path<Integer>(2, 4)).getValue(), "-");
    assertEquals(t.getByPath(new Path<Integer>(2, 4, 5)).getValue(), "five");
  }

}
