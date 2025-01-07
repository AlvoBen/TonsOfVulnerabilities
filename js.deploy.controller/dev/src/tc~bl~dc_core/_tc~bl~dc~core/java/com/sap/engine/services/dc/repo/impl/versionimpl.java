package com.sap.engine.services.dc.repo.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.StringTokenizer;

import com.sap.engine.services.dc.repo.Version;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-4-27
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public class VersionImpl implements Version {

	private static final long serialVersionUID = 3068971264498113757L;

	private final static String DOT_STRING = ".";

	private final String versionAsString;

	// for backward compatibility in serialization
	private String[] versionStringTokens = null;
	/**
	 * A representation of <code>numericalStrings</code> as
	 * <code>BigInteger</code>s without trailing zeroes. This data structure
	 * enables a more straightforward implementation of some methods.
	 */
	private final BigInteger[] numStringsAsBigInts;

	public VersionImpl(String versionAsString) {
		if (versionAsString == null) {
			throw new NullPointerException(
					"[ERROR CODE DPL.DC.3427] The version could not be null.");
		} else if (versionAsString.trim().equals("")) {
			throw new IllegalArgumentException(
					"[ERROR CODE DPL.DC.3428] The version could not be an empty string.");
		}

		this.versionAsString = versionAsString;
		this.numStringsAsBigInts = toBigIntegersNoTrailingZeroes(getStringTokens(versionAsString));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.Version#getVersionAsString()
	 */
	public String getVersionAsString() {
		return this.versionAsString;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.Version#getNumStringsAsBigInts()
	 */
	public BigInteger[] getNumStringsAsBigInts() {
		return this.numStringsAsBigInts;
	}

	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}

		if (this == other) {
			return true;
		}

		if (!(other instanceof Version)) {
			return false;
		}

		final Version verObj = (Version) other;

		return Arrays.equals(numStringsAsBigInts, verObj
				.getNumStringsAsBigInts());
	}

	public int hashCode() {
		return this.versionAsString.hashCode();
	}

	public String toString() {
		return this.getVersionAsString();
	}

	private String[] getStringTokens(String versionString) {
		final StringTokenizer tok = new StringTokenizer(versionString,
				DOT_STRING);
		final String[] result = new String[tok.countTokens()];
		for (int i = 0; i < result.length; i++) {
			result[i] = tok.nextToken();
		}

		return result;
	}

	private BigInteger[] toBigIntegersNoTrailingZeroes(String[] numStrings) {
		return removeTrailingZeroes(toBigIntegers(numStrings));
	}

	private BigInteger[] toBigIntegers(String[] numStrings) {
		final BigInteger[] result = new BigInteger[numStrings.length];
		for (int i = 0; i < numStrings.length; i++) {
			result[i] = new BigInteger(numStrings[i]);
		}

		return result;
	}

	private BigInteger[] removeTrailingZeroes(BigInteger[] bigInts) {
		final int lengthNoTrailingZeroes = Math.max(0,
				getIndexLastNonZero(bigInts)) + 1;

		final BigInteger[] result = new BigInteger[lengthNoTrailingZeroes];
		System.arraycopy(bigInts, 0, result, 0, lengthNoTrailingZeroes);

		return result;
	}

	private int getIndexLastNonZero(BigInteger[] bigInts) {
		for (int i = bigInts.length - 1; i >= 0; i--) {
			if (bigInts[i].compareTo(BigInteger.ZERO) != 0) {
				return i;
			}
		}

		return -1;
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		this.versionStringTokens = null;
	}

}
