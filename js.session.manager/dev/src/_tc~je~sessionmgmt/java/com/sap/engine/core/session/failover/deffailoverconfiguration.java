/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.core.session.failover;

import com.sap.engine.session.failover.FailoverConfig;
import com.sap.engine.session.failover.FailoverConfigException;
import com.sap.engine.session.spi.persistent.PersistentStorage;

import com.sap.engine.session.trace.Trace;
import com.sap.engine.core.Framework;
import com.sap.engine.core.Names;
import com.sap.engine.frame.core.load.LoadContext;

/**
 * Author: georgi-s
 * Date: 2005-5-8
 */
public class DefFailoverConfiguration implements FailoverConfig {
  public final static LoadContext loadContext = (LoadContext) Framework.getManager(Names.CLASSLOADER_MANAGER);
  private int failoverScope;

  public DefFailoverConfiguration(int failoverScope) {
    this.failoverScope = failoverScope;
  }

  public PersistentStorage getPersistentStorage(String storageName) throws FailoverConfigException {
    PersistentStorage storage = null;
    switch (failoverScope) {
      case FailoverConfig.VM_LOCAL: {
         break;
      }
      case FailoverConfig.INSTANCE_LOCAL: {

        break;
      }
      case FailoverConfig.CLUSTER_WIDE: {
        break;
      }
      default: {
        if (Trace.beDebug())
          Trace.trace("Invalid failover scope:" + failoverScope);

        throw new FailoverConfigException("Invalid failover scope:" + failoverScope);
      }

    }

    return storage;
  }

}
