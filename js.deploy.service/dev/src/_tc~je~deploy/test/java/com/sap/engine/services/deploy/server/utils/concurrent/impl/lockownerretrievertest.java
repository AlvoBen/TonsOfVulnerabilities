package com.sap.engine.services.deploy.server.utils.concurrent.impl;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.sap.engine.frame.core.thread.ThreadSystem;

public class LockOwnerRetrieverTest {
	private ThreadSystem ts;
	
	@Before
	public void setUp() {
		ts = new MockThreadSystem();
	}
	
	@Test
	public void testRetrieve() {
		LockOwnerRetriever<String> retriever = 
			new LockOwnerRetriever<String>(ts, false);
		LockOwner<String> owner = retriever.retrieve();
		assertNotNull(owner);
	}
	
	@Test
	public void testMultipleRetrieve() {
		final LockOwnerRetriever<String> retriever = 
			new LockOwnerRetriever<String>(ts, false);
		final LockOwner<String> ownerOne = retriever.retrieve();
		assertNotNull(ownerOne);
		
		final LockOwner<String> ownerTwo = retriever.retrieve();
		assertNotNull(ownerTwo);
		assertSame(ownerOne, ownerTwo);
	}
}