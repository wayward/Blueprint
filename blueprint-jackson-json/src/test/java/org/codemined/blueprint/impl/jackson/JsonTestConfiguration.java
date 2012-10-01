package org.codemined.blueprint.impl.jackson;/*
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

/**
 * @author Zoran Rilak
 */
public interface JsonTestConfiguration {
  public String name();
  public int age();
  public Family family(); interface Family {
    public String mother();
    public String father();
    public String sister();
    public String uncle();
    public String[] nephews();
    public boolean married();
  }
}

