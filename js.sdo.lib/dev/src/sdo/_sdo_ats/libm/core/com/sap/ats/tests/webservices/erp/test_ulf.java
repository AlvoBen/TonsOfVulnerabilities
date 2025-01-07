package com.sap.ats.tests.webservices.erp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import report.consumption.es.Report;
import report.consumption.es.TestType;

import com.sap.ats.Test;
import com.sap.ats.env.LogEnvironment;
import com.sap.ats.tests.webservices.dynamic.util.Util;
import com.sap.engine.services.webservices.espbase.client.ProxyGeneratorConfigNew;
import com.sap.engine.services.webservices.espbase.client.ProxyGeneratorNew;
import com.sap.engine.services.webservices.espbase.configuration.ConfigurationFactory;
import com.sap.engine.services.webservices.espbase.configuration.ConfigurationRoot;
import com.sap.engine.services.webservices.espbase.mappings.MappingFactory;
import com.sap.engine.services.webservices.espbase.mappings.MappingRules;

import commonj.sdo.DataObject;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.impl.HelperProvider;

public class Test_Ulf implements Test {
  static final String STATUS_OK = "Ok";
  static final String STATUS_ERROR = "Error";
  
  private String folder = "C:/sandbox/ats/ERP_SERVICES_20071126_Q2O"; // change to d:/erp for the first system -> also in Preparator!!!
  private String gen_dir = "C:/sandbox/ats/erp_demo_gen/"; // change to d:/erp_gen for the first system
  private String testDataFile = "file:///C:/sandbox/ats/ESOA_TESTDATA_20071204/SERVICE_ECATTSDISTINC+TESTDATA.XML";
  private static final String REPORT_FOLDER = "C:/sandbox/ats";
  private File inputDir = new File(folder);
  private File[] wsdl_folders;
  private Map<Integer, List<File>> runs = new Hashtable<Integer, List<File>>();
  private File wsdl_folder = null; 
  private File[] folderContents = null;
  private SdoPreparator prep = null; 
  private Map<TestData, ClientData> testToClient; 
  private SdoExecutor exec;
  private Map<Integer, Map<TestData, ClientData>> runsToExec = new Hashtable<Integer, Map<TestData, ClientData>>();
  private Map<TestData, Boolean> testResults = new HashMap<TestData, Boolean>();
  private Map<TestData, Exception> failedTests = new HashMap<TestData, Exception>();
  private HelperContext context = HelperProvider.getDefaultContext();
  
  private LogEnvironment logEnv = new LogEnvironmentImpl();;

  
  public int prepare() {
    try {
      //here logic for getting specific tests to execute could be added
      ArrayList<File> folders = new ArrayList<File>();
      wsdl_folders = inputDir.listFiles();       
      
      if (wsdl_folders != null && wsdl_folders.length > 0) {
        for (int j = 0; j < wsdl_folders.length; j++) {
          if (folders.size() >= (wsdl_folders.length / 6)) {
            runs.put(runs.size() + 1, folders);
            logEnv.log("Folders for run[" + runs.size() + "] determined. " + folders.size() + " in this run");
            folders = new ArrayList<File>();
          }
          
          wsdl_folder = wsdl_folders[j];
          
          if (wsdl_folder.isDirectory()) {
            folders.add(wsdl_folder);
          }         
        }
        
        if (folders.size() > 0) { 
          runs.put(runs.size() + 1, folders);
          logEnv.log("Folders for run[" + runs.size() + "] determined. " + folders.size() + " in this run." );
        }
      }
      
      File gDir = new File(gen_dir);
      
      if (gDir.exists()) {
        Util.deleteFile(gDir);        
      }
      
      gDir.mkdirs();
      return Test.PASSED;
    } catch (Exception e) { 
      logEnv.log(e);
      return Test.FAILED;
    }
  }
  
  public int executeTest1() throws Exception {    
    return executeTest(1);    
  }
  
  public int executeTest2() throws Exception {
    return executeTest(2); 
  }
  
  public int executeTest3() throws Exception {
    return executeTest(3);
  }
  
  public int executeTest4() throws Exception {
    return executeTest(4);
  }

  public int executeTest5() throws Exception {
    return executeTest(5);
  }
  
  public int executeTest6() throws Exception {
    return executeTest(6);
  }

  public int executeTest7() throws Exception {
    return executeTest(7);
  }
  
  private int executeTest(int run) throws Exception {    
    int passed = PASSED;
    List<File> folders = runs.get(run);
    File currFile = null;
    String currInfFile = null;
    String wsdlFileName = null;
    String currenGenDir = null;
        
    if (folders == null || folders.isEmpty()) {
      logEnv.log("Nothing more to execute.");
      return passed;
    }
    
    for (int i = 0; i < folders.size(); i++) {
      try {
        wsdl_folder = folders.get(i);
        logEnv.log("****** Processing directory " + wsdl_folder.getPath() + "******");
          
        folderContents = wsdl_folder.listFiles();
        
        if (folderContents == null || folderContents.length == 0) {
          logEnv.log("Folder " + wsdl_folder + " is empty.\n");
          continue;
        }
        
        currInfFile = null;
        
        for (int j = 0; j < folderContents.length; j++) {
          currFile = folderContents[j];
          
          if (currFile.getName().endsWith(".inf")) {
            currInfFile = currFile.getName();  
            logEnv.log("Inf file: " + currInfFile);
            break;
          }
        }       
        
        if (currInfFile != null) {
          wsdlFileName = getShortFileName(currInfFile) + ".wsdl";
          currenGenDir = gen_dir + "gen" + run + "_" + i;
          Util.copyFile(currFile, new File(currenGenDir, currInfFile));
          File wsdlFile = new File(wsdl_folder, wsdlFileName);
          Util.copyFile(wsdlFile, new File(currenGenDir, wsdlFileName));
          
          //HelperContext context = HelperProvider.getDefaultContext();

          //Map options = new HashMap();
          //options.put(SapXmlHelper.OPTION_KEY_DEFINE_SCHEMAS, SapXmlHelper.OPTION_VALUE_TRUE);
          
          //context.getXMLHelper().load(new FileInputStream(wsdlFile), wsdlFile.getAbsolutePath(), options);

          generateProxy(currenGenDir, wsdl_folder.getPath() + "/" + wsdlFileName);
          logEnv.log("****** Processing directory " + wsdl_folder.getPath() + "  SUCCESSFUL ******\n");
        } else {
          logEnv.log("Info file is missing.");
          logEnv.log("****** Processing directory " + wsdl_folder.getPath() + "  FAILED ******\n");
          passed = FAILED;  
        }
      } catch (Exception e) {
        logEnv.log(e);
        logEnv.log("****** Processing directory " + wsdl_folder.getPath() + "  FAILED ******\n");
        passed = FAILED;        
      }
    } 
    
    return passed;
  }

  private void generateProxy(String curr_gen_dir, String wsdl_path) throws Exception {
    if (curr_gen_dir == null || wsdl_path == null) {
      return;
    }

    ProxyGeneratorConfigNew config = new ProxyGeneratorConfigNew();
    ProxyGeneratorNew proxyGenerator = new ProxyGeneratorNew();
    config.setGenerationMode(ProxyGeneratorConfigNew.JAXWS_MODE);
    config.setUnwrapDocumentStyle(false);
    config.setOutputPath(curr_gen_dir + "/src");
    config.setWsdlPath("file:///" + wsdl_path);
    File customization = new File((new File(wsdl_path)).getParentFile(), "customization.xml"); // customization.xml or .cust file
    
    if (customization.isFile() && customization.canRead()) {
      logEnv.log("Customization file: " + customization);
      config.setExternalBindings(new String[]{customization.getAbsolutePath()});
    }
    
    proxyGenerator.generateAll(config);
    logEnv.log("Proxy was generated");

    MappingRules mr = config.getMappingRules();
    ConfigurationRoot cRoot = config.getProxyConfig();
    MappingFactory.save(mr, curr_gen_dir + "/mapping.xml");
    ConfigurationFactory.save(cRoot, curr_gen_dir + "/configuration.xml");
    logEnv.log("mapping.xml and configuration.xml were created");
  }

  private String getShortFileName(String name) {
    int cutIndex = name.lastIndexOf(".");

    if (cutIndex > 0) {
      return name.substring(0, cutIndex);
    }

    return name;
  }

  public int processTest_ClientData() {
    int result = PASSED;
    
    try {    
      prep = new SdoPreparator(testDataFile, context);
      prep.setLogger(logEnv);
      prep.prepare();
      testToClient = prep.getTestToClientMap();
      result = prep.getResult();
    } catch (Exception e) {
      logEnv.log(e);
      result = FAILED;    
    }
    
    return result;
  }
  
  public int processWSCalls() {
    if (testToClient == null) {
      return FAILED;
    }

    logEnv.log("Found mapping for " + testToClient.size() + " web services" );
    Hashtable<TestData, ClientData> map = new Hashtable<TestData, ClientData>();
        
    for(TestData tData : testToClient.keySet()) {
      if (map.size() >= (testToClient.size() / 6)) {
        runsToExec.put(runsToExec.size() + 1, map);
        logEnv.log("Tests for run[" + runsToExec.size() + "] determined. " + map.size() + " in this run.");
        map = new Hashtable<TestData, ClientData>();
      }      
     
      map.put(tData, testToClient.get(tData));
    }    
  
    if (map.size() > 0) { 
      runsToExec.put(runsToExec.size() + 1, map);
      logEnv.log("Tests for run[" + runsToExec.size() + "] determined. " + map.size() + " in this run." );
    }            
    
    return Test.PASSED;
  }

  public List<TestType> wsCalls1() {
    return wsCalls(1);
  }
  
  public List<TestType> wsCalls2() {
    return wsCalls(2);
  }
  
  public List<TestType> wsCalls3() {
    return wsCalls(3);
  }
  
  public List<TestType> wsCalls4() {
    return wsCalls(4);
  }
  
  public List<TestType> wsCalls5() {
    return wsCalls(5);
  }
  
  public List<TestType> wsCalls6() {
    return wsCalls(6);
  }
  
  public List<TestType> wsCalls7() {
    return wsCalls(7);
  }
  
  public List<TestType> wsCalls(int run) {
    List<TestType> tests = new ArrayList<TestType>();
    Map<TestData, ClientData> map = runsToExec.get(run);
    
    if (map == null || map.isEmpty()) {
      logEnv.log("Nothing more to execute.");
      return tests;
    }
    
    for(TestData tData : map.keySet()) {
      try {
        exec = new SdoExecutor(tData, map.get(tData));
        exec.setLogger(logEnv);
        
        TestType test = exec.execute(1);
        tests.add(test);
        if (STATUS_ERROR.equals(test.getStatus())) {
          logEnv.log("Test: FAILED");
          testResults.put(tData, false);
          continue;
        }        
        
        logEnv.log("Test: PASSED");        
        testResults.put(tData, true);
      } catch (Exception e) {
        logEnv.log(e);
        logEnv.log("Test [" + tData + "]: FAILED");
        testResults.put(tData, false);
        failedTests.put(tData, e);
      }
    }  
    
    return tests;   
  }
  
  public int zipNotTestedFolders() {
    prep.getNotTestedZipFile();
    return PASSED;
  }
  
  public static void main(String[] args) throws Exception {
      Test_Ulf test = new Test_Ulf();
//      URL url = test.getClass().getClassLoader().getResource("report/consumption/es/es-consumption-test-report.xsd");
//      List<Type> types = test.context.getXSDHelper().define(url.openStream(), url.toString());
//      URL url = test.getClass().getClassLoader().getResource("report/payload/metadata/payload_metadata.xsd");
//      List<Type> types = context.getXSDHelper().define(url.openStream(), url.toString());
      Report report = (Report)test.context.getDataFactory().create(Report.class);
      Calendar calendar = Calendar.getInstance();
      report.setCw(calendar.get(Calendar.WEEK_OF_YEAR));
      report.setYear(calendar.get(Calendar.YEAR));
      report.setTechnology("Dynamic API");
      report.setConsumer("SDO");
      
      List<TestType> tests = report.getTest();

      try {
//          InterfaceGenerator gen =
//              ((SapTypeHelper)test.context.getTypeHelper()).createInterfaceGenerator(
//                  "C:/sandbox/p4/sdo/engine/js.sdo.lib/CoreDev_stream/src/sdo/_sdo_ats/libm/core");
//          gen.addPackage(
//              "http://www.sap.com/es-consumption-test-report",
//              "report.consumption.es");
//          gen.addSchemaLocation(
//              "http://www.sap.com/es-consumption-test-report",
//              "es-consumption-test-report.xsd");
//          gen.generate(types);
//          InterfaceGenerator gen = ((SapTypeHelper)context.getTypeHelper()).createInterfaceGenerator("C:/sandbox/ats/src");
//          gen.addPackage("http://www.sap.com/abapxml", "report.payload.metadata");
//          gen.addSchemaLocation("http://www.sap.com/abapxml", "payload_metadata.xsd");
//          gen.generate(types);
//          System.exit(2);
          
//          test.prepare();
//          test.executeTest1();
//          test.executeTest2();
//          test.executeTest3();
//          test.executeTest4();
//          test.executeTest5();
//          test.executeTest6();
//          test.executeTest7();
          test.processTest_ClientData();
          test.processWSCalls();
          tests.addAll(test.wsCalls1());
          tests.addAll(test.wsCalls2());
          tests.addAll(test.wsCalls3());
          tests.addAll(test.wsCalls4());
          tests.addAll(test.wsCalls5());
          tests.addAll(test.wsCalls6());
          tests.addAll(test.wsCalls7());
          //test.zipNotTestedFolders();
      } catch (Exception ex) {
          ex.printStackTrace();
      }
      
      System.out.println("Executed test: " + tests.size());
      int failed = 0;
      for (TestType testCase : tests) {
        if (STATUS_ERROR.equals(testCase.getStatus())) {
            ++failed;
        }
      }
      System.out.println("failed: " + failed);
      
      Set<Entry<String, Integer>> entries = SdoExecutor.exceptions.entrySet();
      for (Entry<String, Integer> entry : entries) {
        System.out.println(entry.getValue() + " - " + entry.getKey());
      }
      Set<Entry<String, Integer>> entries2 = SdoExecutor.ioExceptions.entrySet();
      for (Entry<String, Integer> entry : entries2) {
        System.out.println(entry.getValue() + " : " + entry.getKey());
      }
      
      System.out.println("***************************************************");
      for (Entry<TestData, Exception> entry : test.failedTests.entrySet()) {
          System.out.println("TestData: " + entry.getKey());
          entry.getValue().printStackTrace();
      }
      
      File reportFile = new File(REPORT_FOLDER + "/sdo-report-cw" + report.getCw() + ".xml");
      reportFile.createNewFile();
      OutputStream out = new FileOutputStream(reportFile);
      test.context.getXMLHelper().save(
          (DataObject)report,
          "http://www.sap.com/es-consumption-test-report",
          "report",
          out);
  }

}