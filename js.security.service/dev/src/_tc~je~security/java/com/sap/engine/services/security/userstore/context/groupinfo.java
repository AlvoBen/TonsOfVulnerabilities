/**
 * Copyright:    2002 by SAP AG
 * Company:      SAP AG, http://www.sap.com
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license
 * agreement you entered into with SAP.
 */
package com.sap.engine.services.security.userstore.context;

import com.sap.engine.interfaces.security.userstore.spi.GroupInfoSpi;

public final class GroupInfo implements com.sap.engine.interfaces.security.userstore.context.GroupInfo {

  private GroupInfoSpi spi = null;

  public GroupInfo(GroupInfoSpi spi) {
    this.spi = spi;
  }

  /**
   * Get the name of the group.
   *
   * @return  the name of the group
   */
  public String getName() {
    return spi.engineGetName();
  }

  /**
   * Get the parent groups of this group.
   *
   * @return  the names of the parent groups of this group.
   */
  public java.util.Iterator getParentGroups() {
    return spi.engineGetParentGroups();
  }

  /**
   * Get the child groups of this group.
   *
   * @return  the names of the child groups of this group.
   */
  public java.util.Iterator getChildGroups() {
    return spi.engineGetChildGroups();
  }

  /**
   * Get the users in this group.
   *
   * @return  the names of the users, which belong to this group
   */
  public java.util.Iterator getUsersInGroup() {
    return spi.engineGetUsersInGroup();
  }

  protected GroupInfoSpi getSpi() {
    return spi;
  }

  public String toString() {
    return spi.toString();
  }
}

