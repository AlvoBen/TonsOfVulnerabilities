package com.sap.security.api;

/***
 * This exception indicates an attempt to access a non-existing role in the
 * role store.
***/
public class RoleAlreadyExistsException extends PrincipalAlreadyExistsException {

	private static final long serialVersionUID = 873176843643879013L;

	/**
	 * Constructs a new RoleAlreadyExistsException
	 * @param   message   the detail message.
	 * @param   nestedException   the root exception.
	 */
	public RoleAlreadyExistsException(
		Throwable nestedException,
		String message) {
		super(nestedException, message);
	}

	/**
	 * Constructs a new RoleAlreadyExistsException
	 * @param   nestedException   the root exception.
	 */
	public RoleAlreadyExistsException(Throwable nestedException) {
		super(nestedException);
	}

	/**
	 * Constructs a new RoleAlreadyExistsException
	 * @param   message   the detail message.
	 */
	public RoleAlreadyExistsException(String message) {
		super(message);
	}

	/**
	 * Constructs a new RoleAlreadyExistsException
	 */
	public RoleAlreadyExistsException() {
		super();
	}
}
