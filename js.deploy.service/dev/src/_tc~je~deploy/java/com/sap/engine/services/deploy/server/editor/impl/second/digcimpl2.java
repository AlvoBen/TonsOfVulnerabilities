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

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.services.deploy.server.dpl_info.module.Version;
import com.sap.engine.services.deploy.server.editor.DIGC;
import com.sap.engine.services.deploy.server.utils.ConfigUtils;
import com.sap.engine.services.deploy.server.utils.ValidateUtils;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class DIGCImpl2 implements DIGC {

	private final Version version = Version.SECOND;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.editor.DIGC#delete(com.sap.engine
	 * .frame.core.configuration.Configuration,
	 * com.sap.engine.frame.core.configuration.Configuration)
	 */
	public void delete(Configuration appsCfg, Configuration deployCfg)
			throws ConfigurationException {
		ValidateUtils.nullValidator(appsCfg, "'apps' configuration");
		// deployCfg may be null

		{// ********************APPS********************//
			appsCfg.deleteAllConfigEntries();
			appsCfg.deleteAllFiles();

			ConfigUtils.deleteFile(appsCfg, DIConsts2.serialized);
			ConfigUtils.deleteSubConfiguration(appsCfg, DIConsts2.references);
			ConfigUtils
					.deleteSubConfiguration(appsCfg, DIConsts2.containerData);
			ConfigUtils
					.deleteSubConfiguration(appsCfg, DIConsts2.remoteSupport);
			ConfigUtils.deleteSubConfiguration(appsCfg, DIConsts2.properties);
			ConfigUtils.deleteSubConfiguration(appsCfg, DIConsts2.languageLibs);

			ConfigUtils.deleteFile(appsCfg, DIConsts2.application_xml);
			ConfigUtils.deleteFile(appsCfg, DIConsts2.containers_info_xml);

			// DIConsts2.appcfg - do not delete it, because in
			// RuntimeTransaction it won't be persisted
			// when the DIWriter->modifyDeploymentInfo(...) is invoked.
		}
		{// ********************DEPLOY********************//
			if (deployCfg != null) {
				ConfigUtils.deleteConfigEntry(deployCfg, DIConsts2.version);
				deleteAppStatus(deployCfg, null);

				ConfigUtils.deleteConfigEntry(deployCfg, DIConsts2.startUp);

				ConfigUtils.deleteFile(deployCfg, DIConsts2.references);
				ConfigUtils.deleteConfigEntry(deployCfg, DIConsts2.references);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.editor.VersionInterface#getVersion
	 * ()
	 */
	public Version getVersion() {
		return version;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.editor.DIGC#deleteAppStatus(com
	 * .sap.engine.frame.core.configuration.Configuration,
	 * com.sap.engine.frame.core.configuration.Configuration)
	 */
	public void deleteAppStatus(Configuration deployCfg, Configuration globalCfg)
		throws ConfigurationException {
		if (globalCfg != null) {
			ConfigUtils.deleteLocalConfigEntry(globalCfg, DIConsts2.status);
		}
		if (deployCfg != null) {
			ConfigUtils.deleteConfigEntry(deployCfg, DIConsts2.status);
		}
	}

}
