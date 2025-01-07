package com.sap.security.api;

/**
 * This exception indicates an attempt to access a non-existing PCD role object in the
 * object store.
 */
public class NoSuchPCDRoleException extends UMRuntimeException {

	private static final long serialVersionUID = -4560676119186521743L;
/**
 * Constructs a new NoSuchPCDRoleException with a descriptive <code>message</code>.
 */
	public NoSuchPCDRoleException (Throwable exception) {
		super(exception);
	}

    public NoSuchPCDRoleException (String message) {
        super(message);
    }
}
