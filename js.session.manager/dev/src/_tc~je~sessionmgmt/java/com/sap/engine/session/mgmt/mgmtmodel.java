/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.mgmt;

import com.sap.engine.session.SessionDomain;

/**
 * Author: georgi-s
 * Date: 2005-4-26
 */
public interface MgmtModel {

  String sessionId();

  String compositeName();

	SessionDomain domain();

	long lastAccessedTime();

	long maxInactiveInterval();

  void setMaxInactiveInterval(int p);

  boolean isValid();

  void renew();

}