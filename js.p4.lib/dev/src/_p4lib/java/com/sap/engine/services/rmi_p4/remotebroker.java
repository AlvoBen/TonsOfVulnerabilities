package com.sap.engine.services.rmi_p4;

import com.sap.engine.lib.lang.Convert;
import com.sap.engine.interfaces.cross.Connection;

import java.util.Properties;

/**
 * Author: Asen Petrov
 * Date: 2006-2-20
 * Time: 12:48:29
 */
public class RemoteBroker implements com.sap.engine.interfaces.cross.RemoteBroker {

  private Connection repliable = null;

  private int serverId = 0;
  private int brokerId = 0;
  private String connectionType;
  private String host;
  private int port;

  public RemoteBroker(Connection repliable, String host, int port, Properties properties) {
   this.repliable = repliable;
   this.host = host;
   this.port = port;
   if(properties != null) {
    String dsid = (String)properties.get("DestinationServerId");
    if (dsid != null) {
      serverId = Integer.parseInt(dsid);
    }
    connectionType = (String)properties.get("TransportLayerQueue");
   }
    if (connectionType == null) {
      connectionType = "None";
    }
  }

  public Object resolveInitialReference(String name, Class stubClass) throws Exception {
    StubBaseInfo info = (StubBaseInfo)P4ObjectBroker.init().resolveInitialReference(name, repliable, serverId);
    if (serverId == 0) {
      serverId = info.server_id;
    }
    brokerId = info.ownerId;
    return P4ObjectBroker.init().narrow(info, stubClass, connectionType);
  }

  public int getServerId() {
    return serverId;
  }

  public int getBrokerId() {
    return brokerId;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getConnectionType() {
    return connectionType;
  }

  protected Connection getConnection() {
    return repliable;  
  }

  public String getIdentity() {
    return String.valueOf(serverId);
  }

  public String toString() {
    return "P4 RemoteBroker: host:" + host + "; port:" + port + "; connection type:" + connectionType + "; server id:" + serverId + "; broker id:"+brokerId;
  }
}
