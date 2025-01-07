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
package com.sap.engine.services.deploy.ear.jar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Assia Djambazova
 */
public class FileFromPath {

	public static File getFileFromClassPath(String relativePath, File tmpDir) throws IOException {
		File file = new File(tmpDir, relativePath);
		file.getParentFile().mkdirs();
		InputStream input = EarReaderCleanTest.class.getClassLoader()
				.getResourceAsStream(relativePath);
		try {
			FileOutputStream output = new FileOutputStream(file);
			try {
				byte[] buff = new byte[512];
				for (int count = -1; (count = input.read(buff)) > 0;) {
					output.write(buff, 0, count);
				}
			} finally {
				output.close();
			}
		} finally {
			input.close();
		}

		return file;
	}
	

}
