/* 
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.deploy.sda;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.sap.engine.lib.deploy.sda.constants.Constants;
import com.sap.engine.lib.deploy.sda.exceptions.DeployLibException;
import com.sap.engine.lib.deploy.sda.exceptions.ExceptionConstants;
import com.sap.engine.lib.deploy.sda.exceptions.FileNotJarCompatibleException;
import com.sap.engine.lib.deploy.sda.logger.Logger;
import com.sap.engine.lib.deploy.sda.propertiesholder.PropertiesHolder;
import com.sap.engine.lib.io.FileUtils;
import com.sap.engine.lib.jar.JarExtractor;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Location;
import com.sap.sdm.ant.api.AttributeIF;
import com.sap.sdm.ant.api.ComponentIF;
import com.sap.sdm.ant.api.JarSAPFactory;
import com.sap.sdm.ant.api.JarSAPIF;
import com.sap.sdm.ant.api.JarSAPException;
import com.sap.sdm.ant.api.ManifestIF;
import com.sap.sdm.ant.api.DependencyIF;

/**
 * @author Mariela Todorova
 */
public class SDAProducer implements Constants {
	private static final Location location = Location
			.getLocation(SDAProducer.class);
	private static final String MAIN_CLASS = "Main-Class";

	private String context = null;
	private String archive = null;
	private String workDir = null;
	private String destination = null;
	private String fileName = null;
	private File deployFile = null;
	private SDADescriptor descriptor = null;

	public SDAProducer(String archiveFile) {

		PropertiesHolder.init();
		try {
			Logger.initLogging();
		} catch (IOException e) {
			throw new RuntimeException(
					"An IOException was thrown while initializing the logging",
					e);
		}

		Logger.log(location, Severity.INFO, "Generating SDA file for "
				+ archiveFile);
		archive = archiveFile;
	}

	public void setWorkDir(String dir) {
		Logger.trace(location, Severity.DEBUG, "Set work dir " + dir);
		workDir = dir;
	}

	public void setDestinationFile(String dest) {
		Logger.trace(location, Severity.DEBUG, "Set destination file " + dest);
		destination = dest;
	}

	public String getDestinationFile() {
		return destination;
	}

	public void setDescriptor(SDADescriptor descr) {
		Logger.trace(location, Severity.DEBUG, "Set descriptor " + descr);
		descriptor = descr;
	}

	private void init() throws DeployLibException, SAXException, IOException {
		if (archive == null || archive.equals("")) {
			Logger.log(location, Severity.ERROR, "No source file specified");
			throw new DeployLibException(location,
					ExceptionConstants.NO_SOURCE_FILE);
		}

		archive = archive.replace('/', File.separatorChar);
		archive = archive.replace('\\', File.separatorChar);

		if (!(new File(archive)).isFile()) {
			Logger.log(location, Severity.ERROR, "Could not find file "
					+ archive);
			throw new DeployLibException(location,
					ExceptionConstants.NO_SOURCE_FILE);
		}

		try {
			new JarFile(archive);
		} catch (IOException ioe) {
			Logger.logThrowable(location, Severity.INFO, "File " + archive
					+ " is not in JAR compatible format", ioe);
			throw new FileNotJarCompatibleException(location,
					ExceptionConstants.NOT_JAR_COMPATIBLE_FORMAT,
					new String[] { archive }, ioe);
		}

		initWorkDir();
		fileName = archive.substring(archive.lastIndexOf(sep) + 1);
		initDestination();
		initDescriptor();
	}

	private void initWorkDir() {
		if (workDir == null || workDir.equals("")) {
			workDir = PropertiesHolder
					.getProperty(PropertiesHolder.CTS_WORK_DIR);

			if (workDir == null || !(new File(workDir)).isDirectory()) {
				workDir = PropertiesHolder
						.getProperty(PropertiesHolder.WORK_DIR);

				if (workDir == null || !(new File(workDir)).isDirectory()) {
					workDir = "..";
				}
			}
		}

		workDir = workDir.replace('/', File.separatorChar);
		workDir = workDir.replace('\\', File.separatorChar);
		workDir = workDir + sep + SDA;
		Logger.trace(location, Severity.DEBUG, "Initialized work dir "
				+ workDir);
	}

	private void initDestination() throws IOException {
		try {
			if (destination == null || destination.equals("")) {
				destination = workDir + sep + fileName;
			}

			destination = destination.replace('/', File.separatorChar);
			destination = destination.replace('\\', File.separatorChar);

			if (destination.indexOf(sep) == -1) {
				destination = "." + sep + destination;
			}

			FileUtils.copy(new File(archive), destination);
		} catch (IOException ioe) {// $JL-EXC$
			Logger.log(location, Severity.WARNING,
					"Could not copy source file to intended destination due to "
							+ ioe.getMessage());
			Logger.log(location, Severity.WARNING,
					"Source file will be overwritten.");
			destination = archive;
		}

		Logger.trace(location, Severity.DEBUG, "Initialized destination file "
				+ destination);
		workDir = workDir + sep
				+ fileName.substring(0, fileName.lastIndexOf('.')) + "_"
				+ System.currentTimeMillis();

		try {
			(new JarExtractor()).extractJar(destination, workDir);
		} catch (IOException ioe) {
			Logger.log(location, Severity.ERROR, "Could not extract "
					+ destination + " to " + workDir + " due to "
					+ ioe.getMessage());
			throw ioe;
		}

	}

	private void initDescriptor() throws DeployLibException, SAXException,
			IOException {

		AttributesDeterminant determinant = new AttributesDeterminant(fileName,
				workDir);

		if (descriptor == null) {
			descriptor = new SDADescriptor();
		}

		if (descriptor.getType() == null) {
			descriptor.setType(determinant.determineType());

			descriptor.setSubType(determinant.determineSubType(descriptor
					.getType()));

		} else if (descriptor.getType().equals(SoftwareType.SINGLE_MODULE)
				&& descriptor.getSubType() == null) {
			descriptor.setSubType(determinant
					.determineSubType(SoftwareType.SINGLE_MODULE));
		}

		Logger.trace(location, Severity.INFO, "SDA type "
				+ descriptor.getType());
		Logger.trace(location, Severity.INFO, "SDA subtype "
				+ descriptor.getSubType());

		if (descriptor.getName() == null) {
			descriptor.setName(determinant.determineName(descriptor.getType()));
		}

		Logger.trace(location, Severity.INFO, "SDA name "
				+ descriptor.getName());

		if (descriptor.getVendor() == null) {
			descriptor.setVendor(determinant.determineVendor(descriptor
					.getType()));
		}

		Logger.trace(location, Severity.INFO, "SDA vendor "
				+ descriptor.getVendor());

		if (descriptor.getLocation() == null) {
			descriptor.setLocation(determinant.determineLocation());
		}

		Logger.trace(location, Severity.INFO, "SDA location "
				+ descriptor.getLocation());

		if (descriptor.getCounter() == null) {
			descriptor.setCounter(determinant.determineCounter());
		}

		Logger.trace(location, Severity.INFO, "SDA counter "
				+ descriptor.getCounter());
	}

	private void createDeployFile() throws DeployLibException, IOException {
		DeployFile dFile = new DeployFile(descriptor.getType());
		String fileName = workDir + sep + META_INF + sep + SDA_XML;
		File file = new File(fileName);
		file.getParentFile().mkdirs();
		try {
			dFile.make(file.getAbsolutePath());
		} catch (ParserConfigurationException e) {
			throw new DeployLibException(location, e);
		} catch (TransformerException e) {
			throw new DeployLibException(location, e);
		}
		deployFile = file;
	}

	private void setRuntimeDependencies() throws DeployLibException,
			IOException, SAXException {
		if (null == descriptor.getRuntimeDependencies()) {
			return;
		}
		RuntimeDependencyFile runtimeDependencyFile = new RuntimeDependencyFile(
				descriptor.getRuntimeDependencies(), workDir + File.separator
						+ META_INF);
		try {
			runtimeDependencyFile.create();
		} catch (ParserConfigurationException e) {
			throw new DeployLibException(location, e);
		} catch (TransformerException e) {
			throw new DeployLibException(location, e);
		}
	}

	private void setManifestData() throws DeployLibException, IOException {

		JarSAPIF jarsap = JarSAPFactory.getInstance().createJarSAP(null);
		ComponentIF componentdef = jarsap.createComponent();
		jarsap.setSoftwaretype(descriptor.getType().getValue());
		if (null != descriptor.getSubType()) {
			jarsap.setSoftwaresubtype(descriptor.getSubType().getValue());
		} else {
			jarsap.setSoftwaresubtype(null);
		}

		if (SoftwareType.J2EE.equals(descriptor.getType())) {
			String value = prepareAliasesAttrValue();

			if (value != null) {
				ManifestIF manifest = jarsap.createManifest();
				AttributeIF attr = manifest.createAttribute();
				attr.setName(CONTEXT_ROOTS);
				attr.setValue(value);
			}
		} else if (SoftwareType.SINGLE_MODULE.equals(descriptor.getType())) {
			if (SoftwareSubType.JAR.equals(descriptor.getSubType())) {
				String mainClass = getMainClass();
				if (mainClass != null) {
					jarsap.setMainclass(mainClass);
					Logger.trace(location, Severity.DEBUG, "Main-Class: "
							+ mainClass);
				}
			} else if (SoftwareSubType.WAR.equals(descriptor.getSubType())) {
				Properties aliases = descriptor.getAliases();

				if (aliases != null) {
					String alias = aliases.getProperty(fileName);

					if (alias != null && !alias.equals("")) {
						ManifestIF manifest = jarsap.createManifest();
						AttributeIF attr = manifest.createAttribute();
						attr.setName(CONTEXT_ROOT);
						attr.setValue(alias);
					}
				}
			}
		}

		componentdef.setCounter(descriptor.getCounter());
		componentdef.setName(descriptor.getName());
		componentdef.setVendor(descriptor.getVendor());
		componentdef.setLocation(descriptor.getLocation());

		// definition of the dependent DCs
		ArrayList<Dependency> dependencies = descriptor.getDependencies();
		Dependency dep = null;
		DependencyIF dependency = null;

		if (dependencies != null) {
			for (int i = 0; i < dependencies.size(); i++) {
				dep = (Dependency) dependencies.get(i);
				dependency = jarsap.createDependency();
				dependency.setVendor(dep.getVendor());
				dependency.setName(dep.getName());
			}
		}

		// sda is compressed
		jarsap.setCompress(TRUE);

		// definition of the SDM deployfile
		jarsap.setDeployfile(deployFile.getAbsolutePath());

		// definition of the archivename to be created
		jarsap.setJarfile(destination);

		// pack directory content
		jarsap.createANTFileSet(workDir);

		// create the file
		try {
			jarsap.execute();
		} catch (JarSAPException jse) {
			throw new DeployLibException(location,
					ExceptionConstants.COULD_NOT_MAKE_SDA,
					new String[] { destination }, jse);
		}
	}

	private String getMainClass() throws IOException {
		File manFile = new File(workDir + sep + META_INF + sep + MANIFEST);
		FileInputStream fis = null;
		String mainClass = null;

		if (!manFile.isFile()) {
			Logger.log(location, Severity.WARNING,
					"Could not find META-INF/MANIFEST.MF");
			return mainClass;
		}

		if (manFile.canRead()) {
			try {
				fis = new FileInputStream(manFile);
				Manifest manifest = new Manifest(fis);
				Attributes attr = manifest.getMainAttributes();

				if (attr == null || attr.isEmpty()) {
					Logger.log(location, Severity.WARNING,
							"No main attributes in META-INF/MANIFEST.MF");
					return mainClass;
				}

				mainClass = attr.getValue(MAIN_CLASS);
			} catch (IOException ioe) {// $JL-EXC$
				Logger.logThrowable(location, Severity.DEBUG,
						"Could not load META-INF/MANIFEST.MF", ioe);
				throw ioe;
			} finally {
				try {
					fis.close();
				} catch (IOException ioex) {// $JL-EXC$
					Logger.trace(location, Severity.DEBUG,
							"Could not close stream due to "
									+ ioex.getMessage());
				}
			}
		}

		return mainClass;
	}

	private String prepareAliasesAttrValue() {
		String value = null;
		Properties aliases = descriptor.getAliases();

		if (aliases == null || aliases.isEmpty()) {
			return value;
		}

		StringBuffer attrValue = new StringBuffer();
		Iterator<Object> iterator = aliases.keySet().iterator();
		String webModule = null;

		while (iterator.hasNext()) {
			webModule = (String) iterator.next();
			attrValue.append(webModule);
			attrValue.append(":");
			attrValue.append(aliases.get(webModule));
			attrValue.append("::");
		}

		attrValue.delete(attrValue.length() - 2, attrValue.length());
		value = attrValue.toString();
		return value;
	}

	/**
	 * Returns an SDADescriptor object which represents the sda properties. The
	 * object can be shared between several sda files.
	 * 
	 * @param archive
	 *            the archive path, it is used to set the alias property of the
	 *            SDADescriptor
	 * 
	 * @param propertiesFile
	 * @return
	 * @throws IOException
	 * @throws DeployLibException
	 */
	private SDADescriptor processSDAProperties(String archive,
			String propertiesFile) throws IOException, DeployLibException {
		SDADescriptor descr = null;

		if (propertiesFile != null && !propertiesFile.equals("")) {
			File propsFile = new File(propertiesFile);

			if (propsFile.isFile() && propsFile.canRead()) {
				try {
					Properties props = new Properties();
					FileInputStream inStream = new FileInputStream(propsFile);
					props.load(inStream);
					descr = new SDADescriptor();
					descr.setType(props.getProperty(SOFTWARE_TYPE));
					String sub = props.getProperty(SUBTYPE);

					if (sub != null && !sub.equals("")) {
						descr.setSubType(sub);
					}

					context = props.getProperty(CONTEXT_ROOT);

					if (null != archive) {
						addAliasToDescriptor(archive);
					}

					context = props.getProperty(CONTEXT_ROOTS);

					if (context != null && !context.equals("")) {
						String current = null;
						String alias = null;
						String webModule = null;
						int index = -1;
						int innerIndex = -1;

						// context-roots=web_uri1:context_root1::web_uri2:
						// context_root2
						do {
							if ((index = context.indexOf("::")) > -1) {
								current = context.substring(0, index);
								context = context.substring(index + 2);
							} else {
								current = context;
							}

							if ((innerIndex = current.indexOf(':')) > -1) {
								webModule = current.substring(0, innerIndex)
										.trim();
								alias = current.substring(innerIndex + 1)
										.trim();
								descr.addAlias(webModule, alias);
								Logger.trace(location, Severity.DEBUG, "Alias "
										+ alias + " set for " + webModule);
							}
						} while (index > -1);
					}

					descr.setName(props.getProperty(NAME));
					descr.setVendor(props.getProperty(VENDOR));
					descr.setLocation(props.getProperty(LOCATION));
					descr.setCounter(props.getProperty(COUNTER));
					String dependencies = props.getProperty(DEPENDENCIES);

					if (dependencies != null && !dependencies.equals("")) {
						// dependencies=sap.com/deploy;J2EE/Increment

						StringTokenizer tokenizer = new StringTokenizer(
								dependencies, ";");
						String token = null;
						int index = -1;
						String dep_name = null;
						String dep_vendor = null;

						while (tokenizer.hasMoreTokens()) {
							token = tokenizer.nextToken().trim();
							index = token.indexOf("/");

							if (index != -1 && index != 0) {
								dep_vendor = token.substring(0, index);
								dep_name = token.substring(index + 1);
							} else {
								dep_vendor = "";
								dep_name = token;
							}

							descr.addDependency(new Dependency(dep_vendor,
									dep_name));
						}
					}

					String runtimeDependencies = props
							.getProperty(RUNTIME_DEPENDENCIES);

					if (runtimeDependencies != null
							&& !runtimeDependencies.equals("")) {
						// dependencies=library/weak/sap.com/deploy;service/strong/J2EE/Increment

						StringTokenizer tokenizer = new StringTokenizer(
								runtimeDependencies, ";");
						String token = null;

						while (tokenizer.hasMoreTokens()) {
							token = tokenizer.nextToken().trim();

							String[] values = token.split("/");

							if (values.length != 4) {

								throw new DeployLibException(
										location,
										ExceptionConstants.RUNTIME_REFERENCES_INCORRECT_FORMAT,
										new String[] { values.toString() });
							}

							descr
									.addRuntimeDependency(new RuntimeDependency(
											values[0], values[1], values[2],
											values[3]));
						}
					}

				} catch (IOException ioe) {// $JL-EXC$
					Logger.log(location, Severity.ERROR,
							"Could not load properties due to "
									+ ioe.getMessage());
					throw ioe;
				}
			}
		}

		return descr;
	}

	SDADescriptor processSDAProperties(String propertiesFile)
			throws DeployLibException, IOException {
		return processSDAProperties(null, propertiesFile);
	}

	void addAliasToDescriptor(String archive) {
		SDADescriptor descriptorCopy = descriptor == null ? new SDADescriptor()
				: descriptor.clone();
		if (context != null && !context.equals("")) {
			descriptorCopy.addAlias(archive
					.substring(archive.lastIndexOf(sep) + 1), context);
		}
		setDescriptor(descriptorCopy);
	}

	public void produce() throws DeployLibException, SAXException, IOException {
		init();
		createDeployFile();
		setRuntimeDependencies();
		setManifestData();
		clear();
		Logger.log(location, Severity.INFO, "SDA " + destination
				+ " generated successfully");
	}

	private void clear() {
		String toClear = PropertiesHolder
				.getProperty(PropertiesHolder.CLEAR_TEMP_DIRS);

		if (toClear != null && toClear.trim().toLowerCase(Locale.ENGLISH).equals(FALSE)) {
			Logger.trace(location, Severity.DEBUG,
					"Temp directories will not be deleted");
			return;
		}

		if (workDir != null) {
			if (FileUtils.deleteDirectory(new File(workDir))) {
				Logger.trace(location, Severity.DEBUG, "Deleted " + workDir);
			} else {
				Logger.trace(location, Severity.DEBUG, "Could not delete "
						+ workDir);
			}
		}
	}

	private static String getHelpMessage() {
		return "\n"
				+ "Generates an SDA file from the specified source file.\n"
				+ "\n"
				+ "Usage: make_SDA <path_to_source_file> [-w work_dir] [-d destination_file] [-pr properties_file]\n"
				+ "Parameters:\n"
				+ "  <path_to_source_file> - Path to source file to produce an SDA from.\n"
				+ "  [-w work_dir]         - Work directory.\n"
				+ "  [-d destination_file] - Destination SDA file.\n"
				+ "  [-pr properties_file] - Properties file where SDA attributes are specified.\n"
				+ "\n"
				+ "Examples:\n"
				+ "make_SDA /EARs/MyOldEar.ear -pr JavaEE.properties\n"
				+ "make_SDA /EARs/MyOldEar.ear\n"
				+ "make_SDA D:\\libs\\OurLib.jar -d D:\\libs\\sda\\OurLib.sda -pr library.properties\n"
				+ "make_SDA /RARs/OldRar.rar -w /RARs/temp -pr single-module.properties\n"
				+ "make_SDA /EJBs/OurEJB.jar\n";
	}

	/**
	 * Used by the make_SDA script: converts pure Java EE applications into sda
	 * files
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		final int INCORRECT_ARGUMENTS_PASSED = 1;
		final int DEPLOY_LIB_EXCEPTION = 2;
		final int SAX_EXCEPTION = 3;
		final int IO_EXCEPTION = 4;
		final int LOGGING_NOT_INITIALIZED = 5;

		PropertiesHolder.init();
		try {
			Logger.initLogging();
		} catch (IOException e) {
			System.out
					.println("Exception occured while initializing the logger");
			System.out.println(e);
			System.exit(LOGGING_NOT_INITIALIZED);
		}

		if (args == null || args.length < 1 || args.length > 7) {
			System.out.println(getHelpMessage());// $JL-SYS_OUT_ERR$
			Logger.log(location, Severity.ERROR, "Incorrect arguments passed");
			return;
		}

		if (args[0].equals("-?") || args[0].toLowerCase(Locale.ENGLISH).startsWith("-h")) {
			System.out.println(getHelpMessage());// $JL-SYS_OUT_ERR$
			return;
		}

		String archiveName = args[0];
		SDAProducer maker = null;

		if (archiveName == null || archiveName.equals("")) {
			System.out.println("No source file specified");// $JL-SYS_OUT_ERR$
			Logger.log(location, Severity.ERROR, "No source file specified");
			System.exit(INCORRECT_ARGUMENTS_PASSED);
		}

		maker = new SDAProducer(archiveName);

		String properties = null;

		for (int i = 1; i < args.length; i++) {
			if (args[i].toLowerCase(Locale.ENGLISH).equals("-w")) {
				maker.setWorkDir(args[++i]);
				Logger.trace(location, Severity.DEBUG, "Working directory "
						+ args[i]);
			} else if (args[i].toLowerCase(Locale.ENGLISH).equals("-d")) {
				maker.setDestinationFile(args[++i]);
				Logger.trace(location, Severity.DEBUG, "Destination file "
						+ args[i]);
			} else if (args[i].toLowerCase(Locale.ENGLISH).equals("-pr")) {
				properties = args[++i];
				Logger.trace(location, Severity.DEBUG, "Properties "
						+ properties);
			} else {
				System.out.println(getHelpMessage());// $JL-SYS_OUT_ERR$
				Logger.log(location, Severity.ERROR,
						"Incorrect arguments passed");
				System.exit(INCORRECT_ARGUMENTS_PASSED);
			}
		}

		try {
			maker.setDescriptor(maker.processSDAProperties(archiveName,
					properties));
			maker.produce();
			System.out.println("SDA " + maker.getDestinationFile()
					+ " generated successfully");
		} catch (DeployLibException e) {// $JL-EXC$
			Logger.logThrowable(location, Severity.ERROR,
					"Error occurred while generating SDA file", e);
			System.out.println("Error occurred while generating SDA file: "
					+ "\nFor details refer to deployment logs and traces.");
			e.printStackTrace();
			System.exit(DEPLOY_LIB_EXCEPTION);
		} catch (SAXException e) {
			Logger.logThrowable(location, Severity.ERROR,
					"Error occurred while generating SDA file", e);
			System.out.println("Error occurred while generating SDA file: "
					+ "\nFor details refer to deployment logs and traces.");
			e.printStackTrace();
			System.exit(SAX_EXCEPTION);
		} catch (IOException e) {
			Logger.logThrowable(location, Severity.ERROR,
					"Error occurred while generating SDA file", e);
			System.out.println("Error occurred while generating SDA file: "
					+ "\nFor details refer to deployment logs and traces.");
			e.printStackTrace();
			System.exit(IO_EXCEPTION);
		}
	}

}