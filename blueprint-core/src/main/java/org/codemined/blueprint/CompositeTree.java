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

import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Zoran Rilak
 * @version 0.1
 * @since 0.1
 */
public class CompositeTree implements ConfigTree {

  private List<ConfigNode<?>> nodes;


  public CompositeTree() {
    this.nodes = new CopyOnWriteArrayList<ConfigNode<?>>();
  }

  public void add(ConfigNode node) {
    this.nodes.add(node);
  }

  public CompositeTree with(ConfigNode node) {
    add(node);
    return this;
  }

  @Override
  public void load(InputStream in) {
    //FIXME This does not belong here.
  }

  @Override
  public String getValue() {
    String value = null;
    for (ConfigNode t : nodes) {
      value = t.getValue();
      if (value != null) {
        break;
      }
    }
    return value;
  }

  @Override
  public List<? extends ConfigNode> getList() {
    List<? extends ConfigNode<?>> list = Collections.emptyList();
    for (ConfigNode<?> t : nodes) {
      list = t.getList();
      if (! list.isEmpty()) {
        break;
      }
    }
    return list;
  }

  @Override
  public boolean containsNode(String key) {
    for (ConfigNode<?> t : nodes) {
      if (t.containsNode(key)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public ConfigNode<?> getNode(String key) {
    ConfigNode<?> node = null;
    for (ConfigNode<?> t : nodes) {
      node = t.getNode(key);
      if (node != null) {
        break;
      }
    }
    return node;
  }

  @Override
  public Set<String> keySet() {
    Set<String> keySet = new HashSet<String>();
    for (ConfigNode<?> t : nodes) {
      keySet.addAll(t.keySet());
    }
    return keySet;
  }

}
