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
import java.util.Set;

import junit.framework.TestCase;

import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.container.rtgen.GenerationException;
import com.sap.engine.services.deploy.container.rtgen.ModuleDetector;
import com.sap.engine.services.deploy.ear.Module;
import com.sap.engine.services.deploy.ear.jar.moduledetect.ModuleDetectorWrapper;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.server.properties.PropManagerFactory;
import com.sap.engine.services.deploy.server.utils.container.ContainerWrapper;

/**
 * @author Luchesar Cekov
 */
public class EarReaderDetectorTest extends TestCase {
	private final static String APPS_WORKS_DIR = "apps_work_dir";
	private final static String CLUSTER_ELEM_NAME = "ceName";
	private final static int CLUSTER_ELEM_ID = 0;

	private File tmpDir;
	private ContainerWrapper nonJ2eeContainer;

	public void setUp() throws Exception {
		PropManagerFactory.initInstance(
			APPS_WORKS_DIR, CLUSTER_ELEM_ID, CLUSTER_ELEM_NAME);
		
		System.getProperties().put("server.parser.inqmy", "true");
		tmpDir = new File(System.currentTimeMillis() + "");
		tmpDir.mkdirs();
		SimpleEarDescriptorPopulatorTest.initJ2EEContainers();

		ContainerInfo nonJ2eeInfo = new ContainerInfo();
		nonJ2eeInfo.setJ2EEContainer(false);
		nonJ2eeInfo.setFileExtensions(new String[] {".bot"});
		nonJ2eeInfo.setName("BotDeployer");
		nonJ2eeContainer = ContainerWrapperFactory.buildContainerWrapper(nonJ2eeInfo, "non_J2EE_Container", 1);
		Containers.getInstance().addContainer(nonJ2eeContainer.getContainerInfo().getName(), nonJ2eeContainer);
	}

	public void tearDown() throws Exception {
		tmpDir.delete();
	}

	public void testDetectModulesNoAppXML() throws Exception {
		File file = FileFromPath.getFileFromClassPath("com/sap/engine/services/deploy/ear/jar/jee5/jee5.ear.tmp", tmpDir);
		EARReader reader = new EARReader(file.getAbsolutePath(), tmpDir, new Properties());
		try {
			reader.setAnnotationClassLoader(getClass().getClassLoader());
			reader.read();
			assertEquals(3, reader.getDescriptor().getClients().length);
			assertEquals(3, reader.getDescriptor().getEJBs().length);
			assertEquals(1, reader.getDescriptor().getWEBs().length);
			assertEquals("theweb", reader.getDescriptor().getWEBs()[0].getContextRoot());    
			assertEquals(1, reader.getDescriptor().getConnectors().length);
			assertEquals(0, reader.getDescriptor().getModulesAdditional().size());
		} finally {
			reader.clear();
		}
	}

	public void testDetectModulesNoAppXML_WithNonJ2EEContainers() throws Exception {    
		File file = FileFromPath.getFileFromClassPath("com/sap/engine/services/deploy/ear/jar/jee5/jee5_with_other_modules.ear.tmp", tmpDir);
		EARReader reader = new EARReader(file.getAbsolutePath(), tmpDir, new Properties());
		try {
			reader.setAnnotationClassLoader(getClass().getClassLoader());
			reader.read();
			assertEquals(3, reader.getDescriptor().getClients().length);
			assertEquals(3, reader.getDescriptor().getEJBs().length);
			assertEquals(1, reader.getDescriptor().getWEBs().length);
			assertEquals("theweb", reader.getDescriptor().getWEBs()[0].getContextRoot());
			assertEquals(0, reader.getDescriptor().getConnectors().length);
			Set<Module> additionalModules  = reader.getDescriptor().getModulesAdditional();
			assertEquals(2, additionalModules.size());
			for (Module module : additionalModules) {
				assertTrue(module.getAbsolutePath().endsWith(".bot"));
			}
		} finally {
			reader.clear();
		}
	}

	public void testDetectModulesNoAppXML_WithNonJ2EEContainers_presentDetector() throws Exception {
		nonJ2eeContainer.setDetectorWrapper(new ModuleDetectorWrapper(new ModuleDetector() {
			public Module detectModule(File aTempDir, String aModuleRelativeFileUri)
				throws GenerationException {
				return aModuleRelativeFileUri.endsWith("1.bot") ?
					new Module(aTempDir, aModuleRelativeFileUri, nonJ2eeContainer.getContainerInfo().getName()) :
					null;
			}
		}, false));
    
		File file = FileFromPath.getFileFromClassPath("com/sap/engine/services/deploy/ear/jar/jee5/jee5_with_other_modules.ear.tmp", tmpDir);
		EARReader reader = new EARReader(file.getAbsolutePath(), tmpDir, new Properties());
		try { 
			reader.setAnnotationClassLoader(getClass().getClassLoader());    
			reader.read();
			assertEquals(3, reader.getDescriptor().getClients().length);
			assertEquals(3, reader.getDescriptor().getEJBs().length);
			assertEquals(1, reader.getDescriptor().getWEBs().length);
			assertEquals("theweb", reader.getDescriptor().getWEBs()[0].getContextRoot());
			assertEquals(0, reader.getDescriptor().getConnectors().length);
			Set<Module> additionalModules  = reader.getDescriptor().getModulesAdditional();
			assertEquals(1, additionalModules.size());
			for (Module module : additionalModules) {
				assertTrue(module.getAbsolutePath().endsWith("1.bot"));
			}
		} finally {
			reader.clear();
		}
	}

	public void testDetectModulesNoAppXML_OLD_EJB_MODULE() throws Exception {    
		File file = FileFromPath.getFileFromClassPath("com/sap/engine/services/deploy/ear/jar/jee5/ejb_sam_Hello.ear.tmp", tmpDir);
		EARReader reader = new EARReader(file.getAbsolutePath(), tmpDir, new Properties());
		try {
			reader.setAnnotationClassLoader(getClass().getClassLoader());
			reader.read();
			assertEquals(1, reader.getDescriptor().getClients().length);
			assertEquals(1, reader.getDescriptor().getEJBs().length);
			assertEquals(0, reader.getDescriptor().getWEBs().length);    
			assertEquals(0, reader.getDescriptor().getConnectors().length);
			Set<Module> additionalModules  = reader.getDescriptor().getModulesAdditional();
			assertEquals(0, additionalModules.size());
		} finally {
			reader.clear();
		}
	}
}
