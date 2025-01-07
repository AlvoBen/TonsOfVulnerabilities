package com.sap.sdm.api.remote.undeployresults;

/**
 * Title:        Software Delivery Manager
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      SAP AG
 * @author Software Logistics - here: D019309
 *
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.undeploy.UndeployItemStatuss#PREREQUISITE_VIOLATED</code> 
 * or <code>com.sap.engine.services.dc.api.undeploy.UndeployItemStatuss#NOT_SUPPORTED</code> or 
 * or <code>com.sap.engine.services.dc.api.undeploy.UndeployItemStatuss#NOT_DEPLOYED</code>. 
 * 
 */
public interface NotExecuted extends UnDeployResultType  {
  
}


