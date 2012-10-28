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

import org.apache.commons.configuration.PropertiesConfiguration;
import org.codemined.blueprint.ConfigTree;

import javax.security.auth.login.Configuration;
import java.io.Reader;

/**
 * This implementation is far from optimal.
 * It instantiates plenty of objects as it traverses the tree to reach the required key.
 * On the upside, it guarantees that the lookups will always reflect the most recent state of
 * the underlying configuration object.  This is useful because Apache Configuration objects
 * can dynamically change by reloading at runtime.
 *
 * @author Zoran Rilak
 */
public class ApacheTree implements ConfigTree<ApacheNode> {

  private ApacheNode rootNode;


  public ApacheTree() {
  }

  @Override
  public void load(Reader reader) {
    /* not sure a Reader will do here. A higher abstraction is needed, esp. for non-stream sources
       (Java system properties, environment variables, databases, etc.)
     */
    Configuration apacheConfig = ...;
    rootNode = new ApacheNode(null, null, apacheConfig);
  }

  @Override
  public ApacheNode getRootNode() {
    return rootNode;
  }

}
