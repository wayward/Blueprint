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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * @author Zoran Rilak
 */
@Test
public class BlueprintExceptionTest {

  @Test
  public void message() {
    BlueprintException e = new BlueprintException("message");
    assertEquals(e.getMessage(), "message");
    assertNull(e.getCause());
  }

  @Test
  public void cause() {
    RuntimeException e1 = new RuntimeException();
    BlueprintException e = new BlueprintException(e1);
    assertEquals(e.getMessage(), e1.toString());
    assertEquals(e.getCause(), e1);
  }

  @Test
  public void messageAndCause() {
    // Testing against standard RuntimeException behavior as per Java spec.

    RuntimeException e1 = new RuntimeException();
    BlueprintException e = new BlueprintException("message", e1);
    assertEquals(e.getMessage(), "message");
    assertEquals(e.getCause(), e1);

    e = new BlueprintException(null, e1);
    assertNull(e.getMessage());
    assertEquals(e.getCause(), e1);
  }

}
