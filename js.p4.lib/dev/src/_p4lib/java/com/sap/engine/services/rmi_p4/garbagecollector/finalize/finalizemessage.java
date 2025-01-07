package com.sap.engine.services.rmi_p4.garbagecollector.finalize;

import com.sap.engine.interfaces.cross.Connection;

/**
 * Copyright: Copyright (c) 2003
 * Company: SAP AG - Sap Labs Bulgaria
 *
 * @author Ivan Atanassov
 * @version 7.1
 */
class FinalizeMessage {
  private Connection connection;
  private byte[] message;
  private String className;

  FinalizeMessage next = null;
  FinalizeMessage previous = null;

  public void setMessage(Connection connection, byte[] message, String className) {
    this.connection = connection;
    this.message = message;
    this.className = className;
  }

  public Connection getConnection() {
    try {
      return connection;
    } finally {
      connection = null;
    }
  }

  public byte[] getMessage() {
    try {
      return message;
    } finally {
      message = null;
    }
  }

  public String getClassName() {
    try {
      return className;
    } finally {
      className = null;
    }
  }

  public boolean isEmpty() {
    return (connection == null);
  }

  public String toString() {
    StringBuffer result = new StringBuffer("FinalizeMessage[").append(connection.toString());
    result.append(", ").append(className).append("]");
    return result.toString();
  }
}
