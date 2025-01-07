package com.sap.security.api;

/**
 * This class allows to register for user mangagement events of principals of type IGroup.
 * Interface GroupListener follows the observer pattern. An observer has to register
 * via {@link com.sap.security.api.IGroupFactory#registerListener(GroupListener, int)};
 * @author Alexander Primbs
 * @version $Revision: #1 $ <BR>
 */

public interface GroupListener
{
/***
 * Constant used for the event that a group was created
 ***/
    public static final int INFORM_ON_GROUP_ADDED       = 0x01;
/***
 * Constant used for the event that a group was removec
 ***/
    public static final int INFORM_ON_GROUP_REMOVED     = 0x02;

    // assignment events
/***
 * Constant used for the event that a user was added to a group
 ***/
    public static final int INFORM_ON_USER_ASSIGNED     = 0x04;
/***
 * Constant used for the event that a group was assigned as member to a group
 ***/
    public static final int INFORM_ON_GROUP_ASSIGNED    = 0x08;
/***
 * Constant used for the event that a user was removed from a group
 ***/    
    public static final int INFORM_ON_USER_UNASSIGNED   = 0x10;
/***
 * Constant used for the event that a group was unassigned as member of a group
 ***/
    public static final int INFORM_ON_GROUP_UNASSIGNED  = 0x20;


  // ----------------------------------
  // Methods used for receiving events -----------------------------------------
  // ----------------------------------
  /**
   * groupAdded() is called if event INFORM_ON_GROUP_ADDED is fired from the
   * registering factory
   * @param uniqueIdOfGroup name of the newly added group
   * @throws UMException
   */
  public void groupAdded(String uniqueIdOfGroup) throws UMException;

  /**
   * groupRemoved() is called if event INFORM_ON_GROUP_REMOVED is fired from the
   * registering factory
   * @param uniqueIdOfGroup name of the newly removed group
   * @throws UMException
   */
  public void groupRemoved(String uniqueIdOfGroup) throws UMException;

  /**
   * userAssigned() is called if event INFORM_ON_USER_ASSIGNED is fired from the
   * registering factory
   * @param uniqueIdOfGroup name of the group
   * @param uniqueIdOfUser name of the user who was assigned to the group
   * @throws UMException
   */
  public void userAssigned(String uniqueIdOfGroup, String uniqueIdOfUser) throws UMException;
  
  /**
   * userUnAssigned() is called if event INFORM_ON_USER_UNASSIGNED is fired from the
   * registering factory
   * @param uniqueIdOfGroup name of the group
   * @param uniqueIdOfUser name of the user who was removed from the group
   * @throws UMException
   */
  public void userUnAssigned(String uniqueIdOfGroup, String uniqueIdOfUser) throws UMException;

  /**
   * groupAssigned() is called if event INFORM_ON_GROUP_ASSIGNED is fired from the
   * registering factory
   * @param uniqueIdOfGroup name of the parent group
   * @param assignedUniqueIdOfGroup name of the group which was assigned to the parent group
   * @throws UMException
   */  
  public void groupAssigned(String uniqueIdOfGroup, String assignedUniqueIdOfGroup) throws UMException;
  
  /**
   * groupUnAssigned() is called if event INFORM_ON_GROUP_UNASSIGNED is fired from the
   * registering factory
   * @param uniqueIdOfGroup name of the parent group
   * @param unAssignedUniqueIdOfGroup name of the group which was removed as a member of the parent group
   * @throws UMException
   */  
  public void groupUnAssigned(String uniqueIdOfGroup, String unAssignedUniqueIdOfGroup) throws UMException;

}
