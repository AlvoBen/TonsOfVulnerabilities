/*
 * Copyright (c) 2004 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.webservices.server.deploy.preprocess;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sap.engine.lib.jar.JarUtils;
import com.sap.engine.lib.xml.util.DOMSerializer;
import com.sap.engine.services.webservices.webservices630.server.deploy.util.IOUtil;

/**
 * Title: WebServicesAbstractModuleGenerator
 * Description: WebServicesAbstractModuleGenerator 
 * 
 * @author Dimitrina Stoyanova
 * @version
 */

public class WebServicesAbstractSupportHandler {
	
  public static final String NAME = "name";
  protected static final String WEB_APP         = "web-app"; 
  protected static final String DISPLAY_NAME    = "display-name";  
  protected static final String SERVLET         = "servlet";
  protected static final String SERVLET_NAME    = "servlet-name"; 
  protected static final String SERVLET_CLASS   = "servlet-class";   
  protected static final String SERVLET_MAPPING = "servlet-mapping";
  protected static final String URL_PATTTERN    = "url-pattern";  
  protected static final String SESSION_CONFIG  = "session-config"; 
  protected static final String SESSION_TIMEOUT = "session-timeout"; 
  protected static final String LOAD_ON_STARTUP = "load-on-startup";
  
  protected static final int LOAD_ON_STARTUP_VALUE = 0;
  protected static final int SESSION_TIMEOUT_VALUE = 1; 
  protected static final String SOAP_SERVLET_CLASS_NAME = "com.sap.engine.services.webservices.servlet.SoapServlet";
  protected static final String SOAP_SERVLET_SOURCE     = "com/sap/engine/services/webservices/server/deploy/preprocess/SoapServlet.src";
  
  
  protected static final String WEB_INF = "WEB-INF";
  protected static final String WEB_CLASSES_DIR = WEB_INF + "/classes";
  protected static final String WEB_DESCRIPTOR = "web.xml";   

  protected DocumentBuilder documentBuilder;
  protected DOMSerializer domSerializer; 
  protected ClassLoader loader; 
  
  public WebServicesAbstractSupportHandler() {
  
  }
  
  public WebServicesAbstractSupportHandler(DocumentBuilder documentBuilder, DOMSerializer domSerializer, ClassLoader loader) {
    init(documentBuilder, domSerializer, loader);	  
  }
  
  public void init(DocumentBuilder documentBuilder, DOMSerializer domSerializer, ClassLoader loader) {
    this.documentBuilder = documentBuilder; 
    this.domSerializer = domSerializer; 
    this.loader = loader; 
  }
  
  protected Document createWebDescriptorSingleMode(String webAppDisplayName, String servletName, String servletDisplayName) {
    Document webDescriptorDocument = documentBuilder.newDocument(); 
    
    Element webAppElement = webDescriptorDocument.createElement(WebServicesSupportHandler710Impl.WEB_APP);                  
    webAppElement.appendChild(createTextChildElement(webDescriptorDocument, DISPLAY_NAME, servletName));    
    webAppElement.appendChild(createServletElement(webDescriptorDocument, servletName, servletName, SOAP_SERVLET_CLASS_NAME, LOAD_ON_STARTUP_VALUE));
    webAppElement.appendChild(createServletMappingElement(webDescriptorDocument, servletName, "/*"));      
    webAppElement.appendChild(createSessionConfigElement(webDescriptorDocument, SESSION_TIMEOUT_VALUE));
    
    webDescriptorDocument.appendChild(webAppElement);
    
    return webDescriptorDocument;
  }  
  
  protected void generateWar(String workingDir, Document webDescriptor) throws Exception {
	  String  warFilePath = workingDir + ".war";
	  generateWar(workingDir, warFilePath, webDescriptor, false);  
  }
	  
  protected void generateWar(String workingDir, String warFilePath, Document webDescriptor, boolean includeSoapServlet) throws Exception {
    generateWar(workingDir, warFilePath, webDescriptor, domSerializer, loader, includeSoapServlet);  
  }
  
  protected void generateWar(String workingDir, String warFilePath, Document webDescriptor, DOMSerializer domSerializer, ClassLoader loader, boolean includeSoapServlet) throws Exception {
    String webInfDir = workingDir + "/" + WEB_INF; 
    String webClassesDir = workingDir + "/" + WEB_CLASSES_DIR; 
    
    String webDescriptorPath = webInfDir + "/" + WEB_DESCRIPTOR;
    new File(webDescriptorPath).getParentFile().mkdirs();
    FileOutputStream webDescriptorOut = null; 
    try {
      webDescriptorOut = new FileOutputStream(webDescriptorPath); 
      domSerializer.write(webDescriptor, webDescriptorOut);
    } catch(Exception e) {
      //TODO
      e.printStackTrace(); 
      throw e; 
    } finally { 
      if(webDescriptorOut != null) {
        try {
          webDescriptorOut.close();   
        } catch(Exception e) {
          // $JL-EXC$
        }
      }
    }    
    
    if (includeSoapServlet) {
	    String soapServletClassFileName = SOAP_SERVLET_CLASS_NAME + ".class"; 
	    String soapServletClassFilePath = webClassesDir + "/" + soapServletClassFileName;
	    new File(soapServletClassFilePath).getParentFile().mkdirs(); 
	    FileOutputStream soapServletOut = null; 
	    try {
	      soapServletOut = new FileOutputStream(soapServletClassFilePath); 
	      IOUtil.copy(loader.getResourceAsStream(SOAP_SERVLET_SOURCE), soapServletOut);
	    } catch(Exception e) {
	      //TODO
	      e.printStackTrace(); 
	      throw e; 
	    } finally { 
	      if(soapServletOut != null) {
	        try {
	          soapServletOut.close();   
	        } catch(Exception e) {
	          // $JL-EXC$
	        }
	      }
	    }      
    }
    
    new File(warFilePath).getParentFile().mkdirs();
    try {    
      new JarUtils().makeJarFromDir(warFilePath, workingDir);
    } catch(Exception e) {
      //TODO
      e.printStackTrace(); 
      throw e; 	
    }    
  }
  
  public static Element createServletElement(Document webDescriptorDocument, String servletName, String displayName, String servletClass, int loadOnStartUp) {    
    Element servletElement = webDescriptorDocument.createElement(SERVLET); 
     
    servletElement.appendChild(createTextChildElement(webDescriptorDocument, SERVLET_NAME, servletName));
    // servletElement.appendChild(createTextChildElement(webDescriptorDocument, DISPLAY_NAME, displayName)); 
    servletElement.appendChild(createTextChildElement(webDescriptorDocument, SERVLET_CLASS, servletClass));
    servletElement.appendChild(createTextChildElement(webDescriptorDocument, LOAD_ON_STARTUP, (new Integer(loadOnStartUp)).toString()));
    
    return servletElement; 
  } 
  
  public static Element createServletMappingElement(Document webDescriptorDocument, String servletName, String urlPattern) {
    Element servletMappingElement = webDescriptorDocument.createElement(SERVLET_MAPPING);     
    
    servletMappingElement.appendChild(createTextChildElement(webDescriptorDocument, SERVLET_NAME, servletName));
    servletMappingElement.appendChild(createTextChildElement(webDescriptorDocument, URL_PATTTERN, urlPattern));
    
    return servletMappingElement;      
  }
  
  public static Element createSessionConfigElement(Document webDescriptorDocument, int value) {
    Element sessionConfigElement = webDescriptorDocument.createElement(SESSION_CONFIG); 
    sessionConfigElement.appendChild(createTextChildElement(webDescriptorDocument, SESSION_TIMEOUT, new Integer(value).toString()));
    
    return sessionConfigElement;
  }
  
  public static Element createTextChildElement(Document document, String childElementName, String childElementValue) {    
    Element textChildElement = document.createElement(childElementName);       
    textChildElement.appendChild(document.createTextNode(childElementValue));
    return textChildElement;                    
  }
  
}
