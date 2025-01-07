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

public interface RoleListener
{
    public static final int INFORM_ON_ROLE_ADDED    = 0x01;
    public static final int INFORM_ON_ROLE_REMOVED  = 0x02;

    // assignment events
    public static final int INFORM_ON_USER_ASSIGNED     = 0x04;
    public static final int INFORM_ON_GROUP_ASSIGNED    = 0x08;
    public static final int INFORM_ON_USER_UNASSIGNED   = 0x10;
    public static final int INFORM_ON_GROUP_UNASSIGNED  = 0x20;

  // ----------------------------------
  // Methods used for receiving events -----------------------------------------
  // ----------------------------------
  /**
   * roleAdded() is called if event INFORM_ON_ROLE_ADDED is fired from
   * registered factory
   * @param uniqueIdOfRole name of the newly added role
   * @throws UMException
   */
  public void roleAdded(String uniqueIdOfRole) throws UMException;
  /**
   * roleRemoved() is called if event INFORM_ON_ROLE_REMOVED is fired from
   * registered factory
   * @param uniqueIdOfRole name of the newly added role
   * @throws UMException
   */
  public void roleRemoved(String uniqueIdOfRole) throws UMException;

  /**
   * userAssigned() is called if event INFORM_ON_USER_ASSIGNED is fired from the
   * registering factory
   * @param uniqueIdOfRole name of the role
   * @param uniqueIdOfUser name of the user who was assigned to the role
   * @throws UMException
   */
  public void userAssigned(String uniqueIdOfRole, String uniqueIdOfUser) throws UMException;

  /**
   * userUnAssigned() is called if event INFORM_ON_USER_UNASSIGNED is fired from the
   * registering factory
   * @param uniqueIdOfRole name of the role
   * @param uniqueIdOfUser name of the user who was unassigned from the role
   * @throws UMException
   */  
  public void userUnAssigned(String uniqueIdOfRole, String uniqueIdOfUser) throws UMException;
  
  /**
   * groupAssigned() is called if event INFORM_ON_GROUP_ASSIGNED is fired from the
   * registering factory
   * @param uniqueIdOfRole name of the role
   * @param assignedUniqueIdOfGroup name of the group which was assigned to the role
   * @throws UMException
   */  
  public void groupAssigned(String uniqueIdOfRole, String assignedUniqueIdOfGroup) throws UMException;

  /**
   * groupUnAssigned() is called if event INFORM_ON_GROUP_UNASSIGNED is fired from the
   * registering factory
   * @param uniqueIdOfRole name of the role
   * @param unassignedUniqueIdOfGroup name of the group which was unassigned from the role
   * @throws UMException
   */    
  public void groupUnAssigned(String uniqueIdOfRole, String unassignedUniqueIdOfGroup) throws UMException;

}
