package com.sap.ats.tests.webservices.erp;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sap.ats.Test;
import com.sap.ats.env.LogEnvironment;
import com.sap.engine.lib.xml.dom.DOM;
import com.sap.engine.services.webservices.espbase.client.api.HTTPControlFactory;
import com.sap.engine.services.webservices.espbase.client.api.HTTPControlInterface;

public class Executor {
  private LogEnvironment logger;  
  private TestData testData;
  private ClientData clientData;
  private int result = Test.PASSED;
  
  public Executor(TestData test, ClientData client) {
    testData = test;
    clientData = client;
  }
  
  public void setLogger(LogEnvironment logEnv) {
    logger = logEnv;    
  }
  
  public int execute(int iterations) {
    logger.log("\n***************************************************************");
    logger.log("Executing test for \n Client data: " + clientData + "\n Test Data: " + testData);
    String binFolder = clientData.getFolder().getAbsolutePath() + "/bin";
    TestClassLoader loader = new TestClassLoader(this.getClass().getClassLoader(), new String[]{binFolder});
    loader.setLogger(logger);
    logger.log("Test Class Loader: " + loader);
    String service = clientData.getServiceClass();
    
    try {
      logger.log("Service Class to load: " + service);      
      Class serviceClass = loader.loadClass(service);
      Object serviceObj = serviceClass.newInstance();
      QName portName = (QName) ((Iterator) serviceClass.getMethod("getPorts").invoke(serviceObj)).next(); 
      Class portClass = loader.loadClass(clientData.getPortClass());           
      Service serv = (Service) serviceObj;
      Object port = serv.getPort(portName, portClass);
      configurePort(port); 
      Method[] portMethods  = portClass.getDeclaredMethods();
      
      if (portMethods == null || portMethods.length == 0) {
        logger.log("No methods found for port: " + portClass);
        result = Test.FAILED;
        return result;
      }
                 
      Method portMeth = portMethods[0]; // should be one
      logger.log("Operation: " + portMeth);  
      Class[] paramClasses = portMeth.getParameterTypes();
      
      if (paramClasses.length == 0) {
        logger.log("Method " + portMeth.getName() + " has no parameters");
        result = Test.FAILED;
        return result; // no such case should be present 
      }
      
      Class paramClass = paramClasses[0]; // should be one
      logger.log("Parameter type: " + paramClass);
      Element paramValueEl = null;
      Object paramObject = null;
      Object returnObj = null;  
      long startTime = 0;
      long duration = 0;
          
      for (File payload : testData.getPayloads()) {
        logger.log("Loading parameter value from " + payload.getName());        
        paramValueEl = parsePayload(payload);
        try {
          paramObject = loadParam(paramValueEl, paramClass);
          logger.log("Parameter value: " + paramObject);
        } catch (Exception ex) {
          logger.log("Loading parameter failed:\n");
          logger.log(ex);
          result = Test.FAILED;
          continue;
        }
        
        if (paramObject == null) {
          logger.log("Parameter value is null");
          result = Test.FAILED;
          continue;
        }
        
        if (iterations > 0) {
          for (int i = 0; i < iterations; i++) {  
            try {
              logger.log("Invoking " + portMeth + " of " + serviceObj + " with parameter " + paramObject);
              startTime = System.currentTimeMillis();
              returnObj = portMeth.invoke(port,  paramObject);
              duration = System.currentTimeMillis() - startTime;
              logger.log("$$$$$$$$$$$ Invocation took " + duration + " ms");
              logger.log("Return value: " + returnObj + '\n');          
            } catch (Throwable th) {
              logger.log("WS INVOCATION FAILED:\n");
              logger.log(th);
              result = Test.FAILED;
            }
          }
        }
      }
    } catch (Exception e) {
      logger.log(e);
      result = Test.FAILED;
    } catch (Throwable th) {
      logger.log(th);
      result = Test.FAILED;
    }
    
    return result;
  }
  
  private void configurePort(Object port) {
    logger.log("Configuring Port: " + port);   
    BindingProvider bindingP = (BindingProvider) port;
    Map<String, Object> reqCtx =  bindingP.getRequestContext();
        
    // workaround for QINTesting
    if (testData.getReqPath().startsWith("/")) {          
      String url = "http://" + testData.getHost() + ":" + testData.getPort() + testData.getReqPath();
      String sapClient = testData.getClient();
      
      if (sapClient != null && !sapClient.equals("")) {
        url = url + "?sap-client=" + sapClient;
      }
      
      logger.log("Setting URL: " + url);
      reqCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
    }     
      
    String user = testData.getUser();
    String pass = testData.getPassword();
    logger.log("Setting User: " + user + " and Password: " + pass);
    reqCtx.put(BindingProvider.USERNAME_PROPERTY, user);
    reqCtx.put(BindingProvider.PASSWORD_PROPERTY, pass);   
     
    try {
      HTTPControlInterface httpCtrlI = HTTPControlFactory.getInterface(port);
      FileOutputStream reqStream = new FileOutputStream(new File(clientData.getFolder(), "request_" + System.currentTimeMillis() + ".log"));
      FileOutputStream respStream = new FileOutputStream(new File(clientData.getFolder(), "response_" + System.currentTimeMillis() + ".log"));
      httpCtrlI.startLogging(reqStream, respStream);    
    } catch (Exception e) {
      logger.log(e);  
      result = Test.FAILED;
    }
  }
  
  private Element parsePayload(File file) {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    DocumentBuilder docBuilder = null;
    Element tempEl = null;
    NodeList tempList = null;
        
    try {
      docBuilder = factory.newDocumentBuilder();
      tempEl = docBuilder.parse(file).getDocumentElement();
      
      List body = DOM.getChildElementsByTagNameNS(tempEl, "http://schemas.xmlsoap.org/soap/envelope/", "Body");

      if (body != null && body.size() > 0) {
        tempEl = (Element) body.get(0); // should be one
        tempList = tempEl.getElementsByTagName("*");
              
        if (tempList != null && tempList.getLength() > 0) { 
          return (Element) tempList.item(0);
        }            
      }
    } catch (Exception e) {
      logger.log(e);
      result = Test.FAILED;
      return null;
    }
    
    return null;    
  }
  
  private Object loadParam(Element bodyFirstElem, Class paramClass) throws Exception {
    if (bodyFirstElem == null || paramClass == null) {
      logger.log("SOAP Body:\n" + bodyFirstElem);
      logger.log("Parameter Class: " + paramClass);
      return null;
    }    
    
    JAXBContext jaxbCtx = JAXBContext.newInstance(paramClass);
    Unmarshaller unmarsh = jaxbCtx.createUnmarshaller();    
    JAXBElement jaxbElem = unmarsh.unmarshal(bodyFirstElem, paramClass);
    return jaxbElem.getValue();
  }
  
//  public static void main(String[] args) {
//    String binFolder = "d:/erp_gen/gen3_69/bin";
//    ClassLoader loader = new TestClassLoader(new String[]{binFolder});
////    logger.log("Test Class Loader: " + loader);
//    String service = "com.sap.xi.ea_appl.se.global.ECCINTORDRVARBUDMONRULEROWQRService";
//    
//    try {
////      logger.log("Service Class to load: " + service);      
//      Class serviceClass = loader.loadClass(service);
//      Object serviceObj = serviceClass.newInstance();
//      QName portName = (QName) ((Iterator) serviceClass.getMethod("getPorts").invoke(serviceObj)).next(); 
//      Class portClass = loader.loadClass("com.sap.xi.ea_appl.se.global.InternalOrderVarianceBudgetMonitoringRuleEvaluationResultByOwnerQueryResponseIn");           
//      Service serv = (Service) serviceObj;
//      Object port = serv.getPort(portName, portClass);
//      
//      BindingProvider bindingP = (BindingProvider) port;
//      Map<String, Object> reqCtx =  bindingP.getRequestContext();
//          
//      String url = "http://usai1q2o.wdf.sap.corp:50020/sap/bc/srt/xip/sap/ecc_intordrvarbudmonrulerowqr?sap-client=026";
////      String sapClient = testData.getClient();
////      
////      if (sapClient != null && !sapClient.equals("")) {
////        url = url + "?sap-client=" + sapClient;
////      }
//      
////      logger.log("Setting URL: " + url);
//      reqCtx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
//             
////      String user = testData.getUser();
////      String pass = testData.getPassword();
////      logger.log("Setting User: " + user + " and Password: " + pass);
//      reqCtx.put(BindingProvider.USERNAME_PROPERTY, "FINTESTER");
//      reqCtx.put(BindingProvider.PASSWORD_PROPERTY, "SECATT9!");   
//       
//      try {
//        HTTPControlInterface httpCtrlI = HTTPControlFactory.getInterface(port);
//        FileOutputStream reqStream = new FileOutputStream(new File("d:/erp_gen/gen3_69", "request_" + System.currentTimeMillis() + ".log"));
//        FileOutputStream respStream = new FileOutputStream(new File("d:/erp_gen/gen3_69", "response_" + System.currentTimeMillis() + ".log"));
//        httpCtrlI.startLogging(reqStream, respStream);    
//      } catch (Exception e) {
////        logger.log(e);  
////        result = Test.FAILED;
//      } 
//      Method[] portMethods  = portClass.getDeclaredMethods();
//      
//      if (portMethods == null || portMethods.length == 0) {
////        logger.log("No methods found for port: " + portClass);
////        result = Test.FAILED;
////        return result;
//      }
//                 
//      Method portMeth = portMethods[0]; // should be one
////      logger.log("Operation: " + portMeth);  
//      Class[] paramClasses = portMeth.getParameterTypes();
//      
//      if (paramClasses.length == 0) {
////        logger.log("Method " + portMeth.getName() + " has no parameters");
////        result = Test.FAILED;
////        return result; // no such case should be present 
//      }
//      
//      Class paramClass = paramClasses[0]; // should be one
////      logger.log("Parameter type: " + paramClass);
//      Element paramValueEl = null;
//      Object paramObject = null;
//      Object returnObj = null;       
//          
////      for (File payload : testData.getPayloads()) {
////        logger.log("Loading parameter value from " + payload.getName());        
//        paramValueEl = parsePayload(new File("d:/erp_prod/ecc_intordrvarbudmonrulerowqr/0A42485255AF46BAE24DB69410000000.xml"));
//        paramObject = loadParam(paramValueEl, paramClass);
////        logger.log("Parameter value: " + paramObject);
//        
//        if (paramObject == null) {
////          logger.log("Parameter value is null");
////          result = Test.FAILED;
////          continue;
//        }
//        
//        try {
////          logger.log("Invoking " + portMeth + " of " + serviceObj + " with parameter " + paramObject);
//          returnObj = portMeth.invoke(port,  paramObject);
////          logger.log("Return value: " + returnObj + '\n');          
//        } catch (Exception e) {
////          logger.log("WS INVOCATION FAILED:\n");
////          logger.log(e);
////          result = Test.FAILED;
//        }
////      }            
//    } catch (Exception e) {
////      logger.log(e);
////      result = Test.FAILED;
//    }    
//  }
  
}
