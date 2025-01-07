/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.deploy.ear.jar;

import static com.sap.engine.services.deploy.timestat.ITimeStatConstants.*;
import static com.sap.engine.services.deploy.ear.EARExceptionConstants.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.sap.engine.services.deploy.server.ObjectSerializer;
import com.sap.engine.lib.converter.ConversionContext;
import com.sap.engine.lib.converter.ConversionException;
import com.sap.engine.lib.converter.ConverterTool;
import com.sap.engine.lib.converter.DescriptorParseTool;
import com.sap.engine.lib.converter.IJ2EEDescriptorConverter;
import com.sap.engine.lib.converter.impl.ApplicationConverter;
import com.sap.engine.lib.descriptors5.application.ApplicationType;
import com.sap.engine.lib.processor.SchemaProcessor;
import com.sap.engine.lib.processor.SchemaProcessorFactory;
import com.sap.engine.lib.time.SystemTime;
import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.deploy.DeployService;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.ear.J2EEModule;
import com.sap.engine.services.deploy.ear.Module;
import com.sap.engine.services.deploy.ear.exceptions.BaseIOException;
import com.sap.engine.services.deploy.ear.jar.modulematch.ModuleSource;
import com.sap.engine.services.deploy.ear.modules.Web;
import com.sap.engine.services.deploy.ear.modules.extract.Extractor;
import com.sap.engine.services.deploy.ear.modules.extract.IExtractable;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.DUtils;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.timestat.DeployOperationTimeStat;
import com.sap.engine.services.deploy.timestat.TransactionTimeStat;
import com.sap.tc.logging.Location;

/**
 * Class for reading EARDescriptor from ear file and retrieving all the archive
 * files that are in the ear by their types. The class is intended only for
 * internal use by deploy service. Copyright (c) 2003, SAP-AG
 * 
 * @author Luchesar Cekov
 * @version
 */
public class EARReader extends DplArchiveReader implements ModuleSource {
	
	private static final Location location = 
		Location.getLocation(EARReader.class);

	private final Properties props;

	private final static String meta_inf_application_xml = "META-INF/application.xml";

	private BcaClassFinder classFinder;

	/**
	 * Constructs EARReader object from given file name of EAR file.
	 * 
	 * @param _filename
	 *            the name of EAR file.
	 * 
	 * @exception IOException
	 *                If the file does not exist.
	 */
	public EARReader(String _filename, Properties aProps) throws IOException {
		this(_filename, constructTempDir(PropManager.getInstance()
				.getServiceWorkDir(), _filename), aProps);
	}

	/**
	 * Constructs EARReader object from given file name of EAR file and temp
	 * directory.
	 * 
	 * @param _filename
	 *            the name of EAR file.
	 * @param _tempDir
	 *            the temp directory.
	 * 
	 * @throws IOException
	 *             If the file does not exist.
	 */
	public EARReader(String _filename, File _tempDir, Properties aProps)
			throws IOException {
		filename = _filename;
		tempDir = _tempDir;
		tempDir.mkdirs();
		zipFile = new ZipFile(filename);
		props = aProps == null ? new Properties() : aProps;
	}

	/**
	 * Gets the name of EAR file.
	 * 
	 * @return the EAR file name.
	 */
	public String getJarName() {
		return filename;
	}

	/**
	 * Deletes the temp directory.
	 * 
	 * @throws IOException
	 */
	@Override
	public void clear() throws IOException {
		long start = System.currentTimeMillis();
		long cpuStartTime = SystemTime.currentCPUTimeUs();
		Accounting.beginMeasure(EAR_READ_CLEAR, getClass());
		try {
			zipFile.close();
			if (classFinder != null) {
				classFinder.clear();
			}
			if (tempDir.exists()) {
				DUtils.deleteDirectory(tempDir);
			}
		} finally {
			Accounting.endMeasure(EAR_READ_CLEAR);
		}
		long end = System.currentTimeMillis();
		long cpuEndTime = SystemTime.currentCPUTimeUs();
		TransactionTimeStat.addEarReaderOperation(new DeployOperationTimeStat(
				EAR_READ_CLEAR, start, end, cpuStartTime, cpuEndTime));
	}

	@Override
	public BcaClassFinder getClassFinder() throws IOException {
		if (classFinder == null) {
			classFinder = new BcaClassFinder(descr);
		}
		return classFinder;
	}

	/**
	 * Gets EAR descriptor from application xml file(s).
	 * 
	 * @return SimpleEarDescriptor for this ear file.
	 * 
	 * @exception IOException
	 *                if there is no XML file or structure is incorrect.
	 */
	@Override
	public EarDescriptor getDescriptor() throws IOException,
			DeploymentException {
		long start = System.currentTimeMillis();
		long cpuStartTime = SystemTime.currentCPUTimeUs();
		Accounting.beginMeasure(EAR_READ_GET_DESCRIPTOR, getClass());
		try {
			return descr != null ? descr : (descr = initDescriptor());
		} finally {
			Accounting.endMeasure(EAR_READ_GET_DESCRIPTOR);
			long end = System.currentTimeMillis();
			long cpuEndTime = SystemTime.currentCPUTimeUs();
			TransactionTimeStat
					.addEarReaderOperation(new DeployOperationTimeStat(
							EAR_READ_GET_DESCRIPTOR, start, end, cpuStartTime,
							cpuEndTime));
		}
	}

	@SuppressWarnings("deprecation")
	private EarDescriptor initDescriptor() throws IOException {
		final DescriptorParseTool parser = DescriptorParseTool.getInstance();
		final EarDescriptor result = new EarDescriptor();

		ZipEntry entry = findEntry(meta_inf_application_xml);
		if (entry == null) {
			result.setHasApplicationXML(false);
			result.setApplicationJ2EEVersion(JAVA_EE_5);
		} else {
			try {
				EarDescriptorPopulator.populateFromApplicationXML(getTempDir(),
						result, parseApplicationXML(zipFile
								.getInputStream(entry), true));
			} catch (Exception e) {
				throw new BaseIOException(ERRORS_WHILE_PARSING_APP_XML, e);
			}
		}

		entry = findEntry(meta_inf_application_j2ee_engine_xml);
		try {
			if (entry != null) {
				EarDescriptorPopulator.populateFromApplicationEngineXML(
						getTempDir(), result, parser.parseApplicationJ2ee(
								zipFile.getInputStream(entry), true));
			}
		} catch (Exception e) {
			throw new BaseIOException(ERRORS_WHILE_PARSING_J2EE_XML, e);
		}
		extractApplicationName(result);
		return result;
	}

	@Override
	public void read() throws DeploymentException, IOException {
		// Used to trigger the EarDescriptor initialization.
		getDescriptor();

		extractFiles();

		parseAnnotations(getClassFinder());
		new ModuleGeneratorTool(tempDir, this, getDescriptor())
				.generateModules();
		extractModuleFiles();
		addCalculatedResourcesToClassFinder();
	}

	@SuppressWarnings("deprecation")
	protected void extractApplicationName(final EarDescriptor descr) {
		assert descr != null;
		String realCompName = props
				.getProperty(DeployService.applicationProperty);
		String realProviderName = props
				.getProperty(DeployService.providerProperty);

		if (realCompName == null) {
			if (location.beInfo()) {
				DSLog
						.traceInfo(
								location, 
								"ASJ.dpl_ds.003001",
								"Property for application name is not specified. Setting display name from application.xml as application name.");
			}
		} else {
			descr.setDisplayName(realCompName);
		}

		if (realProviderName == null) {
			if (DSLog.isInfoTraceable()) {
				DSLog.traceInfo(location, "ASJ.dpl_ds.003002",
						"Property for provider name is not specified.");
			}
		} else {
			descr.setProviderName(realProviderName);
		}
	}

	// for Java EE 5 - until a central replacement of descriptors is done
	private ApplicationType parseApplicationXML(InputStream in, boolean validate)
			throws ConversionException, IOException, SAXException,
			TransformerException {
		ApplicationType appType = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ByteArrayInputStream bis = null;
		try {
			ConversionContext ctx = new ConversionContext(null, validate);
			ctx.setAttribute(ConversionContext.FORGIVING_ATTR, Boolean.TRUE);
			ctx.setInputStream(ApplicationConverter.APPLICATION_FILENAME, in);
			ConverterTool.getInstance().convert(
					IJ2EEDescriptorConverter.APPLICATION, ctx);
			Document appDoc = ctx
					.getConvertedDocument(ApplicationConverter.APPLICATION_FILENAME);
			bos = new ByteArrayOutputStream();
			(TransformerFactory.newInstance()).newTransformer().transform(
					new DOMSource(appDoc), new StreamResult(bos));
		} finally {
			bos.close();
		}
		try {
			byte[] xml = bos.toByteArray();
			SchemaProcessor appProcessor = SchemaProcessorFactory
					.getProcessor(SchemaProcessorFactory.APP5);
			appProcessor.switchOffValidation();
			bis = new ByteArrayInputStream(xml);
			appType = (ApplicationType) appProcessor.parse(bis);
		} finally {
			if (bis != null) {
				bis.close();
			}
		}
		return appType;
	}

	private void extractFiles() throws IOException, DeploymentException {
		// extracts all files from the archive of the application
		for (Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries
				.hasMoreElements();) {
			ZipEntry entry = entries.nextElement();
			if (!entry.isDirectory()) {
				Extractor.extractFile(zipFile, tempDir, entry.getName());
				if (entry.getName().endsWith(".war")) {
					// we need to extract .war in order to parse the annotations
					// if the module is missing from the descriptors
					Web.extractWarClassPath(new File(tempDir, entry.getName()));
				}
			}
		}

		for (Module module : descr.getAllModules()) {
			if (findEntry(module.getUri()) == null) {
				final String module4print = (module instanceof J2EEModule ? ((J2EEModule) module)
						.getType().name()
						: module.getModuleType());
				ServerDeploymentException sde = new ServerDeploymentException(
						ExceptionConstants.ERROR_IN_READING_APP_MODULES,
						new Object[] { module.getUri(), filename, module4print,
								meta_inf_application_xml,
								meta_inf_application_j2ee_engine_xml,
								(new File(filename)).getName() });
				sde.setMessageID("ASJ.dpl_ds.005042");
				throw sde;
			}
			// if the module is .war it is already extracted
			if (module instanceof IExtractable && !(module instanceof Web)) {
				((IExtractable) module).extract();
			}
		}
	}

	private void extractModuleFiles() throws IOException {
		for (Module module : descr.getAllModules()) {
			if (module instanceof IExtractable) {
				((IExtractable) module).extract();
			}
		}
	}

	/**
	 * Read entry as byte array.
	 * @param entryName the entry name. Must not be null.
	 * @return byte array, with the content of the entry or <tt>null</tt> if
	 * such entry does not exists. 
	 * @throws IOException
	 */
	public byte[] readEntryAsByteArray(final String entryName) 
		throws IOException {
		final ZipEntry entry = findEntry(entryName);
		if (entry == null) {
			return null;
		}

		final InputStream is = zipFile.getInputStream(entry);
		try {
			return ObjectSerializer.read(is);
		} finally {
			ObjectSerializer.close(is);
		}
	}

	/**
	 * Extracts file with the specified name from the ear file.
	 * 
	 * @param fileName
	 *            the file to be extracted (ignoring case sensitivity).
	 * 
	 * @return the extracted file.
	 * 
	 * @throws IOException
	 *             if a problem occurs during extracting.
	 */
	public File extractFileNonCaseSensitive(String fileName) throws IOException {
		if (fileName == null) {
			return null;
		}

		final Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			final ZipEntry zipEntry = entries.nextElement();
			if (zipEntry.getName().equalsIgnoreCase(fileName)) {
				return Extractor.extractFile(zipFile, tempDir, zipEntry
						.getName());
			}
		}
		return null;
	}

	public boolean containsModuleFile(String aRelativeFilePath) {
		return zipFile.getEntry(aRelativeFilePath) != null;
	}

	public String[] listModuleFileNames() {
		String[] result = new String[zipFile.size()];
		int i = 0;
		for (Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries
				.hasMoreElements();) {
			result[i++] = entries.nextElement().getName();
		}
		return result;
	}
}
