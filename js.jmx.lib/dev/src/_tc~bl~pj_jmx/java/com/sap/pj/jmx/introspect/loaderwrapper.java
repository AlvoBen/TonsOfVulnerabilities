package com.sap.pj.jmx.introspect;

/**
 * @author Gregor Frey
 * @version 1.0
 */
public interface LoaderWrapper {
  public Class loadClass(String name) throws ClassNotFoundException;
}