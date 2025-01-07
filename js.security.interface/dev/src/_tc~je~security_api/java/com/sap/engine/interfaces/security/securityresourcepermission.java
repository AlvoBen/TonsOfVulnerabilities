/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.engine.interfaces.security;

import java.security.Permission;

public class SecurityResourcePermission extends Permission {//$JL-SER$
  private String action = null;
  private String instance = null;
  private String actions = null;
  
  public static final String ACTION_ALL = "ALL";
  public static final String INSTANCE_ALL = "ALL";
  
  private static final String SEPARATOR = ":$:";
  
  public SecurityResourcePermission(String name, String actions) {
    super(name);
    this.actions = actions;
    parseActions(actions);
  }

  public SecurityResourcePermission(String name, String action, String instance) {
    super(name);
    this.action = action;
    this.instance = instance;
    this.actions = actionsToString(action, instance);
  }
  
  public int hashCode() {
    int hashCode = 0;
    
    if (getName() != null ) {
      hashCode = getName().hashCode();
    }
    
    if (action != null) {
      hashCode = hashCode ^ action.hashCode(); 
    }
    
    if(instance != null) {
      hashCode = hashCode ^ instance.hashCode();
    }
    
    return hashCode;
  }

  public boolean equals(Object other) {
    if (!(other instanceof SecurityResourcePermission)) {
      return false;
    }
    
    SecurityResourcePermission perm = (SecurityResourcePermission) other;

    if (!perm.getName().equals(getName())) {
      return false;
    }
    
    if (!action.equals(perm.getAction())) {
      return false;
    } 
    
    if (!instance.equals(perm.getInstance())) {
      return false;
    }
    
    return true;   
  }

  public String getActions() {
    return actions;
  }

  public boolean implies(Permission other) {
    if (!(other instanceof SecurityResourcePermission)) {
      return false;
    }
    
    SecurityResourcePermission perm = (SecurityResourcePermission) other;
    
    if (!perm.getName().equals(getName())) {
      return false;
    }
    
    if (!action.equals(perm.getAction()) && !action.equals(ACTION_ALL)) {
      return false;
    } 
    
    if (!instance.equals(perm.getInstance()) && !instance.equals(INSTANCE_ALL)) {
      return false;
    }
    
    return true;
  }
  
  public String getAction() {
    return action;
  }
  
  public String getInstance() {
    return instance;
  }
  
  public String toString() {
    return "name " + getName() + "\r\n" +
           "action " + action  + "\r\n" +
           "instance " + instance;
  }

///////////////////////////////////////////////////////////////////////////
/////////////////////PRIVATE METHODS///////////////////////////////////////
///////////////////////////////////////////////////////////////////////////  
  private void parseActions(String actions) {
    int index = actions.indexOf(SEPARATOR);
    if (index == -1) {
      throw new IllegalArgumentException(actions + " is not suitable value for parameter of the constructor");
    }
    
    this.action = actions.substring(0, index);
    this.instance = actions.substring(index + SEPARATOR.length(), actions.length());
  }
  
  private String actionsToString(String action, String instance) {
    return action + SEPARATOR + instance;
  }
}
