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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * Creates a blueprint object from an interface and a configuration source.
 * 
 * @author Zoran Rilak
 */
public class Blueprint {

  /**
   * @author Zoran Rilak
   * @version 0.1
   * @since 0.1
   */
  public static class Builder<T> {
    private Class<T> iface;
    private CompositeTree compositeTree;
    private KeyResolver keyResolver;

    Builder(Class<T> iface) {
      checkInterface(iface);
      this.iface = iface;
      this.compositeTree = new CompositeTree();
      this.keyResolver = KeyResolver.IDENTITY;
    }

    Builder<T> from(ConfigNode<?> node) {
      compositeTree.add(node);
      return this;
    }

    Builder<T> from(String fileName)
            throws FileNotFoundException {
      for (Source.Formats f : Source.Formats.values()) {
        if (f.matches(fileName)) {
          return this.from(fileName, f);
        }
      }
      throw new BlueprintException("Unable to determine the format of the configuration file " + fileName);
    }

    Builder<T> from(String fileName, Source.Format format)
            throws FileNotFoundException {
      compositeTree.add(format.load(new FileInputStream(fileName)));
      return this;
    }

    Builder<T> withKeyResolver(KeyResolver keyResolver) {
      this.keyResolver = keyResolver;
      return this;
    }

    T build() {
      return Blueprint.create(iface, compositeTree, keyResolver);
    }
  }

  public static <T> Builder<T> of(Class<T> iface) {
    return new Builder<T>(iface);
  }

  public static <T> T create(Class<T> iface, ConfigNode<?> node) {
    return create(iface, node, new IdentityKeyResolver());
  }

  /**
   * Creates a blueprint object.
   *
   * @param iface the interface for configuration access.
   * @param node configuration tree.
   * @return an instance implementing {@code iface} whose methods return values from the configuration.
   */
  public static <T> T create(Class<T> iface, ConfigNode<?> node, KeyResolver keyResolver) {
    checkInterface(iface);

    final Deserializer deserializer = new Deserializer(iface.getClassLoader(), keyResolver);
    final Stub<T> stub = new Stub<T>(iface, node, new Path<String>(), deserializer, keyResolver);
    return stub.getProxy();
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
