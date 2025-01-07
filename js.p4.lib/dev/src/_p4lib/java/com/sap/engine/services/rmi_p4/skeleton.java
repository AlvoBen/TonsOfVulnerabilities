package com.sap.engine.services.rmi_p4;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.Operation;

/**
 * Server side proxy base interface.Used by the Dispatch class
 * on order to invoke the dispatch method of the proxy.
 *
 * @author Ivo Simeonov, Georgy Stanev
 * @version 7.0
 */
public interface Skeleton {

  /**
   * Called by call executor in order to invoke the proper
   * object method.
   *
   * @param   remote remote object implementation
   * @param   call   call executor
   * @param   opnum  operation number to invoke
   * @exception   RemoteException thrown on error
   */
  public void dispatch(Remote remote, Dispatch call, int opnum) throws RemoteException, Exception;


  /**
   * Called by call executor in order to obtain
   * method number witch has to be invoked
   *
   * @return operations available for dispatching
   */
  public Operation[] getOperations();


  /**
   * @return Interfaces that this skeleton implements
   */
  public String[] getImplemntsObjects();

}

