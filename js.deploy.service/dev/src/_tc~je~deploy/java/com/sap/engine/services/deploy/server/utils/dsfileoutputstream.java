/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This class only checks if the file given in the constructors file name is
 * less then 255 chars. The check is performed only on windows Operation
 * Systems. If canonical paths are bugger then 255 chars FileNotFoundException
 * with detailed message is thrown.
 * 
 * @author Luchesar Cekov
 */
public class DSFileOutputStream extends FileOutputStream {
	public static final short ABSOLUTE_FILE_PATH_MAX_LENGTH = 255;

	public DSFileOutputStream(File aFile, boolean append) throws IOException {
		super(checkFileName(aFile), append);
	}

	public DSFileOutputStream(File aFile) throws IOException {
		this(aFile, false);
	}

	public DSFileOutputStream(String aName, boolean append) throws IOException {
		super(checkFileName(aName), append);
	}

	public DSFileOutputStream(String aName) throws IOException {
		this(aName, false);
	}

	private static String checkFileNameLength(String fileName)
			throws FileNotFoundException {

		if (System.getProperty("os.name").toUpperCase().indexOf("WIN") > -1
				&& fileName.length() > ABSOLUTE_FILE_PATH_MAX_LENGTH) {
			throw new FileNotFoundException(
					"ASJ.dpl_ds.006098 File path \"" + fileName
							+ "\" too long. File path should be less then "
							+ ABSOLUTE_FILE_PATH_MAX_LENGTH + " characters!");
		}
		return fileName;
	}

	private static File checkFileName(File file) throws IOException {
		checkFileNameLength(file.getCanonicalPath());
		return file;
	}

	private static String checkFileName(String fileName) throws IOException {
		checkFileName(new File(fileName));
		return fileName;
	}
}
