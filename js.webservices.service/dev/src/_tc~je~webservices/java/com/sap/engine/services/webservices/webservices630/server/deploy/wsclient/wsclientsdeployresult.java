package com.sap.engine.services.webservices.webservices630.server.deploy.wsclient;

import com.sap.engine.services.webservices.webservices630.server.deploy.common.DeployResult;
import com.sap.engine.services.webservices.runtime.definition.wsclient.WSClientRuntimeInfo;

/**
 * Title: WSClientsDeployResult
 * Description: The class is a container for ws clients deployment generated data.
 * Copyright: Copyright (c) 2004
 * Company: Sap Labs Sofia
 * 
 * @author Dimitrina Stoyanova
 * @version 6.30
 */

public class WSClientsDeployResult extends DeployResult {

  WSClientRuntimeInfo[] wsClientRuntimeInfoes = new WSClientRuntimeInfo[0];

  public WSClientsDeployResult() {
  }

  public WSClientRuntimeInfo[] getWsClientRuntimeInfoes() {
    return wsClientRuntimeInfoes;
  }

  public void setWsClientRuntimeInfoes(WSClientRuntimeInfo[] wsClientRuntimeInfoes) {
    this.wsClientRuntimeInfoes = wsClientRuntimeInfoes;
  }

  public void addWsClientRuntimeInfoes(WSClientRuntimeInfo[] wsClientRuntimeInfoes) {
    this.wsClientRuntimeInfoes = WSClientsUtil.unifyWSClientRuntimeInfoes(new WSClientRuntimeInfo[][]{this.wsClientRuntimeInfoes, wsClientRuntimeInfoes});
  }

  public void addWSClientsDeployResult(DeployResult deployResult) {
    if(deployResult == null) {
      return;
    }

    super.addDeployResult(deployResult);

    if(deployResult instanceof WSClientsDeployResult) {
      WSClientsDeployResult wsClientsDeployResult = (WSClientsDeployResult)deployResult;
      this.addWsClientRuntimeInfoes(wsClientsDeployResult.getWsClientRuntimeInfoes());
    }
  }

}
