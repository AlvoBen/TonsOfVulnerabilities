package com.sap.sdm.is.cs.cmd;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public class CmdError implements CmdIF {
	public final static String NAME = "Error";

	private String errorText = null;
	private boolean isWrongPasswordSupplied = false;
	private Exception exception = null;

	CmdError(String errorText) {
		this(errorText, false, null);
	}

	CmdError(String errorText, boolean wrongPasswordSupplied) {
		this(errorText, wrongPasswordSupplied, null);
	}

	CmdError(String errorText, boolean wrongPasswordSupplied, Exception exc) {
		this.errorText = errorText;
		this.exception = exc;
		this.isWrongPasswordSupplied = wrongPasswordSupplied;
	}

	public String getMyName() {
		return NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.client_server.api.common.CmdError#getErrorText()
	 */
	public String getErrorText() {
		return this.errorText;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.client_server.api.common.CmdError#isWrongPasswordSupplied()
	 */
	public boolean isWrongPasswordSupplied() {
		return this.isWrongPasswordSupplied;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.client_server.api.common.CmdError#getException()
	 */
	public Exception getException() {
		return this.exception;
	}

}
