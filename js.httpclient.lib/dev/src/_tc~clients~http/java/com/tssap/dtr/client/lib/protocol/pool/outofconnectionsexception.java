package com.tssap.dtr.client.lib.protocol.pool;

/**
 * Thrown when a ConnectionFactory cannot return a connection matching
 * a requested connection template. All available instances of a given
 * connection template are currently in use.
 *
 */
public class OutOfConnectionsException extends Exception {
	
	/** Unqiue ID for serialization */
	private static final long serialVersionUID = 3378984079447746425L;

	/**
	 * Create a new OutOfConnections exception without a message.
	 */
	public OutOfConnectionsException() {
		super();
	}

	/**
	 * Create a new OutOfConnections exception.
	 * @param message - the error or warning message.
	 */
	public OutOfConnectionsException(String message) {
		super(message);
	}
}