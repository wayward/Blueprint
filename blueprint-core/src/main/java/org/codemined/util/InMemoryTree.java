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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * <p>In-memory tree.</p>
 * 
 * <p><em>NOT THREAD-SAFE.</em></p>
 *
 * TODO make abstract, remove `value'
 *
 * @param <K>
 * @param <V>
 */
public class InMemoryTree<K,V> extends AbstractTree<K,V> {

  private V value;

  private HashMap<K,Tree<K,V>> subTrees;


  public InMemoryTree() {
    this(null);
  }

  public InMemoryTree(V value) {
    super();
    this.value = value;
    this.subTrees = new HashMap<K,Tree<K,V>>();
  }

  @Override
  public V getValue() {
    return value;
  }

  @Override
  public Tree<K,V> get(K key) {
    return subTrees.get(key);
  }

  @Override
  public Tree<K,V> put(K key, V value) {
    Tree<K,V> t = subTrees.get(key);
    if (t == null) {
      t = new InMemoryTree<K,V>(value);
      subTrees.put(key, t);
    } else {
      t.setValue(value);
    }
    return t;
  }

  @Override
  public Tree<K,V> putTree(K key, Tree<K, V> subTree) {
    return subTrees.put(key, subTree);
  }

  @Override
  public void setValue(V value) {
    this.value = value;
  }

  @Override
  public boolean contains(K key) {
    return subTrees.containsKey(key);
  }

  @Override
  public Iterator<Tree<K,V>> iterator() {
    return new SubTreeIterator();
  }

  @Override
  public int size() {
    return subTrees.size();
  }

  @Override
  public Set<Map.Entry<K, Tree<K, V>>> entrySet() {
    return subTrees.entrySet();
  }

  /* Builder methods ------------------------------------------------ */

  public InMemoryTree<K, V> with(K key, V value) {
    put(key, value);
    return this;
  }

  public InMemoryTree<K, V> with(K key, InMemoryTree<K, V> tree) {
    putTree(key, tree);
    return this;
  }


  /* Privates ------------------------------------------------------- */

  class SubTreeIterator implements Iterator<Tree<K,V>> {

    private Iterator<Tree<K, V>> iter;

    public SubTreeIterator() {
      this.iter = subTrees.values().iterator();
    }

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

  }

}
