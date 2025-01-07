/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.domains;

import com.sap.engine.lib.security.domain.PermissionsFactory;
import com.sap.engine.services.security.Util;
import com.sap.tc.logging.Severity;

import java.io.IOException;
import java.io.Serializable;
import java.security.Permission;
import java.security.UnresolvedPermission;

/**
 * java.security.Permission wrapper
 *
 * @author Ilia Kacarov
 */
public class PRecord implements Serializable {

  static final long serialVersionUID = 1233767603080197288L;

  private String className = null;
  private String name = null;
  private String actions = null;

  public PRecord(String className, String name, String actions) {
    this.className = className;
    this.name = name;
    this.actions = actions;
    if (actions != null && actions.trim().length() == 0) {
      this.actions = null;
    }
    if (name != null && name.length() == 0) {
      this.name = null;
    }
  }

  public PRecord(Permission permission) {
    this.className = permission.getClass().getName();
    this.name = permission.getName();
    this.actions = permission.getActions();

    if (permission instanceof UnresolvedPermission) {
      try {
        String[] temp = new String[3];

        temp = PermissionsFactory.getTargetPermissionData((UnresolvedPermission) permission);
        this.className = temp[0];
        this.name = temp[1];
        this.actions = temp[2];
      } catch (Exception e) {
        // left as is
        Util.SEC_SRV_LOCATION.traceThrowableT(Severity.INFO, "Could not init PRecord", e);
      }
    }

    if (actions != null && actions.trim().length() == 0) {
      this.actions = null;
    }
    if (name != null && name.length() == 0) {
      this.name = null;
    }
  }

  public String getName() {
    return name;
  }

  public String getClassName() {
    return className;
  }

  public String getActions() {
    return actions;
  }

  public String[] toArray() {
    return new String[]{className, name, actions};
  }

  public int hashCode() {
    //The method used to work with
    // return super.hashCode()
    // so if you need performance - uncomment previous line (and comment the following one)
    return getClass().getName().hashCode();
  }

  public boolean equals(Object o) {
    PRecord b = (PRecord) o;

    if (!className.equals(b.className)) {
      return false;
    }
    if (name == null && b.name != null) {
      return false;
    }
    if (name != null && b.name == null) {
      return false;
    }

    if (name != null && b.name != null && !name.equals(b.name)) {
      return false;
    }

    if (actions == null && b.actions != null) {
      return false;
    }
    if (actions != null && b.actions == null) {
      return false;
    }
    if (actions != null && b.actions != null && !actions.equals(b.actions)) {
      return false;
    }

    return true;
  }

  public Object clone() {
    return new PRecord(className, name, actions);
  }

  public String toString() {
    return "[" + className + ": " + name + ": " + actions + "]\n";
  }


  private synchronized void writeObject(java.io.ObjectOutputStream oos) throws IOException {
    oos.defaultWriteObject();
  }

  /**
   * Restores this object from a stream (i.e., deserializes it).
   */
  private synchronized void readObject(java.io.ObjectInputStream ois) throws IOException, ClassNotFoundException {
    ois.defaultReadObject();
  }

}

