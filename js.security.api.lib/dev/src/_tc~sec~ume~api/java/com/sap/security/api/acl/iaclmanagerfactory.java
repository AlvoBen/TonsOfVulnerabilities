package com.sap.security.api.acl;

public interface IAclManagerFactory {
	
	/**
	 * Gets an application specific Access Control List (ACL) Manager.
	 * @return IAclManager object used for handling Access Control Lists
	 * For further details check com.sap.security.api.acl.IAclManager
	 */
	public IAclManager getAclManager(String applicationId);
	
	/**
	 * Returns an array of all used Access Control List (ACL) Managers.
	 * @return String[]   applicationIDs of used ACL managers
	 */
	public String[] getAllAclManagers();
	
}
