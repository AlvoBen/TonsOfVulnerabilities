package com.sap.engine.services.deploy.server.remote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;


public class MessageResponseTest {
	@SuppressWarnings("boxing")
    @Test
	public void testCreation() {
		String[] warnings = {"warning", "warning"};
		String[] errors = {"error", "error"};
		Object response = new Object();
		MessageResponse mr = new MessageResponse(1, warnings, errors, response);
		assertNotNull(mr);
		assertSame(response, mr.getResponse());
		String[] warnings2 = mr.getWarnings();
		assertNotNull(warnings2);
		assertEquals(1, warnings2.length);
		assertEquals("warning", warnings2[0]);
		String[] errors2 = mr.getErrors();
		assertNotNull(errors2);
		assertEquals(1, errors2.length);
		assertEquals("error", errors2[0]);
	}
}
