package com.sap.sdm.is.cs.filetransfer.client;

/**
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 * 
 */
public class FileTransferResult {

	private String errorText = null;
	private String remoteFileName = null;
	private boolean isOK = false;

	FileTransferResult(String errorText, String remoteFileName, boolean isOK) {
		this.errorText = errorText;
		this.remoteFileName = remoteFileName;
		this.isOK = isOK;
	}

	public String getErrorText() {
		return this.errorText;
	}

	public String getRemoteFileName() {
		return this.remoteFileName;
	}

	public boolean isOK() {
		return this.isOK;
	}

}
