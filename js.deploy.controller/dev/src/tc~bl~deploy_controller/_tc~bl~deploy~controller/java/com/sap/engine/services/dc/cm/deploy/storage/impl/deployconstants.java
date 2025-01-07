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
package com.sap.engine.services.dc.cm.deploy.storage.impl;

/**
 * Provides constants for storing the <code>DeploymentData</code> in DB.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
final class DeployConstants {

	// The name of the configuration entry for the error handling strategy.
	final static String DD_ERROR_STRATEGY = "error_strategy";
	// The name of the configuration entry for the deployment workflow strategy.
	final static String DD_WORKFLOW_STRATEGY = "workflow_strategy";
	// The name of the configuration entry for the deployment parallelism
	// strategy.
	final static String DD_PARALLELISM_STRATEGY = "parallelism_strategy";
	// The name of the configuration entry for the life cycle deployment
	// strategy.
	final static String DD_LC_DEPLOY_STRATEGY = "lc_deploy_strategy";
	// The name of the configuration entry for the time statistics enable
	// property
	final static String DD_ENABLE_TIMESTATS = "enable_timestats";

	// The root configuration for the sorted DeploymentBatchItem-s.
	final static String DD_DBI_SORTED = "sorted_dbi";
	// The root configuration for all DeploymentBatchItem-s.
	final static String DD_DBI = "dbi";
	// User who deploy
	final static String DD_USER = "user";
	// Caller Host
	final static String DD_CALLER_HOST = "caller_host";
	// OS User Name
	final static String DD_OS_USER_NAME = "OsUserName";
	// OS User Pass
	final static String DD_OS_USER_PASS = "OsUserPass";
	// Deployment data description
	final static String DD_DESCRIPTION = "Description";
	// The root configuration for measurements.
	final static String DD_MEASUREMENTS = "measurements";

	// DeploymentItem depending
	final static String DI_DEPENDING = "depending";
	// PArent configuration where all deploymetn items from one SCA are stored.
	final static String CDI_DIs = "DIs";

	// DeploymentBatchItem name
	final static String DBI_NAME = "Name";
	// DeploymentBatchItem vendor
	final static String DBI_VENDOR = "Vendor";
	// The number of DeploymentBatchItem with same BatchItemId in current batch.
	final static String DBI_BATCHITEMID_COUNT = "IdCount";
	// DeploymentBatchItem path
	final static String DBI_PATH = "Path";
	// DeploymentBatchItem version status
	final static String DBI_VERSION_STATUS = "Version Status";
	// DeploymentBatchItem deployment status
	final static String DBI_DEPLOYMENT_STATUS = "Deployment Status";
	// DeploymentBatchItem description
	final static String DBI_DESCRIPTION = "Description";
	// DeploymentBatchItem time statistics
	final static String DBI_TIMES = "times";

	// The roor configuration for the sodted <code>Sdu</code>s
	final static String DBI_NEW = "new";
	// The roor configuration for the OLD sodted <code>Sdu</code>s
	final static String DBI_OLD = "old";

	// DeploymentBatchItem sorted number
	final static String DBI_SORTED_NUMBER = "Number";

	// Parent configuration of Cluster descriptor
	final static String DBI_CLUSTER_DESCRIPTOR = "cluster_descriptor";
	// ClusterDescriptor cluster startu
	final static String DBI_CLUSTER_STATUS = "Cluster Status";
	// Parent configuration of instance descriptors
	final static String DBI_INSTANCES = "instances";
	// InstanceDescriptor instance id
	final static String DBI_INSTANCE_ID = "InstanceId";
	// InstanceDescriptor instance status
	final static String DBI_INSTANCE_STATUS = "InstanceStatus";
	// InstanceDescriptor instance description
	final static String DBI_INSTANCE_DESCRIPTION = "InstanceDescription";
	// Parent configuration of TestInfo
	final static String DBI_TEST_INFO = "TestInfo";
	// Parent configuration of ICMInfo
	final static String DBI_ICM_INFO = "ICMInfo";
	// ICM host
	final static String DBI_ICM_HOST = "host";
	// ICM http port
	final static String DBI_ICM_HTTP_PORT = "http_port";
	// Parent configuration of server descriptors
	final static String DBI_SERVERS = "servers";
	// Server status
	final static String DBI_SERVER_STATUS = "ServerStatus";
	// Server instance id
	final static String DBI_SERVER_INSTANCE_ID = "ServerInstanceId";
	// Server cluster id
	final static String DBI_SERVER_CLUSTER_ID = "ServerClusterId";
	// Server description
	final static String DBI_SERVER_DESCRIPTION = "ServerDescription";
	// Rolling info
	final static String DBI_ROLLING_INFO = "RollingInfo";
	// Rolling item name
	final static String DBI_ROLLING_ITEM_NAME = "RollingItemName";
	// Rolling item type
	final static String DBI_ROLLING_ITEM_TYPE = "RollingItemType";
	// Instances data
	final static String DD_INSTANCES_DATA = "instances";
	// Instance Id
	final static String DD_INSTANCE_ID = "InstanceId";
	// the instance sync is processed
	final static String DD_IS_PROCESSED = "isProcessed";
	// lock data
	final static String DD_LOCK_DATA = "lockData";

}
