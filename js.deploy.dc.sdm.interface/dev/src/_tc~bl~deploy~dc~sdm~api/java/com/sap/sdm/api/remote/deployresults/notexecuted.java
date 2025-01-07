package com.sap.sdm.api.remote.deployresults;

/**
 * Represents the result type of a not executed deployment process.
 * This result type means that the deployment process was not executed
 * at all for some reason. To get the result type in more detail you 
 * should check an instance of this interface whether it is an 
 * <code>instanceof</code> of one of the following subinterfaces
 * {@link com.sap.sdm.api.remote.deployresults.Initial}, 
 * {@link com.sap.sdm.api.remote.deployresults.Admitted}, 
 * {@link com.sap.sdm.api.remote.deployresults.Rejected}, 
 * {@link com.sap.sdm.api.remote.deployresults.AlreadyDeployed}, 
 * {@link com.sap.sdm.api.remote.deployresults.PreconditionViolated}
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Martin Stahl
 * @version 1.0
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.deploy.DeployItemStatus#ADMITTED</code>.
 */
public interface NotExecuted extends DeployResultType {

}
