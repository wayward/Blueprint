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

import org.codemined.blueprint.impl.CamelCaseResolver;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Zoran Rilak
 */
@Test
public class BuilderTest {

  @Test
  void createsBuilder() {
    Blueprint.Builder b = Blueprint.of(TestInterface.class);
    assertNotNull(b);
  }

  @Test(dependsOnMethods = "createsBuilder")
  void createsBuilderFromTree() {
    TestInterface i = Blueprint.of(TestInterface.class)
            .from(new TestTree().withTestDefaults())
            .build();
    assertEquals(i.serviceName(), "DummyService");
  }

  @Test(dependsOnMethods = "createsBuilderFromTree")
  void createsBlueprintWithKeyResolver() {
    TestTree t = new TestTree().withTestDefaults();
    t.put("service_name", "DummyServiceUnderscored");
    TestInterface i = Blueprint.of(TestInterface.class)
            .from(t)
            .withKeyResolver(new CamelCaseResolver())
            .build();
    assertEquals(i.serviceName(), "DummyServiceUnderscored");
  }

}
