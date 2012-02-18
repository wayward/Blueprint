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

package com.codemined.blueprint;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.method.MethodConstraintViolation;
import org.hibernate.validator.method.MethodValidator;

import javax.validation.Validation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Set;

/**
 * Creates a blueprint file from a configuration source.
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
   * @param source the source to get the configuration from
   * @return an object of {@code iface}, stubbed to return reified values from the configuration. 
   * @throws InvalidConfigurationException in case of errors
   */
  public static <T> T createBlueprint(Class<T> iface, Source source)
          throws InvalidConfigurationException {
    if (!iface.isInterface()) {
      throw new IllegalArgumentException(
        "Blueprints must be constructed from interfaces.  Not an interface: " + iface);
    }
    
    Stub<T> stub = new Stub<T>(iface, source, null);
    T blueprint = stub.getProxy();
    validate(iface, blueprint);
    return blueprint;
  }


  /* Privates ------------------------------------------------------- */


  private static <T> void validate(Class<T> iface, T blueprint) 
      throws InvalidConfigurationException {
    LinkedList<String> failedValidations = new LinkedList<String>();

    try {
      // Scan for methods declared on the blueprint interface,
      // invoke their equivalents on the blueprint proxy and
      // establish whether the values returned satisfy the constraints.
      // Note that we're currently ignoring methods with return type hints.
      for (Method ifaceMethod : iface.getMethods()) {
        Method method = blueprint.getClass()
                .getMethod(ifaceMethod.getName(), ifaceMethod.getParameterTypes());

        if (method.getDeclaringClass().equals(blueprint.getClass()) &&
                method.getParameterTypes().length == 0) {
          Object returnValue = method.invoke(blueprint);
          Set<MethodConstraintViolation<T>> violations =
              validator.validateReturnValue(blueprint, method, returnValue);
          for (MethodConstraintViolation<T> violation : violations) {
            failedValidations.add(violation.getMessage());
          }
        }
      }

      if (failedValidations.size() > 0) {
        throw new InvalidConfigurationException("There's a problem with the configuration.",
                failedValidations);
      }

    } catch (InvocationTargetException e) {
        throw fromException(e, iface, blueprint);
    } catch (NoSuchMethodException e) {
        throw fromException(e, iface, blueprint);
    } catch (IllegalAccessException e) {
      throw fromException(e, iface, blueprint);
    }
  }

  
  private static <T> BlueprintException fromException(
      Exception thrown, Class<T> iface, T blueprint) throws BlueprintException {
    throw new BlueprintException(
        String.format("For iface='%s', blueprint='%s'", iface, blueprint), thrown);
  }
}
