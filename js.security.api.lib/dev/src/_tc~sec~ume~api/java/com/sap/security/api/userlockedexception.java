package com.sap.security.api;


/**
 * This exception indicates that a user account is locked in the 
 * user account store.
 * @author  Rajeev Madnawat
 */

public class UserLockedException extends UMException {

	private static final long serialVersionUID = 4268849206983202523L;
	
	/**
	 * Constructs a <code>UserManagementException</code>.
	 */
	public UserLockedException() {
	    super();
	}

	public UserLockedException(Throwable reason) {
		super(reason);
	}
	/**
	 * Constructs a <code>UserManagementException</code> with the
	 * specified detail message.
	 *
	 * @param   s   the detail message.
	 */
	public UserLockedException(String s) {
	    super(s);
	}
}
