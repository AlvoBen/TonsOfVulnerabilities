package com.sap.engine.services.iiop.csiv2.CSI;

import org.omg.CORBA.portable.IDLEntity;

public final class ContextError implements IDLEntity {

  public ContextError() {
    client_context_id = 0L;
    major_status = 0;
    minor_status = 0;
    error_token = null;
  }

  public ContextError(long l, int i, int j, byte abyte0[]) {
    client_context_id = 0L;
    major_status = 0;
    minor_status = 0;
    error_token = null;
    client_context_id = l;
    major_status = i;
    minor_status = j;
    error_token = abyte0;
  }

  public long client_context_id;
  public int major_status;
  public int minor_status;
  public byte error_token[];

}

