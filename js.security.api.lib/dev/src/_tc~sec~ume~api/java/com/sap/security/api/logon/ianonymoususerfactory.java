package com.sap.security.api.logon;

import com.sap.security.api.IUser;

/**
 * Anonymous User Factory provides the retrieving of anonymous users.
 * @author Guenther Wannenmacher
 * @version 1.1
 */
public interface IAnonymousUserFactory {
    /**
     * Gets the anonymous user. If anonymous user mode is set, the default user
     * is returned and if no default user is set, the first user of the guest user's
     * unique id is returned.
     *
     * @return anonymous user or <code>null</code> if anonymous user mode is set off.
     */
    public IUser getAnonymousUser() throws com.sap.security.api.UMException;

    /**
     * Gets a named anonymous user. This user must be defined in the configuration
     * property ume.login.guest_user.uniqueids.
     * 
     * This user is not authenticated.
     *
     * @param logonID the logon id of a guest user.
     * @return anonymous user with the given logon id or <code>null</code>
     * if anonymous user mode is set off.
     */
    public IUser getAnonymousUser(String logonID) throws com.sap.security.api.UMException;

    /**
     * Checks a named anonymous user. This user must be defined in the configuration
     * property ume.login.guest_user.uniqueids.
     *
     * @param logonID the logon id of a guest user.
     * @return <code>true</code> if the user is in the list or <code>false</code> if not
     */
    public boolean isAnonymousUser(String logonID);
}