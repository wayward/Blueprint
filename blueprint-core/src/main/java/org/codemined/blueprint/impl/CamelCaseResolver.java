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
public class CamelCaseResolver implements KeyResolver {

  @Override
  public String resolve(String methodName) {
    String key = methodName;

    // Split words before the last uppercase character in a contiguous run.
    key = key.replaceAll("([A-Z]+)([A-Z][^A-Z])", "$1_$2");

    // Split words between a non-uppercase character and the following uppercase character.
    // Exclude underscores inserted by the previous rule.
    key = key.replaceAll("([^A-Z_])([A-Z])", "$1_$2");

    return key.toLowerCase();
  }

}
