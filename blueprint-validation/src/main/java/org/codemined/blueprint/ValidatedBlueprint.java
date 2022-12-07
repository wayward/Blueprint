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

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.executable.ExecutableValidator;
import org.codemined.blueprint.impl.IdentityKeyResolver;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Zoran Rilak
 */
public class ValidatedBlueprint {

  private static final Object[] NULL_HINT = new Object[] { null };


  public static <T> T create(Class<T> iface, ConfigNode<?> node)
          throws ConstraintViolationException {
    return create(iface, node, new IdentityKeyResolver());
  }

  public static <T> T create(Class<T> iface, ConfigNode<?> node, KeyResolver keyResolver)
          throws ConstraintViolationException {
    T blueprint = Blueprint.create(iface, node, keyResolver);

    ExecutableValidator validator;
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      validator = factory.getValidator().forExecutables();
    }

    Set<ConstraintViolation<T>> violations = validate(iface, blueprint, validator);
    if (! violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }

    return blueprint;
  }

  /* Privates ------------------------------------------------------- */

  /**
   * Validates all methods on a blueprint interface.
   *
   * @param iface
   * @param blueprint
   * @param validator
   * @param <T>
   * @return
   * @throws ConstraintViolationException if the configuration fails to validate against
   * {@code javax.validation.constraints.*} annotations present of {@code iface}'s methods.
   */
  private static <T> Set<ConstraintViolation<T>> validate(Class<T> iface, T blueprint, ExecutableValidator validator)
          throws ConstraintViolationException {
    Set<ConstraintViolation<T>> violations = new HashSet<>();
    try {
      for (Method ifaceMethod : iface.getMethods()) {
        //FIXME why query the blueprint instance again? Check this.
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

          Set<ConstraintViolation<T>> vs = validator.validateReturnValue(blueprint, method, value);
          violations.addAll(vs);
        }
      }

      return violations;

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
