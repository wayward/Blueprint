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

import java.util.LinkedList;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * @author Zoran Rilak
 * @version 0.1
 * @since 0.1
 */
@Test
public class PathTest {


  @Test
  public void createsEmptyPath() {
    assertEquals(new Path<Object>().size(), 0);
    assertEquals(new Path<Object>(new LinkedList<Object>()).size(), 0);
    assertEquals(new Path<Object>(new Object[] {}).size(), 0);
  }

  @Test
  public void createsFromList() {
    List<String> l = new LinkedList<String>();
    l.add("a");
    l.add("b");
    l.add("c");
    Path<String> p = new Path<String>(l);
    assertEquals(p.toString(), "a/b/c");
  }

  @Test
  public void createsFromArray() {
    Path<String> p = new Path<String>("a", "b", "c");
    assertEquals(p.toString(), "a/b/c");
  }

  @Test
  public void testTo() throws Exception {
    Path<Integer> p = new Path<Integer>();
    assertEquals(p.to(1).toString(), "1");
    assertEquals(p.to(1).to(2).to(3).size(), 3);
    assertEquals(p.to(1).to(2).to(3).toString(), "1/2/3");
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testVia() throws Exception {
    Path<Integer> p = new Path<Integer>();
    assertEquals(p.via().size(), 0);
    assertEquals(p.via(1, 2, 3).size(), 3);
    assertEquals(p.via(1, 2, 3).toString(), "1/2/3");
  }

}
