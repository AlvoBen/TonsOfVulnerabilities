package com.tssap.dtr.client.lib.protocol.util;

/**
 * Helper exception used to write stack traces at the beginning
 * of a request execution. Request stack traces may help to identify
 * the part of an application that issued a certain request.
 * @see RequestBase#perform(IConnection,boolean)
 */
public class RequestLog extends Exception {

	/** Unqiue ID for serialization */
	private static final long serialVersionUID = -8209606834543117432L;

	/**
	 * Constructs a <code>RequestLog</code> with no specified detail message. 
	 */
	public RequestLog() {
		super();
	}

	/**
	 * Constructs an <code>RequestLog</code> with the specified detail message. 
	 * @param   s   the detail message.
	 */
	public RequestLog(String s) {
		super(s);
	}

}
