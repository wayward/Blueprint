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

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Zoran Rilak
 */
class BlueprintMethod {


  public static void checkReturnType(Class<?> type) {
    checkReturnType(type, null);
  }

  public static void checkReturnType(Class<?> type, List<String> failedChecks) {
    if (type == Void.class) {
      failedChecks.add("return type cannot be `void'");
    }
    if (type.isArray() && type.getComponentType().isArray()) {
      noteError("multi-dimensional arrays are not supported", failedChecks);
    }
  }

  public static void checkArguments(Method method) {
    checkArguments(method, null);
  }

  public static void checkArguments(Method method, List<String> failedChecks) {
    Class<?>[] argTypes = method.getParameterTypes();

    switch (argTypes.length) {
      case 0:
        break;

      case 1:
        Class<?> firstArgType = argTypes[0];
        if (firstArgType == Class.class) {
          break;
        }
        if (firstArgType.isArray() && firstArgType.getComponentType() == Class.class && method.isVarArgs()) {
          break;
        } else {
          noteError("type hint argument must be a Class or a var-arg list of Class", failedChecks);
        }
        break;

      default:
        noteError("too many arguments; blueprint methods may only take" +
                " up to one optional Class argument" +
                ", or a var-arg list of Class arguments", failedChecks);
    }
  }



  /** Privates ------------------------------------------------------ */

  private static void noteError(String error, List<String> errors) {
    if (errors == null) {
      throw new BlueprintException(error);
    } else {
      errors.add(error);
    }
  }

}
