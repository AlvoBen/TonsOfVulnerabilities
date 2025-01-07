package com.sap.engine.services.deploy.server.utils.concurrent;

public class LockSetNotAcquiredException extends Exception {
	private static final long serialVersionUID = 1L;

	public LockSetNotAcquiredException(final String msg) {
		super(msg);
	}
}