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

import java.util.*;

/**
 * @author Zoran Rilak
 */
public abstract class NamedTree<T> implements Iterable<NamedTree<T>> {
  private final String name;
  private final T value;
  private final HashMap<String, NamedTree<T>> children;


  public class Path {
    private final String car;
    private final Path cdr;


    public Path(String car, Path cdr) {
      this.car = car;
      this.cdr = cdr;
    }


    public String car() {
      return car;
    }


    public Path cdr() {
      return cdr;
    }

  }


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


  public boolean addChild(NamedTree<T> child) {
    return (children.put(child.name, child) != null);
  }


  public NamedTree<T> getTree(Path path) {
    if (path == null) {
      return this;
    }
    NamedTree<T> t;
    if ((t = children.get(path.car())) == null) {
      throw new NoSuchElementException();
    }
    return t.getTree(path.cdr());
  }


  @Override
  public Iterator<NamedTree<T>> iterator() {
    return children.values().iterator();
  }

}
