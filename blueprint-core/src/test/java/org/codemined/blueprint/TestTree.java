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

import org.codemined.util.InMemoryTree;
import org.codemined.util.Tree;

/**
 * @author Zoran Rilak
 */
class TestTree extends InMemoryTree<String, String> {

  public TestTree() {
    put("serviceName", "DummyService");
    put("isActive", "true");
    put("timeout", "15");
    put("tempDir", "/tmp/blueprint");
    put("deployUrl", "http://www.codemined.org/blueprint");
    put("backupHours", "3,8,18");
    put("activeBackupDays", "true, false, false, true, false, true, true");

    Tree<String, String> http = put("http", null);
    http.put("host", "localhost");
    http.put("port", "65536");
    http.put("ssl", "true");

    Tree<String, String> proto = put("protocols", null);
    Tree<String, String> telnet = proto.put("telnet", "disabled");
    telnet.put("name", "Telnet");
    telnet.put("port", "25");
    Tree<String, String> ftp = proto.put("ftp", "enabled");
    ftp.put("name", "FTP");
    ftp.put("port", "21");
    Tree<String, String> dns = proto.put("dns", "enabled");
    dns.put("name", "DNS");
    dns.put("port", "53");

    put("state", "TRUE");
    put("typeHintDemo1", "1");
    put("typeHintDemo2", "2");

    Tree<String, String> db = put("db", null);
    db.put("impl", "java.util.Random");
    Tree<String, String> devel = db.put("development", null);
    devel.put("name", "devel");
    devel.put("isTemporary", "true");
    Tree<String, String> prod = db.put("production", null);
    prod.put("name", "Production");
    prod.put("isTemporary", "false");

    put("key1", "true");
    put("key2", "true");
  }

}
