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

import org.codemined.blueprint.Source;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;

import java.util.*;

/**
 * A configuration source backed by the Apache configuration reader.
 * 
 * @author Zoran Rilak
 * @version 0.1
 * @since 0.1
 */
public class ApacheConfigurationSource implements Source {
  private final Configuration configuration;


  public ApacheConfigurationSource(Configuration... configurations) {
    CompositeConfiguration config = new CompositeConfiguration();
    for (Configuration c : configurations) {
      config.addConfiguration(c);
    }
    this.configuration = config;
  }


  @Override
  public String composePath(String... components) {
    StringBuilder sb = new StringBuilder();
    for (String c : components) {
      if (c != null) {
        sb.append(c).append('.');
      }
    }
    if (sb.length() > 0) {
      sb = sb.delete(sb.length()-1, sb.length());
    }
    return sb.toString();
  }


  @Override
  public String getString(String path) {
    return configuration.getString(path);
  }


  @Override
  public Collection<String> getCollection(String path) {
    LinkedList<String> list = new LinkedList<String>();
    for (Object o : configuration.getList(path)) {
      list.add((String) o);
    }
    return list;
  }


  public Iterator<String> getSubComponents(final String path) {
    final Iterator<String> iter = configuration.getKeys(path);

    return new Iterator<String>() {
      String nextComponent;
      final Set<String> seenComponents = new HashSet<String>();

      @Override
      public boolean hasNext() {
        if (nextComponent != null) {
          return true;
        }

        /* generate all unique sub-keys under this path */
        while (nextComponent == null && iter.hasNext()) {
          String key = iter.next();
          if (! key.equals(path)) {
            /* trim leading root path and the `.' after it */
            key = key.substring(path.length() + 1);
            /* trim any trailing components after the first one */
            int dotPosition = key.indexOf('.');
            if (dotPosition >= 0) {
              key = key.substring(0, dotPosition);
            }

            if (seenComponents.contains(key)) {
              nextComponent = null;
            } else {
              nextComponent = key;
              seenComponents.add(nextComponent);
            }
          }
        }
        return (nextComponent != null);
      }

      @Override
      public String next() {
        if (!hasNext()) {
          throw new NoSuchElementException(
              String.format("For path: %s, last: %s", path, nextComponent));
        }
        String s = nextComponent;
        nextComponent = null;
        return s;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

}
