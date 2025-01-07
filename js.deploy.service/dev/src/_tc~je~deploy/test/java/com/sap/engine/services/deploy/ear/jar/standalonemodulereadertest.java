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
import java.io.IOException;
import java.util.Properties;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.sap.engine.lib.io.FileUtils;
import com.sap.engine.services.deploy.DeployService;

/**
 * @author Luchesar Cekov
 */
public class StandaloneModuleReaderTest extends TestCase {
	private static final String TESTING_APPLICATION_PROVIDER = "testingApplicationProvider";

	private static final String TESTING_APPLICATION_NAME = "testingApplicationName";

	private File tmpDir;

	public void setUp() throws Exception {
		SimpleEarDescriptorPopulatorTest.initJ2EEContainers();
		tmpDir = new File(System.currentTimeMillis() + "");
		tmpDir.mkdirs();
	}

	public void tearDown() throws Exception {
		tmpDir.delete();
	}

	public void testReadStandaloneWebModule_1() throws Exception {
		File file = FileFromPath
				.getFileFromClassPath(
						"com/sap/engine/services/deploy/ear/jar/jee5/standalone/jsp_tagext_resource_httplistener_web.war.tmp",
						tmpDir);
		String standaloneModuleFilePath = getTmpFile(file.getAbsolutePath())
				.getAbsolutePath();
		StandaloneModuleReader reader = new StandaloneModuleReader(new File(
				standaloneModuleFilePath), tmpDir, new Properties());
		try {
			reader.setAnnotationClassLoader(getClass().getClassLoader());
			reader.read();
			EarDescriptor desc = reader.getDescriptor();
			Assert.assertEquals(1, desc.getAllModules().size());
			Assert.assertEquals(1, desc.getWEBs().length);
			Assert.assertEquals(0, desc.getEJBs().length);
			Assert.assertEquals(0, desc.getConnectors().length);
			Assert.assertEquals(0, desc.getClients().length);

			Assert.assertEquals("jsp_tagext_resource_httplistener_web", desc
					.getWEBs()[0].getContextRoot());
			Assert.assertEquals("jsp_tagext_resource_httplistener_web.war", desc
					.getWEBs()[0].getUri());
			Assert.assertTrue(desc.getAnnotations().getClasses().size() > 0);

			Assert.assertEquals(2, reader.getTempDir().list().length);
			Assert.assertEquals("jsp_tagext_resource_httplistener_web.extracted.war",
					reader.getTempDir().list()[0]);
			Assert.assertEquals("jsp_tagext_resource_httplistener_web.war", reader
					.getTempDir().list()[1]);
		} finally {
			reader.clear();
		}
	}

	private File getTmpFile(final String absoluteFilePath) throws IOException {
		String newFilePath = absoluteFilePath.substring(0, absoluteFilePath
				.length()
				- ".tmp".length());
		String srcFile = absoluteFilePath;
		File resultFile = new File(tmpDir, new File(newFilePath).getName());
		FileUtils.copy(new File(srcFile), resultFile.getAbsolutePath());
		return resultFile;
	}

	public void testReadStandaloneWebModule_2() throws Exception {
		File file = FileFromPath
				.getFileFromClassPath(
						"com/sap/engine/services/deploy/ear/jar/jee5/standalone/jsp_tagext_resource_httplistener_web.war.tmp",
						tmpDir);
		String standaloneModuleFilePath = getTmpFile(file.getAbsolutePath())
				.getAbsolutePath();
		;
		Properties properties = new Properties();
		properties.put(DeployService.applicationProperty, TESTING_APPLICATION_NAME);
		properties
				.put(DeployService.providerProperty, TESTING_APPLICATION_PROVIDER);
		StandaloneModuleReader reader = new StandaloneModuleReader(new File(
				standaloneModuleFilePath), tmpDir, properties);
		try {
			reader.setAnnotationClassLoader(getClass().getClassLoader());
			reader.read();
			EarDescriptor desc = reader.getDescriptor();

			Assert.assertEquals(TESTING_APPLICATION_NAME, desc.getDisplayName());
			Assert.assertEquals(TESTING_APPLICATION_PROVIDER, desc.getProviderName());
		} finally {
			reader.clear();
		}
	}

	public void testReadStandaloneWithSoftwareSubtype() throws Exception {
		String standaloneModuleFilePath = getTmpFile(
				FileFromPath
						.getFileFromClassPath(
								"com/sap/engine/services/deploy/ear/jar/jee5/standalone/jsp_tagext_resource_httplistener_web.war.tmp",
								tmpDir).getAbsolutePath()).getAbsolutePath();
		Properties properties = new Properties();
		properties.put(DeployService.softwareType, "J2EE");
		properties.put(DeployService.softwareSubType, "jar");
		StandaloneModuleReader reader = new StandaloneModuleReader(new File(
				standaloneModuleFilePath), tmpDir, properties);
		reader.setAnnotationClassLoader(getClass().getClassLoader());
		reader.read();
		EarDescriptor desc = reader.getDescriptor();
		try {

			Assert.assertEquals(2, desc.getAllModules().size());
			Assert.assertEquals(0, desc.getWEBs().length);
			Assert.assertEquals(1, desc.getEJBs().length);
			Assert.assertEquals(0, desc.getConnectors().length);
			Assert.assertEquals(1, desc.getClients().length);

			Assert.assertEquals("jsp_tagext_resource_httplistener_web.jar", desc
					.getEJBs()[0].getName());

			standaloneModuleFilePath = getTmpFile(
					FileFromPath
							.getFileFromClassPath(
									"com/sap/engine/services/deploy/ear/jar/jee5/standalone/fake.standalone.module",
									tmpDir).getAbsolutePath()).getAbsolutePath();

			properties.put(DeployService.softwareType, "J2EE");
			properties.put(DeployService.softwareSubType, "rar");
		} finally {
			reader.clear();
		}
		try {

			reader = new StandaloneModuleReader(new File(standaloneModuleFilePath),
					tmpDir, properties);
			reader.setAnnotationClassLoader(getClass().getClassLoader());
			reader.read();
			desc = reader.getDescriptor();
			Assert.assertEquals(1, desc.getAllModules().size());
			Assert.assertEquals(0, desc.getWEBs().length);
			Assert.assertEquals(0, desc.getEJBs().length);
			Assert.assertEquals(1, desc.getConnectors().length);
			Assert.assertEquals(0, desc.getClients().length);

			Assert.assertEquals("fake.standalone.rar", desc.getConnectors()[0]
					.getName());

			properties.put(DeployService.softwareType, "J2EE");
			properties.put(DeployService.softwareSubType, "war");
		} finally {
			reader.clear();
		}
		try {

			reader = new StandaloneModuleReader(new File(standaloneModuleFilePath),
					tmpDir, properties);
			reader.setAnnotationClassLoader(getClass().getClassLoader());
			reader.read();
			desc = reader.getDescriptor();
			Assert.assertEquals(1, desc.getAllModules().size());
			Assert.assertEquals(1, desc.getWEBs().length);
			Assert.assertEquals(0, desc.getEJBs().length);
			Assert.assertEquals(0, desc.getConnectors().length);
			Assert.assertEquals(0, desc.getClients().length);

			Assert.assertEquals(2, reader.getTempDir().list().length);
			Assert.assertEquals("fake.standalone.extracted.war", reader.getTempDir()
					.list()[0]);
			Assert.assertEquals("fake.standalone.war", reader.getTempDir().list()[1]);
		} finally {
			reader.clear();
		}
	}

}
