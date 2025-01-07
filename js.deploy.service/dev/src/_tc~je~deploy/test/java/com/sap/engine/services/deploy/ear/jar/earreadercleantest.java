/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.ear.jar;

import java.io.File;
import java.util.Properties;

import junit.framework.TestCase;

import com.sap.engine.lib.io.FileUtils;
import com.sap.engine.services.deploy.server.properties.PropManagerFactory;

/**
 * @author Luchesar Cekov
 */
public class EarReaderCleanTest extends TestCase {
	private final static String APPS_WORKS_DIR = "apps_work_dir";
	private final static String CLUSTER_ELEM_NAME = "ceName";
	private final static int CLUSTER_ELEM_ID = 0;
	private File tmpDir;

	public void setUp() throws Exception {
		PropManagerFactory.initInstance(
			APPS_WORKS_DIR, CLUSTER_ELEM_ID, CLUSTER_ELEM_NAME);

		tmpDir = new File(System.currentTimeMillis() + "");
		tmpDir.mkdirs();
		System.out.println("temp dir _> " + tmpDir.getAbsolutePath());//$JL-SYS_OUT_ERR$
		SimpleEarDescriptorPopulatorTest.initJ2EEContainers();
	}

	public void tearDown() {
		FileUtils.deleteDirectory(tmpDir);
	}

	public void testClean() throws Exception {

		for (int i = 0; i < 10; i++) {
			File file = FileFromPath.getFileFromClassPath(
					"com/sap/engine/services/deploy/ear/jar/WSEar.ear.tmp", tmpDir);
			EARReader reader = new EARReader(file.getAbsolutePath(), tmpDir,
					new Properties());
			reader.setAnnotationClassLoader(getClass().getClassLoader());
			reader.getDescriptor();
			reader.clear();

			assertFalse(tmpDir.exists());
		}
	}

	public void test1() throws Exception {
		File file = FileFromPath
				.getFileFromClassPath(
						"com/sap/engine/services/deploy/ear/jar/ears/ejb_connection_sharing.ear.tmp",
						tmpDir);
		EARReader reader = new EARReader(file.getAbsolutePath(), tmpDir,
				new Properties());
		reader.setAnnotationClassLoader(getClass().getClassLoader());
		try {
			EarDescriptor descriptor = reader.getDescriptor();
		} finally {
			reader.clear();
		}
	}
}
