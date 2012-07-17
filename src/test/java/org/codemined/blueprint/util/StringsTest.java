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

import org.codemined.util.Strings;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * @author Zoran Rilak
 */
@Test
public class StringsTest {
  
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void splitNullString() {
    Strings.split(null, "foo");
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void splitWithNullSeparator() {
    Strings.split("foo", null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void splitWithEmptySeparator() {
    Strings.split("foo", "");
  }

  @Test
  public void splitEmptyString() {
    assertEquals(Strings.split("", ".").size(), 1);
  }
  
  @Test
  public void splitSeparatorOnlyString() {
    assertEquals(Strings.split(".", ".").size(), 0);
    assertEquals(Strings.split("..", ".").size(), 0);
  }
  
  @Test
  public void split() {
    List<String> l = Strings.split("a.b..c.d", ".");
    assertEquals(l.size(), 5);
    assertEquals(l.get(2), "");
  }
  
  @Test
  public void joinEmptyStrings() {
    assertEquals(Strings.join("."), "");
    assertEquals(Strings.join(".", ""), "");
    assertEquals(Strings.join(".", "", null), "");
    assertEquals(Strings.join(".", "", null, "", null), "");
  }

  @Test
  public void joinWithEmptyStrings() {
    assertEquals(Strings.join(".", null, "", null, "a", "b", "", "c", "d"), "a.b..c.d");
  }

  
}
