package com.sap.sdm.api.remote;

/**
 * Represents a rule regarding deploy-time error handling.
 * This class is new with API client version 5.
 * 
 * @author lalo-i
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.ErrorStrategy</code>.
 */
public interface ErrorHandlingRule {
  
  /** 
   * Returns an <code>int</code> representation of this 
   * <code>ErrorHandlingRule</code>.
   * 
   * @return an <code>int</code> representation of this 
   *          <code>ErrorHandlingRule</code>
   * @see com.sap.sdm.api.local.ErrorHandlingRules 
   */
  public int getRuleAsInt();
  
  /** 
   * Returns a <code>String</code> representation of this 
   * <code>ErrorHandlingRule</code>.
   * 
   * @return a <code>String</code> representation of this 
   *          <code>ErrorHandlingRule</code>
   * @see com.sap.sdm.api.local.ErrorHandlingRules 
   */
  public String getRuleAsString();
  
  /** 
   * Returns a <code>String</code> containing a short 
   * description of this <code>ErrorHandlingRule</code>.
   * 
   * @return a <code>String</code> containing a short 
   * description of this <code>ErrorHandlingRule</code>
   */
  public String getRuleDescription();  
}
