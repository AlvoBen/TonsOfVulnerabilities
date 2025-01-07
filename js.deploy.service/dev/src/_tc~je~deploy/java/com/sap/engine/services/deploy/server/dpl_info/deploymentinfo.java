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
package com.sap.engine.services.deploy.server.dpl_info;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import com.sap.engine.lib.io.hash.Index;
import com.sap.engine.services.deploy.ConfigProvider;
import com.sap.engine.services.deploy.DeployService;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.AdditionalAppInfo;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.ExceptionInfo;
import com.sap.engine.services.deploy.container.op.IOpConstants;
import com.sap.engine.services.deploy.container.op.util.ModuleProvider;
import com.sap.engine.services.deploy.container.op.util.StartUp;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.container.op.util.StatusDescription;
import com.sap.engine.services.deploy.container.op.util.StatusDescriptionsEnum;
import com.sap.engine.services.deploy.container.util.CAConstants;
import com.sap.engine.services.deploy.container.util.CAConvertor;
import com.sap.engine.services.deploy.ear.common.CloneUtils;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.DUtils;
import com.sap.engine.services.deploy.server.cache.containers.ContainerNameComparatorByCLPrioReverted;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.server.dpl_info.module.ApplicationName;
import com.sap.engine.services.deploy.server.dpl_info.module.ContainerData;
import com.sap.engine.services.deploy.server.dpl_info.module.InitiallyStarted;
import com.sap.engine.services.deploy.server.dpl_info.module.Resource;
import com.sap.engine.services.deploy.server.dpl_info.module.ResourceReference;
import com.sap.engine.services.deploy.server.dpl_info.module.Version;
import com.sap.engine.services.deploy.server.editor.impl.EditorUtil;
import com.sap.engine.services.deploy.server.editor.impl.second.DIConsts2;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.Convertor;
import com.sap.engine.services.deploy.server.utils.DSCloneUtils;
import com.sap.engine.services.deploy.server.utils.FSUtils;
import com.sap.engine.services.deploy.server.utils.ShmComponentUtils;
import com.sap.engine.services.deploy.server.utils.StringUtils;
import com.sap.engine.services.deploy.server.utils.ValidateUtils;
import com.sap.engine.services.deploy.server.utils.container.ContainerWrapper;
import com.sap.tc.logging.Location;

/**
 * This class is used for storing the information obtained during deployment on
 * different containers and to provide the DeployService the needed information
 * about the deployed application. This class is intended only for internal use
 * by deploy service.
 * 
 * @author Monika Kovachka, Rumiana Angelova
 * @version 6.30
 */
public class DeploymentInfo extends AdditionalAppInfo {
	private static final long serialVersionUID = 3L;
	private static final Location location = 
		Location.getLocation(DeploymentInfo.class);

	// Global info
	private Version version;
	private InitiallyStarted initiallyStarted;
	private ModuleProvider moduleProvider;
	private final ApplicationName applicationName;
	private boolean isStandAloneArchive;
	private String[] remoteSupport;
	private String additionalClasspath; 
	private String applicationXML;
	private String containerInfoXML;
	private ReferenceObject[] referencesTo;
	private Properties properties;
	// the container properties
	private Hashtable<String, ContainerData> cName_cData;

	private Status status = Status.UNKNOWN;	
	// Runtime data, which must not be stored in DB	
	
	private transient String thisAppWorkDir;
	private ConfigProvider configProvider;
	private transient StatusDescription statusDesc;
	private transient Index indexFS;

	// Runtime data, which must not be stored in DB

	/**
	 * Creates new deployment info object for the given application.
	 * @param appName the name of the application. Must not be null.
	 */
	public DeploymentInfo(final String appName) {
		this(appName, Version.getNewestVersion());
		initiallyStarted = InitiallyStarted.YES;
		remoteSupport = new String[0];
		properties = new Properties();
		cName_cData = new Hashtable<String, ContainerData>(5);
		statusDesc = new StatusDescription();
	}

	/**
	 * Creates new deployment info object for the given application and 
	 * version.
	 * @param appName the name of the application. Must be not null.
	 * @param version the version of the deployment info. Must not be null.
	 */
	public DeploymentInfo(final String appName, final Version version) {
		assert appName != null;
		assert version != null;
		applicationName = new ApplicationName(appName);
		configProvider = new ConfigProvider(applicationName);
		this.version = version;
		setJavaVersion(IOpConstants.DEFAULT_JAVA_VERSION, false);
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.container.AdditionalAppInfo#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		final DeploymentInfo dInfo = new DeploymentInfo(
			getApplicationNameO().getApplicationName());

		dInfo.setAdditionalAppInfo((AdditionalAppInfo) super.clone());
		dInfo.version = getVersion();
		dInfo.setInitiallyStarted(getInitiallyStarted());
		dInfo.setModuleProvider(getModuleProvider());
		dInfo.setStandAloneArchive(isStandAloneArchive());
		// cloning of the status description is also included here
		dInfo.setProperties(DSCloneUtils.clonePropertiesString(getProperties()));
		dInfo.setStatus(getStatus(), getStatusDescription());
		dInfo.setIndexFS((getIndexFS() != null ? (Index) getIndexFS()
				.deepClone() : null));
		dInfo.setRemoteSupport(
			CloneUtils.cloneStringArray(getRemoteSupport()));
		dInfo.setAdditionalClasspath(getAdditionalClasspath());

		ValidateUtils.nullValidator(getConfigProvider(), "ConfigProvider of "
				+ getApplicationName());
		dInfo.configProvider = (ConfigProvider) getConfigProvider().clone();

		dInfo.setApplicationXML(getApplicationXML());
		dInfo.containerInfoXML = getContainerInfoXML();
		dInfo.setReferences(DSCloneUtils.clone(getReferences()));
		// dInfo.setReferencesFrom(DSCloneUtils.clone(getReferenesFrom()));
		
		if (getCNameAndCData() != null) {
			dInfo.setCNameAndCData(DSCloneUtils.clone(getCNameAndCData()));
		} else {
			dInfo.setCNameAndCData(null);
		}
		// thisAppWorkDir - already is cloned
		if (getExceptionInfo() != null) {
			dInfo.setExceptionInfo((ExceptionInfo) getExceptionInfo().clone());
		} else {
			dInfo.setExceptionInfo(null);
		}
		return dInfo;
	}

	/**
	 * Method used to obtain all unsatisfied references. We achieve this by
	 * removing of the references satisfied by resources (public or private) of
	 * the same application. Unsatisfied references can be satisfied only by
	 * public resources, provided by other components.
	 * 
	 * @param allResources  set of all resources provided by the application.
	 * Must be obtained via call to <tt>getAllProvidedResources()</tt> method.
	 * We use this, because the <tt>getAllProvidedResources()</tt> is 
	 * relatively expensive operation and when we already have obtained the set
	 * of all provided resources, it is not necessary to obtain it again.
	 * @return set of unsatisfied resource references. Not null.
	 */
	public Set<ResourceReference> getNotSelfProvidedResourceReferences(
			final Set<Resource> allResources) {
		final Set<ResourceReference> resRefs = new HashSet<ResourceReference>();
		for (ContainerData cData : cName_cData.values()) {
			for (ResourceReference ref : cData.getResourceReferences()) {
				final Resource res = new Resource(ref.getResRefName(), ref
						.getResRefType());
				if (!allResources.contains(res)) {
					resRefs.add(ref);
				}
			}
		}
		return resRefs;
	}

	@Override
	public String toString() {
		final String shift = "   ";
		final StringBuffer sb = new StringBuffer(CAConstants.EOL
				+ this.getClass().getName() + CAConstants.EOL);

		sb.append(shift + "Version = "
				+ (getVersion() != null ? getVersion().getName() : null)
				+ CAConstants.EOL);
		sb.append(shift + "ApplicationName = " + getApplicationName()
				+ CAConstants.EOL);
		sb.append(shift + "ModuleProvider = "
				+ CAConvertor.toString(getModuleProvider(), shift + shift)
				+ CAConstants.EOL);
		sb.append(shift + "isStandAloneArchive = " + isStandAloneArchive()
				+ CAConstants.EOL);
		sb.append(shift + "Status = " + getStatus() + " - "
				+ getStatusDescription().getDescription() + CAConstants.EOL);
		sb.append(shift + "IndexFS = " + getIndexFS() + CAConstants.EOL);
		sb.append(shift + "InitiallyStarted = " + getInitiallyStarted()
				+ CAConstants.EOL);
		// ******* AdditionalAppInfo *******//
		final AdditionalAppInfo aaInfo = getAdditionalAppInfo();
		sb.append(shift + "FailOver = " + aaInfo.getFailOver()
				+ CAConstants.EOL);
		sb.append(shift + "JavaVersion = " + aaInfo.getJavaVersion()
				+ CAConstants.EOL);
		sb.append(shift + "CustomJavaVersion = " + aaInfo.isCustomJavaVersion()
				+ CAConstants.EOL);
		sb.append(shift + "StartUp = " + aaInfo.getStartUpO()
				+ " (isSupportingLazyStart()="
				+ isSupportingLazyStartFromToString() + ")" + CAConstants.EOL);
		// ******* AdditionalAppInfo *******//
		sb.append(shift + "RemoteSupport = "
				+ CAConvertor.toString(getRemoteSupport(), "") + CAConstants.EOL);
		sb.append(shift + "AdditionalClasspath = " + getAdditionalClasspath()
				+ CAConstants.EOL);
		sb.append(shift + "ConfigProvider = "
				+ CAConvertor.toString(getConfigProvider(), shift + shift)
				+ CAConstants.EOL);
		sb.append(shift + "ApplicationXML = "
				+ (getApplicationXML() != null ? "YES" : "NO")
				+ CAConstants.EOL);
		sb.append(shift + "ReferencesTo = "
				+ CAConvertor.toString(getReferences(), "") + CAConstants.EOL);
		sb.append(shift + "Properties = " + getProperties() + CAConstants.EOL);

		sb.append("   ContainerData = ");
		for(final ContainerData cData : cName_cData.values()) {
			sb.append(CAConvertor.toString(cData, shift + shift)
				+ CAConstants.EOL);
		}

		sb.append("   ThisAppWorkDir = " + getThisAppWorkDir()
			+ CAConstants.EOL);
		sb.append("   ExceptionInfo = "
						+ (getExceptionInfo() != null ? "YES" : "NO")
						+ CAConstants.EOL);

		return sb.toString();
	}

	/**
	 * Add files for class loader provided by the given container. 
	 * @param contName the name of the container, which provides the files.
	 * @param filesForCL array of absolute file paths to files needed by
	 * class loader.
	 */
	public void addContName_FilesForCL(
		final String contName, final String[] filesForCL) {
		if (filesForCL == null || filesForCL.length == 0) {
			return;
		}
		final ContainerData cData = cName_cData.get(contName);
		if (cData == null) {
			throw new NullPointerException(
				"ASJ.dpl_ds.006037 The application '"
							+ getApplicationName() + "' is not deployed "
							+ "on container '" + contName
							+ "'. The files for class loader "
				+ CAConvertor.toString(filesForCL, "") 
				+ " won't be added.");
		}
		cData.addFilesForCL(
			Convertor.cObject(FSUtils.pathNormalizer(filesForCL)));
	}

	/**
	 * Set the names of containers involved in deployment of the corresponding
	 * applications.
	 * @param contNames array of container names. Not null.
	 */
	public void setContainerNames(final String[] contNames) {
		assert contNames != null;
		cName_cData.clear();
		for (final String containerName : contNames) {
			cName_cData.put(StringUtils.intern(containerName), 
				new ContainerData(containerName));
		}
	}

	/**
	 * Return the names of containers involved in deployment of the 
	 * corresponding application.
	 * @return the names of containers involved in deployment of the 
	 * corresponding application.
	 */
	public String[] getContainerNames() {
		return cName_cData.keySet().toArray(
			new String[cName_cData.size()]);
	}

	/**
	 * @return a map of container names to names of deployed files. Not null.
	 */
	public Map<String, String[]> getDeployedFileNames() {
		final Map<String, String[]> containers_deployedFileNames = 
			new Hashtable<String, String[]>();
		for(final ContainerData cData : cName_cData.values()) {
			final Set<String> deployedFileNames = 
				cData.getDeployedFileNames();
			containers_deployedFileNames.put(
				StringUtils.intern(cData.getContName()), 
				deployedFileNames.toArray(
					new String[deployedFileNames.size()]));
		}
		return containers_deployedFileNames;
	}

	/**
	 * Set the deployed file names.
	 * @param cName_deployedFileNames map of container names to names of 
	 * deployed files.
	 */
	public void setDeployedFileNames(
		final Map<String, String[]> cName_deployedFileNames) {
		for(final ContainerData cData : cName_cData.values()) {
			final String[] dfNames = 
				cName_deployedFileNames.get(cData.getContName());
			cData.setDeployedFileNames(Convertor.cObject(dfNames));
		}
	}

	/**
	 * Returns a set of all provided resources. 
	 * @return a set of all provided resources. Not null.
	 */
	public Set<Resource> getAllProvidedResources() {
		final HashSet<Resource> providedResources = new HashSet<Resource>();
		for (ContainerData cData : cName_cData.values()) {
			providedResources.addAll(cData.getProvidedResources());
		}
		return providedResources;
	}

	/**
	 * Return all resources provided by a given container.
	 * @param containerName the name of the container. 
	 * @return all resources provided by a given container. Cannot be null
	 * even if the container is not involved in the deployment of the 
	 * corresponding application.
	 */
	public Set<Resource> getProvidedResources(final String containerName) {
		ContainerData cData = cName_cData.get(containerName);
		if(cData == null) {
			return Collections.emptySet();
		}
		return cData.getProvidedResources();
	}

	/**
	 * Check whether a given container is optional.
	 * @param contName the name of the container to be checked. Can be null.
	 * @return <tt>true</tt> if the container is optional, or <tt>false</tt>
	 * if the container is not involved in the deployment of the corresponding
	 * application; the passed container name is null or the container is not
	 * optional.
	 * @deprecated we have not optional containers anymore.
	 */
	@Deprecated
	public boolean isOptionalContainer(final String contName) {
		if (contName == null) {
			return false;
		}
		final ContainerData cData = cName_cData.get(contName);
		return (cData != null) ? cData.isOptional() : false;
	}

	/**
	 * @return the names of all optional containers involved in the deployment
	 * of the corresponding application or <tt>null</tt> if all of them are
	 * mandatory.
	 * @deprecated we have not optional containers anymore.
	 */
	@Deprecated
	public String[] getOptionalContainers() {
		final List<String> list = new ArrayList<String>();
		for(final String contName : cName_cData.keySet()) {
			final ContainerData cData = cName_cData.get(contName);
			if (cData.isOptional()) {
				list.add(contName);
			}
		}
		final int count = list.size();
		return (count > 0) ? 
			list.toArray(new String[count]) : null;
	}

	/**
	 * Set optional containers.
	 * @param optContainers array of optional container names. Can be null.
	 * @deprecated we have not optional containers anymore.
	 */
	@Deprecated
	public void setOptionalContainers(final String[] optContainers) {
		if (optContainers == null) {
			return;
		}
		for(final String optContainer : optContainers) {
			if(optContainer == null) {
				continue;
			}
			getOrCreateContainerData(optContainer).setOptional(true);
		}
	}

	/**
	 * Add reference to resource consumed by the given container. The 
	 * corresponding application is a consumer of the specified resource.
	 * @param contName the name of the container which consumes the resource. 
	 * Not null.
	 * @param resRef the reference to the consumed resource. Not null.
	 * @throws IllegalStateException if the container with this name is not
	 * involved in the deployment of the corresponding application.
	 */
	public void addResourceReference(final String contName, 
		final ResourceReference resRef) {
		assert contName != null;
		assert resRef != null;

		final ContainerData cData = cName_cData.get(contName);
		if (cData == null) {
			throw new IllegalStateException(
				"ASJ.dpl_ds.006039 The application '"
							+ getApplicationName() + "' is not deployed "
				+ "on container '" + contName + "'. The resource reference "
				+ resRef.print("") + " won't be added.");
		}
		cData.addResourceReference(resRef);
	}

	/**
	 * @return ordered set of references to all consumed resources. Not null.
	 */
	public Set<ResourceReference> getResourceReferences() {
		// TODO: Why we need linked hash set here?
		final Set<ResourceReference> resRefs = 
			new LinkedHashSet<ResourceReference>();
		for (ContainerData cData : cName_cData.values()) {
			final Set<ResourceReference> consumedResources = 
				cData.getResourceReferences();
			if(consumedResources != null) {
				resRefs.addAll(consumedResources);
			}
		}
		return resRefs;
	}

	/**
	 * @return ordered array of file names for the application class loader. 
	 * The files are ordered by containers priority. At the end of the array
	 * are included files from the additional class path. The ordering is 
	 * important to avoid conflicts when a given class is defined twice.
	 */
	public String[] getApplicationLoaderFiles() {
		final Set<String> result = new LinkedHashSet<String>();

		for (final String containerName : orderContainersByCLPrio()) {
			final ContainerData cData = cName_cData.get(containerName);
			result.addAll(cData.getFilesForCL());
		}

		// Add additional classpath
		if (getAdditionalClasspath() != null) {
			final StringTokenizer tokenizer = 
				new StringTokenizer(getAdditionalClasspath(), ";");
			while (tokenizer.hasMoreTokens()) {
				result.add(tokenizer.nextToken());
			}
		}
		return result.toArray(new String[result.size()]);
	}

	/**
	 * @return ordered array of file names for the application heavy class 
	 * loader. The files are ordered by container's class loading priority.
	 */
	public String[] getHeavyApplicationLoaderFiles() {
		final List<String> result = new LinkedList<String>();
		final String[] containerNames = orderContainersByCLPrio();

		for (int i = 0; i < containerNames.length; i++) {
			final ContainerData cData = cName_cData.get(containerNames[i]);
			for (final String heavyFile : cData.getHeavyFilesForCL()) {
				result.add(heavyFile);
			}
		}
		return result.toArray(new String[result.size()]);
	}

	/**
	 * Order the container by the their class loading priority. Class files of
	 * the containers with lower priority have to be in the beginning of the 
	 * class path order.
	 * @return ordered array of container names. Containers with lower class
	 * loading priority are in the beginning or the array. Not null.
	 */
	private String[] orderContainersByCLPrio() {
		final String[] containerNames = new String[cName_cData.size()];
		cName_cData.keySet().toArray(containerNames);
		Arrays.sort(
			containerNames, ContainerNameComparatorByCLPrioReverted.instance);
		return containerNames;
	}

	private ReferenceObject[] addReference(final ReferenceObject refs[],
			ReferenceObject[] toRefs) {
		return DUtils.concatReferences(toRefs, refs);
	}

	/**
	 * @param refsForRemove references to be removed. Can be null or empty
	 * array.
	 * @param allRefs all references, from which we will remove.
	 * @return the remainder.
	 */
	@SuppressWarnings("unchecked")
	private ReferenceObject[] removeReferences(final String[] refsForRemove,
		final ReferenceObject[] allRefs) {
		if (allRefs == null || refsForRemove == null || refsForRemove.length == 0) {
			return allRefs;
		}
		final Set<ReferenceObject> remainder = 
			CAConvertor.cObject(allRefs);
		for(final String toRemove : refsForRemove) {
			for(Iterator<ReferenceObject> iter = remainder.iterator(); iter.hasNext();) {
				if (!iter.next().toString().equals(toRemove)) {
					iter.remove();
			        }
		        }
		}
		return remainder.toArray(new ReferenceObject[remainder.size()]);
	}

	/**
	 * @return the current version of the deployment info. Not null.
	 */
	public Version getVersion() {
		return version;
	}

	/**
	 * Set the current version of the deployment info. 
	 * @param version the current version of the deployment info. Not null.
	 */
	public void setVersion(final Version version) {
		assert version != null;
		this.version = version;
	}

	/**
	 * @return the application name object.
	 */
	public ApplicationName getApplicationNameO() {
		return applicationName;
	}

	/**
	 * @return the application name as string.
	 */
	public String getApplicationName() {
		return getApplicationNameO().getApplicationName();
	}

	/**
	 * @return <tt>true</tt> if the corresponding application is deployed as
	 * standalone module. 
	 */
	public boolean isStandAloneArchive() {
		return isStandAloneArchive;
	}

	/**
	 * Set the flag indicating that the corresponding application is deployed 
	 * as standalone module.
	 * @param isStandAloneArchive whether the application is deployed as
	 * standalone module.
	 */
	public void setStandAloneArchive(final boolean isStandAloneArchive) {
		this.isStandAloneArchive = isStandAloneArchive;
	}

	/**
	 * @return the current application status.
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Set new application status.
	 * @param newStatus the new application status.
	 * @param statDescId the status description.
	 * @param oParams parameters for status description.
	 */
	public void setStatus(final Status newStatus, 
		final StatusDescriptionsEnum statDescId, final Object[] oParams) {
		setStatus(newStatus, statDescId, oParams, true);
	}

	/**
	 * This method is called when we want Local status to be set but without
	 * reporting it in shared memory. No one except DIReader.readSerialized()
	 * method should call this method.
	 * @param newStatus the new application status.
	 * @param statDescId the status description.
	 * @param oParams parameters for status description.
	 */
	public void setStatusWithoutShm(final Status _status,
		final StatusDescriptionsEnum statDescId, final Object[] oParams) {
		setStatus(_status, statDescId, oParams, false);
	}

	private void setStatus(final Status _status, 
		final StatusDescriptionsEnum statDescId, final Object[] oParams, 
		final boolean isShm) {
		if (_status == null) {
			throw new IllegalArgumentException(
					"ASJ.dpl_ds.006042 The application status cannot be NULL.");
		}
		status = _status;
		if (isShm && isJ2EEApplication()) {
			ShmComponentUtils.setLocalStatus(status, applicationName
					.getApplicationName());
		}
		// set status description if explicitly provided
		setStatusDescription(statDescId, oParams);
	}

	/**
	 * Helper method which receives a single parameter for the status
	 * description.
	 * 
	 * @param _status new application status.
	 * @param _statDesc status description.
	 */
	public void setStatus(final Status _status, 
		final StatusDescription _statDesc) {
		setStatus(_status, _statDesc.getId(), _statDesc.getParams());
	}

	/**
	 * @return names of supported remote protocols. 
	 */
	public String[] getRemoteSupport() {
		return remoteSupport;
	}

	/**
	 * @return file system index used for synchronization of files with the DB.
	 * Can be null.
	 */
	public Index getIndexFS() {
		if (indexFS == null) {
			updateIndexFS();
		}
		return indexFS;
	}

	/**
	 * Set the file system index.
	 * @param indexFS file system index used for synchronization of files with 
	 * the DB. Can be null.
	 */
	public void setIndexFS(final Index indexFS) {
		this.indexFS = indexFS;
	}

	/**
	 * Update the file system index used for synchronization of files with the
	 * DB. 
	 */
	public void updateIndexFS() {
		final File version_bin = getVersionBin();
		Index indexFS = null;
		try {
			indexFS = (Index) EditorUtil.getDeserializedObject(version_bin);
		} catch (ServerDeploymentException sde) {
			DSLog.logErrorThrowable(location, "ASJ.dpl_ds.006377",
					"Exception in object deserializing", sde);
		}
		setIndexFS(indexFS);
	}

	/**
	 * @return the file with CRC codes of all application files. It is created
	 * during the bootstrap of application files.
	 */
	public File getVersionBin() {
		return new File(getThisAppWorkDir() + DIConsts2.version_bin);
	}

	/**
	 * Set the names of supported remote protocols.
	 * @param _remoteSupport
	 */
	public void setRemoteSupport(final String[] _remoteSupport) {
		remoteSupport = _remoteSupport;
		if (remoteSupport == null) {
			remoteSupport = new String[0];
		}
		for (int i = 0; i < remoteSupport.length; i++) {
			remoteSupport[i] = StringUtils.intern(remoteSupport[i]);
		}
	}

	/**
	 * Add pure reference (not based on resources) to given component from 
	 * the corresponding application. Self cycles are not allowed.
	 * @param ref reference to given component. Not null.
	 */
	public void addReference(final ReferenceObject ref) {
		assert ref != null;
		if (ref.getReferenceTargetType().equalsIgnoreCase(
			Component.Type.APPLICATION.name()) && 
			ref.getName().equals(getApplicationName())) {
			// no self cycles are allowed.
			return;
		}
		this.referencesTo = addReference(new ReferenceObject[] { ref },
				referencesTo);
	}

	/**
	 * Add array of pure references (not based on resources) to different 
	 * components from the corresponding application. Self cycles are not 
	 * allowed.
	 * @param refs array of pure references to be added.
	 */
	public void addReference(final ReferenceObject refs[]) {
		this.referencesTo = addReference(refs, referencesTo);
	}

	/**
	 * Return the pure references (not based on resources) to other components.
	 * @return the pure references to other components or <tt>null</tt> in case
	 * that the corresponding application does not refer any components.
	 */
	public ReferenceObject[] getReferences() {
		return this.referencesTo;
	}

	/**
	 * Removes pure references to other components.
	 * @param refs references to be removed. Not null.
	 */
	public void removeReferences(final String[] refs) {
		assert refs != null;
		this.referencesTo = removeReferences(refs, referencesTo);
	}

	/**
	 * Set the pure references to other components.
	 * @param references the pure references to other components. Can be null.
	 */
	public void setReferences(ReferenceObject[] references) {
		this.referencesTo = references;
	}

	/**
	 * @return the additional class path used by application class loader. Can
	 * be null.
	 */
	public String getAdditionalClasspath() {
		return additionalClasspath;
	}

	/**
	 * Set the additional class path used by application class loader.
	 * @param additionalClasspath the additional class path. Can be null.
	 */
	public void setAdditionalClasspath(String additionalClasspath) {
		this.additionalClasspath = additionalClasspath;
	}

	/**
	 * Set the deployment descriptor XML.
	 * @param dd deployment descriptor coming from META-INF/application.xml.
	 */
	public void setApplicationXML(String dd) {
		applicationXML = dd;
	}

	/** 
	 * Get the deployment descriptor XML as specified in 
	 * META-INF/application.xml.
	 * @return the deployment descriptor XML. Can be null.
	 */
	public String getApplicationXML() {
		return applicationXML;
	}

	/**
	 * Set the container info descriptor if this application is a container.
	 * @param cInfo container info XML. Not null.
	 */
	public void setContainerInfoXML(String cInfo) {
		assert cInfo != null;
		this.containerInfoXML = cInfo;
	}

	/**
	 * @return the container info descriptor if this application is a 
	 * container or <tt>null</tt> if not.
	 */
	public String getContainerInfoXML() {
		return containerInfoXML;
	}

	/**
	 * @return the custom properties for the corresponding application. Can be
	 * null.
	 */
	public Properties getProperties() {
		if (properties != null && properties.size() == 0) {
			setProperties(null);
		}
		return properties;
	}

	/**
	 * Mutator for the properties of the deployment info. Note! If the set
	 * properties contain a java version key, the value will be considered a
	 * custom java version (read from and stored in the configuration)
	 * 
	 * @param props custom properties.
	 */
	public void setProperties(Properties props) {
		// check for a stored java version in the old properties
		String sPreviousJavaVersion = null;
		if (this.properties != null && this.properties.size() > 0) {
			sPreviousJavaVersion = (String) this.properties
					.get(IOpConstants.JAVA_VERSION);
		}
		// assign new properties
		this.properties = props;
		// since 01.02.2006
		if (properties != null
				&& properties.get(IOpConstants.JAVA_VERSION) != null) {
			// if a version is stored in the new properties
			// but not in the member variable it is also
			// transferred to the member variable for consistency;
			// note that the value may be validated and altered during
			// the set operation - see AdditionalAppInfo#setJavaVersion(String);
			setJavaVersion((String) properties.get(IOpConstants.JAVA_VERSION),
					true);
		} else if (sPreviousJavaVersion != null) {
			// if new set properties are null or do not contain a java version
			// set the previous one (usually the default one)
			setJavaVersion(sPreviousJavaVersion, isCustomJavaVersion);
		} else {
			// precaution - the previous java version should be set
			// to default during construction; this check serves as a
			// precaution;
			// if no previous java version exists in properties
			// the current one should be used, which is
			// originally the default one
			setJavaVersion(sJavaVersion, isCustomJavaVersion);
		}
		this.properties = internProperties(this.properties);
	}

	private Properties internProperties(Properties properties) {
		if (properties == null) {
			return null;
		}

		final Properties resultProperties = new Properties();

		for (Iterator keys = properties.keySet().iterator(); keys.hasNext();) {
			Object key = keys.next();
			Object value = properties.get(key);
			if (key instanceof String) {
				key = StringUtils.intern((String) key);
			}
			if (value instanceof String) {
				value = StringUtils.intern((String) value);
			}
			resultProperties.put(key, value);
		}
		properties.clear();
		properties.putAll(resultProperties);
		return properties;
	}

	/**
	 * @return the work directory for the application.
	 */
	public String getThisAppWorkDir() {
		if (thisAppWorkDir == null) {
			setThisAppWorkDir();
		}
		return thisAppWorkDir;
	}

	private void setThisAppWorkDir() {
		final StringBuilder sb = new StringBuilder(PropManager.getInstance()
				.getAppsWorkDir()).append(
				getApplicationName().replace('/', File.separatorChar)).append(
				File.separatorChar);
		thisAppWorkDir = sb.toString();
		new File(thisAppWorkDir).mkdirs();
	}

	/**
	 * @return the information about the exception during the last operation 
	 * with the corresponding application.
	 */
	public ExceptionInfo getExceptionInfo() {
		if (statusDesc == null) {
			return null;
		}
		return statusDesc.getExceptionInfo();
	}

	/**
	 * Set the information about the exception during the last operation 
	 * with the corresponding application.
	 * @param info exception info.
	 */
	public void setExceptionInfo(ExceptionInfo info) {
		if (statusDesc == null) {
			statusDesc = new StatusDescription();
		}
		statusDesc.setExceptionInfo(info);
	}

	/**
	 * @return the map of container data. Can not be null. The keys are the
	 * container names and values - container data.
	 */
	public Hashtable<String, ContainerData> getCNameAndCData() {
		assert cName_cData != null;
		return cName_cData;
	}

	/**
	 * Set the map of container data.
	 * @param _cName_cData Map of container names to container data, containing
	 * all container data for the involved containers. Not null.
	 */
	public void setCNameAndCData(
		final Hashtable<String, ContainerData> _cName_cData) {
		assert _cName_cData != null;
		cName_cData = internHashtable(_cName_cData);
	}

	/**
	 * Check whether a given container is involved in the deployment of the
	 * corresponding application.
	 * @param containerName container name.
	 * @return
	 */
	public boolean isContainerData(final String containerName) {
		return (cName_cData.get(containerName) != null ? true : false);
	}

	/**
	 * Return the container data for the given container.
	 * @param contName the name of the container.
	 * @return the container data for the given container. Cannot be null.
	 */
	public ContainerData getOrCreateContainerData(final String contName) {
		ContainerData cData = cName_cData.get(contName);
		if (cData == null) {
			cData = new ContainerData(contName);
			cName_cData.put(StringUtils.intern(contName), cData);
		}
		return cData;
	}

	/**
	 * Remove the container data for the given container. This container will
	 * be no more involved in the deployment of the application. 
	 * @param containerName the name of the container. Not null.
	 */
	public void removeContainerData(final String containerName) {
		assert containerName != null;
		cName_cData.remove(containerName);
	}

	/**
	 * Set the container data of an involved container in the container 
	 * data map.
	 * @param containerData container data.
	 */
	public void setContainerData(final ContainerData containerData) {
		cName_cData.put(
			StringUtils.intern(containerData.getContName()), containerData);
	}

	/**
	 * Returns the corresponding software type of the application.
	 * @return the corresponding software type. Will be <tt>null</tt> for pure
	 * JavaEE applications.
	 */
	public String getSoftwareType() {
		if (properties != null) {
			return (String) properties.get(DeployService.softwareType);
		}
		return null;
	}

	/**
	 * Checks whether the corresponding application is JavaEE application.
	 * @return <tt>true</tt> if this is JavaEE application.
	 */
	public boolean isJ2EEApplication() {
		return (getSoftwareType() == null);
	}

	/**
	 * Check whether the application is initially started.
	 * @return
	 */
	// TODO; to be replaced by boolean flag.
	public InitiallyStarted getInitiallyStarted() {
		return initiallyStarted;
	}

	/**
	 * Set the flag indicating that the application is initially started.
	 * @param initiallyStarted
	 */
	// TODO; to be replaced by boolean flag.
	public void setInitiallyStarted(final InitiallyStarted initiallyStarted) {
		this.initiallyStarted = initiallyStarted;
	}

	/**
	 * @return the module provider.
	 */
	public ModuleProvider getModuleProvider() {
		return moduleProvider;
	}

	/**
	 * Set the module provider.
	 * @param moduleProvider
	 */
	public void setModuleProvider(ModuleProvider moduleProvider) {
		this.moduleProvider = moduleProvider;
	}

	/**
	 * Gets the <code>ConfigProvider</code> for this application. It contains
	 * information about the configurations used from this application and are
	 * for public use.
	 * 
	 * @return <code>ConfigProvider</code>.
	 */
	public ConfigProvider getConfigProvider() {
		return configProvider;
	}

	/**
	 * Checks if all containers for this application support lazy start.
	 * 
	 * @return true - lazy start is supported by all containers for this
	 * application, false - otherwise.
	 */
	public boolean isSupportingLazyStart() {
		return this.isSupportingLazy(false);
	}

	private boolean isSupportingLazyStartFromToString() {
		return this.isSupportingLazy(true);
	}

	private boolean isSupportingLazy(boolean isCalledFromToString) {
		if (!StartUp.LAZY.equals(getStartUpO())) {
			return false;
		}
		final String[] containerNames = getContainerNames();
		if (containerNames != null) {
			ContainerInterface cIntf = null;
			for (int i = 0; i < containerNames.length; i++) {
				cIntf = Containers.getInstance()
						.getContainer(containerNames[i]);
				if (isCalledFromToString && cIntf != null) {
					cIntf = ((ContainerWrapper) cIntf)
							.getRealContainerInterface();
				}
				if (cIntf == null
						|| !cIntf.getContainerInfo().isSupportingLazyStart()
						|| !cIntf.getContainerInfo().isSupportingLazyStart(
								getApplicationName())) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Method overrides the parent method. The java version should be put also
	 * in the deployment info properties. Consequently both methods - the
	 * properties and the java version mutators are affected. If the properties
	 * have been set prior to java version, the version should be explicitly
	 * stored there, too.
	 * 
	 * @see AdditionalAppinfo#setJavaVersion(String, boolean)
	 */
	@Override
	public int setJavaVersion(String sVersion, boolean isCustom) {
		int niResult = super.setJavaVersion(sVersion, isCustom);
		if (properties == null) {
			properties = new Properties();
		}
		properties.put(IOpConstants.JAVA_VERSION, sJavaVersion);
		return niResult;
	}

	/**
	 * @return description of the current application status.
	 */
	public StatusDescription getStatusDescription() {
		return this.statusDesc;
	}

	/**
	 * Sets the status description for the deployment info
	 * 
	 * @param statDesc
	 */
	public void setStatusDescription(StatusDescriptionsEnum id, Object[] oParams) {
		if (statusDesc == null) {
			statusDesc = new StatusDescription();
		}
		this.statusDesc.setStatusDescription(id, oParams);
	}

	private void setAdditionalAppInfo(AdditionalAppInfo addAppInfo) {
		setFailOver(addAppInfo.getFailOver());
		setJavaVersion(addAppInfo.getJavaVersion(), addAppInfo
				.isCustomJavaVersion());
		setStartUpO(addAppInfo.getStartUpO());
	}

	private AdditionalAppInfo getAdditionalAppInfo() {
		return this;
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		this.properties = internProperties(this.properties);
		this.setRemoteSupport(this.remoteSupport);

		// intern
		this.cName_cData = internHashtable(this.cName_cData);
	}

	private Hashtable<String, ContainerData> internHashtable(
			final Hashtable<String, ContainerData> hashtable) {

		if (hashtable == null) {
			return null;
		}

		final Hashtable<String, ContainerData> tempHashtable = new Hashtable<String, ContainerData>(
				hashtable.size());

		for (Iterator<String> keys = hashtable.keySet().iterator(); keys
				.hasNext();) {
			String key = StringUtils.intern(keys.next());
			tempHashtable.put(key, hashtable.get(key));
		}
		hashtable.clear();
		hashtable.putAll(tempHashtable);
		return hashtable;
	}
}
