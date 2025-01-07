package com.sap.security.api;

/**
 * This exception indicates an attempt to access a non-existing user in the
 * user store.
 * @author <a href=mailto:lambert.boskamp@sap.com>Lambert Boskamp</a>
 * @version 1.0
 */
public class NoSuchUserException
    extends NoSuchPrincipalException {

	private static final long serialVersionUID = -3380783063549255183L;
/**
 * Constructs a new NoSuchUserExcption.
 */
    public NoSuchUserException () {
    }

	public NoSuchUserException (Throwable reason) {
		super(reason);
	}

/**
 * Constructs a new NoSuchUserException with a descriptive <code>message</code>.
 */
    public NoSuchUserException (String message) {
        super(message);
    }

	public NoSuchUserException (Throwable reason,String message) {
		super(reason,message);
	}

}
