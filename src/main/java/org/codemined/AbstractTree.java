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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Zoran Rilak
 */
public abstract class AbstractTree<K,V> implements Tree<K,V> {

  private Tree<K,V> parent;

  protected AbstractTree(Tree<K,V> parent) {
    this.parent = parent;
  }

  @Override
  public Tree<K,V> parent() {
    return parent;
  }

  @Override
  public List<K> path() {
    List<K> path;
    if (parent() != null) {
      path = parent().path();
    } else {
      path = new LinkedList<K>();
    }
    path.add(key());
    return path;
  }

  @Override
  public Tree<K,V> get(List<K> path) {
    Tree<K,V> t = this;
    for (K key : path) {
      Tree<K,V> next = t.get(key);
      if (next == null) {
        return null;
      }
      t = next;
    }
    return t;
  }

  @Override
  public Tree<K,V> put(List<K> path, V value) {
    return put(path, null, value);
  }

  @Override
  public Tree<K,V> put(List<K> path, V intermediaryValue, V value) {
    Tree<K,V> t = this;
    Iterator<K> iter = path.iterator();
    while (iter.hasNext()) {
      K k = iter.next();
      Tree<K,V> next = t.get(k);
      if (next == null) {
        t = t.put(k, iter.hasNext() ? intermediaryValue : value);
      }
    }
    return t;
  }

}
