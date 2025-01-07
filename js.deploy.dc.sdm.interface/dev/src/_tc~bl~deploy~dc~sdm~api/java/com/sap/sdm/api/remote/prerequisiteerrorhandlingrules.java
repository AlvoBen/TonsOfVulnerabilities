package com.sap.sdm.api.remote;

/**
 * This class is new with API client version 7. 
 * @author ivan-mih
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.ErrorStrategy</code>.
 */
public interface PrerequisiteErrorHandlingRules {
  
  /**
   * Specifies that all deployments that depend on deployments having been
   * checked for prerequisities not successfully will not be executed.
   * They will be skipped instead.
   */  
  public final static int ON_PREREQUISITE_ERROR_SKIP_DEPENDING = 0;
  
  /**
   * String representation of <code>ON_PREREQUISITE_ERROR_SKIP_DEPENDING</code>
   */
  public final static String ON_PREREQUISITE_ERROR_SKIP_DEPENDING_S = "OnPrerequisiteErrorSkipDepending"; 
  
  /**
   * Description of <code>ON_PREREQUISITE_ERROR_SKIP_DEPENDING</code>
   */  
  public final static String ON_PREREQUISITE_ERROR_SKIP_DEPENDING_DESC = 
                  "Specifies that all deployments that depend on " +
                  "deployments having been executed not successfully will " +
                  "not be executed, but have to be skipped instead.";
                        
  /**
   * Specifies that once a prerequisity check error occurs, the further 
   * processing will be stopped.
   */
  public final static int ON_PREREQUISITE_ERROR_STOP = 1;
  
  /**
   * String representation of <code>ON_PREREQUISITE_ERROR_STOP</code>
   */
  public final static String ON_PREREQUISITE_ERROR_STOP_S = "OnPrerequisiteErrorStop";
  
  /**
   * Description of <code>ON_PREREQUISITE_ERROR_STOP</code>
   */
  public final static String ON_PREREQUISITE_ERROR_STOP_DESC = 
                "Specifies that once a deployment error occurs, the further processing will be stopped.";  
  
}
