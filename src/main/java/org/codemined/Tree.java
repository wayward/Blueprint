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

package org.codemined;

import java.util.List;

/**
 * An immutable N-ary tree.
 * 
 * @author Zoran Rilak
 */
public interface Tree<K,V> extends Iterable<Tree<K,V>> {

  K key();

  V value();

  Tree<K,V> parent();

  List<K> path();

  Tree<K,V> get(K subKey);

  Tree<K,V> get(List<K> path);

  boolean contains(K subKey);

  Tree<K,V> put(K subKey, V value);

  Tree<K,V> put(List<K> path, V value);

  Tree<K,V> put(List<K> path, V intermediaryValue, V value);

  /**
   * <p>Returns the number of sub-trees.</p>
   *
   * @return
   */
  int size();

}

