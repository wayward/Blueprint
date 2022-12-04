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


/**
 * A node may contain:
 * - A string value (#getValue)
 * - Zero or more array nodes (#getArrayNodes)
 * - Zero or more children nodes (#getChildrenNodes)
 * @param <T>
 */
public interface ConfigNode<T extends ConfigNode<T>> {

  boolean hasValue();
  /**
   * Returns the string associated with this node.
   * If the node contains a list of values (#hasArrayNodes == true) but no string value, still returns null.
   * @return
   */
  String getValue();

  /**
   * Returns true if this node contains an array.
   * @return
   */
  boolean hasArrayNodes();

  /**
   * Returns a list of values stored under this key.
   * The list will have a length of 0 if no values are stored under this key.
   * @return
   */
  List<T> getArrayNodes();

  T getChildNode(String key);

  boolean containsKey(String key);

  Set<String> keySet();

}
