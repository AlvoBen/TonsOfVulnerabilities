package com.sap.engine.services.iiop.internal.giop;

import com.sap.engine.lib.util.ConcurrentHashMapIntObject;
import com.sap.engine.interfaces.cross.CrossMessage;
import org.omg.SendingContext.RunTime;

public class ConnectionMetaData {

  public ConcurrentHashMapIntObject storedFragments = new ConcurrentHashMapIntObject();
  private RunTime defaultCodebase = null;
  private boolean isFirst = true;

  public boolean isFirst() {
    return isFirst;
  }

  public void notFirst() {
    isFirst = false;
  }

  public RunTime getDefaultCodebase() {
    return defaultCodebase;
  }

  public void setDefaultCodebase(RunTime defaultCodebase) {
    this.defaultCodebase = defaultCodebase;
  }

  public void storeFragment(int request_id, CrossMessage msg) {
    storedFragments.put(request_id, msg);
  }

}
