package org.codemined.blueprint;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Zoran Rilak
 * @version 0.1
 * @since 0.1
 */
public class CompositeTree extends ConfigTree {

  private List<ConfigTree<?>> trees;


  public CompositeTree() {
    this.trees = new CopyOnWriteArrayList<ConfigTree<?>>();
  }

  public void add(ConfigTree tree) {
    this.trees.add(tree);
  }

  public CompositeTree with(ConfigTree tree) {
    add(tree);
    return this;
  }

  @Override
  public String getValue() {
    String value = null;
    for (ConfigTree t : trees) {
      value = t.getValue();
      if (value != null) {
        break;
      }
    }
    return value;
  }

  @Override
  public List<? extends ConfigTree> getList() {
    List<? extends ConfigTree<?>> list = Collections.emptyList();
    for (ConfigTree<?> t : trees) {
      list = t.getList();
      if (! list.isEmpty()) {
        break;
      }
    }
    return list;
  }

  @Override
  public boolean containsTree(String key) {
    for (ConfigTree<?> t : trees) {
      if (t.containsTree(key)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public ConfigTree<?> getTree(String key) {
    ConfigTree<?> tree = null;
    for (ConfigTree<?> t : trees) {
      tree = t.getTree(key);
      if (tree != null) {
        break;
      }
    }
    return tree;
  }

  @Override
  public Set<String> keySet() {
    Set<String> keySet = new HashSet<String>();
    for (ConfigTree<?> t : trees) {
      keySet.addAll(t.keySet());
    }
    return keySet;
  }

}
