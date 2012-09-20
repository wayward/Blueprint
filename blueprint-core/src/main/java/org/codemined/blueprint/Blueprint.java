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
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.method.MethodConstraintViolation;
import org.hibernate.validator.method.MethodValidator;

import javax.validation.Validation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Set;

/**
 * Creates a blueprint object from an interface and a configuration source.
 * 
 * @author Zoran Rilak
 */
public class Blueprint {
  private static MethodValidator validator = Validation
          .byProvider(HibernateValidator.class)
          .configure()
          .buildValidatorFactory()
          .getValidator()
          .unwrap(MethodValidator.class);


  /**
   * @param iface the interface for configuration access.
   * @param tree configuration tree
   * @return an object of {@code iface}, stubbed to return reified values from the configuration. 
   * @throws ConfigurationValidationException in case of errors
   */
  public static <T> T create(Class<T> iface, Tree<String, String> tree)
          throws ConfigurationValidationException {
    if (! iface.isInterface()) {
      throw new IllegalArgumentException("Blueprints must be constructed from interfaces." +
              "  Not an interface: " + iface);
    }

    final Deserializer deserializer = new Deserializer(iface.getClassLoader());
    final Stub<T> stub = new Stub<T>(iface, tree, new Path<String>(), deserializer);
    final T blueprint = stub.getProxy();

    validate(iface, blueprint);
    return blueprint;
  }


  /* Privates ------------------------------------------------------- */


  private static <T> void validate(Class<T> iface, T blueprint) 
          throws ConfigurationValidationException {
    LinkedList<String> failedValidations = new LinkedList<String>();

    try {
      for (Method ifaceMethod : iface.getMethods()) {
        Method method = blueprint.getClass().getMethod(
                ifaceMethod.getName(),
                ifaceMethod.getParameterTypes());
        /* skip methods with type hints as optional parameters
        (figuring out the correct arguments is nontrivial) */
        if (method.getDeclaringClass().equals(blueprint.getClass()) &&
                method.getParameterTypes().length == 0) {
          Object returnValue = method.invoke(blueprint);

          final Set<MethodConstraintViolation<T>> violations =
                  validator.validateReturnValue(blueprint, method, returnValue);
          for (MethodConstraintViolation<T> violation : violations) {
            failedValidations.add(violation.getMessage());
          }
        }
      }

      if (failedValidations.size() > 0) {
        throw new ConfigurationValidationException(failedValidations);
      }

    } catch (InvocationTargetException e) {
      if (e.getCause() instanceof RuntimeException) {
        throw (RuntimeException) e.getCause();
      } else {
        throw new RuntimeException("Uncaught checked exception!  " +
                "This might indicate a bug in Blueprint.");
      }

      /* all other exceptions indicate bugs in Blueprint */
    } catch (NoSuchMethodException e) {
      throw fromException(iface, e);
    } catch (IllegalAccessException e) {
      throw fromException(iface, e);
    }
  }

  private static <T> RuntimeException fromException(Class<T> iface, Throwable cause) {
    return new RuntimeException(String.format("For interface %s", iface), cause);
  }

}
