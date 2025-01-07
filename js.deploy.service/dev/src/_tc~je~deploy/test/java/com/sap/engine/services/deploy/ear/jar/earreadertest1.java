/*
 * Created on 2005-3-28 by Luchesar Cekov
 */
package com.sap.engine.services.deploy.ear.jar;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.rtgen.GenerationException;
import com.sap.engine.services.deploy.container.rtgen.Generator;
import com.sap.engine.services.deploy.container.rtgen.ModuleDetector;
import com.sap.engine.services.deploy.ear.J2EEModule;
import com.sap.engine.services.deploy.ear.Module;
import com.sap.engine.services.deploy.ear.modules.EJB;
import com.sap.engine.services.deploy.ear.modules.Web;
import com.sap.engine.services.deploy.ear.rtgen.webservices.WebServicesGenerator;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.server.utils.container.ContainerWrapper;
import com.sap.lib.javalang.tool.ReadResult;

/**
 * @author Luchesar Cekov
 */
public class EARReaderTest1 extends TestCase {
	private File tmpDir;

	private static final ContainerInfo EJB_CONTAINER_INFO = new ContainerInfo();

	private static final ContainerWrapper EJB_CONTAINER = ContainerWrapperFactory.buildContainerWrapper(EJB_CONTAINER_INFO, "ejb_container", 1);

	private static final ContainerInfo WEB_CONTAINER_INFO = new ContainerInfo();

	private static final ContainerWrapper WEB_CONTAINER = ContainerWrapperFactory.buildContainerWrapper(WEB_CONTAINER_INFO, "web_container", 1);
	static {
		EJB_CONTAINER_INFO.setFileExtensions(new String[] { ".jar" });
		EJB_CONTAINER_INFO.setJ2EEContainer(true);
		EJB_CONTAINER_INFO.setJ2EEModuleName(J2EEModule.Type.ejb.name());
		EJB_CONTAINER_INFO.setName("ejb");

		WEB_CONTAINER_INFO.setFileExtensions(new String[] { ".war" });
		WEB_CONTAINER_INFO.setJ2EEContainer(true);
		WEB_CONTAINER_INFO.setJ2EEModuleName(J2EEModule.Type.web.name());
		WEB_CONTAINER_INFO.setName("servlet_jsp");
	}

	public void setUp() throws Exception {
		System.getProperties().put("server.parser.inqmy", "true");
		tmpDir = new File(System.currentTimeMillis() + "");
		tmpDir.mkdirs();
		SimpleEarDescriptorPopulatorTest.initJ2EEContainers();
	}

	public void tearDown() throws Exception {
		tmpDir.delete();
	}

	public void testWebServicesCheck() throws Exception {

		EJB_CONTAINER.getContainerInfo().setGenerator(new WebServicesGenerator());
		Containers.getInstance().addContainer(
				EJB_CONTAINER.getContainerInfo().getName(), EJB_CONTAINER);
		File file = FileFromPath.getFileFromClassPath(
				"com/sap/engine/services/deploy/ear/jar/WSEar.ear.tmp", tmpDir);
		EARReader reader = new EARReader(file.getAbsolutePath(), tmpDir,
				new Properties());
		reader.setAnnotationClassLoader(getClass().getClassLoader());
		Set<Module> modules = reader.getDescriptor().getAllModules();

		try {
			HashSet<String> modulesSet = new HashSet<String>(2);
			for (Module module : modules) {
				modulesSet.add(module.getUri());
			}

			assertTrue(modulesSet.contains("ejb1.jar"));
			assertTrue(modulesSet.contains("ejb2.jar"));
			assertTrue(modulesSet.contains("Session1Service_Config1.war"));
			assertTrue(modulesSet.contains("TestWebService_Config2.war"));

			String[] fileNames = tmpDir.list();
			modulesSet.clear();
			for (int i = 0; i < fileNames.length; i++) {
				modulesSet.add(fileNames[i]);
			}

			assertTrue(modulesSet.contains("ejb1.jar"));
			assertTrue(modulesSet.contains("Session1Service_Config1.war"));
			assertTrue(modulesSet.contains("TestWebService_Config2.war"));

			for (Module module : modules) {
				System.out.println("EARReaderTest.testWebServicesCheck->"+ module.getUri());//$JL-SYS_OUT_ERR$
			}
		} finally {
			reader.clear();
		}
	}

	public void testWebServicesCheck1() throws Exception {
		// Set<Module> modules = generateModules(new WebServicesGenerator() {
		// public boolean removeModule(String uri) {
		// return uri.endsWith("ejb1.jar");
		// }
		// });

		EJB_CONTAINER.getContainerInfo().setGenerator(new WebServicesGenerator() {
			public boolean removeModule(String uri) {
				return uri.endsWith("ejb1.jar");
			}
		});
		Containers.getInstance().addContainer(
				EJB_CONTAINER.getContainerInfo().getName(), EJB_CONTAINER);

		File file = FileFromPath.getFileFromClassPath(
				"com/sap/engine/services/deploy/ear/jar/WSEar.ear.tmp", tmpDir);
		EARReader reader = new EARReader(file.getAbsolutePath(), tmpDir,
				new Properties());
		reader.setAnnotationClassLoader(getClass().getClassLoader());
		Set<Module> modules = reader.getDescriptor().getAllModules();

		try {
		} finally {
			reader.clear();
		}

		HashSet<String> modulesSet = new HashSet<String>(3);
		for (Module module : modules) {
			modulesSet.add(module.getUri());
		}

		assertTrue(!modulesSet.contains("ejb1.jar"));
		assertTrue(modulesSet.contains("ejb2.jar"));
		assertTrue(modulesSet.contains("Session1Service_Config1.war"));
		assertTrue(modulesSet.contains("TestWebService_Config2.war"));

		// String[] fileNames = tmpDir.list();
		// modulesSet.clear();
		// for (int i = 0; i < fileNames.length; i++) {
		// modulesSet.add(fileNames[i]);
		// }
		//
		// assertTrue(!modulesSet.contains("ejb1.jar"));
		// assertTrue(modulesSet.contains("Session1Service_Config1.war"));
		// assertTrue(modulesSet.contains("TestWebService_Config2.war"));

		for (Module module : modules) {
			System.out.println("EARReaderTest.testWebServicesCheck->"+ module.getUri());//$JL-SYS_OUT_ERR$
		}
	}

	public void testWebServicesCheck2() throws Exception {
		try {

			EJB_CONTAINER.getContainerInfo().setGenerator(new Generator() {

				public J2EEModule[] generate(File arg0, String arg1)
						throws GenerationException {
					J2EEModule module = new EJB(arg0, "fake/uri/fakeEjb.jar");
					return new J2EEModule[] { module };
				}

				public boolean supportsFile(String arg0) {
					return true;
				}

				public boolean removeModule(String arg0) {
					return false;
				}
			});
			Containers.getInstance().addContainer(
					EJB_CONTAINER.getContainerInfo().getName(), EJB_CONTAINER);

			File file = FileFromPath.getFileFromClassPath(
					"com/sap/engine/services/deploy/ear/jar/WSEar.ear.tmp", tmpDir);
			EARReader reader = new EARReader(file.getAbsolutePath(), tmpDir,
					new Properties());
			try {
				reader.setAnnotationClassLoader(getClass().getClassLoader());
				Set<Module> modules = reader.getDescriptor().getAllModules();
			} finally {
				reader.clear();
			}

			assertFalse(true);
		} catch (DeploymentException e) {// $JL-EXC$
			// works fine
			e.printStackTrace();
		}
	}

	public void testWebServicesCheck3() throws Exception {
		File file = FileFromPath.getFileFromClassPath(
				"com/sap/engine/services/deploy/ear/jar/WSEar.ear.tmp", tmpDir);
		EJB_CONTAINER.getContainerInfo().setGenerator(new WebServicesGenerator());
		generateModules1(EJB_CONTAINER, file.getAbsolutePath());
	}

	private Set<Module> generateModules1(final ContainerWrapper container,
			String earFile) throws IOException, DeploymentException {
		Containers.getInstance().addContainer(
				container.getContainerInfo().getName(), container);
		EARReader reader = new EARReader(earFile, tmpDir, new Properties());
		reader.setAnnotationClassLoader(getClass().getClassLoader());
		Set<Module> modules = reader.getDescriptor().getAllModules();
		try {
		} finally {
			reader.clear();
		}

		return modules;
	}

	public void testGenerateEJBModulesInEmptyXML() throws Exception {
		Generator gen = new Generator() {
			public boolean removeModule(String aModuleRelativeFileUri) {
				return false;
			}

			public boolean supportsFile(String aModuleRelativeFileUri) {
				return true;
			}

			public J2EEModule[] generate(File aTempDir, String aModuleRelativeFileUri)
					throws GenerationException {
				return new J2EEModule[] { new EJB(aTempDir, aModuleRelativeFileUri) };
			}

		};
		EJB_CONTAINER.getContainerInfo().setGenerator(gen);
		EJB_CONTAINER.getContainerInfo().setFileExtensions(new String[] { ".jar" });
		EJB_CONTAINER.getContainerInfo().setModuleDetector(new ModuleDetector() {
			public Module detectModule(File aTempDir, String aModuleRelativeFileUri)
					throws GenerationException {
				return aModuleRelativeFileUri.endsWith(".jar") ? new EJB(aTempDir,
						aModuleRelativeFileUri) : null;
			}
		});
		// Set<Module> modules = generateModules1(EJB_CONTAINER,
		// "resources/com/sap/engine/services/deploy/ear/jar/WSEarJarsNoApplicationXML.bot");

		Containers.getInstance().addContainer(
				EJB_CONTAINER.getContainerInfo().getName(), EJB_CONTAINER);
		EARReader reader = new EARReader(
				EARReaderTest1.class
						.getClassLoader()
						.getResource(
								"com/sap/engine/services/deploy/ear/jar/WSEarJarsNoApplicationXML.bot")
						.getFile(), tmpDir, new Properties());
		try {
			reader.setAnnotationClassLoader(getClass().getClassLoader());
			Set<Module> modules = reader.getDescriptor().getAllModules();
			Assert.assertEquals(2, modules.size());
			for (Module module : modules) {
				assertEquals(EJB.class, module.getClass());
				assertEquals(J2EEModule.Type.ejb, ((EJB) module).getType());
			}
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

	public void testGenerateWEBModulesInEmptyXML() throws Exception {
		Generator wargen = new Generator() {
			public boolean removeModule(String aModuleRelativeFileUri) {
				return false;
			}

			public boolean supportsFile(String aModuleRelativeFileUri) {
				return aModuleRelativeFileUri.endsWith(".war");
			}

			public J2EEModule[] generate(File aTempDir, String aModuleRelativeFileUri)
					throws GenerationException {
				StringBuffer contextRoot = new StringBuffer(aModuleRelativeFileUri);
				contextRoot.delete(contextRoot.length() - ".war".length(), contextRoot
						.length());
				contextRoot.delete(0, contextRoot.lastIndexOf("/") + 1);
				return new J2EEModule[] { new Web(aTempDir, aModuleRelativeFileUri,
						contextRoot.toString()) };
			}
		};

		WEB_CONTAINER.getContainerInfo().setGenerator(wargen);
		WEB_CONTAINER.getContainerInfo().setModuleDetector(new ModuleDetector() {
			public Module detectModule(File aTempDir, String aModuleRelativeFileUri)
					throws GenerationException {
				StringBuffer contextRoot = new StringBuffer(aModuleRelativeFileUri);
				contextRoot.delete(contextRoot.length() - ".war".length(), contextRoot
						.length());
				contextRoot.delete(0, contextRoot.lastIndexOf("/") + 1);
				return aModuleRelativeFileUri.endsWith(".war") ? new Web(aTempDir,
						aModuleRelativeFileUri, contextRoot.toString()) : null;
			}
		});
		// Set<Module> modules = generateModules1(WEB_CONTAINER,
		// "resources/com/sap/engine/services/deploy/ear/jar/WSEarWarsNoApplicationXML.bot");

		Containers.getInstance().addContainer(
				WEB_CONTAINER.getContainerInfo().getName(), WEB_CONTAINER);

		EARReader reader = new EARReader(
				EARReaderTest1.class
						.getClassLoader()
						.getResource(
								"com/sap/engine/services/deploy/ear/jar/WSEarWarsNoApplicationXML.bot")
						.getFile(), tmpDir, new Properties());
		try {
			reader.setAnnotationClassLoader(getClass().getClassLoader());
			Set<Module> modules = reader.getDescriptor().getAllModules();

			Assert.assertEquals(2, modules.size());

			Module m = getModule(modules, new Web(new File(""), "CalcWeb.war",
					"CalcWeb"));
			assertNotNull(m);
			assertSame(Web.class, m.getClass());
			assertNull(((Web) m).getAlt_dd());

			m = getModule(modules, new Web(new File(""), "in/CalcWeb.war", "CalcWeb"));
			assertNotNull(m);
			assertSame(Web.class, m.getClass());
			assertNull(((Web) m).getAlt_dd());
		} finally {
			reader.clear();
		}
	}
}
