package com.sap.engine.services.security.patch;

public interface Change {

  /**
   * Runs a patch level for the security persistent storage.
   */
  public void run() throws Exception;
}
