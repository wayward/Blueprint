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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Zoran Rilak
 */
public class Path implements Iterable<Path.Component> {

  /* Root path; contains no components. */
  public static final Path ROOT = new Path();

  /*
    Blueprint interface query
      - generates a Path using KeyResolver(s)
        - calls sourceImpl.lookupValue(Path)
          - sourceImpl resolves the Path and returns a value

    Blueprint interface query with a @Path(...) annotation
      - parses a Path from its ... representation using KeyResolver(s)
        - calls sourceImpl.lookupValue(Path)
          - sourceImpl resolves the Path and returns a value
   */


  /*
    - Sources map onto Blueprint interfaces.
    - The particular mode of mapping is configured in the Source itself, not in the interface;
      this is because Sources are heterogeneous by their very nature (some configuration formats
      support lists and arrays, some don't, etc.) so the details of how a given Path is to be
      interpreted belong there.  Example:
    - XML has nodes, attributes and lists: <foo><bar name="Bar!"> <baz>1</baz> <baz>2</baz> </bar></foo>
                 lists can also be parsed: <foo><bar name="Bar!"><baz>1,2,3</bar></foo>
      How lists are read from a given XML file depends on the actual XML Source behavior.
    - On XML attributes: given a Path, an XML Source may provide
      - value (tag content, CDATA);
      - array (parsed tag content / all tags matching path)
      - key-value map of attribute names and values.
   */
  public static class Component {
  }

  public static class NodeComponent extends Component {
    private final String name;

    public NodeComponent(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  public static class IndexComponent extends Component {
    private final int index;

    public IndexComponent(int index) {
      this.index = index;
    }

    public int getIndex() {
      return index;
    }
  }


  private List<Component> components;

  private String str;


  public Path() {
    components = new ArrayList<Component>();
  }

  Path(List<Component> components, Component c) {
    this.components = new ArrayList<Component>(components);
    this.components.add(c);
  }

  public Path _(String key) {
    return toNode(key);
  }

  public int size() {
    return components.size();
  }

  public Path toNode(String key) {
    return new Path(components, new NodeComponent(key));
  }

  public Path toIndex(int index) {
    return new Path(components, new IndexComponent(index));
  }

  public Iterator<Component> iterator() {
    return components.iterator();
  }

  @Override
  public String toString() {
    if (str == null) {
      str = toString("/");
    }
    return str;
  }

  public String toString(String keySeparator) {
    StringBuilder sb = new StringBuilder();
    if (! components.isEmpty()) {
      boolean firstNode = true;
      for (Component c : components) {
        if (c instanceof NodeComponent) {
          sb.append(((NodeComponent) c).getName());
          if (firstNode) {
            firstNode = false;
          } else {
            sb.append(keySeparator);
          }
        } else if (c instanceof IndexComponent) {
          sb.append('[').append(((IndexComponent) c).getIndex()).append(']');
        } else {
          throw new RuntimeException("Unknown component type: " + c.getClass());
        }
      }
    }
    return sb.toString();
  }

  /* Privates ------------------------------------------------------- */

  private void append(Component c) {
    components.add(c);
  }

}

