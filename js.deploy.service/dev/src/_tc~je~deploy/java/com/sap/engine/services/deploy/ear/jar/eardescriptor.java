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
package com.sap.engine.services.deploy.ear.jar;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import com.sap.engine.lib.io.SerializableFile;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.ReferenceObjectIntf;
import com.sap.engine.services.deploy.ear.J2EEModule;
import com.sap.engine.services.deploy.ear.Module;
import com.sap.engine.services.deploy.ear.SimpleEarDescriptor;
import com.sap.engine.services.deploy.ear.common.SecurityRoles;
import com.sap.engine.services.deploy.ear.modules.Connector;
import com.sap.engine.services.deploy.ear.modules.EJB;
import com.sap.engine.services.deploy.ear.modules.Java;
import com.sap.engine.services.deploy.ear.modules.Web;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.lib.javalang.tool.ReadResult;

/**
 *@author Luchesar Cekov
 */
public class EarDescriptor extends SimpleEarDescriptor {
	private static final long serialVersionUID = -8316182872064876407L;

	public static final Containers containers = Containers.getInstance();

	protected Modules modules;
	protected String description = null;
	protected String displayName = null;
	protected SerializableFile smallIcon = null;
	protected SerializableFile largeIcon = null;
	protected SecurityRoles roles[] = null;
	protected Hashtable archiveTable = null;
	protected Hashtable componentTable = null;
	protected ReferenceObjectIntf references[] = null;
	protected String classPath = null;
	protected String providerName = DeployConstants.DEFAULT_PROVIDER_4_APPS_SAP_COM;
	protected String applicationJ2EEVersion;
	protected ReadResult annotations;
	protected boolean hasApplicationXML = true;
	protected String libraryDirectory = null;
	protected boolean hasContainersInfoXML = false;
	// this is just because backward compatibility
	private Hashtable additionalModules;

	public EarDescriptor() {
		modules = new Modules();
	}

	public Set<Module> getAllModules() {
		HashSet<Module> result = new HashSet<Module>();
		for (final Set<Module> mods : modules.container2modules.values()) {
			result.addAll(mods);
		}
		return result;
	}

	@Override
	public Set<Module> getJ2EEModules() {
		HashSet<Module> result = new HashSet<Module>();

		Set<String> registeredJ2eeContainerNames = containers
				.getRegisteredJ2eeContainerNames();
		for (final String containerName : modules.container2modules.keySet()) {
			if (registeredJ2eeContainerNames.contains(containerName)) {
				result.addAll(modules.container2modules.get(containerName));
			}
		}
		return result;
	}

	public Set<Module> getJ2EEModules(J2EEModule.Type aType) {
		ContainerInterface ci = containers.getJ2eeContainer(aType);
		if (ci == null)
			return new HashSet<Module>(0);
		return modules.get(ci.getContainerInfo().getName());
	}

	@Override
	public Set<Module> getModulesAdditional() {
		HashSet<Module> result = new HashSet<Module>();
		for (final String containerName : modules.container2modules.keySet()) {
			ContainerInterface container = containers
					.getContainer(containerName);
			if (container == null
					|| !container.getContainerInfo().isJ2EEContainer()) {
				result.addAll(modules.get(containerName));
			}
		}
		return result;
	}

	@Override
	public Hashtable getAdditionalModules() {
		return additionalModules;
	}

	@Override
	public J2EEModule[] getModules() {
		Set<Module> j2eeModules = getJ2EEModules();
		J2EEModule[] result = new J2EEModule[j2eeModules.size()];
		j2eeModules.toArray(result);
		return result;
	}

	public void setAdditionalModules(Hashtable modules) {
		additionalModules = modules;
	}

	public Set<Module> getModules(ContainerInterface aContainer) {
		return modules.get(aContainer.getContainerInfo().getName());
	}

	public Set<Module> getModules(String aContainerName) {
		return modules.get(aContainerName);
	}

	/**
	 * 
	 * @return
	 * @deprecated
	 */
	@Deprecated
	public Hashtable<String, File[]> getAllContainerFiles() {
		Hashtable<String, File[]> allcontainerFiles = new Hashtable<String, File[]>();
		for (String container : modules.container2modules.keySet()) {
			Set<Module> mods = modules.container2modules.get(container);
			File[] files = new File[mods.size()];
			int counter = 0;
			for(Module mod : mods) {
				files[counter++] = mod;
			}
			allcontainerFiles.put(container, files);
		}
		return allcontainerFiles;
	}

	public Set<String> containers() {
		return Collections.unmodifiableSet(modules.container2modules.keySet());
	}

	/**
	 * Gets an array with the references.
	 * 
	 * @return an array with the references.
	 */
	@Override
	public ReferenceObjectIntf[] getReferences() {
		return references;
	}

	/**
	 * Sets an array with the references.
	 * 
	 * @param references
	 */
	public void setReferences(ReferenceObjectIntf[] references) {
		this.references = references;
	}

	@Override
	public SecurityRoles[] getRoles() {
		return this.roles;
	}

	public void setRoles(SecurityRoles[] roles) {
		this.roles = roles;
	}

	@Override
	public EJB[] getEJBs() {
		Set<Module> resultSet = getJ2EEModules(J2EEModule.Type.ejb);
		EJB[] result = new EJB[resultSet.size()];
		resultSet.toArray(result);
		return result;
	}

	@Override
	public Web[] getWEBs() {
		Set<Module> resultSet = getJ2EEModules(J2EEModule.Type.web);
		Web[] result = new Web[resultSet.size()];
		resultSet.toArray(result);
		return result;
	}

	@Override
	public Java[] getClients() {
		Set<Module> resultSet = getJ2EEModules(J2EEModule.Type.java);
		Java[] result = new Java[resultSet.size()];
		resultSet.toArray(result);
		return result;
	}

	@Override
	public Connector[] getConnectors() {
		Set<Module> resultSet = getJ2EEModules(J2EEModule.Type.connector);
		Connector[] result = new Connector[resultSet.size()];
		resultSet.toArray(result);
		return result;
	}

	public void addModule(Module module) {
		if (modules.contains(module))
			return;
		modules.addModule(module);
	}

	public void removeModule(Module module) {
		modules.removeModule(module);
	}

	/**
	 * Sets the description of this jar file.
	 * 
	 * @param descr
	 *            the description.
	 */
	public void setDescription(String descr) {
		this.description = descr;
	}

	/**
	 * Sets the display name of the jar file. If the name is null the jar will
	 * be displayed with the name of the physical jar file.
	 * 
	 * @param name
	 *            the name of jar that is used when the jar is displayed.
	 */
	public void setDisplayName(String name) {
		this.displayName = name;
	}

	/**
	 * Sets the location of the small icon for visual representation of the jar
	 * file.
	 * 
	 * @param icon
	 *            the location tho the icon file.
	 */
	public void setSmallIconName(String icon) {
		this.smallIcon = new SerializableFile(icon);
	}

	/**
	 * Sets the location of the large icon for visual representation of the jar
	 * file.
	 * 
	 * @param icon
	 *            the location of large icon file.
	 */
	public void setLargeIconName(String icon) {
		this.largeIcon = new SerializableFile(icon);
	}

	/**
	 * Gets the additional class path.
	 * 
	 * @return the additional class path.
	 */
	@Override
	public String getClassPath() {
		return classPath;
	}

	/**
	 * Sets the additional class path.
	 * 
	 * @param classPath
	 *            the additional.
	 */
	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	/**
	 * Gets the provider name.
	 * 
	 * @return the provider name.
	 */
	@Override
	public String getProviderName() {
		return providerName;
	}

	/**
	 * Sets the provider name.
	 * 
	 * @param providerName
	 *            the provider name.
	 */
	public void setProviderName(String providerName) {
		this.providerName = providerName;
		if (this.providerName == null) {
			this.providerName = DeployConstants.DEFAULT_PROVIDER_4_APPS_SAP_COM;
		}
	}

	/**
	 * Returns the description of jar file.
	 * 
	 * @return String the current value of description.
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the display name of the jar file.
	 * 
	 * @return String the display name of the jar.
	 */
	@Override
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Gets the jar small icon location.
	 * 
	 * @return String the location of small icon file.
	 */
	@Override
	public String getSmallIconName() {
		if (smallIcon != null) {
			return smallIcon.getFileName();
		}

		return null;
	}

	/**
	 * Gets the jar large icon location.
	 * 
	 * @return String the location of jar large icon file.
	 */
	@Override
	public String getLargeIconName() {
		if (largeIcon != null) {
			return largeIcon.getFileName();
		}

		return null;
	}

	/**
	 * Gets the small icon.
	 * 
	 * @return SerializableFile the small icon.
	 */
	@Override
	public SerializableFile getSmallIcon() {
		return this.smallIcon;
	}

	/**
	 * Sets the small icon.
	 * 
	 * @param smallIcon
	 *            SerializableFile.
	 */
	public void setSmallIcon(SerializableFile smallIcon) {
		this.smallIcon = smallIcon;
	}

	/**
	 * Gets the large icon.
	 * 
	 * @return SerializableFile the large icon.
	 */
	@Override
	public SerializableFile getLargeIcon() {
		return this.largeIcon;
	}

	/**
	 * Sets the large icon.
	 * 
	 * @param largeIcon
	 *            SerializableFile.
	 */
	public void setLargeIcon(SerializableFile largeIcon) {
		this.largeIcon = largeIcon;
	}

	/**
	 * Gets the library directory.
	 * 
	 * @param String
	 *            the library directory.
	 */
	@Override
	public String getLibraryDirectory() {
		return this.libraryDirectory;
	}

	/**
	 * Sets the library directory.
	 * 
	 * @param libDir
	 *            the library directory.
	 */
	public void setLibraryDirectory(String libDir) {
		this.libraryDirectory = libDir;
	}

	public Hashtable<String, String> getFileMappings() {
		return new Hashtable<String, String>(modules.getAbs2RelPaths());
	}

	@Override
	public String getApplicationJ2EEVersion() {
		return applicationJ2EEVersion;
	}

	public void setApplicationJ2EEVersion(String aApplicationJ2EEVersion) {
		applicationJ2EEVersion = aApplicationJ2EEVersion;
	}

	public ReadResult getAnnotations() {
		return annotations;
	}

	public void setAnnotations(ReadResult aAnnotations) {
		annotations = aAnnotations;
	}

	public boolean getHasApplicationXML() {
		return hasApplicationXML;
	}

	public void setHasApplicationXML(boolean aHasApplicationXML) {
		hasApplicationXML = aHasApplicationXML;
	}

	
	public boolean getHasContainersInfoXML() {
		return hasContainersInfoXML;
	}

	public void setHasContainersInfoXML(boolean aHasContainersInfoXML) {
		hasContainersInfoXML = aHasContainersInfoXML;
	}

	private static class Modules {

		Hashtable<String, Set<Module>> container2modules = new Hashtable<String, Set<Module>>();
		Hashtable<String, String> fileMappings = new Hashtable<String, String>();
		Hashtable<String, String> altDDMappings = new Hashtable<String, String>();

		public static final int INITIAL_CAPACITY_FOR_MODULES_FROM_TYPE = 3;

		public void addModule(Module aModule) {
			String containerName = getContainerName(aModule);
			if (containerName == null)
				return;
			getTheInternalSet(containerName).add(aModule);
			fileMappings.put(aModule.getAbsolutePath(), aModule.getUri());
		}

		public void removeModule(Module aModule) {
			String containerName = getContainerName(aModule);
			if (containerName == null)
				return;
			getTheInternalSet(containerName).remove(aModule);
			// TODO remove the libe below. There could be several modules for
			// one file path, so we can not remove this file mapping
			fileMappings.remove(aModule.getAbsolutePath());
		}

		public boolean contains(Module aModule) {
			for (final Set<Module> modulesPerType : container2modules.values()) {
				if (modulesPerType.contains(aModule)) {
					return true;
				}
			}

			return false;
		}

		public Set<Module> get(String containerName) {
			Set<Module> result = container2modules.get(containerName);
			return result == null ? new HashSet<Module>(0) : result;
		}

		public Map<String, String> getAbs2RelPaths() {
			return Collections.unmodifiableMap(fileMappings);
		}

		private String getContainerName(Module aModule) {
			String containerName = null;
			if (aModule instanceof J2EEModule) {
				ContainerInterface container = containers
						.getJ2eeContainer(((J2EEModule) aModule).getType());
				if (container == null)
					return null;
				containerName = container.getContainerInfo().getName();
				aModule.setModuleType(containerName);
				return containerName;
			}
			return aModule.getModuleType();
		}

		private Set<Module> getTheInternalSet(String containerName) {
			if (containerName == null)
				return new HashSet<Module>(0);
			Set<Module> result = container2modules.get(containerName);
			if (result == null) {
				result = new HashSet<Module>(
						INITIAL_CAPACITY_FOR_MODULES_FROM_TYPE);
				container2modules.put(containerName, result);
			}
			return result;
		}
	}
}
