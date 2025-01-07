package com.sap.engine.session;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sap.engine.session.exec.SessionExecContext;
import com.sap.engine.session.exec.DummyThreadContextProxy;
import com.sap.engine.session.runtime.RuntimeSessionModel;
import com.sap.engine.session.runtime.timeout.TimeoutProcessor;

public class AppSessionTest {

	static int TIMEOUT = 30000;
	SessionContext sessionContext;
	AppSession appSession;
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
	}

	@Before
	public void before() {
	  threadContextProxy.refresh();
		sessionContext = SessionContextFactory.getInstance().getSessionContext("Test_Session_Context", true);
		appSession = new DummyAppSession("sessionId");
    
	}

	@After
	public void after() {
		appSession.invalidate();
		sessionContext.destroy();
	}

	@Test
	public void testMethod() throws Exception {
		DummyAppSession session;
		SessionDomain sessionDomain = sessionContext.createSessionDomain("Test_Session_Domain");
		SessionHolder holder = sessionDomain.getSessionHolder("sessionId");
		session = (DummyAppSession) holder.getSession(new DummyAppSessionFactory());// .thisModel;
		((RuntimeSessionModel) session.thisModel).commit("endOfRequest");

		DummyLifecycleManagedData dataToExpire = new DummyLifecycleManagedData(10);
		DummyLifecycleManagedData dataNotToExpire = new DummyLifecycleManagedData(100);

		session.addLifecycleManagedAttribute("dataToExpire", dataToExpire);
		session.addLifecycleManagedAttribute("dataNotToExpire", dataNotToExpire);

		try {
			Thread.sleep(1000 * 20);
		} catch (InterruptedException e) {
		}

		assertTrue(dataToExpire.isExpireCalled());
		assertFalse(dataNotToExpire.isExpireCalled());
		session.beforeLogout();
		assertTrue(dataNotToExpire.isExpireCalled());
	}

	@Test
	public void testLifecycleManagedData() throws Exception {
		DummyAppSession session;

		SessionDomain sessionDomain = sessionContext.createSessionDomain("Test_Session_Domain");
		SessionHolder holder = sessionDomain.getSessionHolder("sessionId");

		session = (DummyAppSession) holder.getSession(new DummyAppSessionFactory());

		DummyLifecycleManagedData2 dataToExpire = new DummyLifecycleManagedData2(10);

		session.addLifecycleManagedAttribute("lifecycleManagedData", dataToExpire);

		try {
			Thread.sleep(1000 * 25);
			assertFalse("isExpire() is called when there is an active request", dataToExpire.isCalledInRequest());
			assertFalse("expire() method is called in an active request", dataToExpire.isExpireCalled());

			Thread.sleep(1000 * 5);
			((RuntimeSessionModel) session.thisModel).commit("endOfRequest");

			Thread.sleep(1000 * 15);
			assertFalse("isExpire() is called when there is an active request", dataToExpire.isCalledInRequest());
			assertTrue(dataToExpire.isExpireCalled());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAttributes() throws Exception {
		assertNotNull(appSession.attributeNames());
		assertTrue(appSession.attributeNames().size() == 0);

		assertNull(appSession.getAttribute("attr1"));

		String attr1 = "Hellom world1";
		appSession.setAttribute("attr1", attr1);
		assertTrue(appSession.attributeNames().size() == 1);

		String attr2 = "Hellom world2";
		appSession.setAttribute("attr2", attr2);
		assertTrue(appSession.attributeNames().size() == 2);

		Object getAttr1 = appSession.getAttribute("attr1");
		assertNotNull(getAttr1);
		assertEquals((String) getAttr1, attr1);

		getAttr1 = appSession.getRemoveAttribute("attr1");
		assertNotNull(getAttr1);
		assertEquals((String) getAttr1, attr1);

		getAttr1 = appSession.getAttribute("attr1");
		assertNull(getAttr1);

		assertTrue(appSession.attributeNames().size() == 1);
	}

	@Test
	public void testAttributeNames() throws Exception {
		assertNotNull(appSession.attributeNames());
	}

	@Test
	public void testSetAttribute1() throws Exception {
		String str = "Test Object";
		appSession.setAttribute("attr", str);

		assertTrue(appSession.attributeNames().size() == 1);
		assertTrue(appSession.attributeNames().contains("attr"));

	}

	@Test
	public void testSetAttribute2() throws Exception {
		SessionDomain sessionDomain = sessionContext.createSessionDomain("domain");
		SessionHolder holder = sessionDomain.getSessionHolder("pesho");

		DummyAppSession session = (DummyAppSession) holder.getSession(new DummyAppSessionFactory());
		session.invalidate();
		String str = "Test Object";

		boolean pass = false;
		try {
			session.setAttribute("attr", str);
		} catch (IllegalStateException e) {
			pass = true;
		}
		assertTrue(pass);
		assertTrue(session.attributeNames().size() == 0);

	}

	@Test
	public void testGetRemoveAttribute1() throws Exception {
		String str = "Test Object";
		appSession.setAttribute("attr", str);

		appSession.getRemoveAttribute("attr");
		assertTrue(appSession.attributeNames().size() == 0);
		assertFalse(appSession.attributeNames().contains("attr"));
	}

	@Test
	public void testGetRemoveAttribute2() throws Exception {
		SessionDomain sessionDomain = sessionContext.createSessionDomain("domain");
		SessionHolder holder = sessionDomain.getSessionHolder("sessionId");

		DummyAppSession session = (DummyAppSession) holder.getSession(new DummyAppSessionFactory());
		String str = "Test Object";
		session.setAttribute("attr", str);

		session.invalidate();

		boolean pass = false;
		try {
			session.getRemoveAttribute("attr");
		} catch (IllegalStateException e) {
			pass = true;
		}
		assertTrue(pass);
		assertTrue(session.attributeNames().size() == 1);

	}

	@Test
	public void testGetAttribute1() throws Exception {
		String str = "Test Object";
		appSession.setAttribute("attr", str);
		assertNotNull(appSession.getAttribute("attr"));
		assertTrue(((String) appSession.getAttribute("attr")).equals(str));

		assertNull(appSession.getAttribute("nonexistingattribute"));
	}

	@Test
	public void testGetAttribute2() throws Exception {
		String str = "Test Object";
		appSession.addChunkData("attr", str);
		assertNotNull(appSession.getAttribute("attr"));
		assertTrue(((String) appSession.getAttribute("attr")).equals(str));

		assertNull(appSession.getAttribute("nonexistingattribute"));

		appSession.invalidate();
	}

	@Test
	public void testAddLifecycleManagedAttribute() throws Exception {
		SessionDomain sessionDomain = sessionContext.createSessionDomain("domain");
		SessionHolder holder = sessionDomain.getSessionHolder("sessionId");

		DummyAppSession session = (DummyAppSession) holder.getSession(new DummyAppSessionFactory());
		DummyLifecycleManagedData data = new DummyLifecycleManagedData(10);
		session.addLifecycleManagedAttribute("data", data);

		assertTrue(session.hasLifecycleManagedAttributes());
		assertTrue(session.getLifecycleManagedAttribute("data").equals(data));

	}

	@Test
	public void testGetLifecycleManagedDataEntry() throws Exception {
		SessionDomain sessionDomain = sessionContext.createSessionDomain("Test_Session_Domain");
		SessionHolder holder = sessionDomain.getSessionHolder("sessionId");
		DummyAppSession session = (DummyAppSession) holder.getSession(new DummyAppSessionFactory());

		assertNull(session.getLifecycleManagedAttribute("lifecycleManagedData"));

		DummyLifecycleManagedData lifecycleManagedData = new DummyLifecycleManagedData(10);
		session.addLifecycleManagedAttribute("lifecycleManagedData", lifecycleManagedData);

		assertNotNull(session.getLifecycleManagedAttribute("lifecycleManagedData"));
		DummyLifecycleManagedData data = new DummyLifecycleManagedData(5);
		session.addLifecycleManagedAttribute("data", data);

		assertNotNull(session.getLifecycleManagedAttribute("data"));
	}

	@Test
	public void testRemoveLifecycleManagedDataEntry() throws Exception {
		SessionDomain sessionDomain = sessionContext.createSessionDomain("Test_Session_Domain");
		SessionHolder holder = sessionDomain.getSessionHolder("sessionId");

		DummyAppSession session = (DummyAppSession) holder.getSession(new DummyAppSessionFactory());

		DummyLifecycleManagedData lifecycleManagedData = new DummyLifecycleManagedData(10);
		session.addLifecycleManagedAttribute("lifecycleManagedData", lifecycleManagedData);

		session.removeLifecycleManagedAttribute("lifecycleManagedData");

		assertNull(session.getLifecycleManagedAttribute("lifecycleManagedData"));
		assertFalse(session.hasLifecycleManagedAttributes());
	}

	@Test
	public void testRuntimeDependencies() throws Exception {
		SessionDomain sessionDomain1 = sessionContext.createSessionDomain("Test_Session_Domain1");
		SessionDomain sessionDomain2 = sessionContext.createSessionDomain("Test_Session_Domain2");
		SessionHolder holder = sessionDomain1.getSessionHolder("sessionId");

		DummyAppSession session = (DummyAppSession) holder.getSession(new DummyAppSessionFactory());

		DomainReference reference = new DomainReference(sessionDomain2);

		DummyLifecycleManagedData lifecycleManagedData = new DummyLifecycleManagedData(10);
		session.addLifecycleManagedAttribute("lifecycleManagedData", lifecycleManagedData);

		session.addRuntimeDependency(reference, "lifecycleManagedData");

		session.removeRuntimeDependancy(reference, "lifecycleManagedData");
	}

	@Test
	public void testInvalidate() throws Exception {
		SessionDomain sessionDomain = sessionContext.createSessionDomain("Test_Session_Domain");
		SessionHolder holder = sessionDomain.getSessionHolder("sessionId");
		DummyAppSession session = (DummyAppSession) holder.getSession(new DummyAppSessionFactory());
		((RuntimeSessionModel) session.thisModel).commit("endOfRequest");

		DummyLifecycleManagedData lifecycleManagedData = new DummyLifecycleManagedData(10);
		session.addLifecycleManagedAttribute("lifecycleManagedData", lifecycleManagedData);
		session.invalidate();

		assertFalse("Session is invalidated and must not be valid", session.isValid());
		assertTrue("sessionDomain consists of a session which is invalidated", sessionDomain.size() == 0);
	}

}
