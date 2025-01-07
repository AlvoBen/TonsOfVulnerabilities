package com.sap.engine.services.dc.api.archive_mng;

import com.sap.engine.services.dc.api.APIException;
import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.archive_mng.ArchiveNotFoundException;
import com.sap.engine.services.dc.api.archive_mng.DownloadingException;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Archive manager serves to download archives of already deployed
 * componetns</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2005-4-20</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public interface ArchiveManager {
	/**
	 * This method downloads the sda sources if they are included in the sda
	 * archive. The sources should be in the root folder whithin the archive
	 * with the name "src.zip"
	 * 
	 * @param compName
	 *            component key name
	 * @param compVendor
	 *            component vendor
	 * @param downloadFilePath
	 *            where to store the downloaded content
	 * @return real archive name
	 * @throws ArchiveNotFoundException
	 * @throws DownloadingException
	 * @throws ConnectionException
	 * @throws APIException
	 */
	public String downloadSduSources(String compName, String compVendor,
			String downloadFilePath) throws ArchiveNotFoundException,
			DownloadingException, ConnectionException, APIException;

	/**
	 * Downloads SDA archive to the local FS if the component is deployed on the
	 * J2EE engine
	 * 
	 * @param compName
	 *            component key name
	 * @param compVendor
	 *            component vendor
	 * @param downloadFilePath
	 *            where to store the archive on the local FS.
	 * @return real SDA file name
	 * @throws ArchiveNotFoundException
	 * @throws DownloadingException
	 * @throws ConnectionException
	 * @throws APIException
	 */
	public String downloadSdaArchive(String compName, String compVendor,
			String downloadFilePath) throws ArchiveNotFoundException,
			DownloadingException, ConnectionException, SduNotStoredException,
			APIException;

	/**
	 * Downloads entire SCA archive to the local FS if the component is deployed
	 * on the J2EE engine
	 * 
	 * @param compName
	 *            component key name
	 * @param compVendor
	 *            component vendor
	 * @param downloadFilePath
	 *            where to store the archive on the local FS
	 * @return real SCA file name
	 * @throws ArchiveNotFoundException
	 * @throws DownloadingException
	 * @throws ConnectionException
	 * @throws APIException
	 */
	public String downloadScaArchive(String compName, String compVendor,
			String downloadFilePath) throws ArchiveNotFoundException,
			DownloadingException, ConnectionException, APIException;

	/**
	 * Downloads generated during the deployment jar file
	 * 
	 * @param compName
	 *            component key name
	 * @param compVendor
	 *            comonent vendor
	 * @param downloadFilePath
	 *            where to store the archive on the local FS
	 * @return the real jar file name
	 * @throws ArchiveNotFoundException
	 * @throws DownloadingException
	 * @throws ConnectionException
	 * @throws APIException
	 */
	public String downloadClientJar(String compName, String compVendor,
			String downloadFilePath) throws ArchiveNotFoundException,
			DownloadingException, ConnectionException, APIException;

}
