/*
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.msp;

import java.rmi.*;
import java.util.*;
import javax.rmi.*;

import com.sap.engine.frame.state.ManagementListener;

public class MSPRuntimeInterfaceImpl extends PortableRemoteObject implements MSPRuntimeInterface {

  /**
   * Stores the utils holding constants and methods
   * needed for processing the data in MSMonitor
   */
  private MSPProcessor processor = null;

  /**
   * Constructs the class
   */
  public MSPRuntimeInterfaceImpl(MSPProcessor prcs) throws RemoteException {
    super();
    this.processor = prcs;
  }

  /**
   * Method getInfo.
   * The getInfo method returns a map which contains the internal message server
   * information (Release, SID, Patch Level...) as key/value pairs.
   *
   * @return TreeMap internal message server information.
   */
  public TreeMap getInformation() throws RemoteException {
    return processor.getInformation();
  }

  /**
   * Method getParams.
   * The getParams method returns a map which contains the message server
   * parameters.
   *
   * @return TreeMap message server parameters
   */
  public TreeMap getParameters() throws RemoteException {
    return processor.getParameters();
  }

  /**
   * Method getStatistic.
   * The getStatistics method returns a map which contains the message server
   * statistics as key/value pairs. You have to switch on the statistics by
   * calling activateStatistic().
   *
   * @return TreeMap message server statistics.
   */
  public TreeMap getStatistic() throws RemoteException {
    return processor.getStatistic();
  }

  /**
   * Method activateStatistic.
   * Activate the internal message server statistics.
   *
   */
  public void activateStatistic() throws RemoteException {
    processor.activateStatistic();
  }

  /**
   * Method deactivateStatistic.
   * Deactivate the internal message server statistics.
   */
  public void deactivateStatistic() throws RemoteException {
    processor.deactivateStatistic();
  }

  /**
   * Method resetStatistic.
   * Reset the internal message server statistics.
   */
  public void resetStatistic() throws RemoteException {
    processor.resetStatistic();
  }

  /**
   * Method incrementTraceLevel.
   * Increment the internal message server trace level.
   */
  public void incrementTraceLevel() throws RemoteException {
    processor.incrementTraceLevel();
  }

  /**
   * Method decrementTraceLevel.
   * Decrement the internal message server trace level.
   */
  public void decrementTraceLevel() throws RemoteException {
    processor.decrementTraceLevel();
  }

  /**
   * Method resetTraceLevel.
   * Reset the internal message server trace level to the default value.
   */
  public void resetTraceLevel() throws RemoteException {
    processor.resetTraceLevel();
  }

  /**
   * Method getHardwareId.
   * Get the hardware id for the licensing.
   *
   * @return String hardware id of the message server system.
   */
  public String getHardwareId() throws RemoteException {
    return processor.getHardwareId();
  }

  /**
   * Method getSystemId.
   * Get the system id for the licensing.
   *
   * @return String system id of the message server.
   */
  public String getSystemId() throws RemoteException {
    return processor.getSystemId();
  }

  /**
   * Method updateLocalClusterRepository.
   * Update the local cluster repository with the repository of the
   * message server. The method process all cluster events comming up
   * with the update.
   */
  public void updateLocalClusterRepository() throws RemoteException {
    processor.updateLocalClusterRepository();
  }

  public void registerManagementListener(ManagementListener managementListener) {
  }

  /**
   * Method getStatistic.
   * The getStatistics method returns a map which contains the message server
   * client statistics as key/value pairs. You have to switch on the statistics by
   * calling activateStatistic().
   *
   * @return TreeMap message server statistics.
   */
  public TreeMap getClientStatistic() throws RemoteException {
    return processor.getClientStatistic();
  }

  /**
   * Method activateClientStatistic.
   * Activate the internal message server client statistics.
   *
   */
  public void activateClientStatistic() throws RemoteException {
    processor.activateClientStatistic();
  }

  /**
   * Method deactivateClientStatistic.
   * Deactivate the internal message server client statistics.
   */
  public void deactivateClientStatistic() throws RemoteException {
    processor.deactivateClientStatistic();
  }

  /**
   * Method resetClientStatistic.
   * Reset the internal message server statistics.
   */
  public void resetClientStatistic() throws RemoteException {
    processor.resetClientStatistic();
  }
  
	/**
	 * @see com.sap.engine.services.msp.MSPRuntimeInterface#getServiceInfo()
	 */
	public TreeMap getServiceInfo() throws RemoteException {
		return processor.getServiceInfo();
	}
}
