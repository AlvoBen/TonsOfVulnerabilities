/*
 * Created on 2004.11.30
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.cache.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

import com.sap.engine.cache.util.dump.LogUtil;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SubstituteObjectInputStream extends ObjectInputStream {

  /**
   * @param in
   * @throws IOException
   */
  public SubstituteObjectInputStream(InputStream in) throws IOException {
    super(in);
  }
  
  protected Class resolveProxyClass(String[] interfaces) throws IOException, ClassNotFoundException {
    try {
      ClassLoader latestLoader = Thread.currentThread().getContextClassLoader();
      ClassLoader nonPublicLoader = null;
      boolean hasNonPublicInterface = false;
      
      // define proxy in class loader of non-public interface(s), if any
      Class[] classObjs = new Class[interfaces.length];
      for (int i = 0; i < interfaces.length; i++) {
        Class cl = Class.forName(interfaces[i], false, latestLoader);
        if ((cl.getModifiers() & Modifier.PUBLIC) == 0) {
            if (hasNonPublicInterface) {
              if (nonPublicLoader != cl.getClassLoader()) {
                throw new IllegalAccessError("conflicting non-public interface class loaders");
              }
            } else {
              nonPublicLoader = cl.getClassLoader();
              hasNonPublicInterface = true;
            }
        }
        classObjs[i] = cl;
      }
      try {
        return Proxy.getProxyClass(hasNonPublicInterface ? nonPublicLoader : latestLoader, classObjs);
      } catch (IllegalArgumentException e) {
        throw new ClassNotFoundException(null, e);
      }
    } catch (ClassNotFoundException e) {
      LogUtil.logTInfo(e);
      return super.resolveProxyClass(interfaces);
    }
  }
  
  
  protected Class resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
    try {
      return Class.forName(desc.getName(), false, Thread.currentThread().getContextClassLoader());
    } catch (ClassNotFoundException e) {
      LogUtil.logTInfo(e);
      return super.resolveClass(desc);
    }
  }
}
