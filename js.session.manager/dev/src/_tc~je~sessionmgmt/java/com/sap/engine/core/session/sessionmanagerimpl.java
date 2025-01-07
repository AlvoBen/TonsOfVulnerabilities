/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.core.session;

import com.sap.engine.core.session.timer.tasks.ShMemoryCheck;
/**
 * Author: georgi-s
 * Date: Feb 20, 2004
 */
public class SessionManagerImpl extends Manager {

  protected void clearShmMonitoring() {
    new ShMemoryCheck();
  }

}
