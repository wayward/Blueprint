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

import org.codemined.blueprint.source.Source;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Zoran Rilak
 */
public class MockSource implements Source {

  private Node root;


  public MockSource() {
    this.root = new Node();
  }

  @Override
  public boolean isValid(Path path) {
    return true;
  }

  @Override
  public boolean containsPath(Path path) {
    return (getNodeAt(path) != null);
  }

  @Override
  public Set<String> getSubKeys(Path path) {
    Set<String> result = new HashSet<String>();
    result.addAll(getNodeAt(path).getChildren().keySet());
    return result;
  }

  @Override
  public String getStringValue(Path path) {
    return getNodeAt(path).getValue();
  }

  @Override
  public Integer getListSize(Path path) {
    return getNodeAt(path).getArraySize();
  }

  @Override
  public void reload() {
    // no-op
  }

  private Node getNodeAt(Path path) {
    Node node = root;
    Iterator<Path.Component> iter = path.iterator();
    while ((node != null) && iter.hasNext()) {
      Path.Component c = iter.next();
      if (c instanceof Path.NodeComponent) {
        node = node.getChild(((Path.NodeComponent) c).getName());
      } else if (c instanceof Path.IndexComponent) {
        node = node.getArrayItem(((Path.IndexComponent) c).getIndex());
      } else {
        return null;
      }
    }
    return node;
  }

  Node getRootNode() {
    return this.root;
  }

  static MockSource withTestDefaults() {
    MockSource result = new MockSource();
    result.root
            .withChild("serviceName", "DummyService")
            .withChild("isActive", "true")
            .withChild("timeout", "15")
            .withChild("tempDir", "/tmp/blueprint")
            .withChild("deployUrl", "http://www.codemined.org/blueprint")
            .withChild("backupHours", new Node()
                    .withArrayItems("3", "8", "18"))
            .withChild("activeBackupDays", new Node()
                    .withArrayItems("true", "false", "false", "true", "false", "true", "true"))
            .withChild("http", new Node()
                    .withChild("host", "localhost")
                    .withChild("port", "65536")
                    .withChild("ssl", "true"))
            .withChild("protocols", new Node()
                    .withChild("telnet", new Node("disabled")
                            .withChild("name", "Telnet")
                            .withChild("port", "25"))
                    .withChild("ftp", new Node("enabled")
                            .withChild("name", "FTP")
                            .withChild("port", "21"))
                    .withChild("dns", new Node("enabled")
                            .withChild("name", "DNS")
                            .withChild("port", "53")))
            .withChild("state", "TRUE")
            .withChild("typeHintDemo1", "1")
            .withChild("typeHintDemo2", "2")
            .withChild("db", new Node()
                    .withChild("impl", "java.util.Random")
                    .withChild("development", new Node()
                            .withChild("name", "devel")
                            .withChild("isTemporary", "true"))
                    .withChild("production", new Node()
                            .withChild("name", "Production")
                            .withChild("isTemporary", "false")))
            .withChild("key1", "true")
            .withChild("key2", "true");
    return result;
  }

}
