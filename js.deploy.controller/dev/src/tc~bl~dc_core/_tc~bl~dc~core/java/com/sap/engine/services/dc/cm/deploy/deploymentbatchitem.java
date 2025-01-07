package com.sap.engine.services.dc.cm.deploy;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

import com.sap.engine.services.dc.cm.dscr.ClusterDescriptor;
import com.sap.engine.services.dc.cm.utils.statistics.TimeStatisticsEntry;
import com.sap.engine.services.dc.repo.Sdu;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-8-17
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface DeploymentBatchItem extends Serializable {

	public BatchItemId getBatchItemId();

	public Sdu getSdu();

	public String getSduFilePath();

	public void setVersionStatus(VersionStatus versionStatus);

	public VersionStatus getVersionStatus();

	public DeploymentStatus getDeploymentStatus();

	public void setDeploymentStatus(DeploymentStatus deploymentItemStatus);

	public String getDescription();

	public void deserializeTimeStatisticsFromStream(InputStream iStream)
			throws NumberFormatException, IOException;

	public InputStream serializeTimeStatisticsAsStream();

	/**
	 * Provides information about the distinct steps during the item deployment.
	 * Traverse through the object for detailed information about distinct
	 * deployment steps.
	 * 
	 * @return <code>TimeStatistics</code> node which is the root of the time
	 *         statistics tree.
	 */
	public com.sap.engine.services.dc.api.statistics.TimeStatisticsEntry[] getTimeStatisticEntries();

	/**
	 * For internal purposes only. CLients should not invoke this method.
	 * 
	 * @param entryName
	 * @return created <code>TimeStatisticsEntry</code> with
	 *         <code>entryName</code>
	 */
	public TimeStatisticsEntry startTimeStatEntry(String entryName,
			int entryType);

	/**
	 * For internal purposes only. CLients should not invoke this method.
	 * 
	 * @return
	 */
	public TimeStatisticsEntry finishTimeStatEntry();

	/**
	 * Sets the argument to the items description.
	 * 
	 * @param description
	 *            a text which adds to the previous description if there is any.
	 */
	public void setDescription(String description);

	/**
	 * Concatenates the argument to the items description.
	 * 
	 * @param description
	 *            a text which adds to the previous description if there is any.
	 */
	public void addDescription(String description);

	/**
	 * Concatenates the argument to the items description.
	 * 
	 * @param th
	 */
	public void addDescription(Throwable th);

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
	 * 
	 * 
	 * @param clusterDescriptor
	 *            <code>ClusterDescriptor</code>
	 * @deprecated The method will only be used for proofing the concept in the
	 *             prototyping phase. It will not be shipped to external
	 *             customers and is not considered as public interface, without
	 *             reviewing it.
	 */
	public void setClusterDescriptor(ClusterDescriptor clusterDescriptor);

	public Map getProperties();

	public void setProperties(Map propsMap);

	/**
	 * If the <code>VersionStatus</code> is one of the <code>SAME</code>,
	 * <code>LOWER</code> <code>HIGHER</code> the operation returns the
	 * previously deployed <code>Sdu</code>. If the <code>VersionStatus</code>
	 * is NEW then the operation returns <code>null</code>.
	 * 
	 * @return <code>Sdu</code> in case of update and <code>null</code> in case
	 *         of deploy.
	 * @see com.sap.engine.services.dc.cm.deploy.VersionStatus
	 */
	public Sdu getOldSdu();

	/**
	 * Sets the previously deployed <code>Sdu</code>.
	 * 
	 * @param sdu
	 *            specifies the <code>Sdu</code> which is going to be updated
	 *            with the one returned from the operation <code>getSdu()</code>
	 *            .
	 */
	public void setOldSdu(Sdu sdu);

	public void accept(DeploymentBatchItemVisitor visitor);

	public String toString();

	public boolean equals(Object obj);

	public int hashCode();

}
