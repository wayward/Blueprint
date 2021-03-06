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

/*
 BlueprintException:
   - CreationException: blueprint class cannot be created
   - ReadException: configuration cannot be read
   - ValidationException: configuration is invalid
 IllegalArgumentException during runtime for e.g. incorrect runtime hints.
 */

import org.codemined.util.Strings;

import java.util.LinkedList;

/**
 * Thrown to indicate [...]
 *
 * BlueprintException: unchecked; thrown if:
 *   - method with >1 argument (CT)
 *   - map w/o type hint (CT - creation time)
 *   - collection w/o type hint (CT)
 *   - incompatible type hint (CT if no RTT hints used, RT for RTT hints)
 *   - deserialization method not found (CT)
 *   - deserialization method threw an exception (CT if no RTT hints used, RT for RTT hints)
 *   - class not found (CT)
 *   - RTT not a class (RT)
 * - Reifier
 *   - attempting to reify a non-interface (@ creation time)
 *   - interface cannot be reified (@ creation time)
 * - Stub
 *   - Blueprint internal error (@ creation time)
 *   - invocation failed: configuration key missing, deserialization failed (@ creation time & runtime)
 *     => IOException
 *
 * @author Zoran Rilak
 */
public class BlueprintException extends RuntimeException {

  public BlueprintException(String message) {
    super(decorateException(message));
  }

  public BlueprintException(Throwable cause) {
    super(cause);
  }

  public BlueprintException(String message, Throwable cause) {
    super(decorateException(message), cause);
  }

  /* Privates ------------------------------------------------------- */

  private static String decorateException(String message) {
    LinkedList<String> argStr = new LinkedList<String>();
    Context ctxt = Context.getThreadInstance();
    for (Object o : ctxt.getArgClasses()) {
      argStr.add(o.toString());
    }
    return message +
            " (in method " + ctxt.getMethod().getName()
            + "(" + Strings.join(", ", argStr) + ")"
            + ", for class " + ctxt.getIface().getCanonicalName()
            + ", on config path '/" + Strings.join("/", ctxt.getCfgPath()) + "'"
            + ")";
  }

}
