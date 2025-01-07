package com.sap.ats.tests.webservices.erp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.rpc.Stub;
import javax.xml.soap.MessageFactory;

import junit.framework.TestCase;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.sap.ats.Test;
import com.sap.ats.env.LogEnvironment;
import com.sap.ats.env.TestEnvironment;
import com.sap.ats.env.system.EnvironmentFactory;
import com.sap.ats.tests.webservices.dynamic.util.Util;
import com.sap.engine.lib.xml.parser.tokenizer.XMLTokenReader;
import com.sap.engine.lib.xml.parser.tokenizer.XMLTokenReaderImpl;
import com.sap.engine.services.webservices.espbase.client.ProxyGeneratorConfigNew;
import com.sap.engine.services.webservices.espbase.client.ProxyGeneratorNew;
import com.sap.engine.services.webservices.espbase.client.dynamic.DGenericService;
import com.sap.engine.services.webservices.espbase.client.dynamic.DInterface;
import com.sap.engine.services.webservices.espbase.client.dynamic.DInterfaceInvoker;
import com.sap.engine.services.webservices.espbase.client.dynamic.DOperation;
import com.sap.engine.services.webservices.espbase.client.dynamic.DParameter;
import com.sap.engine.services.webservices.espbase.client.dynamic.GenericServiceFactory;
import com.sap.engine.services.webservices.espbase.client.dynamic.ParametersConfiguration;
import com.sap.engine.services.webservices.espbase.client.dynamic.ServiceFactoryConfig;
import com.sap.engine.services.webservices.espbase.client.dynamic.content.GenericObject;
import com.sap.engine.services.webservices.espbase.client.dynamic.content.ObjectFactory;
import com.sap.engine.services.webservices.espbase.client.dynamic.content.impl.ObjectFactoryImpl;
import com.sap.engine.services.webservices.espbase.configuration.ConfigurationFactory;
import com.sap.engine.services.webservices.espbase.configuration.ConfigurationRoot;
import com.sap.engine.services.webservices.espbase.mappings.MappingFactory;
import com.sap.engine.services.webservices.espbase.mappings.MappingRules;
import com.sap.engine.services.webservices.espbase.messaging.impl.MessageConvertor;
import com.sap.engine.services.webservices.jaxrpc.encoding.DeserializerBase;
import com.sap.engine.services.webservices.jaxrpc.encoding.ExtendedTypeMapping;
import com.sap.engine.services.webservices.jaxrpc.encoding.SOAPDeserializationContext;
import com.sap.engine.services.webservices.jaxrpc.encoding.XMLTypeMapping;
import com.sap.engine.services.webservices.jaxrpc.util.PackageBuilder;

public class QINTesting extends TestCase implements Test {
  private static final String COMMON_TEST_CLASSPATH = "C:\\sandbox\\ats\\libs\\jaxb-api.jar";
  private Logger testEnv;
  private Logger logEnv;
  private String folderPath = "C:/sandbox/ats/ERP_SERVICES_20070924_HU2";
  private List<File> tests = new ArrayList<File>();
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    prepare();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public int prepare() {
    try {
      logEnv = Logger.getLogger(getClass().getName());
      testEnv = logEnv;
      
      File folder = new File(folderPath);
      
      if (!folder.isDirectory()) {
        logEnv.info("Path " + folder.getAbsolutePath() + " does not denote a directory. Exiting...");
        return FAILED;
      }      
      
      File[] serviceFolders = folder.listFiles();
      
      if (serviceFolders == null || serviceFolders.length == 0) {
        logEnv.info("No service folders to be tested. Exiting...");
        return FAILED;
      }
      
      for (File service : serviceFolders) {
        if (service.isDirectory()) {
          tests.add(service);
        }
      }      
      
      return Test.PASSED;
    } catch (Exception e) {
      logEnv.log(Level.INFO, e.getMessage(), e);
      return Test.FAILED;
    }
  }
  
  public void testStandaloneProxies() {
    try {
      logEnv.info("Testing stand alone proxies");      
    
      if (tests.isEmpty()) {
        logEnv.info("No tests to execute. Exiting...");
        //return FAILED;
        fail();
      }
      
      int result = PASSED;
      File genFolder = null;
      String wsdl = null;
      File[] contents = null;
      TestData testData = null;
      List<File> payloads = null; 
      ClientData clientData = null;
      Executor executor = null;
      
      for (File folder : tests) {
        logEnv.info("*****************************************************");
        logEnv.info("Processing folder " + folder.getAbsolutePath());
        contents = folder.listFiles();
        
        if (contents == null || contents.length == 0) {
          result = FAILED;
          logEnv.info("No files in this test folder");
          continue;
        }
        
        for (File file : contents) {
          if (file.isFile() && file.getName().endsWith(".wsdl")) {
            wsdl = file.getAbsolutePath();
            logEnv.info("WSDL file " + wsdl);
            break;
          }
        }       
        
        // generate proxy
        genFolder = new File(folder, "proxy");
        
        if (genFolder.exists()) {
          Util.deleteFile(genFolder);
        }
        
        genFolder.mkdirs();     
        
        if (generateProxy(genFolder.getAbsolutePath(), wsdl) == FAILED) {
          result = FAILED;
          logEnv.info("Generating proxy failed");
          continue;
        }
        
        // test data
//        testData = new TestData(wsdl);
//        testData.setFolder(folder);
//        payloads = new ArrayList<File>();
//        payloads.add(new File(folder, "payload.xml"));
//        testData.setPayloads(payloads);
//        
//        if (configureTestData(testData) == FAILED) {
//          result = FAILED;
//          logEnv.info("Configuring of test data failed");
//          continue;
//        }
//        
//        // client data
//        clientData = new ClientData(wsdl);
//        clientData.setFolder(genFolder);
//        
//        if (configureClientData(clientData) == FAILED) {
//          result = FAILED;
//          logEnv.info("Configuring of client data failed");
//          continue;
//        }
//        
//        executor = new Executor(testData, clientData);
//        executor.setLogger(new LogEnvironmentImpl());
//
//        if (executor.execute(10) == FAILED) {
//          result = FAILED;
//          logEnv.info("WS execution failed");                  
//        }        
      }  
      
      //return result;
      if (result == FAILED) {
          fail();
      }
    } catch (Exception e) {
      logEnv.log(Level.INFO, e.getMessage(), e);
      //return FAILED;
      fail();
    }
  }
  
  public void testDynamicProxies() {
    try {
      logEnv.info("Testing dynamic proxies");
      
      if (tests.isEmpty()) {
        logEnv.info("No tests to execute. Exiting...");
        fail();
        //return FAILED;
      }
      
      int result = PASSED;
      File workDir = null;
      String wsdl = null;
      File[] contents = null;
      FileReader reader = null;
      BufferedReader buff = null; 
       
      for (File folder : tests) {
        logEnv.info("*****************************************************");
        logEnv.info("Processing folder " + folder.getAbsolutePath());
        contents = folder.listFiles();
        
        if (contents == null || contents.length == 0) {
          result = FAILED;
          logEnv.info("No files in this test folder");
          continue;
        }
        
        for (File file : contents) {
          if (file.isFile() && file.getName().equals("wsdl_url.txt")) {
            try {
              reader = new FileReader(file);
              buff = new BufferedReader(reader);
              wsdl = buff.readLine();  
              logEnv.info("\nWSDL URL: " + wsdl);
              workDir = new File(folder, "work_url");              
             
              // load WSDL remotely & invoke service via Dynamic Proxy API
              if (workDir.exists()) {
                Util.deleteFile(workDir);
              }
              
              workDir.mkdirs();
              
              if (testSingleProxy(workDir.getAbsolutePath(), wsdl) == FAILED) {
                result = FAILED;
                logEnv.info("Testing dynamic proxy for WSDL " + wsdl + " failed");          
              }               
            } catch (Exception e) {
              result = FAILED;
              logEnv.log(Level.INFO, e.getMessage(), e);              
            } finally {
              reader.close();
              buff.close();
            }           
          } else if (file.isFile() && file.getName().endsWith(".wsdl")) {
            wsdl = file.getAbsolutePath();
            logEnv.info("\nWSDL file " + wsdl);
            workDir = new File(folder, "work");    
            
            // load WSDL locally & invoke service via Dynamic Proxy API
            if (workDir.exists()) {
              Util.deleteFile(workDir);
            }
            
            workDir.mkdirs();
            
            if (testSingleProxy(workDir.getAbsolutePath(), wsdl) == FAILED) {
              result = FAILED;
              logEnv.info("Testing dynamic proxy for WSDL " + wsdl + " failed");          
            }            
          }
        }        
      }      
      // return result;
      if (result == FAILED) {
          fail();
      }
    } catch (Exception e) {
      logEnv.log(Level.INFO, e.getMessage(), e);
      fail();
      //return FAILED;
    }
  }  
  
  private int generateProxy(String outputDir, String wsdlPath) {
    if (outputDir == null || wsdlPath == null) {
      return FAILED;
    }

    try {    
      ProxyGeneratorConfigNew config = new ProxyGeneratorConfigNew();
      ProxyGeneratorNew proxyGenerator = new ProxyGeneratorNew();
      config.setGenerationMode(ProxyGeneratorConfigNew.JAXWS_MODE);
      config.setUnwrapDocumentStyle(false);
      String srcFolder = outputDir + "/src";
      config.setOutputPath(srcFolder);
      config.setWsdlPath(wsdlPath);
      long startTime = System.currentTimeMillis();
      proxyGenerator.generateAll(config);
      long duration = System.currentTimeMillis() - startTime;
      logEnv.info("$$$$$$$$$$$ Proxy for WSDL " + wsdlPath + " was generated for " + duration + " ms");
  
      String cp = COMMON_TEST_CLASSPATH;
      PackageBuilder packbuild = new PackageBuilder();
      File outputPath = new File(outputDir + "/bin");
      outputPath.mkdir();
      packbuild.setOutputPath(outputPath);
      startTime = System.currentTimeMillis();
      packbuild.compile(cp, new File(srcFolder));
      duration = System.currentTimeMillis() - startTime;
      logEnv.info("$$$$$$$$$$$ Proxy classes were compiled for " + duration + " ms");
  
      MappingRules mr = config.getMappingRules();
      MappingFactory.save(mr, outputDir + "/mapping.xml");
      ConfigurationRoot cRoot = config.getProxyConfig();      
      ConfigurationFactory.save(cRoot, outputDir + "/configuration.xml");
      logEnv.info("Corresponding mapping.xml and configuration.xml were created");
      return PASSED;
    } catch (Exception e) {
      logEnv.log(Level.INFO, e.getMessage(), e);
      return FAILED;
    }
  }
  
  private int configureTestData(TestData testData) {
    if (testData == null) {
      return FAILED;
    }
    
    try {
      testData.setHost("iwdf0512.wdf.sap.corp");
      testData.setPort("55080");
      testData.setClient("800");
      testData.setUser("d049041");
      testData.setPassword("abcd1234");            
      return PASSED;
    } catch (Exception e) {
      logEnv.log(Level.INFO, e.getMessage(), e);
      return FAILED;
    }
  }
  
  private int configureClientData(ClientData clientData) {
    if (clientData == null) {
      return FAILED;
    }   
    
    try {
      File mappingFile = new File(clientData.getFolder(), "mapping.xml");
         
      if (mappingFile.isFile() && mappingFile.canRead()) {
        logEnv.info("Processing mapping.xml");
        String[] service_port = getService_PortClass(mappingFile);
        String serviceClass = service_port[0];
        
        if (serviceClass == null || serviceClass.equals(COMMON_TEST_CLASSPATH)) {
          logEnv.info("Missing service class: " + serviceClass);
          return FAILED;
        }
        
        logEnv.info("Service Class: " + serviceClass);
        clientData.setServiceClass(serviceClass);
        String portClass = service_port[1];
        
        if (portClass == null || portClass.equals(COMMON_TEST_CLASSPATH)) {
          logEnv.info("Missing port class: " + portClass);
          return FAILED;          
        }
        
        logEnv.info("Port Class: " + portClass);
        clientData.setPortClass(portClass);  
        String serviceQName = service_port[2];
        
        if (serviceQName == null || serviceQName.equals(COMMON_TEST_CLASSPATH)) { 
          logEnv.info("Missing service QName: " + serviceQName);               
        }
        
        clientData.setServiceQName(serviceQName);    
      }
    
      return PASSED;
    } catch (Exception e) {
      logEnv.log(Level.INFO, e.getMessage(), e);
      return FAILED;
    }
  }  
  
  private String[] getService_PortClass(File file) throws Exception {
    String info[] = new String[3];
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    Element tempEl = null;
    NodeList innerList = null;    
    DocumentBuilder docBuilder = factory.newDocumentBuilder();
    Element docEl = docBuilder.parse(file).getDocumentElement();
    NodeList tempList = docEl.getElementsByTagName("interface");
    
    if (tempList != null && tempList.getLength() > 0) {
      tempEl = (Element) tempList.item(0);
      tempList = tempEl.getElementsByTagName("property");
      
      if (tempList != null && tempList.getLength() > 0) {
        for (int i = 0; i < tempList.getLength(); i++) {
          tempEl = (Element) tempList.item(i);
          
          if (tempEl.getAttribute("name").equals("SEIName")) {
            tempList = tempEl.getChildNodes();
            
            if (tempList != null && tempList.getLength() > 0) { 
              if (tempList.item(0) instanceof Text) {
                info[1] = ((Text) tempList.item(0)).getData().trim();
              }
            }
          }
        }
      }            
    }
    
    tempList = docEl.getElementsByTagName("service");
    
    if (tempList != null && tempList.getLength() > 0) {
      tempEl = (Element) tempList.item(0);
      tempList = tempEl.getElementsByTagName("property");
      
      if (tempList != null && tempList.getLength() > 0) {
        for (int i = 0; i < tempList.getLength(); i++) {
          tempEl = (Element) tempList.item(i);
          
          if (tempEl.getAttribute("name").equals("SIName")) {
            innerList = tempEl.getChildNodes();
            
            if (innerList != null && innerList.getLength() > 0) { 
              if (innerList.item(0) instanceof Text) {
                info[0] = ((Text) innerList.item(0)).getData().trim();
                continue;
              }
            }
          } else if (tempEl.getAttribute("name").equals("ServiceQName")) {
            innerList = tempEl.getChildNodes();
            
            if (innerList != null && innerList.getLength() > 0) {                
              if (innerList.item(0) instanceof Text) {
                info[2] = ((Text) innerList.item(0)).getData().trim();                  
              }
            }
          }
        }
      }            
    }
    
    return info;    
  }
  
  private int testSingleProxy(String workDir, String wsdlPath) {
    try {
      ServiceFactoryConfig config = new ServiceFactoryConfig();
      config.setTemporaryDir(workDir);
      
      if (wsdlPath.startsWith("http")) {
        config.setUser("d049041");
        config.setPassword("abcd1234");
      }
      
      GenericServiceFactory factory = GenericServiceFactory.newInstance();
      long startTime = System.currentTimeMillis();
      DGenericService service = factory.createService(wsdlPath, config);
      long duration = System.currentTimeMillis() - startTime;
      logEnv.info("$$$$$$$$$$$ Dynamic Proxy API loaded WSDL " + wsdlPath + " for " + duration + " ms");     
      QName[] interfaces = service.getInterfaces();
      
      if (interfaces == null || interfaces.length == 0) {
        logEnv.info("No interfaces found for service " + service);
        return FAILED;
      }
      
      DInterface intf = service.getInterfaceMetadata(interfaces[0]); // should be one
      QName[] ports = intf.getPortNames(); 
      
      if (ports == null || ports.length == 0) {
        logEnv.info("No ports found for interface " + intf.getInterfaceName());
        return FAILED;
      }
      
      DInterfaceInvoker invoker = intf.getInterfaceInvoker(ports[0]); // should be one
      invoker.setProperty(Stub.USERNAME_PROPERTY, "d049041");
      invoker.setProperty(Stub.PASSWORD_PROPERTY, "abcd1234");      
      
      String[] operations = intf.getOperationNames();
      
      if (operations == null || operations.length == 0) {
        logEnv.info("No operations found for interface " + intf.getInterfaceName());
        return FAILED;
      }
      
      ParametersConfiguration paramsConfig = invoker.getParametersConfiguration(operations[0]); // should be one
      DOperation operation = intf.getOperation(operations[0]);
      DParameter[] parameters = operation.getInputParameters();
      
      if (parameters == null || parameters.length == 0) {
        logEnv.info("No parameters found for operation " + operation.getName());
        return FAILED;
      }
            
      String payload = new File((new File(workDir)).getParentFile(), "payload.xml").getAbsolutePath();
      ExtendedTypeMapping mapping = service.getTypeMetadata();
      ObjectFactory objFactory = new ObjectFactoryImpl(mapping);
      GenericObject object = obtainParameterValue(parameters[0].getSchemaName(), payload, mapping);
      paramsConfig.setInputParameterValue(parameters[0].getName(), object); // should be one
      int result = PASSED;
      
      for (int i = 0; i < 10; i++) {  
        try {
          logEnv.info("Invoking " + operation.getName());
          startTime = System.currentTimeMillis();
          invoker.invokeOperation(operation.getName(), paramsConfig, objFactory);
          duration = System.currentTimeMillis() - startTime;
          logEnv.info("$$$$$$$$$$$ Invocation took " + duration + " ms");         
        } catch (Exception e) {
          logEnv.log(Level.INFO, e.getMessage(), e);
          result = FAILED;
        }
      }
                  
      return result;
    } catch (Exception e) {
      logEnv.log(Level.INFO, e.getMessage(), e);
      return FAILED;
    }
  }
  
  private GenericObject obtainParameterValue(QName paramSchemaName, String payload, ExtendedTypeMapping mapping) throws Exception {
    ObjectFactory objFactory = new ObjectFactoryImpl(mapping);
    SOAPDeserializationContext dContext = new SOAPDeserializationContext();
    dContext.setTypeMapping(mapping);
    dContext.setObjectFactory(objFactory);
        
    InputStream input = new FileInputStream(payload);
  
    if (input == null) {
      throw new RuntimeException("Could not get access to file " + payload);
    }
  
    XMLTokenReader reader = new XMLTokenReaderImpl();
    reader.init(input);
    reader.begin();
    reader.moveToNextElementStart();
    reader.next();
    reader.passChars();
    reader.next();
    reader.passChars();
    
    Class resultClass = mapping.getDefaultJavaClass(paramSchemaName);
    XMLTypeMapping xmlMapping = mapping.getXmlTypeMapping(paramSchemaName);
    DeserializerBase deserializer = xmlMapping.getDefaultDeserializer();
    GenericObject object = (GenericObject) deserializer.deserialize(reader, dContext, resultClass);
//    logEnv.info("Payload " + payload + " deserialized to: \n" + ((GenericObjectImpl) object).toString());
    reader.moveToNextElementStart();
    reader.end();
    
    return object;
  }
}
