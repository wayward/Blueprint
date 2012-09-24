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

import org.codemined.util.Path;
import org.codemined.util.Tree;

import java.util.List;

/**
 * Creates a blueprint object from an interface and a configuration source.
 * 
 * @author Zoran Rilak
 */
public class Blueprint {

  private static Validator validator = null;

  static {
    //=== Load the validation framework ===
    try {
      Class<?> clazz = Class.forName("org.codemined.blueprint.ValidatorImpl");
      Blueprint.validator = (Validator) clazz.newInstance();

    } catch (ClassNotFoundException ignored) {
      // ClassNotFoundException means that the validation isn't loaded, so we'll pass
    } catch (Exception e) {
      // Any other exception is a problem with the framework
      throw new ExceptionInInitializerError(e);
    }
  }

  /**
   * Creates a blueprint object.
   *
   * @param iface the interface for configuration access.
   * @param tree configuration tree.
   * @return an instance implementing {@code iface} whose methods return values from the configuration.
   * @throws ConfigurationValidationException if validation is loaded and the configuration fails to validate
   * against {@code javax.validation.constraints.*} annotations present of {@code iface}'s methods.
   */
  public static <T> T create(Class<T> iface, Tree<String, String> tree)
          throws ConfigurationValidationException {
    if (! iface.isInterface()) {
      throw new IllegalArgumentException("Blueprints must be constructed from interfaces" +
              "; not an interface: " + iface);
    }

    final Deserializer deserializer = new Deserializer(iface.getClassLoader());
    final Stub<T> stub = new Stub<T>(iface, tree, new Path<String>(), deserializer);
    final T blueprint = stub.getProxy();

    if (validator != null) {
      List<String> failedValidations = validator.validate(iface, blueprint);
      if (failedValidations.size() > 0) {
        throw new ConfigurationValidationException(failedValidations);
      }
    }

    return blueprint;
  }

}
