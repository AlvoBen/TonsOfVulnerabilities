package com.sap.engine.services.rmi_p4.all;

import java.io.Serializable;

public class ConnectionProfile implements Serializable {

  static final long serialVersionUID = 1656095568155997121L;
  private String type;
  private String host;
  private int port;
  private String identification;

  public ConnectionProfile(String _type, String _host, int _port) {
    this.type = _type;
    this.host = _host;
    this.port = _port;
    StringBuilder buff = new StringBuilder(_type);
    buff.append(":");
    buff.append(_host);
    buff.append(":");
    buff.append(_port);
    identification = buff.toString();
  }

  public boolean equalsTo(String profile) {
    StringBuilder buff = new StringBuilder(type);
    buff.append(host);
    buff.append(port);
    String stringId = buff.toString();
    return stringId.equals(profile);
  }

  public boolean supplyType(String _type) {
    return type.equalsIgnoreCase(_type);
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getType() {
    return type;
  }

  public boolean equals(Object _obj) {
    if (ConnectionProfile.class.isAssignableFrom(_obj.getClass())) {
      ConnectionProfile obj = (ConnectionProfile) _obj;

      if ((obj.port != this.port) || (!obj.host.equals(this.host)) || (!obj.type.equals(this.type))) {
        return false;
      }

      return true;
    }

    return false;
  }

  public int hashCode() {
    return super.hashCode();
  }

  public String toString() {
    return identification;
  }

}

