package com.sap.ats.tests.webservices.erp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.rpc.Stub;

import report.consumption.es.DynamicMetadataLoad;
import report.consumption.es.ErrorDetails;
import report.consumption.es.TestRun;
import report.consumption.es.TestType;

import com.sap.ats.env.LogEnvironment;
import com.sap.engine.services.webservices.espbase.wsdas.OperationConfig;
import com.sap.engine.services.webservices.espbase.wsdas.WSDAS;
import com.sap.engine.services.webservices.espbase.wsdas.WSDASFactory;
import com.sap.engine.services.webservices.espbase.wsdas.impl.WSDASFactoryImpl;
import com.sap.sdo.api.helper.ErrorHandler;
import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.api.helper.SapXsdHelper;
import com.sap.sdo.api.impl.SapHelperProvider;
import com.sap.sdo.api.types.schema.Schema;
import com.sap.sdo.impl.util.Base64Util;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.impl.HelperProvider;

public class SdoExecutor {
  private static final String ERROR_EXECUTION = "Execution";
  private static final String ERROR_TEST_DATA_DESERIALIZATION = "Test Data Deserialization";

  public static final Map<String, Integer> exceptions = new HashMap<String, Integer>();
  public static final Map<String, Integer> ioExceptions = new HashMap<String, Integer>();
  private LogEnvironment logger;  
  private TestData testData;
  private ClientData clientData;
  private WSDASFactoryImpl wsdasFactory = (WSDASFactoryImpl)WSDASFactory.newInstance();
  private static int counter = 0;

  public SdoExecutor(TestData test, ClientData client) {
    testData = test;
    clientData = client;
  }
  
  public void setLogger(LogEnvironment logEnv) {
    logger = logEnv;    
  }
  
  public TestType execute(int iterations) throws Exception{
    logger.log("\n***************************************************************");
    logger.log("" + ++counter);
    logger.log("Executing test for \n Client data: " + clientData + "\n Test Data: " + testData);
    HelperContext context = HelperProvider.getDefaultContext();
    
    TestType test = (TestType)context.getDataFactory().create(TestType.class);
    test.setStatus(Test_Ulf.STATUS_OK);
    test.setService(testData.getReqPath());
    
    HelperContext wsdlContext = SapHelperProvider.getContext("context.wsdl");
    if (wsdlContext.getTypeHelper().getType("http://schemas.xmlsoap.org/wsdl/" , "tDefinitions") == null) {
        // parse wsdl.xsd
        final String path = "report/wsdl/";
        final String wsdlSchema = "wsdl.xsd";
        final String soapSchema = "soap.xsd";
        final URL wsdlSchemaUrl = getClass().getClassLoader().getResource(path + wsdlSchema);
        final URL soapSchemaUrl = getClass().getClassLoader().getResource(path + soapSchema);
        
        final List<Schema> schemas= new ArrayList<Schema>();
        schemas.add(
            (Schema)wsdlContext.getXMLHelper().load(
                wsdlSchemaUrl.openStream(),
                wsdlSchemaUrl.toString(),
                null).getRootObject());
        schemas.add(
            (Schema)wsdlContext.getXMLHelper().load(
                soapSchemaUrl.openStream(),
                soapSchemaUrl.toString(),
                null).getRootObject());
        
        ((SapXsdHelper)wsdlContext.getXSDHelper()).define(schemas, null);
    }

    try {
      DataObject returnObj = null;
      
      long startTime = 0;
      long duration = 0;
      
      Map options = new HashMap();
      options.put(SapXmlHelper.OPTION_KEY_DEFINE_SCHEMAS, SapXmlHelper.OPTION_VALUE_TRUE);
      
      // XMLDocument wsdlDoc = context.getXMLHelper().load(new FileInputStream(wsdl), wsdl.getAbsolutePath(), options);
      URL wsdlUrl = new URL(clientData.getUrl());
      XMLDocument wsdlDoc = wsdlContext.getXMLHelper().load(
          openAuthorizedStream(wsdlUrl, testData.getUser(), testData.getPassword()),
          wsdlUrl.toString(),
          options);
      DataObject wsdlObj = wsdlDoc.getRootObject();
      
      QName infQName = testData.getInfQName();

      DataObject operation = wsdlObj.getDataObject("portType[name='"+infQName.getLocalPart()+"']/operation");
      if (operation == null) {
          operation = wsdlObj.getDataObject("portType.0/operation[name='"+infQName.getLocalPart()+"']");
          if (operation == null) {
              throw new IllegalArgumentException(
                  "Could not find operation for name='"+infQName.getLocalPart()+
                  "' in '" + wsdlUrl + "'");
          }
          infQName = new QName(infQName.getNamespaceURI(), operation.getContainer().getString("name"));
      }

      String operationName = operation.getString("name");
      
      String inMessage = operation.getString("input/message");
      if (inMessage == null) {
          throw new IllegalArgumentException(
              "Could not find message for name='"+infQName.getLocalPart()+
              "' in '" + wsdlUrl + "'");
      }
      inMessage = inMessage.substring(inMessage.lastIndexOf('#')+1);
      
      String inParam = wsdlObj.getString("message[name='"+inMessage+"']/part.0/element");
      if (inParam == null) {
          throw new IllegalArgumentException(
              "Could not find 'message[name='"+inMessage+"']/part/element' in '" + wsdlUrl + "'");
      }
      inParam = inParam.substring(inParam.lastIndexOf('#')+1);
      
      String outMessage = operation.getString("output/message");
      outMessage = outMessage.substring(outMessage.lastIndexOf('#')+1);
      
      String outParam = wsdlObj.getString("message[name='"+outMessage+"']/part.0/element");
      outParam = outParam.substring(outParam.lastIndexOf('#')+1);
      
      QName portQName = new QName(wsdlObj.getString("service.0/port.0/name"));
      
      SapHelperProvider.removeContext("context.payload");
      HelperContext payloadContext = SapHelperProvider.getContext("context.payload");
      ((SapXsdHelper)payloadContext.getXSDHelper()).define(wsdlObj.getList("types.0/schema"), null);
      
      Throwable lastThrowable = null;
      for (File payload : testData.getPayloads()) {
        String payloadName = payload.getName();
        logger.log("Loading parameter value from " + payloadName);
        TestRun testRun = (TestRun)((DataObject)test).createDataObject("test-run");
        testRun.setStatus(Test_Ulf.STATUS_OK);
        testRun.setTestDataFile(payloadName.substring(0, payloadName.length()-4));
        if (payload.length() == 0) {
            logger.log("Loading parameter failed, " + payloadName + " is empty");
            testRun.setStatus(Test_Ulf.STATUS_ERROR);
            test.setStatus(Test_Ulf.STATUS_ERROR);
            ErrorDetails errorDetail =
                (ErrorDetails)((DataObject)testRun).createDataObject("error-details");
            errorDetail.setKind(ERROR_TEST_DATA_DESERIALIZATION);
            errorDetail.setValue("\nLoading parameter failed, payload is empty\n");
            lastThrowable = new IllegalArgumentException("Loading parameter failed, payload is empty");
            continue;
        }
        XMLDocument doc =
            payloadContext.getXMLHelper().load(
                new FileInputStream(payload),
                payload.getAbsolutePath(),
                Collections.singletonMap(
                    SapXmlHelper.OPTION_KEY_ERROR_HANDLER,
                    new ErrorHandler(){
                        public void handleInvalidValue(RuntimeException pException) {
                            logger.log(pException);
                        }
                        public void handleUnknownProperty(RuntimeException pException) {
                            logger.log(pException);
                        }}));
        DataObject root = doc.getRootObject();
        Object payloadData = null;
        if (root != null) {
            DataObject bodyObj = root.getDataObject("Body");
            Property prop = (Property)bodyObj.getInstanceProperties().get(0);
            if (prop.isMany()) {
                payloadData = bodyObj.getList(prop).get(0);
            } else {
                payloadData = bodyObj.get(prop);
            }
        }
        if (payloadData == null) {
            logger.log("Parameter value is null");
            testRun.setStatus(Test_Ulf.STATUS_ERROR);
            test.setStatus(Test_Ulf.STATUS_ERROR);
            ErrorDetails errorDetail =
                (ErrorDetails)((DataObject)testRun).createDataObject("error-details");
            errorDetail.setKind(ERROR_TEST_DATA_DESERIALIZATION);
            errorDetail.setValue("\nParameter value is null\n");
            lastThrowable = new IllegalArgumentException("Parameter value is null");
            continue;
        }
        
        Map<String, String> wsDasOptions = new HashMap<String,String>(3);
        wsDasOptions.put(Stub.USERNAME_PROPERTY, testData.getUser());
        wsDasOptions.put(Stub.PASSWORD_PROPERTY, testData.getPassword());
        
        WSDAS wsDas = wsdasFactory.createWSDAS(wsdlUrl.toString(), infQName, portQName, payloadContext, wsDasOptions);

        if (iterations > 0) {
          for (int i = 0; i < iterations; i++) {  
            try {
              logger.log("Invoking " + portQName + " of " + infQName + " with parameter " + payloadData);
              startTime = System.currentTimeMillis();
              OperationConfig opCfg = wsDas.getOperationCfg(operationName);
              opCfg.setInputParamValue(inParam, payloadData);
              wsDas.invokeOperation(opCfg);
              returnObj = (DataObject)opCfg.getOutputParamValue(outParam);
              
              duration = System.currentTimeMillis() - startTime;
              logger.log("$$$$$$$$$$$ Invocation took " + duration + " ms");
              logger.log("Return value: " + returnObj + '\n');   
            } catch (Throwable th) {
              logger.log("WS INVOCATION FAILED:\n");
              logger.log(th);
              lastThrowable = th;
              testRun.setStatus(Test_Ulf.STATUS_ERROR);
              test.setStatus(Test_Ulf.STATUS_ERROR);
              ErrorDetails errorDetail =
                  (ErrorDetails)((DataObject)testRun).createDataObject("error-details");
              errorDetail.setKind(ERROR_EXECUTION);
              StringWriter writer = new StringWriter();
              PrintWriter printWriter = new PrintWriter(writer);
              th.printStackTrace(printWriter);
              errorDetail.setValue('\n' + writer.toString() + '\n');
            }
          }
        }
      }
      if (lastThrowable != null) {
          trackException(lastThrowable);
      }
    } catch (Throwable th) {
        trackException(th);
        test.setStatus(Test_Ulf.STATUS_ERROR);
        DynamicMetadataLoad dynamicMetdataLoad =
            (DynamicMetadataLoad)((DataObject)test).createDataObject("dynamic-metadata-load");
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        th.printStackTrace(printWriter);
        dynamicMetdataLoad.setErrorDetails('\n' + writer.toString() + '\n');
    }
    
    return test;
  }
  
  private InputStream openAuthorizedStream(URL url, String user, String passwd)
  throws IOException {
    String s = user + ":" + passwd;
    String base64 = "Basic " + Base64Util.encodeBase64(s.getBytes());
    URLConnection conn = url.openConnection();
    conn.setDoInput(true);
    conn.setRequestProperty("Authorization", base64);
    conn.connect();
    return conn.getInputStream();
  }


  /**
   * @param throwable
   */
  private void trackException(Throwable throwable) {
    String message = throwable.getMessage();
      Integer count = exceptions.get(message);
      if (count != null) {
          exceptions.put(message, ++count);
      } else {
          exceptions.put(message, 1);
      }
      String ioMessage =
          "Connection IO Exception. " +
          "Check nested exception for details. " +
          "(Parameter set failure. " +
          "The runtime was not able to set some result parameter value. " +
          "See nested exception for details.)";
    if (ioMessage.equals(message)) {
        String cause = throwable.getCause().getCause().getMessage();
        Integer ioCount = ioExceptions.get(cause);
        if (ioCount != null) {
            ioExceptions.put(cause, ++ioCount);
        } else {
            ioExceptions.put(cause, 1);
        }
    }
  }
}
