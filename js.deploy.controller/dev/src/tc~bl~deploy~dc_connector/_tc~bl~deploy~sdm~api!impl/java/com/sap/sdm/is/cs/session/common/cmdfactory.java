package com.sap.sdm.is.cs.session.common;

/**
 * @author Christian Gabrisch 11.08.2003
 */
public final class CmdFactory {
	private CmdFactory() {
	}

	public static CmdLoginAccepted createCmdLoginAccepted(String sessionID,
			String apiServerVersion) {
		return new CmdLoginAccepted(sessionID, apiServerVersion);
	}

	public static CmdReopenConnectionAccepted createCmdReopenConnectionAccepted(
			String sessionID) {
		return new CmdReopenConnectionAccepted(sessionID);
	}

	public static CmdCloseSessionAccepted createCmdCloseSessionAccepted(
			String sessionID) {
		return new CmdCloseSessionAccepted(sessionID);
	}

	public static CmdCloseConnection createCmdCloseConnection(String sessionID) {
		return new CmdCloseConnection(sessionID);
	}

	public static CmdCloseSession createCmdCloseSession(String sessionID) {
		return new CmdCloseSession(sessionID);
	}

	public static CmdReopenConnection createCmdReopenConnection(String sessionID) {
		return new CmdReopenConnection(sessionID);
	}

	public static CmdLoginRequest createCmdLoginRequest(String guiVersion,
			String role, String password, boolean hashed, String description,
			boolean pwExceptionWanted) {
		return new CmdLoginRequest(guiVersion, role, password, hashed,
				description, pwExceptionWanted);
	}
}
