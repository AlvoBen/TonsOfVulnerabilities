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
package com.sap.engine.services.deploy.server.editor.impl;

import java.util.Hashtable;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.dpl_info.module.Version;
import com.sap.engine.services.deploy.server.editor.DIGC;
import com.sap.engine.services.deploy.server.editor.DIReader;
import com.sap.engine.services.deploy.server.editor.DIWriter;
import com.sap.engine.services.deploy.server.editor.EditorFactory;
import com.sap.engine.services.deploy.server.editor.impl.first.DIGCImpl1;
import com.sap.engine.services.deploy.server.editor.impl.first.DIReaderImpl1;
import com.sap.engine.services.deploy.server.editor.impl.first.DIWriterImpl1;
import com.sap.engine.services.deploy.server.editor.impl.second.DIConsts2;
import com.sap.engine.services.deploy.server.editor.impl.second.DIGCImpl2;
import com.sap.engine.services.deploy.server.editor.impl.second.DIReaderImpl2;
import com.sap.engine.services.deploy.server.editor.impl.second.DIWriterImpl2;
import com.sap.engine.services.deploy.server.utils.ValidateUtils;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class EditorFactoryImpl extends EditorFactory {

	private Hashtable version_DIReader = new Hashtable();
	private Hashtable version_DIWriter = new Hashtable();
	private Hashtable version_DIGC = new Hashtable();

	public EditorFactoryImpl() throws ServerDeploymentException {
		// DIReader-s
		addDIReader(new DIReaderImpl1());
		addDIReader(new DIReaderImpl2());
		// DIWriter-s
		addDIWriter(new DIWriterImpl1());
		addDIWriter(new DIWriterImpl2());
		// DIGC-s
		addDIGC(new DIGCImpl1());
		addDIGC(new DIGCImpl2());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.editor.EditorFactory#getDIWriter
	 * (com.sap.engine.frame.core.configuration.Configuration)
	 */
	public DIWriter getDIWriter(Configuration cfg)
			throws ServerDeploymentException {
		final Version version = readVersion(cfg);
		return getDIWriter(version);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.editor.EditorFactory#getDIWriter
	 * (com.sap.engine.services.deploy.server.dpl_info.module.Version)
	 */
	public DIWriter getDIWriter(Version version)
			throws ServerDeploymentException {
		final DIWriter diWriter = (DIWriter) version_DIWriter.get(version);
		if (diWriter == null) {
			missing("writer", version);
		}
		return diWriter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.editor.EditorFactory#getDIReader()
	 */
	public DIReader getDIReader(Configuration cfg)
			throws ServerDeploymentException {
		final Version version = readVersion(cfg);
		return getDIReader(version);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.editor.EditorFactory#getDIReader
	 * (com.sap.engine.services.deploy.server.dpl_info.module.Version)
	 */
	public DIReader getDIReader(Version version)
			throws ServerDeploymentException {
		ValidateUtils.nullValidator(version, "version");

		final DIReader diReader = (DIReader) version_DIReader.get(version);
		if (diReader == null) {
			missing("reader", version);
		}
		return diReader;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.editor.EditorFactory#getDIGC(com
	 * .sap.engine.frame.core.configuration.Configuration)
	 */
	public DIGC getDIGC(Configuration cfg) throws ServerDeploymentException {
		final Version version = readVersion(cfg);
		return getDIGC(version);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.editor.EditorFactory#getDIGC(com
	 * .sap.engine.services.deploy.server.dpl_info.module.Version)
	 */
	public DIGC getDIGC(Version version) throws ServerDeploymentException {
		final DIGC diGC = (DIGC) version_DIGC.get(version);
		if (diGC == null) {
			missing("garbage collector", version);
		}
		return diGC;
	}

	private Version readVersion(Configuration appCfg)
			throws ServerDeploymentException {
		try {
			Version version = null;
			if (appCfg.existsConfigEntry(DIConsts2.version)) {
				final String versionName = (String) appCfg
						.getConfigEntry(DIConsts2.version);
				version = Version.getVersionByName(versionName);
			} else {
				version = Version.FIRST;
			}
			return version;
		} catch (ConfigurationException ce) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
					new String[] { "reading deployment info '"
							+ DIConsts2.version + "'" }, ce);
			sde.setMessageID("ASJ.dpl_ds.005082");
			throw sde;
		}
	}

	private void addDIReader(DIReader diReader)
			throws ServerDeploymentException {
		ValidateUtils.nullValidator(diReader, "deployment info reader");

		final Version version = diReader.getVersion();
		if (version_DIReader.get(version) != null) {
			duplicated("reader", version);
		}
		version_DIReader.put(version, diReader);
	}

	private void addDIWriter(DIWriter diWriter)
			throws ServerDeploymentException {
		ValidateUtils.nullValidator(diWriter, "deployment info writer");

		final Version version = diWriter.getVersion();
		if (version_DIWriter.get(version) != null) {
			duplicated("writer", version);
		}
		version_DIWriter.put(version, diWriter);
	}

	private void addDIGC(DIGC diGC) throws ServerDeploymentException {
		ValidateUtils.nullValidator(diGC, "deployment info garbage collector");

		final Version version = diGC.getVersion();
		if (version_DIGC.get(version) != null) {
			duplicated("garbage collector", version);
		}
		version_DIGC.put(version, diGC);
	}

	private void duplicated(String role, Version version)
			throws ServerDeploymentException {
		ServerDeploymentException  sde = new ServerDeploymentException(
				ExceptionConstants.DI_ACTOR_DUPLICATED, new String[] { role,
						version.getName() });
		sde.setMessageID("ASJ.dpl_ds.005201");
		throw sde;
	}

	private void missing(String role, Version version)
			throws ServerDeploymentException {
		ServerDeploymentException  sde = new ServerDeploymentException(
				ExceptionConstants.DI_ACTOR_MISSING, new String[] { role,
						version.getName() });
		sde.setMessageID("ASJ.dpl_ds.005200");
		throw sde;
	}

}
