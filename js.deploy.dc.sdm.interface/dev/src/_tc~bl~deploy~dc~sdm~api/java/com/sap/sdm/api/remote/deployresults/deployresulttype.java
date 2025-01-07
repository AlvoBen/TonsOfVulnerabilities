package com.sap.sdm.api.remote.deployresults;

/**
 * Represents the result type of a started deployment process.
 * There are several subinterfaces which specify the result type 
 * in more detail:
 * 
 * @see com.sap.sdm.api.remote.deployresults.Executed
 * @see com.sap.sdm.api.remote.deployresults.Aborted
 * @see com.sap.sdm.api.remote.deployresults.Success
 * @see com.sap.sdm.api.remote.deployresults.Warning
 * 
 * @see com.sap.sdm.api.remote.deployresults.NotExecuted
 * @see com.sap.sdm.api.remote.deployresults.Admitted
 * @see com.sap.sdm.api.remote.deployresults.Initial
 * @see com.sap.sdm.api.remote.deployresults.Rejected
 * @see com.sap.sdm.api.remote.deployresults.AlreadyDeployed
 * @see com.sap.sdm.api.remote.deployresults.PreconditionViolated
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Martin Stahl
 * @version 1.0
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.deploy.DeployItemStatus</code>.
 */
public interface DeployResultType {

}
