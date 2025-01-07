package com.sap.engine.services.dc.cm.deploy.storage.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.InconsistentReadException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.services.dc.cm.dscr.ClusterDscrFactory;
import com.sap.engine.services.dc.cm.dscr.ICMInfo;
import com.sap.engine.services.dc.cm.dscr.InstanceDescriptor;
import com.sap.engine.services.dc.cm.dscr.InstanceStatus;
import com.sap.engine.services.dc.cm.dscr.ItemStatus;
import com.sap.engine.services.dc.cm.dscr.ServerDescriptor;
import com.sap.engine.services.dc.cm.dscr.TestInfo;
import com.sap.engine.services.dc.repo.RepositoryException;
import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;

public abstract class DBICommonLoader {

	protected ConfigurationException configurationException;
	protected RepositoryException repositoryException;

	public void getException() throws ConfigurationException,
			RepositoryException {
		if (this.configurationException != null) {
			throw this.configurationException;
		}
		if (this.repositoryException != null) {
			throw this.repositoryException;
		}
	}

	protected void init() {
		this.configurationException = null;
		this.repositoryException = null;
	}

	protected InstanceDescriptor loadInstanceDescriptor(
			Configuration currInstanceCfg) throws ConfigurationException,
			NameNotFoundException, InconsistentReadException,
			RepositoryException {
		String instanceStatusName;
		InstanceStatus instanceStatus;
		TestInfo testInfo;
		Integer instanceId;
		String description = null;
		Set<ServerDescriptor> serverDescriptors;
		InstanceDescriptor instanceDescriptor;
		instanceId = (Integer) currInstanceCfg
				.getConfigEntry(DeployConstants.DBI_INSTANCE_ID);
		if (instanceId == null) {
			throw new IllegalArgumentException("There is no set Instance Id");
		}

		instanceStatusName = (String) currInstanceCfg
				.getConfigEntry(DeployConstants.DBI_INSTANCE_STATUS);
		instanceStatus = InstanceStatus
				.getInstanceStatusByName(instanceStatusName);
		if (instanceStatus == null) {
			throw new IllegalArgumentException(
					"There is no InstanceStatus with name "
							+ instanceStatusName);
		}

		if (currInstanceCfg
				.existsFile(DeployConstants.DBI_INSTANCE_DESCRIPTION)) {
			description = loadDBIDescription(currInstanceCfg,
					DeployConstants.DBI_INSTANCE_DESCRIPTION);
		}

		testInfo = loadTestInfo(currInstanceCfg);

		serverDescriptors = loadServerDescriptors(currInstanceCfg);

		instanceDescriptor = ClusterDscrFactory.getInstance()
				.createInstanceDescriptor(instanceId, serverDescriptors,
						instanceStatus, testInfo, description);
		return instanceDescriptor;
	}

	/**
	 * 
	 * @param cfg
	 * @return
	 * @throws InconsistentReadException
	 * @throws ConfigurationException
	 * @throws RepositoryException
	 * @deprecated The method will only be used for proofing the concept in the
	 *             prototyping phase. It will not be shipped to external
	 *             customers and is not considered as public interface, without
	 *             reviewing it.
	 */
	private Set<ServerDescriptor> loadServerDescriptors(Configuration cfg)
			throws InconsistentReadException, ConfigurationException,
			RepositoryException {
		Set<ServerDescriptor> result = new HashSet<ServerDescriptor>();

		if (!cfg.existsSubConfiguration(DeployConstants.DBI_SERVERS)) {
			return result;
		}
		final Configuration serversCfg = cfg
				.getSubConfiguration(DeployConstants.DBI_SERVERS);
		final String subNames[] = serversCfg.getAllSubConfigurationNames();
		if (subNames != null) {
			Configuration currServerCfg;
			String serverStatusName;
			ItemStatus serverStatus;
			Integer instanceId;
			Integer clusterId;
			ServerDescriptor serverDescriptor;
			String description = null;
			for (int i = 0; i < subNames.length; i++) {
				currServerCfg = serversCfg.getSubConfiguration(subNames[i]);

				instanceId = (Integer) currServerCfg
						.getConfigEntry(DeployConstants.DBI_SERVER_INSTANCE_ID);
				if (instanceId == null) {
					throw new IllegalArgumentException(
							"There is no set Instance Id");
				}

				clusterId = (Integer) currServerCfg
						.getConfigEntry(DeployConstants.DBI_SERVER_CLUSTER_ID);
				if (clusterId == null) {
					throw new IllegalArgumentException(
							"There is no set Cluster Id");
				}

				serverStatusName = (String) currServerCfg
						.getConfigEntry(DeployConstants.DBI_SERVER_STATUS);
				serverStatus = ItemStatus.getItemStatusByName(serverStatusName);
				if (serverStatus == null) {
					throw new IllegalArgumentException(
							"There is no ServerStatus with name "
									+ serverStatusName);
				}

				if (currServerCfg
						.existsFile(DeployConstants.DBI_SERVER_DESCRIPTION)) {
					description = loadDBIDescription(currServerCfg,
							DeployConstants.DBI_SERVER_DESCRIPTION);
				}

				serverDescriptor = ClusterDscrFactory.getInstance()
						.createServerDescriptor(clusterId, instanceId,
								serverStatus, description);
				result.add(serverDescriptor);
			}
		}

		return result;
	}

	/**
	 * 
	 * @param cfg
	 * @return
	 * @throws InconsistentReadException
	 * @throws ConfigurationException
	 * @deprecated The method will only be used for proofing the concept in the
	 *             prototyping phase. It will not be shipped to external
	 *             customers and is not considered as public interface, without
	 *             reviewing it.
	 */
	private TestInfo loadTestInfo(Configuration cfg)
			throws InconsistentReadException, ConfigurationException {
		TestInfo result = null;
		if (!cfg.existsSubConfiguration(DeployConstants.DBI_TEST_INFO)) {
			return result;
		}
		final Configuration testInfoCfg = cfg
				.getSubConfiguration(DeployConstants.DBI_TEST_INFO);
		final ICMInfo icmInfo = loadICMInfo(testInfoCfg);
		result = ClusterDscrFactory.getInstance().createTestInfo(icmInfo);
		return result;
	}

	/**
	 * 
	 * @param cfg
	 * @return
	 * @throws InconsistentReadException
	 * @throws ConfigurationException
	 * @deprecated The method will only be used for proofing the concept in the
	 *             prototyping phase. It will not be shipped to external
	 *             customers and is not considered as public interface, without
	 *             reviewing it.
	 */
	private ICMInfo loadICMInfo(Configuration cfg)
			throws InconsistentReadException, ConfigurationException {
		ICMInfo result = null;
		if (!cfg.existsSubConfiguration(DeployConstants.DBI_ICM_INFO)) {
			return result;
		}
		final Configuration icmInfoCfg = cfg
				.getSubConfiguration(DeployConstants.DBI_ICM_INFO);

		String icmHost = (String) icmInfoCfg
				.getConfigEntry(DeployConstants.DBI_ICM_HOST);
		if (icmHost == null) {
			throw new IllegalArgumentException("There is no set ICM host");
		}

		Integer icmHttpPort = (Integer) icmInfoCfg
				.getConfigEntry(DeployConstants.DBI_ICM_HTTP_PORT);
		if (icmHttpPort == null) {
			throw new IllegalArgumentException("There is no set ICM HTTP port");
		}

		result = ClusterDscrFactory.getInstance().createICMInfo(icmHost,
				icmHttpPort.intValue());
		return result;
	}

	protected String loadDBIDescription(Configuration ddCfg, String entryName)
			throws RepositoryException, ConfigurationException {
		Reader isReader = null;
		BufferedReader bufferedReader = null;
		StringBuffer sbDescr = new StringBuffer();
		try {
			isReader = new InputStreamReader(ddCfg.getFile(entryName));
			bufferedReader = new BufferedReader(isReader);
			String line;

			while ((line = bufferedReader.readLine()) != null) {
				sbDescr.append(line);
				sbDescr.append(Constants.EOL);
			}
		} catch (IOException ioe) {
			throw new RepositoryException(
					DCExceptionConstants.REPO_ERROR_OCCUR_WHILE_READ_DBI_DESCR,
					new Object[] { entryName, ioe });
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
				if (isReader != null) {
					isReader.close();
				}
			} catch (IOException ioe) {
				throw new RepositoryException(
						DCExceptionConstants.REPO_ERROR_OCCUR_WHILE_READ_DBI_DESCR,
						new Object[] { entryName, ioe });
			}
		}
		return sbDescr.toString();
	}

}
