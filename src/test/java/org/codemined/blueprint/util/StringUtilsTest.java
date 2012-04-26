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

package org.codemined.blueprint.util;

import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * @author Zoran Rilak
 */
@Test
public class StringUtilsTest {
  
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void splitNullString() {
    StringUtils.split(null, "foo");
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void splitWithNullSeparator() {
    StringUtils.split("foo", null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void splitWithEmptySeparator() {
    StringUtils.split("foo", "");
  }

  @Test
  public void splitEmptyString() {
    assertEquals(StringUtils.split("", ".").size(), 1);
  }
  
  @Test
  public void splitSeparatorOnlyString() {
    assertEquals(StringUtils.split(".", ".").size(), 0);
    assertEquals(StringUtils.split("..", ".").size(), 0);
  }
  
  @Test
  public void split() {
    List<String> l =StringUtils.split("a.b..c.d", ".");
    assertEquals(l.size(), 5);
    assertEquals(l.get(2), "");
  }
  
  @Test
  public void joinTest() {
    
  }
  
}
