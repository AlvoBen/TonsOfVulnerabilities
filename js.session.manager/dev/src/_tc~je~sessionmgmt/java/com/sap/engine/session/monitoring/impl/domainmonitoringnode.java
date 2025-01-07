package com.sap.engine.session.monitoring.impl;

import com.sap.engine.core.Names;
import com.sap.engine.session.monitoring.MonitoringNode;
import com.sap.engine.session.SessionDomain;
import com.sap.engine.session.SessionContext;
import com.sap.engine.session.Session;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author: Pavel Bonev, Mladen Droshev
 * Date: 2006-8-14
 */
public class DomainMonitoringNode<T> extends AbstractMonitoringNode {
  private String contextName = null;

  private static Location loc = Location.getLocation(DomainMonitoringNode.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);

  public DomainMonitoringNode(String contextName, String domainPath) {
    super(domainPath);

    this.contextName = contextName;
    initID(domainPath);
  }

  public DomainMonitoringNode(String contextName, String domainPath, String ID) {
    super(domainPath);

    this.contextName = contextName;
    this.ID = ID;
  }

  public DomainMonitoringNode(String contextName, String domainPath, String ID, T referent) {
    super(domainPath, referent);
    this.contextName = contextName;
    if (ID != null) {
      this.ID = ID;
    } else {
      initID(domainPath);
    }

  }

  public Map<String, MonitoringNode> getChildNodes() {
    Map<String, MonitoringNode> map = new HashMap<String, MonitoringNode>();

    Object ref = this.getReferent();
    if (ref != null) {
      SessionDomain domain = (SessionDomain) ref;
      Iterator iterat = domain.subDomains();

      while (iterat.hasNext()) {
        SessionDomain subdomain = (SessionDomain) iterat.next();

        /* the child of the domain is subdomain*/
        if (subdomain != null) {
          map.put(subdomain.getName(), subdomain.getMonitoringNode());
        } else {
          if (loc.beInfo()) {
            String msg = "getChildNodes(): A null subdomain has been returned for domain:" + contextName +
                    SessionDomain.SEPARATOR + path;
            loc.logT(Severity.INFO, msg);
          }
        }
      }

      iterat = domain.sessions();
      while (iterat.hasNext()) {
        Session session = (Session) iterat.next();

        if (session != null) {
          String sessionID = session.getId();
          map.put(sessionID, new SessionMonitoringNode<Session>(contextName, path, sessionID, session));
        } else {
          if (loc.beInfo()) {
            String msg = "getChildNodes(): A null session has been returned for domain:" + contextName +
                    SessionDomain.SEPARATOR + path;
            loc.logT(Severity.INFO, msg);
          }
        }
      }
    } else {
      if (loc.beInfo()) {
        String msg = "getChildNodes(): A null domain has been returned for path:" + contextName +
                SessionDomain.SEPARATOR + path;
        loc.logT(Severity.INFO, msg);
      }
    }

    return map;
  }

  private void initID(String path) {
    int i = path.lastIndexOf(SessionDomain.SEPARATOR);
    if (i > -1) {
      this.ID = path.substring(0, i);
    } else {
      this.ID = path;
    }
  }

  public MonitoringNode getChildNode(String id) {
    if (loc.beInfo()) {
      String msg = "getChildNode(String subID) is called with param = " + id;
      loc.logT(Severity.INFO, msg);
    }

    Object ref = this.getReferent();
    if (ref != null) {
      SessionContext context = (SessionContext) ref;
      SessionDomain domain = context.findSessionDomain(path);

      if (domain != null) {
        Iterator iterat = domain.subDomains();

        while (iterat.hasNext()) {
          SessionDomain subdomain = (SessionDomain) iterat.next();

          if (subdomain != null) {
            if (subdomain.getName().equals(id)) {
              return subdomain.getMonitoringNode();
            }

          } else {
            if (loc.beInfo()) {
              String msg = "getChildNode(String subID): A null subdomain has been returned for path:" + contextName +
                      SessionDomain.SEPARATOR + path + SessionDomain.SEPARATOR + id;
              loc.logT(Severity.INFO, msg);
            }
          }
        }

        iterat = domain.sessions();

        while (iterat.hasNext()) {
          Session session = (Session) iterat.next();

          if (session != null) {
            String sessionID = session.getId();
            if (sessionID.equals(id)) {
              return new SessionMonitoringNode<Session>(contextName, path, sessionID, session);
            }
          } else {
            if (loc.beInfo()) {
              String msg = "getChildNode(String subID): A null session has been returned for path:" + contextName +
                      SessionDomain.SEPARATOR + path + SessionDomain.SEPARATOR + id;
              loc.logT(Severity.INFO, msg);
            }
          }
        }
      } else {
        if (loc.beInfo()) {
          String msg = "getChildNode(String subID): A null domain has been returned for path:" + contextName +
                  SessionDomain.SEPARATOR + path;
          loc.logT(Severity.INFO, msg);
        }
      }
    } else {
      if (loc.beInfo()) {
        String msg = "getChildNode(String subID): A null session context has been returned for context name '" + contextName + "'";
        loc.logT(Severity.INFO, msg);
      }
    }

    return null;
  }
}
