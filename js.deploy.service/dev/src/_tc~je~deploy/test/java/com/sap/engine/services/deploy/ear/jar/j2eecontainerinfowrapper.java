/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.ear.jar;

import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.ear.J2EEModule;


/**
 *@author Luchesar Cekov
 */
public class J2EEContainerInfoWrapper extends ContainerInfo {
  public J2EEContainerInfoWrapper(String name, J2EEModule.Type aType) {
    setName(name);
    setJ2EEContainer(true);
    setJ2EEModuleName(aType.name());
  }
  
}
