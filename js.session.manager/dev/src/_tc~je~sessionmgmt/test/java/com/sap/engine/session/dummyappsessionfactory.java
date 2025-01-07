package com.sap.engine.session;

import com.sap.engine.session.CreateException;
import com.sap.engine.session.Session;
import com.sap.engine.session.SessionFactory;

public class DummyAppSessionFactory implements SessionFactory{

  public Session getSession(String sessionId) throws CreateException {
    return new DummyAppSession(sessionId);
  }
}
