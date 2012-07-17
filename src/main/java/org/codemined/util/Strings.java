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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Zoran Rilak
 */
public class Strings {


  public static List<String> split(String str, String separator) {
    if (str == null) {
      throw new IllegalArgumentException("cannot split null string");
    }
    if (separator == null) {
      throw new IllegalArgumentException("separator cannot be null");
    }
    if (separator.isEmpty()) {
      throw new IllegalArgumentException("separator cannot be an empty string");
    }
    return Arrays.asList(str.split(Pattern.quote(separator)));
  }


  public static String join(String separator, List<String> components) {
    if (components == null) {
      throw new IllegalArgumentException("list cannot be null");
    }
    if (separator == null) {
      separator = "";
    }

    StringBuilder sb = new StringBuilder();
    Iterator<String> iter = components.iterator();
    while (iter.hasNext()) {
      String comp = iter.next();
      // skip null components
      if (comp == null) {
        continue;
      }

      if (sb.length() > 0) {
        sb.append(separator);
      }
      sb.append(comp);
    }

    return sb.toString();
  }


  public static String join(String separator, String... components) {
    return join(separator, Arrays.asList(components));
  }

}
