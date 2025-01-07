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

import com.sap.engine.core.Names;
import com.sap.tc.logging.Location;

import java.util.Iterator;
/*
 * Author: i024157 /Georgi Stanev/ 
 */

public interface PersistentDomainModel {

  public static Location LOC = Location.getLocation("com.sap.engine.session.spi.persistent", Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);

  //creates persistent record for subName
  PersistentDomainModel createSub(String subName) throws PersistentStorageException;
  // list subs form persistent layer
  PersistentDomainModel[] listSubs();
  //remove sum domain model
  void removeSub(String subname) throws PersistentStorageException;

  /**
   * The PersistemModel interface presents the Persistent storage view of the session object.
   * This method is used to create new model for the newly created session object
   * (missed in the persistent storage)
   * @param sessionId the session id
   * @return the persistent model
   * @throws PersistentStorageException if a storrage exception occurs
   */
 PersistentSessionModel createModel(String sessionId) throws PersistentStorageException;

  /**
   * This method returns the model of the already stored session object.
   * @param sessionId the session id
   * @return the persistent model
   * @throws PersistentStorageException if a storrage exception occurs
   */
PersistentSessionModel getModel(String sessionId) throws PersistentStorageException;

  /**
   * Remove the session entry present from the model argument.
   * @param model the persistent model
   * @throws PersistentStorageException if a storrage exception occurs
   */
  void remove(PersistentSessionModel model) throws PersistentStorageException;

  /**
   * Returns java.util.Iterator object over available sessions(PersistentSessionModel) in the storage.
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
  void removeExpired() throws PersistentStorageException;

  void destroy() throws PersistentStorageException;

}
