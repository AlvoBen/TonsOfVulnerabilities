package com.sap.ats.tests.webservices.erp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.sap.ats.Test;
import com.sap.ats.env.LogEnvironment;
import com.sap.ats.tests.webservices.dynamic.util.Util;

public class Preparator {
  private LogEnvironment logger;  
  private String testSetDir;  
  private String clientsDir;
  private String iniDir = "C:/sandbox/ats/ERP_SERVICES_20071126_Q2O"; // change to d:/erp for the first system -> also in Test_Maria!!!
  private List<TestData> testDataList = new ArrayList<TestData>();
  private List<ClientData> clientDataList = new ArrayList<ClientData>();  
  private Map<TestData, ClientData> testToClient = new Hashtable<TestData, ClientData>();
  private List<File> notTested = new ArrayList<File>();
  private String notTestedZipFile = "C:/sandbox/ats/erp_demo_not_tested/packed_folders.zip"; // change to D:/erp_not_tested/packed_folders.zip for the first system
//  private Map<String, String> serviceStatus = new Hashtable<String, String>(); 
  private List<String> tested = new ArrayList<String>(); 
  private String statusFileName = "C:/sandbox/ats/erp_demo_not_tested/service_invocation.txt"; // change to D:/erp_not_tested/service_invocation.txt for the first system
  private int result = Test.PASSED;
  
  public Preparator(String testDir, String clientDir) {
    testSetDir = testDir;
    clientsDir = clientDir;    
  }
  
  public void prepare() {       
    traverseTestSetDir();
    traverseClientsDir();
    findClientForTestData();
  }
  
  public Map<TestData, ClientData> getTestToClientMap() {
    return testToClient;
  }
  
  public int getResult() {
    return result;
  }
  
  public void setLogger(LogEnvironment logEnv) {
    logger = logEnv;    
  }
  
  private void traverseTestSetDir() {
    logger.log("Processing test set folder: " + testSetDir);
    
    if (testSetDir == null || testSetDir.equals("")) {
      logger.log("Incorrect test set folder: " + testSetDir);
      result = Test.FAILED;
      return;
    }
    
    File testSetD = new File(testSetDir);
    File[] testSetFolders = null;
    File[] folderFiles = null;
    TestData tData = null;
    List<File> payloads = null; 
        
    if (testSetD.isDirectory()) {
      testSetFolders = testSetD.listFiles();
      
      if (testSetFolders != null && testSetFolders.length > 0) {
        for (File folder : testSetFolders) {
          folderFiles = folder.listFiles();
          logger.log("\nProcessing folder: " + folder);
                    
          if (folderFiles != null && folderFiles.length > 0) {
            for (File file : folderFiles) {
              if (file.isFile() && file.getName().endsWith(".url.xml")) {
                logger.log("Connection details are in: " + file.getName());                
                tData = obtainTestData(file);
                
                if (tData == null) { 
                  result = Test.FAILED;
                  continue;
                }
                
                tData.setFolder(file.getParentFile());
                payloads = getPayloadFiles(folderFiles);
                
                if (payloads == null || payloads.size() == 0) {
                  logger.log("No payloads in folder " + folder.getAbsolutePath());
                  result = Test.FAILED;
                  continue;
                }
                
                tData.setPayloads(payloads);
                logger.log("Test Data: " + tData);
                testDataList.add(tData);
                break;
              }              
            }
          }          
        }
      }
    }    
  }
  
  private void traverseClientsDir() {
    logger.log("Processing clients folder: " + clientsDir);
    
    if (clientsDir == null || clientsDir.equals("")) {
      logger.log("Incorrect clients folder: " + clientsDir);
      result = Test.FAILED;
      return;
    }
    
    File clientsD = new File(clientsDir);
    File[] clientFolders = null;
    String url = null;
    ClientData clData = null;
    File configFile = null;
    File mappingFile = null;
    String[] service_port = null;
    String serviceClass = null;
    String portClass = null;
    String serviceQName = null;
    File[] folderContents = null;
    
    if (clientsD.isDirectory()) {
      clientFolders = clientsD.listFiles();
      
      if (clientFolders != null && clientFolders.length > 0) {
        for (File folder : clientFolders) {
          logger.log("\nProcessing folder: " + folder);
          configFile = new File(folder, "configuration.xml");
            
          if (configFile.isFile() && configFile.canRead()) {
            logger.log("Processing configuration.xml");
            url = getUrl(configFile);
            logger.log("URL: " + url);
            
            if (url == null || url.equals("")) {
              logger.log("Missing url: " + url);
              result = Test.FAILED;
              continue;
            }
            
            clData = new ClientData(url);
            clData.setFolder(folder);
            
            mappingFile = new File(folder, "mapping.xml");
             
            if (mappingFile.isFile() && mappingFile.canRead()) {
              logger.log("Processing mapping.xml");
              service_port = getService_PortClass(mappingFile);
              serviceClass = service_port[0];
              
              if (serviceClass == null || serviceClass.equals("")) {
                logger.log("Missing service class: " + serviceClass);
                result = Test.FAILED;
                continue;
              }
              
              logger.log("Service Class: " + serviceClass);
              clData.setServiceClass(serviceClass);
              portClass = service_port[1];
              
              if (portClass == null || portClass.equals("")) {
                logger.log("Missing port class: " + portClass);
                result = Test.FAILED;
                continue;
              }
              
              logger.log("Port Class: " + portClass);
              clData.setPortClass(portClass);  
              serviceQName = service_port[2];
              
              if (serviceQName == null || serviceQName.equals("")) { 
                logger.log("Missing service QName: " + serviceQName);               
              }
              
              clData.setServiceQName(serviceQName);
              folderContents = folder.listFiles();
              
              for (File file : folderContents) {
                if (file.isFile() && file.getName().endsWith(".inf")) {
                  clData.setInfFile(file.getName());
                  break;
                }
              }              
              
              logger.log("Client Data: " + clData);
              clientDataList.add(clData);
            }
          }          
        }
      }
    } 
  }
  
  private String getRequestPath(Element docEl) {
    String reqPath = null;
    Element tempEl = null;
    NodeList tempList = null;
        
    try {
      tempList = docEl.getElementsByTagName("REQUEST_PATH");
        
      if (tempList != null && tempList.getLength() > 0) {
        tempEl = (Element) tempList.item(0);
        tempList = tempEl.getChildNodes();
        
        if (tempList == null || tempList.getLength() == 0) { 
          return reqPath;
        }
         
        if (tempList.item(0) instanceof Text) {
          reqPath = ((Text) tempList.item(0)).getData().trim();
        }
      }
    } catch (Exception e) {
      logger.log(e);
      result = Test.FAILED;
      return reqPath;
    }
    
    return reqPath;    
  }
  
  private String getUrl(File file) {
    String url = null;
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = null;
    Element tempEl = null;
    NodeList tempList = null;
        
    try {
      docBuilder = factory.newDocumentBuilder();
      tempEl = docBuilder.parse(file).getDocumentElement();
      tempList = tempEl.getElementsByTagName("BindingData");
      
      if (tempList != null && tempList.getLength() > 0) {
        tempEl = (Element) tempList.item(0);
        url = tempEl.getAttribute("url");       
      }
    } catch (Exception e) {
      logger.log(e);
      result = Test.FAILED;
      return url;
    }
    
    return url;    
  }
  
  private String[] getService_PortClass(File file) {
    String info[] = new String[3];
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = null;
    Element docEl = null;
    Element tempEl = null;
    NodeList tempList = null;
    NodeList innerList = null;
        
    try {
      docBuilder = factory.newDocumentBuilder();
      docEl = docBuilder.parse(file).getDocumentElement();
      tempList = docEl.getElementsByTagName("interface");
      
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
    } catch (Exception e) {
      logger.log(e);
      result = Test.FAILED;
      return info;
    }
    
    return info;    
  }
  
  private TestData obtainTestData(File file) {    
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
    DocumentBuilder docBuilder = null;
    Element docEl = null;
    Element tempEl = null;
    NodeList tempList = null;
    String reqPath = null;
    TestData testData = null;
    String host = null;
    String port = null;
        
    try {
      docBuilder = factory.newDocumentBuilder();
      docEl = docBuilder.parse(file).getDocumentElement();
      reqPath = getRequestPath(docEl);
      logger.log("Request Path: " + reqPath);
      
      if (reqPath == null || reqPath.equals("")) {
        logger.log("Missing request path: " + reqPath);
        result = Test.FAILED;
        return testData;
      }
      
      testData = new TestData(reqPath);
      
      tempList = docEl.getElementsByTagName("REQUEST_HOST");
        
      if (tempList != null && tempList.getLength() > 0) {
        tempEl = (Element) tempList.item(0);
        tempList = tempEl.getChildNodes();
        
        if (tempList != null && tempList.getLength() > 0) { 
         if (tempList.item(0) instanceof Text) {
            host = ((Text) tempList.item(0)).getData().trim();
            logger.log("Host: " + host);
            
            if (host == null || host.equals("")) {
              logger.log("Missing host: " + host);
              result = Test.FAILED;
              return testData;
            }
            
            // additional check -> if the request host belongs to system Q2O 
            if (host.toLowerCase().indexOf("q2o") < 0) {
              logger.log("WARNING: Host " + host + " does not belong to system Q2O. Setting usai1q2o.wdf.sap.corp by default.");              
            } else {
              testData.setHost(host);
            }
          }  
        }
      }
      
      tempList = docEl.getElementsByTagName("REQUEST_PORT");
      
      if (tempList != null && tempList.getLength() > 0) {
        tempEl = (Element) tempList.item(0);
        tempList = tempEl.getChildNodes();
        
        if (tempList != null && tempList.getLength() > 0) { 
          if (tempList.item(0) instanceof Text) {
             port = ((Text) tempList.item(0)).getData().trim();
             logger.log("Port: " + port);
             
             if (port == null || port.equals("")) {
               logger.log("Missing port: " + port);
               result = Test.FAILED;
               return testData;
             }
             
             if (host.toLowerCase().indexOf("q2o") > 0) {
                 testData.setPort(port);
             }  // else use the default port 
           }  
         }
       }
      
    } catch (Exception e) {
      result = Test.FAILED;
      logger.log(e);
    }
  
  return testData; 
  }
  
  private List<File> getPayloadFiles(File[] folderFiles) {
    String name = null;
    List<File> payloads = new ArrayList<File>(); 
    
    for (File file : folderFiles) {
      name = file.getName();
      
      if (!name.endsWith(".url.xml")) {
        logger.log("Payload file: " + name);
        payloads.add(file);
      }      
    }   
    
    return payloads;
  }
  
  private void findClientForTestData() {
    if (testDataList.isEmpty() || clientDataList.isEmpty()) {
      logger.log("Test set size: " + testDataList.size());
      logger.log("Client set size: " + clientDataList.size());
      result = Test.FAILED;
      return;
    }    
    
    for (TestData test : testDataList) {
      logger.log("Searching ClientData for TestData[" + test.getReqPath() + "] in folder " + test.getFolder());
      
      for (ClientData client: clientDataList) {
        int index = client.getUrl().indexOf(test.getReqPath());
        if (index > -1) {
          int indexNext = index+test.getReqPath().length();
          if (indexNext < client.getUrl().length()) {
            char nextChar = client.getUrl().charAt(indexNext);
            if (!(nextChar=='/' || nextChar=='?')) {
              continue;
            }
          }
          logger.log("Mapped Test Data[" + test.getReqPath() + "] to Client Data[" + client.getUrl() + "] in folder " + client.getFolder());          
          testToClient.put(test, client);
          clientDataList.remove(client);
          tested.add(client.getServiceQName() + "=EXECUTED\n"); 
          break;
        }
      }
    }    
  }
  
  private void traverseIniDir() {
    logger.log("Processing initial folder: " + iniDir);
    
    if (iniDir == null || iniDir.equals("")) {
      logger.log("Incorrect initial folder: " + iniDir);
      return;
    }
    
    File iniD = new File(iniDir);
    File[] iniFolders = null;
    File[] folderFiles = null;
    ClientData client = null;
                        
    if (iniD.isDirectory()) {
      iniFolders = iniD.listFiles();
      
      if (iniFolders != null && iniFolders.length > 0) {
        for (File folder : iniFolders) {
          folderFiles = folder.listFiles();
          logger.log("\nProcessing folder: " + folder);
                    
          if (folderFiles != null || folderFiles.length > 0) {
            for (File file : folderFiles) {
              if (file.isFile() && file.getName().endsWith(".inf")) {
                logger.log("Setup details are in: " + file.getName());                
                client = findGeneratedClientForInfFile(file);
                
                if (client == null) {
                  logger.log("Not tested " + folder);
                  notTested.add(folder);      
                  break;
                }
              }              
            }
          }          
        }
      }
    }    
  }
  
  private ClientData findGeneratedClientForInfFile(File file) {
    Collection<ClientData> clients = testToClient.values();
    
    if (clients.isEmpty()) {
      return null;      
    }    
    
    String fileName = file.getName();
    logger.log("\nSearching ClientData for Setup file " + fileName);
    
    for (ClientData client: clients) {
      if (client.getInfFile().equals(fileName)) {
        client.setIniFolder(file.getParentFile());
        logger.log("Initial folder " + file.getParent() + " set to " + client.getUrl() + " in folder " + client.getFolder());          
        return client;
      }
    }    
    
    return null;
  }
 
  private void packNotTestedFolders() {
    if (notTested.isEmpty()) {
      return;
    }
    
    if (notTestedZipFile == null || notTestedZipFile.equals("")) {
      logger.log("Incorrect zip file name: " + notTestedZipFile);
      return;
    }
    
    File file = new File(notTestedZipFile);
    file.getParentFile().mkdirs();
   
    File[] files = null;
    FileOutputStream fos = null;
    BufferedOutputStream bos = null;
    ZipOutputStream zipOut = null;
    String zEntryName = null;
    FileInputStream in = null; 
    BufferedInputStream buffIn = null;
    
    try {   
      // Instance ZIP stream
      fos = new FileOutputStream(file);
      bos = new BufferedOutputStream(fos);
      zipOut = new ZipOutputStream(bos);      
    
      for (File folder : notTested) {
        files = folder.listFiles();
        
        for (File tempFile : files) {
          zEntryName = folder.getName() + "/" + tempFile.getName();
          try {
            in = new FileInputStream(tempFile);
            buffIn = new BufferedInputStream(in);
            Util.writeZipEntry(zipOut, buffIn, buffIn.available(), zEntryName);            
          } catch (Exception e) {
            logger.log(e);
          } finally {
            in.close();
            buffIn.close();
          }
        }
      }  
    } catch (IOException ioe) {
      logger.log(ioe);      
    } finally {
      try { 
        zipOut.finish();
        zipOut.close();
        bos.close();
        fos.close();
      } catch (Exception e) {
        // nothing to do
      }      
    }    
  }
  
  public void getNotTestedZipFile () {
    traverseIniDir();
    packNotTestedFolders();   
    FileWriter writer = null;
    
    try {
      writer = new FileWriter(statusFileName);
       
      if (!tested.isEmpty()) {
        for (String line : tested) {
          writer.write(line);
        }
      }
        
      if (!clientDataList.isEmpty()) {
        for (ClientData client: clientDataList) {
          writer.write(client.getServiceQName() + "=NOT_EXECUTED\n");          
        }
      }
    } catch (Exception exc) {
     logger.log(exc);
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (Exception e) {
          // nothing to do
        }
      }
    }    
  }
  
}
