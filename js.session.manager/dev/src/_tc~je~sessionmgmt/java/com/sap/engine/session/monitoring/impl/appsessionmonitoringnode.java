package com.sap.engine.session.monitoring.impl;

import com.sap.engine.session.Session;
import com.sap.engine.session.monitoring.MonitoringNode;
import com.sap.engine.session.runtime.RuntimeSessionModel;
import com.sap.engine.session.data.SessionChunk;
import com.sap.engine.session.exec.ClientContextImpl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Date;

/**
 * @author: Mladen Droshev
 * Date: 2007-5-10
 */
public class AppSessionMonitoringNode<ClientSession> extends AbstractMonitoringNode {

  
  public AppSessionMonitoringNode(String name, ClientSession s){
    super(name, s);
    this.ID = name;
  }

  public Map<String, MonitoringNode> getChildNodes() {
    Map<String, MonitoringNode> map = new HashMap<String, MonitoringNode>();

    Object ref = this.getReferent();
    if (ref != null ) {
      Session session = ((RuntimeSessionModel) ref).session();
      if (session != null) {
        Iterator iterat = session.chunksIterator();
  
        while (iterat.hasNext()) {
          SessionChunk chunk = (SessionChunk) iterat.next();
          if (chunk != null && chunk instanceof MonitoringNode) {
            String id = ((MonitoringNode) chunk).getID();
            map.put(id, (MonitoringNode) chunk);
          }
        }
      }
    }

    return map;
  }

  public MonitoringNode getChildNode(String id) {
    Object ref = this.getReferent();
    if (ref != null ) {
      Session session = ((RuntimeSessionModel) ref).session();
      if( session != null ) {
        Object chunk = session.getChunkData(id);
  
        if (chunk != null && chunk instanceof MonitoringNode) {
          return (MonitoringNode) chunk;
        }
      }
    }
    return null;
  }

  public String getSessionId(){
    Object ref = this.getReferent();
    if(ref != null){
      return ((RuntimeSessionModel)ref).getSessionId();
    }
    return null;
  }

  public String getAppName(){
    Object ref = this.getReferent();
    if(ref != null && ((RuntimeSessionModel)ref).domain() != null){
      return ((RuntimeSessionModel)ref).domain().getName();
    }
    return null;
  }

  public String getUserName(){
    Object ref = this.getReferent();
    if(ref != null && ((RuntimeSessionModel)ref).domain() != null){
      return (ClientContextImpl.getByClientId(((RuntimeSessionModel)ref).sessionId())).getUser();
    } else {
      return null;
    }
  }

  public Date creationTime(){
    Object ref = this.getReferent();
    if(ref != null){
      return new Date(((RuntimeSessionModel)ref).getCreationTime());
    }
    return null;
  }

  public Date expTime(){
    Object ref = this.getReferent();
    if(ref != null){
      return new Date(((RuntimeSessionModel)ref).getCreationTime() + ((RuntimeSessionModel)ref).maxInactiveInterval());
    }
    return null;
  }

  public boolean isSticky(){
    Object ref = this.getReferent();
    return ref != null && ((RuntimeSessionModel) ref).isSticky();
  }

  public String persStorage(){
    Object ref = this.getReferent();
    if(ref != null && ((RuntimeSessionModel)ref).persistentModel() != null){
      return ((RuntimeSessionModel)ref).persistentModel().toString();
    }

    return null;
  }
  
  public String getIP() {
	  Object ref = this.getReferent();
	  if(ref != null && ((RuntimeSessionModel)ref).domain() != null){
		  ClientContextImpl client_context = ClientContextImpl.getByClientId(((RuntimeSessionModel)ref).sessionId());
	      return client_context != null ? client_context.getIP() : null;
	  } else {
	      return null;
	    }	  
  }
  
  public Date getLastAcessed() {
	  Object ref = this.getReferent();
	  if(ref != null && ((RuntimeSessionModel)ref).domain() != null){
	      return new Date((ClientContextImpl.getByClientId(((RuntimeSessionModel)ref).sessionId())).getLastAcessed());
	  } else {
	      return null;
	    }	  
  }
}
