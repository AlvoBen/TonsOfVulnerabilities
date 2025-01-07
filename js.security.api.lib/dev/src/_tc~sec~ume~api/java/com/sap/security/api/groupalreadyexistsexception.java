package com.sap.security.api;

/**
 * This exception indicates an attempt to create a group which already exists
 * in the data store.
 * @author <a href=mailto:lambert.boskamp@sap.com>Lambert Boskamp</a>
 * @version 1.0
 */
public class GroupAlreadyExistsException
    extends PrincipalAlreadyExistsException {

	private static final long serialVersionUID = -5664604536241089998L;

	/**
	 * Constructs a new GroupAlreadyExistsException
	 * @param   message   the detail message.
	 * @param   nestedException   the root exception.
	 */
	public GroupAlreadyExistsException(
		Throwable nestedException,
		String message) {
		super(nestedException, message);
	}

	/**
	 * Constructs a new GroupAlreadyExistsException
	 * @param   nestedException   the root exception.
	 */
	public GroupAlreadyExistsException(Throwable nestedException) {
		super(nestedException);
	}

	/**
	 * Constructs a new GroupAlreadyExistsException
	 * @param   message   the detail message.
	 */
	public GroupAlreadyExistsException(String message) {
		super(message);
	}

	/**
	 * Constructs a new GroupAlreadyExistsException
	 */
	public GroupAlreadyExistsException() {
		super();
	}
}
