package com.sap.sdm.is.cs.filetransfer.common;

import com.sap.sdm.is.cs.cmd.CmdIF;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date 2003-11-19
 * 
 * @author dimitar-d
 * @version 1.0
 * @since 6.40
 * 
 */
public class CmdFileTransferResponse implements CmdIF {

	public static final String NAME = "FileTransferResponse";

	private String remoteFilePath;
	private long fileSize;

	public CmdFileTransferResponse(String remoteFilePath, long fileSize) {
		this.remoteFilePath = remoteFilePath;
		setFileSize(fileSize);
	}

	public String getMyName() {
		return NAME;
	}

	public String getRemoteFilePath() {
		return remoteFilePath;
	}

	public void setRemoteFilePath(String remoteFilePath) {
		this.remoteFilePath = remoteFilePath;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public long getFileSize() {
		return fileSize;
	}

}
