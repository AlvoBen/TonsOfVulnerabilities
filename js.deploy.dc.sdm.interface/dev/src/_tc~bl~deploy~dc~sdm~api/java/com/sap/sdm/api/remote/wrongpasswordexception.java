package com.sap.sdm.api.remote;

/**
 * Signals that the wrong password was used for login trial. 
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Martin Stahl
 * @version 1.0
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.AuthenticationException</code>.
 */
public abstract class WrongPasswordException extends Exception {

  public WrongPasswordException(String msg) {
    super(msg);
  }
}
