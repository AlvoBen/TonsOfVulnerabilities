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
package com.sap.engine.services.deploy.server.dpl_info.module;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import com.sap.engine.services.deploy.container.util.CAConstants;
import com.sap.engine.services.deploy.container.util.CAConvertor;
import com.sap.engine.services.deploy.container.util.PrintIt;
import com.sap.engine.services.deploy.server.utils.DSCloneUtils;
import com.sap.engine.services.deploy.server.utils.FSUtils;
import com.sap.engine.services.deploy.server.utils.StringUtils;

/**
 * Contains all container related information for each application. One 
 * instance of this object is created per every container involved in 
 * the deployment of a given application and is inserted into its deployment
 * info.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class ContainerData implements Serializable, PrintIt {
	private static final long serialVersionUID = 3554423744226591995L;

	// The container name cannot be changed.
	private String contName;

	private boolean optional = false;

	// $JL-SER$ - the implementation is Serializable
	private Set<String> filesForCL = new LinkedHashSet<String>();

	// $JL-SER$ - the implementation is Serializable
	private Set<String> heavyFilesForCL = new LinkedHashSet<String>();

	private Set<Resource> providedResources = new HashSet<Resource>();

	// The 'deployedFileNames' are String objects
	// $JL-SER$ - the implementation is Serializable
	private Set<String> deployedFileNames = new LinkedHashSet<String>();

	// $JL-SER$ - the implementation is Serializable
	private Set<ResourceReference> resourceReferences = 
		new LinkedHashSet<ResourceReference>();

	/**
	 * The constructor.
	 * @param contName the name of the container, involved in the deployment of
	 * the corresponding application. Must not be null.
	 */
	public ContainerData(final String contName) {
		assert contName != null;
		this.contName = StringUtils.intern(contName);
	}

	/**
	 * @return the name of the container, involved in the deployment of the 
	 * corresponding application. Not null.
	 */
	public String getContName() {
		return contName;
	}

	/**
	 * @return flag indicating that the container is optional and the 
	 * application can be started even without it.
	 * @deprecated no real use cases
	 */
	@Deprecated
	public boolean isOptional() {
		return optional;
	}

	/**
	 * @return set of absolute file paths, to files used by the application 
	 * class loader.
	 */
	public Set<String> getFilesForCL() {
		return filesForCL;
	}

	/**
	 * @return set of absolute file paths, to files used by the application 
	 * heavy class loader.
	 */
	public Set<String> getHeavyFilesForCL() {
		return heavyFilesForCL;
	}

	/**
	 * @return set of resources provided by this particular container.
	 */
	public Set<Resource> getProvidedResources() {
		return providedResources;
	}

	/**
	 * @return set of file names (modules), deployed by this container.
	 */
	public Set<String> getDeployedFileNames() {
		return deployedFileNames;
	}

	/**
	 * @return set of references to consumed resources.
	 */
	public Set<ResourceReference> getResourceReferences() {
		return resourceReferences;
	}

	/**
	 * Set the optional flag indicating that the container is optional and the 
	 * application can be started even without it.
	 * @param optional the new value of the optional flag.
	 * @deprecated no real use cases
	 */
	@Deprecated
	public void setOptional(final boolean optional) {
		this.optional = optional;
	}

	/**
	 * Set files for the application class loader.
	 * @param filesForCL set of absolute paths to files used by application 
	 * class loader. Can be null or empty set.
	 */
	public void setFilesForCL(final Set<String> filesForCL) {
		this.filesForCL.clear();
		addFilesForCL(filesForCL);
	}

	/**
	 * Add files for the application class loader.
	 * @param filesForCL set of absolute paths to files used by application 
	 * class loader. Can be null or empty set.
	 */
	public void addFilesForCL(final Set<String> filesForCL) {
		if (filesForCL == null || filesForCL.size() == 0) {
			return;
	}
		for(final String filePath : filesForCL) {
			this.filesForCL.add(FSUtils.pathNormalizer(filePath));
	        }
	}

	/**
	 * Set files for the application heavy class loader.
	 * @param heavyFilesForCL set of absolute paths to files used by application 
	 * heavy class loader. Can be null or empty set.
	 */
	public void setHeavyFilesForCL(final Set<String> heavyFilesForCL) {
		this.heavyFilesForCL.clear();
		addHeavyFilesForCL(heavyFilesForCL);
	}

	/**
	 * Add files for the application heavy class loader.
	 * @param heavyFilesForCL set of absolute paths to files used by application 
	 * heavy class loader. Can be null or empty set.
	 */
	public void addHeavyFilesForCL(final Set<String> heavyFilesForCL) {
		if (heavyFilesForCL == null || heavyFilesForCL.size() == 0) {
			return;
		}
		for(final String filePath : heavyFilesForCL) {
			this.heavyFilesForCL.add(FSUtils.pathNormalizer(filePath));
		}
	}

	/**
	 * Set provided resources by this particular container.
	 * @param provided resources by this particular container. Can be
	 * null or empty set.
	 */
	public void setProvidedResources(final Set<Resource> resources) {
		providedResources.clear();
		if (resources == null || resources.size() == 0) {
			return;
		}
		for(final Resource resource : resources) {
			addProvidedResource(resource);
		}
	}

	/**
	 * Add new resource to the set of provided resources.
	 * @param newResource new provided resource.
	 * @throws IllegalStateException  if such resource is already provided.
	 */
	public void addProvidedResource(final Resource newResource) {
		if (providedResources.contains(newResource)) {
			throw new IllegalStateException(
					"ASJ.dpl_ds.006036 Cannot add deployed component "
							+ newResource.getName() + " in container data for "
							+ getContName() + ", because it is already added.");
		}
		updateProvidedResources(newResource);
	}

	/**
	 * Updates the given <code>Resource</code>, which means pure add or
	 * replace.
	 * @param providedResource newly provided resource.
	 */
	public void updateProvidedResources(final Resource providedResource) {
		providedResources.add(providedResource);
	}

	/**
	 * Set the files (modules), deployed by this container. 
	 * @param deployedFileNames the set of deployed file names. Can be null or
	 * empty set.
	 */
	public void setDeployedFileNames(final Set<String> deployedFileNames) {
		this.deployedFileNames.clear();
		addDeployedFileNames(internSet(deployedFileNames));
	}

	/**
	 * Add the files (modules), to the set of files deployed by this container. 
	 * @param deployedFileNames the set of deployed file names. Can be null or
	 * empty set.
	 */
	public void addDeployedFileNames(final Set<String> deployedFileNames) {
		if (deployedFileNames == null || deployedFileNames.size() == 0) {
			return;
		}
		this.deployedFileNames.addAll(internSet(deployedFileNames));
	}

	/**
	 * Set the references to consumed resources.
	 * @param consumedResources references to consumed resources. Not null.
	 */
	public void setResourceReferences(
		final Set<ResourceReference> consumedResources) {
		assert consumedResources != null;
		resourceReferences.clear();
		resourceReferences.addAll(consumedResources);
	}

	/**
	 * Add new consumed resource to the set of consumed resources.
	 * @param consumedResource reference to consumed resource. Not null.
	 */
	public void addResourceReference(ResourceReference consumedResource) {
		assert consumedResource != null;
		resourceReferences.add(consumedResource);
	}


	@Override
	public int hashCode() {
		return getContName().hashCode();
	}

	@Override
	public String toString() {
		return getContName();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof ContainerData)) {
			return false;
		}

		ContainerData other = (ContainerData) obj;

			if (!this.getContName().equals(other.getContName())) {
				return false;
			}
		return true;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		final ContainerData cData = new ContainerData(getContName());
		cData.setOptional(isOptional());
		cData.setFilesForCL(DSCloneUtils.cloneObject(getFilesForCL()));
		cData.setHeavyFilesForCL(
			DSCloneUtils.cloneObject(getHeavyFilesForCL()));
		cData.providedResources = new HashSet(getProvidedResources());
		cData.setDeployedFileNames(
			DSCloneUtils.cloneObject(getDeployedFileNames()));
		cData.setResourceReferences(
			DSCloneUtils.cloneObject(getResourceReferences()));

		return cData;
	}

	// basic methods

	public String print(String shift) {
		final StringBuffer sb = new StringBuffer(CAConstants.EOL);

		sb.append(shift + "ContName = " + getContName() + CAConstants.EOL);
		sb.append(shift + "isOptional = " + isOptional() + CAConstants.EOL);
		sb.append(shift + "FilesForCL = "
			+ CAConvertor.toString(CAConvertor.cObject(getFilesForCL()), "")
			+ CAConstants.EOL);
		sb.append(shift	+ "HeavyFilesForCL = "
			+ CAConvertor.toString(CAConvertor.cObject(getHeavyFilesForCL()),
				"") + CAConstants.EOL);
		sb.append(shift + "ProvidedResources = "
			+ CAConvertor.toString(getProvidedResources(), "")
			+ CAConstants.EOL);
		sb.append(shift + "DeployedFileNames = "
			+ CAConvertor.toString(getDeployedFileNames(), "")
			+ CAConstants.EOL);
		sb.append(shift + "ResourceReferences = "
			+ CAConvertor.toString(getResourceReferences(), "")
			+ CAConstants.EOL);

		return sb.toString();
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		this.contName = StringUtils.intern(this.contName);
		this.deployedFileNames = internSet(this.deployedFileNames);
	}

	private Set<String> internSet(final Set<String> set) {
		if (set == null) {
			return null;
		}

		final Set<String> resultSet = new HashSet<String>();

		for (Iterator<String> iteratorSet = set.iterator(); iteratorSet
				.hasNext();) {
			String string = iteratorSet.next();
			resultSet.add(StringUtils.intern(string));
		}
		set.clear();
		set.addAll(resultSet);
		return set;

	}
}
