/*
 * (c) Copyright 2002 SAP.
 * All Rights Reserved.
 */

package com.sap.pj.jmx.introspect;

import java.lang.reflect.Method;

/**
 * @version 	1.0
 * @author Gregor Frey
 */
public class AttributeAccessor {
  private boolean getter;
  private boolean setter;
  private boolean isGetter;
  private String name;
  private Class type;
  private Method method;

// RK TCK1.2.1  
  private AttributeAccessor() {
  }

  public static AttributeAccessor getAttributeAccessor(Method method) {
    if (method == null) {
      throw new IllegalArgumentException("Parameter method must not be null");
    }
    if (method.getReturnType() == Void.TYPE) {
      // assume setter
      if (method.getParameterTypes().length != 1) {
        return null;
      }
      if (!method.getName().startsWith("set")) { //$NON-NLS-1$
        // names of setter methods must start with 'set'
        return null;
      }
      if (method.getName().length() <= 3) {
        // method with name 'set' is not a setter
        return null;
      }
      AttributeAccessor aa = new AttributeAccessor();
      aa.method = method;
      aa.setter = true;
      aa.getter = false;
      aa.isGetter = false;
      aa.type = method.getParameterTypes()[0];
      aa.name = method.getName().substring(3);
      return aa;
    } else {
      // assume getter
      if (method.getParameterTypes().length != 0) {
        // getter methods must have no parameters
        return null;
      }
      if (method.getName().startsWith("is") && method.getName().length() > 2) { //$NON-NLS-1$
        if (method.getReturnType() != Boolean.class && method.getReturnType() != Boolean.TYPE) {
          throw new IllegalArgumentException("Getter methods starting with \"is\" must have a boolean return value, method: " + method.getName());
        }
        AttributeAccessor aa = new AttributeAccessor();
        aa.method = method;
        aa.setter = false;
        aa.getter = true;
        aa.isGetter = true;
        aa.type = method.getReturnType();
        aa.name = method.getName().substring(2);
        return aa;
      } else if (method.getName().startsWith("get") && method.getName().length() > 3) { //$NON-NLS-1$
        AttributeAccessor aa = new AttributeAccessor();
        aa.method = method;
        aa.setter = false;
        aa.getter = true;
        aa.isGetter = false;
        aa.type = method.getReturnType();
        aa.name = method.getName().substring(3);
        return aa;
      } else {
        return null;
      }
    }
  }
  // KR

  public boolean isGetter() {
    return getter;
  }

  public boolean isSetter() {
    return setter;
  }

  public boolean isIsGetter() {
    return isGetter;
  }

  public Class getType() {
    return type;
  }

  public String getName() {
    return name;
  }

}
