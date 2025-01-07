package com.sap.engine.core.log.api;

/**
 * Title:        Logging
 * Description:  Logging API
 * Copyright:    Copyright (c) 2002
 * Company:      SAP Labs Bulgaria LTD., Sofia, Bulgaria.
 * Url:          Http://www.saplabs.bg
 *               All rights reserved.
 *
 *               This software is the confidential and proprietary information
 *               of SAP AG International ("Confidential Information").
 *               You shall not disclose such  Confidential Information
 *               and shall use it only in accordance with the terms of
 *               the license agreement you entered into with SAP AG.
 */

import com.sap.engine.frame.state.ManagementInterface;
import com.sap.engine.lib.logging.descriptors.LogConfiguration;
import com.sap.tc.logging.FileLog;

// import com.sap.tc.logging.LogController;

/**
 * @author Nikola Marchev
 * @version 6.30
 */
public interface LogManagerManagementInterface extends ManagementInterface {

  public LogConfiguration getLogConfiguration();

  /** @deprecated */
  public Object getDatabaseFilter();

  /** @deprecated */
  public String[] getDatabaseFiltered();

  /** @deprecated */
  // public void enableDbFiltering(LogController[] target);
  public void enableDbFiltering(String[] targets);

  /** @deprecated */
  // public void disableDbFiltering(LogController[] target);
  public void disableDbFiltering(String[] targets);

  public FileLog getDefaultTraceFile();

  public boolean singleTraceFileIsForced();

  public String[] getSingleTraceFileExcluded();

  public boolean isSingleTraceFileExcluded(String target);

}
