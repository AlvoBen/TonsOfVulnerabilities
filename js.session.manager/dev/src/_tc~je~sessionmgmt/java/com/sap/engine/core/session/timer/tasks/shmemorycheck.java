/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.core.session.timer.tasks;

import com.sap.engine.core.Names;
import com.sap.bc.proj.jstartup.sadm.*;
import com.sap.engine.core.session.timer.SingleThreadedTask;
import com.sap.engine.core.session.timer.Timer;
import com.sap.engine.session.*;
import com.sap.engine.system.ThreadWrapper;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.util.Vector;
import java.util.Iterator;

/**
 * Author: georgi-s
 * Date: 2005-3-24
 */
public class ShMemoryCheck extends SingleThreadedTask {
  static String DESCRIPTION = "cleanup Web Sessions";
  public static final Location loc = Location.getLocation("com.sap.engine.core.session.timer.tasks_shmCheck", Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);
  public static int period;

  private volatile boolean cleanupOn = false;
  
  public ShMemoryCheck() {
    cleanupWebSessions();
    cleanupEjbSessions();
    ThreadWrapper.setState(ThreadWrapper.TS_WAITING_FOR_TASK); // initial state
    Timer.schedule(this, period, period);
  }

  public void task() {
    if (!cleanupOn) {
      synchronized (this) {
        if (!cleanupOn) {
          cleanupOn = true;
          cleanupWebSessions();
          cleanupOn = false;
        }
      }
    }
  }

  public void cleanupWebSessions() {
    updateMMC(ThreadWrapper.TS_PROCESSING, "cleanup WEB sessions");
    StringBuffer log = null;
    try {
      ShmWebSession[] sessions = ShmWebSession.getAllSessions();

      if (loc.beInfo()) {
        log = new StringBuffer("cleanupWebSessions() is called. Total ShmWebSession:" + sessions.length);
        if (loc.beDebug()) {
          log.append("\n\r");
          for (ShmWebSession session : sessions) {
            log.append(session);
            log.append("\n\r");
          }
        }
      }

      Vector<String> sessionIDs = null;

      for (ShmWebSession session : sessions) {
        try {
          if (session.isCorrupt()) {
            if (loc.beDebug()) {
              log.append("\n\r");
              log.append("Closing corrupt session : ").append(session.getJsessionId());
            }
            session.close();
            // just this check should be sufficient "session.isTimedOut()", because those checks should be made in the
            // jstartup by christian fiderer. Anyway let's leave them for now, just to be on the safe side
          } else if ( (session.getUpdateTime() > 0) && (session.getTimeout() > 0) && (session.isTimedOut() ) ) {
            String sessionId = session.getJsessionId();           

            //It is because the jsession id != session id
            //if the jsession id in SAP format get the session id from it
            //this is syncronized with web container source for getting sessionid from jsession id.
            //TODO - expose a method from Web container to session management for this
            if (sessionId.length() > 44 && sessionId.contains("_SAP") ){
              sessionId = sessionId.substring(0,44);
            }
        
        
            if (loc.beDebug()) {
              log.append("\n\r");
              log.append("Timed out session : ").append(sessionId);
            }
            if (sessionIDs == null) {
              sessionIDs = new Vector<String>();
            }
            sessionIDs.add(sessionId);
          }
        } catch (IllegalStateException ex) {
          loc.traceThrowableT(Severity.INFO, session.toString(), ex);
        }
      }

      if (loc.beInfo() && log != null) {
        log.append("\n\r");
        log.append("All Sessions checked...");
      }

      for (ShmWebSession session : sessions) {
        try {
          session.release();
        } catch (IllegalStateException ex) {
          loc.traceThrowableT(Severity.INFO, session.toString(), ex);
        }
      }

      if (loc.beInfo() && log != null) {
        log.append("\n\r");
        log.append("All Sessions released...");
      }

      if (sessionIDs != null) {
        if (loc.beInfo() && log != null) {
          log.append("\n\r");
          log.append("Timeouted session found, notifying domains...");
        }
        
        SessionContext ctx = SessionContextFactory.getInstance().getSessionContext(SessionContext.HTTP_CONTEXT, false);
        Iterator iter = ctx.rootDomains();
        while (iter.hasNext()) {
          SessionDomain sd = (SessionDomain) iter.next();
          for(String sID:sessionIDs) {
            SessionHolder sh = sd.getSessionHolder(sID);
// this will cause the domain to create a runtime session model
// and invalidate it if it is expired
            sh.sessionExist();
            sh.releaseAccess();
          }
        }
      }

    } catch (Exception e) {
      loc.traceThrowableT(Severity.ERROR, "", e);
    } finally {
      if (loc.beInfo() && log != null) {
        loc.infoT(log.toString());
      }
      updateMMC(ThreadWrapper.TS_WAITING_FOR_TASK, null);
    }
  }

  public void cleanupEjbSessions() {
    updateMMC(ThreadWrapper.TS_PROCESSING, "cleanup EJB sessions");
    StringBuffer log = null;
    try {
      if (loc.beInfo()) {
        log = new StringBuffer("cleanupEjbSessions() is called...");
      }
    } catch (Exception e) {
      loc.traceThrowableT(Severity.INFO, "", e);
    } finally {
      if (loc.beInfo() && log != null) {
        loc.infoT(log.toString());
      }
      updateMMC(ThreadWrapper.TS_WAITING_FOR_TASK, null);
    }
  }


//  private ShmEjbSession initShmEJBSession(String appName, String ejbName, String sessionId) throws ShmException {
//    ShmEjb shmEjb= new ShmApplication(appName).addEjb(ejbName, -1);
//    if (shmEjb != null) {
//      int backStore = ShmWebSession.BS_NONE;
//      try {
//        return new ShmEjbSession(shmEjb,sessionId, backStore, ShmEjbSession.EJR_NONE);
//      } catch (ShmException ex)  {
//        loc.traceThrowableT(Severity.DEBUG,"Cannot create session object", ex);
//      }
//    }
//    return null;
//  }


  public String taskDescription() {
    return DESCRIPTION;
  }
}
