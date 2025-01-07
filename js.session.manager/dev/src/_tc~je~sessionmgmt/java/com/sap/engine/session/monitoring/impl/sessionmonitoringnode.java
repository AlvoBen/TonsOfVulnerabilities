package com.sap.engine.session.monitoring.impl;

import com.sap.engine.core.Names;
import com.sap.engine.session.monitoring.MonitoringNode;
import com.sap.engine.session.SessionDomain;
import com.sap.engine.session.Session;
import com.sap.engine.session.data.SessionChunk;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author: Pavel Bonev, Mladen Droshev
 * Date: 2006-8-14
 */
public class SessionMonitoringNode<T> extends AbstractMonitoringNode {
  private String contextName = null;

  private static Location loc = Location.getLocation(SessionMonitoringNode.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);

  public SessionMonitoringNode(String contextName, String domainPath, String ID, T referent) {
    super(domainPath, referent);

    this.ID = ID;//session Id
    this.contextName = contextName;
  }


  public SessionMonitoringNode(String contextName, String domainPath, String ID) {
    super(domainPath, null);

    this.ID = ID;
    this.contextName = contextName;
  }

  public Map<String, MonitoringNode> getChildNodes() {
    Map<String, MonitoringNode> map = new HashMap<String, MonitoringNode>();

    Object ref = this.getReferent();
    if (ref != null) {
      Session session = (Session) ref;
      Iterator iterat = session.chunksIterator();

      while (iterat.hasNext()) {
        SessionChunk chunk = (SessionChunk) iterat.next();
        if (chunk != null && chunk instanceof MonitoringNode) {
          String id = ((MonitoringNode) chunk).getID();
          map.put(id, (MonitoringNode) chunk);
        }
      }
    } else {
      if (loc.beInfo()) {
        String msg = "getChildNodes(): A null session has been returned for path:" + contextName +
                SessionDomain.SEPARATOR + path + SessionDomain.SEPARATOR + ID;
        loc.logT(Severity.INFO, msg);
      }
    }

    return map;
  }

  public MonitoringNode getChildNode(String chunkID) {
    if (loc.beInfo()) {
      String msg = "getChildNode(String chunkID) is called with param = " + chunkID;
      loc.logT(Severity.INFO, msg);
    }

    Object ref = this.getReferent();
    if (ref != null) {
      Session session = (Session) ref;
      Object chunk = session.getChunkData(chunkID);

      if (chunk != null && chunk instanceof MonitoringNode) {
        return (MonitoringNode) chunk;
      }

    } else {
      if (loc.beInfo()) {
        String msg = "getChildNode(String chunkID): A null session has been returned for path:" + contextName +
                SessionDomain.SEPARATOR + path + SessionDomain.SEPARATOR + ID;
        loc.logT(Severity.INFO, msg);
      }
    }

    return null;
  }

}
