package com.sap.engine.core.session.persistent.sharedmemory;

import com.sap.engine.core.Names;
import com.sap.engine.session.spi.persistent.PersistentSessionModel;
import com.sap.engine.session.spi.persistent.PersistentStorageException;
import com.sap.engine.session.spi.persistent.PersistentDomainModel;
import com.sap.jvm.session.SharedSessionDomain;
import com.sap.jvm.session.SharedSession;
import com.sap.jvm.session.SharedSessionChunk;
import com.sap.jvm.session.SerializationCallback;
import com.sap.jvm.session.DeserializationCallback;
import com.sap.jvm.session.SharedDataAccessException;
import com.sap.jvm.session.OutOfSharedMemoryException;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.SimpleLogger;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.io.IOException;

public class ShMemoryPersistentSessionModel implements PersistentSessionModel {

  private static Location loc = Location.getLocation(ShMemoryPersistentSessionModel.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);

  private static final DeserializationCallback deserializator = new DeserializationCallbackImpl();
  private static final SerializationCallback serializator = new SerializationCallbackImpl();
  private final HashMap<String, SharedSessionChunk> chunks = new HashMap<String, SharedSessionChunk>(4);

  private SharedSessionDomain internalDomain = null;
  private SharedSession internalSession = null;
  private String sessionId = null;

  public ShMemoryPersistentSessionModel(SharedSessionDomain domain, String id) throws PersistentStorageException {
    this.internalDomain = domain;
    this.sessionId = id;
    try {
      this.internalSession = this.internalDomain.getSharedSession(id);
      if (this.internalSession == null) {
        this.internalSession = this.internalDomain.createSharedSession(id);
      }
    } catch (SharedDataAccessException e) {
      SimpleLogger.log(Severity.ERROR, Category.SYS_SERVER, PersistentDomainModel.LOC, "ASJ.ses.ps0006", 
	  "Shared memory storrage creation problem. Failover will not work.");
      throw new PersistentStorageException(e);
    } catch (OutOfSharedMemoryException e) {
      SimpleLogger.log(Severity.ERROR, Category.SYS_SERVER, PersistentDomainModel.LOC, "ASJ.ses.ps0007",
	  "Out of shared memory. Failover will not work. Check the shared memory settings.");
      throw new PersistentStorageException(e);
    }
  }

  public ShMemoryPersistentSessionModel(SharedSessionDomain domain, SharedSession _session, String id) {
    this.internalDomain = domain;
    this.internalSession = _session;
    this.sessionId = id;
  }

  public String sessionId() {
    return this.sessionId;
  }

  public long creationTime() {
    try {
      return this.internalSession.getCreationTime();
    } catch (SharedDataAccessException e) {
      if (loc.beWarning()) {
        loc.traceThrowableT(Severity.WARNING, "", e);
      }
      return 0;
    }
  }

  public long expTime() {
    try {
      return this.internalSession.getExpirationTime();
    } catch (SharedDataAccessException e) {
      if (loc.beWarning()) {
        loc.traceThrowableT(Severity.WARNING, "", e);
      }
      return 0;
    }
  }

  public void setCreationTime(long time) {
    try {
      this.internalSession.setCreationTime(time);
    } catch (SharedDataAccessException e) {
      if (loc.beWarning()) {
        loc.traceThrowableT(Severity.WARNING, "", e);
      }
    }
  }

  public void setExpPeriod(long timeout) {
    try {
      this.internalSession.setExpirationTime(System.currentTimeMillis() + timeout);
    } catch (SharedDataAccessException e) {
      if (loc.beWarning()) {
        loc.traceThrowableT(Severity.WARNING, "", e);
      }
    }
  }

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
    if (loc.bePath()) {
      loc.entering("getChunk<" + name + ">");
    }
    SharedSessionChunk _chunk;
    _chunk = this.chunks.get(name);

    if (_chunk == null) {/* try to get from the Shared Memory */
      if (loc.beDebug()) {
        loc.debugT("getChunk-> The chunk is not found.");
      }
      try {
        _chunk = this.internalSession.getSharedSessionChunk(name);
        if (_chunk != null) {
          if (loc.beDebug()) {
            loc.debugT("getChunk-> Created new chunk.");
          }
          this.chunks.put(name, _chunk);
        }
      } catch (SharedDataAccessException e) {
        throw new PersistentStorageException(e);
      }
    }
    if (_chunk != null) {
      try {
        Object ooo = _chunk.getContent(deserializator);
        if (loc.beDebug()) {
          loc.debugT("getChunk-> Return chunk:" + ooo);
        }
        return ooo;
      } catch (IOException e) {
        if(loc.bePath()){
          loc.throwing(e);
          loc.traceThrowableT(Severity.PATH, null, e);
        }
        throw new PersistentStorageException(e);
      } catch (ClassNotFoundException e) {
        if(loc.bePath()){
          loc.throwing(e);
          loc.traceThrowableT(Severity.PATH, null, e);
        }
        throw new PersistentStorageException(e);
      }
    }
    return null;
  }

  public void setChunk(String name, Object chunk) throws PersistentStorageException {
    if (loc.bePath()) {
      loc.entering("setChunk<" + name + "><" + chunk + ">");
    }
    SharedSessionChunk _chunk = this.chunks.get(name);
    try {
      if (_chunk == null) {
        if(loc.beDebug()){
          loc.debugT("setChunk: the chunk is not found in local cache");
        }
        _chunk = this.internalSession.getSharedSessionChunk(name);
        if (_chunk == null) {
          if(loc.beDebug()){
            loc.debugT("setChunk: the chunk is not found in local cache and will be created a new one");
          }
          _chunk = this.internalSession.createSharedSessionChunk(name);
        }
        this.chunks.put(name, _chunk);
      }
      _chunk.setContent(chunk, serializator);

    } catch (IOException e) {
      if(loc.beDebug()){
        loc.throwing(e);
        loc.traceThrowableT(Severity.DEBUG, null, e);
      }
      throw new PersistentStorageException(e);
    }
  }

  public void removeChunk(String name) throws PersistentStorageException {
    if (loc.bePath()) {
      loc.entering("removeChunk<" + name + ">");
    }
    SharedSessionChunk _chunk;
    _chunk = this.chunks.remove(name);
    if (_chunk == null) {
      try {
        _chunk = this.internalSession.getSharedSessionChunk(name);
      } catch (SharedDataAccessException e) {
        throw new PersistentStorageException(e);
      }
    }
    _chunk.destroy();
  }

  public Map listChunks() throws PersistentStorageException {
    Set<SharedSessionChunk> _chunks = null;
    try {
      _chunks = this.internalSession.chunks();
    } catch (SharedDataAccessException e) {
      new PersistentStorageException("Session no longer exists in shared memory", e);
    }

    //todo ?
    if(_chunks == null || _chunks.size() == 0){
      return null;
    }

    HashMap<String, Object> _resultChunks = new HashMap<String, Object>(_chunks.size());
    for (SharedSessionChunk _chunk : _chunks) {
      try {
        _resultChunks.put(_chunk.getName(), _chunk.getContent(deserializator));
      } catch (IOException e) {
        if (loc.beWarning()) {
          loc.traceThrowableT(Severity.WARNING, "", e);
        }
      } catch (ClassNotFoundException e) {
        if (loc.beWarning()) {
          loc.traceThrowableT(Severity.WARNING, "", e);
        }
      }
    }

    return _resultChunks;
  }

  public void destroy() throws PersistentStorageException {
    this.chunks.clear();
    this.internalSession.destroy();

  }

  public int size() {
    try {
      return (int) this.internalSession.getTotalSize();
    } catch (SharedDataAccessException e) {
      if (loc.beWarning()) {
        loc.traceThrowableT(Severity.WARNING, "the session no longer exists in shared memory", e);
      }
      return -1;
    }
  }

}
