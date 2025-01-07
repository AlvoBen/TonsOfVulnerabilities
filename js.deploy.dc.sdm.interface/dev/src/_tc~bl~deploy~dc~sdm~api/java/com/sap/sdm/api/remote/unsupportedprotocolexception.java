package com.sap.sdm.api.remote;

/**
 *
 * Title:        J2EE Deployment Team
 * Description:    
 * 
 * Copyright:    Copyright (c) 2003
 * Company:      SAP AG
 * Date:         2004-7-5
 * 
 * @author       Dimitar Dimitrov
 * @version      1.0
 * @since        7.0
 *
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used.
 */
public abstract class UnsupportedProtocolException extends Exception {

  public UnsupportedProtocolException(String errMessage) {
    super(errMessage);
  }

  public UnsupportedProtocolException(String errMessage, Throwable throwable) {
    super(errMessage, throwable);
  }
  
}
