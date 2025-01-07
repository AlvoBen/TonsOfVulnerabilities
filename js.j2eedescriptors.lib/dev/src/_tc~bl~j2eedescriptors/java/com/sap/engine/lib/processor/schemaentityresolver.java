/*
 * Copyright (c) by SAP AG,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential Information 
 * and shall use it only in accordance with the terms of the 
 * license agreement you entered into with SAP.
 */
package com.sap.engine.lib.processor;

import com.sap.engine.lib.xml.StandardDOMParser;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Hashtable;

public class SchemaEntityResolver implements EntityResolver {

  public static final String URL_PREFIX = "http://java.sun.com/xml/ns/j2ee/";
  public static final String APPCLIENT_J2EE_ENGINE = "appclient-j2ee-engine.xsd";
  public static final String APPLICATION_1_4 = "application_1_4.xsd";
  public static final String APPLICATION_CLIENT_1_4 = "application-client_1_4.xsd";
  public static final String APPLICATION_J2EE_ENGINE = "application-j2ee-engine.xsd";
  public static final String CONNECTOR_1_5 = "connector_1_5.xsd";
  public static final String CONNECTOR_J2EE_ENGINE = "connector-j2ee-engine.xsd";
  public static final String EJB_J2EE_ENGINE = "ejb-j2ee-engine.xsd";
  public static final String EJB_JAR_2_1 = "ejb-jar_2_1.xsd";
  public static final String J2EE_1_4 = "j2ee_1_4.xsd";
  public static final String JSP_2_0 = "jsp_2_0.xsd";
  public static final String PERSISTENT = "persistent.xsd";
  public static final String WEB_APP_2_4 = "web-app_2_4.xsd";
  public static final String WEB_J2EE_ENGINE = "web-j2ee-engine.xsd";
  public static final String WEB_JSPTAGLIBRARY_2_0 = "web-jsptaglibrary_2_0.xsd";
  public static final String XML = "http://www.w3.org/2001/xml.xsd";
  public static final String J2EE_WEB_SERVICES_CLIENT_1_1 = "http://www.ibm.com/webservices/xsd/j2ee_web_services_client_1_1.xsd";
  public static final String PORTLET_APP_1_0 = "http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd";
  public static final String J2EE_WEB_SERVICES_1_1 = "http://www.ibm.com/webservices/xsd/j2ee_web_services_1_1.xsd";
  public static final String J2EE_JAXRPC_MAPPING_1_1 = "http://www.ibm.com/webservices/xsd/j2ee_jaxrpc_mapping_1_1.xsd";

  //Java EE 5.0
  public static final String URL_PREFIX_5 = "http://java.sun.com/xml/ns/javaee/";
  public static final String JAVAEE_5 = "javaee_5.xsd";
  public static final String JSP_2_1 = "jsp_2_1.xsd";
  public static final String WEB_APP_2_5 = "web-app_2_5.xsd";
  public static final String WEB_JSPTAGLIBRARY_2_1 = "web-jsptaglibrary_2_1.xsd";
  public static final String JAVAEE_WEB_SERVICES_CLIENT_1_2 = "javaee_web_services_client_1_2.xsd";
  public static final String APPLICATION_CLIENT_5 = "application-client_5.xsd";
  public static final String APPLICATION_5 = "application_5.xsd";
  public static final String WEB_FACESCONFIG_1_2 = "web-facesconfig_1_2.xsd";
  
  public static final String EJB_J2EE_ENGINE_3_0 = "ejb-j2ee-engine_3_0.xsd";
  public static final String EJB_JAR_3_0 = "ejb-jar_3_0.xsd";
  public static final String JAVAEE_WEB_SERVICES_1_2 = "javaee_web_services_1_2.xsd";

  public static Hashtable schemas = new Hashtable();

  static {
    schemas.put(APPCLIENT_J2EE_ENGINE, "com/sap/engine/lib/schema/appclient-j2ee-engine.xsd");
    schemas.put(APPLICATION_1_4, "com/sap/engine/lib/schema/application_1_4.xsd");
    schemas.put(URL_PREFIX + APPLICATION_1_4, "com/sap/engine/lib/schema/application_1_4.xsd");
    schemas.put(APPLICATION_CLIENT_1_4, "com/sap/engine/lib/schema/application-client_1_4.xsd");
    schemas.put(URL_PREFIX + APPLICATION_CLIENT_1_4, "com/sap/engine/lib/schema/application-client_1_4.xsd");
    schemas.put(APPLICATION_J2EE_ENGINE, "com/sap/engine/lib/schema/application-j2ee-engine.xsd");
    schemas.put(CONNECTOR_1_5, "com/sap/engine/lib/schema/connector_1_5.xsd");
    schemas.put(URL_PREFIX + CONNECTOR_1_5, "com/sap/engine/lib/schema/connector_1_5.xsd");
    schemas.put(CONNECTOR_J2EE_ENGINE, "com/sap/engine/lib/schema/connector-j2ee-engine.xsd");
    schemas.put(EJB_J2EE_ENGINE, "com/sap/engine/lib/schema/ejb-j2ee-engine.xsd");
    schemas.put(EJB_JAR_2_1, "com/sap/engine/lib/schema/ejb-jar_2_1.xsd");
    schemas.put(URL_PREFIX + EJB_JAR_2_1, "com/sap/engine/lib/schema/ejb-jar_2_1.xsd");
    schemas.put(J2EE_1_4, "com/sap/engine/lib/schema/j2ee_1_4.xsd");
    schemas.put(URL_PREFIX + J2EE_1_4, "com/sap/engine/lib/schema/j2ee_1_4.xsd");
    schemas.put(JSP_2_0, "com/sap/engine/lib/schema/jsp_2_0.xsd");
    schemas.put(URL_PREFIX + JSP_2_0, "com/sap/engine/lib/schema/jsp_2_0.xsd");
    schemas.put(PERSISTENT, "com/sap/engine/lib/schema/persistent.xsd");
    schemas.put(WEB_APP_2_4, "com/sap/engine/lib/schema/web-app_2_4.xsd");
    schemas.put(URL_PREFIX + WEB_APP_2_4, "com/sap/engine/lib/schema/web-app_2_4.xsd");
    schemas.put(WEB_J2EE_ENGINE, "com/sap/engine/lib/schema/web-j2ee-engine.xsd");
    schemas.put(WEB_JSPTAGLIBRARY_2_0, "com/sap/engine/lib/schema/web-jsptaglibrary_2_0.xsd");
    schemas.put(URL_PREFIX + WEB_JSPTAGLIBRARY_2_0, "com/sap/engine/lib/schema/web-jsptaglibrary_2_0.xsd");
    schemas.put(XML, "com/sap/engine/lib/schema/xml.xsd");
    schemas.put(J2EE_WEB_SERVICES_CLIENT_1_1, "com/sap/engine/lib/schema/j2ee_web_services_client_1_1.xsd");
    schemas.put(PORTLET_APP_1_0, "com/sap/engine/lib/schema/portlet-app_1_0.xsd");
    schemas.put(J2EE_WEB_SERVICES_1_1, "com/sap/engine/lib/schema/j2ee_web_services_1_1.xsd");
    schemas.put(J2EE_JAXRPC_MAPPING_1_1, "com/sap/engine/lib/schema/j2ee_jaxrpc_mapping_1_1.xsd");

    //Java EE 5.0
    schemas.put(JAVAEE_5, "com/sap/engine/lib/schema/javaee_5.xsd");
    schemas.put(URL_PREFIX_5 + JAVAEE_5, "com/sap/engine/lib/schema/javaee_5.xsd");
    schemas.put(JSP_2_1, "com/sap/engine/lib/schema/jsp_2_1.xsd");
    schemas.put(URL_PREFIX_5 + JSP_2_1, "com/sap/engine/lib/schema/jsp_2_1.xsd");
    schemas.put(WEB_APP_2_5, "com/sap/engine/lib/schema/web-app_2_5.xsd");
    schemas.put(URL_PREFIX_5 + WEB_APP_2_5, "com/sap/engine/lib/schema/web-app_2_5.xsd");
    schemas.put(WEB_JSPTAGLIBRARY_2_1, "com/sap/engine/lib/schema/web-jsptaglibrary_2_1.xsd");
    schemas.put(URL_PREFIX_5 + WEB_JSPTAGLIBRARY_2_1, "com/sap/engine/lib/schema/web-jsptaglibrary_2_1.xsd");
    schemas.put(JAVAEE_WEB_SERVICES_CLIENT_1_2, "com/sap/engine/lib/schema/javaee_web_services_client_1_2.xsd");
    schemas.put(URL_PREFIX_5 + JAVAEE_WEB_SERVICES_CLIENT_1_2, "com/sap/engine/lib/schema/javaee_web_services_client_1_2.xsd");
    schemas.put(APPLICATION_CLIENT_5, "com/sap/engine/lib/schema/application-client_5.xsd");
    schemas.put(URL_PREFIX_5 + APPLICATION_CLIENT_5, "com/sap/engine/lib/schema/application-client_5.xsd");
    schemas.put(APPLICATION_5, "com/sap/engine/lib/schema/application_5.xsd");
    schemas.put(URL_PREFIX_5 + APPLICATION_5, "com/sap/engine/lib/schema/application_5.xsd");
    schemas.put(WEB_FACESCONFIG_1_2, "com/sap/engine/lib/schema/web-facesconfig_1_2.xsd");
    schemas.put(URL_PREFIX_5 + WEB_FACESCONFIG_1_2, "com/sap/engine/lib/schema/web-facesconfig_1_2.xsd");
    
	schemas.put(EJB_J2EE_ENGINE_3_0,
		"com/sap/engine/lib/schema/ejb-j2ee-engine_3_0.xsd");
	schemas.put(URL_PREFIX_5 + EJB_J2EE_ENGINE_3_0,
		"com/sap/engine/lib/schema/ejb-j2ee-engine_3_0.xsd");
	schemas.put(EJB_JAR_3_0,
		"com/sap/engine/lib/schema/ejb-jar_3_0.xsd");
	schemas.put(URL_PREFIX_5 + EJB_JAR_3_0,
		"com/sap/engine/lib/schema/ejb-jar_3_0.xsd");
	schemas.put(JAVAEE_WEB_SERVICES_1_2,
		"com/sap/engine/lib/schema/javaee_web_services_1_2.xsd");    
	schemas.put(URL_PREFIX_5 + JAVAEE_WEB_SERVICES_1_2,
		"com/sap/engine/lib/schema/javaee_web_services_1_2.xsd");    
  }

  public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
    if (systemId.startsWith(StandardDOMParser.SAP_DTD_PREFIX)) {
      systemId = systemId.substring(StandardDOMParser.SAP_DTD_PREFIX.length());
    }
    if (schemas.containsKey(systemId)) {
      InputSource inputSource = new InputSource();
      inputSource.setByteStream(getClass().getClassLoader().getResourceAsStream((String) schemas.get(systemId)));
      inputSource.setSystemId(systemId);
      return (inputSource);
    } else {
      return null;
    }
  }
}
