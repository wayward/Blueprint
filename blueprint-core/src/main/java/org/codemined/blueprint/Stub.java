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

/* Stub(Interface, ConfigurationSource) or
   Stub(Interface, Prefix)
 ================================================================
 - Interface: class that this Stub will be proxying for
 - ConfigurationSource: source to query for configuration key-value pairs
 - RootPath: ConfigurationSource-specific "root path" for this stub to map onto
 - Stub implements some common behaviors and caches/generates the rest.
 - Stub caching strategy might be configurable if we allow the configurations to change
   (but keep validations in mind: do we re-run them, and if so, when?) */

import org.codemined.util.Path;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Binds values from the configuration to the given interface's methods. 
 *
 * <ul>
 *   <li>Implements common fluff from Object(): toString(), equals(), hashCode(), ...
 *   <li> Scans Interface for contained interfaces and methods
 *   <li>Sub-interfaces are processed by creating and caching proxy classes
 *   <li>Methods are processed thus:
 *     <ul>
 *       <li>Simple return type (Integer, Boolean...) is passed on to the deserializer
 *       <li>Sub-interface return type creates/reuses a child instance of BlueprintStub
 *       <li>Collection return type
 *     </ul>
 *   <li>Weak references will be used for caching large values, especially if we allow
 *       for polymorphic deserialization: {@code <T> T url(Class<T> deserializeAs)}.
 * </ul>
 *
 * <p>
 *   Two additional methods are provided:
 *   <ul>
 *     <li>$value(): returns the deserialized value from the root configuration key;</li>
 *     <li>$asMap(): returns a map of all sub-keys and their deserialized values.</li>
 *   </ul>
 * </p>
 *
 * @param <I> the interface type to proxy
 * 
 * @author Zoran Rilak
 * @version 0.1
 * @since 0.1
 */
class Stub<I> implements InvocationHandler {

  private final Class<I> iface;

  private final ConfigNode cfg;

  private final Path<String> cfgPath;

  private final Deserializer deserializer;

  private final KeyResolver keyResolver;

  private final Map<MethodInvocation, Object> cache;

  private final I proxy;


  public Stub(Class<I> iface,
              ConfigNode cfg,
              Path<String> configPath,
              Deserializer deserializer,
              KeyResolver keyResolver) {
    if (cfg == null) {
      throw new NullPointerException();
    }
    this.iface = iface;
    this.cfg = cfg;
    this.cfgPath = configPath;
    this.deserializer = deserializer;
    this.keyResolver = keyResolver;
    this.cache = Collections.synchronizedMap(new HashMap<MethodInvocation, Object>());
    this.proxy = createProxy();
  }


  public I getProxy() {
    return proxy;
  }


  /* Methods from InvocationHandler --------------------------------- */

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    final String key = getKeyFor(method);

    // mark the context in which we're going to execute
    Context.getThreadInstance().setContext(method, args, iface, cfgPath.to(key));

    // route methods not declared on the blueprint interface to self
    if (method.getDeclaringClass() == Object.class) {
      return method.invoke(this, args);
    }

    // Values are cached by (method, args) pairs to support runtime type hints.
    MethodInvocation invocation = new MethodInvocation(method, args);
    Object o = cache.get(invocation);
    if (o == null) {
      // Get the tree whose value will be passed to the deserializer.
      // For special methods ($value, $asMap), use the tree already associated
      // with this stub instead of looking up children trees.
      ConfigNode t;
      if (key == null) {
        t = cfg;
      } else {
        t = cfg.getNode(key);
      }
      if (t == null) {
        throw new BlueprintException("Configuration key '" + key +
                "' does not exist on path " + cfgPath);
      }

      o = deserializer.deserialize(invocation.getReturnType(), invocation.getHintedType(), key, t);
      cache.put(invocation, o);
    }
    return o;
  }


  /* Methods from Object -------------------------------------------- */


  @Override
  public boolean equals(Object o) {
    return proxy == o;
  }


  @Override
  public int hashCode() {
    int result = iface != null ? iface.hashCode() : 0;
    result = 31 * result + (deserializer != null ? deserializer.hashCode() : 0);
    return result;
  }


  @Override
  public String toString() {
    return "[" + iface.getName() + " blueprint]";
  }


  /* Privates ------------------------------------------------------- */


  private I createProxy() {
    try {
      return iface.cast(Proxy.newProxyInstance(
              iface.getClassLoader(), new Class[]{ iface, BlueprintProxy.class }, this));
    } catch (ClassCastException e) {
      throw new BlueprintException("Error casting proxy instance to " + iface.getName(), e);
    } catch (IllegalArgumentException e) {
      throw new BlueprintException("Error creating proxy instance for " + iface.getName(), e);
    }
  }


  private String getKeyFor(Method method) {
    /* handle special methods */
    //if ("$value".equals(method.getName())) {
    //  return null;
    //}
    /* check for our Key annotation first */
    Key keyAnn = method.getAnnotation(Key.class);
    if (keyAnn != null) {
      return keyAnn.value();
    }
    /* no overrides -- return the method's name */
    return keyResolver.resolve(method.getName());
  }

}

