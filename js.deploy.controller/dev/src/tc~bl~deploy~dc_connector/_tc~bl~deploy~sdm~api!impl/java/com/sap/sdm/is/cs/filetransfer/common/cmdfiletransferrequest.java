package com.sap.sdm.is.cs.filetransfer.common;

import com.sap.sdm.is.cs.cmd.CmdIF;

/**
 * 
 * Title: Software Deployment Manager
 * 
 * Description: This command is send when a file has to be transfered to the
 * server. The result of this command has to be a command from type
 * <code>CmdFileTransferResponse</code>.
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date 2003-11-18
 * 
 * @author dimitar-d
 * @version 1.0
 * @since 6.40
 * 
 */
public class CmdFileTransferRequest implements CmdIF {

	public static final String NAME = "FileTransferRequest";

	private String absoluteFilePath;
	private String fileName;
	private long fileSize;

	public CmdFileTransferRequest(String absoluteFilePath, String fileName,
			long fileSize) {
		this.absoluteFilePath = absoluteFilePath;
		this.fileName = fileName;
		setFileSize(fileSize);
	}

	public String getMyName() {
		return NAME;
	}

	public String getAbsoluteFilePath() {
		return absoluteFilePath;
	}

	public void setAbsoluteFilePath(String absoluteFilePath) {
		this.absoluteFilePath = absoluteFilePath;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public long getFileSize() {
		return fileSize;
	}

}
