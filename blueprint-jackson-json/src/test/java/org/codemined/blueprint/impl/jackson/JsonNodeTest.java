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

import org.codemined.blueprint.impl.JsonNode;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * @author Zoran Rilak
 */
@Test
public class JsonNodeTest {

  private static final String TEST_FILE = "test.json";


  @Test
  public static JsonNode loadTree() {
    try {
      return new JsonNode(TEST_FILE);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void readsFirstLevelValues()
          throws IOException {
    JsonNode t = loadTree();
    assertEquals(t.getChildNode("name").getValue(), "Donald Duck");
    assertEquals(t.getChildNode("age").getValue(), "71");
    assertEquals(t.getChildNode("family").getValue(), null);
  }

  @Test
  public void readsNestedValues() {
    JsonNode t = loadTree();
    assertEquals(t.getChildNode("family").getChildNode("mother").getValue(), "Hortense McDuck");
    assertEquals(t.getChildNode("family").getChildNode("nephews").getArrayNodes().get(0).getValue(), "Huey");
    assertEquals(t.getChildNode("family").getChildNode("nephews").getArrayNodes().get(1).getValue(), "Dewey");
    assertEquals(t.getChildNode("family").getChildNode("nephews").getArrayNodes().get(2).getValue(), "Louie");
    assertEquals(t.getChildNode("family").getChildNode("married").getValue(), "false");
  }

}
