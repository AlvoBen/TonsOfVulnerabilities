package com.sap.engine.services.deploy.ear.jar;

import java.io.File;
import java.util.Hashtable;

import com.sap.engine.lib.descriptors5.application.ApplicationType;
import com.sap.engine.lib.descriptors5.application.ModuleType;
import com.sap.engine.lib.descriptors5.application.WebType;
import com.sap.engine.lib.descriptors.applicationj2eeengine.ApplicationJ2EeEngine;
import com.sap.engine.lib.descriptors.applicationj2eeengine.FailOverEnableType;
import com.sap.engine.lib.descriptors.applicationj2eeengine.FailOverEnableType_disable;
import com.sap.engine.lib.descriptors.applicationj2eeengine.FailOverEnableType_enable;
import com.sap.engine.lib.descriptors.applicationj2eeengine.ModulesAdditionalType;
import com.sap.engine.lib.descriptors.applicationj2eeengine.ReferenceTargetType;
import com.sap.engine.lib.descriptors.applicationj2eeengine.ReferenceType;
import com.sap.engine.lib.descriptors5.javaee.DescriptionType;
import com.sap.engine.lib.descriptors5.javaee.DisplayNameType;
import com.sap.engine.lib.descriptors5.javaee.IconType;
import com.sap.engine.lib.descriptors5.javaee.PathType;
import com.sap.engine.lib.descriptors5.javaee.SecurityRoleType;
import com.sap.engine.lib.io.SerializableFile;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.op.IOpConstants;
import com.sap.engine.services.deploy.container.op.util.FailOver;
import com.sap.engine.services.deploy.ear.EARExceptionConstants;
import com.sap.engine.services.deploy.ear.J2EEModule;
import com.sap.engine.services.deploy.ear.Module;
import com.sap.engine.services.deploy.ear.common.SecurityRoles;
import com.sap.engine.services.deploy.ear.exceptions.BaseWrongStructureException;
import com.sap.engine.services.deploy.ear.modules.Connector;
import com.sap.engine.services.deploy.ear.modules.EJB;
import com.sap.engine.services.deploy.ear.modules.Java;
import com.sap.engine.services.deploy.ear.modules.Web;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.tc.logging.Location;

/**
 * @author Luchesar Cekov
 */
public class EarDescriptorPopulator {
	
	private static final Location location = 
		Location.getLocation(EarDescriptorPopulator.class);
	
	public static void populateFromApplicationXML(File tmpDir,
			EarDescriptor descriptor, ApplicationType applicationType) {
		descriptor.setApplicationJ2EEVersion(applicationType.getVersion() + "");
		DisplayNameType[] displayNames = applicationType.getDisplayName();
		if (displayNames.length > 0) {
			descriptor.setDisplayName(displayNames[0].get_value());
		}

		DescriptionType[] appTypes = applicationType.getDescription();
		if (appTypes.length > 0) {
			descriptor.setDescription(appTypes[0].get_value());
		}

		IconType[] icons = applicationType.getIcon();
		if (icons.length > 0) {
			IconType firstIcon = icons[0];

			PathType smallIconName = firstIcon.getSmallIcon();
			if (smallIconName != null) {
				SerializableFile smallIconFilefile = new SerializableFile();
				smallIconFilefile.setFileName(smallIconName.get_value());
				descriptor.setSmallIcon(smallIconFilefile);
			}

			PathType largeIconName = firstIcon.getLargeIcon();
			if (largeIconName != null) {
				SerializableFile largeIconFilefile = new SerializableFile();
				largeIconFilefile.setFileName(largeIconName.get_value());
				descriptor.setLargeIcon(largeIconFilefile);
			}
		}

		extractModules(descriptor, tmpDir, applicationType.getModule());
		descriptor.setRoles(extractSecurityRoles(applicationType
				.getSecurityRole()));

		PathType libDirectory = applicationType.getLibraryDirectory();
		if (libDirectory != null) {
			descriptor.setLibraryDirectory(libDirectory.get_value());
		}
	}

	public static void populateFromApplicationEngineXML(File tmpDir,
			EarDescriptor descriptor, ApplicationJ2EeEngine applicationType)
			throws BaseWrongStructureException {
		descriptor.setReferences(getReference(applicationType.getReference()));
		descriptor.setClassPath(applicationType.getClasspath());
		descriptor.setProviderName(applicationType.getProviderName());
		extractModulesAditional(descriptor, tmpDir, applicationType);
		int niValidationResult = descriptor.setJavaVersion(applicationType
				.getJavaVersion(), true);
		// log erroneous custom values
		if (niValidationResult == IOpConstants.UNSUPPORTED) {
			DSLog
					.logWarningWithFaultyDcName(
							location, 
							descriptor.getProviderName()
									+ DeployConstants.DELIMITER_4_PROVIDER_AND_NAME
									+ descriptor.getDisplayName(),
							"ASJ.dpl_ds.002001",
							"Unsupported java version [{0}] was detected in the application-j2ee-engine.xml. Custom value will not take effect! A default version [{1}] will be used.",
							applicationType.getJavaVersion(),
							IOpConstants.DEFAULT_JAVA_VERSION);
		} else if (niValidationResult == IOpConstants.SUB_VERSION) {
			DSLog
					.logWarningWithFaultyDcName(
							location, 
							descriptor.getProviderName()
									+ DeployConstants.DELIMITER_4_PROVIDER_AND_NAME
									+ descriptor.getDisplayName(),
							"ASJ.dpl_ds.002002",
							"A sub java version [{0}] was detected in the application-j2ee-engine.xml. Sub versions are not allowed! A version [{1}] will be used.",
							applicationType.getJavaVersion(), descriptor
									.getJavaVersion());
		} else if (niValidationResult == IOpConstants.MISSING) {
			if (location.beDebug()) {
				DSLog
						.traceDebug(
								location, 
								"No Java version was detected in the application-j2ee-engine.xml. A default version [{0}] will be used.",
								descriptor.getJavaVersion());
			}
		}
		if (applicationType.getFailOverEnable() != null) {
			FailOverEnableType failOver = applicationType.getFailOverEnable();
			if (failOver != null) {
				if (failOver instanceof FailOverEnableType_disable) {
					descriptor.setFailOver(FailOver.DISABLE);
				} else if (failOver instanceof FailOverEnableType_enable) {
					final FailOverEnableType_enable foEnable = ((FailOverEnableType_enable) failOver);
					descriptor.setFailOver(FailOver.getFailOverByKey(foEnable
							.getMode().getValue(), foEnable.getScope()
							.getValue(), foEnable.getDelta()));
				} else {
					throw new BaseWrongStructureException(
							EARExceptionConstants.WRONG_FAIL_OVER_VALUE);
				}
			}
		}
		if (applicationType.getStartUp() != null) {
			descriptor.setStartUpValue(applicationType.getStartUp().getMode()
					.getValue());
		}
	}

	private static void extractModules(EarDescriptor descriptor, File tmpDir,
			ModuleType[] modules) {
		for (int i = 0; i < modules.length; i++) {
			ModuleType type = modules[i];
			ModuleType.Choice1 choice = type.getChoiceGroup1();
			J2EEModule currentModule = null;
			if (choice.isSetConnector()) {
				currentModule = new Connector(
						tmpDir,
						removeFirstSlashInUri(choice.getConnector().get_value()));
			} else if (choice.isSetEjb()) {
				currentModule = new EJB(tmpDir, removeFirstSlashInUri(choice
						.getEjb().get_value()));
			} else if (choice.isSetJava()) {
				currentModule = new Java(tmpDir, removeFirstSlashInUri(choice
						.getJava().get_value()));
			} else if (choice.isSetWeb()) {
				WebType webModule = choice.getWeb();
				currentModule = new Web(tmpDir, removeFirstSlashInUri(webModule
						.getWebUri().get_value()), webModule.getContextRoot()
						.get_value());
			}
			PathType altDd = type.getAltDd();
			if (altDd != null) {
				currentModule.setAlt_dd(altDd.get_value());
			}
			descriptor.addModule(currentModule);
		}
	}

	private static SecurityRoles[] extractSecurityRoles(SecurityRoleType[] roles) {
		SecurityRoles[] result = new SecurityRoles[roles.length];
		for (int i = 0; i < roles.length; i++) {
			SecurityRoleType role = roles[i];
			DescriptionType[] descriptions = role.getDescription();
			String description = "";
			if (descriptions.length > 0) {
				description = descriptions[0].get_value();
			}
			result[i] = new SecurityRoles(role.getRoleName().get_value(),
					description);
		}
		return result;
	}

	/**
	 * @param applicationType
	 * @return
	 */
	private static void extractModulesAditional(EarDescriptor descriptor,
			File tmpDir, ApplicationJ2EeEngine applicationType) {
		Hashtable aditionalModules = new Hashtable();// Obsolete just for
		// backward
		// compatibility
		ModulesAdditionalType moduleType = applicationType
				.getModulesAdditional();
		if (moduleType != null) {
			com.sap.engine.lib.descriptors.applicationj2eeengine.ModuleType[] modules = moduleType
					.getModule();
			StringBuilder containers = new StringBuilder(); // Obsolete just for
			// backward
			// compatibility
			for (int i = 0; i < modules.length; i++) {
				String[] containersArray = modules[i].getContainerType();
				for (int j = 0; j < containersArray.length; j++) {
					if (Containers.getInstance().getContainer(
							containersArray[j]) == null)
						continue;
					Module module = new Module(tmpDir, modules[i]
							.getEntryName(), containersArray[j]);
					descriptor.addModule(module);
					containers.append(containersArray[j]).append(
							j < containersArray.length - 1 ? ";" : "");// Obsolete
					// just
					// for
					// backward
					// compatibility
				}
				aditionalModules.put(modules[i].getEntryName(), containers
						.toString());// Obsolete just for backward compatibility
			}

		}
		descriptor.setAdditionalModules(aditionalModules);
	}

	private static ReferenceObject[] getReference(ReferenceType[] referances)
			throws BaseWrongStructureException {
		ReferenceObject[] result = new ReferenceObject[referances.length];
		for (int i = 0; i < referances.length; i++) {
			ReferenceType type = referances[i];
			ReferenceObject reference = new ReferenceObject();

			ReferenceTargetType referenceTarget = type.getReferenceTarget();
			String provider = referenceTarget.getProviderName();
			if (provider != null) {
				if (provider.length() == 0) {
					throw new BaseWrongStructureException(
							EARExceptionConstants.WRONG_PROVIDER_VALUE);
				}
				if (!provider.trim().equals("")) {
					reference.setReferenceProviderName(provider);
				}
			}

			reference.setReferenceTargetType(referenceTarget.getTargetType()
					.getValue());
			reference.setReferenceType(type.getReferenceType().getValue());
			reference.setReferenceTarget(referenceTarget.get_value());

			result[i] = reference;
		}
		return result;
	}

	private static String removeFirstSlashInUri(String uri) {
		if (uri == null)
			return null;
		if (uri.startsWith("/") || uri.startsWith("\\")) {
			return uri.substring(1);
		}
		return uri;
	}

}