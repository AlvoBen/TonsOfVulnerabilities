/**
 * Copyright (c) 2002 by InQMy Software AG.,
 * url: http://www.inqmy.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of InQMy Software AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with InQMy.
 */
package com.sap.engine.services.security.remoteimpl.roles;

import com.sap.engine.services.security.remote.roles.RemoteSecurityRoles;
import com.sap.engine.services.security.roles.SecurityRoleReference;
import com.sap.engine.interfaces.security.SecurityRole;
import com.sap.engine.interfaces.security.SecurityRoleContext;

import javax.rmi.PortableRemoteObject;
import java.rmi.RemoteException;

/**
 *
 *
 * @author Stephan Zlatarev
 * @version 6.30
 */
public class RemoteSecurityRolesImpl extends PortableRemoteObject implements RemoteSecurityRoles {

  private SecurityRoleContext roles = null;

  public RemoteSecurityRolesImpl(SecurityRoleContext roles) throws RemoteException {
    this.roles = roles;
  }

  /**
   *  Adds a security role for the application.
   *
   * @param  securityRole  the name of the security role.
   *
   * @return  the new security role
   */
  public SecurityRole addSecurityRole(String securityRole) throws RemoteException, SecurityException {
    try {
      return roles.addSecurityRole(securityRole);
    } catch (Exception e) {
      throw new RemoteException(e.getMessage());
    }
  }

  /**
   *  Adds a security role for the application.
   *
   * @param  roleName             the name of the new security role.
   * @param  policyConfiguration  the policy configuration that contain the security role.
   * @param  securityRole         the name of the security role in the given policy configuration.
   *
   * @return  the new security role
   */
  public SecurityRole addSecurityRoleReference(String roleName, String policyConfiguration, String securityRole) throws RemoteException, SecurityException {
    try {
      return roles.addSecurityRoleReference(roleName, policyConfiguration, securityRole);
    } catch (Exception e) {
      throw new RemoteException(e.getMessage());
    }
  }

  /**
   *  Returns the security role with the given name
   *
   * @return  a security role instance.
   */
  public SecurityRole getSecurityRole(String securityRole) throws RemoteException, SecurityException {
    try {
      return roles.getSecurityRole(securityRole);
    } catch (Exception e) {
      throw new RemoteException(e.getMessage());
    }
  }


  /**
   *  Lists all registered security roles of this component.
   *
   * @return  list of the registered security roles.
   */
  public SecurityRole[] listSecurityRoles() throws RemoteException, SecurityException {
    try {
      return roles.listSecurityRoles();
    } catch (Exception e) {
      throw new RemoteException(e.getMessage());
    }
  }


  /**
   *  Lists all registered security roles for the specified group.
   *
   * @param  groupName  group name
   *
   * @return  list of the registered security roles.
   */
  public SecurityRole[] listSecurityRolesOfGroup(String groupName) throws RemoteException, SecurityException {
    try {
      return roles.listSecurityRolesOfGroup(groupName);
    } catch (Exception e) {
      throw new RemoteException(e.getMessage());
    }
  }


  /**
   *  Lists all registered security roles for the specified user.
   *
   * @param  userName  user name
   *
   * @return  list of the registered security roles.
   */
  public SecurityRole[] listSecurityRolesOfUser(String userName) throws RemoteException, SecurityException {
    try {
      return roles.listSecurityRolesOfUser(userName);
    } catch (Exception e) {
      throw new RemoteException(e.getMessage());
    }
  }


  /**
   *  Modifies a security role.
   *
   * @param  securityRole  the modified security role.
   */
  public void modifySecurityRole(SecurityRole securityRole) throws RemoteException, SecurityException {
    try {
      SecurityRole role = roles.getSecurityRole(securityRole.getName());

      if (!(role instanceof SecurityRoleReference)) {
        role.setDescription(securityRole.getDescription());

        String[] old_users = role.getUsers();
        String[] old_groups = role.getGroups();
        String[] new_users = securityRole.getUsers();
        String[] new_groups = securityRole.getGroups();

        for (int i = 0; i < new_users.length; i++) {
          if (!exists(new_users[i], old_users)) {
            role.addUser(new_users[i]);
          }
        }

        for (int i = 0; i < new_groups.length; i++) {
          if (!exists(new_groups[i], old_groups)) {
            role.addGroup(new_groups[i]);
          }
        }

        for (int i = 0; i < old_users.length; i++) {
          if (!exists(old_users[i], new_users)) {
            role.removeUser(old_users[i]);
          }
        }

        for (int i = 0; i < old_groups.length; i++) {
          if (!exists(old_groups[i], new_groups)) {
            role.removeGroup(old_groups[i]);
          }
        }
      }
      String runAsIdentity = securityRole.getRunAsIdentity(false);
      if (runAsIdentity != null) {
        role.setRunAsIdentity(runAsIdentity);
      }
    } catch (Exception e) {
      throw new RemoteException(e.getMessage());
    }
  }

  private final boolean exists(String name, String[] names) {
    for (int i = 0; i < names.length; i++) {
      if (names[i].equals(name)) {
        return true;
      }
    }
    return false;
  }

  /**
   *  Removes a security role for the component.
   *
   * @param  securityRole  the name of the security role.
   */
  public void removeSecurityRole(String securityRole) throws RemoteException, SecurityException {
    try {
      roles.removeSecurityRole(securityRole);
    } catch (Exception e) {
      throw new RemoteException(e.getMessage());
    }
  }

}