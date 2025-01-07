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
import java.util.Locale;

import org.xml.sax.SAXException;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.engine.lib.deploy.sda.constants.Constants;
import com.sap.engine.lib.deploy.sda.exceptions.DeployLibException;
import com.sap.engine.lib.deploy.sda.exceptions.FileNotJarCompatibleException;
import com.sap.engine.lib.deploy.sda.logger.Logger;
import com.sap.engine.lib.deploy.sda.propertiesholder.PropertiesHolder;

/**
 * @author Mariela Todorova
 */
public class SDABatch implements Constants {
	private static final Location location = Location
			.getLocation(SDABatch.class);
	private String sourceDir = null;
	private String workDir = null;
	private String destDir = null;
	private SDADescriptor descriptor = null;
	private String[] fileSet = null;

	public SDABatch(String dir) {
		Logger.trace(location, Severity.INFO,
				"Generating SDA files for files in " + dir);
		sourceDir = dir;
	}

	public SDABatch(String[] fileSet) {
		Logger.trace(location, Severity.INFO,
				"Generating SDA files for fileset of " + fileSet.length
						+ " files...");
		this.fileSet = fileSet;
	}

	public void setWorkDir(String dir) {
		Logger.trace(location, Severity.DEBUG, "Set work dir " + dir);
		workDir = dir;
	}

	public void setDestinationDir(String dir) {
		Logger
				.trace(location, Severity.DEBUG, "Set destination dir "
						+ destDir);
		destDir = dir;
	}

	public void setDescriptor(SDADescriptor descriptor) {
		this.descriptor = descriptor;
	}

	private File[] getArchiveFiles() {
		if (sourceDir != null) {
			File dir = new File(sourceDir);

			if (!dir.isDirectory()) {
				Logger.log(location, Severity.ERROR, "Directory " + sourceDir
						+ " not found");
				System.out.println("Directory " + sourceDir + " not found");// $JL
				// -
				// SYS_OUT_ERR$
				return null;
			}

			return dir.listFiles();

		} else if (fileSet != null) {
			File[] files = new File[fileSet.length];
			for (int i = 0; i < fileSet.length; i++) {
				files[i] = new File(fileSet[i]);
			}

			return files;

		} else {
			throw new RuntimeException(
					"[ERROR CODE DPL.JSR.8100] no files to import");
		}
	}

	public void process() throws IOException, DeployLibException, SAXException {
		File[] files = getArchiveFiles();

		if (files == null) {
			return;
		}

		File file = null;
		String filePath = null;
		SDAProducer maker = null;

		for (int i = 0; i < files.length; i++) {
			file = files[i];

			if (file == null || !file.isFile()) {
				continue;
			}

			filePath = file.getAbsolutePath();

			if (SDUChecker.check(filePath)) {
				Logger.log(location, Severity.INFO, "File " + filePath
						+ " is already in SDA format. Will not be converted.");
				System.out.println("File " + filePath
						+ " is already in SDA format. Will not be converted.");// $JL
				// -
				// SYS_OUT_ERR$
				continue;
			}

			maker = new SDAProducer(filePath);
			maker.setWorkDir(workDir);

			if (null != descriptor) {
				maker.setDescriptor(descriptor);
				maker.addAliasToDescriptor(file.getAbsolutePath());
			}

			if (destDir != null && !destDir.equals("")) {
				maker.setDestinationFile(destDir + sep + file.getName());
			}

			try {

				maker.produce();

				Logger.log(location, Severity.INFO, "SDA "
						+ maker.getDestinationFile()
						+ " generated successfully");
				System.out.println("SDA " + maker.getDestinationFile()
						+ " generated successfully");// $JL-SYS_OUT_ERR$

			} catch (FileNotJarCompatibleException e) {
				System.out
						.println(file.getName()
								+ " is not in jar compatible format and will be skipped");
			}

		}
	}

	private static String getHelpMessage() {
		return "\n"
				+ "Generates an SDA file for each source file in the specified directory.\n"
				+ "\n"
				+ "Usage: make_SDAs <path_to_source_dir> [-w work_dir] [-d destination_dir] [-pr properties_file]\n"
				+ "Parameters:\n"
				+ "  <path_to_source_dir> - Path to directory with source files.\n"
				+ "  [-w work_dir]        - Work directory.\n"
				+ "  [-d destination_dir]  - Destination directory.\n"
				+ "  [-pr properties_file] - Properties file for all files in the path_to_source_dir.\n"
				+ "\n"
				+ "Examples:\n"
				+ "make_SDAs /EARs\n"
				+ "make_SDAs D:\\libs -d D:\\libs\\sda\n"
				+ "make_SDAs /RARs -w /RARs/temp\n"
				+ "make_SDAs D:\\ears -d D:\\sdas -pr D:\\ears\\ear_to_sda.properties";
	}

	/**
	 * Used by the make_SDAs script: converts pure Java EE applications into sda
	 * files
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		
		final int INCORRECT_ARGUMENTS_PASSED = 1;
		final int IO_EXCEPTION = 2;
		final int XML_PARSE_ERROR = 3;
		final int DEPLOYMENT_ERROR = 4;
		final int LOGGING_NOT_INITIALIZED = 5;
		
		PropertiesHolder.init();
		try {
			Logger.initLogging();
		} catch (IOException e) {
			System.out.println("Exception occured while initializing the logger");
			System.out.println(e);
			System.exit(LOGGING_NOT_INITIALIZED);
		}

		if (args == null || args.length < 1 || args.length > 7) {
			System.out.println(getHelpMessage());// $JL-SYS_OUT_ERR$
			Logger.log(location, Severity.ERROR, "Incorrect arguments passed");
			System.exit(INCORRECT_ARGUMENTS_PASSED);
		}

		if (args[0].equals("-?") || args[0].toLowerCase(Locale.ENGLISH).startsWith("-h")) {
			System.out.println(getHelpMessage());// $JL-SYS_OUT_ERR$
			return;
		}

		String dir = args[0];

		if (dir == null || dir.equals("")) {
			System.out.println("No source directory specified");// $JL-
			// SYS_OUT_ERR$
			Logger.log(location, Severity.ERROR,
					"No source directory specified");
			System.exit(INCORRECT_ARGUMENTS_PASSED);
		}

		SDABatch batch = new SDABatch(dir);

		String properties = null;

		for (int i = 1; i < args.length; i++) {
			if (args[i].toLowerCase(Locale.ENGLISH).equals("-w")) {
				batch.setWorkDir(args[++i]);
				Logger.trace(location, Severity.DEBUG, "Working directory "
						+ args[i]);
			} else if (args[i].toLowerCase(Locale.ENGLISH).equals("-d")) {
				batch.setDestinationDir(args[++i]);
				Logger.trace(location, Severity.DEBUG, "Destination directory "
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

			SDADescriptor descriptor = new SDAProducer(null)
					.processSDAProperties(properties);
			batch.setDescriptor(descriptor);

			batch.process();

		} catch (IOException e) {
			Logger.logThrowable(location, Severity.ERROR,
					"Error occurred while generating SDA file", e);
			System.out.println("Error occurred while generating SDA file");
			System.out
					.println("For details refer to deployment logs and traces");
			System.exit(IO_EXCEPTION);
		} catch (DeployLibException e) {
			Logger.logThrowable(location, Severity.ERROR,
					"Error occurred while generating SDA file", e);
			System.out.println("Error occurred while generating SDA file");
			System.out
					.println("For details refer to deployment logs and traces");
			System.exit(DEPLOYMENT_ERROR);
		} catch (SAXException e) {
			Logger.logThrowable(location, Severity.ERROR,
					"Error occurred while generating SDA file", e);
			System.out.println("Error occurred while generating SDA file");
			System.out
					.println("For details refer to deployment logs and traces");
			System.exit(XML_PARSE_ERROR);
		}
	}

}
