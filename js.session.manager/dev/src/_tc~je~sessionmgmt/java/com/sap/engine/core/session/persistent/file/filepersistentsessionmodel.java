/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.core.session.persistent.file;

import com.sap.engine.core.Names;
import com.sap.engine.session.spi.persistent.PersistentSessionModel;
import com.sap.engine.session.spi.persistent.PersistentStorageException;
import com.sap.engine.session.spi.persistent.PersistentDomainModel;
import com.sap.engine.core.session.failover.SessionReader;
import com.sap.engine.core.session.failover.SessionWriter;
import com.sap.engine.core.session.persistent.file.util.FilenameFilterImpl;
import com.sap.engine.core.session.persistent.util.SessionProps;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

import java.io.*;
import java.util.Map;
import java.util.HashMap;
/*
 * Author: i024157 /Georgi Stanev/ 
 */

public class FilePersistentSessionModel implements PersistentSessionModel {
  private File dir;
  private SessionProps sessionProps = null;  
  private static Location loc = Location.getLocation(FilePersistentSessionModel.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);

  private static final String CHUNK_PREFIX = "ch_";
  private static final String SESSION_INFO_FILE_NAME = "session_info";
  private static final String FILE_EXTENSION = ".srs";

  public FilePersistentSessionModel(File domainDir, String sessionid) throws PersistentStorageException {
    sessionProps = new SessionProps(sessionid);

    String sessionDirName = getSessionDirName(domainDir, sessionid);

    this.dir = new File(domainDir, sessionDirName);

    if (!this.dir.exists()) {
      this.dir.mkdirs();
    }

    update();
  }

  public FilePersistentSessionModel(File sessionDir) throws PersistentStorageException {
    this.dir = sessionDir;

    sessionProps = getSessionProps(dir);
  }

  public static boolean sessionExists(File domainDir, String id) throws PersistentStorageException {
    String prefix = FilenameFilterImpl.SESSION_DIR_PREFIX + id.hashCode()+"_";

    FilenameFilterImpl filter = new FilenameFilterImpl(prefix, FilenameFilterImpl.DIRS_ONLY);
    File[] files = domainDir.listFiles(filter);

    for (File file : files) {
      if (checkExistsID(file, id)) {
        return true;
      }
    }

    return false;
  }

  private synchronized String getSessionDirName(File domainDir, String id) throws PersistentStorageException {
    String prefix = FilenameFilterImpl.SESSION_DIR_PREFIX + id.hashCode()+"_";

    FilenameFilterImpl filter = new FilenameFilterImpl(prefix, FilenameFilterImpl.DIRS_ONLY);
    File[] files = domainDir.listFiles(filter);
    int num = -1;
    if(files != null){
	    for (File file : files) {
	      String sufix = file.getName().substring(prefix.length());
	      try {
	        int sufNum = Integer.parseInt(sufix);
	        if (sufNum > num) {
	          num = sufNum;
	        }
	      } catch (NumberFormatException e) {
	        num = -1;
	      }
	
	      if (checkExistsID(file, id)) {
	        return file.getName();
	      }
	    }
    }
    num++;
    return prefix + num;
  }

  private static boolean checkExistsID(File dir, String id) throws PersistentStorageException {
    File file = new File(dir, SESSION_INFO_FILE_NAME + FILE_EXTENSION);
    if (!file.exists()) {
      return false;
    }

    SessionProps props = getSessionProps(dir);

    return props.getId().equals(id);

  }

  private static SessionProps getSessionProps(File dir) throws PersistentStorageException  {
    File file = new File(dir, SESSION_INFO_FILE_NAME + FILE_EXTENSION);
    try {
      return (SessionProps)readChunk(file);
    } catch (Exception e) {
      throw new PersistentStorageException(e);
    }
  }


  public static String parseID(File file) {
    String name = file.getName();

    return name.substring(FilenameFilterImpl.SESSION_DIR_PREFIX.length());
  }


  //update adminsistrative data - creation time, increase the exp time
  public void update() throws PersistentStorageException {
    try {
      writeChunk(SESSION_INFO_FILE_NAME, "", sessionProps);
    } catch (IOException e) {
      SimpleLogger.log(Severity.ERROR, Category.SYS_SERVER, PersistentDomainModel.LOC, "ASJ.ses.ps0004", 
	  "File session persistance failed. Check the folder permissions and the disk space.");
      throw new PersistentStorageException(sessionProps.getId(), e);
    }
  }

  public String sessionId() {
    return sessionProps.getId();
  }

  public long creationTime() {
    return sessionProps.getCreationTime();
  }

  public long expTime() {
    if (sessionProps == null) {
      return 0;
    } else {
      return sessionProps.getExpTime();
    }
  }

  public void setCreationTime(long time) {
    sessionProps.setCreationTime(time);
  }

  public void setExpPeriod(long timeout) {
    sessionProps.setExpTime(System.currentTimeMillis()+timeout);
  }

  public void lock(String lockInfo) throws PersistentStorageException {
    sessionProps.setLockInfo(lockInfo);
    update();
  }

  public void unlock() throws PersistentStorageException {
    sessionProps.setLockInfo(null);
    update();
  }

  public String getLockInfo() throws PersistentStorageException {
    return sessionProps.getLockInfo();
  }

  public Object getChunk(String name) throws PersistentStorageException {
    try {
       return readChunk(name, CHUNK_PREFIX);
    } catch (Exception e) {
      throw new PersistentStorageException(sessionProps.getId(), e);
    }
  }


  private Object readChunk(String name, String prefix) throws ClassNotFoundException, IOException {
    String fileName = prefix + name + FILE_EXTENSION;

    File file = new File(dir, fileName);

    return readChunk(file);
  }

  private static Object readChunk(File file) throws ClassNotFoundException, IOException {
    if (file.exists()) {
      SessionReader reader = new SessionReader(new FileInputStream(file));
      Object readChunk = reader.readObject();
      reader.close();
      return readChunk;
    } else {
      return null;
    }
  }

  public void setChunk(String name, Object chunk) throws PersistentStorageException {
    try {
      writeChunk(name, CHUNK_PREFIX, chunk);
    } catch (IOException e) {
      throw new PersistentStorageException(sessionProps.getId(), e);
    }
  }

  private void writeChunk(String name, String prefix, Object chunk)  throws IOException  {
    String fileName = prefix + name + FILE_EXTENSION;

    File f = new File(dir, fileName);

    if (!f.exists()) {
      f.createNewFile();
    }

    SessionWriter writer =  new SessionWriter(new FileOutputStream(f));
    writer.writeObject(chunk);
	writer.close();
  }

  public void removeChunk(String name) throws PersistentStorageException {
    File f = new File(dir, CHUNK_PREFIX + name + FILE_EXTENSION);
    if (f.exists()) {
      f.delete();
    }
  }

  public Map listChunks() throws PersistentStorageException {
    HashMap<String, Object> map = new HashMap<String, Object>();

    FilenameFilter filter = new FilenameFilterImpl(CHUNK_PREFIX, FilenameFilterImpl.FILES_ONLY);

    File[] files = dir.listFiles(filter);

    for (File file : files) {
      try {
        Object chunk = readChunk(file);
        String name = file.getName().substring(CHUNK_PREFIX.length());

        map.put(name, chunk);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return map;
  }

  public void destroy() throws PersistentStorageException {
    File[] f = dir.listFiles();
    for (File aF : f) {
      aF.delete();
    }
    dir.delete();
  }


  public int size()  {
    int size = 0;
    try {
      File[] ff = dir.listFiles(new FilenameFilterImpl(CHUNK_PREFIX, FilenameFilterImpl.FILES_ONLY));
      if (ff != null && ff.length >0) {
        for (File file : ff) {
          size += file.length();
        }
      }
    } catch (Exception e) {
      if(loc.beDebug()){
        loc.throwing(e);
      }
      return 0;
    }

    return size;
  }
}
