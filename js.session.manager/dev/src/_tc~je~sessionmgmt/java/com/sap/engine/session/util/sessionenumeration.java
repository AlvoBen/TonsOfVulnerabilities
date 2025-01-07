/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.util;

import com.sap.engine.session.SessionHolder;
import com.sap.engine.session.Session;

import java.util.Enumeration;

/**
 * Author: georgi-s
 * Date: Jun 18, 2004
 */
public interface SessionEnumeration extends Enumeration {

  public SessionHolder getSessionHolder(Session session);

  public void reset();

  public void release();
}
