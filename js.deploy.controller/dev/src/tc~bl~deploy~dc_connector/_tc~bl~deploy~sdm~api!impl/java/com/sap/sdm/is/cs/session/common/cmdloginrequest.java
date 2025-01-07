package com.sap.sdm.is.cs.session.common;

import com.sap.sdm.is.cs.cmd.CmdIF;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public class CmdLoginRequest implements CmdIF {

	public final static String NAME = "LoginRequest";

	private String role = null;
	private String password = null;
	private String description = null;
	private String guiVersion = null;
	private boolean hashed = false;
	private boolean pwExceptionWanted = false;

	public CmdLoginRequest(String guiVersion, String role, String password,
			boolean hashed, String description, boolean pwExceptionWanted) {
		this.guiVersion = guiVersion;
		this.role = role;
		this.password = password;
		this.hashed = hashed;
		this.description = description;
		this.pwExceptionWanted = pwExceptionWanted;
	}

	public String getMyName() {
		return NAME;
	}

	public String getRole() {
		return this.role;
	}

	public String getPassword() {
		return this.password;
	}

	public String getDescription() {
		return this.description;
	}

	public String getGuiVersion() {
		return this.guiVersion;
	}

	/**
	 * Returns the hashed.
	 * 
	 * @return boolean
	 */
	public boolean isHashed() {
		return this.hashed;
	}

	public boolean isPasswordExceptionWanted() {
		return this.pwExceptionWanted;
	}

}
