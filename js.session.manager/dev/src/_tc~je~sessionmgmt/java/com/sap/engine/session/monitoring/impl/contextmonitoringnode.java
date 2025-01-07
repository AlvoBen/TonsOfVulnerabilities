package com.sap.engine.session.monitoring.impl;

import com.sap.engine.core.Names;
import com.sap.engine.session.monitoring.MonitoringNode;
import com.sap.engine.session.SessionContext;
import com.sap.engine.session.SessionDomain;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author: Pavel Bonev, Mladen Droshev
 * Date: 2006-8-14
 */
public class ContextMonitoringNode<T> extends AbstractMonitoringNode {
  private static Location loc = Location.getLocation(ContextMonitoringNode.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);
  
  public ContextMonitoringNode(String path) {
    super(path);
    this.ID = path;
  }

  public ContextMonitoringNode(String path, T referent){
    super(path, referent);
    this.ID = path;
  }

  public Map<String, MonitoringNode> getChildNodes(){
    Map<String, MonitoringNode> map = new HashMap<String, MonitoringNode>();

    Object ref = this.getReferent(); 
    if(ref != null) {
      SessionContext sContext = (SessionContext)ref;
      Iterator iterat = sContext.rootDomains();
      while (iterat.hasNext()) {
        SessionDomain domain = (SessionDomain)iterat.next();
        if (domain != null) {
          map.put(domain.getName(), domain.getMonitoringNode());
        } else {
          if (loc.beInfo()) {
            String msg = "getChildNodes(): A null domain has been returned!";
            loc.logT(Severity.INFO, msg);
          }
        }
      }
    } else {
      if (loc.beInfo()) {
        String msg = "getChildNodes(): A null context has been returned for context name: "+ID;
        loc.logT(Severity.INFO, msg);
      }
    }

    return map;
  }

  public MonitoringNode getChildNode(String domainID){
    if (loc.beInfo()) {
      String msg = "getChildNode(String domainID) is called with param = " + domainID;
      loc.logT(Severity.INFO, msg);
    }

    Object ref = this.getReferent();
    if(ref != null) {
      SessionContext sContext = (SessionContext)ref;
      SessionDomain sDomain = sContext.findSessionDomain(domainID);
      if(sDomain != null){
        sDomain.getMonitoringNode();
      } else {
        if (loc.beInfo()) {
          String msg = "getChildNode(String domainID): A null domain has been returned for domain name: " + domainID;
          loc.logT(Severity.INFO, msg);
        }
      }
    } else {
      if (loc.beInfo()) {
        String msg = "getChildNode(String domainID): A null context has been returned for context name: " + ID;
        loc.logT(Severity.INFO, msg);
      }
    }

    return null;
  }
}
