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

  private K key;

  private V value;

  private HashMap<K,Tree<K,V>> subTrees;


  public InMemoryTree() {
    this(null, null);
  }

  public InMemoryTree(K key, V value) {
    super();
    this.key = key;
    this.value = value;
    this.subTrees = new HashMap<K,Tree<K,V>>();
  }

  @Override
  public K getKey() {
    return key;
  }

  public V getValue() {
    return value;
  }

  @Override
  public Tree<K,V> get(K subKey) {
    return subTrees.get(subKey);
  }

  @Override
  public Tree<K,V> put(K subKey, V value) {
    Tree<K,V> t = subTrees.get(subKey);
    if (t == null) {
      t = new InMemoryTree<K,V>(subKey, value);
      subTrees.put(subKey, t);
    } else {
      t.setValue(value);
    }
    return t;
  }

  @Override
  public Tree<K,V> put(Tree<K,V> subTree) {
    return subTrees.put(subTree.getKey(), subTree);
  }

  @Override
  public void setKey(K key) {
    this.key = key;
  }

  @Override
  public void setValue(V value) {
    this.value = value;
  }

  @Override
  public boolean contains(K subKey) {
    return subTrees.containsKey(subKey);
  }

  @Override
  public Iterator<Tree<K,V>> iterator() {
    return new SubTreeIterator();
  }

  @Override
  public int size() {
    return subTrees.size();
  }


  /* Protected methods ---------------------------------------------- */

  protected Tree<K,V> add(Tree<K,V> subTree) {
    return subTrees.put(subTree.getKey(), subTree);
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
