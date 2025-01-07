package com.sap.engine.services.dc.util;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-6
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class UniqueKeyGenerator {

	private final static UniqueKeyGenerator INSTANCE = new UniqueKeyGenerator();

	public static UniqueKeyGenerator getInstance() {
		return INSTANCE;
	}

	private UniqueKeyGenerator() {
	}

	public String generate() {
		return "";
	}

}

/*
 * Without enetring in the specifics (you can fully check out the pattern by
 * reading the appropriate chapter), the solution is to generate a 32 digit key,
 * encoded in hexadecimal composed as follows:
 * 
 * 1: Unique down to the millisecond. Digits 1-8 are are the hex encoded lower
 * 32 bits of the System.currentTimeMillis() call.
 * 
 * 2: Unique across a cluster. Digits 9-16 are the encoded representation of the
 * 32 bit integer of the underlying IP address.
 * 
 * 3: Unique down to the object in a JVM. Digits 17-24 are the hex
 * representation of the call to System.identityHashCode(), which is guaranteed
 * to return distinct integers for distinct objects within a JVM.
 * 
 * 4: Unique within an object within a millisecond. Finally digits 25-32
 * represent a random 32 bit integer generated on every method call using the
 * cryptographically strong java.security.SecureRandom class.
 */
