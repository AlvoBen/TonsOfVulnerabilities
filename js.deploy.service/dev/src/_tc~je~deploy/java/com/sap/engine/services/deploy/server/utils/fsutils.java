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
package com.sap.engine.services.deploy.server.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import com.sap.engine.lib.io.FileUtils;
import com.sap.engine.services.deploy.ear.modules.extract.Extractor;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.tc.logging.Location;

/**
 * Should be used for file system operations. This class is intended only for
 * internal use by deploy service.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public final class FSUtils {
	
	private static final Location location = 
		Location.getLocation(FSUtils.class);
	
	@SuppressWarnings("unchecked")
	public static Set<String> relativePath(String parentDir, Set absolute) {
		Set<String> relative = null;
		if (absolute == null) {
			return relative;
		}
		ValidateUtils.nullValidator(parentDir, "parent directory");

		StringBuffer debugInfo = new StringBuffer("FSUtils.relativePath(...)\n");
		debugInfo.append("\n parentDir= " + parentDir + "\n");

		relative = new LinkedHashSet<String>();
		String temp = null;
		final Iterator absIter = absolute.iterator();
		int i = -1;
		while (absIter.hasNext()) {
			temp = (String) absIter.next();
			i++;
			debugInfo.append("\n absolute[" + i + "]= " + temp);
			temp = relativePath(parentDir, temp);
			debugInfo.append("\n relative[" + i + "]= " + temp + "\n");
			relative.add(temp);
		}
		if (location.beDebug()) {
			DSLog.traceDebug(location, "{0}", debugInfo.toString());
		}
		return relative;
	}

	public static String relativePath(String parentDir, String absolute) {
		ValidateUtils.nullValidator(parentDir, "parent directory");
		ValidateUtils.nullValidator(absolute, "absolute path");

		if (!absolute.startsWith(parentDir)) {
			throw new IllegalArgumentException(
					"ASJ.dpl_ds.006099 The path '" + absolute
							+ "' doesn't start with '" + parentDir + "'.");
		}

		final String relative = absolute.substring(parentDir.length());
		return relative;
	}

	/**
	 * Builds an ordered absolute paths, where the file separator is
	 * File.separatorChar
	 * 
	 * @param parentDir
	 *            string, which must ends with File.separator
	 * @param ordered
	 *            relative strings, which must ends without File.separator
	 * @return
	 */
	public static Set<String> absolutePaths(String parentDir,
			Set<String> relative) {
		Set<String> absolute = null;
		if (relative == null) {
			return absolute;
		}
		ValidateUtils.nullValidator(parentDir, "parent directory");

		StringBuilder debugInfo = new StringBuilder(
				"FSUtils.buildAbsolutePaths(...)\n");
		debugInfo.append("\n parentDir= " + parentDir + "\n");

		absolute = new LinkedHashSet<String>();
		String absTemp = null, relTemp = null;
		int i = 0;
		for (Iterator<String> relativeIter = relative.iterator(); relativeIter
				.hasNext();) {
			relTemp = relativeIter.next();
			debugInfo.append("\n relative[" + i + "]= " + relTemp);
			absTemp = absolutePath(parentDir, relTemp);
			debugInfo.append("\n absolute[" + i + "]= " + absTemp + "\n");
			absolute.add(absTemp);
			i++;
		}
		if (location.beDebug()) {
			DSLog.traceDebug(location, "{0}", debugInfo.toString());
		}
		return absolute;
	}

	private static String absolutePath(String parentDir, String relative) {
		ValidateUtils.nullValidator(parentDir, "parent directory");
		ValidateUtils.nullValidator(relative, "relative path");

		String absolute = parentDir + relative;
		// All separators must be replaced with File.separatorChar, 
		// because latter there will be path comparison.
		absolute = pathNormalizer(absolute);

		return absolute;
	}

	public static String dirNormalizer(final String source, 
		final String defaultValue, final boolean doClear) throws IOException {
		final File dir = new File(source == null ? defaultValue : source);
		if (!dir.exists()) {
			dir.mkdirs();
		} else {
			if (doClear) {
				FileUtils.deleteDirectory(dir);
			}
		}
		if (dir.isFile()) {
			throw new IllegalArgumentException(
					"ASJ.dpl_ds.006100 The given '" + source
							+ "' is file. It has to be folder name.");
		}
		String path = dir.getCanonicalPath();
		if (!path.endsWith(File.separator)) {
			path += File.separator;
		}
		return path;
	}

	/**
	 * Normalize the file path replacing the slashes with the separator char.
	 * 
	 * @param filePath
	 * @return
	 */
	public static String pathNormalizer(final String filePath) {
		if (filePath == null) {
			return null;
		}
		return filePath.replace('\\', File.separatorChar)
			.replace('/', File.separatorChar);
	}

	public static String[] pathNormalizer(final String filePaths[]) {
		if (filePaths == null) {
			return null;
		}
		final String result[] = new String[filePaths.length];
		for (int i = 0; i < filePaths.length; i++) {
			result[i] = pathNormalizer(filePaths[i]);
		}
		return result;
	}

	public static Set<String> pathNormalizer(Set<String> filePaths) {
		if (filePaths == null) {
			return filePaths;
		}
		final Set<String> result = new LinkedHashSet<String>();
		for (final Iterator<String> fpIter = filePaths.iterator(); fpIter
				.hasNext();) {
			result.add(pathNormalizer(fpIter.next()));
		}
		return result;
	}

	public static void downloadFile(InputStream is, File file)
			throws ServerDeploymentException {
		file.getParentFile().mkdirs();
		byte[] buffer = new byte[1024];
		int count = 0;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			while ((count = is.read(buffer)) > 0) {
				fos.write(buffer, 0, count);
			}
		} catch (IOException ioex) {
			ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.GENERAL_IO_EXCEPTION,
					new String[] { "downloading " + file.getAbsolutePath() },
					ioex);
			sde.setMessageID("ASJ.dpl_ds.005103");
			throw sde;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ioex) {
					ServerDeploymentException sde = new ServerDeploymentException(
							ExceptionConstants.GENERAL_IO_EXCEPTION,
							new String[] { "closing " + fos }, ioex);
					sde.setMessageID("ASJ.dpl_ds.005103");
					throw sde;
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException ioex) {
					ServerDeploymentException sde = new ServerDeploymentException(
							ExceptionConstants.GENERAL_IO_EXCEPTION,
							new String[] { "closing " + is }, ioex);
					sde.setMessageID("ASJ.dpl_ds.005103");
					throw sde;
				}
			}
		}
	}

	public static File getFileNonCaseSensitive(
		final File dir, final String[] path) {
		if (path == null) {
			return null;
		}
		File result = dir;
		String[] children = null;
		int j = 0;
		for (int i = 0; i < path.length; i++) {
			if (path[i] != null) {
				children = result.list();
				if (children != null) {
					for (j = 0; j < children.length; j++) {
						if (children[j].equalsIgnoreCase(path[i])) {
							result = new File(dir, children[j]);
							break;
						}
					}
					if (j == children.length) {
						return null;
					}
				} else {
					return null;
				}
			}
		}
		return result;
	}

	/**
	 * @param parentDir
	 *            The parent directory, where a new unique directory has to be
	 *            created. Usually this is the DeployService work directory.
	 * @param prefix
	 *            The prefix of the temporary directory.
	 * @return A temporary directory under the workDir, which name starts with
	 *         the given prefix and ends with unique number.
	 */
	public static String getUniqueDir(final String parentDir,
			final String prefix) {
		ValidateUtils.nullValidator(parentDir,
				"absolute path to parent directory");
		return Extractor.getUniqueDir(parentDir, prefix).getAbsolutePath();
	}

	public static String getUniqueDir(String parentDir) {
		return getUniqueDir(parentDir, "");
	}

	public static File getUniqueDir(File parentDir) {
		return getUniqueDir(parentDir, "");
	}

	public static File getUniqueDir(File parentDir, String mark) {
		ValidateUtils.nullValidator(parentDir, "directory");
		return new File(getUniqueDir(parentDir.getAbsolutePath(), mark));
	}
}