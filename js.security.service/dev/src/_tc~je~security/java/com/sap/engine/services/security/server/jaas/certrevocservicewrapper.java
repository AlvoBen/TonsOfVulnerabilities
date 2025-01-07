package com.sap.engine.services.security.server.jaas;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

import java.security.cert.X509Certificate;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 *  This is class is needed in order to avoid build time reference between projects
 *  com.sapall.security and AppServer
 *
 * User: i024108
 * Date: 2007-3-12
 * Time: 11:51:17
 */
public class CertRevocServiceWrapper {
  private static Object certRevocInterface = null;
  private static Class certRevocClass = null;
  private static Method isValidMethod = null;
  private static Location location = null;


  public static synchronized void init(Location loc) {
    location = loc;
  }

  private static boolean isOperational() {
    return isValidMethod != null;
  }

  public static synchronized boolean isInitialized() {
    return certRevocInterface != null;
  }

  /**
   *  isInitialized() must be true before calling isValid()
   *
   * @param profileName
   * @param x509Certificate
   * @return
   */
  public static synchronized boolean isValid(String profileName, X509Certificate x509Certificate) throws IllegalAccessException, InvocationTargetException {
    if (!isOperational()) {
      if (location.beDebug()) {
        location.logT(Severity.DEBUG, "isValid(" + profileName + ", " + x509Certificate + ") is not operational");
      }
      return false;
    }
    Boolean result = (Boolean) isValidMethod.invoke(certRevocInterface, profileName, x509Certificate);
    if (location.beDebug()) {
      location.logT(Severity.DEBUG, "isValid(" + profileName + ", " + x509Certificate + ") == " + result);
    }
    return result;
  }

  public static synchronized void interfaceIsAvailable(Object interfaceImpl) {
    certRevocInterface = interfaceImpl;
    try {
      certRevocClass = CertRevocServiceWrapper.class.getClassLoader().loadClass("com.sap.security.api.certrevoc.CertRevocStatusService");
    } catch (ClassNotFoundException e) {
      SimpleLogger.traceThrowable(Severity.ERROR, location, e, "ASJ.secsrv.000161", "Can't load class 'com.sap.security.api.certrevoc.CertRevocStatusService' using classloader '{0}'", new Object[]{ CertRevocServiceWrapper.class.getClassLoader()});
      return;
    }
    try {
      isValidMethod = certRevocClass.getDeclaredMethod("isValid", String.class, X509Certificate.class);
    } catch (NoSuchMethodException e) {
      SimpleLogger.traceThrowable(Severity.ERROR, location, e, "ASJ.secsrv.000162", "'com.sap.security.api.certrevoc.CertRevocStatusService' class does not provide method 'isValid(String, X509Certificate)'", e);
    }
  }

  public static synchronized void interfaceNotAvailable() {
    certRevocInterface = null;
    certRevocClass = null;
    isValidMethod = null;
  }
}
