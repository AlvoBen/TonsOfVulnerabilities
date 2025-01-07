/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.frame.container.deploy.zdm;

/**
 * Enables the zero downtime concept based on the rolling approach for online
 * components at instance level, which is the smallest entity in the cluster.
 * The responsibility for synchronizing the whole cluster is delegated to the
 * upper layer, which uses this interface. 
 * 
 * @author Dimitar Kostadinov
 * @version 1.00
 * @since 7.10
 * @deprecated The interface will only be used for proofing the concept in the 
 * prototyping phase. It will not be shipped to external customers and is not 
 * considered as public interface, without reviewing it.
 */
public interface RollingPatch {

  /**
   * Updates the application in the date base and in the instance. 
   * 
   * @param rollingComponent, which is an <code>RollingComponent</code>
   * and describes the new component version.
   * @return a <code>RollingResult</code>, which describes how the state of the
   * instance was changed as a result from calling this method. Based on the result
   * the method caller is responsible to decide is the new state of the instance  
   * acceptable or not. It cannot be null.
   * @throws RollingException can be thrown only if the update process cannot start
   * and the state of the instance before invoking the method and after throwing the 
   * exception is the same, otherwise a <code>RollingResult</code> will be returned.
   * @deprecated
   */
  public RollingResult updateInstanceAndDB(RollingComponent rollingComponent) throws RollingException;

  /**
   * Synchronizes the component in the instance with the data base.
   * 
   * @param rollingName, which is an <code>RollingName</code> with name
   * and vendor.
   * @return a <code>RollingResult</code>, which describes how the state of the
   * instance was changed as a result from calling this method. Based on the result
   * the method caller is responsible to decide is the new state of the instance  
   * acceptable or not. It cannot be null.
   * @throws RollingException can be thrown only if the update process cannot start
   * and the state of the instance before invoking the method and after throwing the 
   * exception is the same, otherwise a <code>RollingResult</code> will be returned.
   * @deprecated
   */
  public RollingResult syncInstanceWithDB(RollingName rollingName) throws RollingException;

}