Blueprint: Type-Safe Configurations for Java
=

Blueprint is a simple framework that makes reading configuration files more enjoyable.

- The code resides [on GitHub](https://github.com/wayward/Blueprint).
- There is also a small but attentive [mailing list](http://groups.google.com/group/blueprint-configuration).

Features
-
- automatic conversion to the desired type from strings in the configuration
- deserialization types can be selected at runtime
- hierarchical configurations can be accessed using the familiar
`foo().bar().baz()` syntax
- optional validation of the configuration object

The Problem of Reading Configuration Files
-

Configuration files are a common mainstay of many Java applications.
Some standard vessels in which configuration is held include:

- Java Properties format;
- XML;
- Windows-style INI files;
- JSON;
- &hellip;

The majority of approaches to storing configuration are viewing it as a tree structure
where each path may map onto a value.  One important implementational detail when dealing
with configurations is that their primary requisite, especially during early stages of
software development, is for them to be easily modifiable by external applications, e.g. text
editors. This means that the natural format for a configuration is that of a plain text file
with little or no type information associated with the values.  This is especially true
for some more popular configuration formats, like Java Properties, Windows INI files,
CSV files, etc.

Certain formats _do_ provide type information, like XML DTD/Schema, JSON, YAML and a few
others, but using those in the Java world usually means that you have to pull in some heavy
machinery in order to parse a relatively simple, mostly "flat" configuration file.  Even so, once
you have your configuration loaded in memory, you still have to do most of the type conversions
between whatever intermediary format the library uses and your desired value types by hand.

Consider the following code one might write in order to read an integer from a Properties file:

    Properties props = new Properties();
    props.load(new FileReader("conf/server.properties"));

// ...

    Integer i;
    try {
      String str = props.getProperty("http.connection.readTimeout");
      if (str == null || str.isEmpty()) {
        // throw exception or use default value
      }
    } catch (NumberFormatException e) {
      // throw exception or use default value
    }

That's a lot of boilerplate to read a single integer!  Thankfully, there are good and solid libraries out there
that provide means of type conversion from serialized string values into whatever type we might need.
[Apache Configuration library](http://commons.apache.org/configuration/) does it this way:

    Configuration cfg = new PropertiesConfiguration("conf/server.properties");
    
    // ...
    
    cfg.getInteger("http.connection.readTimeout", 30);  // use 30 as the default value

This is, however, still suboptimal for two reasons:

- key names remain hard-coded throughout our classes;
- there is no way to know whether the entire configuration is valid or not until we've manually accessed all the keys.

Careful programmers will usually alleviate these problems by localizing configuration reads inside a
specialized DTO class usually serving both as a converter and as a container for the values obtained
through calls like the one above.  If the DTO object fails to build from a specified configuration
source, then the rest of the application does not have to begin initializing.

What Blueprint Does Instead
-

Blueprint converts your configuration sources from key-value trees of strings into concrete, type-safe
method calls, performing validation along the way.  You provide a _blueprint interface_ that drives the
conversion from plain strings into Java types and describes what the configuration source may or must
contain in order to be considered valid.  For example:

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

We then give this interface to Blueprint along with the appropriate configuration source and receive an
object that does all the necessary conversions for us:

    ServerConfiguration cfg = Blueprint.createBlueprint(ServerConfiguration.class,
      new ApacheConfigurationSource(new PropertiesConfiguration("conf/server.properties")));

    cfg.proxyAddress();                     // => a java.net.URL object parsed from the "proxyAddress" key
    cfg.tmpDirectory();                     // => a java.io.File object parsed from the "tmpDirectory" key
    cfg.http().port();                      // => an int primitive parsed from the "http.port" key
    cfg.http().connection().readTimeout();  // => an int primitive parsed from the "http.connection.readTimeout"

This DTO is validated against any `javax.validation.constraints.*` annotations present on the methods
in the `ServerConfiguration` interface.

...
-
This has been just a quick introduction/rationale.  For more information, please refer to tests in the
source tree and of course to all the JavaDoc you can find.

Community and Participation
-
Contributions big and small are welcome!  If you want to know what's cooking, post a question or demand an
apology for breaking your production environment, please join the
[mailing list](http://groups.google.com/group/blueprint-configuration).  Bug reports and feature requests
are encouraged; we have a tracker on our [GitHub project page](https://github.com/wayward/Blueprint).
You have a patch for Blueprint?  You're the best person on the planet!
[Fork Blueprint](https://github.com/wayward/Blueprint/fork), fix stuff and send us a pull request on GitHub.
We can't offer you money but we will think you are a really hoopy frood. :)
