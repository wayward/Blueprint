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

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.codemined.blueprint.ConfigTree;
import org.codemined.util.Strings;

import java.util.*;

/**
 * @author Zoran Rilak
 */
public class ApacheTree extends ConfigTree<ApacheTree> {

  private final CompositeConfiguration config;

  /** Apache Configuration key corresponding to this tree.  Root tree contains an empty string (""). */
  private final String configKey;

  private List<ApacheTree> list;

  private final HashMap<String, ApacheTree> subTrees;

  public ApacheTree(Configuration... cfgs) {
    this(null, null, new CompositeConfiguration());

    // we'll do our own parsing
    this.config.setDelimiterParsingDisabled(true);
    if (cfgs != null) {
      for (Configuration c : cfgs) {
        this.config.addConfiguration(c);
      }
    }
    loadSubTrees();
  }


  protected ApacheTree(ApacheTree parent, String key, CompositeConfiguration config) {
    this.config = config;
    this.subTrees = new HashMap<String, ApacheTree>();

    if (parent == null) {
      this.configKey = "";
    } else {
      this.configKey = Strings.join(".", parent.configKey, key);
    }
  }


  @Override
  public String getValue() {
    // values aren't cached to allow the changes in the underlying Apache Configuration to show through.
    return config.getString(configKey);
  }

  @Override
  public List<ApacheTree> getList() {
    if (this.list == null) {
      this.list = new ArrayList<ApacheTree>();

      // split at commas, ignoring whitespace (multiple commas yield empty elements)
      //TODO provide for custom list splitters (although this is going to be OK 99% of the time)
      String[] elements = getValue().split("\\s*,\\s*");
      for (int i = 0; i < elements.length; i++) {
        list.add(new ApacheTree(this, "[" + i + "]", config));
      }
    }

    return this.list;
  }

  @Override
  public boolean containsTree(String key) {
    return subTrees.containsKey(key);
  }

  @Override
  public ApacheTree getTree(String key) {
    return subTrees.get(key);
  }

  @Override
  public Set<String> keySet() {
    return subTrees.keySet();
  }


  /** Privates ------------------------------------------------------ */

  private void loadSubTrees() {
    final Iterator<String> iter;
    if (configKey.isEmpty()) {
      iter = config.getKeys();
    } else {
      iter = config.getKeys(configKey);
    }

    /* iterate over all unique sub-keys under this path */
    while (iter.hasNext()) {
      String key = iter.next();

      if (! configKey.equals(key)) {
        /* trim leading root path and the `.' after it */
        if (! configKey.isEmpty()) {
          key = key.substring(configKey.length() + 1);
        }

        /* trim any trailing components after the first one */
        int dotPos = key.indexOf('.');
        if (dotPos >= 0) {
          key = key.substring(0, dotPos);
        }

        if (! containsTree(key)) {
          ApacheTree tree = new ApacheTree(this, key, config);
          tree.loadSubTrees();
          this.subTrees.put(key, tree);
        }
      }
    }
  }

}
