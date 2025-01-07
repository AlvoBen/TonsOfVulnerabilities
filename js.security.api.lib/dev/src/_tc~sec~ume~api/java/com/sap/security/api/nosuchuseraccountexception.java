package com.sap.security.api;

/**
 * This exception indicates an attempt to access a non-existing user in the
 * user store.
 * @author <a href=mailto:lambert.boskamp@sap.com>Lambert Boskamp</a>
 * @version 1.0
 */

public class NoSuchUserAccountException
    extends NoSuchPrincipalException {

	private static final long serialVersionUID = -1314097111649601883L;

/**
 * Constructs a new NoSuchUserAccountException.
 */
    public NoSuchUserAccountException () {
    }

	public NoSuchUserAccountException (Throwable reason) {
		super(reason);
	}

/**
 * Constructs a new NoSuchUserAccountException with a descriptive <code>message</code>.
 */
    public NoSuchUserAccountException (String message) {
        super(message);
    }

	public NoSuchUserAccountException (Throwable reason,String message) {
		super(reason,message);
	}
}
