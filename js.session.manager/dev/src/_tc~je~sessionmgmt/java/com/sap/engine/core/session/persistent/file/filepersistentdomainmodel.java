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

import com.sap.engine.session.spi.persistent.PersistentDomainModel;
import com.sap.engine.session.spi.persistent.PersistentStorageException;
import com.sap.engine.session.spi.persistent.PersistentSessionModel;
import com.sap.engine.core.session.persistent.file.util.FilenameFilterImpl;

import java.util.Iterator;
import java.util.ArrayList;
import java.io.File;
import java.io.FilenameFilter;
/*
 * Author: i024157 /Georgi Stanev/ 
 */

public class FilePersistentDomainModel implements PersistentDomainModel {
  private File dir;

  public FilePersistentDomainModel(File parentDir, String domainName) throws PersistentStorageException {
    String domainDirName = domainName.replace('\\', '/');
    domainDirName = domainDirName.replace('/', '_');

    this.dir = new File(parentDir, domainDirName);

    if (!this.dir.exists()) {
      this.dir.mkdirs();
    }
  }


  private FilePersistentDomainModel(File domainDir) {
    this.dir = domainDir;
  }

  public PersistentDomainModel createSub(String subName) throws PersistentStorageException {
    return new FilePersistentDomainModel(dir, subName);
  }

  // list subs form persistent layer
  public PersistentDomainModel[] listSubs() {
    FilenameFilter filter = new FilenameFilterImpl(FilenameFilterImpl.DOMAIN_DIR_PREFIX, FilenameFilterImpl.DIRS_ONLY);

    File[] subdomainFiles = dir.listFiles(filter);
    FilePersistentDomainModel[] domains = new FilePersistentDomainModel[subdomainFiles.length];
    for (int i=0;i<domains.length;i++) {
      domains[i] = new FilePersistentDomainModel(subdomainFiles[i]);
    }

    return domains;
  }

  //remove sum domain model
  public void removeSub(String subname) throws PersistentStorageException {
    String domainDirName = subname.replace('\\', '/');
    domainDirName = domainDirName.replace('/', '_');

    File file = new File(dir, domainDirName);
    FilePersistentDomainModel model = new FilePersistentDomainModel(file);

    model.destroy();
  }

  public PersistentSessionModel createModel(String sessionId) throws PersistentStorageException {
    return new FilePersistentSessionModel(dir, sessionId);
  }

  public PersistentSessionModel getModel(String sessionId) throws PersistentStorageException {
    if (FilePersistentSessionModel.sessionExists(dir, sessionId)) {
      return new FilePersistentSessionModel(dir, sessionId);
    }

    return null;
  }

  public void remove(PersistentSessionModel model) throws PersistentStorageException {
    model.destroy();
  }

  public Iterator sessionModels() {
    ArrayList<FilePersistentSessionModel> list = new ArrayList<FilePersistentSessionModel>();

    FilenameFilter filter = new FilenameFilterImpl(FilenameFilterImpl.SESSION_DIR_PREFIX,
                                                   FilenameFilterImpl.DIRS_ONLY);

    File[] sessionFiles = dir.listFiles(filter);
    for (File sessionFile : sessionFiles) {
      try {
        list.add(new FilePersistentSessionModel(sessionFile));
      } catch (PersistentStorageException e) {
        e.printStackTrace();
      }
    }

    return list.iterator();
  }

   public synchronized Iterator allExpired() {
    ArrayList<FilePersistentSessionModel> list = new ArrayList<FilePersistentSessionModel>();

    Iterator iterat = sessionModels();
    while (iterat.hasNext()) {
      FilePersistentSessionModel model = (FilePersistentSessionModel)iterat.next();

      long curTime = System.currentTimeMillis();
      if (model.expTime() >= curTime) {
        list.add(model);
      }
    }

    return list.iterator();
  }

  /**
   * Removes all expired entries from the storage.
   */
  public synchronized void removeExpired() throws PersistentStorageException {
    Iterator iterat = sessionModels();
    while (iterat.hasNext()) {
      FilePersistentSessionModel model = (FilePersistentSessionModel)iterat.next();
      long curTime = System.currentTimeMillis();

      if (model.expTime() >= curTime) {
        model.destroy();
      }
    }
  }

  public synchronized void destroy() throws PersistentStorageException {
    PersistentDomainModel[] domains = listSubs();
    for (PersistentDomainModel domain : domains) {
      domain.destroy();
    }

    Iterator iterat = sessionModels();
    while (iterat.hasNext()) {
      FilePersistentSessionModel model = (FilePersistentSessionModel)iterat.next();
      model.destroy();
    }

    dir.delete();
  }
}
