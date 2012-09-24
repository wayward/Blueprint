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

import org.codemined.util.InMemoryTree;
import org.codemined.util.Tree;
import org.testng.annotations.Test;

/**
 * @author Zoran Rilak
 */
public class ValidationTest {

  @Test
  public void validates()
          throws ConfigurationValidationException {
    Blueprint.create(ValidatingConfiguration.class, createTree());
  }

  @Test(expectedExceptions = ConfigurationValidationException.class)
  public void rejectsValuesOutOfRange()
          throws ConfigurationValidationException {
    Tree<String, String> cfg = createTree();
    cfg.get("hours").setValue("24");
    Blueprint.create(ValidatingConfiguration.class, cfg);
  }

  /* Privates ------------------------------------------------------- */


  private Tree<String, String> createTree() {
    InMemoryTree<String, String> t = new InMemoryTree<String, String>();
    t.put("hours", "4");
    t.put("dayOfWeek", "Thursday");
    t.put("birthday", "23/11/1878");
    t.put("names", "Felipe Cayetano Lopez Martinez y Gonzales");
    t.put("eMail", "chico@darkwood.net");
    return t;
  }

}
