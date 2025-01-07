package com.sap.engine.services.dc.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-29
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public class StreamUtils {

	public static String getStringFromInputStream(InputStream is)
			throws IOException {
		final int bufferSize = 65536;
		final char[] buff = new char[bufferSize];
		int readChars = -1;
		final BufferedInputStream bis = new BufferedInputStream(is, bufferSize);
		final BufferedReader in = new BufferedReader(new InputStreamReader(bis));
		StringBuffer sb = new StringBuffer();
		while ((readChars = in.read(buff)) != -1) {
			final String str = new String(buff);
			sb.append(str);
		}

		return sb.toString();
	}

	public static byte[] getByteArrayFromInputStream(InputStream is)
			throws IOException {
		return getByteArrayFromInputStream(is, false);
	}

	public static byte[] getByteArrayFromInputStream(InputStream is,
			boolean closeStream) throws IOException {
		BufferedInputStream bis = null;
		ByteArrayOutputStream bos = null;
		try {
			final int bufferSize = 65536;
			final byte[] buff = new byte[bufferSize];
			int readBytes = -1;
			bis = new BufferedInputStream(is, bufferSize);
			bos = new ByteArrayOutputStream(bufferSize);

			while ((readBytes = bis.read(buff)) != -1) {
				bos.write(buff, 0, readBytes);
			}
			bos.flush();
			return bos.toByteArray();
		} finally {
			if (closeStream) {
				close(bis);
			}
			close(bos);
		}
	}

	private static void close(InputStream is) throws IOException {
		if (is != null) {
			is.close();
		}
	}

	private static void close(OutputStream os) throws IOException {
		if (os != null) {
			os.close();
		}
	}

}
