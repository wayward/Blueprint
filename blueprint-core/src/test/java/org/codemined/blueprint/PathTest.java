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

package org.codemined.blueprint;

import org.testng.annotations.Test;

import java.util.LinkedList;

import static org.testng.Assert.assertEquals;

/**
 * @author Zoran Rilak
 * @version 0.1
 * @since 0.1
 */
@Test
public class PathTest {


  @Test
  public void createsPath() {
    assertEquals(new Path().size(), 0);
    assertEquals(new Path(new LinkedList<Path.Component>(), new Path.NodeComponent("")).size(), 1);
    assertEquals(new Path()._(""), 1);
  }

}
