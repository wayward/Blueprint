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

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.method.MethodConstraintViolation;
import org.hibernate.validator.method.MethodValidator;

import javax.validation.Validation;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Zoran Rilak
 */
public class ValidatedBlueprint {

  private static final Object[] NULL_HINT = new Object[] { null };


  public static <T> T create(Class<T> iface, ConfigTree tree)
          throws ConfigurationValidationException {
    T blueprint = Blueprint.create(iface, tree);

      List<String> failedValidations = validate(iface, blueprint,
              Validation.byProvider(HibernateValidator.class)
                      .configure()
                      .buildValidatorFactory()
                      .getValidator()
                      .unwrap(MethodValidator.class));
    if (failedValidations.size() > 0) {
      throw new ConfigurationValidationException(failedValidations);
    }

    return blueprint;
  }

  /* Privates ------------------------------------------------------- */

  /**
   * Validates all methods on a blueprint interface.
   *
   * @param iface
   * @param blueprint
   * @param methodValidator
   * @param <T>
   * @return
   * @throws ConfigurationValidationException if the configuration fails to validate against
   * {@code javax.validation.constraints.*} annotations present of {@code iface}'s methods.
   */
  private static <T> List<String> validate(Class<T> iface, T blueprint, MethodValidator methodValidator)
          throws ConfigurationValidationException {
    LinkedList<String> failedValidations = new LinkedList<String>();
    try {
      for (Method ifaceMethod : iface.getMethods()) {
        //FIXME why query the blueprint instance again?  I forgot.  Need to check.
        // get the method
        Method method = blueprint.getClass().getMethod(
                ifaceMethod.getName(),
                ifaceMethod.getParameterTypes());

        // validate the method if it comes from the blueprint interface
        if (method.getDeclaringClass().equals(blueprint.getClass())) {
          Object value;

          if (method.getParameterTypes().length == 0) {
            value = method.invoke(blueprint);
          } else {
            // pass null for runtime type hint (fall back to the declared return type)
            value = method.invoke(blueprint, NULL_HINT);
          }

          final Set<MethodConstraintViolation<T>> violations =
                  methodValidator.validateReturnValue(blueprint, method, value);
          for (MethodConstraintViolation<T> violation : violations) {
            failedValidations.add(violation.getMessage());
          }
        }
      }

      return failedValidations;

    } catch (Exception e) {
      /* all other exceptions indicate bugs in Blueprint */
      if (e.getCause() instanceof RuntimeException) {
        throw (RuntimeException) e.getCause();
      } else {
        throw new RuntimeException("Uncaught checked exception!  " +
                "This might indicate a bug in Blueprint.");
      }
    }
  }

}
