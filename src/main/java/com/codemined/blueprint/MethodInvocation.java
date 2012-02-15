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

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Represents an invocation of a method from a Blueprint target interface.
 *
 * @author Zoran Rilak
 * @version 0.1
 * @since 0.1
 */
class MethodInvocation {
  private final Method method;
  private final Object[] args;
  private final Class<?> returnType;
  private final Class<?> hintedType;

  public MethodInvocation(Method method, Object[] args) {
    this.method = method;
    this.args = args;
    this.returnType = TypeUtil.deprimitivize(method.getReturnType());
    this.hintedType = TypeUtil.deprimitivize(getHintedType0());
  }


  public Class<?> getReturnType() {
    return returnType;
  }


  public Class<?> getHintedType() {
    return hintedType;
  }


  /* Privates ------------------------------------------------------- */


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


  private Class<?> getRuntimeTypeHint0() {
    Object[] args = this.args;

    if (args == null) {
      return null;
    }
    // unwrap single vararg array before continuing
    if (args[0] instanceof Class[] && args.length == 1) {
      args = (Object[]) args[0];
    }
    switch (args.length) {
      case 0:
        return null;
      case 1:
        if (! (args[0] instanceof Class)) {
          throw new BlueprintException("Optional type hint argument must be an instance of Class");
        }
        return (Class<?>) args[0];
      default:
        throw new BlueprintException("Item methods may only take one optional type hint argument.");
    }
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

}
