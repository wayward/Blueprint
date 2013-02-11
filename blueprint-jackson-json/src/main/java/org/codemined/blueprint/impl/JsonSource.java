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

import org.codemined.blueprint.Path;
import org.codemined.blueprint.source.Source;

import java.util.Set;

/**
 * @author Zoran Rilak
 */
public class JsonSource implements Source {

  @Override
  public boolean isValid(Path path) {
    return false;  //TODO implement JsonSource#isValid
  }

  @Override
  public boolean containsPath(Path path) {
    return false;  //TODO implement JsonSource#containsPath
  }

  @Override
  public Set<String> getSubKeys(Path path) {
    return null;  //TODO implement JsonSource#getSubKeys
  }

  @Override
  public String getStringValue(Path path) {
    return null;  //TODO implement JsonSource#getStringValue
  }

  @Override
  public Integer getListSize(Path path) {
    return null;  //TODO implement JsonSource#getListSize
  }

  @Override
  public void reload() {
    //TODO implement JsonSource#reload
  }
}
