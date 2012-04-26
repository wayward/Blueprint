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

import java.util.HashMap;
import java.util.Iterator;

/**
 * <p>In-memory tree.</p>
 * 
 * <p><em>NOT THREAD-SAFE.</em></p>
 *
 * @param <K>
 * @param <V>
 */
public class InMemoryTree<K,V> extends AbstractTree<K,V> {

  private K key;

  private V value;

  private HashMap<K,InMemoryTree<K,V>> subtrees;


  public InMemoryTree(Tree<K,V> parent) {
    this(null, null, parent);
  }


  public InMemoryTree(K key, V value, Tree<K,V> parent) {
    super(parent);
    this.key = key;
    this.value = value;
    this.subtrees = new HashMap<K,InMemoryTree<K,V>>();
  }


  @Override
  public K key() {
    return key;
  }


  public V value() {
    return value;
  }


  @Override
  public Tree<K,V> get(K subKey) {
    return subtrees.get(subKey);
  }


  @Override
  public Tree<K,V> put(K subKey, V value) {
    InMemoryTree<K,V> t = subtrees.get(subKey);
    if (t == null) {
      t = new InMemoryTree<K,V>(subKey, value, this);
      subtrees.put(subKey, t);
    } else {
      t.setValue(value);
    }
    return t;
  }


  @Override
  public boolean contains(K subKey) {
    return subtrees.containsKey(subKey);
  }


  @Override
  public Iterator<Tree<K,V>> iterator() {
    final Iterator<InMemoryTree<K,V>> iter = subtrees.values().iterator();

    return new Iterator<Tree<K,V>>() {

      @Override
      public boolean hasNext() {
        return iter.hasNext();
      }

      @Override
      public Tree<K, V> next() {
        return iter.next();
      }

      @Override
      public void remove() {
        iter.remove();
      }
    };
  }


  @Override
  public int size() {
    return subtrees.size();
  }


  /* Protected methods ---------------------------------------------- */

  protected InMemoryTree<K,V> add(InMemoryTree<K,V> subTree) {
    return subtrees.put(subTree.key(), subTree);
  }


  protected void setKey(K key) {
    this.key = key;
  }


  protected void setValue(V value) {
    this.value = value;
  }

}
