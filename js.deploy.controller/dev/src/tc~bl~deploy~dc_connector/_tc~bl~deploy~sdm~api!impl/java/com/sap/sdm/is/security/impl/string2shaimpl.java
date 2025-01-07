package com.sap.sdm.is.security.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.sap.sdm.is.security.Base64;
import com.sap.sdm.is.security.String2SHA;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
class String2SHAImpl implements String2SHA {

	private String text = null;

	/**
	 * Encrypts a String using the SHA-1 algorithm. The resulting byte array is
	 * converted to a String using the Base64 encoding.
	 */
	String2SHAImpl(String text) {
		if (null == text)
			throw new NullPointerException(
					"Constructor of String2SHAImpl was called with illegal argument: null");
		this.text = text;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.client_server.api.client.APIClientString2SHA#getSHAString()
	 */
	public String getSHAString() {
		byte[] buf = this.text.getBytes();

		MessageDigest algorithm = null;
		try {
			algorithm = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e);
		}
		algorithm.reset();
		algorithm.update(buf);
		byte[] digest = algorithm.digest();

		String result = null;
		result = Base64.encode(digest);
		return result;

	}

}
