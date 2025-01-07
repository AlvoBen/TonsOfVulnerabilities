package com.sap.engine.services.iiop.csiv2.CSI;

import org.omg.CORBA.portable.IDLEntity;

public final class MessageInContext implements IDLEntity {

  public MessageInContext() {
    client_context_id = 0L;
    discard_context = false;
  }

  public MessageInContext(long l, boolean flag) {
    client_context_id = 0L;
    discard_context = false;
    client_context_id = l;
    discard_context = flag;
  }

  public long client_context_id;
  public boolean discard_context;

}

