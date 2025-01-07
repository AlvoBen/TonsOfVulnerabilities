package com.sap.engine.core.session.management;

import com.sap.engine.session.SessionContextFactory;
import com.sap.engine.session.SessionContext;
import com.sap.engine.session.SessionDomain;
import com.sap.engine.session.Session;

import java.util.Iterator;

/**
 * User: pavel-b
 * Date: 2006-7-24
 * Time: 15:00:56
 */
public class SessionContextMonitor {

  public int getOpenWebSessionCount() {
    return countByType("HTTP");
  }

  public int getOpenEJBSessionCount() {
    return countByType("EJB");
  }

   public int getActiveWebSessionCount() {
     return countByType("HTTP",System.currentTimeMillis());
  }

  private int countByType(String type) {
    return countByType(type, -1);
  }

  private int countByType(String type, long currentTime) {
    int counter = 0;

    SessionContextFactory factory = SessionContextFactory.getInstance();
    Iterator iterat = factory.contexts();
    while (iterat.hasNext()) {
      SessionContext context = (SessionContext)iterat.next();

      if (context.getName().toUpperCase().indexOf(type) > -1) {
        Iterator iterat2 = context.rootDomains();
        while (iterat2.hasNext()) {
          SessionDomain domain = (SessionDomain)iterat2.next();

          counter += countDomain(domain, currentTime);
        }

        break;
      }
    }

    return counter;
  }


  private int countDomain(SessionDomain domain, long currentTime) {
    int counter = 0;

    if (currentTime > 0) {
      Iterator iterat = domain.sessions();
      while (iterat.hasNext()) {
        Session session = (Session)iterat.next();
        long lastAccessedTime = session.getLastAccessedTime();
        float minutes = (currentTime-lastAccessedTime)/60000f;

        if (minutes <= 10) {
          counter++;
        }
      }
    } else {
      counter = domain.size();
    }

    Iterator iterat = domain.subDomains();
    while (iterat.hasNext()) {
      SessionDomain subdomain = (SessionDomain)iterat.next();
      counter += countDomain(subdomain, currentTime);
    }

    return counter;
  }
}
