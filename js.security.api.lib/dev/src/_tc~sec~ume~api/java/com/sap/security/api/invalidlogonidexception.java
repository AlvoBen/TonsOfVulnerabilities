package com.sap.security.api;

/**
 * This exception indicates an invalid logon id.
 */

public class InvalidLogonIdException extends UMException {

	private static final long serialVersionUID = 5030429269773881936L;
	
	/**
	 * Constructs a <code>InvalidLogonIdException</code>
	 */
	public InvalidLogonIdException() {
		super();
	}
	/**
	 * Constructs a <code>InvalidLogonIdException</code> with the
	 * specified detail message.
	 *
	 * @param   s   the detail message.
	 */
	public InvalidLogonIdException(String s) {
		super(s);
	}
}
