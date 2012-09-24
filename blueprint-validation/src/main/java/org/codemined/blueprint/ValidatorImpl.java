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
class ValidatorImpl implements Validator {

  private MethodValidator methodValidator;

  public ValidatorImpl() {
    this.methodValidator = Validation.byProvider(HibernateValidator.class)
            .configure()
            .buildValidatorFactory()
            .getValidator()
            .unwrap(MethodValidator.class);
  }

  public <T> List<String> validate(Class<T> iface, T blueprint)
          throws ConfigurationValidationException {
    LinkedList<String> failedValidations = new LinkedList<String>();
    try {
      for (Method ifaceMethod : iface.getMethods()) {
        Method method = blueprint.getClass().getMethod(
                ifaceMethod.getName(),
                ifaceMethod.getParameterTypes());
        /* skip methods with optional type hints (don't know what to pass) */
        if (method.getDeclaringClass().equals(blueprint.getClass()) &&
                method.getParameterTypes().length == 0) {
          Object returnValue = method.invoke(blueprint);

          final Set<MethodConstraintViolation<T>> violations =
                  methodValidator.validateReturnValue(blueprint, method, returnValue);
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
