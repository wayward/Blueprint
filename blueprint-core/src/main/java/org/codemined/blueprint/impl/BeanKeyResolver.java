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

import org.codemined.blueprint.KeyResolver;

/**
 * @author Zoran Rilak
 */
public class BeanKeyResolver implements KeyResolver {

  @Override
  public String resolve(String methodName) {
    if (methodName.startsWith("get") && methodName.length() > 3) {
      StringBuilder sb = new StringBuilder();

      // copy the first character after "get",
      // transforming it to lowercase if the character immediately following is lowercase too
      int copyFrom = 3;
      if (methodName.length() > 4 && Character.isLowerCase(methodName.charAt(4))) {
        sb.append(methodName.substring(3, 4).toLowerCase());
        copyFrom = 4;
      }
      sb.append(methodName.substring(copyFrom));
      return sb.toString();

    } else {
      return methodName;
    }
  }

}
