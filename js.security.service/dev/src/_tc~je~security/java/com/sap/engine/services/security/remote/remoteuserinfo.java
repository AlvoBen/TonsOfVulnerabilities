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
package com.sap.engine.services.security.remote;

/**
 *  Information for the user or group.
 *
 * @author  Stephan Zlatarev
 * @version 4.0.3.
 */
public interface RemoteUserInfo
  extends java.io.Serializable {

  /**
   *  Returns the identifier of the user.
   *
   * @return  the identifier of the user
   */
  public int getId();


  /**
   *  Returns the name of the user.
   *
   * @return  the name of the user
   */
  public String getName();


  /**
   *  Returns an array of all direct parents of the user/group.
   *
   * @return  array of user/group information instances or an empty array.
   */
  public String[] getParents();


  /**
   *  Returns the principal associated with the logical user.
   *
   * @return  the principal associated with the logical user.
   */
  public java.security.Principal getPrinicpal();


  /**
   *  Returns an array of all direct children of the group.
   *
   * @return     array with children of this group or an empty array.
   */
  public String[] getChildrenUsers();


  public String[] getChildrenGroups();


  /**
   *  Returns true if the object represents a user, and false if this is a group.
   *
   * @return  true if the object represents a user, and false if this is a group.
   */
  public boolean isUser();


  public boolean isAdmin();

}

