package com.sap.engine.services.dc.manage;

import com.sap.engine.services.dc.util.FileUtils;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-4-7
 * 
 * @author dimitar, anton
 * @version 1.0
 * @since 6.40
 * 
 */
public final class PathsConfigurer {

	private static final PathsConfigurer INSTANCE = new PathsConfigurer();

	private String uploadDirName = "archives";
	private String storageDirName = "storage";

	private PathsConfigurer() {
	}

	public static PathsConfigurer getInstance() {
		return INSTANCE;
	}

	public void setUploadDirName(String uploadDirName) {
		this.uploadDirName = removeLastSlash(uploadDirName);
	}

	public String getUploadDirName(String sessionId) {
		return FileUtils.concatDirs(uploadDirName, sessionId);
	}

	public String getUploadDirNameParent() {
		return uploadDirName;
	}

	public void setStorageDirName(String string) {
		storageDirName = removeLastSlash(string);
	}

	public String getStorageDirName(String sessionId) {
		return FileUtils.concatDirs(storageDirName, sessionId);
	}

	// Removes the last symbol, if it is slash or back slash.
	private String removeLastSlash(String dirName) {
		if (dirName != null) {
			if (dirName.endsWith("/") || dirName.endsWith("\\")) {
				return removeLastSlash(dirName.substring(0,
						dirName.length() - 1));
			}
			return dirName;
		}
		return dirName;
	}

}
