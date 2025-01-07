package com.sap.pj.jmx.introspect;

import javax.management.loading.ClassLoaderRepository;
import java.lang.reflect.Array;

/**
 * @author Gregor Frey
 * @version 1.0
 */
public class Utilities {
  public static Class convertClassName(String name, LoaderWrapper loader)
          throws ClassNotFoundException {
    if (name == null) {
      throw new ClassNotFoundException("null");
    }
    name = name.trim();
    if (name.equals("boolean")) {
      return boolean.class;
    } else if (name.equals("byte")) {
      return byte.class;
    } else if (name.equals("char")) {
      return char.class;
    } else if (name.equals("short")) {
      return short.class;
    } else if (name.equals("int")) {
      return int.class;
    } else if (name.equals("long")) {
      return long.class;
    } else if (name.equals("float")) {
      return float.class;
    } else if (name.equals("double")) {
      return double.class;
    } else if (name.equals("java.lang.String")) {
      return String.class;
    } else if (name.equals("java.lang.Object")) {
      return Object.class;
    } else if (name.startsWith("[")) {
      // It's an array, figure out how many dimensions
      int dimension = 0;
      while (name.charAt(dimension) == '[') {
        ++dimension;
      }
      char type = name.charAt(dimension);
      Class cls = null;
      switch (type) {
        case 'Z':
          cls = boolean.class;
          break;
        case 'B':
          cls = byte.class;
          break;
        case 'C':
          cls = char.class;
          break;
        case 'S':
          cls = short.class;
          break;
        case 'I':
          cls = int.class;
          break;
        case 'J':
          cls = long.class;
          break;
        case 'F':
          cls = float.class;
          break;
        case 'D':
          cls = double.class;
          break;
        case 'L':
          // Strip the semicolon at the end
          String n = name.substring(dimension + 1, name.length() - 1);
          cls = convertClassName(n, loader);
          break;
      }
      if (cls == null) {
        throw new ClassNotFoundException(name);
      }
      int[] dim = new int[dimension];
      return Array.newInstance(cls, dim).getClass();
    } else {
      return loader.loadClass(name);
    }
  }

  /**
   * Constructor for Utilities.
   */
  private Utilities() {
  }

  public static Class[] convertSignature(
          String[] signature,
          final ClassLoader loader)
          throws ClassNotFoundException {
	Class[] clsArr = null;
	if (signature != null) {
	    clsArr = new Class[signature.length];
	    LoaderWrapper loaderWrapper = new LoaderWrapper() {
	      public Class loadClass(String name) throws ClassNotFoundException {
	        return loader.loadClass(name);
	      }
	    };
	    for (int i = 0; i < signature.length; i++) {
	      clsArr[i] = convertClassName(signature[i], loaderWrapper);
	    }
	}
    return clsArr;
  }

  /**
   * Method convertSignature.
   * @param signature
   * @param classLoaderRep
   * @return Class[]
   */
  public static Class[] convertSignature(
          String[] signature,
          final ClassLoaderRepository classLoaderRep)
          throws ClassNotFoundException {
    Class[] clsArr = new Class[signature.length];
    LoaderWrapper loaderWrapper = new LoaderWrapper() {
      public Class loadClass(String name) throws ClassNotFoundException {
        return classLoaderRep.loadClass(name);
      }
    };
    for (int i = 0; i < signature.length; i++) {
      clsArr[i] = convertClassName(signature[i], loaderWrapper);
    }
    return clsArr;
  }

  /**
   * Method convertSignature.
   * @param signature
   * @param loaderWrapper
   * @return Class[]
   */
  public static Class[] convertSignature(
          String[] signature,
          final LoaderWrapper loaderWrapper)
          throws ClassNotFoundException {
    Class[] clsArr = new Class[signature.length];
    for (int i = 0; i < signature.length; i++) {
      clsArr[i] = convertClassName(signature[i], loaderWrapper);
    }
    return clsArr;
  }

  /**
   * Method convertClassName.
   * @param string
   * @param classLoader
   * @return Class
   */
  public static Class convertClassName(String name, final ClassLoader loader)
          throws ClassNotFoundException {
    LoaderWrapper loaderWrapper = new LoaderWrapper() {
      public Class loadClass(String name) throws ClassNotFoundException {
        return loader.loadClass(name);
      }
    };
    return convertClassName(name, loaderWrapper);
  }

}