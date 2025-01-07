package com.sap.engine.services.rmi_p4;

public class ObjectInfo {

  private byte[] message;

  public void setInfo(byte[] message) {
    this.message = message;
  }

  public byte[] getInfo() {
    return this.message;
  }

}

