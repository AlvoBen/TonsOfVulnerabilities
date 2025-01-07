package com.tssap.dtr.client.lib.protocol.templates;

/**
 * Thrown when a ConnectionFactory cannot return a connection matching
 * a requested connection template due to an invalid template id.
 */
public class InvalidTemplateIDException extends TemplateException {
	
	/** Unqiue ID for serialization */
	private static final long serialVersionUID = -4008260989520244001L;

	/**
	 * Create a new OutOfConnections exception without a message.
	 */
	public InvalidTemplateIDException() {
		super();
	}

	/**
	 * Create a new OutOfConnections exception.
	 * @param message - the error or warning message.
	 */
	public InvalidTemplateIDException(String message) {
		super(message);
	}
}
