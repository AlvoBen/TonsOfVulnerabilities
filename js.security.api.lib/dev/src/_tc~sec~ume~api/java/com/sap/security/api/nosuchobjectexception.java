package com.sap.security.api;

/**
 * This exception indicates an attempt to access a non-existing object in the
 * object store.
 * @author <a href=mailto:lambert.boskamp@sap.com>Lambert Boskamp</a>
 * @version 1.0
 */
public class NoSuchObjectException
    extends NoSuchPrincipalException {

	private static final long serialVersionUID = -5981689137858062847L;

/**
 * Constructs a new NoSuchUserExcption.
 */
    public NoSuchObjectException () {
    }

	public NoSuchObjectException (Throwable reason) {
		super(reason);
	}


/**
 * Constructs a new NoSuchObjectException with a descriptive <code>message</code>.
 */
    public NoSuchObjectException (String message) {
        super(message);
    }
}
