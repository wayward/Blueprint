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

package org.codemined.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An immutable N-ary tree.
 * 
 * @author Zoran Rilak
 */
public interface Tree<K,V> extends Iterable<Tree<K,V>> {

  V getValue();

  Tree<K,V> get(K key);

  Tree<K,V> getByPath(List<K> path);

  boolean contains(K key);

  public void setValue(V value);

  /**
   * Sets the value in a sub-tree, possibly generating a new sub-tree if one doesn't exist.
   *
   * @return the updated or newly created sub-tree.
   */
  Tree<K,V> put(K key, V value);

  /**
   * Adds a sub-tree to this tree.
   *
   * @param subTree sub-tree to add.
   * @return the previous sub-tree associated with the given
   * sub-tree's key, or null if there was no such sub-tree.
   */
  Tree<K,V> putTree(K key, Tree<K, V> subTree);

  Tree<K,V> putByPath(List<K> path, V value);

  Tree<K,V> putByPath(List<K> path, V intermediaryValue, V value);

  /**
   * <p>Returns the number of sub-trees contained in this tree.</p>
   *
   * @return number of sub-trees.
   */
  int size();

  Set<Map.Entry<K, Tree<K, V>>> entrySet();

}
