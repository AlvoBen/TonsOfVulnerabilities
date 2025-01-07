package com.sap.engine.session;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sap.engine.session.Session;
import com.sap.engine.session.SessionContext;
import com.sap.engine.session.SessionContextFactory;
import com.sap.engine.session.SessionDomain;
import com.sap.engine.session.SessionHolder;
import com.sap.engine.session.exec.ClientContextImpl;
import com.sap.engine.session.exec.DummySubjectHolder;
import com.sap.engine.session.exec.DummyThreadContextProxy;
import com.sap.engine.session.exec.LoginSessionImpl;
import com.sap.engine.session.exec.SessionExecContext;
import com.sap.engine.session.runtime.RuntimeSessionModel;
import com.sap.engine.session.runtime.timeout.TimeoutProcessor;

public class SessionTest {

	SessionContext sessionContext;

  static DummyThreadContextProxy threadContextProxy;
  
	@BeforeClass
  public static void beforeClass() {
    RuntimeSessionModel.timeoutProcessor = new TimeoutProcessor();
    RuntimeSessionModel.timeoutProcessor.setDaemon(true);
    RuntimeSessionModel.timeoutProcessor.start();
    threadContextProxy = new DummyThreadContextProxy("sessionId");
    try {
    SessionExecContext.setThreadContextProxyImpl(threadContextProxy);
    } catch (IllegalStateException ise) {
    }
    new DummyContextFactory(); // to set instance
    LoginSessionImpl ls = new LoginSessionImpl();
    ls.setSubjectHolder(new DummySubjectHolder());
    ClientContextImpl.anonymousLoginSession = ls;
  }
  
	@Before
	public void before() {
    threadContextProxy.refresh();
		sessionContext = SessionContextFactory.getInstance().getSessionContext("Test_Session_Context", true);
	}

	@After
	public void after() {
		sessionContext.destroy();
	}

	@Test
	public void testCreateSession() throws Exception {
		SessionDomain sessionDomain = sessionContext.createSessionDomain("domain");
		SessionHolder holder = sessionDomain.getSessionHolder("holder");
		Session session = holder.getSession(new DummySessionFactory());
		assertNotNull(session);
	}

	@Test
	public void testDomainOfSession() throws Exception {
		SessionDomain sessionDomain = sessionContext.createSessionDomain("domain");
		SessionHolder holder = sessionDomain.getSessionHolder("holder");
		Session session = holder.getSession(new DummySessionFactory());
		assertSame(session.domain(), sessionDomain);
	}

	@Test
	public void testSessionIsValid() throws Exception {
		SessionDomain sessionDomain = sessionContext.createSessionDomain("domain");
		SessionHolder holder = sessionDomain.getSessionHolder("holder");
		Session session = holder.getSession(new DummySessionFactory());
		assertTrue(session.isValid());
	}

	@Test
	public void testSessionDomainContainsSession() throws Exception {
		SessionDomain sessionDomain = sessionContext.createSessionDomain("domain");
		SessionHolder holder = sessionDomain.getSessionHolder("holder");
		Session session = holder.getSession(new DummySessionFactory());
		assertTrue(sessionDomain.containsSession(session.sessionId()));
		assertTrue(sessionDomain.containsSession("holder"));
	}

	@Test
	public void testSessionDomainSizes() throws Exception {
		SessionDomain sessionDomain = sessionContext.createSessionDomain("domain");
		SessionHolder holder = sessionDomain.getSessionHolder("pesho");
		holder.getSession(new DummySessionFactory());
		assertTrue(sessionDomain.size() == 1);
	}

	@Test
	public void testSessionDomainSizes2() throws Exception {
		SessionDomain sessionDomain = sessionContext.createSessionDomain("domain");
		SessionHolder holder = sessionDomain.getSessionHolder("pesho");
		holder.getSession(new DummySessionFactory());
		sessionDomain.getSessionHolder("pesho2").getSession(new DummySessionFactory());
		assertTrue(sessionDomain.size() == 2);
	}

	@Test
	public void testSessionInvalidation() throws Exception {
		SessionDomain sessionDomain = sessionContext.createSessionDomain("domain");
		RuntimeSessionModel rsm[] = new RuntimeSessionModel[1000];
		for (int i = 0; i < rsm.length; i++) {
			SessionHolder holder = sessionDomain.getSessionHolder("pesho" + i);
			Session session = holder.getSession(new DummySessionFactory());
			session.expired();
			session.updateInternalLifecycle();
			session.setMaxInactiveInterval((int) Math.round(Math.random() * 9) + 1);
			holder.commitAccess();
			rsm[i] = (RuntimeSessionModel) session.getSessionModel();
		}
    Thread.sleep(20000);

		System.out.println("Before Invalidate check");
		for (int i = 0; i < rsm.length; i++) {
		  assertTrue(rsm[i].isInvalidateCalled());
		}
		System.out.println("After invalidation check");

		// to kill the Queue and Timeouter threads which are not daemon threads
	}
}