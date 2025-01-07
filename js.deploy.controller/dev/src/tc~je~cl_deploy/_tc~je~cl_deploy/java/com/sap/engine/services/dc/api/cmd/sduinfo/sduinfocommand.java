/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * Created on Oct 19, 2005
 */
package com.sap.engine.services.dc.api.cmd.sduinfo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.cmd.AbstractCommand;
import com.sap.engine.services.dc.api.cmd.Command;
import com.sap.engine.services.dc.api.explorer.RepositoryExplorer;
import com.sap.engine.services.dc.api.explorer.RepositoryExplorerException;
import com.sap.engine.services.dc.api.model.Dependency;
import com.sap.engine.services.dc.api.model.Sca;
import com.sap.engine.services.dc.api.model.ScaId;
import com.sap.engine.services.dc.api.model.Sda;
import com.sap.engine.services.dc.api.model.SdaId;
import com.sap.engine.services.dc.api.model.Sdu;
import com.sap.engine.services.dc.api.model.SoftwareType;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright (c) 2005, SAP-AG Date: Oct 19, 2005
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * 
 */
public class SduInfoCommand extends AbstractCommand {
	private String name, vendor;
	private File outputFile = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.api.cmd.AbstractCommand#processOption(java
	 * .lang.String, java.lang.String)
	 */
	protected int processOption(String key, String value) {
		if ("-n".equals(key) || "--name".equals(key)) {
			if (this.name != null) {
				addDescription("option '" + key + "' could occure only once.",
						true);
				return Command.CODE_ERROR_OCCURRED;
			}
			this.name = value;
			return Command.CODE_SUCCESS;
		} else if ("-v".equals(key) || "--vendor".equals(key)) {
			if (this.vendor != null) {
				addDescription("option '" + key + "' could occure only once.",
						true);
				return Command.CODE_ERROR_OCCURRED;
			}
			this.vendor = value;
			return Command.CODE_SUCCESS;
		} else if ("-o".equals(key) || "--outFile".equals(key)) {
			this.outputFile = new File(value);
			try {
				if (this.outputFile.exists()) {
					if (!this.outputFile.canWrite()) {
						addDescription("Cannot write to existing file '"
								+ super.getCanonicalFilePath(this.outputFile)
								+ "'", true);
						return Command.CODE_ERROR_OCCURRED;
					}
				} else if (!this.outputFile.createNewFile()) {
					addDescription(
							"Cannot write to file '"
									+ super
											.getCanonicalFilePath(this.outputFile)
									+ "'", true);
					return Command.CODE_ERROR_OCCURRED;
				}
			} catch (IOException e) {
				addDescription("Exception during creating file '"
						+ super.getCanonicalFilePath(this.outputFile) + "'",
						true);
				super.daLog().logThrowable(e);
				return Command.CODE_ERROR_OCCURRED;
			} finally {
				try {
					this.outputFile.delete();
				} catch (SecurityException e) {
					// $JL-EXC$
				}
			}
			return Command.CODE_SUCCESS;
		} else {
			addDescription("Unknown option '" + key + "', value '" + value
					+ "'", false);
			return Command.CODE_SUCCESS_WITH_WARNINGS;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.cmd.AbstractCommand#executeCommand()
	 */
	protected int executeCommand() {
		if (this.name != null && this.vendor == null) {
			addDescription("Component vendor name is not provided.", true);
			return Command.CODE_PREREQUISITE_VIOLATED;
		}
		if (this.vendor != null && this.name == null) {
			addDescription("Component name is not provided", true);
			return Command.CODE_PREREQUISITE_VIOLATED;
		}

		try {
			RepositoryExplorer repositoryExplorer = getClient()
					.getComponentManager().getRepositoryExplorerFactory()
					.createRepositoryExplorer();
			if (this.name == null) {
				return getAll(repositoryExplorer);
			} else {
				return getComponent(repositoryExplorer);
			}
		} catch (RepositoryExplorerException e) {
			addDescription("RepositoryExplorerException:" + e.getMessage(),
					true);
			super.daLog().logThrowable(e);
			return Command.CODE_ERROR_OCCURRED;
		} catch (ConnectionException e) {
			addDescription("ConnectionException:" + e.getMessage(), true);
			super.daLog().logThrowable(e);
			return Command.CODE_ERROR_OCCURRED;
		}
	}

	private int getComponent(RepositoryExplorer repositoryExplorer)
			throws RepositoryExplorerException {
		Sda sda = repositoryExplorer.findSda(this.name, this.vendor);
		StringBuffer buffer = new StringBuffer();
		if (sda != null) {
			buildSdaInfo(Command.TAB, sda, buffer);
		} else {
			Sca sca = repositoryExplorer.findSca(this.name, this.vendor);
			if (sca != null) {
				buildScaInfo(Command.TAB, sca, buffer);
			} else {
				addDescription("Component name '" + this.name + "', vendor '"
						+ this.vendor + "' not found.", true);
				return Command.CODE_ERROR_OCCURRED;
			}
		}
		return visualizeResult(buffer);
	}

	private int getAll(RepositoryExplorer repositoryExplorer)
			throws RepositoryExplorerException {
		Sdu[] sdus = repositoryExplorer.findAll();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < sdus.length; i++) {
			if (sdus[i] instanceof Sda) {
				buildSdaInfo(Command.TAB, (Sda) sdus[i], buffer);
			} else if (sdus[i] instanceof Sca) {
				buildScaInfo(Command.TAB, (Sca) sdus[i], buffer);
			}
		}
		return visualizeResult(buffer);
	}

	private int visualizeResult(StringBuffer rawBuffer) {

		StringBuffer buffer = new StringBuffer("<sdus>").append(Command.EOL)
				.append(rawBuffer).append("</sdus>").append(Command.EOL);

		getCmdLogger().logInfo(buffer.toString());
		if (this.outputFile != null) {
			FileWriter fileWriter = null;
			try {
				fileWriter = new FileWriter(this.outputFile);
				fileWriter.write(buffer.toString());
			} catch (IOException e) {
				addDescription(
						"IOException while store the sduInfo to the file:"
								+ e.getMessage(), true);
				super.daLog().logThrowable(e);
				return Command.CODE_ERROR_OCCURRED;
			} finally {
				try {
					if (fileWriter != null) {
						fileWriter.close();
					}
				} catch (IOException e1) {
					// $JL-EXC$
				}
			}
		}
		return Command.CODE_SUCCESS;
	}

	private void buildSdaInfo(String inFront, Sda sda, StringBuffer buffer) {
		buffer.append(inFront).append("<Sda>").append(Command.EOL);
		buildSduInfo(inFront + Command.TAB, sda, buffer);

		SoftwareType softwareType = sda.getSoftwareType();
		buffer.append(inFront).append(Command.TAB).append("<SoftwareType>")
				.append(softwareType.getName()).append("</SoftwareType>")
				.append(Command.EOL);
		String subType = softwareType.getSubTypeName();
		if (subType != null && subType.length() > 0) {
			buffer.append(inFront).append(Command.TAB).append(
					"<SoftwareSubType>").append(subType).append(
					"</SoftwareSubType>").append(Command.EOL);
		}
		ScaId scaId = sda.getScaId();
		if (scaId != null) {
			buffer.append(inFront).append(Command.TAB).append("<ScaId>")
					.append(Command.EOL).append(inFront).append(
							Command.TWO_TABS).append("<Name>").append(
							scaId.getName()).append("</Name>").append(
							Command.EOL).append(inFront).append(
							Command.TWO_TABS).append("<Vendor>").append(
							scaId.getVendor()).append("</Vendor>").append(
							Command.EOL).append(inFront).append(Command.TAB)
					.append("</ScaId>").append(Command.EOL);
		}
		Set dependencies = sda.getDependencies();
		if (dependencies != null && dependencies.size() > 0) {
			buffer.append(inFront).append(Command.TAB).append("<Dependencies>")
					.append(Command.EOL);
			Dependency dependency;
			for (Iterator iter = dependencies.iterator(); iter.hasNext();) {
				dependency = (Dependency) iter.next();
				buffer.append(inFront).append(Command.TWO_TABS).append(
						"<Dependency>").append(Command.EOL).append(inFront)
						.append(Command.THREE_TABS).append("<Name>").append(
								dependency.getName()).append("</Name>").append(
								Command.EOL).append(inFront).append(
								Command.THREE_TABS).append("<Vendor>").append(
								dependency.getVendor()).append("</Vendor>")
						.append(Command.EOL).append(inFront).append(
								Command.TWO_TABS).append("</Dependency>")
						.append(Command.EOL);
			}
			buffer.append(inFront).append(Command.TAB)
					.append("</Dependencies>").append(Command.EOL);
		}
		buildComponentElementXML(inFront + Command.TAB, sda, buffer);

		buffer.append(inFront).append("</Sda>").append(Command.EOL);
	}

	private void buildScaInfo(String inFront, Sca sca, StringBuffer buffer) {
		Set sdas = sca.getSdaIds();
		buffer.append(inFront).append("<Sca>").append(Command.EOL);
		buildSduInfo(inFront + Command.TAB, sca, buffer);

		if (sdas != null && sdas.size() > 0) {
			buffer.append(inFront).append(Command.TAB).append("<Content>")
					.append(Command.EOL);
			for (Iterator iter = sdas.iterator(); iter.hasNext();) {
				SdaId sdaId = (SdaId) iter.next();
				buffer.append(inFront).append(Command.TWO_TABS).append(
						"<SdaId name=\"").append(sdaId.getName()).append(
						"\" vendor=\"").append(sdaId.getVendor())
						.append("\"/>").append(Command.EOL);
			}
			buffer.append(inFront).append(Command.TAB).append("</Content>")
					.append(Command.EOL);
		}

		buildComponentElementXML(inFront + Command.TAB, sca, buffer);
		buffer.append(inFront).append("</Sca>").append(Command.EOL);
	}

	private void buildSduInfo(String inFront, Sdu sdu, StringBuffer buffer) {
		buffer.append(inFront).append("<Name>").append(sdu.getName()).append(
				"</Name>").append(Command.EOL).append(inFront).append(
				"<Vendor>").append(sdu.getVendor()).append("</Vendor>").append(
				Command.EOL).append(inFront).append("<Location>").append(
				sdu.getLocation()).append("</Location>").append(Command.EOL)
				.append(inFront).append("<Version>").append(sdu.getVersion())
				.append("</Version>").append(Command.EOL);
	}

	private void buildComponentElementXML(String inFront, Sdu sdu,
			StringBuffer buffer) {
		String xml = sdu.getComponentElementXML();
		if (xml != null && xml.length() > 0) {
			buffer.append(inFront).append("<ComponentElementXML>").append(xml)
					.append("</ComponentElementXML>").append(Command.EOL);
		}
	}

}
