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
package com.sap.engine.services.deploy.server.editor.impl.second;

import java.util.Set;

import com.sap.engine.services.deploy.server.dpl_info.module.ContainerData;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.FSUtils;
import com.sap.engine.services.deploy.server.utils.ValidateUtils;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class DIAdapter2 {

	// ************* FILES FOR CLASS LOADER *************//

	protected static Set<String> getFilesForCL(ContainerData cData) {
		ValidateUtils.nullValidator(cData, "container data");

		// absolute -> relative
		final Set<String> relative = FSUtils.relativePath(PropManager
				.getInstance().getAppsWorkDir(), cData.getFilesForCL());

		return relative;
	}

	protected static void setFilesForCL(ContainerData cData,
			Set<String> filesForCL) {
		ValidateUtils.nullValidator(cData, "container data");

		// relative -> absolute
		final Set<String> absolute = FSUtils.absolutePaths(PropManager
				.getInstance().getAppsWorkDir(), filesForCL);

		cData.setFilesForCL(absolute);
	}

	// ************* FILES FOR CLASS LOADER *************//

	// ************* HEAVY FILES FOR CLASS LOADER *************//

	protected static Set<String> getHeavyFilesForCL(ContainerData cData) {
		ValidateUtils.nullValidator(cData, "container data");

		// absolute -> relative
		final Set<String> relative = FSUtils.relativePath(PropManager
				.getInstance().getAppsWorkDir(), cData.getHeavyFilesForCL());

		return relative;
	}

	protected static void setHeavyFilesForCL(ContainerData cData,
			Set<String> heavyFilesForCL) {
		ValidateUtils.nullValidator(cData, "container data");

		// relative -> absolute
		final Set<String> absolute = FSUtils.absolutePaths(PropManager
				.getInstance().getAppsWorkDir(), heavyFilesForCL);

		cData.setHeavyFilesForCL(absolute);
	}

	// ************* HEAVY FILES FOR CLASS LOADER *************//

}
