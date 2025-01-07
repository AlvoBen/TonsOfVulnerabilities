/*
 * Created on 2005-3-21 by Luchesar Cekov
 */
package com.sap.engine.services.deploy.ear.jar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sap.engine.lib.converter.ConversionContext;
import com.sap.engine.lib.converter.ConversionException;
import com.sap.engine.lib.converter.ConverterTool;
import com.sap.engine.lib.converter.DescriptorParseException;
import com.sap.engine.lib.converter.DescriptorParseTool;
import com.sap.engine.lib.converter.FileNameExceptionPair;
import com.sap.engine.lib.converter.IJ2EEDescriptorConverter;
import com.sap.engine.lib.converter.impl.ApplicationConverter;
import com.sap.engine.lib.descriptors5.application.ApplicationType;
import com.sap.engine.lib.io.FileUtils;
import com.sap.engine.lib.processor.SchemaProcessor;
import com.sap.engine.lib.processor.SchemaProcessorFactory;
import com.sap.engine.services.deploy.container.AdditionalAppInfo;
import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.container.ReferenceObjectIntf;
import com.sap.engine.services.deploy.ear.EARExceptionConstants;
import com.sap.engine.services.deploy.ear.J2EEModule;
import com.sap.engine.services.deploy.ear.Module;
import com.sap.engine.services.deploy.ear.SimpleEarDescriptor;
import com.sap.engine.services.deploy.ear.common.SecurityRoles;
import com.sap.engine.services.deploy.ear.exceptions.BaseIOException;
import com.sap.engine.services.deploy.ear.jar.initcontainer.AppClientModuleDetector;
import com.sap.engine.services.deploy.ear.jar.initcontainer.ConnectorModelDetector;
import com.sap.engine.services.deploy.ear.jar.initcontainer.EjbModuleDetector;
import com.sap.engine.services.deploy.ear.jar.initcontainer.WebModuleDetector;
import com.sap.engine.services.deploy.ear.modules.Connector;
import com.sap.engine.services.deploy.ear.modules.EJB;
import com.sap.engine.services.deploy.ear.modules.Java;
import com.sap.engine.services.deploy.ear.modules.Web;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.server.utils.container.ContainerWrapper;

/**
 * @author Luchesar Cekov
 */
public class SimpleEarDescriptorPopulatorTest extends TestCase {
	private File tmpDir;

	public void setUp() throws Exception {
		System.getProperties().put("server.parser.inqmy", "true");
		initJ2EEContainers();
		tmpDir = new File(System.currentTimeMillis() + "");
		tmpDir.mkdirs();
	}

	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(tmpDir);
	}

	public static void initJ2EEContainers() throws Exception {
		Containers.getInstance().clear();
		ContainerInfo webInfo = new J2EEContainerInfoWrapper("servlet_jsp",
				J2EEModule.Type.web);
		webInfo.setFileExtensions(new String[] { ".war" });
		webInfo.setModuleDetector(new WebModuleDetector());
		ContainerWrapper web = ContainerWrapperFactory.buildContainerWrapper(webInfo, "web_container",1);

		ContainerInfo ejbInfo = new J2EEContainerInfoWrapper("ejb",
				J2EEModule.Type.ejb);
		ejbInfo.setModuleDetector(new EjbModuleDetector());
		ejbInfo.setFileExtensions(new String[] { ".jar" });
		ContainerWrapper ejb = ContainerWrapperFactory.buildContainerWrapper(ejbInfo, "ejb_container",1);

		ContainerInfo connectorInfo = new J2EEContainerInfoWrapper(
				"connector_container", J2EEModule.Type.connector);
		connectorInfo.setFileExtensions(new String[] { ".rar" });
		connectorInfo.setModuleDetector(new ConnectorModelDetector());
		ContainerWrapper connector = ContainerWrapperFactory.buildContainerWrapper(connectorInfo, "connector",1);

		ContainerInfo javaInfo = new J2EEContainerInfoWrapper("app_client",
				J2EEModule.Type.java);
		javaInfo.setFileExtensions(new String[] { ".jar" });
		javaInfo.setModuleDetector(new AppClientModuleDetector());
		ContainerWrapper java = ContainerWrapperFactory.buildContainerWrapper(javaInfo, "app_client", 1 );

		Containers.getInstance()
				.addContainer(web.getContainerInfo().getName(), web);
		Containers.getInstance()
				.addContainer(ejb.getContainerInfo().getName(), ejb);
		Containers.getInstance().addContainer(
				connector.getContainerInfo().getName(), connector);
		Containers.getInstance().addContainer(java.getContainerInfo().getName(),
				java);
	}

	public void testPopulateFromApplicationXML() throws Exception {
		File file = FileFromPath.getFileFromClassPath(
				"com/sap/engine/services/deploy/ear/jar/sampleApplication1.3.1.xml",
				tmpDir);
		teztPopulateFromApplicationXML(file.getAbsolutePath());
	}

	public void testPopulateFromApplicationXML1() throws Exception {
		File file = FileFromPath.getFileFromClassPath(
				"com/sap/engine/services/deploy/ear/jar/sampleApplication1.4.1.xml",
				tmpDir);
		teztPopulateFromApplicationXML(file.getAbsolutePath());
	}

	public void testPopulateFromApplicationXML2() throws Exception {
		File file = FileFromPath.getFileFromClassPath(
				"com/sap/engine/services/deploy/ear/jar/sampleApplication1.4.2.xml",
				tmpDir);
		teztPopulateFromApplicationXML(file.getAbsolutePath());
	}

	public void testPopulateFromApplicationXML3() throws Exception {
		File file = FileFromPath.getFileFromClassPath(
				"com/sap/engine/services/deploy/ear/jar/sampleApplication1.4.3.xml",
				tmpDir);
		InputStream in = new FileInputStream(file);
		// DescriptorParseTool parserr = DescriptorParseTool.getInstance();
		EarDescriptor descriptor = new EarDescriptor();
		EarDescriptorPopulator.populateFromApplicationXML(new File(""), descriptor,
				parseApplicationXML(in, true));
		validate1(descriptor);
	}

	public void testPopulateFromApplicationXML4() throws Exception {
		File file = FileFromPath.getFileFromClassPath(
				"com/sap/engine/services/deploy/ear/jar/sampleApplication1.4.4.xml",
				tmpDir);
		InputStream in = new FileInputStream(file);
		// DescriptorParseTool parserr = DescriptorParseTool.getInstance();
		EarDescriptor descriptor = new EarDescriptor();
		EarDescriptorPopulator.populateFromApplicationXML(new File(""), descriptor,
				parseApplicationXML(in, true));
		validate2(descriptor);
	}

	public void testPopulateFromApplicationFakeXML() throws Exception {
		try {
			File file = FileFromPath.getFileFromClassPath(
					"com/sap/engine/services/deploy/ear/jar/fakeApplication1.4.1.xml",
					tmpDir);
			teztPopulateFromApplicationXML(file.getAbsolutePath());
			assertFalse(
					"com/sap/engine/services/deploy/ear/jar/fakeApplication1.4.1.xml is wrong, but is parsed without exceptions",
					true);
		} catch (DescriptorParseException e) {// $JL-EXC$ works OK
			return;
		} catch (SAXParseException e) { // $JL-EXC$ works OK
			return;
		} catch (ConversionException e) { // $JL-EXC$ works OK
			return;
		} catch (Throwable t) {// $JL-EXC$
			StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
			assertFalse(
				"com/sap/engine/services/deploy/ear/jar/fakeApplication1.4.1.xml is wrong, but is parsed with incorect exception\n[" +
				sw.toString() + "]", true);
		}
	}

	public void testPopulateFromApplicationFakeXML1() throws Exception {
		try {
			File file = FileFromPath.getFileFromClassPath(
					"com/sap/engine/services/deploy/ear/jar/fakeNSApplication1.4.xml",
					tmpDir);
			teztPopulateFromApplicationXML(file.getAbsolutePath());
			assertFalse(
					"com/sap/engine/services/deploy/ear/jar/fakeApplication1.4.1.xml is wrong, but is parsed without exceptions",
					true);
		} catch (DescriptorParseException e) {// $JL-EXC$ works OK
			return;
		} catch (SAXParseException e) { // $JL-EXC$ works OK
			return;
		} catch (ConversionException e) { // $JL-EXC$ works OK
			return;
		} catch (Throwable t) {// $JL-EXC$
			assertFalse(
					"com/sap/engine/services/deploy/ear/jar/fakeApplication1.4.1.xml is wrong, but is parsed with incorect exception "
							+ t.getMessage(), true);
		}
	}
	
	public void testExceptions (){
		try {
			teztBaseIOException();
		} catch (BaseIOException e) {
			e.getLocalizedMessage();
			e.printStackTrace();
			return;
		}
		fail();
	}
	
	public void teztBaseIOException() throws BaseIOException{ 
				
		try {
			File file = FileFromPath.getFileFromClassPath("com/sap/engine/services/deploy/ear/jar/sampleApplication1.3.1.xml",
				tmpDir);
			InputStream in = new FileInputStream(file);
			DescriptorParseTool parserr = DescriptorParseTool.getInstance();
			EarDescriptor descriptor = new EarDescriptor();
			in.close();
			FileUtils.deleteDirectory(file);
			
			ApplicationType appType = parseApplicationXML(in, true); 
			EarDescriptorPopulator.populateFromApplicationXML(new File(""), descriptor, appType);
			
		} catch (Exception e) {
			throw new BaseIOException(EARExceptionConstants.ERRORS_WHILE_PARSING_J2EE_XML, e);
			} 
 }

	public void teztPopulateFromApplicationXML(String filePath) throws Exception {

		InputStream in = new FileInputStream(filePath);
		// DescriptorParseTool parserr = DescriptorParseTool.getInstance();
		EarDescriptor descriptor = new EarDescriptor();
		EarDescriptorPopulator.populateFromApplicationXML(new File(""), descriptor,
				parseApplicationXML(in, true));
		validate(descriptor);
		teztRoles(descriptor);
	}
	

	// for Java EE 5 - until a central replacement of descriptors is done
	private ApplicationType parseApplicationXML(InputStream in, boolean validate)
			throws ConversionException, IOException, SAXException,
			TransformerException {
		ApplicationType appType = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ByteArrayInputStream bis = null;
		try {
			ConversionContext ctx = new ConversionContext(null, validate);
			// "forgiving" conversion by default
			ctx.setAttribute(ConversionContext.FORGIVING_ATTR, Boolean.TRUE);
			ctx.setInputStream(ApplicationConverter.APPLICATION_FILENAME, in);
			ConverterTool.getInstance().convert(IJ2EEDescriptorConverter.APPLICATION,
					ctx);
			Document appDoc = ctx
					.getConvertedDocument(ApplicationConverter.APPLICATION_FILENAME);
			bos = new ByteArrayOutputStream();
			(TransformerFactory.newInstance()).newTransformer().transform(
					new DOMSource(appDoc), new StreamResult(bos));
		} finally {
			bos.close();
		}
		try {
			byte[] xml = bos.toByteArray();
			SchemaProcessor appProcessor = SchemaProcessorFactory
					.getProcessor(SchemaProcessorFactory.APP5);
			appProcessor.switchOffValidation();
			bis = new ByteArrayInputStream(xml);
			appType = (ApplicationType) appProcessor.parse(bis);
		} finally {
			if (bis != null) {
				bis.close();
			}
		}
		return appType;
	}

	/**
	 * @param descriptor
	 */
	private void validate(EarDescriptor descriptor) {
		assertEquals("EAR description", descriptor.getDescription());
		assertEquals("testApplication", descriptor.getDisplayName());
		assertEquals("META-INF", descriptor.getSmallIconName());
		assertEquals("largeIcon", descriptor.getLargeIconName());

		File tmpDir = new File("");

		Set<Module> modules = descriptor.getJ2EEModules();
		assertEquals(6, modules.size());

		Module m = getModule(modules, new Web(tmpDir, "web/webModule1.war",
				"webModule1ContextRoot"));
		assertNotNull(m);
		assertEquals("webModule1ContextRoot", ((Web) m).getContextRoot());
		assertSame(Web.class, m.getClass());
		assertNull(((Web) m).getAlt_dd());

		m = getModule(modules, new Web(tmpDir, "web/web\"?o\\<|ot>Module2.war",
				"webModule_/2ContextR__o/__ot_"));
		assertNotNull(m);
		assertEquals("webModule_/2ContextR__o/__ot_", ((Web) m).getContextRoot());
		assertSame(Web.class, m.getClass());
		assertEquals("test1.dd", ((Web) m).getAlt_dd());

		m = getModule(modules, new EJB(tmpDir, "ejb/ejbProject.jar"));
		assertNotNull(m);
		assertSame(EJB.class, m.getClass());
		assertEquals("test.dd", ((EJB) m).getAlt_dd());

		m = getModule(modules, new EJB(tmpDir, "ejb/ejbProject2.jar"));
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

		SecurityRoles[] roles = descriptor.getRoles();
		assertEquals(2, roles.length);
		assertEquals("test role description", roles[0].getDescription());
		assertEquals("testRoleName", roles[0].getName());

		assertEquals("test role description 1", roles[1].getDescription());
		assertEquals("testRoleName 1", roles[1].getName());
	}

	private void validate1(SimpleEarDescriptor descriptor) {
		assertEquals("EAR description sdfkjah pfdjhas;kdfj", descriptor
				.getDescription());
		assertEquals("testApplicationAKAJSHDKAJH", descriptor.getDisplayName());
		assertNull(descriptor.getSmallIconName());
		assertNull(descriptor.getLargeIconName());

		Set<Module> modules = descriptor.getJ2EEModules();
		assertEquals(1, modules.size());

		Module m = getModule(modules, new Web(new File(""),
				"web/webModule1.war/sdfsdf", "webModule1ContextRootLKHGLKHsdlfkgsldkf"));
		assertNotNull(m);
		assertSame(Web.class, m.getClass());
		assertNull(((Web) m).getAlt_dd());

		assertEquals(0, descriptor.getRoles().length);
	}

	private void validate2(SimpleEarDescriptor descriptor) {
		assertEquals("EAR description sdfkjah pfdjhas;kdfj", descriptor
				.getDescription());
		assertEquals("testApplicationAKAJSHDKAJH", descriptor.getDisplayName());
		assertNull(descriptor.getSmallIconName());
		assertNull(descriptor.getLargeIconName());

		Set<Module> modules = descriptor.getJ2EEModules();
		assertEquals(1, modules.size());

		Module m = getModule(modules, new Web(new File(""), "sdfsd/fsdfsdf/sdf",
				"/"));
		assertNotNull(m);
		assertSame(Web.class, m.getClass());
		assertNull(((Web) m).getAlt_dd());

		assertEquals(0, descriptor.getRoles().length);
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

	private void validateEngine(SimpleEarDescriptor descriptor) {
		ReferenceObjectIntf[] references = descriptor.getReferences();
		assertEquals(3, references.length);
		assertEquals("sap.com", references[0].getReferenceProviderName());
		assertEquals("hard", references[0].getReferenceType());
		assertEquals("application", references[0].getReferenceTargetType());
		assertEquals("restartHardReferancesAfterUpdateRefToAppName", references[0]
				.getReferenceTarget());

		assertEquals("ma.ga", references[1].getReferenceProviderName());
		assertEquals("weak", references[1].getReferenceType());
		assertEquals("library", references[1].getReferenceTargetType());
		assertEquals("testingLibrary", references[1].getReferenceTarget());

		assertEquals("hard", references[2].getReferenceType());
		assertEquals("library", references[2].getReferenceTargetType());
		assertEquals("testingLibrary1", references[2].getReferenceTarget());

		assertEquals("/classes/myClass.jar;myLibDir/test;myLIbDir/classes.jar",
				descriptor.getClassPath());
		assertEquals("foo.bar", descriptor.getProviderName());

		Set<Module> aditionalModules = descriptor.getModulesAdditional();

		assertNotNull(new Module(new File(""), "fooBar/SonicDestination.fobar",
				"FooBarContainer"));
		assertNotNull(new Module(new File(""), "jms/SonicDestination.xml",
				"JMSConnector"));
		assertNotNull(new Module(new File(""), "portal/portalApp.par", "Portals"));

		assertEquals("always", AdditionalAppInfo.getStartUpString(descriptor
				.getStartUp()));
		assertEquals("disable", descriptor.getFailOverValue());
		assertEquals("1.4", descriptor.getJavaVersion());
	}

	public void testPopulateFromApplicationEngineXML() throws Exception {
		File file = FileFromPath.getFileFromClassPath(
				"com/sap/engine/services/deploy/ear/jar/application-j2ee-engine1.xml",
				tmpDir);
		InputStream in = new FileInputStream(file);
		DescriptorParseTool parserr = DescriptorParseTool.getInstance();
		EarDescriptor descriptor = new EarDescriptor();
		EarDescriptorPopulator.populateFromApplicationEngineXML(new File(""),
				descriptor, parserr.parseApplicationJ2ee(in, true));
		validateEngine(descriptor);
		in.close();

	}

	public void testPopulateFromApplicationEngineXML1() throws Exception {
		File file = FileFromPath
				.getFileFromClassPath(
						"com/sap/engine/services/deploy/ear/jar/application-j2ee.engine-maximal.xml",
						tmpDir);
		InputStream in = new FileInputStream(file);
		DescriptorParseTool parserr = DescriptorParseTool.getInstance();
		EarDescriptor descriptor = new EarDescriptor();
		EarDescriptorPopulator.populateFromApplicationEngineXML(new File(""),
				descriptor, parserr.parseApplicationJ2ee(in, true));
		validateEngine(descriptor);
		in.close();
		}

	public void testPopulateFromApplicationEngineXML2() throws Exception {
		File file = FileFromPath
				.getFileFromClassPath(
						"com/sap/engine/services/deploy/ear/jar/application-j2ee.engine-maximal2.xml",
						tmpDir);
		InputStream in = new FileInputStream(file);
		DescriptorParseTool parserr = DescriptorParseTool.getInstance();
		EarDescriptor descriptor = new EarDescriptor();
		EarDescriptorPopulator.populateFromApplicationEngineXML(new File(""),
				descriptor, parserr.parseApplicationJ2ee(in, true));
		validateEngine2(descriptor);
		in.close();
	}

	private void validateEngine2(SimpleEarDescriptor descriptor) {
		ReferenceObjectIntf[] references = descriptor.getReferences();
		assertEquals(3, references.length);
		assertEquals("sap.com", references[0].getReferenceProviderName());
		assertEquals("hard", references[0].getReferenceType());
		assertEquals("application", references[0].getReferenceTargetType());
		assertEquals("restartHardReferancesAfterUpdateRefToAppName", references[0]
				.getReferenceTarget());

		assertEquals("ma.ga", references[1].getReferenceProviderName());
		assertEquals("weak", references[1].getReferenceType());
		assertEquals("library", references[1].getReferenceTargetType());
		assertEquals("testingLibrary", references[1].getReferenceTarget());

		assertEquals("hard", references[2].getReferenceType());
		assertEquals("library", references[2].getReferenceTargetType());
		assertEquals("testingLibrary1", references[2].getReferenceTarget());

		assertEquals("/classes/myClass.jar;myLibDir/test;myLIbDir/classes.jar",
				descriptor.getClassPath());
		assertEquals("foo.bar", descriptor.getProviderName());

		Set<Module> modules = descriptor.getModulesAdditional();

		assertNotNull(new Module(new File(""), "fooBar/SonicDestination.fobar",
				"FooBarContainer"));
		assertNotNull(new Module(new File(""), "jms/SonicDestination.xml",
				"JMSConnector"));
		assertNotNull(new Module(new File(""), "portal/portalApp.par", "Portals"));

		assertEquals("lazy", AdditionalAppInfo.getStartUpString(descriptor
				.getStartUp()));
		assertEquals("on_request", descriptor.getFailOverValue());
	}

	private void teztRoles(EarDescriptor descriptor) {
		SecurityRoles[] roles = descriptor.getRoles();
		
		assertEquals(2, roles.length);
		assertEquals("test role description", roles[0].getDescription());
		assertEquals("testRoleName", roles[0].getName());
		
		roles[0].setRoleDescription("test role description ");
		assertEquals("test role description ", roles[0].getDescription());

		assertEquals("test role description 1", roles[1].getDescription());
		assertEquals("testRoleName 1", roles[1].getName());
		
		roles[0].setRoleName("testRoleName 1");
		assertEquals("testRoleName 1", roles[0].getName());
		
			
		roles[1].setUserNames(new String[]{"testName", "testName"});
		assertEquals(roles[1].getUserNames()[1], "testName");
		
		roles[1].addUser("testUser");
		assertTrue(roles[1].getUserNames().length == 3);
		int[] sids = {1,2,3,4};
		
		roles[1].setUserSIDs(sids);
		roles[1].setUserGroups(new String[]{"testGroup"});
		assertEquals(roles[1].getUserGroup()[0], "testGroup");
		roles[1].removeGroupNames("testGroup");
		assertTrue(roles[1].getUserGroup().length == 0);
		
		roles[0].setUserName("testName");
		assertEquals(roles[0].getUsers()[0], "testName");
		
		roles[0].removeUser("testName");
		assertTrue(roles[0].getUserNames().length == 0);
		
		roles[0].setGroupSIDs(sids);
		assertTrue(roles[0].getGroupSIDs().length == 4);
		
		roles[0].setServerRoleName("ServerRoleName");
		assertTrue(roles[0].getServerRoleName().equals("ServerRoleName"));
		assertTrue(roles[0].getUserNames() == null);
		assertTrue(roles[0].getUserGroup() == null);
		
		SecurityRoles role = (SecurityRoles) roles[0].clone();
		assertTrue(role.equals(roles[0]));
	
		role = new SecurityRoles(roles[0].getName(), roles[0].getDescription(), roles[0].getUserSIDs());
		role.setServerRoleName("ServerRoleName");
		role.setGroupSIDs(sids);
		
		assertTrue(role.equals(roles[0]));
		
		roles[0].setGroupSIDsElement(1);
		assertTrue(roles[0].getGroupSIDs().length == 4);
		
		roles[0].setUserSIDs(sids);
		roles[0].setUserSIDsElement(1);
		assertTrue(roles[0].getUserSIDs().length == 4);
		
		roles[0].setUserSIDsElement(0, 10);
		assertTrue(roles[0].getUserSIDs()[0] == 10);
		roles[0].setUserSIDsElement(5);
		assertTrue(roles[0].getUserSIDs().length == 5);
		
		
	}

	private static final String[] hotFileNames = new String[] { "000.xml",
			"00.xml", "0.xml", "01.xml", "02.xml", "03.xml", "1.xml", "2.xml",
			"3.xml", "5.xml", "application1.3.1.xml", "application1.3.2.xml",
			"application1.3.3.xml", "application1.3.4.xml", "application1.3.5.xml" };

	public void testAllHot() throws Exception {

		DescriptorParseTool parserr = DescriptorParseTool.getInstance();
		for (int i = 0; i < hotFileNames.length; i++) {
			InputStream in = SimpleEarDescriptorPopulatorTest.class.getClassLoader()
					.getResourceAsStream(
							"com/sap/engine/services/deploy/ear/jar/applicationHot/"
									+ hotFileNames[i]);
			try {
				System.out.print("SimpleEarDescriptorPopulatorTest.testAllHot PARSING FILE"+ "com/sap/engine/services/deploy/ear/jar/applicationHot" + File.separator + hotFileNames[i] + "...");//$JL-SYS_OUT_ERR$
				parserr.parseApplication(in, true);
				System.out.println("done");//$JL-SYS_OUT_ERR$
			} catch (DescriptorParseException e) {
				if (e.getCause() instanceof ConversionException) {
					FileNameExceptionPair[] filePairs = ((ConversionException) e
							.getCause()).getFileExceptionPairs();
					for (int j = 0; j < filePairs.length; j++) {
						FileNameExceptionPair filePair = filePairs[j];
						filePair.getThrowable().printStackTrace();
					}
				}
				throw e;
			} catch (Exception e) {
				System.out.println("ERROR: parsing file "+ "com/sap/engine/services/deploy/ear/jar/applicationHot"+ File.separator + hotFileNames[i]);//$JL-SYS_OUT_ERR$
				throw e;
			} finally {
				in.close();
			}
		}
	}

	private static final String[] j2eeHotFileNames = new String[] { "0.xml",
			"01.xml", "1.xml", "2.xml", "3.xml", "4.xml",
			"application-j2ee-engine1.3.1.xml", "application-j2ee-engine1.3.2.xml" };

	public void testJ2eeAllHot() throws Exception {

		DescriptorParseTool parserr = DescriptorParseTool.getInstance();
		for (int i = 0; i < j2eeHotFileNames.length; i++) {
			InputStream in = SimpleEarDescriptorPopulatorTest.class.getClassLoader()
					.getResourceAsStream(
							"com/sap/engine/services/deploy/ear/jar/applicationJ2eeHot/"
									+ j2eeHotFileNames[i]);
			try {
				System.out.print("SimpleEarDescriptorPopulatorTest.testAllHot PARSING FILE"	+ "com/sap/engine/services/deploy/ear/jar/applicationJ2eeHot" + File.separator + j2eeHotFileNames[i] + "...");//$JL-SYS_OUT_ERR$
				parserr.parseApplicationJ2ee(in, true);
				System.out.println("done");//$JL-SYS_OUT_ERR$
			} catch (DescriptorParseException e) {
				if (e.getCause() instanceof ConversionException) {
					FileNameExceptionPair[] filePairs = ((ConversionException) e
							.getCause()).getFileExceptionPairs();
					for (int j = 0; j < filePairs.length; j++) {
						FileNameExceptionPair filePair = filePairs[j];
						filePair.getThrowable().printStackTrace();
					}
				}
				throw e;
			} catch (Exception e) {
				System.out.println("ERROR: parsing file " + "com/sap/engine/services/deploy/ear/jar/applicationJ2eeHot" + File.separator + j2eeHotFileNames[i]);//$JL-SYS_OUT_ERR$
				throw e;
			} finally {
				in.close();
			}
		}
	}

}