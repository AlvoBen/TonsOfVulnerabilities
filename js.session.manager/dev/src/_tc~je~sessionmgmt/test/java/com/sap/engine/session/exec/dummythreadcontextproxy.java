package com.sap.engine.session.exec;

import com.sap.engine.session.exec.SessionExecContext;
import com.sap.engine.session.exec.ThreadContextProxy;
import com.sap.engine.session.runtime.RuntimeSessionModel;
import com.sap.engine.session.usr.SubjectHolder;
import com.sap.engine.session.usr.ThrLocalContainer;

public class DummyThreadContextProxy implements ThreadContextProxy{

  SessionExecContext contextObject;
  String clientId;
  
  public DummyThreadContextProxy(String clientId) {
    this.clientId = clientId;
    refresh();
  }
  
  public void refresh(){
    contextObject = new DummySessionExecContext(clientId);
    contextObject.clientContextId = clientId;
    LoginSessionImpl ls = new LoginSessionImpl();
    SubjectHolder holder = new DummySubjectHolder();
    ls.setSubjectHolder(holder);
    contextObject.currentClientContext().setLoginSession(ls);
    contextObject.currentClientContext().setClientId(clientId);
  }
  
  public SessionExecContext currentContextObject() {
    return contextObject;
  }

  public ThrLocalContainer getCurrentSecContext() {
    return null;
  }

  public String getTenancyID() {
    return null;
  }

  public void setTenancyID(String id) {
    
  }

  public void sheduleSessionPassivation(RuntimeSessionModel session) {
    
  }

}
