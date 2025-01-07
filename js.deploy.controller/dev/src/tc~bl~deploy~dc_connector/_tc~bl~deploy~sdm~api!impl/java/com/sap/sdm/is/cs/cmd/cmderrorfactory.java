package com.sap.sdm.is.cs.cmd;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public final class CmdErrorFactory {
	private CmdErrorFactory() {
	}

	public static CmdError createCmdError(String errorText) {
		return new CmdError(errorText);
	}

	public static CmdError createCmdError(String errorText,
			boolean wrongPasswordSupplied) {
		return new CmdError(errorText, wrongPasswordSupplied);
	}

	public static CmdError createCmdError(String errorText,
			boolean wrongPasswordSupplied, Exception exc) {
		return new CmdError(errorText, wrongPasswordSupplied, exc);
	}
}
