package com.sap.engine.lib.jaxp;

public interface LoaderConnection {

  public Class forName(String name) throws ClassNotFoundException;

}

