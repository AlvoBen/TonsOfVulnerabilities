package com.sap.sdm.api.remote.deployresults;

/**
 * Represents the result type of a not executed deployment process.
 * This result type means that the deployment process was rejected
 * because the given SDA/SCA was deployed already.
 * You should check 
 * {@link com.sap.sdm.api.remote.DeployResult#getResultText()} for a specific 
 * description.
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Martin Stahl
 * @version 1.0
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.deploy.DeployItemStatus#ALREADY_DEPLOYED</code>.
 */
public interface AlreadyDeployed extends Rejected {

}
