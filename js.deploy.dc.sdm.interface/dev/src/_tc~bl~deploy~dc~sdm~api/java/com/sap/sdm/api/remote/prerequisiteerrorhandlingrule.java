package com.sap.sdm.api.remote;

/**
 * Represents a rule regarding prerequisite deploy-time 
 * error handling.
 * This class is new with API client version 7.
 * 
 * @author ivan-mih
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.ErrorStrategy</code>.
 */
public interface PrerequisiteErrorHandlingRule {
  
  /** 
   * Returns an <code>int</code> representation of this 
   * <code>PrerequisiteErrorHandlingRule</code>.
   * 
   * @return an <code>int</code> representation of this 
   *          <code>PrerequisiteErrorHandlingRule</code>
   * @see com.sap.sdm.api.remote.PrerequisiteErrorHandlingRules 
   */
  public int getRuleAsInt();
  
  /** 
   * Returns a <code>String</code> representation of this 
   * <code>PrerequisiteErrorHandlingRule</code>.
   * 
   * @return a <code>String</code> representation of this 
   *          <code>PrerequisiteErrorHandlingRule</code>
   * @see com.sap.sdm.api.remote.PrerequisiteErrorHandlingRules 
   */
  public String getRuleAsString();
  
  /** 
   * Returns a <code>String</code> containing a short 
   * description of this <code>PrerequisiteErrorHandlingRule</code>.
   * 
   * @return a <code>String</code> containing a short 
   * description of this <code>PrerequisiteErrorHandlingRule</code>
   */
  public String getRuleDescription();  
}
