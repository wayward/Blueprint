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

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Zoran Rilak
 */
public interface TestConfiguration {

  /* When creating objects to return as parsed configuration values,
   * Blueprint examines the return type for static factory methods to try and
   * convert the string representation of a value into its expected type.
   * These static methods are: valueOf(String), parse(String), fromString(String)
   * and static deserialize(String).  If no such method is found, Blueprint will
   * also try using a single-argument constructor taking a single String.
   */
  String serviceName();

  /* Methods that return primitive types are handled the same way as those
   * returning objects (see serviceName() above), but the primitive type will
   * be replaced with its boxed counterpart.
   */
  boolean isActive();

  /* Blueprint can validate methods against any javax.validation.constraints.*
   * annotations at the time the instance of your interface is created.
   * If a return value of any such method fails to validate, a BlueprintException
   * will be thrown to indicate that a valid blueprint cannot be created with the
   * given combination of interface and configuration source.
   */
  @Min(0)
  @Max(60)
  int timeout();

  /* Configuration values can be made mandatory with @NotNull.
   * If a method has not been annotated with @NotNull and the configuration does
   * not contain a matching key-value pair, that method will simply return null.
   * If the annotation is present, however, a BlueprintException will be thrown
   * at creation time, much the same as above.
   */
  @NotNull
  File tempDir();

  /* URLs and URIs deserialize quite nicely.
   */
  URL deployUrl();

  /* Abstract collection types can be deserialized semi-automatically with the
   * help of "type hinting".  Blueprint can instantiate the appropriate classes
   * for most collection interfaces, but since generic parameters can only be
   * inferred from concrete classes, we must provide additional type information
   * to deserialize the actual elements.  One way to pass the actual class to use
   * when deserializing is by passing a single class as the element type hint
   * at runtime.
   */
  <T> List<T> backupHours(Class<T> elementType);

  /* If including a runtime argument is not feasible nor desired, type hinting
   * can also be done by annotating the method with @UseType().  Type hint provided
   * in the annotation must be assignment-compatible with the method's return type.
   */
  @UseType(Integer.class)
  List<Integer> backupHours();

  /* Concrete collection types can also be used.
   */
  @UseType(Boolean.class)
  ArrayList activeBackupDays();

  /* Maps, like collections, are read by type-hinting the desired element type,
   * either at runtime or by annotating the method with @UseType().
   */
  <V> Map<String, V> http(Class<V> valueType);

  @UseType(String.class)
  Map<String, String> http();

  /* Hierarchical configurations are specified by having a method with a return type
   * which is neither a collection nor a map.  That type is reified using the same
   * mechanism as for the main interface itself, so you can nest arbitrarily complex
   * objects inside your interface and have the corresponding keys accessible through
   * simple method chaining.
   */
  _DB db();

  interface _DB {

    /* Class names are resolved to the actual class objects by the class loader
     * responsible for loading the blueprint interface itself.
     */
    <T> Class<T> impl();

    <T> T impl(Class<?> typeHint);

    _Database development();

    _Database production();

    interface _Database {

      String name();

      boolean isTemporary();
    }
  }


  /* We can also deserialize configuration maps containing non-primitive objects.
   */
  @UseType(_Protocol.class)
  Map<String, _Protocol> protocols();

  interface _Protocol {

    String name();

    int port();
  }


  /* Sometimes we want to be flexible and have access to a configuration item
   * in two or more "type flavors".  For example, if we have a configuration key
   * such as "remote.base_url = http://foo:23/", is by its intended nature a URI,
   * but we might also want to use it as a String, as stored in the configuration.
   * We can always call URI#toString(), but there are situations when this conversion
   * is "lossy" (i.e. parsing "000.001" to Double and back), or when we don't need
   * a String at all but something entirely different.
   * As long as the types we need are those that Blueprint knows how to deserialize
   * from their common string representation, we can use runtime type hinting to
   * achieve the same effect while hiding all the boilerplate code.
   *
   * As an example, we can obtain the value of ``_state'' in three different types:
   *   String str   = cfg._state(String.class)   => String: "TRUE"
   *   boolean bool = cfg._state(Boolean.class)  => boolean: true
   *   _State state = cfg._State(_State.class)   => enum: _State.TRUE
   *
   * Note, though, that methods using runtime type hinting cannot be pre-validated,
   * hence using them may result in deserialization errors at the time of invocation.
   * If that is the case, a BlueprintException will be thrown.  This is a compromise
   * between strict type checking and flexibility.
   * Of course, if you don't use runtime type hints, no exceptions will be thrown.
   */
  <T> T state(Class<T> typeHint);

  enum _State {
    TRUE,
    FALSE,
    SCHRÖDINGER
  }


  /* Concrete return type, type hint annotation and runtime type hint can all be present
   * simultaneously on a method.  Annotation takes precedence over the return type,
   * and a runtime type hint trumps both.  Note, though, that no matter which type is
   * determined to be the final required type for deserialization, it still has to be a
   * subclass of the declaring method's return type.
   */
  A typeHintDemo1(Class<? extends A>... typeHint);

  @UseType(A1.class)
  A typeHintDemo2(Class<? extends A>... typeHint);

  boolean key1();

  @Key("key2")
  boolean keyTwo();

}
