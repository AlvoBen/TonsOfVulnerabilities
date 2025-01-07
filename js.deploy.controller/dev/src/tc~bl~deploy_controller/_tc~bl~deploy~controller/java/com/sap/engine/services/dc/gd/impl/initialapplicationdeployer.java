package com.sap.engine.services.dc.gd.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.VersionStatus;
import com.sap.engine.services.dc.cm.server.ServerFactory;
import com.sap.engine.services.dc.cm.server.spi.SoftwareTypeService;
import com.sap.engine.services.dc.cm.utils.statistics.TimeStatisticsEntry;
import com.sap.engine.services.dc.gd.DeliveryException;
import com.sap.engine.services.dc.gd.RollingDeliveryException;
import com.sap.engine.services.dc.repo.ScaId;
import com.sap.engine.services.dc.repo.SoftwareType;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import com.sap.engine.services.deploy.DeployService;

/**
 * @author Rumiana Angelova
 * @version 7.0
 * 
 */
abstract class InitialApplicationDeployer extends Deployer {

	public static String[] SUPPORTEDTRANSPORTS = { "p4" };

	private SoftwareTypeService getSoftwareTypeService() {
		return (SoftwareTypeService) ServerFactory
				.getInstance()
				.createServer()
				.getServerService(
						ServerFactory.getInstance().createSoftwareTypeRequest());
	}

	private Properties prepareProperties(DeploymentItem deploymentItem,
			boolean isStandAlone) throws DeliveryException {
		final SoftwareTypeService stService = getSoftwareTypeService();
		final SoftwareType swtType = deploymentItem.getSda().getSoftwareType();
		boolean isStandAloneButNotApplication = stService
				.getSoftwareTypesByAttribute(
						SoftwareTypeService.STAND_ALONE_BUT_NOT_APPLICATION)
				.contains(swtType);
		final String dcName = deploymentItem.getSda().getName();
		final String dcVendor = deploymentItem.getSda().getVendor();

		if ((dcName == null) || (dcName.equals(""))) {
			throw new DeliveryException(DCExceptionConstants.DC_EMPTY,
					new String[] { deploymentItem.toString() });
		}
		if ((dcVendor == null) || (dcVendor.equals(""))) {
			throw new DeliveryException(DCExceptionConstants.VENDOR_EMPTY,
					new String[] { deploymentItem.toString() });
		}

		final Properties deploymentProperties = new Properties();
		deploymentProperties.put(DeployService.applicationProperty, dcName);
		deploymentProperties.put(DeployService.providerProperty, dcVendor);

		final String newSdaPath = deploymentItem.getSduFilePath();
		if (stService.getSoftwareTypesByAttribute(SoftwareTypeService.WAR)
				.contains(swtType)) {
			String context = getContextRoot(newSdaPath);
			if (context != null && !context.equals("")) {
				deploymentProperties.put("web:" + context.trim(), newSdaPath
						.substring(newSdaPath.lastIndexOf(File.separator) + 1));
			}
		} else if (stService.getSoftwareTypesByAttribute(
				SoftwareTypeService.JEE_APPLICATION).contains(swtType)) {
			final Map aliases = getContextRoots(newSdaPath);
			if (aliases != null && !aliases.isEmpty()) {
				deploymentProperties.putAll(aliases);
			}
		}

		if (isStandAloneButNotApplication) {
			deploymentProperties.put(DeployService.softwareType, swtType
					.getName());
		}
		if (isStandAlone && swtType.getSubTypeName() != null) {// to avoid
			// renaming of
			// the
			// standalone
			// modules with
			// extention
			// .sda
			deploymentProperties.put(DeployService.softwareSubType, swtType
					.getSubTypeName());
		}
		final ScaId scaId = deploymentItem.getSda().getScaId();
		if (scaId != null) {
			deploymentProperties
					.put(DeployService.scaVendor, scaId.getVendor());
			deploymentProperties.put(DeployService.scaName, scaId.getName());
		}

		return deploymentProperties;
	}

	void performDeployment(DeploymentItem deploymentItem)
			throws DeliveryException, RollingDeliveryException {
		deploymentItem.startTimeStatEntry("Application Deployer",
				TimeStatisticsEntry.ENTRY_TYPE_OTHER);
		final String tagName = "Application Deployer " + deploymentItem.getSdu().getId();
	    Accounting.beginMeasure( tagName, InitialApplicationDeployer.class );	    
		try {
			if (deploymentItem.getVersionStatus().equals(
					VersionStatus.NOT_RESOLVED)) {
				throw new DeliveryException(DCExceptionConstants.NOT_RESOLVED,
						new String[] { deploymentItem.toString() });
			}

			final SoftwareTypeService stService = getSoftwareTypeService();

			final boolean isStandAlone = stService.getSoftwareTypesByAttribute(
					SoftwareTypeService.STAND_ALONE).contains(
					deploymentItem.getSda().getSoftwareType());
			final Properties deploymentProperties = prepareProperties(
					deploymentItem, isStandAlone);

			if (deploymentItem.getVersionStatus().equals(VersionStatus.NEW)) {
				deploy(deploymentItem, isStandAlone, deploymentItem
						.getSduFilePath(), deploymentProperties);
			} else {
				update(deploymentItem, isStandAlone, deploymentItem
						.getSduFilePath(), deploymentProperties);
			}
		} finally {
			Accounting.endMeasure( tagName );
			deploymentItem.finishTimeStatEntry();
		}
	}

	abstract protected void update(DeploymentItem deploymentItem,
			boolean isStandAlone, String newSdaPath,
			Properties deploymentProperties) throws DeliveryException,
			RollingDeliveryException;

	abstract protected void deploy(DeploymentItem deploymentItem,
			boolean isStandAlone, String newSdaPath,
			Properties deploymentProperties) throws DeliveryException,
			RollingDeliveryException;

	/*
	 * Method registerLibraryReferences is removed as its logic is relevant only
	 * to SDM 6.20 & 6.20 compatible SDA files. This logic is not supported in
	 * 6.40 and higher.
	 * 
	 * 6.20 SDA deployment descriptors contain info about application runtime
	 * references:
	 * 
	 * <!ELEMENT j2ee-deployment-descriptor (display-name, description?, module,
	 * security-role, additional-references?)>
	 * 
	 * The additional-references element describes components of J2EE Engine,
	 * which are referenced by the current software. It is functional only, when
	 * deployment is done on SAP J2EE Engine 6.20. During deployment on SAP J2EE
	 * Engine 6.20, references will be created on the referenced components.
	 * 
	 * During deployment on SAP J2EE Engine 6.30 or higher, the element will be
	 * just ignored. For specification of such references on SAP J2EE Engine
	 * 6.30 Engine-specific descriptors (provider.xml,
	 * application-j2ee-engine.xml) must be used.
	 */

	private String getContextRoot(String sdaFile) {
		String context = null;
		JarFile jar = null;

		try {
			jar = new JarFile(sdaFile);
			Manifest manifest = jar.getManifest();

			if (manifest == null) {
				return context;
			}

			Attributes attr = manifest.getMainAttributes();

			if (attr == null || attr.isEmpty()) {
				return context;
			}

			context = attr.getValue("context-root");
		} catch (IOException ioe) {// $JL-EXC$
			// nothing to do
		} finally {
			try {
				if (jar != null) {
					jar.close();
				}
			} catch (IOException e) {// $JL-EXC$
				// nothing to do
			}
		}

		return context;
	}

	private Map getContextRoots(String sdaFile) {
		Map contexts = null;
		JarFile jar = null;
		String contextAttr = null;

		try {
			jar = new JarFile(sdaFile);
			Manifest manifest = jar.getManifest();

			if (manifest == null) {
				return contexts;
			}

			Attributes attr = manifest.getMainAttributes();

			if (attr == null || attr.isEmpty()) {
				return contexts;
			}

			contextAttr = attr.getValue("context-roots");

			if (contextAttr == null || contextAttr.equals("")) {
				return contexts;
			} else {
				contexts = new HashMap();
				String current = null;
				String alias = null;
				String webModule = null;
				int index = -1;
				int innerIndex = -1;

				// context-roots=web_uri1:context_root1::web_uri2:context_root2
				do {
					if ((index = contextAttr.indexOf("::")) > -1) {
						current = contextAttr.substring(0, index);
						contextAttr = contextAttr.substring(index + 2);
					} else {
						current = contextAttr;
					}

					if ((innerIndex = current.indexOf(':')) > -1) {
						webModule = current.substring(0, innerIndex).trim();
						alias = current.substring(innerIndex + 1).trim();
						contexts.put("web:" + alias, webModule);
					}
				} while (index > -1);
			}
		} catch (IOException ioe) {// $JL-EXC$
			// nothing to do
		} finally {
			try {
				if (jar != null) {
					jar.close();
				}
			} catch (IOException e) {// $JL-EXC$
				// nothing to do
			}
		}

		return contexts;
	}

	final String concatVendorAndName(String dcVendor, String dcName) {
		if (dcVendor == null || dcName == null) {
			return null;
		}
		return (dcVendor + "/" + dcName);
	}
} // class EngineJ2EE620OnlineDeployer

