/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session;

/*
 * @author: Georgi Stanev, Nikolai Neichev 
 */

public abstract class ClusterEnv {

  private static ClusterEnv impl;

  public static void setClusterEnvironment(ClusterEnv impl) {    
    ClusterEnv.impl = impl;
  }

  public static ClusterEnv getInstance() {
    return impl;
  }

  public abstract void createSessionOnNode(SessionDomain domain, String serverNodeID, String sessionID) throws SessionException; 
}
