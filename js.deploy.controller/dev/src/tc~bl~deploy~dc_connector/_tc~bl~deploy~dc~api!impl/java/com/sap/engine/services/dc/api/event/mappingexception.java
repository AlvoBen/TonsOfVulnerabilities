package com.sap.engine.services.dc.api.event;

/**
 * Thrown when there is a problem mapping remote to local events
 * 
 * @author I040924
 * 
 */
public class MappingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MappingException(String message) {
		super(message);
	}

}
