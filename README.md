Blueprint: Type-Safe Configurations for Java
=

Blueprint is a simple framework that makes working with configuration files more enjoyable.

The Problem
-

Configuration files are a common mainstay of many Java applications.  Some standard vessels in which configuration is held include:

  - Java Properties format;
  - XML;
  - Windows-style INI files;
  - JSON;
  - etc.

All of these and many other formats are generally well-suited for a structured representation of key-value pairs where keys are paths composed of strings, and values are of whatever type our application expects them to be.  However, the common problem you encounter no matter which file format and parsing library you choose is that the values read from the configuration come as plain String objects.  Consider the following code one might write in order to read an integer from a configuration file:

    Properties props = new Properties();
    props.load(new FileReader("conf/server.properties"));

    ...

    Integer i;
    try {
      String str = props.getProperty("http.connection.readTimeout");
      if (str == null || str.isEmpty()) {
        // throw exception or use default value
      }
    } catch (NumberFormatException e) {
      // throw exception or use default value
    }

That's a lot of boilerplate to read a single integer!  Thankfully, there are good and solid libraries out there that provide means of type conversion from serialized string values into whatever type we might need.  Apache's solid Configuration library does it this way:

    Configuration cfg = new PropertiesConfiguration("conf/server.properties");

    ...

    try {
      cfg.getInteger("http.connection.readTimeout", 30);  // use 30 as the default value
    } catch (ConversionException e) {
      // throw exception or use default value
    }

This is, however, still sub-optimal for several reasons:

  - we still have to `try... catch` if we're at all unsure that the value stored under the desired key will parse as an integer;
  - key names remain hard-coded throughout our classes;
  - there is no way to know that the entire configuration is valid until we manually access all the keys.

Careful programmers will usually alleviate these problems by localizing them in a specialized DTO class as a placeholder for all the configuration values.  If the DTO object fails to build from a specified configuration source, then the rest of the application does not have to begin initializing.

The Solution
-

Blueprint converts your configuration sources from key-value trees into concrete DTO objects, performing validation along the way.  You write an interface that describes what the configuration source can or must contain in order to be valid; for example:

    import javax.validation.constraints.*;

    interface ServerConfiguration {
      @NotNull URL proxyAddress();
      @NotNull File tmpDirectory();
      HTTP http();

      interface HTTP {
        int port();
        Connection connection();

        interface Connection {
          int connectTimeout();
          int readTimeout();
        }
      }
    }

You then give this interface to Blueprint along with the appropriate configuration source:

    ServerConfiguration cfg = Blueprint.create(
        ServerConfiguration.class,
        new ApacheTree(new PropertiesConfiguration("conf/server.properties")));

...and receive an object whose methods will spew out objects parsed from the configuration source:

    cfg.proxyAddress();                     // => a java.net.URL object parsed from the "proxyAddress" key
    cfg.tmpDirectory();                     // => a java.io.File object parsed from the "tmpDirectory" key
    cfg.http().port();                      // => an int primitive parsed from the "http.port" key
    cfg.http().connection().readTimeout();  // => an int primitive parsed from the "http.connection.readTimeout"

The DTO is validated against any `javax.validation.constraints.*` annotations placed on the methods in the `ServerConfiguration` interface.

...
-
This has been just a quick introduction/rationale.  For more information, please refer to Blueprint tests in the source tree and of course to all the JavaDoc you can find. :)

