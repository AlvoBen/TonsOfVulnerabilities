package com.sap.security.api;

/***
 * This exception indicates an attempt to access a non-existing principal in the
 * data store.
***/
public class PrincipalAlreadyExistsException extends UMException {

	private static final long serialVersionUID = 1732136352243659084L;
	
	/**
	 * Constructs a new PrincipalAlreadyExistsException
	 * @param   message   the detail message.
	 * @param   nestedException   the root exception.
	 */
	public PrincipalAlreadyExistsException(
		Throwable nestedException,
		String message) {
		super(nestedException, message);
	}

	/**
	 * Constructs a new PrincipalAlreadyExistsException
	 * @param   nestedException   the root exception.
	 */
	public PrincipalAlreadyExistsException(Throwable nestedException) {
		super(nestedException);
	}

	/**
	 * Constructs a new PrincipalAlreadyExistsException
	 * @param   message   the detail message.
	 */
	public PrincipalAlreadyExistsException(String message) {
		super(message);
	}

	/**
	 * Constructs a new PrincipalAlreadyExistsException
	 */
	public PrincipalAlreadyExistsException() {
		super();
	}
}
