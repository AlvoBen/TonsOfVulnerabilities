package com.sap.security.api;

/**
 * This class allows to register for user mangagement events.
 * PrincipalListener follows the observer pattern. An observer has to register
 * via
 * {@link com.sap.security.api.IPrincipalFactory#registerListener(PrincipalListener,int)}.
 * @author Alexander Primbs
 * @version $Revision: #1 $ <BR>
 */

public interface PrincipalListener
{
/***
 * Constant used for the event that a principal was created
 ***/	
    public static final int INFORM_ON_OBJECT_ADDED    = 0x01;
/***
 * Constant used for the event that a principal was removed
 ***/	
    public static final int INFORM_ON_OBJECT_REMOVED  = 0x02;
/***
 * Constant used for the event that a principal was changed
 ***/	
    public static final int INFORM_ON_OBJECT_EDITED   = 0x04;
/***
 * Constant used for the event that a principal was assigned
 ***/	
    public static final int INFORM_ON_OBJECT_ASSIGNED   = 0x10;
/***
 * Constant used for the event that a principal was unassigned
 ***/	
    public static final int INFORM_ON_OBJECT_UNASSIGNED  = 0x20;
    

  // ----------------------------------
  // Methods used for receiving events -----------------------------------------
  // ----------------------------------
  /**
   * objectAdded() is called if event INFORM_ON_OBJECT_ADDED is fired from the
   * registering factory
   * @param uniqueID of the newly added principal
   * @throws UMException
   */
  public void objectAdded(String uniqueID) throws UMException;
  
  /**
   * objectRemoved() is called if event INFORM_ON_OBJECT_REMOVED is fired from the
   * registering factory
   * @param uniqueID of the newly removed principal
   * @throws UMException
   */
  public void objectRemoved(String uniqueID) throws UMException;

  /**
   * objectEdited() is called if event INFORM_ON_OBJECT_EDITED is fired from the
   * registering factory
   * @param uniqueID of the newly changed principal
   * @throws UMException
   */
  public void objectEdited(String uniqueID) throws UMException;

  /**
   * objectAssigned() is called if event INFORM_ON_OBJECT_ASSIGNED is fired from the
   * registering factory
   * @param uniqueID of the IPrincipalSet to which a principal was assigned
   * @param assignedPrincipalID contains the uniqueID of the principal which
   * was assigned to IPrincipalSet
   * @throws UMException
   */
  public void objectAssigned(String uniqueID, String assignedPrincipalID) throws UMException;

  /**
   * objectUnAssigned() is called if event INFORM_ON_OBJECT_UNASSIGNED is fired from the
   * registering factory
   * @param uniqueID of the IPrincipalSet from which a principal was unassigned
   * @param unassignedPrincipalID contains the uniqueID of the principal which
   * was unassigned from IPrincipalSet
   * @throws UMException
   */
  public void objectUnAssigned(String uniqueID, String unassignedPrincipalID) throws UMException;


}
