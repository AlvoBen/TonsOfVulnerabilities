package com.sap.security.api;


/**
 * This exception indicates an attempt to logon with an invalid password.
 * @author  William Li
 */

public class InvalidPasswordException extends UMException {

	private static final long serialVersionUID = -9116565271366591003L;
	
	/**
	 * Constructs a <code>InvalidPasswordException</code>
	 */
	public InvalidPasswordException() {
	    super();
	}
	/**
	 * Constructs a <code>InvalidPasswordException</code> with the
	 * specified detail message.
	 *
	 * @param   s   the detail message.
	 */
	public InvalidPasswordException(String s) {
	    super(s);
	}
}
