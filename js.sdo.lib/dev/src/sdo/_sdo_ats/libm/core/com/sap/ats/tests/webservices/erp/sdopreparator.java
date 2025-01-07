package com.sap.ats.tests.webservices.erp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import javax.xml.namespace.QName;

import report.service.ecattdistinc.testdata.Abap;
import report.service.ecattdistinc.testdata.ItemType;

import com.sap.ats.Test;
import com.sap.ats.env.LogEnvironment;
import com.sap.ats.tests.webservices.dynamic.util.Util;

import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;

public class SdoPreparator {
  private LogEnvironment _logger;
  private HelperContext _context;
  private String _testDataFile;
  private String _testSetFolder = "ICFRECORDINGS";
  private String _wsdlFolder = "WSDL_BINDING";
  private String _iniDir = "C:/sandbox/ats/ESOA_TESTDATA_20071204/";
  private List<ClientData> _clientDataList = new ArrayList<ClientData>();  
  private Map<TestData, ClientData> _testToClient = new Hashtable<TestData, ClientData>();
  private List<File> _notTested = new ArrayList<File>();
  private String _notTestedZipFile = "C:/sandbox/ats/erp_demo_not_tested/packed_folders.zip"; // change to D:/erp_not_tested/packed_folders.zip for the first system
  private List<String> _tested = new ArrayList<String>(); 
  private String _statusFileName = "C:/sandbox/ats/erp_demo_not_tested/service_invocation.txt"; // change to D:/erp_not_tested/service_invocation.txt for the first system
  private int _result = Test.PASSED;
  
  public SdoPreparator(String testDataFile, HelperContext context) {
    _testDataFile = testDataFile;
    _context = context;
  }
  
  public void prepare() {       
    traverseTestSetDir();
    // traverseClientsDir();
    // findClientForTestData();
  }
  
  public Map<TestData, ClientData> getTestToClientMap() {
    return _testToClient;
  }
  
  public int getResult() {
    return _result;
  }
  
  public void setLogger(LogEnvironment logEnv) {
    _logger = logEnv;    
  }
  
  private void traverseTestSetDir() {
    _logger.log("Processing test set: " + _testDataFile);
    if (_testDataFile == null || _testDataFile.equals("")) {
        _logger.log("Incorrect test set: " + _testDataFile);
        _result = Test.FAILED;
        return;
      }
    
    List<ItemType> testItems = null;
    try {
        testItems = parseTestData();
    } catch (IOException ex) {
        _logger.log(ex);
    }
    if (testItems == null || testItems.isEmpty()) {
      _logger.log("Incorrect test set: " + _testDataFile);
      _result = Test.FAILED;
      return;
    }
    
    File[] folderFiles = null;
    TestData tData = null;
    List<File> payloads = null;
    ClientData clData = null;
    
    for (ItemType item : testItems) {
        String testDataFolder = item.getTestdatafolder();
        File folder = new File(_iniDir + _testSetFolder
            + testDataFolder.substring(testDataFolder.lastIndexOf('\\')));

        folderFiles = folder.listFiles();
        _logger.log("\nProcessing folder: " + folder);

        if (folderFiles != null && folderFiles.length > 0) {
            try {
                tData = new TestData(item.getWsdname());
            } catch (IllegalArgumentException ex) {
                _logger.log("Invalid endpoint WSDL URL: " + ex.getMessage());
                _result = Test.FAILED;
                continue;
            }

            tData.setInfQName(new QName(item.getNamespace(), item.getPortname()));

            payloads = getPayloadFiles(folderFiles);

            if (payloads == null || payloads.size() == 0) {
                _logger.log("No payloads in folder " + folder.getAbsolutePath());
                _result = Test.FAILED;
                continue;
            }

            tData.setPayloads(payloads);
            _logger.log("Test Data: " + tData);
        }

        String url = item.getWsdlUrlBinding();
        _logger.log("URL: " + url);

        if (url == null || url.equals("")) {
            _logger.log("Missing url: " + url);
            _result = Test.FAILED;
            continue;
        }

        clData = new ClientData(url);
        clData.setFolder(folder);
        String wsdlFile = item.getWsdlFileBind();
        clData.setInfFile(
            _iniDir + _wsdlFolder + wsdlFile.substring(wsdlFile.lastIndexOf('\\')));
        _logger.log("Client Data: " + clData);

        _logger.log("Mapped Test Data[" + tData.getReqPath()
            + "] to Client Data[" + clData.getUrl() + "] in folder "
            + clData.getFolder());
        _testToClient.put(tData, clData);
        _tested.add(clData.getServiceQName() + "=EXECUTED\n");
    }          
  }
  
  private List<File> getPayloadFiles(File[] folderFiles) {
    String name = null;
    List<File> payloads = new ArrayList<File>(); 
    
    for (File file : folderFiles) {
      name = file.getName();
      
      if (!name.endsWith(".url.xml")) {
        _logger.log("Payload file: " + name);
        payloads.add(file);
      }      
    }   
    
    return payloads;
  }
  
  private void traverseIniDir() {
    _logger.log("Processing initial folder: " + _iniDir);
    
    if (_iniDir == null || _iniDir.equals("")) {
      _logger.log("Incorrect initial folder: " + _iniDir);
      return;
    }
    
    File iniD = new File(_iniDir);
    File[] iniFolders = null;
    File[] folderFiles = null;
    ClientData client = null;
                        
    if (iniD.isDirectory()) {
      iniFolders = iniD.listFiles();
      
      if (iniFolders != null && iniFolders.length > 0) {
        for (File folder : iniFolders) {
          folderFiles = folder.listFiles();
          _logger.log("\nProcessing folder: " + folder);
                    
          if (folderFiles != null || folderFiles.length > 0) {
            for (File file : folderFiles) {
              if (file.isFile() && file.getName().endsWith(".inf")) {
                _logger.log("Setup details are in: " + file.getName());                
                client = findGeneratedClientForInfFile(file);
                
                if (client == null) {
                  _logger.log("Not tested " + folder);
                  _notTested.add(folder);      
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
    Collection<ClientData> clients = _testToClient.values();
    
    if (clients.isEmpty()) {
      return null;      
    }    
    
    String fileName = file.getName();
    _logger.log("\nSearching ClientData for Setup file " + fileName);
    
    for (ClientData client: clients) {
      if (client.getInfFile().equals(fileName)) {
        client.setIniFolder(file.getParentFile());
        _logger.log("Initial folder " + file.getParent() + " set to " + client.getUrl() + " in folder " + client.getFolder());          
        return client;
      }
    }    
    
    return null;
  }
 
  private void packNotTestedFolders() {
    if (_notTested.isEmpty()) {
      return;
    }
    
    if (_notTestedZipFile == null || _notTestedZipFile.equals("")) {
      _logger.log("Incorrect zip file name: " + _notTestedZipFile);
      return;
    }
    
    File file = new File(_notTestedZipFile);
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
    
      for (File folder : _notTested) {
        files = folder.listFiles();
        
        for (File tempFile : files) {
          zEntryName = folder.getName() + "/" + tempFile.getName();
          try {
            in = new FileInputStream(tempFile);
            buffIn = new BufferedInputStream(in);
            Util.writeZipEntry(zipOut, buffIn, buffIn.available(), zEntryName);            
          } catch (Exception e) {
            _logger.log(e);
          } finally {
            in.close();
            buffIn.close();
          }
        }
      }  
    } catch (IOException ioe) {
      _logger.log(ioe);      
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
      writer = new FileWriter(_statusFileName);
       
      if (!_tested.isEmpty()) {
        for (String line : _tested) {
          writer.write(line);
        }
      }
        
      if (!_clientDataList.isEmpty()) {
        for (ClientData client: _clientDataList) {
          writer.write(client.getServiceQName() + "=NOT_EXECUTED\n");          
        }
      }
    } catch (Exception exc) {
     _logger.log(exc);
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

  private List<ItemType> parseTestData() throws IOException {
      // intialize context
      _context.getTypeHelper().getType(Abap.class);
      
      URL url = new URL(_testDataFile);
      XMLDocument xmlDoc =
          _context.getXMLHelper().load(url.openStream(), url.toString(), null);
      Abap testDescription = (Abap)xmlDoc.getRootObject();
      return testDescription.getValues().getData().getItem();
  }
  
}
