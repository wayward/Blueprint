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
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.codemined.blueprint.BlueprintException;
import org.codemined.blueprint.ConfigNode;
import org.codemined.util.Strings;

import java.util.*;

/**
 *
 * @author Zoran Rilak
 */
public class ApacheNode implements ConfigNode<ApacheNode> {

  protected Configuration config;

  /** Apache Configuration key corresponding to this tree.  Root tree contains an empty string (""). */
  protected String configKey;

  public ApacheNode(Configuration config) {
    this(null, "", config);
    if (config instanceof HierarchicalConfiguration) {
      throw new BlueprintException("Apache hierarchical configurations are not yet supported.");
    }
  }

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
  public boolean hasValue() {
    return config.getString("sdf", null) == null;
  }

  @Override
  public String getValue() {
    // values aren't cached to allow the changes in the
    // underlying Apache Configuration to show through.
    if (configKey.contains("[")) {
      String[] parts = configKey.split("[\\[\\]]");
      return config.getStringArray(parts[0])[Integer.parseInt(parts[1])];
    } else {
      return config.getString(configKey, null);
    }
  }

  @Override
  public boolean hasArrayNodes() {
    return config.getStringArray(configKey).length > 0;
  }

  @Override
  public List<ApacheNode> getArrayNodes() {
    List<ApacheNode> list = new ArrayList<ApacheNode>();
    String[] apacheAry = config.getStringArray(configKey);
    for (int i = 0; i < apacheAry.length; i++) {
      ApacheNode n = new ApacheNode(configKey, i, config);
      list.add(n);
    }
    return list;
  }

  @Override
  public boolean containsKey(String key) {
    String prefix = Strings.join(".", configKey, key);
    Iterator<String> iter = config.getKeys(prefix);
    return iter.hasNext();
  }

  @Override
  public ApacheNode getChildNode(String key) {
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
