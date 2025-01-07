package com.sap.sdm.api.remote;

/**
 * Signals that the communication between client and server has failed or 
 * has been interrupted.
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Martin Stahl
 * @version 1.0
 * 
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used.
 */
public abstract class RemoteException extends Exception {

  public RemoteException(String msg) {
    super(msg);
  }
  
  public RemoteException(String msg, Throwable throwable) {
    super(msg, throwable);
  }   
  
}
