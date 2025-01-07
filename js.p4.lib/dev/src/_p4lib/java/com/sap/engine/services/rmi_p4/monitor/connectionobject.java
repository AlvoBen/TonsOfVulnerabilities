/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.services.rmi_p4.monitor;

import com.sap.engine.services.rmi_p4.interfaces.ConnectionObjectInt;

import java.io.Serializable;

/**
 * @author Nikolai Neychev, Mladen Droshev
 * @version 7.0
 */
public class ConnectionObject implements Serializable, ConnectionObjectInt {

  public static final long serialVersionUID = -1984255406290757211L;

  private String type;
  private String host;
  private int port;
  private int conId;
  private int dispId;

  private boolean alive;

  public ConnectionObject(boolean alive, String type, String host, int port, int conId, int dispId) {
    this.alive = alive;
    this.type = type;
    this.host = host;
    this.port = port;
    this.conId = conId;
    this.dispId = dispId;
  }

  public boolean isAlive() {
    return alive;
  }

  public int getDispId() {
    return dispId;
  }

  public void setDispId(int dispId) {
    this.dispId = dispId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getConId() {
    return conId;
  }

  public void setConId(int conId) {
    this.conId = conId;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String toString() {
     return super.toString() + ":" + type + ":" + host + ":" + port + ":" + conId +":"+dispId +":"+alive;
  }

}
