package com.sap.engine.session;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sap.engine.core.session.persistent.memory.MemoryPersistentSessionModel;
import com.sap.engine.session.runtime.RuntimeSessionModel;
import com.sap.engine.session.runtime.timeout.TimeoutProcessor;

public class SessionClassTest {

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
	public void testAccess() throws Exception {
		DummySession session = new DummySession("session");
		session.access();
		session.initInternalLifecycle();
		session.updateInternalLifecycle();
		session.beforeLogout();
	}

	@Test
	public void testRuntimeReferences() throws Exception {
		DummySession session = new DummySession("session");
		SessionDomain sessionDomain = sessionContext.createSessionDomain("domain");
		DomainReference domainReference = new DomainReference(sessionDomain);

		session.addRuntimeDependency(domainReference, "data");
		session.removeRuntimeDependancy(domainReference, "data");
		session.markGetChunk();
		session.isExtracted();

	}

	@Test
	public void testId() throws Exception {
		DummySession session = new DummySession("pesho");
		assertTrue(session.sessionId.equals("pesho"));
		assertTrue(session.getId().equals("pesho"));
	}

	@Test
	public void testChunks() throws Exception {
		SessionDomain sessionDomain = sessionContext.createSessionDomain("domain");
		SessionHolder holder = sessionDomain.getSessionHolder("holder");
		DummySession session = (DummySession) holder.getSession(new DummySessionFactory());
		assertTrue(session.chunkCount() == 0);

		assertFalse(session.isChunksUpdated());

		Object data = new Object();
		session.addChunkData("data", data);
		assertTrue(session.chunkCount() == 1);
		assertTrue(session.chunks().size() == 1);

		DummySessionChunk chunkData = new DummySessionChunk();
		session.addChunkData("dataChunk", chunkData);

		assertTrue(session.chunkCount() == 2);
		assertTrue(session.chunks().size() == 2);
		Iterator<Object> it = session.chunksIterator();
		while (it.hasNext()) {
			assertTrue(it.next().equals(chunkData));
		}

		Collection<String> col = session.getChunkNames();
		assertTrue(col.size() == 2);
		assertTrue(col.contains("data"));
		assertTrue(col.contains("dataChunk"));

		assertTrue(session.getChunkData("dataChunk").equals(chunkData));

		session.removeChunk("data");
		assertTrue(session.chunkCount() == 1);
		assertTrue(session.chunks().size() == 1);

		assertTrue(session.isChunksUpdated());
		session.resetChunksUpdated();
		assertFalse(session.isChunksUpdated());

		session.removeChunk("dataChunk");
		assertTrue(session.chunkCount() == 0);
		assertTrue(session.chunks().size() == 0);
		assertTrue(session.isChunksUpdated());

		((RuntimeSessionModel) session.thisModel).persistentModel = new MemoryPersistentSessionModel("session id");
//		((RuntimeSessionModel) session.thisModel).deltaFailoverEnabled = true;
		RuntimeSessionModel.deltaFailoverEnabled = true;

		assertFalse(session.hasModifyChunkNames());
		assertFalse(session.hasRemoveChunkNames());

		chunkData = new DummySessionChunk();
		session.addChunkData("chunk", chunkData);

		assertTrue(session.hasModifyChunkNames());
		assertTrue(session.getModifyChunkNames().size() == 1);
		assertTrue(session.getModifyChunkNames().contains("chunk"));
		assertFalse(session.hasRemoveChunkNames());

		session.removeChunk("chunk");
		assertFalse(session.hasModifyChunkNames());
		assertTrue(session.hasRemoveChunkNames());
		assertTrue(session.getRemoveChunkNames().size() == 1);
		assertTrue(session.getRemoveChunkNames().contains("chunk"));

		session.clearModifyChunkNames();
		session.clearRemoveChunkNames();
		assertFalse(session.hasModifyChunkNames());
		assertFalse(session.hasRemoveChunkNames());

		assertFalse(session.markGetChunk());
		session.renew();

	}

	@Test
	public void testCommon() throws Exception {
		SessionDomain sessionDomain = sessionContext.createSessionDomain("domain");
		SessionHolder holder = sessionDomain.getSessionHolder("holder");
		DummySession session = (DummySession) holder.getSession(new DummySessionFactory());

		assertTrue(session.isSticky());
		assertTrue(session.isValid());

		session.setSticky(false);
		assertTrue(session.isSticky());
		session.setMaxInactiveInterval(10);

		assertTrue(session.getMaxInactiveInterval() == 10);
		assertTrue(session.getMaxInactiveIntervalField() == 10);
		try {
			Thread.sleep(5000);
		} catch (Exception e) {
		}
		((RuntimeSessionModel) session.thisModel).commit("aaa");

		assertTrue(session.getLastAccessedTime() > session.getCreationTime());

		assertTrue(session.failoverScope() == 0);

		assertNull(session.getPersistentModel());

		assertFalse(session.isExtracted());
		session.setExtracted(true);
		assertTrue(session.isExtracted());
		session.setPersistedChunks(new HashMap<String, Object>());
		sessionDomain.invalidateSession(session);
	}

}
