package com.sap.security.api;

/**
 * This class allows to register for user mangagement events.
 * IUserListener follows the observer pattern. An observer has to register
 * via registerForEvent. An observer can subscribe more than one time to an event
 * by using different namespaces.
 *
 * Copyright (c) SAP Portals Europe GmbH 2001
 * @author Alexander Primbs
 * @version $Revision: #1 $ <BR>
 */

public interface UserAccountListener
{
    public static final int INFORM_ON_USERACCOUNT_ADDED    = 0x01;
    public static final int INFORM_ON_USERACCOUNT_REMOVED  = 0x02;
    public static final int INFORM_ON_USERACCOUNT_LOGOUT   = 0x04;

  // ----------------------------------
  // Methods used for receiving events -----------------------------------------
  // ----------------------------------
  /**
   * userAccountAdded() is called if event INFORM_ON_USERACCOUNT_ADDED is fired from
   * registered factory
   * @param uniqueIdOfAccount name of the newly added user account
   * @throws UMException
   */
  public void userAccountAdded(String uniqueIdOfAccount) throws UMException;
  
  /**
   * userAccountRemoved() is called if event INFORM_ON_USERACCOUNT_REMOVED is fired from
   * registered factory
   * @param uniqueIdOfAccount name of the removed user account
   * @throws UMException
   */
  public void userAccountRemoved(String uniqueIdOfAccount) throws UMException;

  /**
   * userAccountLogOut() is called if event INFORM_ON_USERACCOUNT_LOGOUT is fired from
   * registered factory
   * @param uniqueIdOfAccount name of the user account who logged out
   * @throws UMException
   */
  public void userAccountLogOut(String uniqueIdOfAccount) throws UMException;

}
