/*
 * Copyright (c) 2002 by SAP Labs Bulgaria AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */

package com.sap.engine.services.httpserver.server;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.core.locking.LockingContext;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.frame.core.locking.LockException;

/*
 *
 *
 * @author Velin Doychinov
 * @version 6.30
 */
public class HttpLock {
  public static String LOCK_AREA = "_HTTP_LOCK_AREA_";
  public static String HTTP_VIRTUAL_HOSTS_LOCK = "_HTTP_VIRTUAL_HOSTS_LOCK_";
  public static String HTTP_UPLOADED_FILES_LOCK = "_HTTP_UPLOADED_FILES_LOCK_";
  public static String LOGON_GROUPS_LOCK = "_LOGON_GROUPS_LOCK_";
  private static final long LOCK_WAIT_TIMEOUT = 1000;
  public static final long READ_LOCK_WAIT_TIMEOUT = 100;
  public static final long READ_LOCK_ITER_COUNT = 100;

  private LockingContext lock = null;
  private String httpOwner = null;

  public HttpLock(ApplicationServiceContext sc) {
    lock = sc.getCoreContext().getLockingContext();
    try {
      httpOwner = lock.getAdministrativeLocking().createUniqueOwner();
    } catch (TechnicalLockException tlex) {
      httpOwner = LOCK_AREA;
    }
  }

  public void lock(String name) throws LockException, TechnicalLockException {
    if (lock != null) {
      lock.getAdministrativeLocking().lock(httpOwner, LOCK_AREA, name, LockingContext.MODE_EXCLUSIVE_NONCUMULATIVE);
    } else {
      Log.logError("ASJ.http.000178", "Cannot get a cluster lock for HTTP Provider service. " +
        "The Locking Manager is not available or not initialized. " +
        "Possible synchronization problems can occur.", null, null, null);
    }
  }

  public void enterLockArea(String name) throws TechnicalLockException, InterruptedException {
    boolean areaLocked = false;
    while (!areaLocked) {
      try {
        lock(name);
        areaLocked = true;
      } catch (LockException e) {
        Thread.sleep(LOCK_WAIT_TIMEOUT);
      }
    }
  }

  public void unlock(String name) throws TechnicalLockException {
    if (lock != null) {
      lock.getAdministrativeLocking().unlock(httpOwner, LOCK_AREA, name, LockingContext.MODE_EXCLUSIVE_NONCUMULATIVE, true);
    } else {
      Log.logError("ASJ.http.000179", "Cannot release a cluster lock for HTTP Provider service. " +
      	"The Locking Manager is not available or not initialized. " +
      	"Possible synchronization problems can occur.", null, null, null);
    }
  }

  public void leaveLockArea(String name) throws TechnicalLockException {
    unlock(name);
  }
}
