/* 
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.deploy.sda;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.sap.engine.lib.deploy.sda.constants.Constants;
import com.sap.engine.lib.deploy.sda.logger.Logger;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * Provides a basic functionality to check whether a file is a sdu and to get
 * the Sap Manifest data
 * 
 * @author Radoslav Popov
 */
public class SDUChecker implements Constants {
	private static final String SAP_MANIFEST_CANONICAL_PATH = "META-INF/SAP_MANIFEST.MF";
	private static final Location loc = Location.getLocation(SDUChecker.class);

	/**
	 * Checks whether the file is a sdu
	 * 
	 * @return
	 * @throws IOException
	 */
	public static boolean check(String file) throws IOException {
		Logger.trace(loc, Severity.DEBUG, "Checking file " + file);
		return (getSapManifestZipEntryName(file) == null ? false : true);
	}

	/**
	 * Returns the Sap Manifest entry if any, null on failure
	 * 
	 * @return entry name
	 * @throws IOException
	 */
	public static String getSapManifestZipEntryName(String file)
			throws IOException {
		if (!(new File(file).exists())) {
			throw new FileNotFoundException("'" + file + "' does not exist!");
		}

		final FileInputStream fis = new FileInputStream(file);
		ZipInputStream zis = null;
		try {
			zis = new ZipInputStream(fis);
			ZipEntry entry = null;
			while ((entry = zis.getNextEntry()) != null) {
				if (entry.getName().equalsIgnoreCase(
						SAP_MANIFEST_CANONICAL_PATH)) {
					Logger.trace(loc, Severity.DEBUG,
							"the Sap Manifest is found.");
					return entry.getName();
				}
			}
		} finally {
			if (zis != null) {
				zis.close();
			}
			if (fis != null) {
				fis.close();
			}
		}
		Logger.trace(loc, Severity.DEBUG, "the Sap Manifest is not found.");
		return null;
	}

}
