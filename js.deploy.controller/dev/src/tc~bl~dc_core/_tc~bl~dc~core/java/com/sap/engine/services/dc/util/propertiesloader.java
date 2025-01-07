package com.sap.engine.services.dc.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-8
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class PropertiesLoader {

	private PropertiesLoader() {
	}

	public static Properties load(String propsPath) throws IOException {
		final Properties props = new Properties();
		InputStream is = null;
		try {
			final InputStream resInputStream = PropertiesLoader.class
					.getClassLoader().getResourceAsStream(propsPath);
			if (resInputStream == null) {
				new IOException(
						"The system could not get input stream from the "
								+ "specified resource '" + propsPath + "'.");
			}

			is = new BufferedInputStream(resInputStream);

			props.load(is);

			return props;
		} finally {
			close(is);
		}
	}

	public static Properties loadFromFile(String propsPath) throws IOException {
		final Properties props = new Properties();
		InputStream is = null;
		try {
			final InputStream resInputStream = new FileInputStream(propsPath);
			is = new BufferedInputStream(resInputStream);

			props.load(is);

			return props;
		} finally {
			close(is);
		}
	}

	private static void close(InputStream is) throws IOException {
		if (is != null) {
			is.close();
		}
	}

}
