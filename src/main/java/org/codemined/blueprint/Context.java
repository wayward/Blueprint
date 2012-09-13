package org.codemined.blueprint;

import org.codemined.util.Strings;

import java.lang.reflect.Method;
import java.util.LinkedList;

/**
 * @author Zoran Rilak
 * @version 0.1
 * @since 0.1
 */
public class Context {

  private Method method;
  private Object[] args;
  private Class iface;

  public Context() {

  }

  public void setContext(Method method, Object[] argClasses, Class iface) {
    this.method = method;
    this.args = argClasses == null ? new Object[]{}: argClasses;
    this.iface = iface;
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


  public String decorateException(String message) {
    LinkedList<String> argStr = new LinkedList<String>();
    for (Object o : getArgClasses()) {
      argStr.add(o.toString());
    }
    return message +
            ", in method " + getMethod()
            + "(" + Strings.join(", ", argStr) + ")"
            + ", for class " + getIface().getCanonicalName();
  }

}
