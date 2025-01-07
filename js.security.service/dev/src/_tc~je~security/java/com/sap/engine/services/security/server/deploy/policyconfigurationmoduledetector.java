/**
 * Copyright (c) 2008 by SAP Labs Bulgaria,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * 
 * Created on Nov 24, 2008 by I030797
 *   
 */
 
package com.sap.engine.services.security.server.deploy;

import java.io.File;

import com.sap.engine.services.deploy.container.rtgen.GenerationException;
import com.sap.engine.services.deploy.container.rtgen.ModuleDetector;
import com.sap.engine.services.deploy.ear.Module;


/**
 * @author I030797
 */
public class PolicyConfigurationModuleDetector implements ModuleDetector {

  public PolicyConfigurationModuleDetector() {
  }

  /* (non-Javadoc)
   * @see com.sap.engine.services.deploy.container.rtgen.ModuleDetector#detectModule(java.io.File, java.lang.String)
   */
  public Module detectModule(File tempDir, String moduleRelativeFileUri) throws GenerationException {
    PolicyConfigurationContainer.setParallelDeployment(true);
    return new Module(tempDir, moduleRelativeFileUri, PolicyConfigurationContainer.NAME);
  }

}
