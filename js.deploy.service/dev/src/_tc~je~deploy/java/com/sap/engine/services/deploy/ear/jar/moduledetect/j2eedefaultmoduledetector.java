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
import java.util.Iterator;
import java.util.Set;

import com.sap.engine.services.deploy.container.rtgen.GenerationException;
import com.sap.engine.services.deploy.container.rtgen.ModuleDetector;
import com.sap.engine.services.deploy.ear.J2EEModule;
import com.sap.engine.services.deploy.ear.Module;
import com.sap.engine.services.deploy.ear.jar.EarDescriptor;

/**
 * @author Luchesar Cekov
 */
public class J2EEDefaultModuleDetector implements ModuleDetector {
	private static final long serialVersionUID = 5444454011868418040L;

	private EarDescriptor ear;
	private J2EEModule.Type containerType;

	public J2EEDefaultModuleDetector(EarDescriptor aEar,
			J2EEModule.Type aContainerType) {
		ear = aEar;
		containerType = aContainerType;
	}

	public Module detectModule(File aTempDir, String aModuleRelativeFileUri)
			throws GenerationException {
		Set<Module> modules = ear.getJ2EEModules(containerType);
		for (Iterator<Module> iter = modules.iterator(); iter.hasNext();) {
			J2EEModule module = (J2EEModule) iter.next();
			if (containerType.equals(module.getType())
					&& module.getUri().equals(aModuleRelativeFileUri)) {
				return module;
			}
		}
		return null;
	}
}
