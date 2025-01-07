/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.spi.persistent;

import java.util.Iterator;

/**
 * Author: georgi-s
 * Date: 2005-5-8
 */
public interface PersistentStorage {


  PersistentStorage createSub(String subName) throws PersistentStorageException;

  PersistentStorage[] listSubs();

  boolean tryLock();

  void releaseLock();

  void move(PersistentStorage target) throws PersistentStorageException;

  /**
   * The PersistemModel interface presents the Persistent storage view of the session object.
   * This method is used to create new model for the newly created session object
   * (missed in the persistent storage)
   * @param sessionId the session id
   * @return the persistent model
   * @throws PersistentStorageException if a storrage exception occurs
   */
  com.sap.engine.session.failover.PersistentModel createModel(String sessionId) throws PersistentStorageException;

  /**
   * This method returns the model of the already stored session object.
   * @param sessionId the session id
   * @return the persistent model
   * @throws PersistentStorageException if a storrage exception occurs
   */
  com.sap.engine.session.failover.PersistentModel getModel(String sessionId) throws PersistentStorageException;

  /**
   * Remove the session entry present from the model argument.
   * @param model the perforce
   * @throws PersistentStorageException if a storrage exception occurs
   */
  void remove(com.sap.engine.session.failover.PersistentModel model) throws PersistentStorageException;

  /**
   * Returns java.util.Iterator object over available sessions in the storage.
   * @return iterator over the persistent session models
   */
  Iterator sessionModels();

  /**
   *Returns java.util.Iterator object over all expired sessions in the storage.
   * @return iterator over the expired persistent session models
   */
  Iterator allExpired ();

  /**
   * Removes all expired entries from the storage.
   * @throws PersistentStorageException if a storrage exception occurs
   */
  void removeExpierd() throws PersistentStorageException;

  void destroy() throws PersistentStorageException;

}