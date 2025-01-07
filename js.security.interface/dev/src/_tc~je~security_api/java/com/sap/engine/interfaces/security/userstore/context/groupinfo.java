﻿/**
 * Copyright:    2002 by SAP AG
 * Company:      SAP AG, http://www.sap.com
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license
 * agreement you entered into with SAP.
 */
package com.sap.engine.interfaces.security.userstore.context;


/**
 * Group info is the root class for group representation classes.
 *
 * @author  Boris Koeberle
 * @version 6.30
 */
public interface GroupInfo {


  /**
   * Get the name of the group.
   *
   * @return  the name of the group
   */
  public String getName();


  /**
   * Get the parent groups of this group.
   *
   * @return  the names of the parent groups of this group.
   */
  public java.util.Iterator getParentGroups();
  

  /**
   * Get the child groups of this group.
   *
   * @return  the names of the child groups of this group.
   */
  public java.util.Iterator getChildGroups();

  /**
   * Get the users in this group.
   *
   * @return  the names of the users, which belong to this group
   */
  public java.util.Iterator getUsersInGroup();
}

