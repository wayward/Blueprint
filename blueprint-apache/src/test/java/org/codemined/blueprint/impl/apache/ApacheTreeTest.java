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

package org.codemined.blueprint.impl.apache;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Zoran Rilak
 */
@Test
public class ApacheTreeTest {

  private static final String TEST_PROPERTIES_FILE = "test.properties";

  private TestProperties testProperties;

  @BeforeClass
  public void setUp()
          throws IOException {
    this.testProperties = new TestProperties(TEST_PROPERTIES_FILE);
  }

  @Test
  public ApacheTree loadTree() {
    try {
      PropertiesConfiguration pc = new PropertiesConfiguration(TEST_PROPERTIES_FILE);
      pc.setDelimiterParsingDisabled(true);
      pc.refresh();
      return new ApacheTree(pc);
    } catch (ConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void validRootNode() {
    ApacheTree t = loadTree();
    assertEquals(t.getKey(), null);
    assertEquals(t.getValue(), null);
    assertEquals(t.size(), 15);
  }

  @Test
  public void validFirstLevelNodes() {
    ApacheTree t = loadTree();
    assertEquals(t.size(), testProperties.firstLevelKeys().size());
    for (String k : testProperties.firstLevelKeys()) {
      assertTrue(t.contains(k), "key '" + k + "' exists in properties but not in the tree");
    }
  }

}
