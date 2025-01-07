package com.sap.engine.services.deploy.server.application;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;

import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.ApplicationDeployInfo;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.ReferenceObjectIntf;
import com.sap.engine.services.deploy.container.ResourceReferenceType;
import com.sap.engine.services.deploy.container.op.ApplicationOperationInfo;
import com.sap.engine.services.deploy.container.op.start.ApplicationStartInfo;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.ContainerData;
import com.sap.engine.services.deploy.server.dpl_info.module.InitiallyStarted;
import com.sap.engine.services.deploy.server.dpl_info.module.Resource;
import com.sap.engine.services.deploy.server.dpl_info.module.ResourceReference;
import com.sap.engine.services.deploy.server.utils.Convertor;
import com.sap.engine.services.deploy.server.utils.DSConstants;
import com.sap.engine.services.deploy.server.utils.ValidateUtils;
import com.sap.engine.services.deploy.server.utils.container.ContainerWrapper;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-1-6
 * 
 * @author Rumiana Angelova
 * @version 1.0
 * @since 7.0
 * 
 */

public class TransactionUtil {
	private static final Location location = 
		Location.getLocation(TransactionUtil.class);

	// StartInitially
	public static void updateCDataInDInfo(ApplicationTransaction transaction,
			DeploymentInfo dInfo, ApplicationStartInfo tempInfo,
			ContainerInterface cont) throws DeploymentException {
		final String cName = cont.getContainerInfo().getName();
		if (tempInfo == null) {
			if (location.beDebug()) {
				DSLog
						.traceDebug(
								location,
								"The container [{0}] returned NULL from its [{1}] operation for ApplicationStartInfo.",
								cName, transaction.getTransactionType());
			}
			return;
		}
		// Warnings
		getWarnings(transaction, tempInfo);

		final String[] components = tempInfo.getDeployedComponentNames();
		// ContainerData
		if ((components == null || components.length <= 0)
				&& (!tempInfo.isAddnotSet())) {
			// in case of "set", there must be deployed components
			if (location.beDebug()) {
				DSLog
						.traceDebug(
								location,
								"The container [{0}] returned NONE deployed components from its [{1}] operation. Will be removed from the DeploymentInfo of application [{2}], because its handling type of the returned data is [{3}]",
								cName, transaction.getTransactionType(), dInfo
										.getApplicationName(), (tempInfo
										.isAddnotSet() ? "ADD" : "SET"));
			}
			dInfo.removeContainerData(cName);
			return;
		} else {
			final ContainerData cData = dInfo.getOrCreateContainerData(cName);
			// Check for Add or Set data handling rule
			if (location.beDebug()) {
				DSLog.traceDebug(
						location,
					"The the handling type of the returned data is from [{0}] is [{1}]",
					cName, (tempInfo.isAddnotSet() ? "ADD" : "SET"));
			}
			if (!tempInfo.isAddnotSet()) {
				cData.setProvidedResources(null);
				cData.setFilesForCL(null);
			}

			{// DeployedComponentNames
				if (components != null) {
					final String[] supportedTypes = cont.getContainerInfo()
							.getResourceTypes();
					for (int i = 0; i < components.length; i++) {
						if (supportedTypes != null) {
							for (String supportedType : supportedTypes) {
								cData.addProvidedResource(new Resource(
										components[i], supportedType));
							}
						} else {
							cData.addProvidedResource(new Resource(
									components[i], cont.getContainerInfo()
											.getName()));
						}
					}
				}
			}

			cData.addFilesForCL(Convertor.cObject(tempInfo
					.getFilesForClassloader()));

		}
		// TODO - move to upper lever
		addResourceReferences(dInfo, tempInfo, cont.getContainerInfo()
				.getName());
		// TODO - move to upper lever
	}

	// Default
	public static void updateCDataInDInfo(
		final ApplicationTransaction transaction,
		final DeploymentInfo dInfo, final DeploymentInfo oldDInfo,
		final ApplicationDeployInfo tempInfo, 
		final Set<String> dfNames, final ContainerInterface cont) {
		if (location.beDebug()) {
			// Dump old deploy info.
			DSLog.traceDebug(
							location,
							"The [{0}] application will be [{1}] on [{2}] container and its old deploy info is [{3}].",
				transaction.getModuleID(),
				transaction.getTransactionType(), 
				cont.getContainerInfo().getName(), oldDInfo);
			// dump new application deploy info
			DSLog.traceDebug(
							location,
							"The [{0}] application was [{1}] on [{2}] container, which returned following application deploy info [{3}].",
				transaction.getModuleID(),
				transaction.getTransactionType(), 
				cont.getContainerInfo().getName(), tempInfo);
		}
		try {
			boolean isAdded = false;
			// in case of update if not added remote servers won't be updated
			if (oldDInfo != null
					&& oldDInfo.getCNameAndCData().get(
							cont.getContainerInfo().getName()) != null) {
				transaction.addContainer(cont, tempInfo != null ? tempInfo
						.getDeployProperties() : null);
				isAdded = true;
			}

			if (tempInfo == null) {
				if (location.beDebug()) {
					DSLog
							.traceDebug(
									location,
									"The container [{0}] returned NULL from its [{1}] operation for ApplicationDeployInfo.",
									cont.getContainerInfo().getName(),
									transaction.getTransactionType());
				}
				return;
			}
			getWarnings(transaction, tempInfo);

			final String[] components = tempInfo.getDeployedComponentNames();
			if (components == null || components.length == 0) {
				if (location.beDebug()) {
					DSLog
							.traceDebug(
									location,
									"The container [{0}] returned NONE deployed components from its [{1}] operation. Will be removed from the DeploymentInfo of application [{2}].",
									cont.getContainerInfo().getName(),
									transaction.getTransactionType(), dInfo
											.getApplicationName());
				}
				return;
			}
			if (!isAdded) {// add if not added
				transaction.addContainer(cont, tempInfo.getDeployProperties());
			}

			updateContainerData(dInfo, tempInfo, dfNames, cont, components,
					transaction instanceof RuntimeTransaction);
			updateDeploymentInfo(transaction, dInfo, tempInfo, cont
					.getContainerInfo().getName());
			if (location.beDebug()) {
				DSLog
						.traceDebug(
								location,
								"Container [{0}] accepted components of the application [{1}]",
								cont.getContainerInfo().getName(), transaction
										.getModuleID());
			}
		} finally {
			if (location.beDebug()) {// dump new deploy info
				DSLog
						.traceDebug(
								location,
								"The [{0}] application was [{1}] on [{2}] container and its new deploy info in this finally block is [{3}].",
								transaction.getModuleID(), transaction
										.getTransactionType(), cont
										.getContainerInfo().getName(), dInfo);
			}
		}
	}

	private static void updateContainerData(DeploymentInfo dInfo,
		ApplicationDeployInfo tempInfo, Set<String> dfNames,
			ContainerInterface cont, final String[] components,
			boolean allowAddNotSet) {
		final ContainerData cData;
		final boolean isAddNotSet = allowAddNotSet && tempInfo.isAddnotSet();
		if (isAddNotSet) {// perform "add"
			if (location.beDebug()) {
				DSLog.traceDebug(
								location,
								"The [{0}] returned from [{1}] for [{2}] will be [added].",
					tempInfo,
					cont.getContainerInfo().getName(), dInfo.getApplicationName());
			}
			cData = dInfo.getOrCreateContainerData(cont.getContainerInfo()
					.getName());
		} else { // perform "set"
			if (location.beDebug()) {
				DSLog
						.traceDebug(
								location,
								"The [{0}] returned from [{1}] for [{2}] will be [set].",
								tempInfo, cont.getContainerInfo().getName(),
								dInfo.getApplicationName());
			}
			cData = new ContainerData(cont.getContainerInfo().getName());
		}
		dInfo.setContainerData(cData);

		if (((ContainerWrapper) cont).isCIExtension()
				&& cont.getContainerInfo().isNeedStartInitially()) {
			dInfo.setInitiallyStarted(InitiallyStarted.NO);
		}

		if (tempInfo.isOptionalContainer()) {
			cData.setOptional(true);
		} else {
			cData.setOptional(false);
		}

		cData.addFilesForCL(Convertor
				.cObject(tempInfo.getFilesForClassloader()));
		cData.addHeavyFilesForCL(Convertor.cObject(tempInfo
				.getHeavyFilesForClassloader()));

		final String[] supportedTypes = cont.getContainerInfo()
				.getResourceTypes();
		for (int i = 0; i < components.length; i++) {
			if (components[i] == null) {
				continue;
			}
			if (supportedTypes != null) {
				for (String supportedType : supportedTypes) {
					processProvidedResource(cData, new Resource(components[i],
							supportedType), isAddNotSet);
				}
			} else {
				if ((tempInfo.getPrivateDeployedResources_Types() == null || tempInfo
						.getPrivateDeployedResources_Types().size() < 1)
						&& (tempInfo.getDeployedResources_Types() == null || tempInfo
								.getDeployedResources_Types().size() < 1)) {
					processProvidedResource(cData, new Resource(components[i],
							cont.getContainerInfo().getName()), isAddNotSet);
				}
			}
		}

		updateDCsFromResourcesWithType(cData, tempInfo
				.getPrivateDeployedResources_Types(),
				Resource.AccessType.PRIVATE);
		updateDCsFromResourcesWithType(cData, tempInfo
				.getDeployedResources_Types(), Resource.AccessType.PUBLIC);

		cData.addDeployedFileNames(dfNames);
	}

	private static void processProvidedResource(ContainerData cData,
			Resource resource, boolean isAddNotSet) {
		if (isAddNotSet) {
			cData.updateProvidedResources(resource);
		} else {
			cData.addProvidedResource(resource);
		}
	}

	private static void updateDeploymentInfo(
		ApplicationTransaction transaction, DeploymentInfo dInfo,
		ApplicationDeployInfo tempInfo, final String cName) {

		// TODO - move to upper lever
		addResourceReferences(dInfo, tempInfo, cName);

		if (tempInfo.getReferences() != null) {
			ReferenceObjectIntf containerRefs[] = tempInfo.getReferences();
			ReferenceObject temp = null;
			for (int i = 0; i < containerRefs.length; i++) {
				temp = new ReferenceObject(containerRefs[i]);
				dInfo.addReference(temp);
			}
		}
	}

	private static void updateDCsFromResourcesWithType(ContainerData cData,
			Hashtable resName2resTypes, Resource.AccessType accessModifier) {
		if (resName2resTypes == null) {
			return;
		}
		ValidateUtils.nullValidator(cData, "ContainerData");
		final Enumeration resNames = resName2resTypes.keys();
		String resName = null, resTypes[] = null;
		while (resNames.hasMoreElements()) {
			resName = (String) resNames.nextElement();
			resTypes = (String[]) resName2resTypes.get(resName);
			for (String resType : resTypes) {
				cData.updateProvidedResources(new Resource(resName, resType,
						accessModifier));
			}
		}
	}

	private static void addResourceReferences(DeploymentInfo dInfo,
			ApplicationOperationInfo appOpInfo, String cName) {
		Properties resRefs = appOpInfo.getResourceReferences();
		Properties refResTypes = appOpInfo.getResReferenceTypes();
		Hashtable resTypeOHash = appOpInfo.getResourceReferenceTypesO();
		if (resRefs != null) {
			Enumeration resources = resRefs.propertyNames();
			String resource = null;
			while (resources.hasMoreElements()) {
				resource = (String) resources.nextElement();
				String type = null;
				if (refResTypes.get(resource) != null) {
					type = refResTypes.getProperty(resource);
				} else {
					type = ApplicationDeployInfo.WEAK_REF;
				}
				type = ApplicationDeployInfo.WEAK_REF.equals(type) ? ReferenceObject.REF_TYPE_WEAK
						: ReferenceObject.REF_TYPE_HARD;
				ResourceReferenceType resTypeO = (ResourceReferenceType) resTypeOHash
						.get(resource);
				final ResourceReference resRef = new ResourceReference(
						resource, resRefs.getProperty(resource), type, resTypeO
								.isFunctional(), resTypeO.isClassloading());
				dInfo.addResourceReference(cName, resRef);
			}
		}
	}

	private static void getWarnings(ApplicationTransaction transaction,
			ApplicationOperationInfo adInfo) {
		if (adInfo != null && adInfo.getWarnings() != null) {
			String[] w = new String[adInfo.getWarnings().size()];
			adInfo.getWarnings().toArray(w);
			transaction.addWarnings(w);
		}
	}

	private static void throwResTypes(String contName, String type, Set set)
			throws ServerDeploymentException {

		ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.PARSING_DELOYED_RESOURCES, new String[] {
						type, contName, Convertor.toString(set, "") });
		sde.setMessageID("ASJ.dpl_ds.005137");
		throw sde;
	}

	public static String getDescriptiveMessage(Throwable th) {
		if (th == null) {
			return null;
		}
		if (th.getCause() != null) {
			if (th.getCause().getCause() != null) {
				return getDescriptiveMessage(th.getCause());
			} else {
				if (getMessage(th).indexOf(getMessage(th.getCause())) != -1) {
					if ((getMessage(th.getCause()).indexOf("Hint") != -1)
							|| (getMessage(th.getCause()).indexOf("Solution") != -1)
							|| (getMessage(th.getCause()).indexOf("Reason") != -1)) {
						return getMessage(th.getCause());
					} else {
						return getMessage(th);
					}
				} else {
					return getMessage(th) + DSConstants.EOL_TAB_TAB + " -> "
							+ getMessage(th.getCause());
				}
			}
		} else {
			return getMessage(th);
		}
	}

	private static String getMessage(Throwable th) {
		if (th.getLocalizedMessage() != null) {
			return th.getLocalizedMessage();
		}
		if (th.getMessage() != null) {
			return th.getMessage();
		}
		return th.getClass().toString();
	}

}
