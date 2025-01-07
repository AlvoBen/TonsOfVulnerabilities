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
package com.sap.engine.services.deploy.server.application;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.io.File;
import java.util.Hashtable;
import java.util.Properties;

import junit.framework.TestCase;

import org.junit.BeforeClass;

import com.sap.engine.lib.io.FileUtils;
import com.sap.engine.services.deploy.container.ComponentNotDeployedException;
import com.sap.engine.services.deploy.container.ContainerDeploymentInfo;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.server.DeployServiceContext;
import com.sap.engine.services.deploy.server.properties.PropManagerFactory;
import com.sap.engine.services.deploy.server.remote.ClusterMonitorHelper;
import com.sap.engine.services.deploy.server.remote.RemoteCallerTest;

/**
 * @author Anton Georgiev
 * @version 7.1
 */

public class DeployUtilTransactionTest extends TestCase {
	private static final String TEMP_DIR = "DeployUtilTransactionTestDir";
	private static final int clElemID = 1;
	private static final String clElemName = "testName";

	private static DeployServiceContext mockContext;
	
    @Override
    @BeforeClass
	public void setUp() {
		PropManagerFactory.initInstance(TEMP_DIR, clElemID, clElemName);
		final ClusterMonitorHelper mockCMH =
			RemoteCallerTest.createMockClusterMonitorHelper();
		mockContext = createStrictMock(DeployServiceContext.class);
		expect(mockContext.getClusterMonitorHelper()).andReturn(mockCMH);
		replay(mockContext);
	}

	public void testIfPropertiesAreNull() throws Exception {
		final Properties props = null;
		final Properties expected = new Properties();
		validateProperties(props, expected);
	}

	public void testIfPropertiesAreEmpty() throws Exception {
		final Properties props = new Properties();
		validateProperties(props, props);
	}

	public void testIfPropertiesHaveOneElement() throws Exception {
		final Properties props = new Properties();
		props.put("one", new Object());
		validateProperties(props, props);
	}

	public void testIfPropertiesHaveTwoElements() throws Exception {
		final Properties props = new Properties();
		props.put("one", new Object());
		props.put("two", new Object());
		validateProperties(props, props);
	}

	private void validateProperties(Properties props, Properties expected) {
		final MockDeployUtilTransaction mockTx = new MockDeployUtilTransaction(
		    props);
		FileUtils.deleteDirectory(new File(TEMP_DIR));
		assertEquals(expected, mockTx.getProperties());
	}

	class MockDeployUtilTransaction extends DeployUtilTransaction {
		public MockDeployUtilTransaction(Properties props) {
			super(mockContext);
			setProperties(props);
		}

		@Override
		public Properties getProperties() {
			return super.getProperties();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.sap.engine.services.deploy.server.application.DeployUtilTransaction
		 * #makeComponents(java.io.File[],
		 * com.sap.engine.services.deploy.container.ContainerDeploymentInfo,
		 * java.util.Properties,
		 * com.sap.engine.services.deploy.container.ContainerInterface)
		 */
		@Override
		protected void makeComponents(File[] arg0,
		    ContainerDeploymentInfo arg1, Properties arg2,
		    ContainerInterface arg3) throws DeploymentException {
		}

		/*
		 * (non-Javadoc)
		 * @see com.sap.engine.services.deploy.server.DTransaction#begin()
		 */
		public void begin() throws DeploymentException,
		    ComponentNotDeployedException {
		}

		/*
		 * (non-Javadoc)
		 * @see com.sap.engine.services.deploy.server.DTransaction#beginLocal()
		 */
		public void beginLocal() throws DeploymentException,
		    ComponentNotDeployedException {
		}

		/*
		 * (non-Javadoc)
		 * @see com.sap.engine.services.deploy.server.DTransaction#prepare()
		 */
		public void prepare() throws DeploymentException {
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.sap.engine.services.deploy.server.DTransaction#prepareLocal()
		 */
		public void prepareLocal() throws DeploymentException {
		}

		/*
		 * (non-Javadoc)
		 * @see com.sap.engine.services.deploy.server.DTransaction#commit()
		 */
		public void commit() {
		}

		/*
		 * (non-Javadoc)
		 * @see com.sap.engine.services.deploy.server.DTransaction#commitLocal()
		 */
		public void commitLocal() {
		}

		/*
		 * (non-Javadoc)
		 * @see com.sap.engine.services.deploy.server.DTransaction#rollback()
		 */
		public void rollback() {
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.sap.engine.services.deploy.server.DTransaction#rollbackLocal()
		 */
		public void rollbackLocal() {
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.sap.engine.services.deploy.server.DTransaction#rollbackPrepare()
		 */
		public void rollbackPrepare() {
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.sap.engine.services.deploy.server.DTransaction#rollbackPrepareLocal
		 * ()
		 */
		public void rollbackPrepareLocal() {
		}

		@Override
		protected Hashtable<ContainerInterface, Properties> getConcernedContainers(
			Hashtable<String, File[]> allContFiles,
			ContainerDeploymentInfo containerInfo) throws DeploymentException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int[] getRemoteParticipants() {
			// TODO Auto-generated method stub
			return null;
		}
	}
}