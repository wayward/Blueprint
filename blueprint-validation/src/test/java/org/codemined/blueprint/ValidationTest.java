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

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * @author Zoran Rilak
 */
@Test
public class ValidationTest {

  private static Validator validator;

  @BeforeClass
  public static void setup() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  public void validates()
          throws ConstraintViolationException {
    ValidatedBlueprint.create(ValidatedConfiguration.class, createTree());
  }

  @Test(expectedExceptions = ConstraintViolationException.class)
  public void rejectsOutOfRange()
          throws ConstraintViolationException {
    TestTree cfg = createTree();
    cfg.getChildNode("hours").setValue("25");
    ValidatedBlueprint.create(ValidatedConfiguration.class, cfg);
  }

  /* Privates ------------------------------------------------------- */


  private TestTree createTree() {
    TestTree t = new TestTree();
    t.put("hours", "4");
    t.put("dayOfWeek", "Thursday");
    t.put("birthday", "23/11/1878");
    t.put("names", null).setList("Felipe", "Cayetano", "Lopez", "Martinez", "Gonzales");
    t.put("eMail", "chico@darkwood.net");
    return t;
  }

}
