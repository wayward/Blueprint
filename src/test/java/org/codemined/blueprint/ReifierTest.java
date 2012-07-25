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

import org.codemined.util.Tree;
import org.testng.annotations.Test;

import java.util.AbstractList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * @author Zoran Rilak
 */
@Test
public class ReifierTest {


  public void wontReifyClasses() {
    try {
      Reifier.reifyCollection(Class.class);
      fail();
    } catch (BlueprintException ignored) {}
    try {
      Reifier.reifyCollection(AbstractList.class);
      fail();
    } catch (BlueprintException ignored) {}
  }

  public void reifiesListInterface() {
    assertTrue(Reifier.reifyCollection(List.class) instanceof List);
  }

  public void reifiesSetInterface() {
    assertTrue(Reifier.reifyCollection(Set.class) instanceof Set);
  }

  @Test(expectedExceptions = BlueprintException.class)
  public void failsForUnknownTypes() {
    Reifier.reifyCollection(Tree.class);
  }

  public void reifiesStringMap() {
    Map<String, Integer> map = Reifier.reifyStringMap();
    map.put("string", 0);
  }

}
