/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.jndi;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.Binding;
import javax.naming.directory.DirContext;

import com.sap.engine.frame.state.ManagementListener;
import com.sap.engine.services.jndi.cluster.SecurityBase;

import java.rmi.RemoteException;

import com.sap.engine.services.jndi.implserver.ServerContextImpl;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * @author Petio Petev
 * @version 4.00
 */
public class PermissionAdministrator implements JNDIManagementInterface {

	private final static Location LOG_LOCATION = Location.getLocation(PermissionAdministrator.class);

  public PermissionAdministrator() throws java.rmi.RemoteException {
  }

  public synchronized Object getPermissions() throws java.rmi.RemoteException {
    try {
      return SecurityBase.getPrincipals();
    } catch (Exception e) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception while trying to get the current naming permissions.", e);
      }
      RemoteException re = new RemoteException("Exception while trying to get the current naming permissions.", e);
      throw re;
    }
  }

  public synchronized Object getPermissions(String permissionName) throws java.rmi.RemoteException {
    try {
      return SecurityBase.getPrincipals(permissionName);
    } catch (Exception e) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception while trying to get naming permission with name " + permissionName + ".", e);
      }
      RemoteException re = new RemoteException("Exception while trying to get naming permission with name " + permissionName + ".", e);
      throw re;
    }
  }

  public void addPermission(String userName, String permissionName, boolean isGroup) throws java.rmi.RemoteException {
//    try {
//      SecurityBase.allowOperation(userName, permissionName, isGroup);
//    } catch (Exception e) {
//      RemoteException re = null;
//      if (isGroup) {
//        if (JNDIFrame.log.toLogInfoInLocation()) {
//          JNDIFrame.log.logInfo(RemoteException.CANNOT_ADD_PERMISSION_TO_GROUP, new Object[] {permissionName, userName});
//          JNDIFrame.log.logCatching(e);
//        }
//        re = new RemoteException(RemoteException.CANNOT_ADD_PERMISSION_TO_GROUP, new Object[] {permissionName, userName}, e);
//      } else {
//        if (JNDIFrame.log.toLogInfoInLocation()) {
//          JNDIFrame.log.logInfo(RemoteException.CANNOT_ADD_PERMISSION_TO_USER, new Object[] {permissionName, userName});
//          JNDIFrame.log.logCatching(e);
//        }
//        re = new RemoteException(RemoteException.CANNOT_ADD_PERMISSION_TO_USER, new Object[] {permissionName, userName}, e);
//      }
//      throw re;
//    }
  }

  public void removePermission(String userName, String permissionName, boolean isGroup) throws java.rmi.RemoteException {
//    try {
//      SecurityBase.denyOperation(userName, permissionName, isGroup);
//    } catch (Exception e) {
//      RemoteException re = null;
//      if (isGroup) {
//        if (JNDIFrame.log.toLogInfoInLocation()) {
//          JNDIFrame.log.logInfo(RemoteException.CANNOT_REMOVE_PERMISSION_FROM_GROUP, new Object[] {permissionName, userName});
//          JNDIFrame.log.logCatching(e);
//        }
//        re = new RemoteException(RemoteException.CANNOT_REMOVE_PERMISSION_FROM_GROUP, new Object[] {permissionName, userName}, e);
//      } else {
//        if (JNDIFrame.log.toLogInfoInLocation()) {
//          JNDIFrame.log.logInfo(RemoteException.CANNOT_REMOVE_PERMISSION_FROM_USER, new Object[] {permissionName, userName});
//          JNDIFrame.log.logCatching(e);
//        }
//        re = new RemoteException(RemoteException.CANNOT_REMOVE_PERMISSION_FROM_USER, new Object[] {permissionName, userName}, e);
//      }
//      throw re;
//    }
  }

  public Object listAllBindings() throws java.rmi.RemoteException {
    try {
      Context ctx = new InitialContext();
      DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode("root", true);
      initTree(treeNode, ctx);
      return treeNode;
    } catch (Exception e) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Cannot list the bindings of the naming system.", e);
      }
      RemoteException re = new RemoteException("Cannot list the bindings of the naming system.", e);
      throw re;
    }
  }

  public Object getUsersTree() throws java.rmi.RemoteException {
    return null;
  }

  private void initTree(DefaultMutableTreeNode treeNode, Context ctx) throws javax.naming.NamingException {
    NamingEnumeration nenum = ctx.listBindings("");
    Binding binding = null;
    String name = null;
    String className = null;
    String objectValue = null;
    Object o = null;
    boolean isContext = false;
    DefaultMutableTreeNode newNode = null;

    while (nenum.hasMoreElements()) {
      try {
        binding = (Binding) nenum.nextElement();
      } catch (Exception e) {
                LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
      }

      if (binding != null) {
        name = binding.getName();
        className = binding.getClassName();
        objectValue = "";
        o = binding.getObject();
        isContext = o != null ? (o instanceof javax.naming.Context) : false;
        newNode = null;
        Object objref = null;

        if (isContext) {
          newNode = new DefaultMutableTreeNode(name + "   [Context]", isContext);
        } else {
          if (o != null) {
            if (o instanceof java.io.Serializable) {
              objectValue = o.toString().replace('\n', ' ');
            } else {
              objectValue = "NON Serializable Object";
            }
          } else {
            objectValue = "null";
          }
          newNode = new DefaultMutableTreeNode(name, true);
          insertInfo(newNode, className, objectValue);
        }
        insertSorted(treeNode, newNode, isContext);

        if (isContext) {
          try {
            objref = ctx.lookup(name);
          } catch (Exception e) {
                        LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
          }
          if (objref != null && objref instanceof DirContext) {
            initTree(newNode, (Context) objref);
          }
        }
      }
    }
  }

  private static void insertSorted(DefaultMutableTreeNode parent, DefaultMutableTreeNode child, boolean isContext) {
    int index = 0;
    int childCount = parent.getChildCount();
    DefaultMutableTreeNode nextNode = null;
    boolean nextIsContext = false;
    Object userObject = null;

    while (index < childCount) {
      nextNode = (DefaultMutableTreeNode) parent.getChildAt(index);
      userObject = nextNode.getUserObject();
      if (userObject instanceof String) {
        nextIsContext = ((String) userObject).endsWith("[Context]");
      } else {
        nextIsContext = nextNode.getAllowsChildren();
      }

      if (isContext == nextIsContext) {
        if (child.toString().compareToIgnoreCase(nextNode.toString()) < 0) {
          break;
        }
      }

      if (!isContext && nextIsContext) {
        break;
      }

      index++;
    }

    parent.insert(child, index);
  }

  private static void insertInfo(DefaultMutableTreeNode child, String className, String objectValue) {
    DefaultMutableTreeNode newNode = new DefaultMutableTreeNode("[Class Name] : " + className, false);
    child.insert(newNode, 0);
    newNode = new DefaultMutableTreeNode("[Object Value] : " + objectValue, false);
    child.insert(newNode, 1);
  }


  public void registerManagementListener(ManagementListener managementListener) {
    /* @todo registerManagmentListener */
  }

  public int getByteArrayCacheSize() {
    return 0;
  }

  public int getBoundObjectsCount() {
    return ServerContextImpl.getNumberOfBindings();
  }


}

