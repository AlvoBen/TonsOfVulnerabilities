package com.sap.engine.session;

import com.sap.engine.session.SessionContextFactory;

public class DummyContextFactory extends SessionContextFactory{

  public DummyContextFactory() {
    super();
  }
  
  @Override
  public String lockInfo() {
    return "Pesho";
  }
  
}
