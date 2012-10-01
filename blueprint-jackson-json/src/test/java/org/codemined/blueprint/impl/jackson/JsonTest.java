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

import org.codemined.blueprint.Blueprint;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Zoran Rilak
 */
@Test
public class JsonTest {

  @Test
  public void readsArrays() {
    JsonTestConfiguration cfg = Blueprint.create(JsonTestConfiguration.class, JsonTreeTest.loadTree());
    String[] n = cfg.family().nephews();
    assertEquals(n.length, 3);
    assertEquals(n[0], "Huey");
    assertEquals(n[1], "Dewey");
    assertEquals(n[2], "Louie");
  }



}
