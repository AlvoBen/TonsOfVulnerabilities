package com.sap.security.api.mt;

import com.sap.security.api.UMException;

/**
 * This exception is the superclass of all tenant exceptions
 */
public class CrossTenantException extends UMException {

	private static final long serialVersionUID = -3176547064805668167L;
	/**
	 * @param nestedException
	 * @param message
	 */
	public CrossTenantException(Throwable nestedException, String message) {
		super(nestedException, message);
	}

	/**
	 * @param nestedException
	 */
	public CrossTenantException(Throwable nestedException) {
		super(nestedException);
	}

	/**
	 * @param message
	 */
	public CrossTenantException(String message) {
		super(message);
	}

	/**
	 * 
	 */
	public CrossTenantException() {
		super();
	}

}
