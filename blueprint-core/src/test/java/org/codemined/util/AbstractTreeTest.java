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

package org.codemined.util;

import org.testng.annotations.Test;

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
  public void getByPath_noSubTree() {
    InMemoryTree<Integer,String> t = new InMemoryTree<Integer,String>(1, "one");
    t.put(2, "two").put(3, "three");
    t.get(2).put(4, "four").put(5, "five");
    assertNull(t.getByPath(new Path<Integer>(100)));
    assertNull(t.getByPath(new Path<Integer>(3)));
    assertNull(t.getByPath(new Path<Integer>(2, 4, 5, 6)));
  }

  @Test
  public void getByPath() throws Exception {
    InMemoryTree<Integer,String> t = new InMemoryTree<Integer,String>(1, "one");
    t.put(2, "two").put(3, "three");
    t.get(2).put(4, "four").put(5, "five");
    assertEquals(t.getByPath(new Path<Integer>(2, 3)).getValue(), "three");
    assertEquals(t.getByPath(new Path<Integer>(2, 4, 5)).getValue(), "five");
  }

  @Test
  public void putByPath_createsSubTree() throws Exception {
    InMemoryTree<Integer,String> t = new InMemoryTree<Integer,String>(1, "one");
    t.putByPath(new Path<Integer>(2, 3), "three");
    assertEquals(t.getByPath(new Path<Integer>(2)).getValue(), null);
    assertEquals(t.getByPath(new Path<Integer>(2, 3)).getValue(), "three");
    t.putByPath(new Path<Integer>(2, 4, 5), "five", "-");
    assertEquals(t.getByPath(new Path<Integer>(2, 4)).getValue(), "-");
    assertEquals(t.getByPath(new Path<Integer>(2, 4, 5)).getValue(), "five");
  }

}
