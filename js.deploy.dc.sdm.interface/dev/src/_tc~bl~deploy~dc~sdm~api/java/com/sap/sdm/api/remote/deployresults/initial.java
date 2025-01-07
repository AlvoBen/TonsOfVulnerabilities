package com.sap.sdm.api.remote.deployresults;

/**
 * Represents the result type of a deployment process that was not executed 
 * yet.
 * This result type means that the deployment process is in an initial state -
 * no actions have taken place so far.
 * You should check 
 * {@link com.sap.sdm.api.remote.DeployResult#getResultText()} for a description
 * of the reason.
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Martin Stahl
 * @version 1.0
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.deploy.DeployItemStatus#INITIAL</code>.
 */
public interface Initial extends NotExecuted {

}
