package com.sap.engine.services.rmi_p4;

import java.rmi.server.Operation;

public class StubBaseImpl extends StubBase {

  public static final long serialVersionUID = 7407173669422945709L;  

  public Operation[] getOperations() {
    return new Operation[0];
  }
}
