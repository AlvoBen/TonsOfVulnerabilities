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
package com.sap.engine.services.deploy.ear.jar.moduledetect;

/**
 * @author Assia Djambazova
 */
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.sap.engine.services.deploy.container.rtgen.GenerationException;
import com.sap.engine.services.deploy.container.rtgen.ModuleDetector;
import com.sap.engine.services.deploy.container.rtgen.ModuleDetectorExt;
import com.sap.engine.services.deploy.ear.Module;

//Class that is used as wrapper of the ModuleDetector of ModuleDetectorExt
public class ModuleDetectorWrapper implements ModuleDetectorExt {

	private ModuleDetector realDetector;
	private boolean isDetectorExt;

	// depending on the interface that is implemented by the container
	// the wrapper will call the proper method
	public ModuleDetectorWrapper(ModuleDetector detector, boolean isDetectorExt) {
		assert detector != null;// detector should not be null
		this.realDetector = detector;
		this.isDetectorExt = isDetectorExt;
	}

	public Module[] detectModules(File tempDir, String[] moduleRelativeFileUri,
			String applicationName) throws GenerationException {
		if (isDetectorExt) {
			return ((ModuleDetectorExt) realDetector).detectModules(tempDir,
					moduleRelativeFileUri, applicationName);
		} else {
			Set<Module> modules = new HashSet<Module>();
			for (String fileUri : moduleRelativeFileUri) {// moduleRelativeFileUri
															// should not be
															// null
				modules.add(realDetector.detectModule(tempDir, fileUri));
			}
			if (modules.size() > 0) {
				return modules.toArray(new Module[modules.size()]);
			}
		}
		return null; // if no modules are detected
	}

	public Module detectModule(File tempDir, String moduleRelativeFileUri)
			throws GenerationException {
		// will not be used from deploy service, so there is no implementation
		return null;
	}

}
