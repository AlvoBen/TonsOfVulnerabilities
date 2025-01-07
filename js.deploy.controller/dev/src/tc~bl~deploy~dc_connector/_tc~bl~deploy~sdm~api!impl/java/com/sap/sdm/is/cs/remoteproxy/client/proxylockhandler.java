package com.sap.sdm.is.cs.remoteproxy.client;

/**
 * 
 * Title: Software Deployment Manager
 * 
 * Description: The purpose of the class this to handle the lock object, which
 * is used by the {@link InvocationHandler#invoke invoke} implementation. The
 * lock has to be per client session in order synchronization to be a correct
 * one.
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date 2003-11-28
 * 
 * @author dimitar-d
 * @version 1.0
 * @since 6.40
 * 
 */
public interface ProxyLockHandler {

	/**
	 * @return <code>Object</code> which will be used for proxy synchronization.
	 */
	public Object getLock();

}
