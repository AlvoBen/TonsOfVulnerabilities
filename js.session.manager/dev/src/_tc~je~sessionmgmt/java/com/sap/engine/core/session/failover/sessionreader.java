/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.core.session.failover;

import java.io.ObjectInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.ObjectStreamClass;
import java.util.HashMap;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

/**
 * Author: georgi-s
 * Date: 2005-6-1
 */
public class SessionReader extends ObjectInputStream {
  /** table mapping primitive type names to corresponding class objects */
  private static final HashMap<String, Class> primClasses = new HashMap<String, Class>(8, 1.0F);
     static {
   primClasses.put("boolean", boolean.class);
   primClasses.put("byte", byte.class);
   primClasses.put("char", char.class);
   primClasses.put("short", short.class);
   primClasses.put("int", int.class);
   primClasses.put("long", long.class);
   primClasses.put("float", float.class);
   primClasses.put("double", double.class);
   primClasses.put("void", void.class);
     }
  public SessionReader(InputStream in) throws IOException {
    super(in);
  }

  protected Class resolveClass(ObjectStreamClass osc) throws java.io.IOException, ClassNotFoundException {
     String loaderName = (String) readObject();
     String name = osc.getName();
     ClassLoader additionalClassLoader;
     try {
       if ("NoName".equals(loaderName) || "system:kernel".equals(loaderName.toLowerCase())) {
         return Class.forName(name);
       }
     } catch (ClassNotFoundException e) {
       // Excluding this catch block from JLIN $JL-EXC$ since there's no need to log this exception
       // Please do not remove this comment!
     }
     additionalClassLoader = DefFailoverConfiguration.loadContext.getClassLoader(loaderName);
    try {
      return Class.forName(name, false, additionalClassLoader);
    } catch (ClassNotFoundException cln) {
      Class cl = primClasses.get(name);
              if (cl != null) {
                return cl;
              } else {
                throw cln;
              }
    }
   }

    protected Class resolveProxyClass(String[] interfaces) throws IOException, ClassNotFoundException {
    String loaderName = (String) readObject();
    ClassLoader commonLoader;

    try {
      if ("NoName".equals(loaderName)) {
        return super.resolveProxyClass(interfaces);
      }
    } catch (ClassNotFoundException e) {
      // Excluding this catch block from JLIN $JL-EXC$ since there's no need to log this exception
      // Please do not remove this comment!
    }

    commonLoader = DefFailoverConfiguration.loadContext.getClassLoader(loaderName);
    return loadProxyClass(interfaces, commonLoader);
    }

    private Class loadProxyClass(String[] interfaces, ClassLoader loader) throws IOException, ClassNotFoundException {
    ClassLoader nonPublicLoader = null;
    boolean hasNonPublicInterface = false;

    // define proxy in class loader of non-public interface(s), if any
    Class[] classObjs = new Class[interfaces.length];

    for (int i = 0; i < interfaces.length; i++) {

      Class cl = Class.forName(interfaces[i], false, loader);
      if ((cl.getModifiers() & Modifier.PUBLIC) == 0) {
        if (hasNonPublicInterface) {
          if (nonPublicLoader != cl.getClassLoader()) {
            throw new IllegalAccessError("conflicting non-public interface class loaders"); //$JL-EXC$
          }
        } else {
          nonPublicLoader = cl.getClassLoader();
          hasNonPublicInterface = true;
        }
      }
      classObjs[i] = cl;
    }

    try {
      return Proxy.getProxyClass(hasNonPublicInterface ? nonPublicLoader : loader, classObjs);
    } catch (IllegalArgumentException e) {
      throw new ClassNotFoundException(null, e);
    }
  }
}
