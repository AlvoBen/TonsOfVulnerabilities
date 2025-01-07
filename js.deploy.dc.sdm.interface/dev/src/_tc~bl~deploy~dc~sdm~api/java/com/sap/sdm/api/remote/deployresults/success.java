package com.sap.sdm.api.remote.deployresults;

/**
 * Represents the result type of an executed deployment process that was
 * successful.
 * This result type means that the deployment process really
 * was executed and finished with success. 
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Martin Stahl
 * @version 1.0
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.deploy.DeployItemStatus#SUCCESS</code>.
 */
public interface Success extends Executed {

}
