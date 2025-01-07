﻿/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.ear.jar.initcontainer;

import java.io.File;

import com.sap.engine.services.deploy.container.rtgen.GenerationException;
import com.sap.engine.services.deploy.container.rtgen.ModuleDetector;
import com.sap.engine.services.deploy.ear.Module;
import com.sap.engine.services.deploy.ear.modules.Connector;
import com.sap.lib.javalang.tool.ReadResult;


/**
 *@author Luchesar Cekov
 */
public class ConnectorModelDetector implements ModuleDetector {
  public Module detectModule(File aTempDir, String aModuleRelativeFileUri) throws GenerationException {
    if (!aModuleRelativeFileUri.endsWith(".rar")) return null;
    return new Connector(aTempDir, aModuleRelativeFileUri);
  }
}
