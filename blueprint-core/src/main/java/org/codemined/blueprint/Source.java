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

import java.io.InputStream;
import java.util.regex.Pattern;

/**
 * @author Zoran Rilak
 * @version 0.1
 * @since 0.1
 */
public class Source {

  public interface Format {
    ConfigNode<?> load(InputStream in);
  }

  public enum Formats implements Format {
    XML (".xml", "org.codemined.blueprint.impl.ApacheTree", "blueprint-apache"),
    PROPERTIES (".properties", "org.codemined.blueprint.impl.ApacheTree", "blueprint-apache"),
    JSON (".json", "org.codemined.blueprint.impl.JsonTree", "blueprint-jackson-json");

    private String suffix;
    private Pattern suffixPattern;
    private String moduleName;
    private Class<? extends ConfigNode> nodeClass;

    private Formats(String suffix, String className, String moduleName) {
      this.suffix = suffix;
      this.suffixPattern = Pattern.compile(Pattern.quote(suffix) + "$", Pattern.CASE_INSENSITIVE);
      this.moduleName = moduleName;
      // try to load the class responsible for handling files of this format
      try {
        this.nodeClass = Class.forName(className).asSubclass(ConfigNode.class);
      } catch (ClassNotFoundException e) {
        this.nodeClass = null;  /* no implementation found in classpath */
      } catch (ClassCastException e) {
        throw new RuntimeException(String.format(
                "Wrong or outdated implementation for %s found in class path." +
                        "Please ensure that you are using the latest version of %s.",
                className, moduleName));
      }
    }

    @Override
    public ConfigNode<?> load(InputStream in) {
      if (nodeClass == null) {
        throw new RuntimeException(String.format(
                "The class responsible for handling %s files was not found in class path. " +
                        "Make sure you're using the latest version of %s.",
                suffix, moduleName));
      }

      try {
        ConfigNode<?> node = nodeClass.newInstance();
        load(in);
        return node;
      } catch (InstantiationException e) {
        throw new RuntimeException(e);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }

    public boolean matches(String fileName) {
      return suffixPattern.matcher(fileName).matches();
    }

  }

}
