/*
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
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.ear.Module;
import com.sap.engine.services.deploy.ear.exceptions.BaseIOException;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.ObjectSerializer;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.properties.impl.PropManagerImpl;
import com.sap.engine.services.deploy.server.utils.FSUtils;
import com.sap.lib.javalang.tool.ClassFinder;
import com.sap.lib.javalang.tool.ReadResult;
import com.sap.lib.javalang.tool.ReaderFactory;
import com.sap.lib.javalang.tool.exception.ReadingException;

/**
 * This class is intended only for internal use by deploy service.
 * 
 * @author Luchesar Cekov
 */
public abstract class DplArchiveReader {

	public static final String JAVA_EE_5 = "5";
	protected final static String meta_inf_application_j2ee_engine_xml = "META-INF/application-j2ee-engine.xml";
	protected final static String meta_inf_containers_info_xml = "META-INF/containers-info.xml";

	protected ZipFile zipFile;
	protected EarDescriptor descr = null;
	protected File tempDir = null;
	protected String filename = null;
	protected ClassLoader annotationClassLoader;

	@SuppressWarnings("unchecked")
	protected void parseAnnotations(ClassFinder aClassFinder)
			throws DeploymentException {
		try {
			ReadResult readResult = null;
			if (shouldParseAnnotations()) {
				readResult = new ReaderFactory(getAnnotationsClassLoader(),
						new HashMap(0), aClassFinder).getReader().read(
						new File[] { tempDir });
			} else {
				readResult = new ReaderFactory(getAnnotationsClassLoader(),
						new HashMap(0), aClassFinder).getReader().read(
						new File[] {});
			}
			descr.setAnnotations(readResult);
		} catch (ReadingException e) {
			throw new DeploymentException("ASJ.dpl_ds.006000 "
					+ e.getMessage(), e);
		}
	}

	protected boolean shouldParseAnnotations() {
		return (descr.getHasApplicationXML() && JAVA_EE_5.equals(descr
				.getApplicationJ2EEVersion()))
				|| !descr.getHasApplicationXML();
	}

	private ClassLoader getAnnotationsClassLoader() {
		if (annotationClassLoader == null) {
			PropManager properties = PropManager.getInstance();
			ApplicationServiceContext sc = properties.getAppServiceCtx();
			annotationClassLoader = sc.getCoreContext().getLoadContext()
					.getClassLoader(properties.getAnnotationClassloaderName());
			if (annotationClassLoader == null) {
				throw new IllegalArgumentException(
						"ASJ.dpl_ds.006001 There is no class loader with name "
								+ properties.getAnnotationClassloaderName()
								+ " defined by Deploy Service property \""
								+ PropManagerImpl.ANNOTATION_CLASS_LOADER_NAME);
			}
		}
		return annotationClassLoader;
	}

	/**
	 * Used for JUnit tests only.
	 */
	// TODO: Remove the method and use reflection in the JUnit test.
	public void setAnnotationClassLoader(ClassLoader c) {
		annotationClassLoader = c;
	}

	public File getTempDir() {
		return tempDir;
	}

	/**
	 * @param workDir
	 *            The root working directory, usually DeployService work
	 *            directory.
	 * @param archivePathName
	 *            The pathname of the archive.
	 * @return A temporary directory under the workDir, which name starts with
	 *         the file name of the archive, and ends with unique number.
	 */
	public static File constructTempDir(String workDir, String archivePathName) {
		return new File(FSUtils.getUniqueDir(workDir, new File(archivePathName)
				.getName()));
	}

	protected void addCalculatedResourcesToClassFinder() throws IOException {
		assert descr != null;
		for (Module m : descr.getAllModules()) {
			getClassFinder().addResource(m);
		}
	}

	public abstract EarDescriptor getDescriptor() throws IOException,
			DeploymentException;

	/**
	 * This method has to be called only when the locks for the corresponding
	 * transaction are acquired, in order to avoid collisions. Here we will
	 * parse the annotations, will extract modules and will ask all containers
	 * to generate their modules if needed.
	 * 
	 * @throws IOException
	 * @throws DeploymentException
	 */
	public abstract void read() throws IOException, DeploymentException;

	/**
	 * This method has to be called in order to clean temporary files and to
	 * release all used resources.
	 * 
	 * @throws IOException
	 */
	public abstract void clear() throws IOException;

	public abstract BcaClassFinder getClassFinder() throws IOException;

	/**
	 * Finds a zip entry with the given name.
	 * @param entryName the name of entry. Must not be null.
	 * @return the corresponding zip entry or <tt>null</tt> if such entry 
	 * cannot be found.
	 */
	protected ZipEntry findEntry(String entryName) {
		assert entryName != null;
		if (zipFile != null) {
			for (Enumeration<? extends ZipEntry> entries = zipFile.entries(); 
				entries.hasMoreElements();) {
				final ZipEntry zipEntry = entries.nextElement();
				if (zipEntry.getName().equalsIgnoreCase(entryName)) {
					return zipEntry;
				}
			}
		}
		return null;
	}

	public void addContainerIfProvided(DeploymentInfo dInfo) throws IOException {
		final ZipEntry entry = findEntry(meta_inf_containers_info_xml);
		try {
			if (entry != null) {
				String componentName = descr.getProviderName() + "/"
						+ descr.getDisplayName();
				Containers.getInstance().addContainers(
					zipFile.getInputStream(entry),
					new Component(componentName, Component.Type.APPLICATION));
				// set it to DeploymentInfo
				final InputStream is = zipFile.getInputStream(entry);
				try {
					final byte contInfoXml[] = ObjectSerializer.read(is);
					dInfo.setContainerInfoXML(new String(contInfoXml));
				} finally {
					ObjectSerializer.close(is);
				}
			}
		} catch (IOException ex) {
			// Used to add additional message to the thrown exception.
			throw new BaseIOException(
				ExceptionConstants.ERRORS_WHILE_PARSING_CONTAINERS_INFO_XML,
				ex);
		}
	}
}