package com.sap.engine.services.dc.cm.security.authorize;

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
public interface Authorizer {

	/**
	 * Performs an authorization check. If the current user is not authorized an
	 * exception <code>AuthorizationException</code> is thrown.
	 * 
	 * @throws AuthenticationException
	 *             if the login credentials are not valid.
	 */
	public void doAuthorize() throws AuthorizationException;

	/**
	 * Performs an authorization check fro offline. If the current user is not
	 * authorized an exception <code>AuthorizationException</code> is thrown.
	 * 
	 * @throws AuthenticationException
	 *             if the login credentials are not valid.
	 */
	public void isAuthorized4Offline() throws AuthorizationException;

	/**
	 * Returns user unique id if authorized.
	 * 
	 * @return user unique id
	 * @throws AuthenticationException
	 *             if the login credentials are not valid.
	 */
	public String getUserUniqueId() throws AuthorizationException;

}
