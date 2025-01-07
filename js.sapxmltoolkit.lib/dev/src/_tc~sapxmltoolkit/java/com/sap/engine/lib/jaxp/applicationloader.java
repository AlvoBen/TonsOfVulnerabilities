package com.sap.engine.lib.jaxp;

public class ApplicationLoader {

  private static LoaderConnection conn = null;

  public static Class forName(String name) throws ClassNotFoundException {
    if (conn == null) {
      return Class.forName(name);
    } else {
      return conn.forName(name);
    }
  }

  public static void registerConnection(LoaderConnection aconn) {
    conn = aconn;
  }

}

