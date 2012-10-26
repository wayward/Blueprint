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

import java.util.List;
import java.util.Set;


public abstract class ConfigTree<T extends ConfigTree<T>> {


  public abstract String getValue();

  /**
   * Returns an empty list if this tree has no array content.
   * @return
   */
  public abstract List<T> getList();

  public abstract boolean containsTree(String key);

  public abstract T getTree(String key);

  public abstract Set<String> keySet();

}
