package com.sap.engine.core.session.persistent.db;

import com.sap.engine.core.Names;
import com.sap.engine.session.spi.persistent.PersistentSessionModel;
import com.sap.engine.session.spi.persistent.PersistentStorageException;
import com.sap.engine.core.session.failover.SessionWriter;
import com.sap.engine.core.session.failover.SessionReader;
import com.sap.tc.logging.Location;
import java.sql.Timestamp;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.HashMap;

/**
 * User: Pavel Bonev
 * Date: 2006-11-16
 */
public class DBPersistentSessionModel implements PersistentSessionModel {
  
  private static Location loc = Location.getLocation(DBPersistentSessionModel.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);
  private int domainHash = 0;
  private int domainCounter = -1;
  private int sessionHash = 0;
  private int sessionCounter = -1;

  private String sessionID;
  private String lockInfo;
  private Timestamp creationTime;
  private Timestamp expTime;

  private DBStorage storage = null;

  public DBPersistentSessionModel(int domainHash, int domainCounter, int sessionHash, int sessionCounter, String sessionID, DBStorage storage) {
    this(domainHash, domainCounter, sessionHash, sessionCounter, sessionID, null, null, null, storage);
  }

  public DBPersistentSessionModel(int domainHash, int domainCounter, int sessionHash, int sessionCounter,
                                  String sessionID, String lockInfo, Timestamp creationTime, Timestamp expTime, DBStorage storage) {

    this.domainHash = domainHash;
    this.domainCounter = domainCounter;
    this.sessionHash = sessionHash;
    this.sessionCounter = sessionCounter;
    this.sessionID = sessionID;
    this.lockInfo = lockInfo;
    this.creationTime = creationTime;
    this.expTime = expTime;

    this.storage = storage;
  }

  public int getSessionHash() {
    return sessionHash;
  }

  public int getSessionCounter() {
    return sessionCounter;
  }


  public String sessionId() {
    return sessionID;
  }

  public long creationTime() {
    return creationTime.getTime();
  }

  public long expTime() {
    if (expTime == null) {
      return 0;
    } else{
      return expTime.getTime();
    }
  }

  public void setCreationTime(long time) {
    creationTime = new Timestamp(time);
  }

  public void setExpPeriod(long timeout) {
    if (expTime == null) {
      expTime = new Timestamp(creationTime()+timeout);
    } else {
      expTime.setTime(System.currentTimeMillis()+timeout);
    }
  }

  //update adminsistrative data - creation time, increase the exp time
  public void update() throws PersistentStorageException {
    storage.updateSessionModel(domainHash, domainCounter, sessionHash, sessionCounter,
                               creationTime, expTime, lockInfo);
  }

  public void lock(String lockInfo) throws PersistentStorageException {
    storage.updateSessionModel(domainHash, domainCounter, sessionHash, sessionCounter,
                               creationTime, expTime, lockInfo);
    this.lockInfo = lockInfo;
  }

  public void unlock() throws PersistentStorageException {
   storage.updateSessionModel(domainHash, domainCounter, sessionHash, sessionCounter,
                              creationTime, expTime, null);
    this.lockInfo = null;
  }

  public String getLockInfo() throws PersistentStorageException {
    return storage.getLockInfo(domainHash, domainCounter, sessionHash, sessionCounter);
  }

  public Object getChunk(String chunkName) throws PersistentStorageException {
    byte[] chunkData = storage.getChunk(domainHash, domainCounter, sessionHash, sessionCounter, chunkName);

    if (chunkData != null) {
      return bytes2Object(chunkData);
    }

    return null;
  }

  public void removeChunk(String chunkName) throws PersistentStorageException {
    try {
      storage.removeChunk(domainHash, domainCounter, sessionHash, sessionCounter, chunkName);
    } catch (Exception e) {
      throw new PersistentStorageException(e);
    }

  }

  public void setChunk(String chunkName, Object chunk) throws PersistentStorageException {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      SessionWriter sw = new SessionWriter(baos);
      sw.writeObject(chunk);
      sw.flush();
      byte[] chunkData = baos.toByteArray();
      sw.close();

      storage.setChunk(domainHash, domainCounter, sessionHash, sessionCounter, chunkName, chunkData);
    } catch (Exception e) {
      throw new PersistentStorageException(e);
    }
  }

  public Map listChunks() throws PersistentStorageException {
    Map map = storage.listChunks(domainHash, domainCounter, sessionHash, sessionCounter);
    HashMap<String, Object> objects = new HashMap<String, Object>(map.size());
    for (Object o : map.entrySet()) {
      Map.Entry entry = (Map.Entry) o;
      String name = (String) entry.getKey();
      byte[] chunkData = (byte[]) entry.getValue();
      if (chunkData != null) {
        Object obj = bytes2Object(chunkData);
        objects.put(name, obj);
      }
    }
    return objects;
  }

  public void destroy() throws PersistentStorageException {
    storage.removeSession(domainHash, domainCounter, sessionHash, sessionCounter);
  }

  private Object bytes2Object(byte[] bytes) throws PersistentStorageException {
    try {
      SessionReader sr = new SessionReader(new ByteArrayInputStream(bytes));
      sr.close();
      return sr.readObject();
    } catch (Exception e) {
      throw new PersistentStorageException(e);
    }
  }


  public int size()  {
    int size = 0;
    try {
      Map map = storage.listChunks(domainHash, domainCounter, sessionHash, sessionCounter);
      if (map != null && map.size() > 0) {
        for (Object o : map.entrySet()) {
          Map.Entry entry = (Map.Entry) o;
          size += ((byte[]) entry.getValue()).length;
        }
      }
    } catch (PersistentStorageException e) {
      if(loc.beDebug()){
        loc.throwing(e);
      }
      return 0;
    }
    return size;
  }
}
