package com.sap.engine.services.dc.cm.archive_mng;

import java.rmi.Remote;

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
public interface ArchiveManager extends Remote {

	/**
	 * Downloads the sources of the selected bby <code>name</code> and
	 * <code>vendor</code> component if there any.
	 * 
	 * @param compName
	 *            Sdu key name
	 * @param compVendor
	 *            Sdu vendor name
	 * @param sessionId
	 * @return fully qualiffied file path to the downloaded sources if there
	 *         any.
	 * @throws ArchiveNotFoundException
	 * @throws ArchiveManagementException
	 * @throws SduNotStoredException
	 */
	public String getSduSourcesPath(String compName, String compVendor,
			String sessionId) throws ArchiveNotFoundException,
			ArchiveManagementException;

	public String getSdaArchivePath(String compName, String compVendor,
			String sessionId) throws ArchiveNotFoundException,
			SduNotStoredException, ArchiveManagementException;

	public String getScaArchivePath(String compName, String compVendor,
			String sessionId) throws ArchiveNotFoundException,
			ArchiveManagementException;

	public String getClientJarPath(String compName, String compVendor,
			String sessionId) throws ArchiveManagementException;

	public void gc(String filePath, String sessionId)
			throws ArchiveManagementException;

	public boolean canRead(String filePath);

	public boolean canWrite(String filePath);

}
