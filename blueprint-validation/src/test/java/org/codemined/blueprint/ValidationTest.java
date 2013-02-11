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

import org.testng.annotations.Test;

/**
 * @author Zoran Rilak
 */
@Test
public class ValidationTest {

  @Test
  public void validates()
          throws ConfigurationValidationException {
    ValidatedBlueprint.create(ValidatingConfiguration.class, createMockSource());
  }

  @Test(expectedExceptions = ConfigurationValidationException.class)
  public void rejectsOutOfRange()
          throws ConfigurationValidationException {
    MockSource ms = createMockSource();
    ms.getRootNode().getChild("hours").setValue("25");
    ValidatedBlueprint.create(ValidatingConfiguration.class, ms);
  }

  /* Privates ------------------------------------------------------- */


  private MockSource createMockSource() {
    MockSource s = new MockSource();
    s.getRootNode()
            .withChild("hours", "4")
            .withChild("dayOfWeek", "Thursday")
            .withChild("birthday", "23/11/1878")
            .withChild("names", new Node()
                    .withArrayItems("Felipe", "Cayetano", "Lopez", "Martinez", "Gonzales"))
            .withChild("eMail", "chico@darkwood.net");
    return s;
  }

}
