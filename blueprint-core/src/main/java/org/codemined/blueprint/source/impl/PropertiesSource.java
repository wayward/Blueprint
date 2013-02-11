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

package org.codemined.blueprint.source.impl;

import org.codemined.blueprint.Path;
import org.codemined.blueprint.source.Source;

import java.io.File;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

/**
 * @author Zoran Rilak
 */
public class PropertiesSource implements Source {

  private Properties properties;


  public PropertiesSource(String fileName) {

  }

  public PropertiesSource(File file) {

  }

  public PropertiesSource(URL url) {

  }

  public PropertiesSource(Properties properties) {
    this.properties = properties;
  }


  @Override
  public boolean isValid(Path path) {
    return true;  //TODO implement PropertiesSource#isValid
  }

  @Override
  public boolean containsPath(Path path) {
    return isValid(path) && properties.containsKey(path.toString("."));
  }

  @Override
  public Set<String> getSubKeys(Path path) {
    Set<String> keys = properties.stringPropertyNames();
    String prefix = path.toString(".");
    for (String key : keys) {
      if (! key.startsWith(prefix)) {
        keys.remove(key);
      }
    }
    return keys;
  }

  @Override
  public String getStringValue(Path path) {
    return null;  //TODO implement PropertiesSource#getStringValue
  }

  @Override
  public Integer getListSize(Path path) {
    return null;  //TODO implement PropertiesSource#getListSize
  }

  @Override
  public void reload() {
    //TODO implement PropertiesSource#reload
  }

}
