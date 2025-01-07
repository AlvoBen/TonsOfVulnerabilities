package com.sap.engine.services.security.login;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import com.sap.engine.boot.SystemProperties;
import com.sap.engine.session.usr.LoginSessionInterface;
import com.sap.tc.logging.Location;

public class SecuritySessionPool {
  
  private final static Location TRACER = SecurityContext.TRACER;
  private final static boolean IN_SERVER = SystemProperties.getBoolean("server");
  private static SecuritySessionPool pool = new SecuritySessionPool();

  public static SecuritySessionPool getPool() {
    return pool;
  }

  private SecuritySessionPool() {
  }
  
  public SecuritySession[] listSessions() {
    List<SecuritySession> values = new ArrayList<SecuritySession>();
    
    Iterator<LoginSessionInterface> iterator = SecurityContext.getLoginAccessor().getClientLoginSessions().iterator();
    
    while (iterator.hasNext()) {
      SecuritySession session = new SecuritySessionDTO(iterator.next());
      
      if (!session.isAnonymous()) {
        values.add(session);
      }
    }
    
    SecuritySession[] result = new SecuritySession[values.size()];
    values.toArray(result);

    return result;
  }

  /**
   * @deprecated replace with direct call to (SecuritySession) session).markForDelte()
   *             when listeners are no longer needed.
   */
  public void removeSessions(SecuritySession session) {
    if(IN_SERVER && TRACER.beInfo()) {
      TRACER.infoT("Terminating {0}", new Object[] {session});
    }
    
    Iterator<LoginSessionInterface> iterator = SecurityContext.getLoginAccessor().getClientLoginSessions().iterator();
    
    while (iterator.hasNext()) {
      LoginSessionInterface loginSession = iterator.next();
      
      if (session.getSessionNumber() == loginSession.getSessionNumber()) {
        if(IN_SERVER && TRACER.beDebug()) {
          TRACER.debugT("Terminating {0}", new Object[] {loginSession});
        }
        
        SecurityContext.getLoginAccessor().terminate(loginSession.getClientId());
        return;
      }
    }
  }

}
