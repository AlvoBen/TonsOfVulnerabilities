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
package com.sap.engine.deployment.proxy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.ArrayList;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;

import javax.enterprise.deploy.shared.ModuleType;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Location;
import com.sap.engine.deployment.Constants;
import com.sap.engine.deployment.Logger;
import com.sap.engine.lib.io.FileUtils;
import com.sap.engine.lib.jar.JarExtractor;
import com.sap.engine.lib.jar.JarUtils;
import com.sap.engine.deployment.exceptions.SAPIOException;
import com.sap.engine.deployment.exceptions.ExceptionConstants;
import com.sap.engine.deployment.PropertiesHolder;

/**
 * @author Mariela Todorova
 */
public class ApplicationTuner implements Constants {
	private static final Location location = Location
			.getLocation(ApplicationTuner.class);
	private String workDir = null;
	private File archive = null;
	private File dPlan = null;
	private Properties props = new Properties();
	private boolean isStandAlone = false;

	public ApplicationTuner(File moduleArchive, File deploymentPlan)
			throws SAPIOException, ParserConfigurationException, TransformerException {
		if (moduleArchive == null || !moduleArchive.isFile()) {
			Logger.trace(location, Severity.DEBUG,
					"No module archive specified");
			throw new SAPIOException(location,
					ExceptionConstants.ARCHIVE_NOT_FOUND,
					new String[] { moduleArchive == null ? "null"
							: moduleArchive.getAbsolutePath() });
		}

		Logger.trace(location, Severity.DEBUG, "Application tuner for "
				+ moduleArchive.getAbsolutePath() + " with deployment plan "
				+ deploymentPlan);
		this.initWorkDir(moduleArchive.getName());
		this.archive = new File(workDir, moduleArchive.getName());

		try {
			FileUtils.copyFile(moduleArchive, archive);
			Logger.trace(location, Severity.INFO, "Temp module archive "
					+ archive.getAbsolutePath());

			if (deploymentPlan != null) {
				this.dPlan = new File(workDir, deploymentPlan.getName());
				FileUtils.copyFile(deploymentPlan, dPlan);
				Logger.trace(location, Severity.INFO, "Temp deployment plan "
						+ dPlan.getAbsolutePath());
				this.loadDeploymentProperties();
				this.updateDistrModule();

				if (TRUE.equals(props.getProperty(WEBSERVICES_PACK))
						&& isStandAlone
						&& archive.getName().toLowerCase().endsWith(JAR)) {

					WSPacker packer = new WSPacker(archive, props, workDir);
					archive = new File(packer.getResultArchive());
				}
			}
		} catch (IOException ioe) {
			throw new SAPIOException(location,
					ExceptionConstants.COULD_NOT_TUNE
							+ new String[] { moduleArchive.getAbsolutePath() },
					ioe);
		}
	}

	public ApplicationTuner(InputStream moduleArchive,
			InputStream deploymentPlan) throws SAPIOException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		if (moduleArchive == null) {
			Logger.trace(location, Severity.DEBUG,
					"No module archive stream specified");
			throw new SAPIOException(location,
					ExceptionConstants.NO_ARCHIVE_STREAM);
		}

		Logger.trace(location, Severity.DEBUG,
				"Application tuner for stream archive");
		this.initWorkDir(STREAM);

		if (deploymentPlan != null) {
			this.dPlan = this.getDeploymentPlanFile(deploymentPlan);
			Logger.trace(location, Severity.INFO, "Temp deployment plan "
					+ dPlan.getAbsolutePath());
		}

		this.loadDeploymentProperties();

		if (props == null || props.getProperty(ROOT_MODULE_NAME) == null) {
			Logger.trace(location, Severity.DEBUG,
					"Property root_module_name not specified");
			throw new SAPIOException(location,
					ExceptionConstants.NO_APPLICATION_NAME);
		}

		this.archive = this.getFileForDistribution(moduleArchive, props
				.getProperty(ROOT_MODULE_NAME));
		Logger.trace(location, Severity.INFO, "Temp module archive "
				+ archive.getAbsolutePath());

		if (dPlan != null && dPlan.exists()) {
			this.updateDistrModule();

			if (TRUE.equals(props.getProperty(WEBSERVICES_PACK))
					&& isStandAlone
					&& archive.getName().toLowerCase().endsWith(JAR)) {// module
				// type
				// JAR ?

				WSPacker packer;
				try {
					packer = new WSPacker(archive, props, workDir);
				} catch (IOException ioe) {// $JL-EXC$
					Logger
							.logThrowable(
									location,
									Severity.ERROR,
									"Could not pack ear file from standalone EJB jar for webservices ",
									new String[] { archive.getAbsolutePath() }, ioe);
					throw new SAPIOException(location, ioe);
				}
				archive = new File(packer.getResultArchive());
			}
		}
	}

	public ApplicationTuner(ModuleType mType, InputStream moduleArchive,
			InputStream deploymentPlan) throws SAPIOException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		if (moduleArchive == null) {
			Logger.trace(location, Severity.DEBUG,
					"No module archive stream specified");
			throw new SAPIOException(location,
					ExceptionConstants.NO_ARCHIVE_STREAM);
		}

		if (mType == null) {
			Logger.trace(location, Severity.DEBUG, "No module type specified");
			throw new SAPIOException(location,
					ExceptionConstants.PARAMETER_NULL,
					new String[] { "module type" });
		}

		Logger.trace(location, Severity.DEBUG, "Application tuner for " + mType
				+ " stream archive");

		this.initWorkDir(mType + "_" + STREAM);

		if (deploymentPlan != null) {
			this.dPlan = this.getDeploymentPlanFile(deploymentPlan);
			Logger.trace(location, Severity.INFO, "Temp deployment plan "
					+ dPlan.getAbsolutePath());
		} else {
			Logger.trace(location, Severity.INFO,
					"No deployment plan specified");// TODO - this case is not
			// handled, yet
		}

		this.loadDeploymentProperties();

		if (props == null || props.getProperty(ROOT_MODULE_NAME) == null) {
			Logger.trace(location, Severity.DEBUG,
					"Property root_module_name not specified");
			throw new SAPIOException(location,
					ExceptionConstants.NO_APPLICATION_NAME);
		}

		this.archive = this.getFileForDistribution(moduleArchive, props
				.getProperty(ROOT_MODULE_NAME));
		Logger.trace(location, Severity.INFO, "Temp module archive "
				+ archive.getAbsolutePath());

		if (dPlan != null && dPlan.exists()) {
			this.updateDistrModule();

			if (TRUE.equals(props.getProperty(WEBSERVICES_PACK))
					&& isStandAlone
					&& archive.getName().toLowerCase().endsWith(JAR)) {// module
				// type
				// JAR ?

				WSPacker packer;
				try {
					packer = new WSPacker(archive, props, workDir);
				} catch (IOException ioe) {// $JL-EXC$
					Logger
							.logThrowable(
									location,
									Severity.ERROR,
									"Could not pack ear file from standalone EJB jar for webservices ",
									new String[] { archive.getAbsolutePath() }, ioe);
					throw new SAPIOException(location, ioe);
				}
				archive = new File(packer.getResultArchive());
			}
		}
	}

	private void initWorkDir(String name) {
		String base = PropertiesHolder
				.getProperty(PropertiesHolder.CTS_WORK_DIR);

		if (base == null || !(new File(base).isDirectory())) {
			base = PropertiesHolder.getProperty(PropertiesHolder.WORK_DIR);

			if (base == null || !(new File(base)).isDirectory()) {
				base = "..";
			}
		}

		this.workDir = base + sep + SAP_DM + sep + System.currentTimeMillis()
				+ "_" + name;
		File workDirFile = new File(workDir);
		workDirFile.mkdirs();
		Logger.trace(location, Severity.INFO, "Work dir "
				+ workDirFile.getAbsolutePath());
	}

	private File getDeploymentPlanFile(InputStream is) throws SAPIOException {
		File plan = new File(workDir + sep + DPLAN_ZIP);
		Logger.trace(location, Severity.DEBUG, "Deployment plan file "
				+ plan.getAbsolutePath());
		this.writeStreamToFile(is, plan);
		return plan;
	}

	private void writeStreamToFile(InputStream is, File file)
			throws SAPIOException {
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int count = 0;

			while ((count = is.read(buffer, 0, buffer.length)) > -1) {
				fos.write(buffer, 0, count);
			}
		} catch (IOException ioe) {
			throw new SAPIOException(location,
					ExceptionConstants.COULD_NOT_WRITE_STREAM,
					new String[] { file.getAbsolutePath() }, ioe);
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException ioex) {// $JL-EXC$
				Logger.trace(location, Severity.DEBUG,
						"Could not close stream due to " + ioex.getMessage());
			}

			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException ioex) {// $JL-EXC$
				Logger.trace(location, Severity.DEBUG,
						"Could not close stream due to " + ioex.getMessage());
			}
		}
	}

	private void loadDeploymentProperties() throws SAPIOException {
		this.props = new Properties();

		if (this.dPlan != null) {
			ZipFile plan = null;

			try {
				plan = new ZipFile(this.dPlan);
				ZipEntry entry = plan.getEntry(DPLAN);

				if (entry == null) {
					return;
				}

				this.props.load(plan.getInputStream(entry));
				String value = props.getProperty(STAND_ALONE);

				if (value != null && value.equals(TRUE)) {
					this.isStandAlone = true;
				}
			} catch (IOException ioe) {
				throw new SAPIOException(location,
						ExceptionConstants.COULD_NOT_LOAD_PROPERTIES, ioe);
			} finally {
				if (plan != null) {
					try {
						plan.close();
					} catch (IOException ioex) {// $JL-EXC$
						Logger.trace(location, Severity.DEBUG,
								"Could not close stream due to "
										+ ioex.getMessage());
					}
				}
			}
		}
	}

	private File getFileForDistribution(InputStream is, String fileName)
			throws SAPIOException {
		File file = new File(workDir + sep + fileName);
		Logger.trace(location, Severity.DEBUG, "Module archive file "
				+ file.getAbsolutePath());
		this.writeStreamToFile(is, file);
		return file;
	}

	private void updateDistrModule() throws SAPIOException {
		JarExtractor extractor = new JarExtractor();
		JarUtils utils = new JarUtils();
		utils.setCompressMethod(ZipEntry.DEFLATED);
		String tempDir = workDir + sep + UPDATE_MODULE;
		File tempDirFile = new File(tempDir);
		tempDirFile.mkdirs();
		File file = null;

		try {
			if (this.isStandAlone) {
				extractor.extractJar(archive.getPath(), tempDir);
				extractor.extractJar(dPlan.getPath(), tempDir);
			} else {
				ArrayList jarModules = new ArrayList();
				String tempModulesDir = workDir + sep + MODULES;
				File tempModulesDirFile = new File(tempModulesDir);
				tempModulesDirFile.mkdirs();
				this.extractJarModules(archive, tempDir, tempModulesDir,
						extractor, jarModules);
				this.extractJarModules(dPlan, tempDir, tempModulesDir,
						extractor, jarModules);
				File jar = null;

				for (int i = 0; i < jarModules.size(); i++) {
					jar = (File) jarModules.get(i);
					jar.delete();
					File tempJarDir = new File(tempModulesDir + sep
							+ jar.getName());
					utils.makeJarFromDir(jar.getPath(), tempJarDir.getPath());
				}
			}

			file = new File(tempDirFile, DPLAN);
			file.delete();
			archive.delete();
			utils.makeJarFromDir(archive.getPath(), tempDir);
		} catch (IOException ioe) {
			throw new SAPIOException(location,
					ExceptionConstants.COULD_NOT_UPDATE_ARCHIVE,
					new String[] { archive.getPath() }, ioe);
		}
	}

	private void extractJarModules(File file, String tempDir,
			String tempModulesDir, JarExtractor extractor, ArrayList jarModules)
			throws SAPIOException {
		try {
			extractor.extractJar(file.getPath(), tempDir);
		} catch (IOException ioe) {
			throw new SAPIOException(location,
					ExceptionConstants.COULD_NOT_EXTRACT_ARCHIVE, new String[] {
							file.getAbsolutePath(), tempDir }, ioe);
		}

		File tempDirFile = new File(tempDir);
		File[] modules = tempDirFile.listFiles();

		if (modules != null) {
			JarFile jar = null;

			for (int i = 0; i < modules.length; i++) {
				if (modules[i].isFile()) {

					try {
						jar = new JarFile(modules[i]);
					} catch (IOException ioe) {// $JL-EXC$
						Logger.trace(location, Severity.DEBUG, "File "
								+ modules[i].getAbsolutePath()
								+ " is not in JAR compatible format");
						continue;
					}

					try {
						extractor.extractJar(modules[i].getPath(),
								tempModulesDir + sep + modules[i].getName());

						if (!jarModules.contains(modules[i])) {
							jarModules.add(modules[i]);
						}

						jar.close();
					} catch (IOException ioex) {// $JL-EXC$
						Logger.trace(location, Severity.DEBUG,
								"Could not extract "
										+ modules[i].getAbsolutePath()
										+ " due to " + ioex.getMessage());
					}
				}
			}
		}
	}

	public File getArchive() {
		return this.archive;
	}

	public Properties getProperties() {
		return (this.props == null ? new Properties() : props);
	}

	public void clear() {
		String toClear = PropertiesHolder
				.getProperty(PropertiesHolder.CLEAR_TEMP_DIRS);

		if (toClear != null && toClear.trim().toLowerCase().equals(FALSE)) {
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

}