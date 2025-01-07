package com.sap.engine.services.deploy.server.utils.concurrent;

public class ConflictingOperationLockException extends Exception {
	private static final long serialVersionUID = 1L;
	public ConflictingOperationLockException(final String msg) {
		super(msg);
	}
}