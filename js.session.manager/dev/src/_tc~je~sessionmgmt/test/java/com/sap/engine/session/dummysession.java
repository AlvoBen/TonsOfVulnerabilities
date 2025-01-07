package com.sap.engine.session;

import com.sap.engine.session.Session;

public class DummySession extends Session{
  
  private static final long serialVersionUID = 5765562655085253423L;

  public DummySession(String id) {
    super(id);
  }
  
  @Override
  protected void invalidated() {    
  }
}
