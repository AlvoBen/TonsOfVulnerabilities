/*
 * Created on Jul 5, 2004
 *
 */
package com.sap.jmx.provider;


/**
 * @author Jasen Minov
 *
 */
public class ProviderException extends Exception {

	/**
	 * Constructor
	 * 
	 * @param resourceId	Error message
	 */
	public ProviderException(String resourceId) {
	  super(resourceId);
	}

	/**
	 * Constructor
	 * 
	 * @param cause	Nested exception
	 */
	public ProviderException(Throwable cause) {
	  super(cause);

	}

	public ProviderException(String resourceId, Object[] args, Throwable cause) {
 //	  super(resourceId, args, cause);
 	}
 
 	public ProviderException(String resourceId, Throwable cause) {
 	  this(resourceId, null, cause);
 	}
  
  	public ProviderException(String resourceId, Object[] args) {
  	  this(resourceId, args, null);
  	}
  
}
