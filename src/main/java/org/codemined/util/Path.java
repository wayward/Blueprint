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

package org.codemined.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A sequence of keys representing a path in a tree starting from the root node.
 *
 * <p>This is a helper class that adds some utility methods to ArrayList.</p>
 * 
 * <p>Instances of this class are immutable.</p>
 *
 * @author Zoran Rilak
 */
public class Path<K> extends ArrayList<K> {

  public Path() {
    super();
  }

  public Path(List<K> keys) {
    super(keys);
  }

  public Path(K... keys) {
    if (keys != null) {
      addAll(Arrays.asList(keys));
    }
  }
  
  @SuppressWarnings("unchecked")
  public Path<K> to(K subKey) {
    return via(subKey);
  }

  public Path<K> via(K... subKeys) {
    if (subKeys == null) {
      return this;
    }
    Path<K> newPath = new Path<K>(this);
    newPath.addAll(Arrays.asList(subKeys));
    return newPath;
  }

  @Override
  public String toString() {
    ArrayList<String> l = new ArrayList<String>(this.size());
    for (K k : this) {
      l.add(k.toString());
    }
    return Strings.join("/", l);
  }

  /* equals(Object) and hashCode() from ArrayList behave as required for Path, too. */

}
