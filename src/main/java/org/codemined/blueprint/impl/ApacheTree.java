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
import org.codemined.util.InMemoryTree;
import org.codemined.util.Strings;

import java.util.Iterator;
import java.util.List;

/**
 * @author Zoran Rilak
 */
public class ApacheTree extends InMemoryTree<String,String> {

  private final CompositeConfiguration config;

  /** Apache Configuration key corresponding to this tree.  Root tree contains an empty string (""). */
  private final String configKey;


  public ApacheTree(Configuration... cfgs) {
    this(null, null, new CompositeConfiguration());

    this.config.setDelimiterParsingDisabled(true);
    if (cfgs != null) {
      for (Configuration c : cfgs) {
        this.config.addConfiguration(c);
      }
    }
    loadSubTrees();
  }


  protected ApacheTree(ApacheTree parent, String key, CompositeConfiguration config) {
    super(key, null, parent);
    this.config = config;
    if (parent == null) {
      this.configKey = "";
    } else {
      this.configKey = Strings.join(".", parent.configKey, key);
    }
  }


  @Override
  public String value() {
    // values aren't cached to allow the changes in the underlying Apache Configuration to show through.
    return config.getString(configKey);
  }


  @Override
  public ApacheTree put(String subKey, String value) {
    throw new UnsupportedOperationException("Modifying Apache Configuration trees is not supported");
  }


  @Override
  public ApacheTree putByPath(List<String> path, String value) {
    throw new UnsupportedOperationException("Modifying Apache Configuration trees is not supported");
  }


  private void loadSubTrees() {
    final Iterator<String> iter;
    if (configKey.isEmpty()) {
      iter = config.getKeys();
    } else {
      iter = config.getKeys(configKey);
    }

    /* iterate over all unique sub-keys under this path */
    while (iter.hasNext()) {
      String subKey = iter.next();

      if (! configKey.equals(subKey)) {
        /* trim leading root path and the `.' after it */
        if (! configKey.isEmpty()) {
          subKey = subKey.substring(configKey.length() + 1);
        }

        /* trim any trailing components after the first one */
        int dotPos = subKey.indexOf('.');
        if (dotPos >= 0) {
          subKey = subKey.substring(0, dotPos);
        }

        if (! contains(subKey)) {
          ApacheTree subTree = new ApacheTree(this, subKey, config);
          subTree.loadSubTrees();
          add(subTree);
        }
      }
    }
  }

}
