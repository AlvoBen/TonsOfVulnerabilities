package com.sap.engine.sessionmgmt.jco.applib.impl;

import com.sap.engine.session.callback.Callback;
import com.sap.engine.session.callback.CallbackException;
import com.sap.engine.session.callback.CallbackHandler;


public class JCoCallbackHandler implements CallbackHandler {
  public void handle(Callback callback) throws CallbackException {
    if (callback instanceof JCoSessionReferenceImpl) {
      //TODO CHECK
      ((JCoSessionReferenceImpl) callback).clear();
      if(((JCoSessionReferenceImpl) callback).isActive()){
        ((JCoSessionReferenceImpl) callback).releaseConnections();
      }
    } else {
      throw new CallbackException("Not expected callback implementation for this handler! Handler is " + getClass().getName() + " , Callback is " + callback.getClass().getName());
    }
  }

  public String handlerName() {
    return SessionReferenceProviderImpl.JCO_SESSION_REF_KEY;
  }
}
