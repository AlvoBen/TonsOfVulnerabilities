package com.sap.engine.services.dc.api.deploy;

import java.io.File;

import com.sap.engine.services.dc.api.dscr.ClusterDescriptor;
import com.sap.engine.services.dc.api.statistics.TimeStatisticsEntry;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Deploy Item representation.</DD>
 * <DT><B>Usage: </B></DT>
 * <DD>DeployItem deployItem = deployProcessor.createDeployItem( pathToSDA );</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2004-9-9</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 * @see com.sap.engine.services.dc.api.deploy.DeployProcessor#createDeployItem(String)
 * @see com.sap.engine.services.dc.api.deploy.DeployProcessor#deploy(DeployItem[])
 * @see com.sap.engine.services.dc.api.deploy.DeployProcessor#validate(DeployItem[])
 */
public interface DeployItem {
	/**
	 * Returns the archive of this deploy item
	 * 
	 * @return deploy item file
	 */
	public File getArchive();

	/**
	 * Return sdu describing this deploy item
	 * 
	 * @return sdu describing Deploy Item
	 */
	public com.sap.engine.services.dc.api.model.Sdu getSdu();

	/**
	 * returns a value only after deploy action performed
	 * 
	 * @return deploy item status
	 */
	public DeployItemStatus getDeployItemStatus();

	/**
	 * returns a value only after deploy action performed
	 * 
	 * @return deploy item result description after deploying the item
	 */
	public String getDescription();

	/**
	 * returns a value only after deploy action performed with
	 * <code>DeployWorkflowStrategy</code>.ROLLING.
	 * 
	 * @return <code>ClusterDescriptor</code>
	 * @deprecated The method will only be used for proofing the concept in the
	 *             prototyping phase. It will not be shipped to external
	 *             customers and is not considered as public interface, without
	 *             reviewing it.
	 */
	public ClusterDescriptor getClusterDescriptor();

	/**
	 * returns a value only after deploy action performed
	 * 
	 * @return version status of the deployed item
	 */
	public DeployItemVersionStatus getVersionStatus();

	/**
	 * Returns an array with all contained SDA if the current Item is SCA
	 * otherwise null
	 * 
	 * @return array with all contained SDA if the current Item is SCA otherwise
	 *         null.
	 */
	public DeployItem[] getContainedDeployItems();

	/**
	 * Provides information about the distinct steps during the item deployment.
	 * Traverse through the object for detailed information about distinct
	 * deployment steps.
	 * 
	 * @return <code>TimeStatistics</code> node which is the root of the time
	 *         statistics tree.
	 */
	public TimeStatisticsEntry[] getTimeStatisticEntries();

}