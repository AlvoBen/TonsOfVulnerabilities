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

package com.sap.engine.services.deploy.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.sap.engine.lib.io.FileUtils;
import com.sap.engine.lib.io.SerializableFile;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.tc.logging.Location;

/**
 * Utility class for internal use of Deploy service.
 * <p/>
 * Copyright (c) 2003, SAP-AG
 * 
 * @author Monika Kovachka
 */
public class DUtils {
	
	private static final Location location = 
		Location.getLocation(DUtils.class);

	/**
	 * Adds array of files to List of files.
	 * 
	 * @param files array of files. Not null.
	 * @param all List of files. Not null.
	 */
	public static void addArrayToList(File[] files, List<File> all) {
		assert all != null;
		assert files != null;
		for(File file : files) {
			if (file != null && file.exists()) {
					all.add(file);
			}
		}
	}

	public static String[] concatArrays(String[] s1, String[] s2) {
		final Set<String> temp = concat(s1, s2);
		String result[] = null;
		if (temp != null) {
			result = new String[temp.size()];
			temp.toArray(result);
		}
		return result;
	}

	public static Set concat(Object[] o1, Object[] o2) {
		final Set set1 = convert(o1);
		final Set set2 = convert(o2);

		return concat(set1, set2);
	}

	public static Set concat(Set set1, Set set2) {
		Set result = null;
		if (set1 != null) {
			if (result == null) {
				result = new LinkedHashSet();
			}
			result.addAll(set1);
		}
		if (set2 != null) {
			if (result == null) {
				result = new LinkedHashSet();
			}
			result.addAll(set2);
		}
		return result;
	}

	public static Set convert(Object[] o) {
		if (o == null) {
			return null;
		}

		final Set set = new LinkedHashSet();
		for (int i = 0; i < o.length; i++) {
			set.add(o[i]);
		}
		return set;
	}

	/**
	 * Concatenates two int arrays and returns the resultant array.
	 * 
	 * @param s1
	 *            int array.
	 * @param s2
	 *            int array.
	 * @return the resultant int array.
	 */
	public static int[] concatArrays(int[] s1, int[] s2) {
		if (s1 == null) {
			return s2;
		}

		if (s2 == null) {
			return s1;
		}

		if (s1 == null && s2 == null) {
			return null;
		}
		int[] temp = new int[s1.length + s2.length];
		int counter = 0;
		boolean found = false;
		for (int i = 0; i < s1.length; i++) {
			found = false;
			for (int j = 0; j < counter; j++) {
				if (s1[i] == temp[j]) {
					found = true;
					break;
				}
			}
			if (!found) {
				temp[counter++] = s1[i];
			}
		}
		for (int i = 0; i < s2.length; i++) {
			found = false;
			for (int j = 0; j < counter; j++) {
				if (s2[i] == temp[j]) {
					found = true;
					break;
				}
			}
			if (!found) {
				temp[counter++] = s2[i];
			}
		}
		int[] res = new int[counter];
		System.arraycopy(temp, 0, res, 0, counter);
		return res;
	}

	/**
	 * Adds a String to the end of each String in the array and returns the
	 * modified String array.
	 * 
	 * @param s1
	 *            String array.
	 * @param additional
	 *            the String to be added.
	 * @return the resultant String array.
	 */
	public static String[] addToElements(String[] s1, String additional) {
		if (s1 == null) {
			return null;
		}

		if (additional == null || additional.trim().equals("")
				|| s1.length == 0) {
			return s1;
		}

		String[] enlarged = new String[s1.length];

		for (int i = 0; i < s1.length; i++) {
			enlarged[i] = s1[i] + " " + additional;
		}

		return enlarged;
	}

	/**
	 * Merges client jars for an application.
	 * 
	 * @param all
	 *            array of client jars.
	 * @param applicationName
	 *            application name.
	 * @param serviceRootDir
	 *            service root directory.
	 * @return the resultant client jar.
	 * @throws RemoteException
	 *             if a problem occurs during the process.
	 */
	public static SerializableFile mergeClientJars(SerializableFile[] all,
			String applicationName, String serviceRootDir)
			throws RemoteException {
		if (all == null || all.length == 0) {
			return null;
		}

		String name;
		String tempName = serviceRootDir + File.separator + "temp"
				+ System.currentTimeMillis();
		File file = null;
		FileOutputStream fileOut;
		FileOutputStream fileResOut = null;
		ZipOutputStream zipOut = null;
		ZipEntry zipEntry = null;
		File dir = new File(tempName);
		dir.mkdirs();

		String resPath = dir + File.separator + "result" + File.separator
				+ applicationName + "_client" + ".jar";
		resPath = resPath.replace('/', File.separatorChar);

		File resultDir = new File(resPath.substring(0, resPath
				.lastIndexOf(File.separator)));
		resultDir.mkdirs();

		File resultFile = new File(resPath);
		try {
			fileResOut = new FileOutputStream(resultFile);
			zipOut = new ZipOutputStream(fileResOut);
			zipOut.setMethod(ZipOutputStream.STORED);
		} catch (FileNotFoundException mcjexc) {
			throw new RemoteException(
					"[ERROR CODE DPL.DS.6201] Error occurred while merging client jars for application "
							+ applicationName
							+ ".\nReason: "
							+ mcjexc.toString(), mcjexc);
		}
		ZipFile zipFile = null;
		Enumeration e = null;
		int length = 0;
		InputStream in = null;
		byte[] buf = new byte[1024];

		for (int i = 0; i < all.length; i++) {
			if (all[i] == null || all[i].getBytes() == null) {
				continue;
			}

			name = all[i].getFileName();
			file = new File(tempName + File.separator + name);
			try {
				fileOut = new FileOutputStream(file);
				fileOut.write(all[i].getBytes());
				fileOut.flush();
				fileOut.close();
				zipFile = new ZipFile(file);
				e = zipFile.entries();

				while (e.hasMoreElements()) {
					zipEntry = (ZipEntry) (e.nextElement());
					in = zipFile.getInputStream(zipEntry);
					try {
						ZipEntry tmpEntry = new ZipEntry(zipEntry);
						tmpEntry.setCompressedSize(-1);
						zipOut.putNextEntry(tmpEntry);
					} catch (Exception deexc) { // excludes duplacated entries
						zipOut.closeEntry();
						in.close();
						continue;
					}

					while ((length = in.read(buf, 0, buf.length)) != -1) {
						zipOut.write(buf, 0, length);
					}

					zipOut.closeEntry();
					in.close();
				}

				zipFile.close();
			} catch (IOException zfexc) {
				throw new RemoteException(
						"[ERROR CODE DPL.DS.6202] Error occurred while merging client jars for application "
								+ applicationName
								+ ".\nReason: "
								+ zfexc.toString(), zfexc);
			}
		}

		try {
			zipOut.close();
			fileResOut.close();
		} catch (IOException clsexc) {
			throw new RemoteException(
					"[ERROR CODE DPL.DS.6203] Error occurred while merging client jars for application "
							+ applicationName
							+ ".\nReason: "
							+ clsexc.toString(), clsexc);
		}
		SerializableFile ser = new SerializableFile(resultFile);
		DUtils.deleteDirectory(dir);
		return ser;
	}

	/**
	 * Adds a container to the end of array of containers and returns the
	 * resultant array.
	 * 
	 * @param containers
	 *            array of containers.
	 * @param cont
	 *            container to be added.
	 * @return the resultant array of containers.
	 */
	public static ContainerInterface[] addAsLastElement(
			ContainerInterface[] containers, ContainerInterface cont) {
		if (containers == null) {
			return new ContainerInterface[] { cont };
		}

		for (int i = 0; i < containers.length; i++) {
			if (containers[i].getContainerInfo().getName().equals(
					cont.getContainerInfo().getName())) {
				return containers;
			}
		}

		ContainerInterface[] temp = new ContainerInterface[containers.length + 1];
		System.arraycopy(containers, 0, temp, 0, containers.length);
		temp[containers.length] = cont;
		return temp;
	}

	public static Properties addToContainerProperties(
			Properties containerProperties, String containerName,
			Properties properties) {
		properties = properties == null ? new Properties() : properties;
		if (containerProperties.contains(containerName)) {
			Properties props = (Properties) containerProperties
					.get(containerName);
			if (props == null) {
				props = properties;
				containerProperties.put(containerName, props);
			} else {
				props.putAll(properties);
			}
			return containerProperties;
		}

		containerProperties.put(containerName, properties);
		return containerProperties;
	}

	/**
	 * Removes an element from the set of integer values.
	 * @param set set of integer values. Not null.
	 * @param element value to be removed.
	 * @return the resulting set or the same set, if element is not present.
	 */
	public static int[] removeElement(int[] set, int element) {
		assert set != null;
		for (int index = 0; index < set.length; index++) {
			if (set[index] == element) {
				int[] result = new int[set.length - 1];
				if(index > 0) {
					System.arraycopy(set, 0, result, 0, index);
				}
				if (index < set.length - 1) {
					System.arraycopy(set, index + 1, result, 
						index, set.length - index - 1);
				}
				return result;
			}
		}
		return set;
	}

	/**
	 * Deletes a directory.
	 * @param dir directory to be deleted.
	 */
	public static void deleteDirectory(final File dir) {
		if (dir != null && dir.isDirectory()) {
			if(!FileUtils.deleteDirectory(dir)) {
				traceUndeletedFiles(dir);
			}
		}
	}

	private static void traceUndeletedFiles(final File dir) {
		assert dir.isDirectory();
		try {
			for(File file : FileUtils.listFiles(dir)) {
				if (file.exists()) {
					if (file.isFile()) {
						DSLog.traceError(location, "ASJ.dpl_ds.002033",
							"File [{0}] could not be deleted.",
							file.getPath());
					} else if (file.isDirectory()) {
						traceUndeletedFiles(file);
					}
				} else {
					DSLog.traceError(
						location, 
						"ASJ.dpl_ds.002034",
						"File [{0}] does not exist, so it could not be deleted.",
						file.getPath());
				}
			}
		} catch (IOException ex) {
			DSLog.logErrorThrowable(location, ex);
		}
	}

	/**
	 * Internally parses a reference.
	 * 
	 * @param ref
	 *            a reference.
	 * @return the resultant reference.
	 */
	public static String parseReferencesInternal(String ref) {
		if (ref != null) {
			if (!ref.startsWith("interface:") && !ref.startsWith("service:")
					&& !ref.startsWith("library:")) {
				ref = "application:" + ref;
			}
		}
		return ref;
	}

	/**
	 * Returns application ID of an application.
	 * 
	 * @param appName
	 *            application name.
	 * @return application ID.
	 */
	public static String getApplicationID(String appName) {
		if (appName == null) {
			return null;
		}
		int index = appName.indexOf("/");
		if (index != -1 && index != 0) {
			String compName = appName.substring(index + 1);
			return appName.substring(0, index + 1)
					+ replaceForbiddenSymbols(compName);
		} else {
			return "sap.com/" + replaceForbiddenSymbols(appName);
		}
	}

	/**
	 * Replaces forbidden symbols in a component name.
	 * 
	 * @param compName
	 *            component name.
	 * @return the correct component name.
	 */
	public static String replaceForbiddenSymbols(String compName) {
		if (compName == null) {
			return null;
		}
		final String result = compName.trim()
			.replace('/', '~').replace('\\', '~').replace(':', '~')
			.replace('*', '~').replace('?', '~').replace('"', '~')
			.replace('<', '~').replace('>', '~').replace('|', '~')
			.replace(';', '~').replace(',', '~').replace('=', '~')
			.replace('&', '~').replace('%', '~').replace('[', '~')
			.replace(']', '~').replace('#', '~');
		return result.endsWith(".") ?
			result.substring(0, result.length() - 1) + "~" : result;
	}

	/**
	 * Concatenates two arrays of ReferenceObjects and returns the resultant
	 * one.
	 * 
	 * @param oldRefs
	 *            array of ReferenceObjects.
	 * @param newRefs
	 *            array of ReferenceObjects.
	 * @return the resultant array of ReferenceObjects.
	 */
	public static ReferenceObject[] concatReferences(ReferenceObject[] oldRefs,
			ReferenceObject[] newRefs) {
		if (oldRefs == null) {
			return newRefs;
		}
		if (newRefs == null) {
			return oldRefs;
		}

		List<ReferenceObject> concatedRefs = new ArrayList<ReferenceObject>();
		for (int i = 0; i < oldRefs.length; i++) {
			if (oldRefs[i] != null) {
				for (int j = 0; j < newRefs.length; j++) {
					if (newRefs[j] != null) {
						if (!oldRefs[i].equalsSimple(newRefs[j])) {
							if (!isAlreadyAdded(concatedRefs, oldRefs[i])) {
								concatedRefs.add(oldRefs[i]);
							}
						}
					}
				}
			}
		}
		for (int j = 0; j < newRefs.length; j++) {
			if (!isAlreadyAdded(concatedRefs, newRefs[j])) {
				concatedRefs.add(newRefs[j]);
			}
		}

		ReferenceObject[] refs = null;
		if (concatedRefs != null && concatedRefs.size() > 0) {
			refs = new ReferenceObject[concatedRefs.size()];
			concatedRefs.toArray(refs);
		}
		return refs;
	}

	// Checks if a container is already added to list of containers.
	private static boolean isAlreadyAdded(List container, ReferenceObject refObj) {
		if (refObj == null) {
			return false;
		}
		if (container == null) {
			container = new ArrayList<ReferenceObject>();
			return false;
		}
		for (int i = 0; i < container.size(); i++) {
			if (refObj.equals(container.get(i))) {
				return true;
			}
		}

		return false;
	}

	public static String[] processListElement(String temp[], String key) {
		List<String> plus = new ArrayList<String>();
		List<String> minus = new ArrayList<String>();

		for (int i = 0; i < temp.length; i++) {
			if (temp[i] != null) {
				if (temp[i].indexOf(key) != -1) {
					minus.add(temp[i]);
				} else {
					plus.add(temp[i]);
				}
			}
		}

		String current = null;
		int index = -1;
		for (int i = 0; i < minus.size(); i++) {
			current = minus.get(i);
			if (current != null) {
				index = current.indexOf("-");
				if (index != -1
						&& findElement(current.substring(0, index), plus) != -1) {
					minus.set(i, null);
				}
			}
		}

		for (int i = 0; i < minus.size(); i++) {
			current = minus.get(i);
			if (current != null) {
				plus.add(current);
			}
		}

		return plus.toArray(new String[plus.size()]);
	}

	public static int findElement(String target, List source) {
		String current = null;
		if (target != null && source != null) {
			for (int i = 0; i < source.size(); i++) {
				current = (String) source.get(i);
				if (current != null && current.startsWith(target)) {
					return i;
				}
			}
		}
		return -1;
	}

	public static int findElement(Object target, Object source[]) {
		if (target != null && source != null) {
			for (int i = 0; i < source.length; i++) {
				if (source[i] != null && source[i].equals(target)) {
					return i;
				}
			}
		}
		return -1;
	}

	public static int findElement(int target, int source[]) {
		if (source != null) {
			for (int i = 0; i < source.length; i++) {
				if (source[i] == target) {
					return i;
				}
			}
		}
		return -1;
	}

	public static String[] getAliases(DeploymentInfo info) {
		Properties props = info.getProperties();
		if (props == null) {
			return null;
		}
		Enumeration names = props.propertyNames();
		String name = null;
		List<String> aliases = new ArrayList<String>();

		while (names.hasMoreElements()) {
			name = (String) names.nextElement();

			if (name.startsWith(DeployConstants.WEB)) {
				aliases.add(name.substring(DeployConstants.WEB.length()));
			}
		}

		String[] res = new String[aliases.size()];
		aliases.toArray(res);
		return res;
	}

	public static void processWarningsAndErrors(DTransaction transaction)
			throws WarningException {
		if (!PropManager.getInstance().isStrictJ2eeChecks()) {
			return;
		}
		String result[][] = getWarningsAndErrors(transaction);
		if ((result[0] != null && result[0].length != 0)
				|| (result[1] != null && result[1].length != 0)) {
			WarningException wex = new WarningException();
			wex.setWarning(DUtils.concatArrays(result[0], result[1]));
			throw wex;
		}
	}

	public static String[][] getWarningsAndErrors(DTransaction transaction) {
		String result[][] = new String[2][];
		TransactionStatistics[] stat = transaction.getStatistics();
		String[] warnings = new String[0];
		String[] errors = new String[0];
		if (stat != null) {
			for (int i = 0; i < stat.length; i++) {
				if (stat[i] != null) {
					warnings = DUtils.concatArrays(warnings,
						generateResultMessage(stat[i].getWarnings(),
									"Warning", stat[i].getClusterID(),
									transaction.getTransactionType(),
									transaction.getModuleID()));
					errors = DUtils.concatArrays(errors, generateResultMessage(
							stat[i].getErrors(), "Error", stat[i]
									.getClusterID(), transaction
									.getTransactionType(), transaction
									.getModuleID()));
				}
			}
		}
		if (warnings != null && warnings.length != 0) {
			result[0] = new String[warnings.length];
			result[0] = warnings;
		}
		if (errors != null && errors.length != 0) {
			result[1] = new String[errors.length];
			result[1] = errors;
		}

		return result;
	}

	public static String[] generateResultMessage(String[] source, String type,
			int clusterID, String transactionType, String moduleID) {
		String result[] = null;
		if (source != null) {
			result = new String[source.length];
			for (int i = 0; i < source.length; i++) {
				result[i] = "\n" + type + " occurred on server " + clusterID
						+ " during " + transactionType + " of " + moduleID
						+ " : " + source[i];
			}
		}
		return result;
	}

	public static String getStackTrace(Throwable th) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		th.printStackTrace(pw);
		return sw.toString();
	}

}
