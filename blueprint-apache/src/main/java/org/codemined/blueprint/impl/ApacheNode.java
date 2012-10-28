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

package org.codemined.blueprint.impl;

import org.apache.commons.configuration.Configuration;
import org.codemined.blueprint.ConfigNode;
import org.codemined.util.Strings;

import java.util.*;

/**
 * This implementation is far from optimal.
 * It instantiates plenty of objects as it traverses the tree to reach the required key.
 * On the upside, it guarantees that the lookups will always reflect the most recent state of
 * the underlying configuration object.  This is useful because Apache Configuration objects
 * can dynamically change by reloading at runtime.
 *
 * @author Zoran Rilak
 */
public class ApacheNode implements ConfigNode<ApacheNode> {

  protected Configuration config;

  /** Apache Configuration key corresponding to this tree.  Root tree contains an empty string (""). */
  protected String configKey;


  /**
   * Creates a node element.
   *
   * @param parentConfigKey
   * @param key
   * @param config
   */
  protected ApacheNode(String parentConfigKey, String key, Configuration config) {
    this.config = config;

    if (parentConfigKey == null) {
      this.configKey = "";
    } else {
      this.configKey = Strings.join(".", parentConfigKey, key);
    }
  }

  /**
   * Creates an array element.
   *
   * @param parentConfigKey
   * @param index
   * @param config
   */
  protected ApacheNode(String parentConfigKey, int index, Configuration config) {
    this.config = config;

    if (parentConfigKey == null) {
      this.configKey = "";
    } else {
      this.configKey = Strings.join(".", parentConfigKey) + '[' + index + ']';
    }
  }


  @Override
  public String getValue() {
    // values aren't cached to allow the changes in the
    // underlying Apache Configuration to show through.
    return config.getString(configKey);
  }

  @Override
  public List<ApacheNode> getList() {
    // same as above, we're just going to enumerate
    // the elements of the list returned by Apache Configuration.
    List<ApacheNode> list = new ArrayList<ApacheNode>();
    for (int i = 0; i < config.getList(configKey).size(); i++) {
      list.add(new ApacheNode(configKey, i, config));
    }
    return list;
  }

  @Override
  public boolean containsNode(String key) {
    String prefix = Strings.join(".", configKey, key);
    Iterator<String> iter = config.getKeys(prefix);
    return iter.hasNext();
  }

  @Override
  public ApacheNode getNode(String key) {
    return new ApacheNode(configKey, key, config);
  }

  @Override
  public Set<String> keySet() {
    Set<String> keySet = new HashSet<String>();
    Iterator<String> iter = config.getKeys();

    while (iter.hasNext()) {
      String k = iter.next();
      if (k.startsWith(configKey) && k.length() > configKey.length()) {

        // cut off the leading path if any
        if (! configKey.isEmpty()) {
          k = k.substring(configKey.length() + 1);
        }

        // cut off any trailing components
        int i = k.indexOf('.');
        if (i > 0) {
          k = k.substring(0, i);
        }

        keySet.add(k);
      }
    }
    return keySet;
  }

}
