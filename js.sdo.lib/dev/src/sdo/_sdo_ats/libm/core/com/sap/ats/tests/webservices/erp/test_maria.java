package com.sap.ats.tests.webservices.erp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.TestResult;

import com.sap.ats.Test;
import com.sap.ats.env.LogEnvironment;
import com.sap.ats.tests.webservices.dynamic.util.Util;
import com.sap.engine.services.webservices.espbase.client.ProxyGeneratorConfigNew;
import com.sap.engine.services.webservices.espbase.client.ProxyGeneratorNew;
import com.sap.engine.services.webservices.espbase.configuration.ConfigurationFactory;
import com.sap.engine.services.webservices.espbase.configuration.ConfigurationRoot;
import com.sap.engine.services.webservices.espbase.mappings.MappingFactory;
import com.sap.engine.services.webservices.espbase.mappings.MappingRules;
import com.sap.engine.services.webservices.jaxrpc.util.PackageBuilder;

public class Test_Maria implements Test {
  private String folder = "C:/sandbox/ats/ERP_SERVICES_20071112_Q2O"; // change to d:/erp for the first system -> also in Preparator!!!
  private String gen_dir = "C:/sandbox/ats/erp_demo_gen/"; // change to d:/erp_gen for the first system
  private String testDataDir = "C:/sandbox/ats/ICFRECORDINGS";
  private File inputDir = new File(folder);
  private File[] wsdl_folders;
  private Map<Integer, List<File>> runs = new Hashtable<Integer, List<File>>();
  private File wsdl_folder = null; 
  private File[] folderContents = null;
  private Preparator prep = null; 
  private Map<TestData, ClientData> testToClient; 
  private Executor exec;
  private Map<Integer, Map<TestData, ClientData>> runsToExec = new Hashtable<Integer, Map<TestData, ClientData>>();
  private Map<TestData, Boolean> testResults = new HashMap<TestData, Boolean>();
  
  private LogEnvironment logEnv;
  
  public int prepare() {
    try {
      logEnv = new LogEnvironmentImpl();
      
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
    config.setWsdlPath(wsdl_path);
    File customization = new File((new File(wsdl_path)).getParentFile(), "customization.xml"); // customization.xml or .cust file
    
    if (customization.isFile() && customization.canRead()) {
      logEnv.log("Customization file: " + customization);
      config.setExternalBindings(new String[]{customization.getAbsolutePath()});
    }
    
    proxyGenerator.generateAll(config);
    logEnv.log("Proxy was generated");

    String cp = "";
    File srcFile = new File(curr_gen_dir + "/src");
    PackageBuilder packbuild = new PackageBuilder();
    File outputPath = new File(curr_gen_dir + "/bin");
    outputPath.mkdir();
    packbuild.setOutputPath(outputPath);
    packbuild.compile(cp, srcFile);
    logEnv.log("Proxy classes were compiled");

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
      prep = new Preparator(testDataDir, gen_dir);
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

  public int wsCalls1() {
    return wsCalls(1);
  }
  
  public int wsCalls2() {
    return wsCalls(2);
  }
  
  public int wsCalls3() {
    return wsCalls(3);
  }
  
  public int wsCalls4() {
    return wsCalls(4);
  }
  
  public int wsCalls5() {
    return wsCalls(5);
  }
  
  public int wsCalls6() {
    return wsCalls(6);
  }
  
  public int wsCalls7() {
    return wsCalls(7);
  }
  
  public int wsCalls(int run) {
    int result = PASSED;
    Map<TestData, ClientData> map = runsToExec.get(run);
    
    if (map == null || map.isEmpty()) {
      logEnv.log("Nothing more to execute.");
      return result;
    }
            
    for(TestData tData : map.keySet()) {
      try {
        exec = new Executor(tData, map.get(tData));
        exec.setLogger(logEnv);
        
        if (exec.execute(1) == FAILED) {
          logEnv.log("Test: FAILED");
          testResults.put(tData, false);
          result = FAILED;
          continue;
        }        
        
        logEnv.log("Test: PASSED");        
        testResults.put(tData, true);
      } catch (Exception e) {
        logEnv.log(e);
        testResults.put(tData, false);
        result = FAILED; 
      }
    }  
    
    return result;   
  }
  
  public int zipNotTestedFolders() {
    prep.getNotTestedZipFile();
    return PASSED;
  }
  
  public static void main(String[] args) {
      Test_Maria test = new Test_Maria();

      try {
          test.prepare();
          test.executeTest1();
          test.executeTest2();
          test.executeTest3();
          test.executeTest4();
          test.executeTest5();
          test.executeTest6();
          test.executeTest7();
          test.processTest_ClientData();
          test.processWSCalls();
          test.wsCalls1();
          test.wsCalls2();
          test.wsCalls3();
          test.wsCalls4();
          test.wsCalls5();
          test.wsCalls6();
          test.wsCalls7();
      } catch (Exception ex) {
          ex.printStackTrace();
      }
      
      System.out.println("Executed test: " + test.testResults.size());
      int failed = 0;
      for (Entry<TestData,Boolean> entry : test.testResults.entrySet()) {
        if (!entry.getValue()) {
            ++failed;
        }
      }
      System.out.println("failed: " + failed);
  }
  
}