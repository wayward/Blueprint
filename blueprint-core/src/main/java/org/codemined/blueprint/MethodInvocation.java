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
 * Class MethodInvocation represents an invocation of a method from a blueprint interface.
 * <p>
 *   Such an invocation can range from being very simple, in case of methods without type hints,
 *   to complex, for methods with both @UseType and runtime type hints.  MethodInvocation
 *   captures all type hints recognized by Blueprint and calculates the final hinted type.
 * </p>
 *
 * <p>
 *   This class will observe Blueprint hinting conventions but will otherwise not check for
 *   assignment compatibility between the method's declared return type and its hinted type.
 *   This check is performed by the deserializer, which might decide to use type hint in a
 *   different way (for example, as a collection element type rather than the type of the object
 *   returned).
 * </p>
 *
 *
 * @author Zoran Rilak
 * @version 0.1
 * @since 0.1
 */
class MethodInvocation {

  /** Stand-in for null argument list of no-args methods. */
  private static final Object[] NO_ARGS = new Object[] {};

  /** Java reflection object representing the method being called. */
  private final Method method;

  /** A (potentially empty) array of arguments passed to the method when called. */
  private final Object[] args;

  /** Method's declared return type. */
  private final Class<?> returnType;

  /** Method's calculated hinted return type or null if no type hinting is used. */
  private final Class<?> hintedType;

  /**
   * Creates a new MethodInvocation object from a method object and its runtime arguments.
   * <p>
   *   This constructor will enforce Blueprint method calling conventions; namely:
   *   <ul>
   *     <li>methods can take up to one argument, either as a fixed or a vararg parameter;</li>
   *     <li>if given, argument must be an instance of java.lang.Class;</li>
   *     <li>when determining the final hinted type, arguments take precedence over @UseType.</li>
   *   </ul>
   * </p>
   * @param method the method being called.
   * @param args runtime arguments passed in the method call.
   */
  public MethodInvocation(Method method, Object[] args) {
    this.method = method;
    this.args = unwrapRuntimeArguments(args);
    this.returnType = Types.deprimitivize(method.getReturnType());
    this.hintedType = Types.deprimitivize(getHintedType0());
  }


  /**
   * Gets the method's declared returned value.
   *
   * @return method's declared return value.
   */
  public Class<?> getReturnType() {
    return returnType;
  }


  /**
   * Gets the method's calculated type hint.
   * <p>
   *   Type hint is a class object passed to the method as an argument at runtime or specified
   *   in the {@link UseType} annotation.  When determining the final type to be used as a
   *   type hint, runtime argument will take precedence over the type given by the annotation.
   * </p>
   *
   * @return method's calculated type hint or null if none given.
   */
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

  /**
   * Flattens the argument array if it consists of a single array element.
   * Also returns an empty array for a null argument.
   *
   * @param args argument array to unwrap.
   * @return a (possibly empty) array of arguments.
   */
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


  /**
   * Gets the class to use as a type hint, observing rules of precedence.
   *
   * @return class to use as a type hint or null if none given.
   */
  private Class<?> getHintedType0() {
    Class<?> hintedType;

    /* Apply type hint rules, observing precedence */
    hintedType = getRuntimeTypeHint0();
    if (hintedType == null) {
      hintedType = getAnnotationTypeHint0();
    }

    return hintedType;
  }


  /**
   * Gets the type hint specified in the {@link UseType} annotation.
   *
   * @return class object or null.
   */
  private Class<?> getAnnotationTypeHint0() {
    UseType ann = method.getAnnotation(UseType.class);
    if (ann != null) {
      return ann.value();
    }
    return null;
  }


  /**
   * Gets the type hint specified as a value of an argument at the time of invocation.
   *
   * @return class object or null.
   */
  private Class<?> getRuntimeTypeHint0() {
    /* ensure that the blueprint method call semantic has been observed */
    switch (args.length) {
      case 0:
        return null;
      case 1:
        if (! (args[0] instanceof Class)) {
          throw new BlueprintException("Type hint must be a class, "
                  + "but an instance of " + args[0].getClass().getName() + " was given");
        }
        return (Class<?>) args[0];
      default:
        throw new BlueprintException("Blueprint methods may only take one optional type hint argument");
    }
  }


}
