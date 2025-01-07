/*
 * Created on 2005-4-11
 *
 *@author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature.crypto;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *
 */
public abstract class Reusable {
  
  public static final String LIMITED_CRYPTO_POLICY_MESSAGE = "This key is not allowed due to the crypto policy file in use. Using Unlimited Strength Jurisdiction Policy Files will fix the problem (Note: 989517).";
	//filled by the configurator
	// Factory static methods for new instance
	public static Hashtable newInstanceUris = new Hashtable();
	// 
	public static Hashtable releaseMethods = new Hashtable();

	static final Object[] NO_ARGS = new Object[0];
	static {
		ReusableConfigurator.readConfiguration();
	}
	private String uri = null;

	public static Reusable getInstance(String uri) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method newInstance = (Method) newInstanceUris.get(uri);
		if (newInstance == null)
			throw new IllegalArgumentException("No cipher for algorithm " + uri + " could be resolved");
		Reusable ret = (Reusable) newInstance.invoke(null, NO_ARGS);
		ret.uri = uri;
		return ret;
	}

	public void release() {
		Method newInstance = (Method) releaseMethods.get(uri);
		try {
			newInstance.invoke(this, NO_ARGS);
		} catch (Exception e) {
			// $JL-EXC$
		}
	}

	public String getUri() {
		return uri;
	}

	public abstract Object getInternal();

}
