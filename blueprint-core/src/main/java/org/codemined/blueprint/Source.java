package org.codemined.blueprint;

/**
 * @author Zoran Rilak
 * @version 0.1
 * @since 0.1
 */
public class Source {

  public interface Format {
    ConfigTree<?> wrap(String fileName);
  }

  public enum Formats implements Format {
    XML (".xml", "org.codemined.blueprint.impl.ApacheTree", "blueprint-apache"),
    PROPERTIES (".properties", "org.codemined.blueprint.impl.ApacheTree", "blueprint-apache"),
    JSON (".json", "org.codemined.blueprint.impl.JsonTree", "blueprint-jackson-json");

    private String fileSuffix;
    private String providingModuleName;
    private Class<? extends ConfigTree> treeClass;

    private Formats(String fileSuffix, String className, String providingModuleName) {
      this.fileSuffix = fileSuffix;
      this.providingModuleName = providingModuleName;

      // try to load the class responsible for handling files of this format
      try {
        this.treeClass = Class.forName(className).asSubclass(ConfigTree.class);
      } catch (ClassNotFoundException e) {
        this.treeClass = null;  /* no implementation found in classpath */
      } catch (ClassCastException e) {
        throw new RuntimeException(String.format(
                "Wrong or outdated implementation for %s found in class path." +
                        "Please ensure that you are using the latest version of %s.",
                className, providingModuleName));
      }
    }

    @Override
    public ConfigTree<?> wrap(String fileName) {
      if (treeClass == null) {
        throw new RuntimeException(String.format(
                "The class responsible for handling %s files, was not found in class path." +
                        "Please ensure that you are using the latest version of %s.",
                fileSuffix, providingModuleName));
      }

    }
  }

}
