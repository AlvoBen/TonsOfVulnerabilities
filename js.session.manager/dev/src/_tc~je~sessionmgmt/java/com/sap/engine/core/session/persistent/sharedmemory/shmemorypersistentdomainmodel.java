package com.sap.engine.core.session.persistent.sharedmemory;

import com.sap.engine.core.Names;
import com.sap.engine.session.spi.persistent.PersistentDomainModel;
import com.sap.engine.session.spi.persistent.PersistentStorageException;
import com.sap.engine.session.spi.persistent.PersistentSessionModel;
import com.sap.jvm.session.SharedSessionDomain;
import com.sap.jvm.session.SharedSession;
import com.sap.jvm.session.SharedDataAccessException;
import com.sap.jvm.session.OutOfSharedMemoryException;
import com.sap.jvm.session.SharedSessionStore;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.SimpleLogger;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Set;

public class ShMemoryPersistentDomainModel implements PersistentDomainModel {

  private static Location loc = Location.getLocation(ShMemoryPersistentDomainModel.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);

  private SharedSessionDomain shMemoryDomain = null;
  private String domainName = null;

  public ShMemoryPersistentDomainModel(String domainName) throws PersistentStorageException {
    try {
      this.shMemoryDomain = SharedSessionStore.getInstance().getSharedSessionDomain(domainName);
      if (this.shMemoryDomain == null) {
        this.shMemoryDomain = SharedSessionStore.getInstance().createSharedSessionDomain(domainName);
      }
      this.domainName = domainName;
    } catch (OutOfSharedMemoryException e) {
      SimpleLogger.log(Severity.ERROR, Category.SYS_SERVER, PersistentDomainModel.LOC, "ASJ.ses.ps0005", 
	  "Out of shared memory. Failover will not work. Check the shared memory settings.");
      throw new PersistentStorageException(e);
    }
  }

  public PersistentDomainModel createSub(String subName) throws PersistentStorageException {
    return new ShMemoryPersistentDomainModel(domainName + "#" + subName);
  }

  public PersistentDomainModel[] listSubs() {
    return new PersistentDomainModel[0];
  }

  public void removeSub(String subName) throws PersistentStorageException {
    SharedSessionDomain _subDomain = SharedSessionStore.getInstance().getSharedSessionDomain(domainName + "#" + subName);
    if (_subDomain != null) {
      _subDomain.destroy();
    } else {
      throw new PersistentStorageException("There is not subDomain<" + subName + ">");
    }
  }

  public PersistentSessionModel createModel(String sessionId) throws PersistentStorageException {
    return new ShMemoryPersistentSessionModel(this.shMemoryDomain, sessionId);
  }

  public PersistentSessionModel getModel(String sessionId) throws PersistentStorageException {
    SharedSession s = null;
    try {
      s = this.shMemoryDomain.getSharedSession(sessionId);
    } catch (SharedDataAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    if (s != null) {
    return new ShMemoryPersistentSessionModel(this.shMemoryDomain, s, sessionId);
    } else {
      return null;
    }
  }

  public void remove(PersistentSessionModel model) throws PersistentStorageException {
    model.destroy();
  }

  public Iterator sessionModels() {

    ArrayList<PersistentSessionModel> _sessions = new ArrayList<PersistentSessionModel>();
    Set<SharedSession> _allSessions;
    try {
      _allSessions = this.shMemoryDomain.sessions();
    } catch (SharedDataAccessException e) {
      if (loc.beWarning()) {
        loc.traceThrowableT(Severity.WARNING, "Domain no longer exists in shared memory", e);
      }
      return null;
    }

    for (SharedSession _s : _allSessions) {
      try {
        PersistentSessionModel _pModel = new ShMemoryPersistentSessionModel(this.shMemoryDomain, _s, _s.getName());
        _sessions.add(_pModel);
      } catch (SharedDataAccessException e) {
        if (loc.beWarning()) {
          loc.traceThrowableT(Severity.WARNING, "", e);
        }
      }
    }

    if (_sessions.size() == 0) {
      return null;
    }
    return _sessions.iterator();
  }

  public Iterator allExpired() {
    return null;
  }

  public void removeExpired() throws PersistentStorageException {
  }

  public void destroy() throws PersistentStorageException {
    this.shMemoryDomain.destroy();
  }
}
