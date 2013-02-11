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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
* @author Zoran Rilak
*/
class Node {
  String value;
  Map<String, Node> children;
  ArrayList<Node> array;

  public Node() {
    this(null);
  }

  public Node(String value) {
    this.value = value;
    this.children = new HashMap<String, Node>();
    this.array = new ArrayList<Node>(3);
  }

  static Node withValue(String value) {
    return new Node(value);
  }

  public String getValue() {
    return value;
  }

  public Node getChild(String key) {
    return children.get(key);
  }

  public Map<String, Node> getChildren() {
    return children;
  }

  public Node getArrayItem(int index) {
    if (index < array.size()) {
      return array.get(index);
    } else {
      return null;
    }
  }

  public int getArraySize() {
    return array.size();
  }


  public void setValue(String value) {
    value = value;
  }

  public Node withChild(String key, Node child) {
    this.children.put(key, child);
    return this;
  }

  public Node withChild(String key, String value) {
    return withChild(key, new Node(value));
  }

  public Node withArrayItems(Node... item) {
    array.addAll(Arrays.asList(item));
    return this;
  }

  public Node withArrayItems(String... values) {
    for (String s : values) {
      array.add(new Node(s));
    }
    return this;
  }

}
