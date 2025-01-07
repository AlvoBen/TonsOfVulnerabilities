package com.sap.engine.services.dc.util;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import com.sap.sl.util.jarsl.api.JarSLFactory;
import com.sap.sl.util.jarsl.api.JarSLIF;
import com.sap.engine.lib.io.FileUtils;


/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-26
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public final class JarUtils {

	private JarUtils() {
		// To prevent the instantiation.
	}

	public static void extractArchive(String archiveFilePathName,
		String extractDirName) {
		FileUtils.deleteDirectory(new File(extractDirName));

		final JarSLIF jarSL = JarSLFactory.getInstance().createJarSL(
				archiveFilePathName, extractDirName);

		jarSL.extract();
	}

	public static InputStream getJarEntryInputStream(
			String archiveFilePathName, String entryName) {
		final JarSLIF jarSL = JarSLFactory.getInstance().createJarSL(
				archiveFilePathName, null);

		return jarSL.extractSingleFileAsByteArray(entryName);
	}

	/**
	 * Extracts distinct entry from the archive to the specified location.
	 * 
	 * @param archiveFilePathName
	 *            fully qualified path to the archive
	 * @param entryName
	 *            entry name
	 * @param pathToStore
	 *            fully qualified file name where to extract the entry.
	 * @return null on success otherwise String with collected errors returned
	 *         from the JarSL.extractSingleFile execution.
	 */
	public static String extractEntry(String archiveFilePathName,
			String entryName, String pathToStore) {
		final JarSLIF jarSL = JarSLFactory.getInstance().createJarSL(
				archiveFilePathName, null);
		Vector errors = new Vector();
		boolean ret = jarSL.extractSingleFile(entryName, pathToStore, errors);
		if (ret) {
			return null;
		} else {
			StringBuffer buffer = new StringBuffer();
			for (Iterator iter = errors.iterator(); iter.hasNext();) {
				String element = (String) iter.next();
				buffer.append(element).append(Constants.EOL);
			}
			return buffer.toString();
		}
	}

}
