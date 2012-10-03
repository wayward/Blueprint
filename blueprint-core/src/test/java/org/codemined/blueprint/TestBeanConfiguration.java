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

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Zoran Rilak
 */
public interface TestBeanConfiguration {
  String getServiceName();
  boolean isActive();
  int getTimeout();
  File getTempDir();
  URL getDeployUrl();
  <T> List<T> getBackupHours(Class<T> elementType);
  @UseType(Integer.class)
  List<Integer> getBackupHours();
  <T> ArrayList<T> getActiveBackupDays(Class<T> elementType);
  boolean[] getActiveBackupDays();
  <V> Map<String, V> http(Class<V> valueType);
  @UseType(String.class)
  Map<String, String> http();
  _DB db(); interface _DB {
    <T> Class<T> getImpl();
    <T> T getImpl(Class<?> typeHint);
    _Database development();
    _Database production();

    interface _Database {
      String getName();
      boolean isTemporary();
    }
  }
  @UseType(_Protocol.class)
  Map<String, _Protocol> protocols(); interface _Protocol {
    String getName();
    int getPort();
  }
  <T> T getState(Class<T> typeHint); enum _State {
    TRUE,
    FALSE,
    SCHRÖDINGER
  }
}
