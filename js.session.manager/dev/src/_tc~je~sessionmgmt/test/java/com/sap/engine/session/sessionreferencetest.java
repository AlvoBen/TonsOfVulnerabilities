package com.sap.engine.session;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sap.engine.session.runtime.RuntimeSessionModel;
import com.sap.engine.session.runtime.timeout.TimeoutProcessor;

public class SessionReferenceTest {

	SessionContext sessionContext;
	
	@BeforeClass
	public static void beforeClass(){
    RuntimeSessionModel.timeoutProcessor = new TimeoutProcessor();
    RuntimeSessionModel.timeoutProcessor.setDaemon(true);
    RuntimeSessionModel.timeoutProcessor.start();
    new DummyContextFactory(); // to set instance
	}
	
	@Before
	public void before(){
		sessionContext = SessionContextFactory.getInstance().getSessionContext("Test_Session_Context", true);
	}
	
	@After
	public void after(){
		sessionContext.destroy();
	}
	
	@Test
	public void testSessionReference() throws Exception {
   SessionDomain sessionDomain = sessionContext.createSessionDomain("domain");
   SessionHolder holder = sessionDomain.getSessionHolder("pesho");
   DummySession session = (DummySession) holder.getSession(new DummySessionFactory());
   
   SessionReference sRef = new SessionReference(session);
   assertTrue(sRef.getSessionId().equals("pesho"));
   assertTrue(sRef.getDomain().equals(sessionDomain));
   assertTrue(sRef.getSession().equals(session));
   sRef.hashCode();
   
   SessionReference sRef2 = new SessionReference(session);
   
   assertTrue(sRef.equals(sRef2));
   
   sRef2.session = null;
   assertNotNull(sRef2.getSession());
	}
}
