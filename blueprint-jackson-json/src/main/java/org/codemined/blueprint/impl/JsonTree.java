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

package org.codemined.blueprint.impl;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codemined.blueprint.ConfigTree;

import java.io.IOException;
import java.util.*;

/**
 * @author Zoran Rilak
 */
public class JsonTree extends ConfigTree<JsonTree> {

  private static final ObjectMapper mapper = new ObjectMapper();

  private final JsonNode jsonNode;

  private ArrayList<JsonTree> listElements;

  private HashMap<String, JsonTree> subTrees;


  public JsonTree(String fileName)
          throws IOException {
    this(mapper.readTree(JsonTree.class.getClassLoader().getResourceAsStream(fileName)));
  }

  protected JsonTree(JsonNode jsonNode) {
    this.jsonNode = jsonNode;
  }

  @Override
  public String getValue() {
    if (jsonNode.isValueNode()) {
      return jsonNode.asText();
    } else {
      return null;
    }
  }

  @Override
  public List<JsonTree> getList() {
    if (! jsonNode.isArray()) {
      return null;
    }
    if (this.listElements == null) {
      this.listElements = new ArrayList<JsonTree>();
      for (JsonNode node : jsonNode) {
        listElements.add(new JsonTree(node));
      }
    }
    return listElements;
  }

  @Override
  public boolean containsTree(String key) {
    return jsonNode.has(key);
  }

  @Override
  public JsonTree getTree(String key) {
    if (! jsonNode.isObject()) {
      return null;
    }
    if (this.subTrees == null) {
      this.subTrees = new HashMap<String, JsonTree>();
    }
    JsonTree tree = this.subTrees.get(key);
    if (tree == null) {
      tree = new JsonTree(jsonNode.get(key));
      subTrees.put(key, tree);
    }
    return tree;
  }

  @Override
  public Set<String> keySet() {
    return subTrees.keySet();
  }

}
