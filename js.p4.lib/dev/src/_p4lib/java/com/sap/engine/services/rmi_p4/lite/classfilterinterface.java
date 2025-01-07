package com.sap.engine.services.rmi_p4.lite;

/**
 * User: I024084 e-mail:mladen.droshev@sap.com
 * Date: 2007-4-26
 */
public interface ClassFilterInterface {

  public void filter(String classname) throws ClassNotFoundException;
}
