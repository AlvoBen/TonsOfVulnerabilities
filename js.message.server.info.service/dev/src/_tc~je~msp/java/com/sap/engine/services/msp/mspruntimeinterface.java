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

import com.sap.engine.frame.state.ManagementInterface;

import java.rmi.*;
import java.util.*;


/**
 * Remote interface of the MSP service.
 * Intended for clients and the GUI part of the service
 *
 * @author Hristo Iliev
 * @version 6.30 Oct 2002
 */
public interface MSPRuntimeInterface extends ManagementInterface, Remote {

  /**
   * Method getInfo.
   * The getInfo method returns a map which contains the internal message server
   * information (Release, SID, Patch Level...) as key/value pairs.
   *
   * @return String internal message server information.
   */
  TreeMap getInformation() throws RemoteException;

  /**
   * Method getParams.
   * The getParams method returns a map which contains the message server
   * parameters.
   *
   * @return String message server parameters
   */
  TreeMap getParameters() throws RemoteException;

  /**
   * Method getStatistic.
   * The getStatistics method returns a map which contains the message server
   * statistics as key/value pairs. You have to switch on the statistics by
   * calling activateStatistic().
   *
   * @return String message server statistics.
   */
  TreeMap getStatistic() throws RemoteException;

  /**
   * Method activateStatistic.
   * Activate the internal message server statistics.
   *
   */
  void activateStatistic() throws RemoteException;

  /**
   * Method deactivateStatistic.
   * Deactivate the internal message server statistics.
   */
  void deactivateStatistic() throws RemoteException;

  /**
   * Method resetStatistic.
   * Reset the internal message server statistics.
   */
  void resetStatistic() throws RemoteException;

  /**
   * Method incrementTraceLevel.
   * Increment the internal message server trace level.
   */
  void incrementTraceLevel() throws RemoteException;

  /**
   * Method decrementTraceLevel.
   * Decrement the internal message server trace level.
   */
  void decrementTraceLevel() throws RemoteException;

  /**
   * Method resetTraceLevel.
   * Reset the internal message server trace level to the default value.
   */
  void resetTraceLevel() throws RemoteException;

  /**
   * Method getHardwareId.
   * Get the hardware id for the licensing.
   *
   * @return String hardware id of the message server system.
   */
  String getHardwareId() throws RemoteException;

  /**
   * Method getSystemId.
   * Get the system id for the licensing.
   *
   * @return String system id of the message server.
   */
  String getSystemId() throws RemoteException;

  /**
   * Method updateLocalClusterRepository.
   * Update the local cluster repository with the repository of the
   * message server. The method process all cluster events comming up
   * with the update.
   */
  void updateLocalClusterRepository() throws RemoteException;

  /**
   * Method getClientStatistic.
   * The getStatistics method returns a map which contains the message server
   * client statistics as key/value pairs. You have to switch on the statistics by
   * calling activateStatistic().
   *
   * @return String message server statistics.
   */
  TreeMap getClientStatistic() throws RemoteException;

  /**
   * Method activateClientStatistic.
   * Activate the internal message server client statistics.
   *
   */
  void activateClientStatistic() throws RemoteException;

  /**
   * Method deactivateStatistic.
   * Deactivate the internal message server client statistics.
   */
  void deactivateClientStatistic() throws RemoteException;

  /**
   * Method resetClientStatistic.
   * Reset the internal message server client statistics.
   */
  void resetClientStatistic() throws RemoteException;

  /**
   * Method getServiceInfo.
   * The getServiceInfo method returns a map which contains the message server
   * client statistics as key/value pairs. You have to switch on the statistics by
   * calling activateStatistic().
   *
   * @return String message server statistics.
   */
  TreeMap getServiceInfo() throws RemoteException;
}
