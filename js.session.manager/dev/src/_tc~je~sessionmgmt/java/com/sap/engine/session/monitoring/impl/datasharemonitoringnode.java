package com.sap.engine.session.monitoring.impl;

import com.sap.engine.session.monitoring.MonitoringNode;

import java.util.Map;

/**
 * User: I024084 e-mail:mladen.droshev@sap.com
 * Date: 2007-4-24
 */
public class DatashareMonitoringNode  extends AbstractMonitoringNode {

  public static final int HASHTABLE_TYPE = 0;
  public static final int QUEUE_TYPE = 1;

  public static final String HASHTABLE = "Hashtable Type";
  public static final String QUEUE = "Queue Type";

  private int type = -1;

  public DatashareMonitoringNode(int type){
    super(DatashareMonitoringNode.dataType(type));
    this.type = type;

  }

  public Map<String, MonitoringNode> getChildNodes() {
    return null;
  }

  public MonitoringNode getChildNode(String id) {
    return null;
  }

  public static String dataType(int type){
    switch(type){
      case HASHTABLE_TYPE:{
        return HASHTABLE;
      }
      case QUEUE_TYPE:{
        return QUEUE;
      }
    }

    return null;
  }

  public String toString(){
    return "DataShareMonitoringNode<" + type + ">";
  }

  public String[] listClasses(){
    if(type == HASHTABLE_TYPE){
      UserContextMonitoringNode.listHashtableClasses();
    } else if(type == QUEUE_TYPE) {
      UserContextMonitoringNode.listQueueClasses();
    }
    return null;
  }

  public String[] listLoaders(){
    if(type == HASHTABLE_TYPE){
      UserContextMonitoringNode.listHashtableLoaders();
    } else if(type == QUEUE_TYPE) {
      UserContextMonitoringNode.listQueueLoaders();
    }
    return null;
  }

  public int objectsCount(){
    int size = 0;
    if(type == HASHTABLE_TYPE){
      return UserContextMonitoringNode.getHashtablesSize();
    } else if(type == QUEUE_TYPE){
      return UserContextMonitoringNode.getQueuesSize();
    }
    return size;
  }

  public int objectsCountPerLoader(String loaderName){
    int size = 0;
    String[] clazes;
    if(type == HASHTABLE_TYPE){
      clazes = UserContextMonitoringNode.listHashClassesPerLoader(loaderName);
      if(clazes != null && clazes.length > 0){
        for(String claz: clazes){
          size += UserContextMonitoringNode.getHashSize(claz, loaderName);
        }
      }
    } else if(type == QUEUE_TYPE){
      clazes = UserContextMonitoringNode.listQueueClassesPerLoader(loaderName);
      if(clazes != null && clazes.length > 0){
        for(String claz: clazes){
          size += UserContextMonitoringNode.getQueueSize(claz, loaderName);
        }

      }
    }

    return size;
  }
}
