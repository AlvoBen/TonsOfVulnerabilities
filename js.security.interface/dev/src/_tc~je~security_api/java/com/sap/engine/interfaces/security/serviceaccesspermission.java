/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.interfaces.security;

import java.security.Permission;

/**
 *  This is the base permission that will be used by
 * the j2ee services for user authorization decisions.
 * 
 * @version 7.10
 * @author Jako Blagoev
 */
public final class ServiceAccessPermission extends Permission {//$JL-SER$
  private String actions = null;

  /**
   * Constructor. We recomend the argument name to be the name of the service.
   * 
   * @param name the name of the permission
   */
	public ServiceAccessPermission(String name) {
	  this(name, null);
	}
	
	/**
   * Constructor. We recomend the argument name to be the name of the service.
	 * The argument actions must be the operation which will be granted or checked.
	 * 
	 * @param name
	 * @param actions
	 */
	public ServiceAccessPermission(String name, String actions) {
	  super(name);
	  if (name == null) {
	    throw new NullPointerException("Argument name is not allowed to be null!");
	  }
	  this.actions = actions;
	}	
	
	public int hashCode() {
		return getName().hashCode();
	}

  /**
   * The permission is equal to onather, if it is an object from class
   * ServiceAccessPermission, both have the same names and same actions.
   * 
   * @param otherPerm permission to check for equallity
   * @return true if both permissions are equal by the specified rule.
   **/
	public boolean equals(Object otherPerm) {
		if (!(otherPerm instanceof ServiceAccessPermission)) {
		  return false;
		}
		ServiceAccessPermission perm = (ServiceAccessPermission) otherPerm;
		if (!perm.getName().equals(getName())) {
		  return false;
		}
		
		if (perm.actions == null && actions == null) {
		  return true;
		}
		
		if (perm.actions == null || actions == null) {
		  return false;
		}
				
 		return (perm.actions.equals(actions));
	}

  /**
   * Returns the actions of the permission.
   * @return String
   */
	public String getActions() {
		return actions;
	}

  /**
   * A permission implies onather permission if both have the same name and same actions
   * or they both have the same name and the actions of the first one is null.
   * 
   * @return true if the permission is implied by the specified rule.
   */
	public boolean implies(Permission otherPerm) {
		if (!(otherPerm instanceof ServiceAccessPermission)) {
		  return false;
		}
		
		ServiceAccessPermission perm = (ServiceAccessPermission) otherPerm;
		if (!perm.getName().equals(getName())) {
		  return false;
		}
		
		if (actions == null) {
		  return true;
		}
		
		if (perm.actions == null) {
		  return false;
		}
				
		return (perm.actions.equals(actions));
	}

}
