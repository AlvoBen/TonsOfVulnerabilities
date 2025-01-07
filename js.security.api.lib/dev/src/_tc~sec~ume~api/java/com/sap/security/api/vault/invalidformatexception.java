package com.sap.security.api.vault;


/*
 *
 * @author Kai Ullrich
 */

public class InvalidFormatException extends java.lang.Exception {

	private static final long serialVersionUID = 5857269560828642821L;
	/**
	 * Constructs a <code>InvalidFormatException</code> with
	 * <code>null</code> as its error detail message.
	 */
	public InvalidFormatException() {
	    super();
	}
	/**
	 * Constructs a <code>InvalidFormatException</code> with the
	 * specified detail message.
	 *
	 * @param   s   the detail message.
	 */
	public InvalidFormatException(String s) {
	    super(s);
	}
}
