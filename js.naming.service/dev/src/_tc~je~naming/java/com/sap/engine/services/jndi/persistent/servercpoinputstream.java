/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.jndi.persistent;

import java.io.*;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import com.sap.engine.services.jndi.implclient.ClientContext;
import com.sap.engine.services.jndi.JNDIFrame;
import com.sap.engine.services.jndi.implclient.OffsetClientContext;

/**
 * Server side CPO input stream
 *
 * @author Panayot Dobrikov
 * @version 4.00
 */
public class ServerCPOInputStream extends CPOInputStream {

  /**
   * Constructor
   *
   * @param bais Byte array input stream
   * @param cc Client context to use
   * @throws IOException Thrown if a problem occures.
   */
  public ServerCPOInputStream(ByteArrayInputStream bais, ClientContext cc) throws java.io.IOException {
    super(bais, cc);
  }

  /**
   * Resolves class
   *
   * @param osc Object stream class
   * @return Resolved class
   * @throws IOException Thrown if a problem occures.
   * @throws ClassNotFoundException Thrown if a problem occures.
   */
  protected Class resolveClass(ObjectStreamClass osc) throws java.io.IOException, ClassNotFoundException {
    String loaderName = (String) readObject();
    String name = osc.getName();
    Class result = null;
    ClassLoader additionalClassLoader = null;
    try {
      if ("NoName".equals(loaderName)) {
        return Class.forName(name);
      }
    } catch (ClassNotFoundException e) {
      // Excluding this catch block from JLIN $JL-EXC$ since there's no need to log this exception
      // Please do not remove this comment!
      result = null;
    }

    if (context.commonLoader == null) {
      context.commonLoader = JNDIFrame.getClassLoader(loaderName); //still can be null
    }

    try {
      if (super.context instanceof OffsetClientContext) {
        try {
          additionalClassLoader = (((OffsetClientContext) super.context).getAdditionalClassLoader());

          if (additionalClassLoader != null) {
            try {
              result = Class.forName(name, false, additionalClassLoader);
            } catch (Exception e) {
              result = Class.forName(name, false, ((OffsetClientContext) super.context).getApplicationClassLoader()); //classloader cannot be null
            }
          } else {
            result = Class.forName(name, false, ((OffsetClientContext) super.context).getApplicationClassLoader()); //classloader cannot be null
          }
        } catch (ClassNotFoundException e2) {
          //this classloader can be null
          result = Class.forName(name, false, context.commonLoader); //can throw null pointer or ClassNotFound
        }
      } else {
        result = Class.forName(name, false, context.commonLoader); //can throw null pointer or ClassNotFound
      }

      return result;
    } catch (Exception e4) {
      // Excluding this catch block from JLIN $JL-EXC$ since there's no need to log this exception
      // Please do not remove this comment!
      //can be ClassNotFoundException or NullPointerException
      try {
        ClassLoader loader = JNDIFrame.getClassLoader(loaderName);
        return Class.forName(name, false, loader);
      } catch (Exception e5) {
        // Excluding this catch block from JLIN $JL-EXC$ since there's no need to log this exception
        // Please do not remove this comment!
        try {
          return Class.forName(name);
        } catch (Exception e6) { // try with thread context classloader
          try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

            if (contextClassLoader != null) {
              return Class.forName(name, false, contextClassLoader);
            } else {
              throw new ClassNotFoundException(name);
            }
          } catch (ClassNotFoundException e) {
            Class cl = (Class) primClasses.get(name);
            if (cl != null) {
              return cl;
            } else {
              throw e;
            }
          }
        }
      }
    }
  }

  /**
   * table mapping primitive type names to corresponding class objects
   */
  private static final HashMap primClasses = new HashMap(8, 1.0F);

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


  protected Class resolveProxyClass(String[] interfaces) throws IOException, ClassNotFoundException {
    String loaderName = (String) readObject();
    Class result = null;
    ClassLoader additionalClassLoader = null;
    ClassLoader commonLoader = null;

    try {
      if ("NoName".equals(loaderName)) {
        return super.resolveProxyClass(interfaces);
      }
    } catch (ClassNotFoundException e) {
      // Excluding this catch block from JLIN $JL-EXC$ since there's no need to log this exception
      // Please do not remove this comment!
      result = null;
    }

    commonLoader = JNDIFrame.getClassLoader(loaderName); //still can be null

    try {
      if (super.context instanceof OffsetClientContext) {
        try {
          additionalClassLoader = (((OffsetClientContext) super.context).getAdditionalClassLoader());

          if (additionalClassLoader != null) {
            try {
              result = loadProxyClass(interfaces, additionalClassLoader);
            } catch (Exception e) {
              result = loadProxyClass(interfaces, ((OffsetClientContext) super.context).getApplicationClassLoader()); //classloader cannot be null
            }
          } else {
            result = loadProxyClass(interfaces, ((OffsetClientContext) super.context).getApplicationClassLoader()); //classloader cannot be null
          }
        } catch (ClassNotFoundException e2) {
          //this classloader can be null
          result = loadProxyClass(interfaces, commonLoader); //can throw null pointer or ClassNotFound
        }
      } else {
        result = loadProxyClass(interfaces, commonLoader); //can throw null pointer or ClassNotFound
      }

      return result;
    } catch (Exception e4) {
      // Excluding this catch block from JLIN $JL-EXC$ since there's no need to log this exception
      // Please do not remove this comment!
      //can be ClassNotFoundException or NullPointerException
      try {
        ClassLoader loader = JNDIFrame.getClassLoader(loaderName);
        return loadProxyClass(interfaces, loader);
      } catch (Exception e5) {
        // Excluding this catch block from JLIN $JL-EXC$ since there's no need to log this exception
        // Please do not remove this comment!
        try {
          return loadProxyClass(interfaces, this.getClass().getClassLoader());
        } catch (Exception e6) { // try with thread context classloader
          ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

          if (contextClassLoader != null) {
            return loadProxyClass(interfaces, contextClassLoader);
          } else {
            return super.resolveProxyClass(interfaces);
          }
        }
      }
    }

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

