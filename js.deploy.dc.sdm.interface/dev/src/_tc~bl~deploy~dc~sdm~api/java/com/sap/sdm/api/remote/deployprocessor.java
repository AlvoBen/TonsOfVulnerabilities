package com.sap.sdm.api.remote;

/**
 * Provides methods for executing the deployment process on the remote server.
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Martin Stahl
 * @version 1.0
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.deploy.DeployProcessor</code>.
 */
public interface DeployProcessor {
  /**
   * Executes the deployment of the specified archives on the SDM server.
   * Each archive is specified via a <code>DeployItem</code> that also
   * provides a description of the deploy result for the particular archive
   * after the method has returned.
   * Regarding the handling of different versions for a DC during deployment
   * an explicit <code>ComponentVersionHandlingRule</code> can be set. If no explicit
   * rule was set the default is {@link com.sap.sdm.api.remote.ComponentVersionHandlingRules#UPDATE_ALL_VERSIONS}
   * 
   * @param deployItems an array of <code>DeployItem</code>
   * @return a <code>URLMimic</code> pointing to a file that contains the logs
   *          of the executed deploy process
   */
  public void deploy(DeployItem[] deployItems) throws RemoteException;
  
  /**
   * Returns the rule which is used for the version handling by this
   * <code>DeployProcessor</code>. 
   * 
   * @return a <code>ComponentVersionHandlingRule</code> describing the rule for 
   *         version handling of this <code>DeployProcessor</code>
   */
  public ComponentVersionHandlingRule getComponentVersionHandlingRule() throws RemoteException;

  /**
   * Sets the rule which should be used for the version handling by this
   * <code>DeployProcessor</code>. The rule will be applied by all following
   * calls of the <code>deploy</code> method.
   * 
   * @param rule a <code>ComponentVersionHandlingRule</code> specifying the rule to be applied
   * @throws NullPointerException if rule is null
   * @see com.sap.sdm.api.remote.HelperFactory#createComponentVersionHandlingRule(int)
   */
  public void setComponentVersionHandlingRule(ComponentVersionHandlingRule rule) throws RemoteException;  

  /**
   * Returns the rule which is used for the error handling by this
   * <code>DeployProcessor</code>. 
   * This method is new with API client version 5.
   * 
   * @return a <code>ErrorHandlingRule</code> describing the rule for 
   *         version handling of this <code>DeployProcessor</code>
   */
  public ErrorHandlingRule getErrorHandlingRule() throws RemoteException;

  
  /**
   * Sets the rule which should be used for the error handling by this
   * <code>DeployProcessor</code>. The rule will be applied by all following
   * calls of the <code>deploy</code> method.
   * This method is new with API client version 5.
   * 
   * @param rule a <code>ErrorHandlingRule</code> specifying the rule to be applied
   * @throws NullPointerException if rule is null
   * @see com.sap.sdm.api.local.HelperFactory#createErrorHandlingRule(int)
   */
  public void setErrorHandlingRule(ErrorHandlingRule rule) throws RemoteException;  

  /**
   * Returns the rule which is used for the error handling by this
   * <code>DeployProcessor</code> during the prerequisite deployment phase. 
   * This method is new with API client version 7.
   * 
   * @return a <code>PrerequisiteErrorHandlingRule</code> describing the rule for 
   *         version handling of this <code>DeployProcessor</code>
   */
  public PrerequisiteErrorHandlingRule getPrerequisiteErrorHandlingRule() throws RemoteException;
  
  /**
   * Sets the rule which should be used for the error handling by this
   * <code>DeployProcessor</code> during the prerequisite deployment phase. The rule will be applied by all following
   * calls of the <code>deploy</code> method.
   * This method is new with API client version 7.
   * 
   * @param rule a <code>PrerequisiteErrorHandlingRule</code> specifying the rule to be applied
   * @throws NullPointerException if rule is null
   * @see com.sap.sdm.api.remote.HelperFactory#createPrerequisiteErrorHandlingRule(int)
   */
  public void setPrerequisiteErrorHandlingRule(PrerequisiteErrorHandlingRule rule) throws RemoteException;  
  
  /**
   * Executes the validation of the specified archives on the SDM server.
   * The procedure execute all deployment process steps without the physical deployment. 
   * Each archive is specified via a <code>DeployItem</code> that also
   * provides a description of the deploy result for the particular archive
   * after the method has returned.
   * Regarding the handling of different versions for a DC during deployment
   * an explicit <code>ComponentVersionHandlingRule</code> can be set. If no explicit
   * rule was set the default is {@link com.sap.sdm.api.local.ComponentVersionHandlingRules#UPDATE_ALL_VERSIONS}
   * The prerequisite error handling strategy is always ON_ERROR_SKIP_DEPENDING during the validation (no senses what is set as 
   * a prerequisite error handling rule). 
   * This method is new with API client version 7 as long as all related with validation objects and methods.
   * 
   * @param deployItems an array of <code>DeployItem</code>
   * @return a <code>ValidateResult</code> contains validation status, deployment type (offline or online) and
   * specified deploy items.    
   */
  public ValidateResult validate(DeployItem[] deployItems) throws RemoteException;  
  
}
