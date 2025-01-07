package com.sap.security.api.mt;

/**
 * Exception that indicates that the access on a tenant principal is denied
 */
public class CrossTenantAccessDeniedException extends CrossTenantException {

	private static final long serialVersionUID = 5162544793516257193L;
	
	/**
	 * @param nestedException
	 * @param message
	 */
	public CrossTenantAccessDeniedException(
		Throwable nestedException,
		String message) {
		super(nestedException, message);
	}

	/**
	 * @param nestedException
	 */
	public CrossTenantAccessDeniedException(Throwable nestedException) {
		super(nestedException);
	}

	/**
	 * @param message
	 */
	public CrossTenantAccessDeniedException(String message) {
		super(message);
	}

	/**
	 * 
	 */
	public CrossTenantAccessDeniedException() {
		super();
	}

}
