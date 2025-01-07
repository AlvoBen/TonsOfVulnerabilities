/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.httpclient;

/**
 * Used to notify when a response stream has been consumed
 *
 * @author Nikolai Neichev
 */
interface ResponseConsumedListener {

  /**
   * A response has been consumed.
   */
  void responseConsumed();
}