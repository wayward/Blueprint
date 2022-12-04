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

import org.codehaus.jackson.map.ObjectMapper;
import org.codemined.blueprint.ConfigNode;

import java.io.IOException;
import java.util.*;

/**
 * @author Zoran Rilak
 */
public class JsonNode implements ConfigNode<JsonNode> {

  private static final ObjectMapper mapper = new ObjectMapper();

  private final org.codehaus.jackson.JsonNode jsonNode;

  private ArrayList<JsonNode> listElements;

  private HashMap<String, JsonNode> subTrees;


  public JsonNode(String fileName)
          throws IOException {
    this(mapper.readTree(JsonNode.class.getClassLoader().getResourceAsStream(fileName)));
  }

  protected JsonNode(org.codehaus.jackson.JsonNode jsonNode) {
    this.jsonNode = jsonNode;
  }

  @Override
  public boolean hasValue() {
    return false;
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
  public boolean hasArrayNodes() {
    return jsonNode.isArray();
  }

  @Override
  public List<JsonNode> getArrayNodes() {
    if (this.hasArrayNodes() && (listElements == null)) {
      listElements = new ArrayList<>();
      for (org.codehaus.jackson.JsonNode node : jsonNode) {
        listElements.add(new JsonNode(node));
      }
    }
    return listElements;
  }

  @Override
  public boolean containsKey(String key) {
    return jsonNode.has(key);
  }

  @Override
  public JsonNode getChildNode(String key) {
    if (! jsonNode.isObject()) {
      return null;
    }
    if (this.subTrees == null) {
      this.subTrees = new HashMap<String, JsonNode>();
    }
    JsonNode tree = this.subTrees.get(key);
    if (tree == null) {
      tree = new JsonNode(jsonNode.get(key));
      subTrees.put(key, tree);
    }
    return tree;
  }

  @Override
  public Set<String> keySet() {
    return subTrees.keySet();
  }

}
