package com.sap.security.api.srvUser;

import com.sap.security.api.IConfigurable;
import com.sap.security.api.IUser;
import com.sap.security.api.UMException;
/**
 * This class provides access to service users
 *
 * Copyright (c) SAP Portals Europe GmbH 2001
 * @author Alexander Primbs
 * @version $Revision: #1 $ <BR>
 */

public interface IServiceUserFactory extends IConfigurable
{

    public static final String SERVICEUSER_NAMESPACE   = "$serviceUser$";
    public static final String SERVICEUSER_ATTRIBUTE   = "SERVICEUSER_ATTRIBUTE";
    public static final String SERVICEUSER_VALUE       = "IS_SERVICEUSER";

/**
 * Get a service user by uniqueName
 * @param uniqueName uniqueName of user
 * @param bCreateTicket if set to true a ticket is created, if false no ticket is created
 * @exception UMException
 */
    public IUser getServiceUser(String uniqueName, boolean bCreateTicket) throws UMException;

/**
 * Get a service user by uniqueName
 * @param uniqueName uniqueName of user
 * calling this method will automatically create a ticket
 * @exception UMException
 */
    public IUser getServiceUser (String uniqueName) throws UMException ;

/**
 * Creates a new, initially blank service user object with the given uniqueName.
 * The user object is automatically committed to the user store.
 * @exception UserAlreadyExistsException if a user with the given uniqueName already
 * exists.
 * @exception InvalidIDException if <code>uniqueName</code> doesn't meet the user
 * store's requirements (e.g. its too long)
 */
    public void createServiceUser (String uniqueName) throws UMException;

/**
 * Delete a user from the use store
 * @exception UMException if the user can't be deleted
 * @exception NoSuchUserException if the user does not exist
 */
    public void deleteUser (String uniqueID) throws UMException;

/**
 * Check if user is a service user
 */
    public boolean isServiceUser(String uniqueIdOfUser) throws UMException;
    
/**
 * Check if user is a service user
 */
	public boolean isServiceUser(IUser user) throws UMException;    
}