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
public class TestConfigTree extends ConfigTree<TestConfigTree> {

  String value;

  ArrayList<TestConfigTree> list;

  HashMap<String, TestConfigTree> subTrees;


  public TestConfigTree() {
    this.value = null;
    this.list = new ArrayList<TestConfigTree>();
    this.subTrees = new HashMap<String, TestConfigTree>();
  }

  public TestConfigTree(String value) {
    this();
    this.value = value;
  }

  @Override
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public List<TestConfigTree> getList() {
    return list;
  }

  @Override
  public boolean containsTree(String key) {
    return subTrees.containsKey(key);
  }

  @Override
  public TestConfigTree getTree(String key) {
    return subTrees.get(key);
  }

  @Override
  public Set<String> keySet() {
    return subTrees.keySet();
  }

  public TestConfigTree put(String key, String value) {
    TestConfigTree t = new TestConfigTree(value);
    subTrees.put(key, t);
    return t;
  }

  public void setList(Object... elements) {
    this.list = new ArrayList<TestConfigTree>();
    for (Object e : elements) {
      this.list.add(new TestConfigTree(e.toString()));
    }
  }

}
