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

import org.codemined.blueprint.impl.BeanKeyResolver;
import org.testng.annotations.BeforeClass;
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

  private static final ConfigTree testTree = createTestTree();


  @Test
  @BeforeClass
  private TestConfiguration createTestBlueprint() {
    return Blueprint.create(TestConfiguration.class, testTree);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void rejectsClasses()
          throws ConfigurationValidationException {
    Blueprint.create(Class.class, testTree);
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
    assertEquals(cfg.serviceName(), "DummyService");
    assertTrue(cfg.isActive());
    assertEquals(cfg.timeout(), 15);
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

    assertEquals(cfg.activeBackupDays(boolean.class).getClass(), ArrayList.class);
    assertEquals(cfg.activeBackupDays(boolean.class).size(), 7);
    assertTrue(cfg.activeBackupDays(boolean.class).get(0));
    assertFalse(cfg.activeBackupDays(boolean.class).get(1));
  }

  @Test
  public void arrayDeserialization() {
    TestConfiguration cfg = createTestBlueprint();

    assertEquals(cfg.activeBackupDays().getClass(), boolean[].class);
    assertEquals(cfg.activeBackupDays().length, 7);
    assertTrue(cfg.activeBackupDays()[0]);
    assertFalse(cfg.activeBackupDays()[1]);
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
    assertEquals(cfg.deployUrl(), new URL("http://www.codemined.org/blueprint"));
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

  @Test
  public void keyResolution() {
    TestBeanConfiguration cfg = Blueprint.create(TestBeanConfiguration.class, testTree,
            new BeanKeyResolver());
    assertEquals(cfg.getServiceName(), "DummyService");
    assertTrue(cfg.isActive());
    assertEquals(cfg.getBackupHours().size(), 3);
    assertEquals(cfg.http().get("host"), "localhost");
    assertEquals(cfg.db().getImpl(), java.util.Random.class);
    assertEquals(cfg.protocols().get("telnet").getName(), "Telnet");
  }

  /* Privates ------------------------------------------------------- */


  private static ConfigTree<?> createTestTree() {
    TestConfigTree t = new TestConfigTree();
    t.put("serviceName", "DummyService");
    t.put("isActive", "true");
    t.put("timeout", "15");
    t.put("tempDir", "/tmp/blueprint");
    t.put("deployUrl", "http://www.codemined.org/blueprint");
    t.put("backupHours", null).setList("3", "8", "18");
    t.put("activeBackupDays", null).setList("true", "false", "false", "true", "false", "true", "true");

    TestConfigTree http = t.put("http", null);
    http.put("host", "localhost");
    http.put("port", "65536");
    http.put("ssl", "true");

    TestConfigTree proto = t.put("protocols", null);
    TestConfigTree telnet = proto.put("telnet", "disabled");
    telnet.put("name", "Telnet");
    telnet.put("port", "25");
    TestConfigTree ftp = proto.put("ftp", "enabled");
    ftp.put("name", "FTP");
    ftp.put("port", "21");
    TestConfigTree dns = proto.put("dns", "enabled");
    dns.put("name", "DNS");
    dns.put("port", "53");

    t.put("state", "TRUE");
    t.put("typeHintDemo1", "1");
    t.put("typeHintDemo2", "2");

    TestConfigTree db = t.put("db", null);
    db.put("impl", "java.util.Random");
    TestConfigTree devel = db.put("development", null);
    devel.put("name", "devel");
    devel.put("isTemporary", "true");
    TestConfigTree prod = db.put("production", null);
    prod.put("name", "Production");
    prod.put("isTemporary", "false");

    t.put("key1", "true");
    t.put("key2", "true");
    return t;
  }
}
