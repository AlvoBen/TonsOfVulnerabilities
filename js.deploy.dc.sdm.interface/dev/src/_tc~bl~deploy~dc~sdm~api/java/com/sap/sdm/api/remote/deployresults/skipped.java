package com.sap.sdm.api.remote.deployresults;

/**
 *
 * Title:        J2EE Deployment Team
 * Description:  The status <code>skipped</code> specifies that the deployment
 * item has not been executed because of the fact that depends on another component
 * which has been executed unsuccessfully and the error strategy is set to on error
 * skip depending. 
 * 
 * You should check 
 * {@link com.sap.sdm.api.remote.DeployResult#getResultText()} for a specific 
 * description. 
 * 
 * Copyright:    Copyright (c) 2003
 * Company:      SAP AG
 * Date:         2004-9-27
 * 
 * @author       Dimitar Dimitrov
 * @version      1.0
 * @since        7.0
 *
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.deploy.DeployItemStatus#SKIPPED</code>.
 */
public interface Skipped extends NotExecuted {

}

