package com.sap.engine.session.exec;

import com.sap.engine.session.runtime.RuntimeSessionModel;
import com.sap.engine.session.usr.ThrLocalContainer;
import com.sap.engine.frame.core.thread.ContextObject;

public interface ThreadContextProxy {
  public String getTenancyID();

  public void setTenancyID(String id);

  public void sheduleSessionPassivation(RuntimeSessionModel session); 

  public SessionExecContext currentContextObject();

  public ThrLocalContainer getCurrentSecContext();
}
