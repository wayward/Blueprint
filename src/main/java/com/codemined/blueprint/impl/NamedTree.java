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

package com.codemined.blueprint.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A string-decorated tree of objects of type {@code T}.
 * <p>
 * A particular object stored in the tree can be fetched using the 
 * sequence of string decorations starting from the current tree root.
 * The sequence is specified by the string list constructed with 
 * {@link Path}.
 * 
 * @param <T> The type of the objects contained in a tree.
 * @author Zoran Rilak
 */
public abstract class NamedTree<T> implements Iterable<NamedTree<T>> {
  private final String name;
  private final T value;
  private final HashMap<String, NamedTree<T>> children;


  /** Lisp-style list of string atoms, specified by the head atom and a tail list */
  private static class Path {
    private final String car;
    private final Path cdr;


    private Path(String car, Path cdr) {
      this.car = car;
      this.cdr = cdr;
    }


    /** Returns the head of the list */
    public String car() {
      return car;
    }


    /** Returns the tail of the list, or {@code null} if one does not exist. */
    public Path cdr() {
      return cdr;
    }
  }


  /**
   * @param value {@code null} value denotes an empty tail, i.e. 
   * the end of the list
   */
  protected NamedTree(String name, T value) {
    this.name = name;
    this.value = value;
    this.children = new HashMap<String, NamedTree<T>>();
  }


  public T getValue() {
    return value;
  }


  public Collection<NamedTree<T>> getChildren() {
    return Collections.unmodifiableCollection(children.values());
  }


  /** 
   * Adds a child node into {@code this} tree. 
   * 
   * @return {@code true} if the added child replaced an already 
   * existing child; {@code false} otherwise.
   */
  public boolean addChild(NamedTree<T> child) {
    NamedTree<T> oldValue = children.put(child.name, child);
    return oldValue != null;
  }


  /**
   * Gets the subtree of {@code this}, starting from {@code path}.
   * 
   * @param path the path from {@code this} to fetch; may be {@code null}
   * @return the subtree as specified by {@code path}; returns self for a null path
   * @throws NoSuchElementException if the path specified doesn't
   *     exist in this tree.
   */
  public NamedTree<T> getTree(Path path) {
    if (path == null) {
      return this;
    }
    NamedTree<T> t = children.get(path.car());
    if (t == null) {
      throw new NoSuchElementException(String.format("In path: %s", path));
    }
    return t.getTree(path.cdr());
  }


  @Override
  public Iterator<NamedTree<T>> iterator() {
    return children.values().iterator();
  }

}
