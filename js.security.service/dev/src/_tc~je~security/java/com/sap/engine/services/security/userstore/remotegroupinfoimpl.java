package com.sap.engine.services.security.userstore;

import com.sap.engine.interfaces.security.userstore.context.GroupInfo;
import com.sap.engine.services.security.remote.*;
import java.rmi.RemoteException;

public class RemoteGroupInfoImpl extends javax.rmi.PortableRemoteObject implements RemoteGroupInfo {

  private GroupInfo info = null;

  public RemoteGroupInfoImpl(GroupInfo info) throws RemoteException {
    this.info = info;
  }

  /**
   * Get the name of the group.
   *
   * @return  the name of the group
   */
  public String getName() {
    try {
      return info.getName();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Get the parent groups of this group.
   *
   * @return  the names of the parent groups of this group.
   */
  public RemoteIterator getParentGroups() throws RemoteException {
    return new RemoteIteratorImpl(info.getParentGroups());
  }

  /**
   * Get the child groups of this group.
   *
   * @return  the names of the child groups of this group.
   */
  public RemoteIterator getChildGroups() throws RemoteException {
    return new RemoteIteratorImpl(info.getChildGroups());
  }

  /**
   * Get the users in this group.
   *
   * @return  the names of the users, which belong to this group
   */
  public RemoteIterator getUsersInGroup() throws RemoteException {
    return new RemoteIteratorImpl(info.getUsersInGroup());
  }

}

