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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import com.sap.engine.lib.converter.DescriptorParseTool;
import com.sap.engine.lib.io.FileUtils;
import com.sap.engine.services.deploy.DeployService;
import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.ear.EARExceptionConstants;
import com.sap.engine.services.deploy.ear.J2EEModule;
import com.sap.engine.services.deploy.ear.Module;
import com.sap.engine.services.deploy.ear.exceptions.BaseIOException;
import com.sap.engine.services.deploy.ear.exceptions.BaseWrongStructureException;
import com.sap.engine.services.deploy.ear.jar.modulematch.ModuleSource;
import com.sap.engine.services.deploy.ear.modules.Connector;
import com.sap.engine.services.deploy.ear.modules.EJB;
import com.sap.engine.services.deploy.ear.modules.Java;
import com.sap.engine.services.deploy.ear.modules.Web;
import com.sap.engine.services.deploy.ear.modules.extract.IExtractable;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.DUtils;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.server.dpl_info.module.ApplicationName;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.tc.logging.Location;

/**
 * This class is intended only for internal use by deploy service.
 * 
 * @author Luchesar Cekov
 */
public class StandaloneModuleReader extends DplArchiveReader implements
		ModuleSource {
	
	private static final Location location = 
		Location.getLocation(StandaloneModuleReader.class);
	private static final Containers containers = Containers.getInstance();

	private File standaloneModuleFile;
	private Properties deployProperties;
	private String dedicatedContainerName;
	private BcaClassFinder classFinder;

	public StandaloneModuleReader(File aStandaloneModuleFile,
			Properties aProperties) throws IOException {
		this(aStandaloneModuleFile, null, new File(PropManager.getInstance()
				.getServiceWorkDir()), aProperties);
	}

	public StandaloneModuleReader(File aStandaloneModuleFile,
			String aDedicatedContainerName, Properties aProperties)
			throws IOException {
		this(aStandaloneModuleFile, aDedicatedContainerName, new File(
				PropManager.getInstance().getServiceWorkDir()), aProperties);
	}

	public StandaloneModuleReader(File aStandaloneModuleFile, File aTempDir,
			Properties aProperties) throws IOException {
		this(aStandaloneModuleFile, null, aTempDir, aProperties);
	}

	private StandaloneModuleReader(File aStandaloneModuleFile,
			String aDedicatedContainerName, File aTempDir,
			Properties aProperties) throws IOException {
		deployProperties = aProperties == null ? new Properties() : aProperties;
		filename = getFileName(aStandaloneModuleFile);
		tempDir = constructTempDir(aTempDir.getAbsolutePath(), filename);
		standaloneModuleFile = new File(tempDir, filename);
		FileUtils.copyFileUsingNIO(aStandaloneModuleFile, standaloneModuleFile);
		dedicatedContainerName = aDedicatedContainerName;
		try {
			zipFile = new ZipFile(aStandaloneModuleFile);
		} catch (ZipException ze) {
			DSLog.traceDebugThrowable(location, null, ze, "ASJ.dpl_ds.004015",
					"Unable to open file [{0}].", aStandaloneModuleFile);
			zipFile = null;

		}
	}

	@Override
	public EarDescriptor getDescriptor() 
		throws IOException, DeploymentException {
		return descr != null ? descr : (descr = initDescriptor());
	}

	private String getFileName(File aStandaloneModuleFile) {
		String swtSubType = deployProperties
				.getProperty(DeployService.softwareSubType);
		String originalName = aStandaloneModuleFile.getName();
		if (swtSubType != null && swtSubType.length() > 0) {
			int dotIndex = originalName.lastIndexOf('.');
			if (dotIndex > -1) {
				return new StringBuilder(originalName).replace(dotIndex + 1,
						originalName.length(), swtSubType).toString();
			}
			return new StringBuilder(originalName).append(".").append(
					swtSubType).toString();
		}
		return originalName;
	}

	private EarDescriptor initDescriptor() throws IOException, DeploymentException {
		final EarDescriptor result = new EarDescriptor();
		result.setApplicationJ2EEVersion(JAVA_EE_5);
		result.setHasApplicationXML(false);

		if (zipFile != null) {
			final ZipEntry entry = 
				findEntry(meta_inf_application_j2ee_engine_xml);
			try {
				final DescriptorParseTool parser = 
					DescriptorParseTool.getInstance();
				if (entry != null) {
					EarDescriptorPopulator.populateFromApplicationEngineXML(
							getTempDir(), result, parser.parseApplicationJ2ee(
									zipFile.getInputStream(entry), true));

					if (result.getAdditionalModules() != null
							&& !result.getAdditionalModules().isEmpty()) {
						throw new BaseWrongStructureException(
								EARExceptionConstants.ERRORS_PARSING_J2EE_XML_STANDALONE,
								new Object[] { standaloneModuleFile
										.getAbsolutePath() });
					}
				}
			} catch (Exception e) {
				throw new BaseIOException(
						EARExceptionConstants.ERRORS_WHILE_PARSING_J2EE_XML, e);
			}
		}
		extractApplicationName(result);
		return result;
	}

	@Override
	public void read() throws IOException, DeploymentException {
		// Used to trigger the EarDescriptor initialization.
		getDescriptor();

		final String swtSubType = deployProperties
				.getProperty(DeployService.softwareSubType);
		if (filename.endsWith(".war")
				|| (swtSubType != null && swtSubType.equals("war"))) {
			Web.extractWarClassPath(standaloneModuleFile);
		}
		
		parseAnnotations(getClassFinder());
		if (dedicatedContainerName != null
				&& containers.getContainer(dedicatedContainerName) != null) {
			createModule(containers.getContainer(dedicatedContainerName)
					.getContainerInfo());
		} else {
			findContainersSupportingSoftType();
			new ModuleGeneratorTool(tempDir, this, descr).generateModules();
		}
		extractModule();
		addCalculatedResourcesToClassFinder();
	}

	
	@Override
	public void clear() throws IOException {
		if (classFinder != null) {
			classFinder.clear();
		}
		if (tempDir.exists()) {
			DUtils.deleteDirectory(tempDir);
		}
		if (zipFile != null) {
			zipFile.close();
		}
	}

	private void extractModule() throws IOException {
		for (Module m : descr.getAllModules()) {
			if (m instanceof IExtractable) {
				((IExtractable) m).extract();
			}
		}
	}

	private void findContainersSupportingSoftType() throws IOException {
		final String swtType = deployProperties
				.getProperty(DeployService.softwareType);
		final String swtSubType = deployProperties
				.getProperty(DeployService.softwareSubType);
		if (swtType != null || swtSubType != null) {
			Iterator containersIterator = containers.getAll().iterator();
			ContainerInterface cont = null;
			ContainerInfo cInfo = null;
			while (containersIterator.hasNext()) {
				cont = (ContainerInterface) containersIterator.next();
				cInfo = cont.getContainerInfo();
				if (cInfo.isSoftwareTypeSupported(swtType, swtSubType)) {
					createModule(cInfo);
				}
			}
			// if the software type and software subtype are not supported by
			// any of the containers
			// check if the files are supported
			if (descr.getAllModules().size() == 0) {
				InputStream input = null;
				ZipInputStream zip = null;
				try {
					HashSet<String> entries = new HashSet<String>();
					input = new FileInputStream(standaloneModuleFile);
					zip = new ZipInputStream(input);
					ZipEntry entry = zip.getNextEntry();
					while (entry != null) {
						entries.add(entry.getName());
						entry = zip.getNextEntry();
					}
					containersIterator = containers.getAll().iterator();
					cont = null;
					cInfo = null;
					while (containersIterator.hasNext()) {
						cont = (ContainerInterface) containersIterator.next();
						cInfo = cont.getContainerInfo();
						if (cInfo.getFileNames() != null) {
							Iterator entriesIterator = entries.iterator();
							final String[] supportedFileNames = cInfo
									.getFileNames();
							String entryName = null;
							boolean isModuleCreated = false;
							while (entriesIterator.hasNext()
									&& !isModuleCreated) {
								entryName = (String) entriesIterator.next();
								for (int i = 0; i < supportedFileNames.length; i++) {
									if (supportedFileNames[i].equals(entryName)) {
										createModule(cInfo);
										isModuleCreated = true;
										break;
									}
								}
							}
						}
					}
				} finally {
					if (zip != null) {
						zip.close();
					}
					if (input != null) {
						input.close();
					}
				}
			}
		}
	}

	private void createModule(ContainerInfo cInfo) {
		if (cInfo.isJ2EEContainer()) {
			if (J2EEModule.Type.ejb.name().equals(cInfo.getJ2EEModuleName())) {
				descr.addModule(new EJB(tempDir, filename));
			} else if (J2EEModule.Type.web.name().equals(
					cInfo.getJ2EEModuleName())) {
				int index = filename.toLowerCase().indexOf(".war");
				if (index > 0) {
					descr.addModule(new Web(tempDir, filename, filename
							.substring(0, index)));
				} else {
					descr.addModule(new Web(tempDir, filename, filename));
				}
			} else if (J2EEModule.Type.connector.name().equals(
					cInfo.getJ2EEModuleName())) {
				descr.addModule(new Connector(tempDir, filename));
			} else if (J2EEModule.Type.java.name().equals(
					cInfo.getJ2EEModuleName())) {
				descr.addModule(new Java(tempDir, filename));
			}
		} else {
			descr.addModule(new Module(tempDir, filename, cInfo.getName()));
		}
	}


	@SuppressWarnings("deprecation")
	private void extractApplicationName(final EarDescriptor result) {
		final String name = 
			deployProperties.getProperty(DeployService.applicationProperty);
		if(name != null) {
			result.setDisplayName(name);
		} else {
			result.setDisplayName(filename);
		}
		final String provider = 
			deployProperties.getProperty(DeployService.providerProperty);
		if(provider != null) {
			result.setProviderName(provider);
		}
	}

	public boolean containsModuleFile(String aRelativeFilePath) {
		return filename.equals(aRelativeFilePath);
	}

	public String[] listModuleFileNames() {
		return new String[] { filename };
	}

	public BcaClassFinder getClassFinder() throws IOException {
		if (classFinder == null) {
			classFinder = new BcaClassFinder(descr);
		}
		return classFinder;
	}
}