package com.sap.engine.services.dc.cm.archive_mng.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.isDebugLoggable;
import static com.sap.engine.services.dc.util.logging.DCLog.logDebugThrowable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;

import javax.naming.NamingException;

import com.sap.engine.lib.io.SerializableFile;
import com.sap.engine.services.dc.cm.archive_mng.ArchiveManagementException;
import com.sap.engine.services.dc.cm.archive_mng.ArchiveManager;
import com.sap.engine.services.dc.cm.archive_mng.ArchiveNotFoundException;
import com.sap.engine.services.dc.cm.archive_mng.SduNotStoredException;
import com.sap.engine.services.dc.manage.PathsConfigurer;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.repo.Repository;
import com.sap.engine.services.dc.repo.RepositoryException;
import com.sap.engine.services.dc.repo.RepositoryFactory;
import com.sap.engine.services.dc.repo.SduNotStoredInRepositoryException;
import com.sap.engine.services.dc.util.ComponentPropsCorrector;
import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.FileUtils;
import com.sap.engine.services.dc.util.JarUtils;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.deploy.DeployService;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-19
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public final class ArchiveManagerImpl implements ArchiveManager {

	// private static final String FILE_TRANSFER_SERVICE_NAME = "file";

	private Repository repository;

	private  final Location location = DCLog.getLocation(this.getClass());
		
	
	ArchiveManagerImpl() {
		// To prevent the instantiation.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.archive_mng.ArchiveManager#getSduSourcesPath
	 * (java.lang.String, java.lang.String, java.lang.String)
	 */
	public String getSduSourcesPath(String compName, String compVendor,
			String sessionId) throws ArchiveNotFoundException,
			ArchiveManagementException {
		String sdaPath = getSdaArchivePath(compName, compVendor, sessionId);
		File sdaFile = new File(sdaPath);
		String archiveName = sdaFile.getName();
		int pos = archiveName.lastIndexOf(".");
		if (pos != -1) {
			archiveName = archiveName.substring(0, pos);
		}
		File destination = new File(sdaFile.getParentFile(), archiveName
				+ "_src.zip");
		String errorMessage = JarUtils.extractEntry(sdaPath, "src.zip",
				destination.getAbsolutePath());
		if (errorMessage != null) {
			this.gc(sdaPath, sessionId);
			ArchiveManagementException ame = new ArchiveManagementException(
					"The system could extract entry: "
							+ errorMessage);
			ame.setMessageID("ASJ.dpl_dc.003000");
			throw ame;
		}
		try {
			return destination.getCanonicalPath();
		} catch (IOException e) {
			ArchiveManagementException ame = new ArchiveManagementException(
					"The system could not get the canonical archive path "
							+ "from the file " + destination.getAbsolutePath());
			ame.setMessageID("ASJ.dpl_dc.003001");
			throw ame;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.archive_mng.ArchiveManager#getSdaArchivePath
	 * (java.lang.String, java.lang.String, java.lang.String)
	 */
	public String getSdaArchivePath(String compName, String compVendor,
			String sessionId) throws ArchiveNotFoundException,
			SduNotStoredException, ArchiveManagementException {
		final String name = getCorrectedParameter(compName, "comp name");
		final String vendor = getCorrectedParameter(compVendor, "comp name");

		final Repository repo = this.getRepository();
		final File sdaArchive;
		try {
			sdaArchive = repo.loadSdaArchive(name, vendor, sessionId);
		} catch (SduNotStoredInRepositoryException snsire) {
			SduNotStoredException snse = new SduNotStoredException(
					"The system could not upload the archive for the "
							+ "specified component name '" + name
							+ "' and vendor as it is not stored in repository.",
					snsire);
			snse.setMessageID("ASJ.dpl_dc.003002");
			throw snse;
		} catch (RepositoryException re) {
			ArchiveManagementException ame = new ArchiveManagementException(
					"The system could not upload the archive for the "
							+ "specified component name '" + name
							+ "' and vendor.", re);
			ame.setMessageID("ASJ.dpl_dc.003003");
			throw ame;
		}

		if (sdaArchive == null) {
			ArchiveNotFoundException anfe = new ArchiveNotFoundException(
					"The system could not find archive for the "
							+ "specified component name '" + name
							+ "' and vendor '" + vendor
							+ "'. Probably the archive is not deployed.");
			anfe.setMessageID("ASJ.dpl_dc.003004");
			throw anfe;
		}

		try {
			return sdaArchive.getCanonicalPath();
		} catch (IOException e) {
			ArchiveManagementException ame = new ArchiveManagementException(
					"The system could not get the canonical archive path "
							+ "from the file " + sdaArchive.getAbsolutePath());
			ame.setMessageID("ASJ.dpl_dc.003005");
			throw ame;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.archive_mng.ArchiveManager#getScaArchivePath
	 * (java.lang.String, java.lang.String, java.lang.String)
	 */
	public String getScaArchivePath(String compName, String compVendor,
			String sessionId) throws ArchiveNotFoundException,
			ArchiveManagementException {
		final String name = getCorrectedParameter(compName, "comp name");
		final String vendor = getCorrectedParameter(compVendor, "comp name");

		final Repository repo = this.getRepository();
		final File scaArchive;
		try {
			scaArchive = repo.loadScaArchive(name, vendor, sessionId);
		} catch (RepositoryException re) {
			ArchiveManagementException ame = new ArchiveManagementException(
					"The system could not upload the archive for the "
							+ "specified component name '" + name
							+ "' and vendor '", re);
			ame.setMessageID("ASJ.dpl_dc.003006");
			throw ame;
		}

		if (scaArchive == null) {
			ArchiveNotFoundException anfe = new ArchiveNotFoundException(
					"The system could not find archive for the "
							+ "specified component name '" + name
							+ "' and vendor '" + vendor
							+ "'. Probably the archive is not deployed.");
			anfe.setMessageID("ASJ.dpl_dc.003007");
			throw anfe;
		}

		try {
			return scaArchive.getCanonicalPath();
		} catch (IOException e) {
			ArchiveManagementException ame = new ArchiveManagementException(
					"The system could not get the canonical archive path "
							+ "from the file " + scaArchive.getAbsolutePath());
			ame.setMessageID("ASJ.dpl_dc.003008");
			throw ame;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.archive_mng.ArchiveManager#getClientJarPath
	 * (java.lang.String, java.lang.String, java.lang.String)
	 */
	public String getClientJarPath(String compName, String compVendor,
			String sessionId) throws ArchiveManagementException {
		final String name = getCorrectedParameter(compName, "comp name");
		final String vendor = getCorrectedParameter(compVendor, "comp vendor");

		final DeployService deployService = getDeployService();

		final SerializableFile dsSerFile;
		try {
			dsSerFile = deployService.getClientJar(vendor, name);
		} catch (RemoteException re) {
			ArchiveManagementException ame = new ArchiveManagementException(
					"An error occurred while getting the client jar for the "
							+ "specified component name '"
							+ name
							+ "' and vendor '" + vendor + "'.", re);
			ame.setMessageID("ASJ.dpl_dc.003009");
			throw ame;
		}

		if (dsSerFile == null) {
			ArchiveNotFoundException anfe = new ArchiveNotFoundException(
					"The system could not find client jars for the "
							+ "specified component name '" + name
							+ "' and vendor '" + vendor + "'.");
			anfe.setMessageID("ASJ.dpl_dc.003010");
			throw anfe;
		}

		FileOutputStream fos = null;
		try {
			byte[] bytes = dsSerFile.getBytes();
			String parentPath = PathsConfigurer.getInstance()
					.getStorageDirName(sessionId);
			new File(parentPath).mkdirs();
			String dcFileName = FileUtils.concatDirs(parentPath, dsSerFile
					.getAsFile().getName());
			File file = new File(dcFileName);
			String canonicalPath = file.getCanonicalPath();
			fos = new FileOutputStream(canonicalPath);
			fos.write(bytes);
			return canonicalPath;
		} catch (IOException e) {
			ArchiveManagementException ame = new ArchiveManagementException(
					"The system could not get the canonical archive path "
							+ "from the file "
							+ dsSerFile.getAbsoluteFilePath());
			ame.setMessageID("ASJ.dpl_dc.003011");
			throw ame;			
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ioe) {
					if (isDebugLoggable()) {
						logDebugThrowable(location, ioe);
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.archive_mng.ArchiveManager#gc(java.lang
	 * .String, java.lang.String)
	 */
	public void gc(String filePath, String sessionId)
			throws ArchiveManagementException {
		final String storageDirPath = PathsConfigurer.getInstance()
				.getStorageDirName(sessionId);

		validateFile(filePath, storageDirPath);
		com.sap.engine.lib.io.FileUtils.deleteDirectory(
			new File(storageDirPath));
	}

	private void validateFile(String filePath, String dcStorageDirPath)
		throws ArchiveManagementException {
		if (filePath == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003012 the specified argument filePath is null.");			
		}

		final String storageDirPath;
		try {
			storageDirPath = new File(dcStorageDirPath).getCanonicalPath();
		} catch (IOException ioe) {
			ArchiveManagementException ame = new ArchiveManagementException(
					"The system could not get the canonical file path "
							+ "for the file Deploy Controller storage location "
							+ dcStorageDirPath);
			ame.setMessageID("ASJ.dpl_dc.003013");
			throw ame;
		}
		final String comparingFilePath = filePath.replace('\\', '/');
		String comparingStorageDirPath = storageDirPath.replace('\\', '/');
		if (!comparingStorageDirPath.endsWith("/")) {
			comparingStorageDirPath += "/";
		}

		if (!comparingFilePath.startsWith(comparingStorageDirPath)) {
			ArchiveManagementException ame = new ArchiveManagementException(
					"The specified file '"
							+ filePath
							+ "' is not "
							+ "located under the Deploy Controller's storage directory.");
			ame.setMessageID("ASJ.dpl_dc.003014");
			throw ame;
		}

		final File validatingFile = new File(filePath);
		if (!validatingFile.exists()) {
			ArchiveManagementException ame = new ArchiveManagementException(
					"The specified file '" + filePath
							+ "' does not exist.");
			ame.setMessageID("ASJ.dpl_dc.003015");
			throw ame;
		}

		if (!validatingFile.isFile()) {
			ArchiveManagementException ame = new ArchiveManagementException(
					"The specified file path '"
							+ filePath + "' does not refer to a file.");
			ame.setMessageID("ASJ.dpl_dc.003016");
			throw ame;
		}

		if (!validatingFile.canWrite()) {
			ArchiveManagementException ame = new ArchiveManagementException(
					"The system does not own write access for "
							+ "the specified file '" + filePath + "'.");
			ame.setMessageID("ASJ.dpl_dc.003017");
			throw ame;
		}
	}

	private synchronized Repository getRepository() {
		if (this.repository == null) {
			this.repository = RepositoryFactory.getInstance()
					.createRepository();
		}

		return this.repository;
	}

	private DeployService getDeployService() throws ArchiveManagementException {
		try {
			return ServiceConfigurer.getInstance().getDeployService();
		} catch (NamingException nex) {
			ArchiveManagementException ame =  new ArchiveManagementException(
					"An error occurred while getting the service "
							+ Constants.DEPLOY_SERVICE_NAME, nex);
			ame.setMessageID("ASJ.dpl_dc.003018");
			throw ame;
		}
	}

	private String getCorrectedParameter(String parameter, String parameterName) {
		if (parameter == null) {
			return null;
		}

		final String trimmedParameter = parameter.trim();
		final String correctedParameter = ComponentPropsCorrector
				.getCorrected(trimmedParameter);

		checkCorrectedParameter(trimmedParameter, correctedParameter,
				parameterName);

		return correctedParameter;
	}

	private void checkCorrectedParameter(String value, String correctedValue,
			String paramName) {
		if (!value.equals(correctedValue)) {
			DCLog
					.logInfo(location,
							"ASJ.dpl_dc.005601",
							"Property [{0}] [{1}] will be corrected to [{2}] because it contains charactes which are not allowed by the system",
							new Object[] { paramName, value, correctedValue });
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.archive_mng.ArchiveManager#canRead(java
	 * .lang.String)
	 */
	public boolean canRead(String filePath) {
		final File file = new File(filePath);
		if (file.exists() && file.canRead()) {
			return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.archive_mng.ArchiveManager#canWrite(java
	 * .lang.String)
	 */
	public boolean canWrite(String filePath) {
		final File file = new File(filePath);
		if (file.exists() && file.canWrite()) {
			return true;
		}

		return false;
	}
}
