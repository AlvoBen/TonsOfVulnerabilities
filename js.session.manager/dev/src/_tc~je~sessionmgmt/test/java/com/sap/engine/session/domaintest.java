package com.sap.engine.session;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sap.engine.session.DomainExistException;
import com.sap.engine.session.SessionContext;
import com.sap.engine.session.SessionContextFactory;
import com.sap.engine.session.SessionDomain;
import com.sap.engine.session.runtime.RuntimeSessionModel;
import com.sap.engine.session.runtime.timeout.TimeoutProcessor;

public class DomainTest {
	
	SessionContext sessionContext;

	@BeforeClass
	public static void beforeClass() {
		RuntimeSessionModel.timeoutProcessor = new TimeoutProcessor();
		RuntimeSessionModel.timeoutProcessor.setDaemon(true);
		RuntimeSessionModel.timeoutProcessor.start();
		new DummyContextFactory(); // to set instance
	}

	@Before
	public void before() {
		sessionContext = SessionContextFactory.getInstance().getSessionContext("Test_Session_Context", true);
	}

	@After
	public void after() {
		sessionContext.destroy();
	}
	
	@Test
	public void testGettingNonExistingSessionContexts() throws Exception {

		// Trying to get a non-existing SessionContext
		SessionContext sessionContext = SessionContextFactory.getInstance()
				.getSessionContext("Test_Example_Session_Context", false);
		assertNull("Creates SessionContext when it must not create", sessionContext);

		// Creating SessionContext
		sessionContext = SessionContextFactory.getInstance().getSessionContext("Test_Example_Session_Context", true);
		assertNotNull("SessionContext not created", sessionContext);
	}

	@Test
	public void testGettingExistingSessionContexts() throws Exception {
		assertSame("getSessionContext() gets the wrong context", SessionContextFactory.getInstance().getSessionContext(
				"Test_Session_Context", false), sessionContext);
		assertSame("getSessionContext() gets the wrong context", SessionContextFactory.getInstance().getSessionContext(
				"Test_Session_Context", true), sessionContext);
	}

	@Test
	public void testDestroySessionContexts() throws Exception {
		SessionContext sessionContext = SessionContextFactory.getInstance().getSessionContext("Test_Example_Session_Context", true);
		sessionContext.destroy();
		assertNull("Get Destroyed SessionContext", SessionContextFactory.getInstance().getSessionContext(
				"Test_Example_Session_Context", false));
	}

	// Test of creating a new SessionDomain
	// Pass if the new SessionDomain object is not null
	@Test
	public void testCreateAndDestroyDomain() throws Exception {
		// creating session domain
		SessionDomain sessionDomain = sessionContext.createSessionDomain("domain");
		assertNotNull("Null SessionDomain", sessionDomain);
		assertNotNull("SessionDomain not found in the SessionContext", sessionContext.findSessionDomain("domain"));

		SessionDomain sessionDomainSub = sessionDomain.createSubDomain("subdomain");

		assertFalse("SessionDomain destroyed", sessionDomain.isDestroyed());
		sessionDomain.destroy();
		assertTrue("SessionDomain destroyed", sessionDomain.isDestroyed());
		assertNull("Destroyed SessionDomain found", sessionContext.findSessionDomain("domain"));
		assertTrue("SubSessionDomain destroyed", sessionDomainSub.isDestroyed());
		assertNull("Destroyed SubSessionDomain found", sessionContext.findSessionDomain("domain$subdomain"));
		sessionDomainSub.destroy();
	}

	@Test
	public void testDomainEnclosingContext() throws Exception {
		SessionDomain sessionDomain = sessionContext.createSessionDomain("domain");
		assertSame(sessionDomain.getEnclosingContext(), sessionContext);
		SessionDomain sessionDomainSub = sessionDomain.createSubDomain("pe6o");
		assertSame(sessionDomainSub.getEnclosingContext(), sessionContext);
	}

	@Test
	public void testCreateAndDestroySubDomain() throws Exception {
		// creating session domain
		SessionDomain sessionDomain = sessionContext.createSessionDomain("domain");
		SessionDomain sessionDomainSub = sessionDomain.createSubDomain("subdomain");
		assertNotNull("Null SessionDomain", sessionDomain);
		assertNotNull("SessionDomain not found in the SessionContext", sessionContext.findSessionDomain("domain"));
		assertNotNull("Null SubSessionDomain", sessionDomainSub);
		assertNotNull("SubSessionDomain not found in the SessionContext", sessionContext.findSessionDomain("domain" + SessionDomain.SEPARATOR + "subdomain"));
		assertFalse("SubSessionDomain destroyed", sessionDomainSub.isDestroyed());
		sessionDomainSub.destroy();
		assertTrue("SubSessionDomain destroyed", sessionDomainSub.isDestroyed());
		assertNull("Destroyed SubSessionDomain found", sessionContext.findSessionDomain("domain$subdomain"));
	}

	// Destroy SessionDomain test
	// Pass if SessionDomain is destroyed after calling the destroy method
	@Test
	public void testDestroyDomain() throws Exception {
		System.out.println("Context: " + sessionContext);
		SessionDomain sessionDomain = sessionContext.createSessionDomain("domain");
		sessionDomain.destroy();
		assertTrue(sessionDomain.isDestroyed());
	}

	// Creates two domains with the same name
	// DomainExistException is expected
	@Test
	public void testDomainExist() throws Exception {
		System.out.println("Context: " + sessionContext);
		sessionContext.createSessionDomain("d$omain");
		try {
			sessionContext.createSessionDomain("d$omain");
			assertFalse(true);
		} catch (DomainExistException e) {
			// assertTrue(true);
		}
		assertNotNull(sessionContext.findSessionDomain("d$omain"));
	}

	@Test
	public void testDomainIsActive() throws Exception {
		SessionDomain sessionDomain = sessionContext.createSessionDomain("domain");
		assertTrue(sessionDomain.isActive());
		sessionDomain.setActive(false);
		assertFalse(sessionDomain.isActive());
		sessionDomain.setActive(true);
		assertTrue(sessionDomain.isActive());
	}

}
