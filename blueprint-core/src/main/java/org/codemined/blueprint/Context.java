package org.codemined.blueprint;

import org.codemined.util.Path;

import java.lang.reflect.Method;

/**
 * @author Zoran Rilak
 * @version 0.1
 * @since 0.1
 */
public class Context {

  private static final ThreadLocal<Context> threadContext = new ThreadLocal<Context>() {
    @Override
    protected Context initialValue() {
      return new Context();
    }
  };

  private Method method;
  private Object[] args;
  private Class iface;
  private Path<String> cfgPath;


  public static Context getThreadInstance() {
    return threadContext.get();
  }

  public void setContext(Method method, Object[] argClasses, Class iface, Path<String> cfgPath) {
    this.method = method;
    this.args = argClasses == null ? new Object[]{}: argClasses;
    this.iface = iface;
    this.cfgPath = cfgPath;
  }

  public Method getMethod() {
    return method;
  }

  public Object[] getArgClasses() {
    return args;
  }

  public Class getIface() {
    return iface;
  }

  public Path<String> getCfgPath() {
    return cfgPath;
  }

}
