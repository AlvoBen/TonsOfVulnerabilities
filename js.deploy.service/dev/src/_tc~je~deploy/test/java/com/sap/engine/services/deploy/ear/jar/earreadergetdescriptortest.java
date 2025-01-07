/*
 * Created on 2005-3-30 by Luchesar Cekov
 */
package com.sap.engine.services.deploy.ear.jar;

import java.io.File;
import java.util.Properties;
import java.util.Set;

import junit.framework.TestCase;

import com.sap.engine.lib.io.FileUtils;
import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.ReferenceObjectIntf;
import com.sap.engine.services.deploy.ear.J2EEModule;
import com.sap.engine.services.deploy.ear.Module;
import com.sap.engine.services.deploy.ear.common.SecurityRoles;
import com.sap.engine.services.deploy.ear.modules.Connector;
import com.sap.engine.services.deploy.ear.modules.EJB;
import com.sap.engine.services.deploy.ear.modules.Java;
import com.sap.engine.services.deploy.ear.modules.Web;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.server.properties.PropManagerFactory;

/**
 * @author Luchesar Cekov
 */
public class EARReaderGetDescriptorTest extends TestCase {
	private File tmpDir;

	public void setUp() throws Exception {
		PropManagerFactory.initInstance("appsWorkDir", 0, "ceName");
		SimpleEarDescriptorPopulatorTest.initJ2EEContainers();

		Containers.getInstance().addContainer("FooBarContainer",
				ContainerWrapperFactory.buildContainerWrapper(new ContainerInfo(), "FooBarContainer", 1));
		Containers.getInstance().addContainer("JMSConnector",
				ContainerWrapperFactory.buildContainerWrapper(new ContainerInfo(), "JMSConnector", 1));
		Containers.getInstance().addContainer("Portals",
				ContainerWrapperFactory.buildContainerWrapper(new ContainerInfo(), "Portals", 1));

		tmpDir = new File(System.currentTimeMillis() + "");
		tmpDir.mkdirs();
	}

	public void tearDown() {
		FileUtils.deleteDirectory(tmpDir);
	}

	public void testReadMaximalEAR() throws Exception {
		File file = FileFromPath.getFileFromClassPath(
				"com/sap/engine/services/deploy/ear/jar/maximalEar.zip.tmp", tmpDir);
		teztPopulateFromApplicationXML(file.getAbsolutePath());
	}

	// Excluded
	public void testGetDescriptor14() throws Exception {
		File file = FileFromPath.getFileFromClassPath(
				"com/sap/engine/services/deploy/ear/jar/testEar1.4.zip.tmp", tmpDir);
		teztPopulateFromApplicationXML(file.getAbsolutePath());
	}

	public void test1GetDescriptor14() throws Exception {
		File file = FileFromPath.getFileFromClassPath(
				"com/sap/engine/services/deploy/ear/jar/testEar1.4.1.zip.tmp", tmpDir);
		teztPopulateFromApplicationXML(file.getAbsolutePath());
	}

	public void testReadMinimalEAR() throws Exception {
		File file = FileFromPath.getFileFromClassPath(
				"com/sap/engine/services/deploy/ear/jar/minimalEar.zip.tmp", tmpDir);
		EARReader reader = createEARReader(file.getAbsolutePath());
		try {
			reader.setAnnotationClassLoader(getClass().getClassLoader());
			EarDescriptor descriptor = reader.getDescriptor();
			reader.read();
			validate1(descriptor);
		} finally {
			reader.clear();
		}

	}

	public void teztPopulateFromApplicationXML(String filePath) throws Exception {
		EARReader reader = createEARReader(filePath);
		try {
			reader.setAnnotationClassLoader(getClass().getClassLoader());
			EarDescriptor descriptor = reader.getDescriptor();
			reader.read();
			validate(descriptor);
		} finally {
			reader.clear();
		}

	}

	private static Module getModule(Set<Module> fromSet, Module aModule) {
		for (Module module : fromSet) {
			if (aModule instanceof J2EEModule) {
				ContainerInterface ci = Containers.getInstance().getJ2eeContainer(
						((J2EEModule) aModule).getType());
				aModule.setModuleType(ci.getContainerInfo().getName());
			}
			if (aModule.equals(module)) {
				return module;
			}
		}

		return null;
	}

	/**
	 * @param descriptor
	 */
	static void validate(EarDescriptor descriptor) {
		assertEquals("EAR description", descriptor.getDescription());
		assertEquals("testApplication", descriptor.getDisplayName());
		assertEquals("icons/small", descriptor.getSmallIconName());
		assertEquals("icons/large", descriptor.getLargeIconName());

		Set<Module> modules = descriptor.getAllModules();
		assertEquals(9, modules.size());

		File tmpDir = new File("");

		Module m = getModule(modules, new Web(tmpDir, "web/webModule1.war",
				"webModule1ContextRoot"));
		assertNotNull(m);
		assertEquals("webModule1ContextRoot", ((Web) m).getContextRoot());
		assertSame(Web.class, m.getClass());
		assertNull(((Web) m).getAlt_dd());

		m = getModule(modules, new Web(tmpDir, "web/webModule2.war",
				"webModule2ContextRoot"));
		assertNotNull(m);
		assertEquals("webModule2ContextRoot", ((Web) m).getContextRoot());
		assertSame(Web.class, m.getClass());
		assertNull(((Web) m).getAlt_dd());

		m = getModule(modules, new EJB(tmpDir, "ejb/ejbProject.jar"));
		assertNotNull(m);
		assertSame(EJB.class, m.getClass());
		assertEquals("test.dd", ((EJB) m).getAlt_dd());

		m = getModule(modules, new EJB(tmpDir, "ejb/ejbProject1.jar"));
		assertNotNull(m);
		assertSame(EJB.class, m.getClass());
		assertNull(((EJB) m).getAlt_dd());

		m = getModule(modules, new Connector(tmpDir, "connector/connector.jar"));
		assertNotNull(m);
		assertSame(Connector.class, m.getClass());
		assertNull(((Connector) m).getAlt_dd());

		m = getModule(modules, new Java(tmpDir, "java/apps/javaApp.jar"));
		assertNotNull(m);
		assertSame(Java.class, m.getClass());
		assertNull(((Java) m).getAlt_dd());

		assertNotNull(getModule(modules, new Module(tmpDir,
				"jms/SonicDestination.xml", "JMSConnector")));
		assertNotNull(getModule(modules, new Module(tmpDir, "portal/portalApp.par",
				"Portals")));
		assertNotNull(getModule(modules, new Module(tmpDir,
				"fooBar/SonicDestination.fobar", "FooBarContainer")));

		SecurityRoles[] roles = descriptor.getRoles();
		assertEquals(1, roles.length);
		assertEquals("test role description", roles[0].getDescription());
		assertEquals("testRoleName", roles[0].getName());

		ReferenceObjectIntf[] references = descriptor.getReferences();
		assertEquals(2, references.length);

		assertEquals("sap.com", references[0].getReferenceProviderName());
		assertEquals("hard", references[0].getReferenceType());
		assertEquals("application", references[0].getReferenceTargetType());
		assertEquals("restartHardReferancesAfterUpdateRefToAppName", references[0]
				.getReferenceTarget());

		assertEquals("ma.ga", references[1].getReferenceProviderName());
		assertEquals("weak", references[1].getReferenceType());
		assertEquals("library", references[1].getReferenceTargetType());
		assertEquals("testingLibrary", references[1].getReferenceTarget());

		assertEquals("/classes/myClass.jar;myLibDir/test;myLIbDir/classes.jar",
				descriptor.getClassPath());

		assertEquals("foo.bar", descriptor.getProviderName());
		assertEquals("disable", descriptor.getFailOverValue());
		assertEquals("always", descriptor.getStartUpString(descriptor.getStartUp()));
	}

	static void validate1(EarDescriptor descriptor) {
		assertEquals("EAR description", descriptor.getDescription());
		assertEquals("testApplication", descriptor.getDisplayName());
		assertNull(descriptor.getSmallIconName());
		assertNull(descriptor.getLargeIconName());

		Set<Module> modules = descriptor.getAllModules();
		assertEquals(1, modules.size());

		SecurityRoles[] roles = descriptor.getRoles();
		assertEquals(0, roles.length);

		ReferenceObjectIntf[] references = descriptor.getReferences();
		assertEquals(1, references.length);

		// assertEquals("sap.com", references[0].getReferenceProviderName());
		assertEquals("weak", references[0].getReferenceType());
		assertEquals("library", references[0].getReferenceTargetType());
		assertEquals("testingLibrary", references[0].getReferenceTarget());

		// assertEquals("sap.com",descriptor.getProviderName());
		assertEquals("disable", descriptor.getFailOverValue());
		assertEquals("lazy", descriptor.getStartUpString(descriptor.getStartUp()));
	}

	private static final String[] parseEarHotFiles = { "all_containers.ear.tmp",
			"all_containers_.ear.tmp", "com.sap.portal.heartbeat.sda.ear.tmp",
			"jms_connectors.ear.tmp", "lifecycle_test1.ear.tmp", "lifecycle_test.ear.tmp",
			"messageTest1.ear.tmp", "messageTest.ear.tmp", "ModulesNestedInDirs.ear.tmp",
			"temp_EAR258.ear.tmp", "test1.ear.tmp", "test.ear.tmp",
			"XA_local_transaction_test1.ear.tmp", "XA_local_transaction_test.ear.tmp" };

	public void testParseEarHot() throws Exception {

		for (int i = 0; i < parseEarHotFiles.length; i++) {
			File files = FileFromPath.getFileFromClassPath(
					"com/sap/engine/services/deploy/ear/jar/earParseHot/"
							+ parseEarHotFiles[i], tmpDir);
			try {
				if (files.isDirectory())
					continue;
				System.out.print("testParseEarHot PARSING FILE"+ files.getCanonicalPath() + "...");//$JL-SYS_OUT_ERR$
				EARReader reader = createEARReader(files.getCanonicalPath());
				try {
					reader.setAnnotationClassLoader(getClass().getClassLoader());
					reader.getDescriptor();
					reader.read();
				} finally {
					reader.clear();
				}
				System.out.println("done");//$JL-SYS_OUT_ERR$
			} catch (Exception e) {
				System.out.println("ERROR: parsing file " + files.getCanonicalPath());//$JL-SYS_OUT_ERR$
				throw e;
			}
		}
	}

	public void testEarWithMissingModuleFileInApplicatioXML() throws Exception {
		try {
			File file = FileFromPath.getFileFromClassPath(
					"com/sap/engine/services/deploy/ear/jar/test2.ear.tmp", tmpDir);
			EARReader reader = createEARReader(file.getAbsolutePath());
			try {
				reader.getDescriptor();
				reader.read();
			} finally {
				reader.clear();
			}
			assertTrue(false);
		} catch (ServerDeploymentException be) {
			// $JL-EXC$
			// it works fine
		}
	}

	private EARReader createEARReader(String fileName) throws Exception {
		return new EARReader(fileName, tmpDir, new Properties());
	}

}