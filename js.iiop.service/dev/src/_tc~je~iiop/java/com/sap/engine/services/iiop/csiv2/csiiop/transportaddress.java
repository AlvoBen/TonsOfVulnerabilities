package com.sap.engine.services.iiop.csiv2.CSIIOP;

import org.omg.CORBA.portable.IDLEntity;

public final class TransportAddress implements IDLEntity {

  public TransportAddress() {
    host_name = null;
    port = 0;
  }

  public TransportAddress(String s, int word0) {
    host_name = s;
    port = word0;
  }

  public String host_name;
  public int port;

}

