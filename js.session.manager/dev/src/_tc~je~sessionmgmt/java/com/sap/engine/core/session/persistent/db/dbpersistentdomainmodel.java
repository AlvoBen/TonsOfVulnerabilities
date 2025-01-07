package com.sap.engine.core.session.persistent.db;

import com.sap.engine.session.spi.persistent.PersistentDomainModel;
import com.sap.engine.session.spi.persistent.PersistentSessionModel;
import com.sap.engine.session.spi.persistent.PersistentStorageException;
import com.sap.engine.session.SessionDomain;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * User: I028674
 * Date: 2006-11-16
 * Time: 16:22:32
 */
public class DBPersistentDomainModel implements PersistentDomainModel {
  private int hash = 0;
  private int counter = -1;
  private int parentHash = 0;
  private int parentCounter = -1;
  private String path = null;
  private String context = null;

  private DBStorage storage = null;

  public DBPersistentDomainModel(String context, String path, int hash, int counter, int parentHash, int parentCounter, DBStorage storage) {
    this.context = context;
    this.path = path;

    this.hash = hash;
    this.counter = counter;

    this.parentHash = parentHash;
    this.parentCounter = parentCounter;

    this.storage = storage;
  }

  public DBPersistentDomainModel createSub(String name) throws PersistentStorageException {
    String subdomainPath = context +  SessionDomain.SEPARATOR + path + SessionDomain.SEPARATOR + name;
    return (DBPersistentDomainModel)storage.createDomainModel(context, subdomainPath, hash, counter);
  }

  public DBPersistentDomainModel[] listSubs() {
    DBPersistentDomainModel[] models;

    try {
      ArrayList list = storage.getSubdomains(hash, counter);
      models = new DBPersistentDomainModel[list.size()];

      list.toArray(models);
    } catch (Exception e) {
      models = null;
    }

    return models;
  }

  public void removeSub(String name) throws PersistentStorageException {
    String subdomainPath = context +  SessionDomain.SEPARATOR + path + SessionDomain.SEPARATOR + name;
    DBPersistentDomainModel model = (DBPersistentDomainModel)storage.getDomainModel(context, subdomainPath);
    model.destroy();
  }

  public DBPersistentSessionModel createModel(String sessionID) throws PersistentStorageException {
    return storage.createSessionModel(hash, counter, sessionID);
  }

  public DBPersistentSessionModel getModel(String sessionID) throws PersistentStorageException {
    return storage.selectSessionModel(hash, counter, sessionID);
  }

  public void remove(PersistentSessionModel model) throws PersistentStorageException {
    model.destroy();
  }

  public Iterator sessionModels() {
    Iterator iterat = null;

    try {
      iterat = storage.selectSessionModels(hash, counter);
    } catch (PersistentStorageException e) {
      DBStorage.log(e);
    }

    return iterat;
  }

  public Iterator allExpired() {
    Iterator iterat = null;

    try {
      iterat = storage.getExpiredSessions(hash, counter);
    } catch (PersistentStorageException e) {
      DBStorage.log(e);
    }

    return iterat;
  }

  public void removeExpired() {
    try {
      storage.removeExpiredSessions(hash, counter);
    } catch (PersistentStorageException e) {
      DBStorage.log(e);
    }
  }

  public void destroy() {
    try {
      storage.removeDomainModel(hash, counter);
    } catch (Exception e) {
      DBStorage.log(e);
    }
  }
}
