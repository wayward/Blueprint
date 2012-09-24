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

import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

/**
 * @author Zoran Rilak
 */
public interface ValidatingConfiguration {

  /* Blueprint can validate methods against any javax.validation.constraints.*
   * annotations at the time the instance of your interface is created.
   * If a return value of any such method fails to validate, a BlueprintException
   * will be thrown to indicate that a valid blueprint cannot be created with the
   * given combination of interface and configuration source.
   */
  @Min(0)
  @Max(23)
  int hours();

  /* Configuration values can be made mandatory with @NotNull.
   * If a method has not been annotated with @NotNull and the configuration does
   * not contain a matching key-value pair, that method will simply return null.
   * If the annotation is present, however, a BlueprintException will be thrown
   * at creation time, much the same as above.
   */
  @NotNull
  String dayOfWeek();

  @Past
  Date birthday();

  @UseType(String.class)
  @Size(min=1, max=6)
  List<String> names();

  @Pattern(regexp = "[\\w.]+@[\\w.]([\\w.])+")
  String eMail();

}
