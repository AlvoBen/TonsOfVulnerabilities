/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.client.portable.weak;

import java.lang.ref.WeakReference;

/**
 * This class represents a hashtable list node.
 *
 * @author Nikolai Neichev
 * @version 4.0
 */
public class ListNode {

  // the next node
  protected ListNode next;
  // the object key
  private WeakReference object;

  /**
   * Constructor.
   * @param object The object key.
   */
  public ListNode(WeakReference object) {
    this.object = object;
  }

  /**
   * Getter method.
   * @return The object key.
   */
  public WeakReference getObject() {
    return object;
  }

}
