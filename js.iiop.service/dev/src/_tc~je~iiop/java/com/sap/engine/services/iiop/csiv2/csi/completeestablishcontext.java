package com.sap.engine.services.iiop.csiv2.CSI;

import org.omg.CORBA.portable.IDLEntity;

public final class CompleteEstablishContext implements IDLEntity {

  public CompleteEstablishContext() {
    client_context_id = 0L;
    context_stateful = false;
    final_context_token = null;
  }

  public CompleteEstablishContext(long l, boolean flag, byte abyte0[]) {
    client_context_id = 0L;
    context_stateful = false;
    final_context_token = null;
    client_context_id = l;
    context_stateful = flag;
    final_context_token = abyte0;
  }

  public long client_context_id;
  public boolean context_stateful;
  public byte final_context_token[];

}

