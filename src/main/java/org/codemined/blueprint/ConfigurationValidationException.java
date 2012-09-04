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

import java.util.List;

/**
 * Thrown to indicate that the configuration does not meet validate
 * against the constraints specified on the methods of the interface.
 *
 * @author Zoran Rilak
 */
public class ConfigurationValidationException extends Exception {
  private final List<String> failedValidations;

  public ConfigurationValidationException(List<String> failedValidations) {
    super();
    this.failedValidations = failedValidations;
  }


  public List<String> getFailedValidations() {
    return failedValidations;
  }

}
