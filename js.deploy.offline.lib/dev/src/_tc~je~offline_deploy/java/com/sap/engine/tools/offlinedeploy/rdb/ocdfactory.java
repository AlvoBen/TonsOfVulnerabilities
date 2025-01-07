/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.tools.offlinedeploy.rdb;

import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.tc.logging.Location;

import javax.sql.DataSource;

/**
 * Creates instances of </code>OnlineModule</code> and
 * </code>OfflineComponentDeploy</code> implementations.
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public class OCDFactory {

  /**
   * Returns instance of </code>OfflineComponentDeploy</code> using
   * configuration factory. </p>
   *
   * @param factory - configuration factory.
   * @param location - location.
   * @return create new OfflineComponentDeploy instance.
   * @throws DeploymentException if an error occurs.
   */
  @Deprecated
  public static OfflineComponentDeploy createOfflineComponentDeploy(ConfigurationHandlerFactory factory, Location location) throws DeploymentException {
    // TODO Delete this method, it was used only internally and should not be used anymore
    return createOfflineComponentDeploy(factory, location, null);
  }

  /**
   * Returns instance of </code>OfflineComponentDeploy</code> using
   * configuration factory. </p>
   *
   * @param factory - configuration factory.
   * @param location - location.
   * @param ds - SQL datasource used for DB table handling.
   * @return create new OfflineComponentDeploy instance.
   * @throws DeploymentException if an error occurs.
   */
  public static OfflineComponentDeploy createOfflineComponentDeploy(ConfigurationHandlerFactory factory, Location location, DataSource ds) throws DeploymentException {
    return new OfflineComponentDeployImpl(factory, location, ds);
  }

}