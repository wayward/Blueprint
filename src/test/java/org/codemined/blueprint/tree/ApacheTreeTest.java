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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.codemined.blueprint.impl.ApacheTree;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Zoran Rilak
 */
@Test
public class ApacheTreeTest {

  @Test
  public ApacheTree loadTree() {
    try {
      return new ApacheTree(new PropertiesConfiguration("src/test/resources/test.properties"));
    } catch (ConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void validRootNode() {
    ApacheTree t = loadTree();
    assertEquals(t.key(), null);
    assertEquals(t.value(), null);
    assertEquals(t.size(), 14);
  }

}
