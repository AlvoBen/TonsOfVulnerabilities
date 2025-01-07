package com.sap.engine.core.session.persistent.sharedmemory;

import com.sap.jvm.session.DeserializationCallback;
import com.sap.jvm.session.SharedSessionChunk;
import com.sap.engine.core.session.failover.DefFailoverConfiguration;

import java.util.HashMap;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

public class DeserializationCallbackImpl extends DeserializationCallback {

  protected DeserializationCallbackImpl(){
    super();
  }

  /**
   * table mapping primitive type names to corresponding class objects
   */
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

  public Class resolveClass(SharedSessionChunk chunk, java.io.ObjectStreamClass osc, java.lang.Object annotation) throws java.lang.ClassNotFoundException, java.io.IOException {
    String loaderName = (String) annotation;
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

  public Object resolveObject(SharedSessionChunk chunk, java.lang.Object obj)  {
    return obj;
  }

  public Class resolveProxyClass(SharedSessionChunk chunk, java.lang.String[] names, java.lang.Object annotation) throws ClassNotFoundException, java.io.IOException {
    String loaderName = (String) annotation;
    ClassLoader commonLoader;

    try {
      if ("NoName".equals(loaderName)) {
        return resolveProxyClass(names);
      }
    } catch (ClassNotFoundException e) {
      // Excluding this catch block from JLIN $JL-EXC$ since there's no need to log this exception
      // Please do not remove this comment!
    }

    commonLoader = DefFailoverConfiguration.loadContext.getClassLoader(loaderName);
    return loadProxyClass(names, commonLoader);
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

  protected Class resolveProxyClass(String[] interfaces) throws IOException, ClassNotFoundException {
    ClassLoader latestLoader = this.getClass().getClassLoader();
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
  }
}
