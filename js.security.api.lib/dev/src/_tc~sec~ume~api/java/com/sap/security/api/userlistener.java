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

public interface UserListener
{
    public static final int INFORM_ON_USER_ADDED    = 0x01;
    public static final int INFORM_ON_USER_REMOVED  = 0x02;

  // ----------------------------------
  // Methods used for receiving events -----------------------------------------
  // ----------------------------------
  /**
   * userAdded() is called if event INFORM_ON_USER_ADDED is fired from
   * registered factory
   * @param uniqueIdOfUser uniqueID of the newly added user
   * @throws UMException
   */  
  public void userAdded(String uniqueIdOfUser) throws UMException;
  
  /**
   * userRemoved() is called if event INFORM_ON_USER_REMOVED is fired from
   * registered factory
   * @param uniqueIdOfUser uniqueID of the removed user
   * @throws UMException
   */  
  public void userRemoved(String uniqueIdOfUser) throws UMException;

}
