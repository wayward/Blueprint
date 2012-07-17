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

package org.codemined.blueprint.tree;

import org.codemined.util.InMemoryTree;
import org.codemined.util.Tree;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Zoran Rilak
 */
@Test
public class InMemoryTreeTest {

  @Test
  public void buildsEmptyPaths() {
    Tree<String,String> t = new InMemoryTree<String,String>(null);
    t.putByPath(Arrays.asList("a", "b", "c"), "abc");
    assertNotNull(t.getByPath(Arrays.asList("a")));
    assertNotNull(t.getByPath(Arrays.asList("a", "b")));
    assertNotNull(t.getByPath(Arrays.asList("a", "b", "c")));
    assertEquals(t.getByPath(Arrays.asList("a")).value(), null);
    assertEquals(t.getByPath(Arrays.asList("a", "b")).value(), null);
    assertEquals(t.getByPath(Arrays.asList("a", "b", "c")).value(), "abc");
  }
  
}
