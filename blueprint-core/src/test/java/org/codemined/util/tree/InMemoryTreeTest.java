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

package org.codemined.util.tree;

import org.codemined.util.InMemoryTree;
import org.codemined.util.Tree;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.*;

/**
 * @author Zoran Rilak
 */
@Test
public class InMemoryTreeTest {

  @Test
  public void gettersAndSetters() {
    Tree<Integer,String> t = new InMemoryTree<Integer,String>("one");
    assertEquals(t.getValue(), "one");
    t.setValue("two");
    assertEquals(t.getValue(), "two");
  }

  @Test
  public void buildsEmptyPaths() {
    Tree<String,String> t = new InMemoryTree<String,String>();
    t.putByPath(Arrays.asList("a", "b", "c"), "abc");
    assertNotNull(t.getByPath(Arrays.asList("a")));
    assertNotNull(t.getByPath(Arrays.asList("a", "b")));
    assertNotNull(t.getByPath(Arrays.asList("a", "b", "c")));
    assertEquals(t.getByPath(Arrays.asList("a")).getValue(), null);
    assertEquals(t.getByPath(Arrays.asList("a", "b")).getValue(), null);
    assertEquals(t.getByPath(Arrays.asList("a", "b", "c")).getValue(), "abc");
  }

  @Test
  public void getExisting() {
    Tree<Integer,String> t = new InMemoryTree<Integer,String>("one");
    t.put(2, "two");
    assertEquals(t.get(2).getValue(), "two");
  }

  @Test
  public void getNonExisting() {
    Tree<Integer,String> t = new InMemoryTree<Integer,String>("one");
    t.put(2, "two");
    assertNull(t.get(3));
  }

  @Test
  public void putExisting() {
    Tree<Integer,String> t = new InMemoryTree<Integer,String>("one");
    t.put(2, "TWO!");
    Tree<Integer,String> st1 = t.get(2);
    Tree<Integer,String> st2 = t.put(2, "two");
    assertTrue(t.contains(2));
    assertEquals(t.get(2).getValue(), "two");
    assertSame(st1, st2);
  }

  @Test
  public void putNew() {
    Tree<Integer,String> t = new InMemoryTree<Integer,String>("one");
    t.put(2, "two");
    assertTrue(t.contains(2));
    assertEquals(t.get(2).getValue(), "two");
  }

  @Test
  public void iterator() {
    Tree<Integer,String> t = new InMemoryTree<Integer,String>("one");
    t.put(2, "two");
    t.put(3, "three");
    t.put(4, "four");
    t.put(5, "five");

    Set<String> set = new HashSet<String>();
    set.add("two");
    set.add("three");
    set.add("four");
    set.add("five");

    for (Tree<Integer,String> subT : t) {
      assertTrue(set.remove(subT.getValue()));
    }
    assertEquals(set.size(), 0);
  }

  @Test
  public void correctSize() {
    Tree<Integer,String> t = new InMemoryTree<Integer,String>("one");
    assertEquals(t.size(), 0);
    t.put(2, "two");
    t.put(3, "three");
    t.put(4, "four");
    t.put(5, "five");
    assertEquals(t.size(), 4);
    t.put(2, "TWO!");
    assertEquals(t.size(), 4);
  }

}
