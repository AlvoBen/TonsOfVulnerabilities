package com.sap.security.api;

/**
 * This exception indicates an attempt to read an object by a key that
 * is ambiguous.
 * 
 * @author D032841
 * @version 1.0
 */
public class DuplicateKeyException extends UMException {

	private static final long serialVersionUID = 8110675653139283772L;
	
	/**
	 * Constructs a new DuplicateKeyException with a descriptive <code>message</code>.
	 */
	    public DuplicateKeyException () {
	    }

		public DuplicateKeyException (Throwable reason) {
			super(reason);
		}

		public DuplicateKeyException (Throwable reason, String message) {
			super(reason, message);
		}
		
	/**
	 * Constructs a new DuplicateKeyException with a descriptive <code>message</code>.
	 */
	    public DuplicateKeyException (String message) {
	        super(message);
	    }
	
	
}
