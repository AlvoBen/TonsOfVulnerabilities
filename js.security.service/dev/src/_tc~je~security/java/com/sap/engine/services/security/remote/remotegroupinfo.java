package com.sap.engine.services.security.remote;

import java.rmi.*;

public interface RemoteGroupInfo
  extends Remote {

  /**
   * Get the name of the group.
   *
   * @return  the name of the group
   */
  public String getName() throws RemoteException;


  /**
   * Get the parent groups of this group.
   *
   * @return  the names of the parent groups of this group.
   */
  public RemoteIterator getParentGroups() throws RemoteException;


  /**
   * Get the child groups of this group.
   *
   * @return  the names of the child groups of this group.
   */
  public RemoteIterator getChildGroups() throws RemoteException;


  /**
   * Get the users in this group.
   *
   * @return  the names of the users, which belong to this group
   */
  public RemoteIterator getUsersInGroup() throws RemoteException;

}

