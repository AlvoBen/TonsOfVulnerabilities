/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.failover;

import com.sap.engine.session.spi.persistent.PersistentStorage;

/**
 * Author: georgi-s
 * Date: 2005-5-8
 */
public interface FailoverConfig {
  public static final String FAILOVER_SCOPE = "session.failover.scope";

  public static final int VM_LOCAL = 0;
  public static final int INSTANCE_LOCAL = 2;
  public static final int CLUSTER_WIDE = 4;

  public static final String FAILOVER_CONFIGURATION = "session.failover.configuration";

  public static final int FAILOVER_CONFIGURATION_DISABLE = 0;
  public static final int FAILOVER_CONFIGURATION_ON_REQUEST = 2;
  public static final int FAILOVER_CONFIGURATION_ON_ATTRIBUTE = 4;
  public static final int FAILOVER_CONFIGURATION_ON_APP_STOP = 8;

  PersistentStorage getPersistentStorage(String storageName) throws FailoverConfigException;

}