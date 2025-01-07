package com.sap.engine.lib.util.concurrent;

/**
 * ReadWriteLocks maintain a pair of associated locks. The readLock may be
 * held simultanously by multiple reader threads, so long as there are no writers.
 * The writeLock is exclusive. ReadWrite locks are generally preferable to plain Sync
 * locks or synchronized methods in cases where
 */
public interface ReadWriteLock {

  /**
   *  get the readLock Sync
   */
  Sync readLock();


  /**
   * get the writeLock Sync
   */
  Sync writeLock();

}

