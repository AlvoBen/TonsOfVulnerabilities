package com.sap.security.api;

/**
 * This exception indicates an attempt to create a object who already exists
 * in the object store.
 * @author <a href=mailto:lambert.boskamp@sap.com>Lambert Boskamp</a>
 * @version 1.0
 */
public class ObjectAlreadyExistsException
    extends UMException {

	private static final long serialVersionUID = 1839159249155962951L;
/**
 * Constructs a new ObjectAlreadyExistsException with a descriptive <code>message</code>.
 */
    public ObjectAlreadyExistsException () {
    }

	public ObjectAlreadyExistsException (Throwable reason) {
		super(reason);
	}

/**
 * Constructs a new ObjectAlreadyExistsException with a descriptive <code>message</code>.
 */
    public ObjectAlreadyExistsException (String message) {
        super(message);
    }
}
