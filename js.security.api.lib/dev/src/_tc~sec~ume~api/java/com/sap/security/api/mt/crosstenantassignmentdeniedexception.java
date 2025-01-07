package com.sap.security.api.mt;

/**
 * Exception that indicates that the assignment between two tenant principals is denied
 */
public class CrossTenantAssignmentDeniedException
	extends CrossTenantException {

	private static final long serialVersionUID = 7370302396153181402L;
	/**
	 * @param nestedException
	 * @param message
	 */
	public CrossTenantAssignmentDeniedException(
		Throwable nestedException,
		String message) {
		super(nestedException, message);
	}

	/**
	 * @param nestedException
	 */
	public CrossTenantAssignmentDeniedException(Throwable nestedException) {
		super(nestedException);
	}

	/**
	 * @param message
	 */
	public CrossTenantAssignmentDeniedException(String message) {
		super(message);
	}

	/**
	 * 
	 */
	public CrossTenantAssignmentDeniedException() {
		super();
	}

}
