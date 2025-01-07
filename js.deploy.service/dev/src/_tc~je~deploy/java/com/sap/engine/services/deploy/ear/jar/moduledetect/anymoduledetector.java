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
package com.sap.engine.services.deploy.ear.jar.moduledetect;

import java.io.File;

import com.sap.engine.services.deploy.container.rtgen.GenerationException;
import com.sap.engine.services.deploy.container.rtgen.ModuleDetector;
import com.sap.engine.services.deploy.ear.Module;

/**
 * @author Luchesar Cekov
 */
public class AnyModuleDetector implements ModuleDetector {
	private static final long serialVersionUID = -8555246040614361254L;

	private String containerName;

	public AnyModuleDetector(String aContainerName) {
		containerName = aContainerName;
	}

	public Module detectModule(File aTempDir, String aModuleRelativeFileUri)
			throws GenerationException {
		return new Module(aTempDir, aModuleRelativeFileUri, containerName);
	}
}
