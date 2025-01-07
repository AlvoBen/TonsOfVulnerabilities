package com.sap.sdm.api.remote;

/**
 * This class is new with API client version 5.
 * @author lalo-i
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.ErrorStrategy</code>.
 */
public interface ErrorHandlingRules {
  
  /**
   * Specifies that all deployments that depend on 
   * deployments having been executed not successfully will 
   * not be executed but have to be skipped instead.
   */  
  public final static int ON_ERROR_SKIP_DEPENDING = 0;
  
  /**
   * String representation of <code>ON_ERROR_SKIP_DEPENDING</code>
   */
  public final static String ON_ERROR_SKIP_DEPENDING_S = "OnErrorSkipDepending"; 
  
  /**
   * Description of <code>ON_ERROR_SKIP_DEPENDING</code>
   */  
  public final static String ON_ERROR_SKIP_DEPENDING_DESC = 
                  "Specifies that all deployments that depend on " +
                  "deployments having been executed not successfully will " +
                  "not be executed, but have to be skipped instead.";
                        
  /**
   * Specifies that no matter whether a deployment error has occurred so far, the 
   * deployment iteration through the given set of deployments will be continued.
   * Note that this rule should be used with care.
   */  
  public final static int ON_ERROR_IGNORE = 1;
  
  /**
   * String representation of <code>ON_ERROR_IGNORE</code>
   */
  public final static String ON_ERROR_IGNORE_S = "OnErrorIgnore";
  
  /**
   * Description of <code>ON_ERROR_IGNORE_DESC</code>
   */  
  public final static String ON_ERROR_IGNORE_DESC = 
                "Specifies that no matter whether a deployment error has occurred so far, the " +
                "deployment iteration through the given set of deployments will be continued.";    

  /**
   * Specifies that once a deployment error occurs, the further processing will be 
   * stopped.
   */
  public final static int ON_ERROR_STOP = 2;
  
  /**
   * String representation of <code>ON_ERROR_STOP</code>
   */
  public final static String ON_ERROR_STOP_S = "OnErrorStop";
  
  /**
   * Description of <code>ON_ERROR_STOP</code>
   */
  public final static String ON_ERROR_STOP_DESC = 
                "Specifies that once a deployment error occurs, the further processing will be stopped.";  
  
}
