package com.sap.security.api;

/**
 * This exception indicates an attempt to create a persistent object (using
 * a factory) with a unique ID which is not supported by the respective store.
 * For instance, the ID might be too long, or required to be a numeric String.
 * @author <a href=mailto:lambert.boskamp@sap.com>Lambert Boskamp</a>
 * @version 1.0
 */
public class InvalidIDException
    extends UMException {

	private static final long serialVersionUID = 5826346816296599735L;

    /**
     * Constructs a new InvalidIDException.
     */
    public InvalidIDException () {
    }


    /**
     * Constructs a new InvalidIDException with a descriptive <code>message</code>.
     * @param message the detail message.
     */
    public InvalidIDException (String message) {
        super(message);
    }
}
