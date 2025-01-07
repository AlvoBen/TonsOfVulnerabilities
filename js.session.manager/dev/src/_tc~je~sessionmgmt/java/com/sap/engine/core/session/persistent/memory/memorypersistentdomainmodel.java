/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.core.session.persistent.memory;

import com.sap.engine.session.spi.persistent.PersistentDomainModel;
import com.sap.engine.session.spi.persistent.PersistentStorageException;
import com.sap.engine.session.spi.persistent.PersistentSessionModel;

import java.util.Iterator;
/*
 * Author: i024157 /Georgi Stanev/ 
 */

public class MemoryPersistentDomainModel implements PersistentDomainModel {
  //creates persistent record for subName
  public PersistentDomainModel createSub(String subName) throws PersistentStorageException {
    return null;
  }

  // list subs form persistent layer
  public PersistentDomainModel[] listSubs() {
    return new PersistentDomainModel[0];  //To change body of implemented methods use File | Settings | File Templates.
  }

  //remove sum domain model
  public void removeSub(String subname) throws PersistentStorageException {

  }

  public PersistentSessionModel createModel(String sessionId) throws PersistentStorageException {
    return new MemoryPersistentSessionModel(sessionId);
  }

  public PersistentSessionModel getModel(String sessionId) throws PersistentStorageException {
    return new MemoryPersistentSessionModel(sessionId);
  }

  public void remove(PersistentSessionModel model) throws PersistentStorageException {

  }

  public Iterator sessionModels() {
    return null;
  }

  public Iterator allExpired() {
    return null;
  }

  public void removeExpired() throws PersistentStorageException {

  }

  public void destroy() throws PersistentStorageException {

  }
}
