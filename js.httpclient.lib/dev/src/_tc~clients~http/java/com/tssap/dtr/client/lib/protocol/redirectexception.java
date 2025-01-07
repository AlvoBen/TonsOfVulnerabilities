package com.tssap.dtr.client.lib.protocol;

/**
 * 
 */
public class RedirectException extends HTTPException {
	
	public RedirectException() {
		super();
	}

	public RedirectException(String message) {
		super(message);
	}

	public RedirectException(Throwable cause) {
		super(cause);
	}

	public RedirectException(String message, Throwable cause) {
		super(message, cause);
	}

}
