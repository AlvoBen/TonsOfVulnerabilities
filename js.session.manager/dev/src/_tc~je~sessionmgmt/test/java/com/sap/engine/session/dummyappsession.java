package com.sap.engine.session;

public class DummyAppSession extends AppSession{

  private static final long serialVersionUID = -7239968473077049712L;

  public DummyAppSession(String sessionId) {
		super(sessionId);
	}
	
	@Override
	protected void invalidated() {
		
	}
}
