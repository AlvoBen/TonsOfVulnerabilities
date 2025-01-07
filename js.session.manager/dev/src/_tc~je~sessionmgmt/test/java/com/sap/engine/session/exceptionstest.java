package com.sap.engine.session;

import org.junit.Test;

public class ExceptionsTest {

	final String E = "exception";
	final Throwable T = new Throwable();

	@Test
	public void testCreateException() throws Exception {
		new CreateException();
		new CreateException(E);
		new CreateException(E, T);
		new CreateException(T);
	}

	@Test
	public void testDomainExistException() throws Exception {
		new DomainExistException();
		new DomainExistException(E);
		new DomainExistException(E, T);
		new DomainExistException(T);
	}

	@Test
	public void testSessionConfigException() throws Exception {
		new SessionConfigException();
		new SessionConfigException(E);
		new SessionConfigException(E, T);
		new SessionConfigException(T);
	}

	@Test
	public void testSessionException() throws Exception {
		new SessionException();
		new SessionException(E);
		new SessionException(E, T);
		new SessionException(T);
	}

	@Test
	public void testSessionExistException() throws Exception {
		new SessionExistException();
		new SessionExistException(E);
		new SessionExistException(E, T);
		new SessionExistException(T);
	}

	@Test
	public void testSessionNotFoundException() throws Exception {
		new SessionNotFoundException();
		new SessionNotFoundException(E);
		new SessionNotFoundException(E, T);
		new SessionNotFoundException(T);
	}

}
