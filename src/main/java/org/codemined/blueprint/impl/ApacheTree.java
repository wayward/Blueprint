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
import org.codemined.InMemoryTree;
import org.codemined.blueprint.util.StringUtils;

import java.util.Iterator;
import java.util.List;

/**
 * @author Zoran Rilak
 */
public class ApacheTree extends InMemoryTree<String,String> {

  private final CompositeConfiguration config;

  /** Apache Configuration key corresponding to this tree.  For the root tree, contains empty string (""). */
  private final String configKey;


  public ApacheTree(Configuration... cfgs) {
    this(null, null, null, new CompositeConfiguration());
    if (cfgs != null) {
      for (Configuration c : cfgs) {
        this.config.addConfiguration(c);
      }
    }
    loadSubTrees();
  }


  protected ApacheTree(ApacheTree parent, String key, String value, CompositeConfiguration config) {
    super(key, value, parent);
    this.config = config;
    if (parent == null) {
      this.configKey = "";
    } else {
      this.configKey = StringUtils.join(".", parent.configKey, key);
    }
  }


  @Override
  public ApacheTree put(String subKey, String value) {
    throw new UnsupportedOperationException("Modifying Apache Configuration trees is not supported");
  }


  @Override
  public ApacheTree put(List<String> path, String value) {
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
        subKey = subKey.substring(configKey.length() + 1);
        /* trim any trailing components after the first one */
        int dotPosition = subKey.indexOf('.');
        if (dotPosition >= 0) {
          subKey = subKey.substring(0, dotPosition);
        }

        if (! contains(subKey)) {
          String value = config.getString(StringUtils.join(".", configKey, subKey));
          ApacheTree subTree = new ApacheTree(this, subKey, value, config);
          subTree.loadSubTrees();
          add(subTree);
        }
      }
    }
  }

}
