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
package com.sap.engine.services.deploy.server.application;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.CustomParameterMappings;
import com.sap.engine.lib.io.FileUtils;
import com.sap.engine.lib.io.hash.Entry;
import com.sap.engine.lib.io.hash.HashUtils;
import com.sap.engine.lib.io.hash.Index;
import com.sap.engine.lib.io.hash.PathNotFoundException;
import com.sap.engine.lib.time.SystemTime;
import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.deploy.DeployService;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.ApplicationDeployInfo;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.ContainerDeploymentInfo;
import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.ReferenceObjectIntf;
import com.sap.engine.services.deploy.container.op.IOpConstants;
import com.sap.engine.services.deploy.container.op.util.FileInfo;
import com.sap.engine.services.deploy.container.op.util.FileType;
import com.sap.engine.services.deploy.container.op.util.ModuleInfo;
import com.sap.engine.services.deploy.container.op.util.ModuleProvider;
import com.sap.engine.services.deploy.container.op.util.Status;
import com.sap.engine.services.deploy.container.op.util.StatusDescriptionsEnum;
import com.sap.engine.services.deploy.container.util.CAConvertor;
import com.sap.engine.services.deploy.ear.J2EEModule;
import com.sap.engine.services.deploy.ear.Module;
import com.sap.engine.services.deploy.ear.jar.DplArchiveReader;
import com.sap.engine.services.deploy.ear.jar.EARReader;
import com.sap.engine.services.deploy.ear.jar.EarDescriptor;
import com.sap.engine.services.deploy.ear.modules.Web;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.server.DUtils;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.DeployServiceContext;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.cache.containers.ContainerComparatorReverted;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.server.cache.dpl_info.Applications;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.InitiallyStarted;
import com.sap.engine.services.deploy.server.dpl_info.module.Version;
import com.sap.engine.services.deploy.server.editor.DIWriter;
import com.sap.engine.services.deploy.server.editor.EditorFactory;
import com.sap.engine.services.deploy.server.editor.impl.second.DIConsts2;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.ExtractUtils;
import com.sap.engine.services.deploy.server.utils.FSUtils;
import com.sap.engine.services.deploy.server.utils.ValidateUtils;
import com.sap.engine.services.deploy.server.validate.jlin.AppJLinInfo;
import com.sap.engine.services.deploy.server.validate.jlin.JLinPlunin;
import com.sap.engine.services.deploy.timestat.DeployOperationTimeStat;
import com.sap.engine.services.deploy.timestat.ITimeStatConstants;
import com.sap.engine.services.deploy.timestat.TransactionTimeStat;
import com.sap.lib.javalang.tool.exception.ReadingException;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * This class is intended only for internal use by deploy service.
 */
public abstract class DeployUtilTransaction extends ApplicationTransaction {
	private static final Location location = 
		Location.getLocation(DeployUtilTransaction.class);

	protected DeploymentInfo deployment;
	protected String[] allComponents;
	protected Configuration config;
	protected Configuration deployConfig;
	protected DplArchiveReader reader;
	protected File moduleFile;
	protected String[] remoteSupport;
	protected EarDescriptor descr;
	protected File tempDirForExtraction;
	protected Configuration globalConfig;
	private Properties props;

	public DeployUtilTransaction(final DeployServiceContext ctx) {
		super(ctx, PropManager.getInstance().isTxOperationSupported());
	}

	protected final void processApplicationDeployInfo(
		final ApplicationDeployInfo tempInfo, final File[] dFiles, 
		final ContainerInterface cont, final DeploymentInfo oldDInfo) {
		final Set<String> dfNames = CAConvertor.asSet(CAConvertor.cObject(dFiles));
		TransactionUtil.updateCDataInDInfo(this, deployment, oldDInfo,
				tempInfo, dfNames, cont);
		{// add to result
			if (tempInfo != null) {
				String[] components = tempInfo.getDeployedComponentNames();
				if (components != null && components.length > 0) {
					final ContainerInfo cInfo = cont.getContainerInfo();
					components = DUtils.addToElements(components, " - "
							+ (cInfo.isJ2EEContainer() ? cInfo
									.getJ2EEModuleName().toUpperCase() : cInfo
									.getModuleName().toUpperCase()));
					allComponents = DUtils.concatArrays(allComponents,
							components);
				}
			}
		}
	}

	protected void modifyDeploymentInfoInConfiguration()
		throws DeploymentException {
		deployment.setVersion(Version.getNewestVersion());
		final DIWriter diWriter = EditorFactory.getInstance().getDIWriter(
				deployment.getVersion());
		diWriter.modifyDeploymentInfo(config, deployConfig, deployment);

		if (this instanceof DeploymentTransaction) {
			ctx.getTxCommunicator().setLocalApplicationStatus(
				deployment.getApplicationName(), deployment.getStatus(), 
				deployment.getStatusDescription().getId(), 
				deployment.getStatusDescription().getParams());
		}
	}

	protected void commonBegin() throws DeploymentException {
		try {
			reader.read();
		} catch (IOException ex) {
			SimpleLogger.trace(Severity.ERROR, location, 
				"ASJ.dpl_ds.000583", 
				"IOException in thread {0}. {1}",
				ctx.getLockManager().dumpCurrentThread(),
				ctx.getLockManager().dumpLocks());
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.ERROR_IN_READING_DESCR,
				new String[] { moduleFile.getAbsolutePath() }, ex);
			sde.setMessageID("ASJ.dpl_ds.005041");
			throw sde;
		}
		validateEarDescriptor();
		deployment = new DeploymentInfo(getModuleID());

		try {
			// the language libs has to be extracted for the reader.
			descr = reader.getDescriptor();
			deployment.setFailOver(descr.getFailOver());
			deployment.setStartUp(descr.getStartUp());
			// since 30.01.2006
			// set the java version - the version is stored also in the
			// properties so additional handling is implemented within the
			// DeploymentInfo class to accomplish that
			deployment.setJavaVersion(descr.getJavaVersion(), descr
					.isCustomJavaVersion());
		} catch (IOException ioex) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.ERROR_DURING_GET_INTERNAL_LIBS,
				new String[] { getModuleID(), getTransactionType() }, ioex);
			sde.setMessageID("ASJ.dpl_ds.005046");
			throw sde;
		}
		tempDirForExtraction = reader.getTempDir();

		deployment.setStandAloneArchive(
			moduleType == DeployConstants.MODULE_TYPE);
		deployment.setRemoteSupport(remoteSupport);
		if (this instanceof UpdateTransaction) {
			deployment.setStatus(
				((UpdateTransaction) this).oldDeployment.getStatus(), 
				StatusDescriptionsEnum.STATUS_BEFORE_UPDATE, null);
		} else {
			deployment.setStatus(Status.STOPPED,
				StatusDescriptionsEnum.STATUS_BEFORE_TRANSACTION,
				new Object[] { Status.STOPPED.getName(),
					getTransactionType() });
		}
		deployment.setProperties(getProperties());
		if (moduleType == DeployConstants.APP_TYPE) {
			deployment.setAdditionalClasspath(descr.getClassPath());
			try {
				final byte appXml[] = ((EARReader) reader)
					.readEntryAsByteArray("META-INF/application.xml");
				if (appXml != null) {
					deployment.setApplicationXML(new String(appXml));
				}
			} catch (Exception ex) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.UNEXPECTED_EXCEPTION,
						new String[] { "reading application xml stream",
							getModuleID() }, ex);
				sde.setMessageID("ASJ.dpl_ds.005029");
				throw sde;
			}
		}
		processReferences();
		if (this instanceof DeploymentTransaction) {
			ctx.getTxCommunicator().addApplicationInfo(
				getModuleID(), deployment);
		}

		final Hashtable<String, File[]> allContFiles = 
			descr.getAllContainerFiles();
		allComponents = DUtils.concatArrays(allComponents, new String[] {
				"Application : " + getModuleID(), "" });
		try {
			this.fillAliases(allContFiles.keySet());

			deployment.setInitiallyStarted(InitiallyStarted.YES);
			deployment.setModuleProvider(getModuleProvider());

			ContainerDeploymentInfo containerInfo = 
				getCD4getConcernedContainers(allContFiles, deployment);

			final Hashtable<ContainerInterface, Properties> contsProps = 
				getConcernedContainers(allContFiles, containerInfo);
			ContainerInterface[] concernedContainers = new ContainerInterface[contsProps
					.size()];
			Enumeration conts = contsProps.keys();
			int j = 0;
			while (conts.hasMoreElements()) {
				ContainerInterface consernedContainer = (ContainerInterface) conts
						.nextElement();
				concernedContainers[j++] = consernedContainer;
			}
			Arrays.sort(concernedContainers,
					ContainerComparatorReverted.instance);
			if (config == null && deployConfig == null) {
				config = openApplicationConfiguration(
					DeployConstants.ROOT_CFG_APPS,
					ConfigurationHandler.WRITE_ACCESS);
				deployConfig = openApplicationConfiguration("deploy",
						ConfigurationHandler.WRITE_ACCESS);
				globalConfig = createOrGetGlobalApplicationConfiguration();
			}
			try {
				uploadSpecialEARFiles();
			} catch (ConfigurationException cex) {
				ServerDeploymentException sde = new ServerDeploymentException(
						ExceptionConstants.ERROR_DURING_UPLOAD_SPECIAL_FILES,
						new String[] { getModuleID(), getTransactionType() },
						cex);
				sde.setMessageID("ASJ.dpl_ds.005102");
				throw sde;
			} catch (IOException ioex) {
				ServerDeploymentException sde = new ServerDeploymentException(
						ExceptionConstants.ERROR_DURING_UPLOAD_SPECIAL_FILES,
						new String[] { getModuleID(), getTransactionType() },
						ioex);
				sde.setMessageID("ASJ.dpl_ds.005102");
				throw sde;
			}
			containerInfo = getCD4makeComponents(containerInfo);

			{// will validate the application
				containerInfo = validateApplication(
					containerInfo, allContFiles);
			}
			if (location.beDebug()) {
				SimpleLogger.trace(Severity.DEBUG, location, null,
					"ContainerName2File[] for application [{0}] in operation [{1}] : ",
					allContFiles, getModuleID(), getTransactionType());
			}
			for (int i = 0; i < concernedContainers.length; i++) {
				String contName = concernedContainers[i].getContainerInfo()
						.getName();
				makeComponents(allContFiles.get(contName),
					containerInfo, contsProps.get(concernedContainers[i]),
						concernedContainers[i]);
			}
			// The client has the responsibility to clean up the file system,
			// because he has created the file.
			/*
			 * final String modulFilePath = moduleFile.getCanonicalPath(); if
			 * (modulFilePath
			 * .indexOf(PropManager.getInstance().getServiceWorkDir()) != -1) {
			 * moduleFile.delete(); }
			 */
			ValidateUtils.missingDCinDIValidator(deployment,
					getTransactionType(), concernedContainers);
			if (location.beDebug()) {
				SimpleLogger.trace(Severity.DEBUG, location, null,
					"Generation phase for application [{0}] ended successfully.",
					getModuleID());
			}
		} catch (DeploymentException de) {
			throw de;
		} catch (Exception e) {
			final ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.CANNOT_DEPLOY_APP, new String[] {
					getTransactionType(), getModuleID() }, e);
			sde.setMessageID("ASJ.dpl_ds.005047");
			throw sde;
		} catch (OutOfMemoryError oofmer) {
			throw oofmer;
		} catch (ThreadDeath td) {
			throw td;
		} catch (Error er) {
			final ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.CANNOT_DEPLOY_APP, new String[] {
							getTransactionType(), getModuleID() }, er);
			sde.setMessageID("ASJ.dpl_ds.005047");
			throw sde;
		}
		if (this instanceof DeploymentTransaction) {
			ctx.getReferenceResolver().check4Cycles(
				deployment.getApplicationName());
		} else {
			ctx.getReferenceResolver().check4CyclesAndRestore(deployment);
		}
	}

	/**
	 * Extract the passed RAR archives. 
	 * @param workDir working directory used for extraction. Not null, an 
	 * existing directory.
	 * @param rarArchives archives which have to be extracted.
	 * @return a hashtable which describes the content of RAR files. The keys
	 * are absolute paths to RAR files, and values - the absolute paths to 
	 * extracted JAR and WAR archives, which were included in the original RAR
	 * file. The returned result cannot be null.
	 * @throws IOException
	 */
	private Hashtable<String, List<String>> extractRARs(final File workDir,
		final File[] rarArchives) throws IOException {
		assert workDir != null;
		assert workDir.isDirectory();
		
		final Hashtable<String, List<String>> rarsDescription =
			new Hashtable<String, List<String>>();

		if(rarArchives != null) {
			for(File rarArchive : rarArchives) {
				// Extract the corresponding RAR archive.
				final String extrDir = ExtractUtils.extractZip(
					rarArchive, workDir);
				// List included JARs and WARs.
				rarsDescription.put(
					rarArchive.getAbsolutePath(), 
					listArchivePaths(extrDir));
			}
		}
		return rarsDescription;
	}
	
	
	/**
	 * Return a list of all JAR and WAR files, under the given path.
	 * @param path the absolute path on the file system. It has to be an 
	 * existing directory. Not null. 
	 * @return a list of absolute paths to all JAR and WAR files, under the 
	 * given path. Cannot be null, but can be an empty list.
	 * @throws IOException 
	 */
	private List<String> listArchivePaths(final String path) 
		throws IOException {
		final LinkedList<File> dirs = new LinkedList<File>();

		dirs.add(new File(path));
		
		final List<String> result = new ArrayList<String>();
		while(dirs.size() > 0) {
			final File dir = dirs.pop();
			for(File file : FileUtils.listFiles(dir)) {
				if (file.isDirectory()) {
					// We will go through this directory,
					// searching for archives.
					dirs.push(file);
				} else if (file.getName().toLowerCase().endsWith(".jar") ||
					file.getName().toLowerCase().endsWith(".war")) {
					// Add all jar and war files.
					result.add(file.getAbsolutePath());
				}
			}
		}
		return result;
	}

	private File[] getConnectorContainerFiles(
		final Hashtable<String, File[]> cName2Files) {
		final ContainerInterface cIntf = Containers.getInstance()
			.getJ2eeContainer(J2EEModule.Type.connector);
		return (cIntf != null) ? cName2Files.get(
			cIntf.getContainerInfo().getName()) : null;
	}

	protected void validateEarDescriptor() throws DeploymentException {
		final Set<Module> allModules = descr.getAllModules();
		if (allModules.size() == 0) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.NOT_RECOGNIZED_COMPONENT,
					new String[] { moduleFile.getName() });
			sde.setMessageID("ASJ.dpl_ds.005014");
			throw sde;
		}
		if (location.beInfo()) {
			SimpleLogger.trace(Severity.INFO, location, 
				"ASJ.dpl_ds.000050",
				"The component [{0}] was split into the following modules per container : [{1}]",
				moduleFile.getName(), CAConvertor.toString(allModules, ""));
		}
	}

	// fills parsed deployment descriptors in ContainerDeploymentInfo during
	// JLIN
	// validation
	/**
	 * @param cdInfo
	 * @param moduleFile
	 * @param allContFiles
	 * @return
	 * @throws DeploymentException
	 * @throws ReadingException
	 */
	@SuppressWarnings("boxing")
	private ContainerDeploymentInfo validateApplication(
		final ContainerDeploymentInfo cdInfo,
		final Hashtable<String, File[]> allContFiles) 
		throws DeploymentException, ReadingException {

		ValidateUtils.nullValidator(cdInfo, "container deployment info");

		{// JLin
			// Create AppJLinInfo
			AppJLinInfo appJlinInfo = null;

			long start = System.currentTimeMillis();
			long cpuStartTime = SystemTime.currentCPUTimeUs();
			try {
				Accounting.beginMeasure(
					ITimeStatConstants.JLIN_EE_PREPROCESS_DURATION,
					AppJLinInfo.class);
				appJlinInfo = createAppJLinInfo(cdInfo, allContFiles);
			} finally {
				Accounting.endMeasure(ITimeStatConstants.JLIN_EE_PREPROCESS_DURATION);
				// time statistic
				long end = System.currentTimeMillis();
				long cpuEndTime = SystemTime.currentCPUTimeUs();
				TransactionTimeStat
						.addJLinEEOperation(new DeployOperationTimeStat(
								ITimeStatConstants.JLIN_EE_PREPROCESS_DURATION,
								start, end, cpuStartTime, cpuEndTime));
			}
			// Execute validation
			start = System.currentTimeMillis();
			cpuStartTime = SystemTime.currentCPUTimeUs();
			JLinPlunin jlinPlunin = null;
			try {
				Accounting.beginMeasure(
						ITimeStatConstants.JLIN_EE_PREPROCESS_DURATION,
						AppJLinInfo.class);
				jlinPlunin = JLinPlunin.getInstance();
				addWarnings(jlinPlunin.exec(appJlinInfo));
			} finally {
				Accounting
						.endMeasure(ITimeStatConstants.JLIN_EE_PREPROCESS_DURATION);
				// time statistic
				long end = System.currentTimeMillis();
				long cpuEndTime = SystemTime.currentCPUTimeUs();
				TransactionTimeStat
						.addJLinEEOperation(new DeployOperationTimeStat(
								ITimeStatConstants.JLIN_EE_VALIDATION_DURATION,
								start, end, cpuStartTime, cpuEndTime));
				if (location.beDebug()) {
					SimpleLogger.trace(Severity.DEBUG, location, null,
						"The validation of [{0}] application from JLinEE took [{1}] ms.",
						appJlinInfo.getAppName(), (end - start));
				}
			}

			return new ContainerDeploymentInfo(cdInfo,
					appJlinInfo.getValidatedModeslCache());
		}
	}

	/**
	 * Exclusive method for application info preparation for the JLinEE
	 * operation for better time statistic wrapping.
	 * 
	 * @param cdInfo
	 * @param cName2Files
	 * @return
	 * @throws DeploymentException
	 */
	private AppJLinInfo createAppJLinInfo(ContainerDeploymentInfo cdInfo,
		Hashtable<String, File[]> cName2Files) throws DeploymentException {
		// Create AppJLinInfo
		AppJLinInfo appJlinInfo = null;
		
		final Hashtable<String, List<String>> rarsDescription;
		try {
			rarsDescription = extractRARs(
				tempDirForExtraction.getAbsoluteFile(),
				getConnectorContainerFiles(cName2Files));
		} catch (IOException e) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
				new String[] { "sub modules for application "
					+ getModuleID() }, e);
			sde.setMessageID("ASJ.dpl_ds.005082");
			throw sde;
		}

		// Get CustomParameterMappings
		CustomParameterMappings cpMappings;
		try {
			ValidateUtils.nullValidator(getHandler(), "configuration handler");
			cpMappings = getHandler().getCustomParameterMappings();
		} catch (ConfigurationException cex) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.VAL_APP_NOT_POSSIBLE, new String[] {
							getModuleID(), "custom parameter mappings data" },
					cex);
			sde.setMessageID("ASJ.dpl_ds.005401");
			throw sde;
		}

		try {
			appJlinInfo = new AppJLinInfo(
					deployment,
					getTransactionType(),
					(moduleType == DeployConstants.APP_TYPE ? moduleFile : null),
					cName2Files, rarsDescription, cdInfo.getFileMappings(),
					cpMappings, moduleUri2moduleAltDD(), reader);
		} catch (IOException ioEx) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.VAL_APP_NOT_POSSIBLE, new String[] {
							getModuleID(), "application information" }, ioEx);
			sde.setMessageID("ASJ.dpl_ds.005401");
			throw sde;
		}
		return appJlinInfo;
	}

	private Map<String, String> moduleUri2moduleAltDD() {
		Map<String, String> uriInEar2AltDD = null;
		if (moduleType == DeployConstants.APP_TYPE) {
			Set<Module> modules = descr.getJ2EEModules();
			uriInEar2AltDD = new HashMap<String, String>(modules.size());
			for(final Module module : modules) {
				uriInEar2AltDD.put(module.getUri(), 
					((J2EEModule)module).getAlt_dd());
			}
		} else {
			uriInEar2AltDD = Collections.emptyMap();
		}
		return uriInEar2AltDD;
	}


	private ContainerDeploymentInfo getCD4getConcernedContainers(
			Hashtable<String, File[]> allContFiles, DeploymentInfo dInfo)
			throws DeploymentException {
		Properties aliasesUris = new Properties();
		// since 26.01.2006
		// added for java version differentiation
		Properties additionalProps = new Properties();

		EarDescriptor seDescr = null;
		if (this.getModuleType() == DeployConstants.APP_TYPE) {
			seDescr = descr;

			Web[] modules = descr.getWEBs();
			if (modules != null) {
				for (int i = 0; i < modules.length; i++) {
					aliasesUris.put(modules[i].getContextRoot(), modules[i]
							.getUri());
				}
			}

			// since 26.01.2006
			// added for java version differentiation;
			// these properties should be already contained in the
			// DeploymentInfo at this point
			additionalProps.put(IOpConstants.JAVA_VERSION, dInfo
					.getJavaVersion());
		} else {
			Enumeration names = getProperties().propertyNames();
			String name = null;

			while (names.hasMoreElements()) {
				name = (String) names.nextElement();

				if (name.startsWith("web:")) {
					aliasesUris.put(name.substring("web:".length()),
							getProperties().getProperty(name));
				}
			}
			String moduleName = this.moduleFile.getName();
			if (moduleName.endsWith(".war") && aliasesUris.size() == 0) {
				String alias = null;
				alias = moduleName.substring(0, moduleName.lastIndexOf('.'));
				aliasesUris.put(alias, moduleName);
				getProperties().put("web:" + alias, moduleName);
			}
		}

		final Map<String, Entry> archiveFilePath2Entry = 
			getArchiveFilePath2Entry(allContFiles.values());

		return new ContainerDeploymentInfo(getModuleID(), config, null,
				remoteSupport, dInfo.getModuleProvider(),
				seDescr != null ? seDescr.getFailOver() : null, seDescr,
				seDescr != null ? seDescr.getFileMappings()
				: getFileMappings4StandAlone(allContFiles.values()),
				allContFiles, moduleType == DeployConstants.MODULE_TYPE,
				aliasesUris, new AppConfigurationHandlerImpl(getHandler()),
			null, descr.getAnnotations(), additionalProps,
				archiveFilePath2Entry);
	}

	private Map<String, Entry> getArchiveFilePath2Entry(
		final Collection<File[]> allContFiles) throws DeploymentException {
		final Map<String, Entry> archiveFilePath2Entry = 
			new HashMap<String, Entry>(3);
		Index tempIndexFS4Update = null;
		for (final File[] files : allContFiles) { 
			for (File file : files) {
				if (archiveFilePath2Entry.get(file.getAbsolutePath()) != null) {
					continue;
				}
				try {
					tempIndexFS4Update = HashUtils.createIndex(getModuleID());
					tempIndexFS4Update.addFile(file, true);
					archiveFilePath2Entry.put(file.getAbsolutePath(),
							tempIndexFS4Update.getFile(file.getName()));
				} catch (PathNotFoundException pnfEx) {
					ServerDeploymentException sde = new ServerDeploymentException(
							ExceptionConstants.GENERAL_IO_EXCEPTION,
							new String[] { "getting index for " + file
									+ " file." }, pnfEx);
					sde.setMessageID("ASJ.dpl_ds.005103");
					throw sde;
				} catch (IOException ioEx) {
					ServerDeploymentException sde = new ServerDeploymentException(
							ExceptionConstants.GENERAL_IO_EXCEPTION,
							new String[] { "generating index for update "
									+ tempDirForExtraction.getAbsolutePath() },
							ioEx);
					sde.setMessageID("ASJ.dpl_ds.005103");
					throw sde;
				}
			}
		}
		return archiveFilePath2Entry;
	}

	private Hashtable<String, String> getFileMappings4StandAlone(
		final Collection<File[]> allContFiles) {
		final Hashtable<String, String> fileMappings = 
			new Hashtable<String, String>();
		for(final File[] files : allContFiles) {
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
					fileMappings.put(files[i].getAbsolutePath(), 
						files[i].getName());
					}
				}
			}
		return fileMappings;
	}

	private ContainerDeploymentInfo getCD4makeComponents(
		ContainerDeploymentInfo cdi) {
		return new ContainerDeploymentInfo(cdi, config,
				new AppConfigurationHandlerImpl(getHandler()));
	}

	private void uploadSpecialEARFiles() throws IOException,
			ConfigurationException {
		Configuration propertiesConfig = null;
		if (globalConfig.existsSubConfiguration("appcfg")) {
			propertiesConfig = globalConfig.getSubConfiguration("appcfg");
		} else {
			propertiesConfig = globalConfig.createSubConfiguration("appcfg");
		}
		uploadSAPPropertiesFile(propertiesConfig);
		uploadSAPManifestFile(propertiesConfig);
	}

	private void uploadSAPPropertiesFile(Configuration propertiesConfig)
			throws IOException, ConfigurationException {
		File sapPropertiesFile = null;
		if (this.getModuleType() == DeployConstants.APP_TYPE) {
			sapPropertiesFile = ((EARReader) reader)
					.extractFileNonCaseSensitive(DeployConstants.FN_META_INF
							+ "/" + DeployConstants.FN_SAP_APP_GLOBAL_PROPS);
		} else {
			sapPropertiesFile = FSUtils.getFileNonCaseSensitive(
					tempDirForExtraction, new String[] {
							DeployConstants.FN_META_INF,
							DeployConstants.FN_SAP_APP_GLOBAL_PROPS });
		}
		if (sapPropertiesFile != null && sapPropertiesFile.exists()) {
			FileInputStream fis = new FileInputStream(sapPropertiesFile);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				byte[] buffer = new byte[1024];
				int read = 0;
				while ((read = fis.read(buffer)) > -1) {
					baos.write(buffer, 0, read);
				}
				Configuration appProperties = null;
				byte[] res = baos.toByteArray();
				if (propertiesConfig
						.existsSubConfiguration(DIConsts2.APP_GLOB_PROPS)) {
					appProperties = propertiesConfig
							.getSubConfiguration(DIConsts2.APP_GLOB_PROPS);
					appProperties.getPropertySheetInterface()
							.updatePropertyEntries(res);
				} else {
					appProperties = propertiesConfig.createSubConfiguration(
							DIConsts2.APP_GLOB_PROPS,
							Configuration.CONFIG_TYPE_PROPERTYSHEET);
					appProperties.getPropertySheetInterface()
							.createPropertyEntries(res);
				}
			} finally {				
					fis.close();
					baos.close();
			}
			deployment.getConfigProvider().setAppGlobalPropsCfg(true);
		}
	}

	private void uploadSAPManifestFile(Configuration propertiesConfig)
			throws IOException, ConfigurationException {
		File manifestFile = null;
		if (this.getModuleType() == DeployConstants.APP_TYPE) {
			manifestFile = ((EARReader) reader)
					.extractFileNonCaseSensitive(DeployConstants.FN_META_INF
							+ "/" + DeployConstants.FN_SAP_MANIFEST_MF);
		} else {
			manifestFile = FSUtils.getFileNonCaseSensitive(
					tempDirForExtraction, new String[] {
							DeployConstants.FN_META_INF,
							DeployConstants.FN_SAP_MANIFEST_MF });
		}
		if (manifestFile != null && manifestFile.exists()) {
			Configuration manifestConfig = null;
			if (propertiesConfig.existsSubConfiguration(DIConsts2.SAP_MANIFEST)) {
				manifestConfig = propertiesConfig
						.getSubConfiguration(DIConsts2.SAP_MANIFEST);
				manifestConfig.updateFile(manifestFile);
			} else {
				manifestConfig = propertiesConfig
						.createSubConfiguration(DIConsts2.SAP_MANIFEST);
				manifestConfig.addFileEntry(manifestFile);
			}
			deployment.getConfigProvider().setAppGlobalPropsCfg(true);
			deployment.getConfigProvider().setSapManifest(true);
		}
	}

	public String[] getResult() {
		return allComponents;
	}

	@Override
	public String getSoftwareType() {
		return getProperties().getProperty(DeployService.softwareType);
	}

	private void processReferences() throws DeploymentException {
		final List<ReferenceObject> additionalRefs = 
			new ArrayList<ReferenceObject>();
		try {
			if (moduleType == DeployConstants.APP_TYPE
					|| moduleType == DeployConstants.MODULE_TYPE) {
				ReferenceObjectIntf[] refObjs = reader.getDescriptor()
						.getReferences();
				if (refObjs != null && refObjs.length > 0) {
					ReferenceObject temp = null;
					for (int i = 0; i < refObjs.length; i++) {
						temp = new ReferenceObject();
						temp.setReferenceProviderName(refObjs[i]
								.getReferenceProviderName());
						temp.setReferenceTarget(refObjs[i]
										.getReferenceTarget());
						temp.setReferenceTargetType(refObjs[i]
								.getReferenceTargetType());
						temp.setReferenceType(refObjs[i].getReferenceType());
						additionalRefs.add(temp);
					}
				}
			}
		} catch (IOException ioex) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.ERROR_IN_READING_DESCR,
					new String[] { getModuleID() }, ioex);
			sde.setMessageID("ASJ.dpl_ds.005041");
			throw sde;
		}
		ReferenceObject[] previousRefs = ctx.getTxCommunicator()
				.getAdditionalReferences(getModuleID());
		if (previousRefs != null) {
			for (int i = 0; i < previousRefs.length; i++) {
				additionalRefs.add(previousRefs[i]);
			}
		}
		ReferenceObject[] addRefs = new ReferenceObject[additionalRefs.size()];
		additionalRefs.toArray(addRefs);
		for (int i = 0; i < addRefs.length; i++) {
			deployment.addReference(addRefs[i]);
		}
	}

	private void fillAliases(Set<String> containers) 
		throws DeploymentException {
		ContainerInterface cont = null;
		for(String container : containers) {
			cont = ctx.getTxCommunicator().getContainer(container);
			if (cont == null) {
				continue;
			}
			if (cont.getContainerInfo().isJ2EEContainer()
					&& cont.getContainerInfo().getJ2EEModuleName().equals(
							J2EEModule.Type.web.name())) {
				Web[] modules = null;
				try {
					modules = reader.getDescriptor().getWEBs();
				} catch (IOException e) {
					ServerDeploymentException sde = new ServerDeploymentException(
							ExceptionConstants.ERROR_IN_READING_DESCR,
							new String[] { getModuleID() }, e);
					sde.setMessageID("ASJ.dpl_ds.005041");
					throw sde;
				}

				if (modules != null) {
					String key = null, value = null, alias = null;
					Properties props = getProperties();
					for (int i = 0; i < modules.length; i++) {
						key = modules[i].getUri();
						value = modules[i].getContextRoot();
						// if original aliases should be replaced with these
						// from properties
						// currently only CTS 5.0 needs this logic
						if (props != null && !props.isEmpty()) {
							Iterator iterator =	props.keySet().iterator();
							while (iterator.hasNext()) {
								alias = (String) iterator.next();
								if (alias.startsWith("web:")
										&& props.get(alias).equals(key)) {
									value = alias.substring("web:".length());
									modules[i].setContextRoot(value);
								}
							}
						}
						// in case the specified alias contains forbidden
						// character
						// it is replaced with the correct one
						props.remove("web:" + value);
						props.put("web:" + modules[i].getContextRoot(), key);
					}
				}
				break;
			}
		}
	}

	protected void setCompAndProviderNames() throws ServerDeploymentException {
		setAppName(descr.getProviderName(), descr.getDisplayName());
		if (getModuleID() == null) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.NOT_SPECIFIED_APP_NAME, new String[] {
							getModuleID(), getTransactionType() });
			sde.setMessageID("ASJ.dpl_ds.005012");
			throw sde;
		}
	}

	private ModuleProvider getModuleProvider() {
		ModuleProvider moduleProvider = null;
		if (descr != null) {
			final String tempDir = reader.getTempDir().getAbsolutePath();
			final List<ModuleInfo> all = new ArrayList<ModuleInfo>();
			all.addAll(getModuleInfos(descr.getEJBs(), tempDir));
			all.addAll(getModuleInfos(descr.getWEBs(), tempDir));
			all.addAll(getModuleInfos(descr.getConnectors(), tempDir));
			all.addAll(getModuleInfos(descr.getClients(), tempDir));
			final ModuleInfo mInfos[] = new ModuleInfo[all.size()];
			all.toArray(mInfos);
			moduleProvider = new ModuleProvider(mInfos);
		}
		return moduleProvider;
	}

	private List<ModuleInfo> getModuleInfos(J2EEModule temp[], String tempDir) {
		List<ModuleInfo> mInfos = new ArrayList<ModuleInfo>();
		if (temp == null || temp.length == 0) {
			return mInfos;
		}
		ModuleInfo mInfo = null;
		FileInfo fInfo = null;
		J2EEModule j2eeModule = null;
			String altDD = null;
			for (int i = 0; i < temp.length; i++) {
				j2eeModule = temp[i];
				altDD = j2eeModule.getAlt_dd();
				if (altDD != null) {
					fInfo = new FileInfo(FileType.ALT_DD, tempDir
							+ File.separator + altDD, altDD);
					mInfo = new ModuleInfo(j2eeModule.getType(), j2eeModule
							.getUri(), new FileInfo[] { fInfo });
					mInfos.add(mInfo);
				}
		}
		return mInfos;
	}

	protected abstract Hashtable<ContainerInterface, Properties> 
		getConcernedContainers(Hashtable<String, File[]> allContFiles,
			ContainerDeploymentInfo containerInfo) throws DeploymentException;

	protected abstract void makeComponents(File[] filesForContainer,
			ContainerDeploymentInfo containerInfo, Properties props,
			ContainerInterface cont) throws DeploymentException;

	protected void setProperties(Properties props) {
		this.props = (props == null ? new Properties() : props);
	}

	protected Properties getProperties() {
		return this.props;
	}

	/**
	 * TODO: The client of the deploy service has the responsibility to delete
	 * the supplied SDA archive. To reconsider the existence of this method.
	 */
	protected void cleanUpDeploymentFile() {
		long start = System.currentTimeMillis();
		long cpuStartTime = SystemTime.currentCPUTimeUs();
		final String tagName = "cleanUpDeploymentFile";
		try {
			Accounting.beginMeasure(tagName, File.class);
			if (moduleFile == null)
				return;
			if (moduleFile.getAbsolutePath().replace('\\', '/').indexOf(
					"deploy/work/deploying/") != -1
					&& moduleFile.exists()) {
				moduleFile.delete();
			}
		} finally {
			Accounting.endMeasure(tagName);
		}
		long end = System.currentTimeMillis();
		long cpuEndTime = SystemTime.currentCPUTimeUs();
		TransactionTimeStat.addClearFSOperation(new DeployOperationTimeStat(
				ITimeStatConstants.CLEAR_FS_CLEAN_UP_DEPLOYMENT_FILE,
				start, end, cpuStartTime, cpuEndTime));
	}

	protected void throwableInConstructor(Throwable e)
			throws DeploymentException {
		if (e instanceof OutOfMemoryError) {
			throw (OutOfMemoryError) e;
		}
		clearReader();
		cleanUpDeploymentFile();
		if (e instanceof DeploymentException) {
			throw (DeploymentException) e;
		} else if (e instanceof RuntimeException) {
			throw (RuntimeException) e;
		} else if (e instanceof Error) {
			throw (Error) e;
		}
		throw new DeploymentException("ASJ.dpl_ds.006017 "
				+ e.getMessage(), e);
	}
	
	protected void clearReader() {
		if (reader != null) {
			try {
				reader.clear();
			} catch (IOException ioEx) {
				SimpleLogger.traceThrowable(Severity.ERROR, location, 
					ioEx.getLocalizedMessage(), ioEx);
			}
		}		
	}

	// even if stop transaction is not needed, we remove the container wrapper from the cache 
	protected void updateContainerInCache(DeploymentInfo dInfo) throws ServerDeploymentException {
		try {
			super.removeContainerFromCacheIfAny();				
			// add containers-info.xml to deployment info
			reader.addContainerIfProvided(dInfo);
		} catch (IOException ioe) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.ERRORS_WHILE_READING_CONTAINERS_INFO_XML, new String[] {
							getModuleID()}, ioe);
			sde.setMessageID("ASJ.dpl_ds.005130");
			throw sde;
		}		
	}

	// for local transaction only
	protected void updateContainerInCacheLocal() {
		super.removeContainerFromCacheIfAny();
		DeploymentInfo dInfo = Applications.get(getModuleID());
		String contInfoXML = dInfo.getContainerInfoXML();
		// we provide containers-info.xml, therefore we update container's cache
		if (contInfoXML != null) { 
			InputStream is = new StringBufferInputStream(contInfoXML);
			Containers.getInstance().addContainers(is, 
				new Component(getModuleID(), Component.Type.APPLICATION));
		}
	}

}