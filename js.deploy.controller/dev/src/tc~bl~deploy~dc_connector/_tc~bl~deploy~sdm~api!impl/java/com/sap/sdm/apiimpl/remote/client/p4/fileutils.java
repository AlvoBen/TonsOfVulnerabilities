package com.sap.sdm.apiimpl.remote.client.p4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-12-5
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class FileUtils {

	private FileUtils() {
	}

	static String[] getFileLines(String listFilePath) throws IOException {
		if (listFilePath == null) {
			throw new NullPointerException(
					"The specified Deploy Controller API log file path is null.");
		}

		final File listFile = new File(listFilePath);
		checkFile(listFile, "Deploy Controller API log file");

		final FileReader listFileReader = new FileReader(listFile);
		final BufferedReader bufferedReader = new BufferedReader(listFileReader);

		final Collection result = new ArrayList();

		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			line = line.trim();
			if (line.length() < 1) {
				continue;
			}

			result.add(line);
		}

		return (String[]) result.toArray(new String[result.size()]);
	}

	private static void checkFile(File file, String fileDescr) {
		if (!file.exists()) {
			throw new IllegalArgumentException("The specified " + fileDescr
					+ " does not exist.");
		}

		if (!file.canRead()) {
			throw new IllegalArgumentException("The specified " + fileDescr
					+ " could not be read.");
		}
	}

}
