package org.codemined.blueprint.impl.jackson;/*
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

import org.codemined.blueprint.impl.JsonTree;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * @author Zoran Rilak
 */
@Test
public class JsonTreeTest {

  private static final String TEST_FILE = "test.json";


  @Test
  public static JsonTree loadTree() {
    try {
      return new JsonTree(TEST_FILE);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void readsFirstLevelValues()
          throws IOException {
    JsonTree t = loadTree();
    assertEquals(t.getTree("name").getValue(), "Donald Duck");
    assertEquals(t.getTree("age").getValue(), "71");
    assertEquals(t.getTree("family").getValue(), null);
  }

  @Test
  public void readsNestedValues() {
    JsonTree t = loadTree();
    assertEquals(t.getTree("family").getTree("mother").getValue(), "Hortense McDuck");
    assertEquals(t.getTree("family").getTree("nephews").getList().get(0).getValue(), "Huey");
    assertEquals(t.getTree("family").getTree("nephews").getList().get(1).getValue(), "Dewey");
    assertEquals(t.getTree("family").getTree("nephews").getList().get(2).getValue(), "Louie");
    assertEquals(t.getTree("family").getTree("married").getValue(), "false");
  }

}
