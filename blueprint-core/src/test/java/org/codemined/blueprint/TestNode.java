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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author Zoran Rilak
 */
public class TestNode implements ConfigNode<TestNode> {

  String value;

  ArrayList<TestNode> list;

  HashMap<String, TestNode> subTrees;


  public TestNode() {
    this.value = null;
    this.subTrees = new HashMap<String, TestNode>();
  }

  public TestNode(String value) {
    this();
    this.value = value;
  }

  @Override
  public boolean hasValue() {
    return value != null;
  }

  @Override
  public String getValue() {
    return value;
  }

  @Override
  public boolean hasArrayNodes() {
    return list != null;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public List<TestNode> getArrayNodes() {
    return list;
  }

  @Override
  public boolean containsKey(String key) {
    return subTrees.containsKey(key);
  }

  @Override
  public TestNode getChildNode(String key) {
    return subTrees.get(key);
  }

  @Override
  public Set<String> keySet() {
    return subTrees.keySet();
  }

  public TestNode put(String key, String value) {
    TestNode t = new TestNode(value);
    subTrees.put(key, t);
    return t;
  }

  public void setList(Object... elements) {
    this.list = new ArrayList<TestNode>();
    for (Object e : elements) {
      this.list.add(new TestNode(e.toString()));
    }
  }

}
