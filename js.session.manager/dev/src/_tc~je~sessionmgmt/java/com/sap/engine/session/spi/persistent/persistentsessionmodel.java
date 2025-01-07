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
import java.util.Map;
/*
 * Author: i024157 /Georgi Stanev/ 
 */

public interface PersistentSessionModel {

  String sessionId();
  long creationTime();
  long expTime();

  void setCreationTime(long time);
  void setExpPeriod(long timeout);

  //update adminsistrative data - creation time, increase the exp time
  void update() throws PersistentStorageException;

  void lock(String lockInfo) throws PersistentStorageException;
  void unlock() throws PersistentStorageException;
  String getLockInfo() throws PersistentStorageException;

  Object getChunk(String name) throws PersistentStorageException;

  /**
   *
   * @param name of the chunck
   * @param chunk  register chunck with this name
   * @throws PersistentStorageException if there are some problems with the storage
   */
  void setChunk(String name, Object chunk) throws PersistentStorageException;

  /**
   * Remove Chunk
   * @param name remove the chunck with this name
   * @throws PersistentStorageException  if there are some problems with the storage
   */
  void removeChunk(String name) throws PersistentStorageException;

  /**
   *
   * @return Map of all available chunks
   * @throws PersistentStorageException if there are some problems with the storage
   */
  Map listChunks() throws PersistentStorageException;

  void destroy() throws PersistentStorageException;

  /**
   * Calculate chuncks' size
   * @return sum of all chuncks' sizes
   */
  public int size();
}
