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
import org.codemined.blueprint.impl.ApacheNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

/**
 * @author Zoran Rilak
 */
@Test
public class ApacheNodeTest {

  private static final String TEST_FILE = "test.properties";

  private TestProperties testProperties;


  @BeforeClass
  public void setUp()
          throws IOException {
    this.testProperties = new TestProperties(TEST_FILE);
  }

  @Test
  public ApacheNode loadTree() {
    try {
      return new ApacheNode(new PropertiesConfiguration(TEST_FILE));
    } catch (ConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void validRootNode() {
    ApacheNode t = loadTree();
    assertNull(t.getValue());
    assertEquals(t.keySet().size(), 15);
  }

  @Test
  public void validFirstLevelNodes() {
    ApacheNode t = loadTree();
    assertEquals(t.keySet().size(), testProperties.firstLevelKeys().size());
    for (String k : testProperties.firstLevelKeys()) {
      assertTrue(t.containsKey(k), "key '" + k + "' exists in properties but not in the tree");
    }
  }

  @Test
  public void parsesArrays() {
    ApacheNode t = loadTree();
    assertEquals(t.getChildNode("backupHours").getArrayNodes().size(), 3);
    assertEquals(t.getChildNode("activeBackupDays").getArrayNodes().size(), 7);
  }

}
