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

package org.codemined.util.tree;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * @author Zoran Rilak
 */
public class TestProperties extends Properties {

  public TestProperties(String fileName)
          throws IOException {
    super();
    FileInputStream fos = null;
    try {
      fos = new FileInputStream(fileName);
      super.load(fos);
    } finally {
      if (fos != null) {
        try {
          fos.close();
        } catch (IOException ignored) {}
      }
    }
  }

  public Set<String> firstLevelKeys() {
    Set<String> keys = new HashSet<String>();
    for (String k : stringPropertyNames()) {
      // strip everything including and after the first dot
      int dotPos = k.indexOf(".");
      if (dotPos >= 0) {
        k = k.substring(0, dotPos);
      }
      keys.add(k);
    }
    return keys;
  }

}
