package com.sap.engine.services.rmi_p4.reflect;

import com.sap.engine.services.rmi_p4.exception.P4Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public abstract class AbstractInvocationHandler implements InvocationHandler {

  protected static Method equalsMethod = null;
  protected static Method hashCodeMethod = null;
  protected static Method toStringMethod = null;

  static {
    try {
      equalsMethod = Object.class.getMethod("equals", new Class[]{Object.class});
      hashCodeMethod = Object.class.getMethod("hashCode", null);
      toStringMethod = Object.class.getMethod("toString", null);
    } catch (NoSuchMethodException noSuchMethod) {
      if (P4Logger.getLocation().beError()) {
        P4Logger.trace(P4Logger.ERROR, "AbstractInvocationHandler static block", "Used JDK has Object class that does not have at least one of methods: 'equals', 'hashCode' and 'toString'. Exception: {0}", "ASJ.rmip4.rt2035" , new Object []{P4Logger.exceptionTrace(noSuchMethod)});
      }
    }

  }

  /**
   * Processes a method invocation on a proxy instance and returns the result.
   * This method will be invoked on an invocation handler when a method is
   * invoked on a proxy instance that it is associated with.
   *
   * @param proxy  the proxy instance that the method was invoked on
   * @param method the Method instance corresponding to the interface method invoked on the proxy instance
   * @param args   an array of objects containing the values of the arguments
   *               passed in the method invocation on the proxy instance
   * @return the value to return from the method invocation on the proxy instance.
   */
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Class declaringClass = method.getDeclaringClass();
    if (declaringClass == Object.class) {
      if (method.equals(equalsMethod)) {
        if (args != null && args.length > 0) {
          return (equals_internal(proxy, args[0]) ? Boolean.TRUE : Boolean.FALSE);
        }
        return Boolean.FALSE;
      } else if (method.equals(hashCodeMethod)) {
        return new Integer(invokeHashCode(proxy));
      } else if (method.equals(toStringMethod)) {
        return invokeToString(proxy);
      }
    }
    return invokeInternal(proxy, method, args);
  }

  protected String invokeToString(Object proxy) {
    return proxy.getClass().getName() + '@' + Integer.toHexString(proxy.hashCode());
  }

  protected int invokeHashCode(Object proxy) {
    return System.identityHashCode(proxy);
  }

  protected boolean invokeEquals(Object proxy, Object obj) {
    return proxy == obj;
  }

  protected abstract boolean equals_internal(Object base, Object asked) throws Throwable;

  protected abstract Object invokeInternal(Object proxy, Method method, Object[] args) throws Throwable;
}
