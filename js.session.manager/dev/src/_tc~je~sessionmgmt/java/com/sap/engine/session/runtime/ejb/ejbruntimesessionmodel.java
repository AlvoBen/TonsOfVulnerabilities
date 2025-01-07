package com.sap.engine.session.runtime.ejb;

import com.sap.engine.session.runtime.RuntimeSessionModel;
import com.sap.engine.session.runtime.SessionFailoverMode;
import com.sap.engine.session.SessionDomain;
import com.sap.engine.session.Session;
import com.sap.engine.session.spi.persistent.PersistentStorageException;
import com.sap.engine.session.exec.SessionExecContext;
import com.sap.engine.session.usr.UserContext;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Category;
import com.sap.bc.proj.jstartup.sadm.ShmException;
import com.sap.bc.proj.jstartup.sadm.ShmEjbSession;
import com.sap.bc.proj.jstartup.sadm.ShmWebSession;
import com.sap.bc.proj.jstartup.sadm.ShmEjb;

public class EJBRuntimeSessionModel extends RuntimeSessionModel {
  ShmEjbSession shmSlot;

  public EJBRuntimeSessionModel(String sessionId, SessionDomain domain, SessionFailoverMode failoverMode) {
    super(sessionId, domain, failoverMode);
    try {
      initShmEJBMonitoring();
    } catch (ShmException e) {
      loc.traceThrowableT(Severity.WARNING, "Cannot create session object", e);
    }
  }

  public EJBRuntimeSessionModel(String sessionId, SessionDomain domain, Session session,
      SessionFailoverMode failoverMode) {
    super(sessionId, domain, session, failoverMode);
    try {
      initShmEJBMonitoring();
    } catch (ShmException e) {
      loc.traceThrowableT(Severity.WARNING, "Shm ejb session object can't be created.", e);
    }
  }

  protected synchronized void activate(Object info) {
    super.activate(info);
  }

  protected void beforeActivate(Object info) {
    if (!isActive()) {
      if (SessionExecContext.getExecutionContext().isLoadbalanced()) {
        try {
          if (persistentModel() != null && !isSticky()) {
            setSession(internalLoadSession());
          } else if (persistentModel == null) {
            persistentModel = domain.getSoftShutdownPersistentSessionModel(this);
            setSession(internalLoadSession());
            persistentModel = null;
          }
        } catch (PersistentStorageException e) {
          loc.traceThrowableT(Severity.WARNING, "Problem while reading session " + compositeName(), e);
        }
      }
    }
  }

  protected void postActivate(Object info, boolean rolback) {
    if (SessionExecContext.isEnabledSharedMemoryMonitoring) {
      if (shmSlot != null) {
        UserContext current = UserContext.getCurrentUserContext();
        try {
          if (current != null) {
            shmSlot.startRequest(current.getUser());
          } else {
            shmSlot.startRequest("System");
          }
        } catch (ShmException e) {
          loc.traceThrowableT(Severity.WARNING, "Cannot update session object", e);
        }
      }
    }
  }

  protected void postComit(Object info) {
    super.postComit(info);
    if (SessionExecContext.isEnabledSharedMemoryMonitoring) {
      if (shmSlot != null) {
        int flag = shmSlot.getFlags();
        Session session = session();
        if (session != null) {
          flag = session.isSticky() ? 1 : flag;
        }
        try {
          shmSlot.endRequest(flag, domain.size(sessionId()), 0, "None");
        } catch (ShmException e) {
          loc.traceThrowableT(Severity.WARNING, "Shm ejb session object can't be updated.", e);
        }
      }
    }
  }

  protected void postDestroy() {
    if (SessionExecContext.isEnabledSharedMemoryMonitoring) {
      try {
        if (shmSlot != null)
          shmSlot.close();
      } catch (ShmException e) {
        loc.traceThrowableT(Severity.WARNING, "Cannot close shared memory EJB session", e);
      }
    }
  }

  private void initShmEJBMonitoring() throws ShmException {
    if (SessionExecContext.isEnabledSharedMemoryMonitoring) {
      ShmEjb shmEjb = ((EjbSessionDomain) domain).getShmEjbObject();
      if (shmEjb != null) {
        int backStore = ShmWebSession.BS_NONE;
        if (persistentModel != null) {
          String className = persistentModel.getClass().getName();
          if (className.contains("ShMemoryPersistentSessionModel")) {
            backStore = ShmWebSession.BS_SHCLOSURE;
          } else if (className.contains("DBPersistentSessionModel")) {
            backStore = ShmWebSession.BS_DATABASE;
          } else if (className.contains("FilePersistentSessionModel")) {
            backStore = ShmWebSession.BS_FILE;
          } else if (className.equals("MemoryPersistentSessionModel")) {
            backStore = ShmWebSession.BS_SHMEM;
          }
        }
        try {
          shmSlot = new ShmEjbSession(shmEjb, sessionId(), backStore, ShmEjbSession.EJR_NONE);
        } catch (ShmException ex) {
          Category.SYS_SERVER.errorT(loc,
              "Can not create shm ejb object. Possible out of shm free slots. Check the shared memory configuration");
          loc.traceThrowableT(Severity.DEBUG, "Shm ejb session object can't be created.", ex);
        }
      }
    }
  }
}
