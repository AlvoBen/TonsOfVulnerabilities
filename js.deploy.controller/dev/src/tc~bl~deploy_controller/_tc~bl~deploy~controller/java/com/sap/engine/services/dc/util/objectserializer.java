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

package com.sap.engine.services.dc.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Utility class for serialization and deserialization of <code>Objects</code>,
 * which is common for deploy service and deploy controller
 * 
 * @author I031258
 * @version 7.1
 */
public class ObjectSerializer {

	public static byte[] getByteArray(Object obj) throws Exception {
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			return baos.toByteArray();
		} catch (Exception ex) {
			throw ex;
		} finally {
			close(oos);
			close(baos);
		}

	}

	public static Object getObject(byte[] obj) throws Exception {
		if (obj == null) {
			return null;
		}
		return getObject(obj, 0, obj.length);
	}

	public static Object getObject(byte[] obj, int offset, int length)
			throws Exception {
		if (obj == null) {
			return null;
		}

		ByteArrayInputStream bis = null;
		try {
			bis = new ByteArrayInputStream(obj, offset, length);
			return getObject(bis);
		} catch (Exception ex) {
			throw ex;
		} finally {
			close(bis);
		}
	}

	public static Object getObject(InputStream is) throws Exception {
		if (is == null) {
			return null;
		}

		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(is);
			return ois.readObject();
		} catch (Exception ex) {
			throw ex;
		} finally {
			close(ois);
		}
	}

	private static void close(InputStream iStream) {
		try {
			if (iStream != null) {
				iStream.close();
			}
		} catch (IOException ioe) {// $JL-EXC$
		}
	}

	private static void close(OutputStream oStream) {
		try {
			if (oStream != null) {
				oStream.close();
			}
		} catch (IOException ioe) {// $JL-EXC$
		}
	}
}
