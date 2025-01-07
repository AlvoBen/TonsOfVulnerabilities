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

package com.sap.engine.services.deploy.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.tc.logging.Location;

/**
 * Utility class for serialization and de-serialization of objects, which is 
 * common for deploy service and deploy controller.
 * 
 * @author I031258
 * @version 7.1
 */
// TODO: to be moved to js.deploy.lib
public class ObjectSerializer {
	
	private static final Location location = 
		Location.getLocation(ObjectSerializer.class);
	

	/**
	 * Serialize an object to byte array. 
	 * @param obj the object to be serialized. Must not be null.
	 * @return not null array of bytes.
	 * @throws IOException
	 */
	public static byte[] getByteArray(Object obj) throws IOException {
		assert obj != null;
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final ObjectOutputStream oos = new ObjectOutputStream(baos);
		try {
			oos.writeObject(obj);
			return baos.toByteArray();
		} finally {
			close(oos);
			// Closing of outer stream will close also and the inner one.
		}
	}

	/**
	 * De-serialize an object from byte array.   
	 * @param arr the byte array to be de-serialized. Must not be null. 
	 * @param offset
	 * @param length
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object getObject(byte[] arr, int offset, int length)
		throws IOException, ClassNotFoundException {
		assert arr != null;
		return getObject(new ByteArrayInputStream(arr, offset, length));
		// Closing of ByteArrayInputStream has no effect.		
	}

	/**
	 * De-serialize an object from an input stream.
	 * @param is the input stream to be de-serialized. Must not be null.
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object getObject(InputStream is) 
		throws IOException, ClassNotFoundException {
		assert is != null;

		// No need to close the object input stream, because 
		// the underlying input stream will be closed by the caller.
		return new ObjectInputStream(is).readObject();
	}

	/**
	 * Reads an input stream as byte array.
	 * @param is the input stream. Not null.
	 * @return read bytes. Cannot be null.
	 * @throws IOException
	 */
	public static byte[] read(InputStream is) throws IOException {
		assert is != null;

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int read = 0;
		while ((read = is.read(buffer)) != -1) {
			baos.write(buffer, 0, read);
		}
		return baos.toByteArray();
		// Closing of ByteArrayInputStream has no effect.
	}

	/**
	 * Close an input stream, without to throw IOException.
	 * @param in the input stream to be closed. Can be null.
	 */
	public static void close(InputStream is) {
		try {
			if (is != null) {
				is.close();
			}
		} catch (IOException ioe) {
			final ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
				new String[] { "closing a stream." }, ioe);
			DSLog.logErrorThrowable(location, sde);
		}
	}

	private static void close(OutputStream out) {
		try {
			out.close();
		} catch (IOException ioe) {// $JL-EXC$
		}
	}
}