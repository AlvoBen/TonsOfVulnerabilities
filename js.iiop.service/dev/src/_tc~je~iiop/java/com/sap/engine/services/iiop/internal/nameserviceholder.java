package com.sap.engine.services.iiop.internal;

import org.omg.CORBA.DynamicImplementation;
import org.omg.CORBA.portable.InvokeHandler;
import com.sap.engine.services.iiop.internal.giop.IncomingRequest;

public class NameServiceHolder extends InvokeHandlerHolder {

  public NameServiceHolder(InvokeHandler in_target) {
    super(in_target);
  }

  public void invoke(IncomingRequest request) throws Throwable {
    if (request.operation().equals("get")) {
      org.omg.CORBA.portable.OutputStream out = request.createReply();
      out.write_Object((org.omg.CORBA.Object)in_target);
    } else {
      super.invoke(request);
    }
  }
}
