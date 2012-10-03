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

import org.codemined.blueprint.impl.IdentityKeyResolver;
import org.codemined.util.Path;
import org.codemined.util.Strings;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * Creates a blueprint object from an interface and a configuration source.
 * 
 * @author Zoran Rilak
 */
public class Blueprint {


  public static <T> T create(Class<T> iface, ConfigTree<?> tree) {
    return create(iface, tree, new IdentityKeyResolver());
  }

  /**
   * Creates a blueprint object.
   *
   * @param iface the interface for configuration access.
   * @param tree configuration tree.
   * @return an instance implementing {@code iface} whose methods return values from the configuration.
   */
  public static <T> T create(Class<T> iface, ConfigTree<?> tree, KeyResolver keyResolver) {
    checkInterface(iface);

    final Deserializer deserializer = new Deserializer(iface.getClassLoader(), keyResolver);
    final Stub<T> stub = new Stub<T>(iface, tree, new Path<String>(), deserializer, keyResolver);
    final T blueprint = stub.getProxy();

    return blueprint;
  }


  /* Privates ------------------------------------------------------- */

  /**
   * Checks if an interface is conformant to the semantics required by Blueprint.
   *
   * @param iface
   */
  private static void checkInterface(Class<?> iface) {
    if (! iface.isInterface()) {
      throw new IllegalArgumentException(String.format(
              "Blueprints must be constructed from interfaces; %s is not an interface.",
              iface));
    }

    List<String> failedMethodChecks = new LinkedList<String>();
    for (Method method : iface.getMethods()) {
      List<String> failedChecksForMethod = new LinkedList<String>();
      BlueprintMethod.checkReturnType(method.getReturnType(), failedChecksForMethod);
      BlueprintMethod.checkArguments(method, failedChecksForMethod);

      for (String error : failedChecksForMethod) {
        failedMethodChecks.add(String.format("method %s: %s\n", method, error));
      }

      if (! failedMethodChecks.isEmpty()) {
        throw new IllegalArgumentException(String.format(
                "Interface %s cannot be used to construct a blueprint:\n%s",
                iface,
                Strings.join("\n", failedMethodChecks)));
      }
    }
  }

}
