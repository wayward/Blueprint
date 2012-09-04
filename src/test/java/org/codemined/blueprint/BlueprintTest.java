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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.codemined.blueprint.impl.ApacheTree;
import org.testng.annotations.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import static org.testng.Assert.*;

/**
 * @author Zoran Rilak
 */
@Test
public class BlueprintTest {

  private static final String VALID_CONF = "src/test/resources/test.properties";

  private static final String INVALID_CONF = "src/test/resources/test-failing-validations.properties";

  @Test
  public TestConfiguration createTestBlueprint() {
    try {
      PropertiesConfiguration pc = new PropertiesConfiguration(VALID_CONF);
      pc.setDelimiterParsingDisabled(true);
      pc.refresh();
      return Blueprint.create(TestConfiguration.class, new ApacheTree(pc));

    } catch (ConfigurationException e) {
      throw new RuntimeException(e);
    } catch (ConfigurationValidationException e) {
      throw new RuntimeException(e);
    }
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void rejectsClasses()
          throws ConfigurationException, ConfigurationValidationException {
    Blueprint.create(Class.class, new ApacheTree(new PropertiesConfiguration(VALID_CONF)));
  }

  @Test
  public void validationFails() {
    try {
      PropertiesConfiguration pc = new PropertiesConfiguration(INVALID_CONF);
      pc.setDelimiterParsingDisabled(true);
      pc.refresh();
      Blueprint.create(TestConfiguration.class, new ApacheTree(pc));
      fail();

    } catch (ConfigurationException e) {
      throw new RuntimeException(e);

    } catch (ConfigurationValidationException e) {
      assertEquals(e.getFailedValidations().size(), 1);
    }
  }

  @Test
  public void methodsFromObjectBehaveNormally() {
    TestConfiguration cfg = createTestBlueprint();
    assertNotNull(cfg.toString());
    assertTrue(TestConfiguration.class.isInstance(cfg));
    assertEquals(cfg.hashCode(), cfg.hashCode());
    assertNotEquals(cfg.hashCode(), createTestBlueprint().hashCode());
    assertTrue(cfg.equals(cfg));
    assertFalse(cfg.equals(this));
  }

  @Test
  public void simpleDeserialization() {
    TestConfiguration cfg = createTestBlueprint();
    assertEquals(cfg.serviceName(), "BlueprintTestService");
    assertTrue(cfg.isActive());
    assertEquals(cfg.timeout(), 10);
    assertEquals(cfg.tempDir(), new File("/tmp/blueprint"));
  }

  @Test
  public void collectionDeserialization() {
    TestConfiguration cfg = createTestBlueprint();
    assertEquals(cfg.backupHours(Integer.class).size(), 3);
    Iterator<Integer> iter = cfg.backupHours(Integer.class).iterator();
    assertEquals(iter.next().intValue(), 3);
    assertEquals(iter.next().intValue(), 8);
    assertEquals(iter.next().intValue(), 18);

    assertEquals(cfg.activeBackupDays().getClass(), ArrayList.class);
    assertEquals(cfg.activeBackupDays().size(), 7);
    assertEquals(cfg.activeBackupDays().get(0), true);
    assertEquals(cfg.activeBackupDays().get(1), false);
  }

  @Test
  public void mapDeserialization() {
    TestConfiguration cfg = createTestBlueprint();
    assertEquals(cfg.http().size(), 3);
    assertEquals(cfg.http().get("host"), "localhost");
    assertEquals(cfg.http().get("port"), "65536");
    assertEquals(cfg.http().get("ssl"), "true");
    assertNotNull(cfg.protocols().get("dns"));
    assertEquals(cfg.protocols().get("dns").port(), 53);
  }

  @Test
  public void typeHinting()
          throws MalformedURLException {
    TestConfiguration cfg = createTestBlueprint();
    assertEquals(cfg.state(String.class), "TRUE");
    assertTrue(cfg.state(Boolean.class));
    assertEquals(cfg.state(TestConfiguration._State.class), TestConfiguration._State.TRUE);
    assertEquals(cfg.deployUrl().getClass(), URL.class);
    assertEquals(cfg.deployUrl(), new URL("http://www.codemined.org/Blueprint"));
    cfg.deployUrl().getHost();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void typeHintPrecedence()
          throws MalformedURLException {
    TestConfiguration cfg = createTestBlueprint();
    assertEquals(cfg.typeHintDemo1().getClass(), A.class);
    assertEquals(cfg.typeHintDemo1().toString(), "1:A");
    assertEquals(cfg.typeHintDemo1(A1.class).getClass(), A1.class);
    assertEquals(cfg.typeHintDemo1(A1.class).toString(), "1:A1:A");

    assertEquals(cfg.typeHintDemo2().getClass(), A1.class);
    assertEquals(cfg.typeHintDemo2().toString(), "2:A1:A");
    assertEquals(cfg.typeHintDemo2(A2.class).getClass(), A2.class);
    assertEquals(cfg.typeHintDemo2(A2.class).toString(), "2:A2:A1:A");
  }

  @Test
  public void interfaceDeserialization() {
    TestConfiguration cfg = createTestBlueprint();
    assertTrue(TestConfiguration._DB.class.isInstance(cfg.db()));
    assertTrue(cfg.db().development().isTemporary());
    assertFalse(cfg.db().production().isTemporary());
  }

  @Test
  public void classDeserialization() {
    TestConfiguration cfg = createTestBlueprint();
    assertSame(cfg.db().impl(), java.util.Random.class);
    assertSame(cfg.db().impl(Class.class), java.util.Random.class);
  }

  @Test
  public void overridingKeys() {
    TestConfiguration cfg = createTestBlueprint();
    assertTrue(cfg.key1());
    assertTrue(cfg.keyTwo());
  }

}
