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

package org.codemined.blueprint.source;

import org.codemined.blueprint.Path;

import java.util.Set;

/**
 * Provides structured access to the set of values contained in a configuration source.
 *
 * <p>Source offers a view of the underlying data as an N-ary tree where each node can contain:
 * <ul>
 *   <li>a string value;</li>
 *   <li>a map of named sub-nodes;</li>
 *   <li>a map of named attribute nodes;</li>
 *   <li>an ordered list of sub-nodes.</li>
 * </ul>
 *
 *
 * <p>Implementations of this interface perform queries on the specific input formats,
 * by defining their own rules of translation between the original format and the
 * tree representation given above.
 * Since it is up to the implementation to perform the actual source-to-tree mapping,
 * an implementation is free to reject certain paths as inappropriate for querying.
 * For example, an implementation that wraps a Java Properties file might decide
 * to support list and array queries by treating commas in values as list separators.
 * Such an implementation would only accept paths where the last segment is a list index,
 * but reject paths in which an index appears between anywhere else.
 * An XML source, on the other hand, would likely not impose such a restriction because
 * because XML is naturally hierarchical and can contain arrays and attributes
 * interspersed with string values.
 *
 * @author Zoran Rilak
 * @version 0.1
 * @since 0.1
 */
public interface Source {

  /**
   * Checks if the given path makes sense for this source implementation.
   *
   * @param path path to check
   * @return true if the path is valid
   */
  boolean isValid(Path path);

  /**
   * Checks if the given path exists in the source.
   *
   * @param path path to check
   * @return true if the path exists
   */
  boolean containsPath(Path path);

  /**
   * Returns the set of sub-key names stored in the node.
   *
   * @param path  path to query
   * @return a (possibly empty) set, or null if no such node exists
   */
  Set<String> getSubKeys(Path path);

  /**
   * Returns the plain string value stored in the node.
   *
   * @param path path to query
   * @return a (possibly empty) string, or null if no such node exists
   */
  String getStringValue(Path path);

  /**
   * Returns the size of the ordered list stored in the node.
   *
   * @param path path to query
   * @return size of the ordered list on path, or null if no such node exists
   */
  Integer getListSize(Path path);

  /**
   * Reloads the source.
   *
   */
  void reload();

}
