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

import java.io.Reader;

/**
 * @author Zoran Rilak
 */
public class TestTree extends TestNode implements ConfigNode<TestNode> {
  
  public TestTree() {
    load(null);
  }

  public void load(Reader _unused) {
    put("serviceName", "DummyService");
    put("isActive", "true");
    put("timeout", "15");
    put("tempDir", "/tmp/blueprint");
    put("deployUrl", "http://www.codemined.org/blueprint");
    put("backupHours", null).setList("3", "8", "18");
    put("activeBackupDays", null).setList("true", "false", "false", "true", "false", "true", "true");

    TestNode http = put("http", null);
    http.put("host", "localhost");
    http.put("port", "65536");
    http.put("ssl", "true");

    TestNode proto = put("protocols", null);
    TestNode telnet = proto.put("telnet", "disabled");
    telnet.put("name", "Telnet");
    telnet.put("port", "25");
    TestNode ftp = proto.put("ftp", "enabled");
    ftp.put("name", "FTP");
    ftp.put("port", "21");
    TestNode dns = proto.put("dns", "enabled");
    dns.put("name", "DNS");
    dns.put("port", "53");

    put("state", "TRUE");
    put("typeHintDemo1", "1");
    put("typeHintDemo2", "2");

    TestNode db = put("db", null);
    db.put("impl", "java.util.Random");
    TestNode devel = db.put("development", null);
    devel.put("name", "devel");
    devel.put("isTemporary", "true");
    TestNode prod = db.put("production", null);
    prod.put("name", "Production");
    prod.put("isTemporary", "false");

    put("key1", "true");
    put("key2", "true");
  }

}

