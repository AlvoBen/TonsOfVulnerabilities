package com.sap.engine.session.exec;

import com.sap.engine.session.DummySessionContext;
import com.sap.engine.session.SessionContext;
import com.sap.engine.session.exec.SessionExecContext;

public class DummySessionExecContext extends SessionExecContext{
  
  public DummySessionExecContext(String key) {
    super(key);
    sessionContext = new DummySessionContext("sessionId");
  }
  
  void setClientId(String clientId){
    this.clientContextId = clientId;
  }
  
  public boolean applyUserContext(Object sessionId) {
    return true;
  }
}
