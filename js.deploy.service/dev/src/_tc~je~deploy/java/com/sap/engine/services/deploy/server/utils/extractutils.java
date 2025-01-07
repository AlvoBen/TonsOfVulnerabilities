/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server.utils;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

import com.sap.engine.lib.jar.JarExtractor;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class ExtractUtils {

	/**
	 * Extracts the given archive file in an unique sub directory of the given
	 * one
	 * 
	 * @param archive
	 *            The absolute path to the archive
	 * @param parentDir
	 *            A directory
	 * @return The absolute path of the directory, where the archive file has
	 *         been extracted.
	 * @throws IOException
	 */
	public static String extractZip(File archive, File parentDir)
			throws IOException {
		final String extractDir = FSUtils.getUniqueDir(parentDir
				.getAbsolutePath(), archive.getName());

		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(archive);
		} catch (IOException ioex) {
			// $JL-EXC$ - check is this file a zip file?
			return extractDir;
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException io) {
					// $JL-EXC$
				}
			}
		}

		(new JarExtractor()).extractJar(archive.getAbsolutePath(), extractDir);
		return extractDir;
	}

}
