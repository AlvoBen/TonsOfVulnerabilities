/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.deploy;

import java.rmi.Remote;

/* This class belongs to the public API of the DeployService project. */
/**
 * Interface for processing events related to deploy service. It provides
 * methods for handling events on applications, standalone modules, services,
 * libraries, interfaces, containers, references. DeployCallback processes
 * events for registering and unregistering servers to the callback system.
 * Deploy callback system is used as a manager of all registered deploy
 * listeners.
 * <p>
 * DeployCallback is used as a proxy when we need to register more than one
 * remote listener, i.e. one Callback with many local listeners registered in
 * it. Thus we avoid registration of many remote listeners.
 * 
 * @author Rumiana Angelova
 */
public interface DeployCallback extends Remote, DeployListener {

}
