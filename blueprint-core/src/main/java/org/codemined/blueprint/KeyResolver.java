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

package org.codemined.blueprint;

import org.codemined.blueprint.impl.BeanKeyResolver;
import org.codemined.blueprint.impl.CamelCaseResolver;
import org.codemined.blueprint.impl.IdentityKeyResolver;

/**
 * Resolves the name of the configuration key to look up based on the method being called.
 *
 * @author Zoran Rilak
 */
public interface KeyResolver {
  public static final KeyResolver IDENTITY = new IdentityKeyResolver();
  public static final KeyResolver CAMEL_CASE = new CamelCaseResolver();
  public static final KeyResolver BEAN = new BeanKeyResolver();

  public String resolve(String methodName);

}
