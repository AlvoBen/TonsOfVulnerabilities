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

import com.sap.engine.session.spi.persistent.PersistentSessionModel;
import com.sap.engine.session.spi.persistent.PersistentStorageException;
import com.sap.engine.core.session.failover.SessionWriter;

import java.util.Map;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
/*
 * Author: i024157 /Georgi Stanev/ 
 */

public class MemoryPersistentSessionModel implements PersistentSessionModel {

  private String sessionId;

  public MemoryPersistentSessionModel(String sessionId) {
    this.sessionId = sessionId;
  }

  public String sessionId() {
    return sessionId;
  }

  public long creationTime() {
    return 0;
  }

  public long expTime() {
    return 0;
  }

  public void setCreationTime(long time) {

  }

  public void setExpPeriod(long timeout) {

  }

  //update adminsistrative data - creation time, increase the exp time
  public void update() throws PersistentStorageException {

  }

  public void lock(String lockInfo) throws PersistentStorageException {

  }

  public void unlock() throws PersistentStorageException {

  }

  public String getLockInfo() throws PersistentStorageException {
    return null;
  }

  public Object getChunk(String name) throws PersistentStorageException {
    return null;
  }

  public void setChunk(String name, Object chunk) throws PersistentStorageException {
    try {
      SessionWriter writer = new SessionWriter(new ByteArrayOutputStream());
      writer.writeObject(chunk);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void removeChunk(String name) throws PersistentStorageException {

  }

  public Map listChunks() throws PersistentStorageException {
    return null;
  }

  public void destroy() throws PersistentStorageException {
    
  }


  public int size(){
    return -1;
  }
}
