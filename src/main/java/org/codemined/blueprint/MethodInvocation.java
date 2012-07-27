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

import org.codemined.util.Types;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Represents an invocation of a method from a Blueprint interface.
 *
 * @author Zoran Rilak
 * @version 0.1
 * @since 0.1
 */
class MethodInvocation {

  private static final Object[] NO_ARGS = new Object[] {};

  /* Java reflection object representing the method being called. */
  private final Method method;

  /* A (potentially empty) array of arguments passed to the method when called. */
  private final Object[] args;

  private final Class<?> returnType;

  private final Class<?> hintedType;

  public MethodInvocation(Method method, Object[] args) {
    this.method = method;
    this.args = unwrapRuntimeArguments(args);
    this.returnType = Types.deprimitivize(method.getReturnType());
    this.hintedType = Types.deprimitivize(getHintedType0());
  }


  public Class<?> getReturnType() {
    return returnType;
  }


  public Class<?> getHintedType() {
    return hintedType;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MethodInvocation other = (MethodInvocation) o;
    return (method == null ? other.method == null : method.equals(other.method)) &&
            Arrays.equals(args, other.args);
  }


  @Override
  public int hashCode() {
    int result = method != null ? method.hashCode() : 0;
    result = 31 * result + (args != null ? Arrays.hashCode(args) : 0);
    return result;
  }


  /* Privates ------------------------------------------------------- */

  private Object[] unwrapRuntimeArguments(Object[] args) {
    /* promote a null argument list into an empty array */
    if (args == null) {
      return NO_ARGS;
    }
    /* unwrap single vararg array */
    if (args[0] instanceof Object[] && args.length == 1) {
      return (Object[]) args[0];
    }
    return args;
  }


  private Class<?> getHintedType0() {
    Class<?> hintedType;

    hintedType = getRuntimeTypeHint0();
    if (hintedType != null) {
      return getRuntimeTypeHint0();
    }
    hintedType = getAnnotationTypeHint0();
    if (hintedType != null) {
      return hintedType;
    }

    return null;
  }


  private Class<?> getAnnotationTypeHint0() {
    UseType ann = method.getAnnotation(UseType.class);
    if (ann != null) {
      return ann.value();
    }
    return null;
  }


  /**
   * Ensures that Blueprint method call semantic has been observed.
   * @return class passed as the runtime type hint or null
   */
  private Class<?> getRuntimeTypeHint0() {
    /* ensure that the blueprint method call semantic has been observed */
    switch (args.length) {
      case 0:
        return null;
      case 1:
        if (! (args[0] instanceof Class)) {
          throw new BlueprintException("Optional type hint argument must be an instance of Class");
        }
        return (Class<?>) args[0];
      default:
        throw new BlueprintException("Blueprint methods may only take one optional type hint argument");
    }
  }


}
