/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.library_container.deploy;

import static com.sap.engine.services.accounting.Accounting.beginMeasure;
import static com.sap.engine.services.accounting.Accounting.endMeasure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.xml.sax.SAXException;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.load.LoadContext;
import com.sap.engine.interfaces.resourcecontext.ResourceContext;
import com.sap.engine.interfaces.resourcecontext.ResourceContextFactory;
import com.sap.engine.lib.io.FileUtils;
import com.sap.engine.lib.io.hash.CorruptedHashException;
import com.sap.engine.lib.io.hash.Entry;
import com.sap.engine.lib.io.hash.FolderCompareResult;
import com.sap.engine.lib.io.hash.HashUtils;
import com.sap.engine.lib.io.hash.Index;
import com.sap.engine.lib.io.hash.PathNotFoundException;
import com.sap.engine.lib.util.concurrent.CountDown;
import com.sap.engine.lib.util.iterators.ArrayEnumeration;
import com.sap.engine.lib.xml.StandardDOMParser;
import com.sap.engine.services.deploy.container.AdditionalAppInfo;
import com.sap.engine.services.deploy.container.ApplicationDeployInfo;
import com.sap.engine.services.deploy.container.ContainerDeploymentInfo;
import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.DeployCommunicator;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.FileUpdateInfo;
import com.sap.engine.services.deploy.container.ProgressListener;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.engine.services.deploy.ear.Module;
import com.sap.engine.services.deploy.ear.SimpleEarDescriptor;
import com.sap.engine.services.library_container.LCResourceAccessor;

/**
 * <p>
 * The Application Library Container manages the lifecycle of bundled libraries
 * (or application libraries), whose support is required by the JEE 5.0
 * specification. The bundled libraries are JAR format files, embodied in the
 * EAR file and which are available to the other application components. These
 * JAR files contain shared/utility classes used by the other application
 * components like EJBs, Web components, etc. If more application components use
 * the same classes, then they should be packed as internal application
 * libraries in JAR format files.
 * <p>
 * Those files then have to be packed in the application EAR file. The bundled
 * libraries also could contain initialization/destruction classes, which are in
 * fact hooks for start/stop application actions. It is an additional feature
 * for SAP applications which is not required by the JEE 5.0 specification. In
 * these classes the application developer could initialize/destroy used
 * resources out of the context of the standard containers – EJB, Web,
 * Connectors container.
 * 
 * @author Rumiana Angelova
 * @version 7.1
 */
public class LibraryContainer implements ContainerInterface {
	public static final String JAVA_EE_5 = "5";

	private static final String application_service_descriptor = "application-service.xml";
	private static final String META_INF = "META-INF";
	private static final String meta_inf = "meta-inf";
	private static final String[] requiredExts = new String[] { "jar", "war",
			"rar" };
	private static final String deployableExt = "jar";
	static final String CONTAINER_NAME = "app_libraries_container";
	private static String service_name = "n/a";
	private static final String MODULE_NAME = "app_library";

	// name of index file stored only for temporary usage during update
	private static final String BIN_FILE_NAME = "IndexFile";

	private static final String TEMP_DIR_NAME = "temp";
	private static final String LIB_FOLDER_URI = "lib/";

	private ContainerInfo info = null;
	private LoadContext lc = null;
	private ApplicationServiceContext asc = null;
	private DeployCommunicator communicator = null;
	private ResourceContextFactory resourceFactory = null;

	private LCConcurrentMapWrapper<String, ApplicationServiceDescriptor> app_descriptors = new LCConcurrentMapWrapper<String, ApplicationServiceDescriptor>();
	private LCConcurrentMapWrapper<String, ResourceContext> app_resourceContext = new LCConcurrentMapWrapper<String, ResourceContext>();
	private LCConcurrentMapWrapper<String, FolderCompareResult> compare_results_on_update = new LCConcurrentMapWrapper<String, FolderCompareResult>();
	private LCConcurrentMapWrapper<String, FilesForClassLoad> app_files_for_upload = new LCConcurrentMapWrapper<String, FilesForClassLoad>();

	private static boolean DEFINE_HEAVY_RESOURCES = false;
	private static final String DEFINE_HEAVY_RESOURCES_PROPERTY = "Define_Heavy_Resources";

	// for JUnitTest
	public LibraryContainer() {
	}

	public LibraryContainer(ApplicationServiceContext asc) {
		info = new ContainerInfo();
		info.setFileNames(new String[] { application_service_descriptor,
				META_INF + "/" + application_service_descriptor,
				meta_inf + "/" + application_service_descriptor });
		info.setFileExtensions(requiredExts);
		info.setJ2EEContainer(false);
		info.setModuleName(MODULE_NAME);
		info.setName(CONTAINER_NAME);
		info.setPriority(ContainerInfo.MAX_PRIORITY);
		info.setSupportingSingleFileUpdate(false);
		service_name = asc.getServiceState().getServiceName();
		info.setServiceName(service_name);
		// container is requested to be lazy
		info.setSupportingLazyStart(true);
		// container is required to be parallel
		info.setSupportingParallelism(true);
		lc = asc.getCoreContext().getLoadContext();
		this.asc = asc;

		String defineHeavy = asc.getServiceState().getProperty(
				DEFINE_HEAVY_RESOURCES_PROPERTY);
		if (defineHeavy != null) {
			DEFINE_HEAVY_RESOURCES = Boolean.parseBoolean(defineHeavy);
		}
	}

	public void setDeployCommunicator(DeployCommunicator communicator) {
		this.communicator = communicator;
	}

	public void setResourceContext(ResourceContextFactory resourceFactory) {
		this.resourceFactory = resourceFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.ContainerInterface#getContainerInfo
	 * ()
	 */
	public ContainerInfo getContainerInfo() {
		return this.info;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.deploy.container.ContainerInterface#
	 * getApplicationName(java.io.File)
	 */
	public String getApplicationName(File standaloneFile)
			throws DeploymentException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.ContainerInterface#deploy(java
	 * .io.File[],
	 * com.sap.engine.services.deploy.container.ContainerDeploymentInfo,
	 * java.util.Properties)
	 */
	public ApplicationDeployInfo deploy(File[] archiveFiles,
			ContainerDeploymentInfo dInfo, Properties props)
			throws DeploymentException {
		try {
			if (dInfo.isStandAloneArchive()) {
				return null;
			}
			String containerPath = communicator.getMyWorkDirectory(dInfo
					.getApplicationName());
			File containerDir = new File(containerPath);
			FilesForClassLoad allFiles = this.getDeployableFiles(archiveFiles,
					dInfo);
			if (allFiles.size() > 0) {
				Configuration appConf = dInfo.getConfiguration();
				ApplicationDeployInfo deployInfo = new ApplicationDeployInfo();
				Configuration conf = getOrCreateContSubConfiguration(appConf);
				this.uploadFiles(allFiles.getAll(), dInfo.getFileMappings(),
						conf);
				fillApplicationDeployInfo(allFiles, containerDir, dInfo,
						deployInfo);
				return deployInfo;
			}
		} catch (ConfigurationException cex) {
			throw new LCDeploymentException(
					DeploymentExceptionConstants.DEPLOY_ERROR_CFG,
					new String[] { dInfo.getApplicationName() }, cex);
		} catch (IOException ioex) {
			throw new LCDeploymentException(
					DeploymentExceptionConstants.DEPLOY_ERROR_IO,
					new String[] { dInfo.getApplicationName() }, ioex);
		}
		return null;
	}

	private File[] getFilesInContainerDir(List<File> archiveFiles,
			String containerDir, Hashtable mappings) {
		if (archiveFiles.size() > 0) {
			File[] result = new File[archiveFiles.size()];
			String mapping = null;
			int i = 0;
			for (File file : archiveFiles) {
				mapping = (String) mappings.get(file.getAbsolutePath());
				if (mapping != null) {
					result[i++] = new File(containerDir + File.separator
							+ mapping.replace('/', File.separatorChar));
				}
			}
			return result;
		}
		return new File[0];
	}

	private void fillApplicationDeployInfo(FilesForClassLoad allFiles,
			File containerDir, ContainerDeploymentInfo dInfo,
			ApplicationDeployInfo deployInfo) throws ConfigurationException {
		LinkedHashSet<String> components = new LinkedHashSet<String>();
		ArrayList<String> load_list = new ArrayList<String>();
		File[] filesForClassLoad = this.getFilesInContainerDir(allFiles
				.getFilesForClassLoad(), containerDir.getPath(), dInfo
				.getFileMappings());
		filter(components, load_list, filesForClassLoad);
		if (load_list.size() > 0) {
			String[] filesForClassloader = new String[load_list.size()];
			load_list.toArray(filesForClassloader);
			deployInfo.setFilesForClassloader(filesForClassloader);
		}
		File[] heavyFilesForClassLoad = this.getFilesInContainerDir(allFiles
				.getHeavyFilesForClassLoad(), containerDir.getPath(), dInfo
				.getFileMappings());
		load_list.clear();
		filter(components, load_list, heavyFilesForClassLoad);
		if (load_list.size() > 0) {
			String[] heavyFilesForClassloader = new String[load_list.size()];
			load_list.toArray(heavyFilesForClassloader);
			if (DEFINE_HEAVY_RESOURCES) {
				deployInfo
						.setHeavyFilesForClassloader(heavyFilesForClassloader);
			} else {
				deployInfo.setFilesForClassloader(heavyFilesForClassloader);
			}
		}
		String[] comp = new String[components.size()];
		components.toArray(comp);
		deployInfo.setDeployedComponentNames(comp);
	}

	private void filter(LinkedHashSet<String> components,
			ArrayList<String> load_list, File[] filesForClassLoad) {
		for (int i = 0; i < filesForClassLoad.length; i++) {
			if (!filesForClassLoad[i].getName().equals(BIN_FILE_NAME)) {// workaround
				// to
				// remove
				// the
				// configuration
				// mgr
				// index
				// file
				// -
				// former
				// bug.
				components.add(filesForClassLoad[i].getName());
				if (filesForClassLoad[i].getName().endsWith(deployableExt)) {
					load_list.add(filesForClassLoad[i].getPath());
				}
			}
		}
	}

	private FilesForClassLoad getDeployableFiles(File[] files,
			ContainerDeploymentInfo dInfo) throws DeploymentException {
		FilesForClassLoad fileForClassLoad = new FilesForClassLoad();
		if (files == null || files.length == 0) {
			return fileForClassLoad;
		}

		Hashtable fileMappings = dInfo.getFileMappings();
		String applicationName = dInfo.getApplicationName();
		// The library-directory element of the .ear file’s deployment
		// descriptor
		// contains
		// the name of the directory with libraries packed as jar files. If a
		// library-directory
		// element isn’t specified, or if the .ear file does not contain a
		// deployment descriptor,
		// the directory named lib is used. An empty library-directory element
		// may
		// be used to
		// specify that there is no library directory.
		String sLibraryDirectory = dInfo.getEarDescriptor()
				.getLibraryDirectory();
		boolean isJEE5 = JAVA_EE_5.equals(dInfo.getEarDescriptor()
				.getApplicationJ2EEVersion());

		loadASDecriptorFile(files, applicationName);
		final ApplicationServiceDescriptor applicationServiceDescriptor = getASDescriptor(applicationName);
		final boolean isHeavyClassloadingBeforeJavaEE5Enabled = (applicationServiceDescriptor != null && applicationServiceDescriptor
				.isHeavyClassloadingBeforeJavaEE5Enabled());

		if (isJEE5 || isHeavyClassloadingBeforeJavaEE5Enabled) {
			if (sLibraryDirectory != null && sLibraryDirectory.length() > 0) {
				this.addLibsFromFolder(sLibraryDirectory, files, fileMappings,
						fileForClassLoad.getHeavyFilesForClassLoad());
			} else if (sLibraryDirectory == null) {
				this.addLibsFromFolder(LIB_FOLDER_URI, files, fileMappings,
						fileForClassLoad.getHeavyFilesForClassLoad());
			}
		}
		this.addLibsFromClassPathElements(files, dInfo, fileForClassLoad
				.getFilesForClassLoad());
		this.addLibsSAPSpecific(files, fileForClassLoad.getFilesForClassLoad(),
				dInfo, isJEE5);

		return fileForClassLoad;
	}

	private void addLibsSAPSpecific(File[] files, ArrayList result,
			ContainerDeploymentInfo dInfo, boolean ignoreLibFolder) {
		SimpleEarDescriptor applicationDescriptor = dInfo.getEarDescriptor();
		Set modules = applicationDescriptor.getJ2EEModules();
		Hashtable fileMappings = dInfo.getFileMappings();

		boolean jarIsJ2EEModule = false;
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().equalsIgnoreCase(application_service_descriptor)) {
				if (!result.contains(files[i])) {
					result.add(files[i]);
				}
			} else if (files[i].getName().endsWith(deployableExt)) {
				if (modules != null) {
					jarIsJ2EEModule = false;
					for (Iterator iter = modules.iterator(); iter.hasNext();) {
						Module module = (Module) iter.next();
						if (files[i].getName()
								.equalsIgnoreCase(module.getUri())) {
							jarIsJ2EEModule = true;
							break;
						}
					}
					if (!jarIsJ2EEModule) {
						String mapping = (String) fileMappings.get(files[i]
								.getAbsolutePath());
						// ignore files contained in the lib folder if it
						// is explicitly excluded through the library-directory
						// element in the deployment descriptor
						if (ignoreLibFolder && mapping != null
								&& mapping.startsWith(LIB_FOLDER_URI)) {
							continue;
						}
						if (!result.contains(files[i])) {
							result.add(files[i]);
						}
					}
				}
			}
		}
	}

	private void addLibsFromFolder(String sFolder, File[] files,
			Hashtable fileMappings, ArrayList resultFiles) {
		// check passed folder path
		if (sFolder == null || sFolder.length() == 0) {
			return;
		}
		String mapping = null;
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().endsWith(deployableExt)) {
				mapping = (String) fileMappings.get(files[i].getAbsolutePath());
				if (mapping.startsWith(sFolder)) {
					if (!resultFiles.contains(files[i])) {
						resultFiles.add(files[i]);
					}
				}
			}
		}
	}

	private void addLibsFromClassPathElements(File[] files,
			ContainerDeploymentInfo dInfo, ArrayList resultFiles)
			throws DeploymentException {
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				for (int j = 0; j < requiredExts.length; j++) {
					if (files[i].getName().endsWith("." + requiredExts[j])) {
						addLibsFromClassPathElement(files[i], files, dInfo,
								resultFiles);
						break;
					}
				}
			}
		}
	}

	// TODO: directory support is also obligatory in JEE 5.0 specification, part
	// 8.2.1. But currently it's not possible due to
	// lack of info in the fileMappings table. To be discussed with the
	// deployment team
	private void addLibsFromClassPathElement(File file, File[] allFiles,
			ContainerDeploymentInfo dInfo, ArrayList resultFiles)
			throws DeploymentException {
		JarFile jar = null;
		try {
			jar = new JarFile(file);
		} catch (IOException ioex) {
			// not a jar format
			return;
		}
		try {
			Manifest manifest = jar.getManifest();
			if (manifest != null) {
				Attributes manifestAttributes = manifest.getMainAttributes();
				String classPath = manifestAttributes
						.getValue(Attributes.Name.CLASS_PATH);
				if (classPath != null) {
					StringTokenizer tokenizer = new StringTokenizer(classPath,
							" ");
					String token = null;
					File bundledLib = null;
					Hashtable fileMappings = dInfo.getFileMappings();
					String applicationName = dInfo.getApplicationName();
					while (tokenizer.hasMoreElements()) {
						token = tokenizer.nextToken();
						bundledLib = this.getFileWithEntryName(token, file,
								fileMappings, allFiles);
						if (bundledLib != null) {
							if (!resultFiles.contains(bundledLib)) {
								resultFiles.add(bundledLib);
							}
						} else {
							ApplicationServiceDescriptor asDesriptor = getASDescriptor(applicationName);
							if (asDesriptor == null
									|| asDesriptor
											.canIgnoreManifestClasspathError() == false) {
								// deployment will fail so remove the AS
								// descriptor which is no longer necessary
								if (asDesriptor != null) {
									removeASDescriptor(applicationName);
								}
								// throw exception according to the JEE 5.0
								// specification
								throw new LCDeploymentException(
										DeploymentExceptionConstants.MANIFEST_RESOLVE,
										new String[] {
												token,
												file.getAbsolutePath() + "/"
														+ JarFile.MANIFEST_NAME });
							} else {
								if (LCResourceAccessor.isInfoTraceable()) {
									LCResourceAccessor
											.traceInfo(
													"ASJ.dpl_lbc.000011",
													"Bundled library with name {0}, specified in the manifest 'classpath' attribute in file {1}, cannot be found in the application bundle. This error will be ignored by request.",
													token, file);
								}
							}
						}
					}
				}
			}
		} catch (IOException ioex) {
			throw new LCDeploymentException(
					DeploymentExceptionConstants.MANIFEST_ERROR,
					new String[] { file.getAbsolutePath() }, ioex);
		} finally {
			try {
				jar.close();
			} catch (IOException ioex) {
				throw new LCDeploymentException(
						DeploymentExceptionConstants.MANIFEST_ERROR,
						new String[] { file.getAbsolutePath() }, ioex);
			}
		}
	}

	private void loadASDecriptorFile(File[] extractedFiles,
			String applicationName) throws DeploymentException {
		for (int i = 0; i < extractedFiles.length; i++) {
			if (extractedFiles[i].getName().equals(application_service_descriptor)) {
				updateASDescriptor(applicationName, extractedFiles[i]);
				break;
			}
		}
	}

	private File getFileWithEntryName(String entryName, File referencingFile,
			Hashtable fileMappings, File[] files) {
		String referencingMapping = (String) fileMappings.get(referencingFile
				.getAbsolutePath());
		String prefix = referencingMapping.substring(0, referencingMapping
				.lastIndexOf(referencingFile.getName()));
		entryName = prefix + entryName;
		String mapping = null;
		for (int i = 0; i < files.length; i++) {
			mapping = (String) fileMappings.get(files[i].getAbsolutePath());
			if (entryName.equalsIgnoreCase(mapping)) {
				return files[i];
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.deploy.container.ContainerInterface#
	 * notifyDeployedComponents(java.lang.String, java.util.Properties)
	 */
	public void notifyDeployedComponents(String applicationName,
			Properties props) throws WarningException {
		removeASDescriptor(applicationName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.ContainerInterface#prepareDeploy
	 * (java.lang.String, com.sap.engine.frame.core.configuration.Configuration)
	 */
	public void prepareDeploy(String applicationName, Configuration appConfig)
			throws DeploymentException, WarningException {
		// nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.ContainerInterface#commitDeploy
	 * (java.lang.String)
	 */
	public void commitDeploy(String applicationName) throws WarningException {
		removeASDescriptor(applicationName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.ContainerInterface#rollbackDeploy
	 * (java.lang.String)
	 */
	public void rollbackDeploy(String applicationName) throws WarningException {
		// nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.ContainerInterface#needUpdate
	 * (java.io.File[],
	 * com.sap.engine.services.deploy.container.ContainerDeploymentInfo,
	 * java.util.Properties)
	 */
	public boolean needUpdate(File[] archiveFiles,
			ContainerDeploymentInfo dInfo, Properties props)
			throws DeploymentException, WarningException {

		if (dInfo.isStandAloneArchive()) {
			return false;
		}

		FilesForClassLoad filesForClassLoad = this.getDeployableFiles(
				archiveFiles, dInfo); // never null
		archiveFiles = filesForClassLoad.getAll();

		if (archiveFiles.length == 0) {
			final Configuration appConfiguration = dInfo.getConfiguration();
			try {
				if (appConfiguration.existsSubConfiguration(CONTAINER_NAME)) {
					// can happen only if there are no files for
					// container, but it participated in last
					// successful deploy or update and now is supposed to clear
					// its persistent and runtime data in 'makeUpdate'
					return true;
				}
			} catch (ConfigurationException e) {
				throw new LCDeploymentException(
						DeploymentExceptionConstants.UPDATE_ERROR_CFG,
						new String[] { dInfo.getApplicationName() }, e);
			}
			// can happen because container subscribes for widely used
			// extensions
			// but none of the files may be an application library - in
			// this case, the container did not participate
			// in the initial deploy
			// and has no configuration stored for the component
			return false;
		}

		final String applicationName = dInfo.getApplicationName();
		final Hashtable fileMappings = dInfo.getFileMappings();

		final Index containerIndex = getLibContCfgIndex(dInfo
				.getConfiguration());

		if (containerIndex != null) {

			final String createIndexTag = "create index for application "
					+ applicationName;
			final Index newFilesIndex;
			beginMeasure(createIndexTag, HashUtils.class);
			try {
				newFilesIndex = HashUtils.createIndex(CONTAINER_NAME);
			} finally {
				endMeasure(createIndexTag);
			}

			for (File archiveFile : archiveFiles) {
				Entry archiveFileEntryFS4Update = dInfo
						.getEntryFS4Update(archiveFile);
				if (archiveFileEntryFS4Update == null) {
					// with this exact implementation this must
					// never happen
					throw new IllegalStateException(
							"Please report an issue in BC-JAS-DPL, because there is no Entry for "
									+ archiveFile
									+ " in dInfo.getEntryFS4Update(archiveFile)"
									+ " and describe us how to reproduce it.");
				} else {
					String fileMapping = (String) fileMappings.get(archiveFile
							.getAbsolutePath());
					int separatorPosition = fileMapping.lastIndexOf("/");
					try {
						final String addFileToIndexTag = "add file "
								+ archiveFile.getName()
								+ " to new files index for application "
								+ applicationName;
						if (separatorPosition > -1) {
							fileMapping = fileMapping.substring(0,
									separatorPosition);
							beginMeasure(addFileToIndexTag, newFilesIndex
									.getClass());
							try {
								newFilesIndex.addFileTo(fileMapping,
										archiveFile.getName(),
										archiveFileEntryFS4Update.getHash(),
										true, true);
							} finally {
								endMeasure(addFileToIndexTag);
							}
						} else {
							beginMeasure(addFileToIndexTag, newFilesIndex
									.getClass());
							try {
								newFilesIndex.addFile(archiveFile.getName(),
										archiveFileEntryFS4Update.getHash(),
										true);
							} finally {
								endMeasure(addFileToIndexTag);
							}
						}
					} catch (CorruptedHashException e) {
						// cannot compare with current container configuration
						// so upload all received files
						app_files_for_upload.put(applicationName,
								filesForClassLoad);
						return true;
					} catch (PathNotFoundException e) {
						// cannot compare with current container configuration
						// so upload all received files
						app_files_for_upload.put(applicationName,
								filesForClassLoad);
						return true;
					} catch (IOException e) {
						// cannot compare with current container configuration
						// so upload all received files
						app_files_for_upload.put(applicationName,
								filesForClassLoad);
						return true;
					}
				}
			}

			final String compareResultTag = "compare new and old files indices before update of application "
					+ applicationName + " using HashUtuls";
			final FolderCompareResult compareResult;
			beginMeasure(compareResultTag, containerIndex.getClass());
			try {
				compareResult = containerIndex.compare(newFilesIndex, true);
			} finally {
				endMeasure(compareResultTag);
			}
			if (compareResult.isChanged()) {
				compare_results_on_update.put(applicationName, compareResult);
				app_files_for_upload.put(applicationName, filesForClassLoad);
				return true;
			}
			return false;
		} else {
			// cannot compare with current container configuration
			// so upload all received files
			app_files_for_upload.put(applicationName, filesForClassLoad);
			return true;
		}
	}

	// can return null
	private Index getLibContCfgIndex(Configuration appCfg) {
		final String getIndexTag = "get configuration index tag for "
				+ appCfg.getPath();
		beginMeasure(getIndexTag, Configuration.class);
		try {
			// new - index on application level
			final Index appIndex = appCfg.getIndex();
			if (appIndex != null) {
				return appIndex.getFolder(CONTAINER_NAME);
			} else {
				return null;
			}
		} catch (ConfigurationException e1) {
			return null;
		} catch (PathNotFoundException e2) {
			return null;
		} finally {
			endMeasure(getIndexTag);
		}
	}

	/*
	 * private Index getLibContCfgIndex(Configuration conf) throws
	 * ConfigurationException { try { return conf.getIndex();// old - index on
	 * container level } catch (ConfigurationException e2) { return
	 * getDummyIndex(); } }
	 */

	private Index getDummyIndex() {
		return HashUtils.createIndex("dummy");// fake - no index at all
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.ContainerInterface#needStopOnUpdate
	 * (java.io.File[],
	 * com.sap.engine.services.deploy.container.ContainerDeploymentInfo,
	 * java.util.Properties)
	 */
	public boolean needStopOnUpdate(File[] archiveFiles,
			ContainerDeploymentInfo dInfo, Properties props)
			throws DeploymentException, WarningException {
		// nothing to do
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.ContainerInterface#makeUpdate
	 * (java.io.File[],
	 * com.sap.engine.services.deploy.container.ContainerDeploymentInfo,
	 * java.util.Properties)
	 */
	public ApplicationDeployInfo makeUpdate(File[] archiveFiles,
			ContainerDeploymentInfo dInfo, Properties props)
			throws DeploymentException {
		ApplicationDeployInfo result = new ApplicationDeployInfo();

		final String applicationName = dInfo.getApplicationName();
		final Configuration appConfiguration = dInfo.getConfiguration();
		FilesForClassLoad filesForClassLoad = app_files_for_upload
				.get(applicationName);

		try {
			if (filesForClassLoad == null) {
				// can happen only if there are no files for the container
				Configuration libConf = appConfiguration
						.getSubConfiguration(CONTAINER_NAME);
				if (LCResourceAccessor.isDebugTraceable()) {
					LCResourceAccessor
							.traceDebug(
									"Container configuration [{0}] will be deleted because no bundled libraries were found "
											+ "in the new version of application [{1}] during update.",
									libConf.getPath(), dInfo
											.getApplicationName());
				}
				final String deleteConfigurationTag = "delete container configuration during update of application "
						+ applicationName;
				beginMeasure(deleteConfigurationTag, Configuration.class);
				try {
					libConf.deleteConfiguration();
				} finally {
					endMeasure(deleteConfigurationTag);
				}
				return result;
			}

			archiveFiles = filesForClassLoad.getAll();
			final Hashtable mappings = dInfo.getFileMappings();
			FolderCompareResult compareResult = compare_results_on_update
					.get(applicationName);
			if (compareResult != null) {
				uploadChangesOnUpdateToConfiguration(archiveFiles,
						applicationName, mappings, appConfiguration,
						compareResult);
			} else {
				// upload all files intended for this container;
				// first, delete the old configuration
				final String deleteConfigurationTag = "delete container configuration during update of application "
						+ applicationName;
				beginMeasure(deleteConfigurationTag, Configuration.class);
				try {
					if (appConfiguration.existsSubConfiguration(CONTAINER_NAME)) {
						if (LCResourceAccessor.isDebugTraceable()) {
							LCResourceAccessor
									.traceDebug(
											"Container configuration [{0}] will be deleted before uploading the new files during update.",
											appConfiguration
													.getSubConfiguration(
															CONTAINER_NAME)
													.getPath());
						}
						appConfiguration.deleteConfiguration(CONTAINER_NAME);
					}
				} finally {
					endMeasure(deleteConfigurationTag);
				}

				// second, upload all new files
				Configuration libConf = getOrCreateContSubConfiguration(appConfiguration);
				uploadFiles(archiveFiles, mappings, libConf);
			}

			String containerPath;
			try {
				containerPath = communicator
						.getMyWorkDirectory(applicationName);
			} catch (IOException e) {
				throw new LCDeploymentException(
						DeploymentExceptionConstants.UPDATE_ERROR_IO,
						new String[] { applicationName }, e);
			}
			final File containerDir = new File(containerPath);
			fillApplicationDeployInfo(
					app_files_for_upload.get(applicationName), containerDir,
					dInfo, result);
		} catch (ConfigurationException e) {
			throw new LCDeploymentException(
					DeploymentExceptionConstants.UPDATE_ERROR_CFG,
					new String[] { applicationName }, e);
		} finally {
			// delete update info for the current application
			app_files_for_upload.remove(applicationName);
			compare_results_on_update.remove(applicationName);
		}

		return result;
	}

	private void uploadChangesOnUpdateToConfiguration(File[] archiveFiles,
			final String applicationName, final Hashtable mappings,
			final Configuration appConfiguration,
			FolderCompareResult compareResult) throws LCDeploymentException {

		ArrayEnumeration filePathsForDownload = compareResult
				.getFilesForDownload();

		if (filePathsForDownload != null && filePathsForDownload.size() > 0) {

			Map<String, File> filesForDownload = new HashMap<String, File>(
					filePathsForDownload.size());

			while (filePathsForDownload.hasNext()) {
				String filePath = (String) filePathsForDownload.next();
				filePath = filePath.replace('\\', '/');
				if (filePath.startsWith("/")) {
					filePath = filePath.substring(1, filePath.length());
				}

				boolean mappingFound = false;
				for (File archiveFile : archiveFiles) {
					if (filePath.equals(mappings.get(archiveFile
							.getAbsolutePath()))) {
						mappingFound = true;
						filesForDownload.put(filePath, archiveFile);
						break;
					}
				}

				if (mappingFound == false) {
					throw new LCDeploymentException(
							DeploymentExceptionConstants.UPDATE_ERROR_MAPPING,
							new String[] { applicationName, filePath });
				}

				try {
					Configuration libConf = appConfiguration
							.getSubConfiguration(CONTAINER_NAME);
					for (String mapping : filesForDownload.keySet()) {
						uploadFile(filesForDownload.get(mapping), mapping
								.replace('\\', '/'), libConf);
						if (LCResourceAccessor.isDebugTraceable()) {
							LCResourceAccessor
									.traceDebug(
											"File [{0} was uploaded to configuration [{1}]",
											mapping, libConf.getPath());
						}
					}

				} catch (ConfigurationException e) {
					throw new LCDeploymentException(
							DeploymentExceptionConstants.UPDATE_ERROR_CFG,
							new String[] { applicationName }, e);
				}
			}
		}

		ArrayEnumeration filePathsForRemoval = compareResult
				.getDeletedEntries();
		Configuration libConf;
		try {
			libConf = appConfiguration.getSubConfiguration(CONTAINER_NAME);
			while (filePathsForRemoval.hasNext()) {
				String filePath = (String) filePathsForRemoval.next();
				deleteMappingInConfiguration(libConf, filePath.replace('\\',
						'/'));
				if (LCResourceAccessor.isDebugTraceable()) {
					LCResourceAccessor.traceDebug(
							"Deleted mapping [{0}] in configuration [{1}].",
							filePath, libConf.getPath());
				}
			}
		} catch (ConfigurationException e) {
			throw new LCDeploymentException(
					DeploymentExceptionConstants.UPDATE_ERROR_CFG,
					new String[] { applicationName }, e);
		}
	}

	private Configuration getOrCreateContSubConfiguration(Configuration appConf)
			throws ConfigurationException {
		final String getOrCreateContSubConfigurationTag = "get or create container subconfiguration under "
				+ appConf.getPath();
		beginMeasure(getOrCreateContSubConfigurationTag, Configuration.class);
		try {
			if (appConf.existsSubConfiguration(CONTAINER_NAME)) {
				return appConf.getSubConfiguration(CONTAINER_NAME);
			}

			try {
				return appConf.createSubConfiguration(CONTAINER_NAME,
						Configuration.CONFIG_TYPE_INDEXED);// TODO - old - if
				// apps/<vendor>/<name>
				// is not indexed
			} catch (ConfigurationException ce) {
				return appConf.createSubConfiguration(CONTAINER_NAME);// new -
				// if
				// apps
				// /<vendor
				// >/<name>
				// is
				// indexed
			}
		} finally {
			endMeasure(getOrCreateContSubConfigurationTag);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.deploy.container.ContainerInterface#
	 * notifyUpdatedComponents(java.lang.String,
	 * com.sap.engine.frame.core.configuration.Configuration,
	 * java.util.Properties)
	 */
	public void notifyUpdatedComponents(String applicationName,
			Configuration applicationConfig, Properties props)
			throws WarningException {
		removeASDescriptor(applicationName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.ContainerInterface#prepareUpdate
	 * (java.lang.String)
	 */
	public void prepareUpdate(String applicationName)
			throws DeploymentException, WarningException {
		// nothing to do
	}

	private void deleteTempDir(String applicationName) {
		try {
			String containerPath = communicator
					.getMyWorkDirectory(applicationName);
			File tempDir = new File(containerPath + File.separator
					+ TEMP_DIR_NAME);
			final String deleteTempDirTag = "delete temporary folder for application "
					+ applicationName;
			beginMeasure(deleteTempDirTag, FileUtils.class);
			try {
				FileUtils.deleteDirectory(tempDir);
			} finally {
				endMeasure(deleteTempDirTag);
			}
		} catch (IOException ioex) {
			LCResourceAccessor
					.traceException(
							"ASJ.dpl_lbc.000002",
							"[ERROR CODE DPL.LIB.100] Error while obtaining the container work directory of application [{0}]",
							ioex, applicationName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.ContainerInterface#commitUpdate
	 * (java.lang.String)
	 */
	public ApplicationDeployInfo commitUpdate(String applicationName)
			throws WarningException {
		removeASDescriptor(applicationName);
		deleteTempDir(applicationName);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.ContainerInterface#rollbackUpdate
	 * (java.lang.String, com.sap.engine.frame.core.configuration.Configuration,
	 * java.util.Properties)
	 */
	public void rollbackUpdate(String applicationName,
			Configuration applicationConfig, Properties props)
			throws WarningException {
		deleteTempDir(applicationName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.ContainerInterface#remove(java
	 * .lang.String)
	 */
	public void remove(String applicationName) throws DeploymentException,
			WarningException {
		removeASDescriptor(applicationName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.deploy.container.ContainerInterface#
	 * downloadApplicationFiles(java.lang.String,
	 * com.sap.engine.frame.core.configuration.Configuration)
	 */
	public void downloadApplicationFiles(String applicationName,
			Configuration applicationConfig) throws DeploymentException,
			WarningException {
		try {
			String containerPath = communicator
					.getMyWorkDirectory(applicationName);
			File containerFolder = new File(containerPath);
			File tempDir = new File(containerFolder, TEMP_DIR_NAME);
			if (tempDir.exists()) {
				FileUtils.deleteDirectory(tempDir);
			}

			Configuration conf = null;
			if (applicationConfig.existsSubConfiguration(CONTAINER_NAME)) {
				conf = applicationConfig.getSubConfiguration(CONTAINER_NAME);
			}

			// Index (on application level)
			Index appIndexDB = null;
			try {
				appIndexDB = applicationConfig.getIndex();
			} catch (ConfigurationException e) {
				// leave application index as null to trigger download of all
				// files
			}

			if (appIndexDB != null) {
				final Index appIndexFS = communicator
						.getIndexFS(applicationName);
				if (appIndexFS != null) {
					// Index (on container level) - depends where each
					// container stores application binaries
					final Index contIndexDB = appIndexDB
							.getFolder(CONTAINER_NAME);
					if (contIndexDB != null) {
						Index contIndexFS = appIndexFS
								.getFolder(CONTAINER_NAME);
						if (contIndexFS == null) {
							contIndexFS = getDummyIndex();
						}
						// conf is surely not null because it is indexed
						synchronizeFiles(containerFolder, conf, contIndexFS,
								contIndexDB);
					} else {// contIndexDB is null
						// download all
						downloadAllFiles(conf, containerFolder);
					}
				} else {// appIndexFS is null
					// download all
					downloadAllFiles(conf, containerFolder);
				}
			} else {// appIndexDB is null
				// download all
				downloadAllFiles(conf, containerFolder);
			}
		} catch (IOException ioex) {
			throw new LCDeploymentException(
					DeploymentExceptionConstants.START_ERROR_IO,
					new String[] { applicationName }, ioex);
		} catch (ConfigurationException cex) {
			throw new LCDeploymentException(
					DeploymentExceptionConstants.START_ERROR_CFG,
					new String[] { applicationName }, cex);
		} catch (PathNotFoundException pnfe) {
			throw new LCDeploymentException(
					DeploymentExceptionConstants.START_ERROR_CFG,
					new String[] { applicationName }, pnfe);
		}
	}

	private void downloadAllFiles(Configuration conf, File containerFolder)
			throws IOException, ConfigurationException {
		if (LCResourceAccessor.isDebugTraceable()) {
			LCResourceAccessor
					.traceDebug(
							"Download of all files from configuration [{0}] to container folder was triggered.",
							conf.getPath(), containerFolder.getAbsolutePath());
		}

		// FS clean-up
		Set<String> notDeletedFiles = deleteContainerFolderContents(containerFolder);
		if (notDeletedFiles != null) {
			if (conf != null) {
				throw new IOException(
						"Container folder "
								+ containerFolder.getAbsolutePath()
								+ " was not cleared successfully before downloading all files from configuration "
								+ conf.getPath()
								+ ". The following files remain in the container folder - "
								+ notDeletedFiles);
			} else {
				// trace a message with severity INFO that deletion was
				// not successful
				StringBuilder msg = new StringBuilder(
						"Container ["
								+ CONTAINER_NAME
								+ "]  was not able to delete its older libraries in folder ["
								+ containerFolder.getAbsolutePath()
								+ "] because of opened file handlers.\r\n");
				File[] leftFiles = containerFolder.listFiles();
				if (leftFiles != null && leftFiles.length > 0) {
					msg.append("Files left on system are as follows:\r\n");
					for (File file : leftFiles) {
						msg.append(file.getAbsoluteFile()).append("\r\n");
					}
				}
				msg
						.append("This is not expected to be issue for the successful update and synchronization of application files.");
				if (LCResourceAccessor.isInfoTraceable()) {
					LCResourceAccessor.traceInfo("ASJ.dpl_lbc.000001", "{0}",
							msg.toString());
				}
			}
		}
		if (LCResourceAccessor.isDebugTraceable()) {
			LCResourceAccessor
					.traceDebug(
							"All files from container folder [{0}] were removed successfully.",
							containerFolder.getAbsolutePath());
		}

		// download from configuration
		downloadAllFilesByCfgLevel(conf, containerFolder);
	}

	private void downloadAllFilesByCfgLevel(Configuration cfgLevel,
			File containerFolder) throws ConfigurationException, IOException {
		if (cfgLevel != null) {
			// download files from current level
			Map<String, InputStream> filesOnCfgLevel;
			final String getAllFilesFromCfg = "get all file entries from configuration "
					+ cfgLevel.getPath();
			beginMeasure(getAllFilesFromCfg, Configuration.class);
			try {
				filesOnCfgLevel = cfgLevel.getAllFileEntries();
			} finally {
				endMeasure(getAllFilesFromCfg);
			}
			if (filesOnCfgLevel != null) {
				for (String fileName : filesOnCfgLevel.keySet()) {
					File file = new File(containerFolder, fileName);
					saveFile(filesOnCfgLevel.get(fileName), file);
				}

				// proceed with the next sublevels
				final String getAllSubConfigurationsFromCfg = "get all subconfigurations under configuration "
						+ cfgLevel.getPath();
				Map<String, Configuration> subConfigurations;
				beginMeasure(getAllSubConfigurationsFromCfg,
						Configuration.class);
				try {
					subConfigurations = cfgLevel.getAllSubConfigurations();
				} finally {
					endMeasure(getAllSubConfigurationsFromCfg);
				}
				if (subConfigurations != null) {
					for (String cfgName : subConfigurations.keySet()) {
						File folder = new File(containerFolder, cfgName);

						// if-check with side effect - a folder is created !!!
						// NOTE: there is a rare case which is not handled here
						// when the folder file exists but is an ordinary file,
						// not a directory
						if (!folder.exists() && !folder.mkdir()) {
							throw new IOException("Folder "
									+ folder.getAbsolutePath()
									+ " cannot be created on file system !");
						}
						// by this point, the folder should have been created
						downloadAllFilesByCfgLevel(subConfigurations
								.get(cfgName), folder);
					}
				}
			} else {
				// nothing to download
			}
		} else {
			// no configuration to download from --> return
		}
	}

	private Set<String> deleteContainerFolderContents(File containerFolder) {
		boolean status = true;
		File[] contents = containerFolder.listFiles();
		final String deleteContainerFolderTag = "delete content of container folder "
				+ containerFolder.getAbsolutePath() + " using FileUtils";
		beginMeasure(deleteContainerFolderTag, FileUtils.class);
		try {
			for (File file : contents) {
				if (file.isDirectory()) {
					status = FileUtils.deleteDirectory(file) && status;
				} else {
					status = FileUtils.deleteFile(file) && status;
				}
			}
		} finally {
			endMeasure(deleteContainerFolderTag);
		}

		if (status == false) {
			// check which files were not deleted
			contents = containerFolder.listFiles();
			Set<String> result = new HashSet<String>(contents.length);
			for (File file : contents) {
				result.add(file.getName());
			}
			return result;
		}
		return null;
	}

	private ApplicationServiceDescriptor removeASDescriptor(
			String applicationName) {
		return app_descriptors.remove(applicationName);
	}

	private ApplicationServiceDescriptor getASDescriptor(String applicationName) {
		return app_descriptors.get(applicationName);
	}

	private void updateASDescriptor(String applicationName, File descriptorFile)
			throws DeploymentException {
		// 1) descriptorFile != null ==> called during deploy/update ==> refresh
		// descriptor
		// 2) descriptorFile == null ==> called on application start (after
		// deploy)
		// ==> load descriptor if needed
		if (descriptorFile == null && getASDescriptor(applicationName) != null) {
			return;
		}
		if (LCResourceAccessor.isDebugTraceable()) {
			LCResourceAccessor
					.traceDebug(
							"Will update the application service descriptor for [{0}] application.",
							applicationName);
		}
		try {
			ApplicationServiceDescriptor descriptor = null;
			if (descriptorFile == null || descriptorFile.exists() == false) {
				String containerPath = communicator
						.getMyWorkDirectory(applicationName);
				File dir = new File(containerPath);
				descriptor = readASDescriptorFromDir(dir);
			} else {
				descriptor = readASDescriptorFile(descriptorFile);
			}
			if (descriptor != null) {
				app_descriptors.put(applicationName, descriptor);
			} else {
				removeASDescriptor(applicationName);
			}
		} catch (IOException ioex) {
			throw new LCDeploymentException(
					DeploymentExceptionConstants.START_ERROR_IO,
					new String[] { applicationName }, ioex);
		} catch (SAXException sax) {
			throw new LCDeploymentException(
					DeploymentExceptionConstants.START_ERROR_SAX,
					new String[] { applicationName }, sax);
		}
	}

	private File getDescriptorFile(File containerDir) {
		File metaInfDir = new File(containerDir, META_INF);
		File descFile = new File(metaInfDir, application_service_descriptor);
		if (!descFile.exists()) {
			metaInfDir = new File(containerDir, meta_inf);
			descFile = new File(metaInfDir, application_service_descriptor);
			if (!descFile.exists()) {
				descFile = new File(containerDir, application_service_descriptor);
			}
		}
		return descFile;
	}

	private ApplicationServiceDescriptor readASDescriptorFromDir(
			File containerDir) throws IOException, SAXException {
		File descFile = this.getDescriptorFile(containerDir);
		if (descFile.exists()) {
			return readASDescriptorFile(descFile);
		} else {
			return null;
		}
	}

	private ApplicationServiceDescriptor readASDescriptorFile(
			File descriptorFile) throws IOException, SAXException {
		InputStream is = new FileInputStream(descriptorFile);
		try {
			StandardDOMParser parser = createStandardDOMParser();
			return ApplicationServiceDescriptor.readXML(is, parser);
		} finally {
			is.close();
		}
	}

	public void synchronizeFiles(File containerDir, Configuration conf,
			Index dirIndex, Index configurationIndex)
			throws ConfigurationException, IOException {
		final FolderCompareResult fcr;
		final String compareResultTag = "compare container's directory and configuration indices using HashUtils; configuration path - "
				+ conf.getPath();
		beginMeasure(compareResultTag, HashUtils.class);
		try {
			fcr = dirIndex.compare(configurationIndex, true);
		} finally {
			endMeasure(compareResultTag);
		}
		if (fcr.isChanged()) {

			ArrayEnumeration fileNames = fcr.getFilesForDownload();
			String filePath = null;
			while (fileNames.hasNext()) {
				filePath = (String) fileNames.next();
				saveFile(filePath.replace('\\', '/'), conf, containerDir);
				if (LCResourceAccessor.isDebugTraceable()) {
					LCResourceAccessor
							.traceDebug(
									"File [{0}] downloaded from configuration [{1}] to container folder [{2}].",
									filePath, conf.getPath(), containerDir
											.getAbsolutePath());
				}
			}

			fileNames = fcr.getDeletedEntries();
			while (fileNames.hasNext()) {
				filePath = (String) fileNames.next();
				deleteMappingOnFS(containerDir, filePath);
				if (LCResourceAccessor.isDebugTraceable()) {
					LCResourceAccessor
							.traceDebug(
									"File [{0}] was deleted from container folder [{1}].",
									filePath, containerDir.getAbsolutePath());
				}
			}
		}
	}

	private void uploadFiles(File[] files, Hashtable fileMappings,
			Configuration conf) throws ConfigurationException {
		if (files != null) {
			String mapping = null;
			for (int i = 0; i < files.length; i++) {
				mapping = (String) fileMappings.get(files[i].getAbsolutePath());
				uploadFile(files[i], mapping, conf);
				if (LCResourceAccessor.isDebugTraceable()) {
					LCResourceAccessor.traceDebug(
							"File [{0} was uploaded to configuration [{1}]",
							mapping, conf.getPath());
				}
			}
		}
	}

	private void uploadFile(File file, String mapping, Configuration conf)
			throws ConfigurationException {
		if (mapping.startsWith("/")) {
			mapping = mapping.substring(1);
		}
		int index = mapping.indexOf('/');
		if (index != -1 && index < mapping.length() - 1) {
			Configuration subConf = null;
			String dir = mapping.substring(0, index);
			final String getOrCreateSubConfigurationTag = "get or create subconfiguration "
					+ dir + " under configuration " + conf.getPath();
			beginMeasure(getOrCreateSubConfigurationTag, Configuration.class);
			try {
				subConf = (conf.existsSubConfiguration(dir) ? conf
						.getSubConfiguration(dir) : conf
						.createSubConfiguration(dir));
			} finally {
				endMeasure(getOrCreateSubConfigurationTag);
			}
			uploadFile(file, mapping.substring(index + 1), subConf);
		} else {
			final String updateFileTag = "update file " + file.getName()
					+ " under configuration " + conf.getPath();
			beginMeasure(updateFileTag, Configuration.class);
			try {
				conf.updateFile(file, true);
			} finally {
				endMeasure(updateFileTag);
			}
		}
	}

	private void deleteMappingInConfiguration(Configuration conf, String mapping)
			throws ConfigurationException {
		if (mapping.startsWith("/")) {
			mapping = mapping.substring(1);
		}
		if (mapping.endsWith("/")) {
			mapping = mapping.substring(0, mapping.length() - 1);
		}
		int index = mapping.indexOf('/');
		if (index != -1) {
			Configuration subConf = null;
			String dir = mapping.substring(0, index);
			if (conf.existsSubConfiguration(dir)) {
				subConf = conf.getSubConfiguration(dir);
				deleteMappingInConfiguration(subConf, mapping
						.substring(index + 1));
			}
		} else {
			final String deleteMappingInConfigurationTag = "delete mapping "
					+ mapping + " in configuration " + conf.getPath();
			beginMeasure(deleteMappingInConfigurationTag, Configuration.class);
			try {
				if (conf.existsFile(mapping)) {
					conf.deleteFile(mapping);
				} else if (conf.existsSubConfiguration(mapping)) {
					conf.deleteConfiguration(mapping);
				}
			} finally {
				endMeasure(deleteMappingInConfigurationTag);
			}
		}
	}

	private void deleteMappingOnFS(File dir, String mapping)
			throws ConfigurationException {
		if (mapping.startsWith("/")) {
			mapping = mapping.substring(1);
		}
		if (mapping.endsWith("/")) {
			mapping = mapping.substring(0, mapping.length() - 1);
		}
		int index = mapping.indexOf('/');
		if (index != -1) {
			String name = mapping.substring(0, index);
			File subDir = new File(dir, name);
			if (subDir.exists()) {
				deleteMappingOnFS(subDir, mapping.substring(index + 1));
			}
		} else {
			File file = new File(dir, mapping);
			file.delete();
		}
	}

	private void saveFile(String mapping, Configuration rootconf, File rootdir)
			throws IOException, ConfigurationException {
		if (mapping.startsWith("/")) {
			mapping = mapping.substring(1);
		}
		if (mapping.endsWith("/")) {
			mapping = mapping.substring(0, mapping.length() - 1);
		}
		int index = mapping.indexOf('/');
		if (index != -1) {
			Configuration subConf = null;
			String dir = mapping.substring(0, index);
			if (rootconf.existsSubConfiguration(dir)) {
				subConf = rootconf.getSubConfiguration(dir);
				File newDir = new File(rootdir, dir);

				// if-check with side effect - a folder is created !!!
				if (!newDir.exists() && !newDir.mkdir()) {
					throw new IOException("Folder " + newDir.getAbsolutePath()
							+ " cannot be created on file system !");
				}
				saveFile(mapping.substring(index + 1), subConf, newDir);
			}
		} else {
			InputStream is;
			final String getFileTag = "get input stream from configuration "
					+ rootconf.getPath() + " for mapping " + mapping;
			beginMeasure(getFileTag, Configuration.class);
			try {
				is = rootconf.getFile(mapping);
			} finally {
				endMeasure(getFileTag);
			}
			File file = new File(rootdir, mapping);
			saveFile(is, file);
		}
	}

	private void saveFile(InputStream sourceStream, File destination)
			throws IOException {
		if (sourceStream != null) {
			FileOutputStream destinationStream = null;
			try {
				destinationStream = new FileOutputStream(destination);
				byte[] buffer = new byte[1024];
				int read = -1;
				while ((read = sourceStream.read(buffer)) > -1) {
					destinationStream.write(buffer, 0, read);
				}
			} finally {
				if (destinationStream != null) {
					destinationStream.close();
				}
				sourceStream.close();
			}
		}
	}

	/**
	 * Generate and write a hash index file calculated for the contents of the
	 * supplied directory.
	 * 
	 * @param dir
	 *            folder for whose content the hash index is generated (cannot
	 *            be <code>null</code>)
	 * @throws IOException
	 *             if index calculation faiis or the hash index file cannot be
	 *             persisted
	 * @return generated hash index
	 */
	/*
	 * private Index generateAndPersistIndexFile(File dir) throws IOException {
	 * Index dirIndex = HashUtils.getIndex(dir); byte[] indexInBytes =
	 * dirIndex.toByteArr(); FileOutputStream fos = null; try { fos = new
	 * FileOutputStream(new File(dir, BIN_FILE_NAME)); fos.write(indexInBytes);
	 * } finally { if (fos != null) { fos.close(); } } return dirIndex; }
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.ContainerInterface#prepareStart
	 * (java.lang.String, com.sap.engine.frame.core.configuration.Configuration)
	 */
	public void prepareStart(String applicationName,
			Configuration applicationConfig) throws DeploymentException,
			WarningException {
		// update ApplicationServiceDescriptor
		updateASDescriptor(applicationName, null);
		// update ResourceContext
		final String createResourceContextTag = "create resource context for application "
				+ applicationName;
		beginMeasure(createResourceContextTag, resourceFactory.getClass());
		ResourceContext resourceCtx;
		try {
			resourceCtx = resourceFactory.createContext(applicationName, null,
					true, null, true);
		} finally {
			endMeasure(createResourceContextTag);
		}
		if (resourceCtx != null) {
			this.app_resourceContext.put(applicationName, resourceCtx);
		}
		// execute
		this.executeNotification(applicationName, Action.START);
	}

	private void executeNotification(String applicationName, String type)
			throws DeploymentException, WarningException {
		InputStream is = null;
		ClassLoader loader = lc.getClassLoader(applicationName);
		ApplicationServiceDescriptor descriptor = getASDescriptor(applicationName);
		if (descriptor != null) {
			Action[] actions = descriptor.getActionsByType(type);
			if (actions != null && actions.length > 0) {
				for (int j = 0; j < actions.length; j++) {
					if (actions[j].methodName != null
							&& !actions[j].methodName.trim().equals("")) {
						this.invokeMethod(type, actions[j].className,
								actions[j].methodName, actions[j].isFatal,
								loader, applicationName);
					} else {
						this.invokeMethod(type, actions[j].className,
								actions[j].type, actions[j].isFatal, loader,
								applicationName);
					}
				}

				// NW 7.20: warning - the application hooks are being officially
				// deprecated
				final String helpURL = "https://jst.wdf.sap.corp/display/DeployTeam/(Deploy)+Application+Library+Container";
				final String helpSection = "Start and Stop Application Hooks";
				WarningException we = new WarningException(
						"Use of deprecated application hooks during operation type "
								+ type);
				we
						.addWarning("The use of application hooks during start and stop is deprecated "
								+ "and will not be supported in the next major release. "
								+ "The applicaiton developer should remove the current hooks completely and replace them with EJB hooks.\r\n"
								+ "Hints:\r\n"
								+ "1) Details about the deprecation can be found on the following link: \""
								+ helpURL
								+ "\", section \""
								+ helpSection
								+ "\".\r\n"
								+ "2) You can request more implementation details about how to create EJB hooks by mail to DL NW F EJB.");
				throw we;
			}
		}
	}

	private StandardDOMParser createStandardDOMParser() throws SAXException {
		ClassLoader previousLoader = null;
		try {
			previousLoader = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(
					this.getClass().getClassLoader());
			StandardDOMParser parser = new StandardDOMParser();
			parser.setEntityResolver(new EntityResolverImpl());
			parser.setValidation(true);
			return parser;
		} finally {
			Thread.currentThread().setContextClassLoader(previousLoader);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.ContainerInterface#commitStart
	 * (java.lang.String)
	 */
	public void commitStart(String applicationName) throws WarningException {
		// nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.ContainerInterface#rollbackStart
	 * (java.lang.String)
	 */
	public void rollbackStart(String applicationName) throws WarningException {
		// nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.ContainerInterface#prepareStop
	 * (java.lang.String, com.sap.engine.frame.core.configuration.Configuration)
	 */
	public void prepareStop(String applicationName,
			Configuration applicationConfig) throws DeploymentException,
			WarningException {
		// nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.ContainerInterface#commitStop
	 * (java.lang.String)
	 */
	public void commitStop(String applicationName) throws WarningException {
		try {
			this.executeNotification(applicationName, Action.STOP);
		} catch (DeploymentException dex) {
			LCResourceAccessor
					.traceException(
							"ASJ.dpl_lbc.000007",
							"[ERROR CODE DPL.LIB.040] Error during stop of application [{0}].",
							dex, applicationName);
		}
		this.app_resourceContext.remove(applicationName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.ContainerInterface#rollbackStop
	 * (java.lang.String)
	 */
	public void rollbackStop(String applicationName) throws WarningException {
		// nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.deploy.container.ContainerInterface#
	 * notifyRuntimeChanges(java.lang.String,
	 * com.sap.engine.frame.core.configuration.Configuration)
	 */
	public void notifyRuntimeChanges(String applicationName,
			Configuration appConfig) throws WarningException {
		removeASDescriptor(applicationName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.deploy.container.ContainerInterface#
	 * prepareRuntimeChanges(java.lang.String)
	 */
	public void prepareRuntimeChanges(String applicationName)
			throws DeploymentException, WarningException {
		// nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.deploy.container.ContainerInterface#
	 * commitRuntimeChanges(java.lang.String)
	 */
	public ApplicationDeployInfo commitRuntimeChanges(String applicationName)
			throws WarningException {
		removeASDescriptor(applicationName);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.deploy.container.ContainerInterface#
	 * rollbackRuntimeChanges(java.lang.String)
	 */
	public void rollbackRuntimeChanges(String applicationName)
			throws WarningException {
		// nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.ContainerInterface#getClientJar
	 * (java.lang.String)
	 */
	public File[] getClientJar(String applicationName) {
		Collection<File> cFiles = new ArrayList<File>();
		String containerPath;
		try {
			containerPath = communicator.getMyWorkDirectory(applicationName);
			File dir = new File(containerPath);
			getClientJars(dir, cFiles);
		} catch (IOException e) {
			// TODO - log or throw exception
			e.printStackTrace();
		}
		if (cFiles.size() == 0) {
			// TODO - eventually pass warning that the application may have not
			// been started
			// and no bundled libraries have been downloaded
			return null;
		}
		return cFiles.toArray(new File[cFiles.size()]);
	}

	/**
	 * Method iterates over files in a directory and adds them to the passed
	 * collection.
	 */
	private void getClientJars(File fParent, Collection<File> cFiles) {
		File[] files = fParent.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				getClientJars(files[i], cFiles);
			} else if (files[i] != null && files[i].getName().endsWith(".jar")) {
				cFiles.add(files[i]);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.deploy.container.ContainerInterface#
	 * addProgressListener
	 * (com.sap.engine.services.deploy.container.ProgressListener)
	 */
	public void addProgressListener(ProgressListener listener) {
		// nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.deploy.container.ContainerInterface#
	 * removeProgressListener
	 * (com.sap.engine.services.deploy.container.ProgressListener)
	 */
	public void removeProgressListener(ProgressListener listener) {
		// nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.deploy.container.ContainerInterface#
	 * needStopOnSingleFileUpdate
	 * (com.sap.engine.services.deploy.container.FileUpdateInfo[],
	 * com.sap.engine.services.deploy.container.ContainerDeploymentInfo,
	 * java.util.Properties)
	 */
	public boolean needStopOnSingleFileUpdate(FileUpdateInfo[] files,
			ContainerDeploymentInfo dInfo, Properties props)
			throws DeploymentException, WarningException {
		// nothing to do
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.deploy.container.ContainerInterface#
	 * makeSingleFileUpdate
	 * (com.sap.engine.services.deploy.container.FileUpdateInfo[],
	 * com.sap.engine.services.deploy.container.ContainerDeploymentInfo,
	 * java.util.Properties)
	 */
	public ApplicationDeployInfo makeSingleFileUpdate(FileUpdateInfo[] files,
			ContainerDeploymentInfo dInfo, Properties props)
			throws DeploymentException {
		// nothing to do
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.deploy.container.ContainerInterface#
	 * notifySingleFileUpdate(java.lang.String,
	 * com.sap.engine.frame.core.configuration.Configuration,
	 * java.util.Properties)
	 */
	public void notifySingleFileUpdate(String applicationName,
			Configuration config, Properties props) throws WarningException {
		removeASDescriptor(applicationName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.deploy.container.ContainerInterface#
	 * prepareSingleFileUpdate(java.lang.String)
	 */
	public void prepareSingleFileUpdate(String applicationName)
			throws DeploymentException, WarningException {
		// nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.deploy.container.ContainerInterface#
	 * commitSingleFileUpdate(java.lang.String)
	 */
	public ApplicationDeployInfo commitSingleFileUpdate(String applicationName)
			throws WarningException {
		removeASDescriptor(applicationName);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.deploy.container.ContainerInterface#
	 * rollbackSingleFileUpdate(java.lang.String,
	 * com.sap.engine.frame.core.configuration.Configuration)
	 */
	public void rollbackSingleFileUpdate(String applicationName,
			Configuration config) throws WarningException {
		// nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.deploy.container.ContainerInterface#
	 * applicationStatusChanged(java.lang.String, byte)
	 */
	public void applicationStatusChanged(String applicationName, byte status) {
		// nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.deploy.container.ContainerInterface#
	 * getResourcesForTempLoader(java.lang.String)
	 */
	public String[] getResourcesForTempLoader(String applicationName)
			throws DeploymentException {
		// nothing to do
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.deploy.container.ContainerInterface#
	 * acceptedAppInfoChange(java.lang.String,
	 * com.sap.engine.services.deploy.container.AdditionalAppInfo)
	 */
	public boolean acceptedAppInfoChange(String appName,
			AdditionalAppInfo addAppInfo) throws DeploymentException {
		// nothing to do
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.deploy.container.ContainerInterface#
	 * needStopOnAppInfoChanged(java.lang.String,
	 * com.sap.engine.services.deploy.container.AdditionalAppInfo)
	 */
	public boolean needStopOnAppInfoChanged(String appName,
			AdditionalAppInfo addAppInfo) {
		// nothing to do
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.container.ContainerInterface#makeAppInfoChange
	 * (java.lang.String,
	 * com.sap.engine.services.deploy.container.AdditionalAppInfo,
	 * com.sap.engine.frame.core.configuration.Configuration)
	 */
	public void makeAppInfoChange(String appName, AdditionalAppInfo addAppInfo,
			Configuration configuration) throws WarningException,
			DeploymentException {
		// nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.deploy.container.ContainerInterface#
	 * appInfoChangedCommit(java.lang.String)
	 */
	public void appInfoChangedCommit(String appName) throws WarningException {
		// nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.deploy.container.ContainerInterface#
	 * appInfoChangedRollback(java.lang.String)
	 */
	public void appInfoChangedRollback(String appName) throws WarningException {
		// nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.deploy.container.ContainerInterface#
	 * notifyAppInfoChanged(java.lang.String)
	 */
	public void notifyAppInfoChanged(String appName) throws WarningException {
		removeASDescriptor(appName);
	}

	private void invokeMethod(String operationType, String className,
			String methodName, boolean isFatal, ClassLoader loader,
			String applicationName) throws DeploymentException {
		CountDown cd = new CountDown(1);
		ResourceContext resourceCtx = this.app_resourceContext
				.get(applicationName);
		LifeCycleHookThread thread = new LifeCycleHookThread(cd, className,
				loader, methodName, applicationName, resourceCtx);

		try {
			LCThreadWrapperAccessor.getInstance().pushTask(
					selectOperationString(operationType), applicationName);
			asc.getCoreContext().getThreadSystem().startThread(thread, null,
					thread.getThreadName(), false, false);
			cd.acquire();
		} catch (InterruptedException e) {
			LCResourceAccessor
					.traceException(
							"ASJ.dpl_lbc.000008",
							"[ERROR CODE DPL.LIB.106] The thread, executing the life cycle hook for application [{0}], was interrupted.",
							e, applicationName);
		} finally {
			LCThreadWrapperAccessor.getInstance().popTask();
		}

		Throwable th = thread.getThrowable();
		if (th != null) {
			if (isFatal) {
				throw new LCDeploymentException(
						DeploymentExceptionConstants.NOTIFY, new String[] {
								className, methodName, applicationName }, th);
			} else {
				LCResourceAccessor
						.traceException(
								"ASJ.dpl_lbc.000009",
								"[ERROR CODE DPL.LIB.102] Error during execution of notification with class-name [{0}], method-name [{1}] for application [{2}]",
								th, className, methodName, applicationName);
			}
		}
	}

	private String selectOperationString(String operationType) {
		String operationString = "???";
		if (operationType.equals(Action.START)) {
			operationString = "invoke start application hook";
		} else if (operationType.equals(Action.STOP)) {
			operationString = "invoke stop application hook";
		}
		return operationString;
	}

	static String getLCServiceName() {
		return service_name;
	}
}
