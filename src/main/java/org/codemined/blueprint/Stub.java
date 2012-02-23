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
       (but keep validations in mind: do we re-run them, and if so, when?)

   */

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
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
 * @param <I> the interface type to proxy
 * 
 * @author Zoran Rilak
 * @version 0.1
 * @since 0.1
 */
class Stub<I> implements InvocationHandler {
  private final Class<I> iface;
  private final Deserializer deserializer;
  private final Map<MethodInvocation, Object> cache;
  private final I proxy;


  public Stub(Class<I> iface, Deserializer deserializer) {
    this.iface = iface;
    this.deserializer = deserializer;
    this.cache = Collections.synchronizedMap(new HashMap<MethodInvocation, Object>());
    this.proxy = createProxy();
  }

  public I getProxy() {
    return proxy;
  }

  /* Methods from InvocationHandler --------------------------------- */

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // route methods not declared on the blueprint interface to self
    if (method.getDeclaringClass() == Object.class) {
      return method.invoke(this, args);
    }

    // Values are cached by (method, args) pairs to support type hinting in arguments.
    final String key = getKeyFor(method);
    try {
      MethodInvocation invocation = new MethodInvocation(method, args);
      Object o = cache.get(invocation);
      if (o == null) {
        o = deserializer.deserialize(invocation.getReturnType(), invocation.getHintedType(), key);
        cache.put(invocation, o);
      }
      return o;
      
    } catch (BlueprintException e) {
      throw new BlueprintException(buildExceptionMessage(e.getMessage(), method, args), e);
    }
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
    } catch (IllegalArgumentException e) {
      throw new BlueprintException("Error creating proxy instance for " + iface.getName(), e);
    } catch (ClassCastException e) {
      throw new BlueprintException("Error casting proxy instance to " + iface.getName(), e);
    }
  }

  private String getKeyFor(Method method) {
    Key keyAnn = method.getAnnotation(Key.class);
    if (keyAnn != null) {
      return keyAnn.value();
    }
    return method.getName();
  }

  private String buildExceptionMessage(String cause, Method method, Object... args) {
    return String.format("%s, in class %s, method %s%s",
            cause,
            iface.getName(),
            method.getName(),
            args == null ? "[]" : Arrays.asList(args));
  }

}

