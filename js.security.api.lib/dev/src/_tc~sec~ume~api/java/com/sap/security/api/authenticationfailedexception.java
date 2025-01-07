package com.sap.security.api;

/**
 * This exception indicates an exception which can happen during an authentication process
 * @author  Rajeev Madnawat
 */

public class AuthenticationFailedException extends UMException {

	private static final long serialVersionUID = 9013800687787370437L;
	
	/**
	 * Constructs a <code>UserManagementException</code> with
	 * <code>null</code> as its error detail message.
	 */
	public AuthenticationFailedException() {
	    super();
	}
	/**
	 * Constructs a <code>UserManagementException</code> with the
	 * specified detail <code>message</code>.
	 *
	 * @param   s   the detail message.
	 */
	public AuthenticationFailedException(String s) {
	    super(s);
	}

	public AuthenticationFailedException(Throwable reason) {
		super(reason);
	}

	public AuthenticationFailedException(Throwable reason, String s) {
		super(reason,s);
	}

}
