/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.callback;

/**
 * Author: georgi-s
 * Date: 2005-3-22
 */
public interface CallbackHandler {
  void handle(Callback callback) throws CallbackException;
  String handlerName();
}