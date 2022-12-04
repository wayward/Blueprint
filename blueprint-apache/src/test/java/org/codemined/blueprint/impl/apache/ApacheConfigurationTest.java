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
import org.codemined.blueprint.*;
import org.codemined.blueprint.impl.ApacheNode;
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
public class ApacheConfigurationTest {

  private static final String VALID_CFG = "test.properties";


  @Test
  public TestInterface createBlueprintFromApacheTree() {
    try {
      PropertiesConfiguration pc = new PropertiesConfiguration(VALID_CFG);
      return Blueprint.create(TestInterface.class, new ApacheNode(pc));

    } catch (ConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void simpleDeserialization() {
    TestInterface cfg = createBlueprintFromApacheTree();
    assertEquals(cfg.serviceName(), "TestService");
    assertTrue(cfg.isActive());
    assertEquals(cfg.timeout(), 15);
    assertEquals(cfg.tempDir(), new File("/tmp/blueprint"));
  }

  @Test
  public void collectionDeserialization() {
    TestInterface cfg = createBlueprintFromApacheTree();

    assertEquals(cfg.backupHours(Integer.class).size(), 3);
    Iterator<Integer> i = cfg.backupHours(Integer.class).iterator();
    assertEquals(i.next().intValue(), 3);
    assertEquals(i.next().intValue(), 8);
    assertEquals(i.next().intValue(), 18);

    assertEquals(cfg.activeBackupDays(Boolean.class).getClass(), ArrayList.class);  // deserialization into a concrete class
    assertEquals(cfg.activeBackupDays().length, 7);           // deserialization into an array
    assertEquals(cfg.activeBackupDays()[0], true);
    assertEquals(cfg.activeBackupDays()[1], false);
  }

  @Test
  public void mapDeserialization() {
    TestInterface cfg = createBlueprintFromApacheTree();
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
    TestInterface cfg = createBlueprintFromApacheTree();
    assertEquals(cfg.state(String.class), "TRUE");
    assertTrue(cfg.state(Boolean.class));
    assertEquals(cfg.state(TestInterface._State.class), TestInterface._State.TRUE);
    assertEquals(cfg.deployUrl().getClass(), URL.class);
    assertEquals(cfg.deployUrl(), new URL("http://www.codemined.org/blueprint/"));
    cfg.deployUrl().getHost();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void typeHintPrecedence()
          throws MalformedURLException {
    TestInterface cfg = createBlueprintFromApacheTree();
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
    TestInterface cfg = createBlueprintFromApacheTree();
    assertTrue(TestInterface._DB.class.isInstance(cfg.db()));
    assertTrue(cfg.db().development().isTemporary());
    assertFalse(cfg.db().production().isTemporary());
  }

  @Test
  public void classDeserialization() {
    TestInterface cfg = createBlueprintFromApacheTree();
    assertSame(cfg.db().impl(), java.util.Random.class);
    assertSame(cfg.db().impl(Class.class), java.util.Random.class);
  }

  @Test
  public void overridingKeys() {
    TestInterface cfg = createBlueprintFromApacheTree();
    assertTrue(cfg.key1());
    assertTrue(cfg.keyTwo());
  }

}
