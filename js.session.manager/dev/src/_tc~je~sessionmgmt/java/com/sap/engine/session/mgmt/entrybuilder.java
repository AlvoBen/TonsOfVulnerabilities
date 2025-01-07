package com.sap.engine.session.mgmt;

import com.sap.engine.session.runtime.SessionFailoverMode;
import com.sap.engine.session.spi.persistent.Storage;

/**
 * User: I028674
 * Date: 2006-11-13
 * Time: 13:17:37
 */
public interface EntryBuilder {
  Storage buildStorageForType(int type);
  SessionFailoverMode buildFailoverModeForType(int type);
}
