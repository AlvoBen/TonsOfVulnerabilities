package com.sap.engine.lib.xml;

import java.io.IOException;
import java.util.Hashtable;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class StandardEntityResolver implements EntityResolver {

  public static Hashtable stddtd = new Hashtable();

  static {
    stddtd.put("http://java.sun.com/dtd/application-client_1_3.dtd", "com/sap/engine/lib/xml/dtd/application-client_1_3.dtd");
    stddtd.put("http://java.sun.com/dtd/application_1_3.dtd", "com/sap/engine/lib/xml/dtd/application_1_3.dtd");
    stddtd.put("http://java.sun.com/dtd/connector_1_0.dtd", "com/sap/engine/lib/xml/dtd/connector_1_0.dtd");
    stddtd.put("http://java.sun.com/dtd/ejb-jar_2_0.dtd", "com/sap/engine/lib/xml/dtd/ejb-jar_2_0.dtd");
    stddtd.put("http://java.sun.com/dtd/jspxml.dtd", "com/sap/engine/lib/xml/dtd/jspxml.dtd");
    stddtd.put("http://java.sun.com/dtd/jspxml.xsd", "com/sap/engine/lib/xml/dtd/jspxml.xsd");
    stddtd.put("http://java.sun.com/dtd/logger.dtd", "com/sap/engine/lib/xml/dtd/logger.dtd");
    stddtd.put("http://java.sun.com/dtd/preferences.dtd", "com/sap/engine/lib/xml/dtd/preferences.dtd");
    stddtd.put("http://java.sun.com/dtd/web-app_2_3.dtd", "com/sap/engine/lib/xml/dtd/web-app_2_3.dtd");
    stddtd.put("http://java.sun.com/dtd/web-jsptaglibrary_1_2.dtd", "com/sap/engine/lib/xml/dtd/web-jsptaglibrary_1_2.dtd");
    stddtd.put("http://java.sun.com/j2ee/dtds/application-client_1_2.dtd", "com/sap/engine/lib/xml/dtd/application-client_1_2.dtd");
    stddtd.put("http://java.sun.com/j2ee/dtds/application_1_2.dtd", "com/sap/engine/lib/xml/dtd/application_1_2.dtd");
    stddtd.put("http://java.sun.com/j2ee/dtds/ejb-jar_1_1.dtd", "com/sap/engine/lib/xml/dtd/ejb-jar_1_1.dtd");
    stddtd.put("http://java.sun.com/j2ee/dtds/web-app_2_2.dtd", "com/sap/engine/lib/xml/dtd/web-app_2_2.dtd");
    stddtd.put("http://java.sun.com/j2ee/dtds/web-app_2.2.dtd", "com/sap/engine/lib/xml/dtd/web-app_2_2.dtd");
    stddtd.put("http://java.sun.com/j2ee/dtds/web-jsptaglibrary_1_1.dtd", "com/sap/engine/lib/xml/dtd/web-jsptaglibrary_1_1.dtd");
    stddtd.put("http://java.sun.com/j2ee/dtds/jdo_1_0.dtd", "http://java.sun.com/dtd/jdo_1_0.dtd");
    //added for compatibility with non conformant dtd system id's
    stddtd.put("http://java.sun.com/j2ee/dtd/web-app_2_2.dtd", "com/sap/engine/lib/xml/dtd/web-app_2_2.dtd");
    stddtd.put("http://java.sun.com/dtd/web-app_2_2.dtd", "com/sap/engine/lib/xml/dtd/web-app_2_2.dtd");
    stddtd.put("http://java.sun.com/j2ee/dtds/web-app_2_3.dtd", "com/sap/engine/lib/xml/dtd/web-app_2_3.dtd");
    stddtd.put("web-app_2_3.dtd", "com/sap/engine/lib/xml/dtd/web-app_2_3.dtd");
    stddtd.put("http://java.sun.com/j2ee/dtds/web-jsptaglibrary_1_2.dtd", "com/sap/engine/lib/xml/dtd/web-jsptaglibrary_1_2.dtd");
    stddtd.put("http://java.sun.com/j2ee/dtd/web-jsptaglibrary_1_2.dtd", "com/sap/engine/lib/xml/dtd/web-jsptaglibrary_1_2.dtd");
    stddtd.put("ejb-j2ee-engine.dtd", "com/sap/engine/lib/xml/dtd/ejb-j2ee-engine.dtd");
    stddtd.put("persistent.dtd", "com/sap/engine/lib/xml/dtd/persistent.dtd");
    stddtd.put("data-sources.dtd", "com/sap/engine/lib/xml/dtd/data-sources.dtd");
    stddtd.put("data-source-aliases.dtd", "com/sap/engine/lib/xml/dtd/data-source-aliases.dtd");
    stddtd.put("db-init.dtd", "com/sap/engine/lib/xml/dtd/db-init.dtd");
    stddtd.put("appclient-j2ee-engine.dtd", "com/sap/engine/lib/xml/dtd/appclient-j2ee-engine.dtd");
    stddtd.put("application-j2ee-engine.dtd", "com/sap/engine/lib/xml/dtd/application-j2ee-engine.dtd");
    stddtd.put("connector-j2ee-engine.dtd", "com/sap/engine/lib/xml/dtd/connector-j2ee-engine.dtd");
    stddtd.put("interface.provider.dtd", "com/sap/engine/lib/xml/dtd/interface.provider.dtd");
    stddtd.put("jms-destinations.dtd", "com/sap/engine/lib/xml/dtd/jms-destinations.dtd");
    stddtd.put("jms-factories.dtd", "com/sap/engine/lib/xml/dtd/jms-factories.dtd");
    stddtd.put("library.provider.dtd", "com/sap/engine/lib/xml/dtd/library.provider.dtd");
    stddtd.put("log-configuration.dtd", "com/sap/engine/lib/xml/dtd/log-configuration.dtd");
    stddtd.put("service.provider.dtd", "com/sap/engine/lib/xml/dtd/service.provider.dtd");
    stddtd.put("web-j2ee-engine.dtd", "com/sap/engine/lib/xml/dtd/web-j2ee-engine.dtd");
    //added for backward compatibility - in case of opening an old application
    stddtd.put("application-client-additional.dtd", "com/sap/engine/lib/xml/dtd/application-client-additional.dtd");
    // added for j2ee 1.4 support
    stddtd.put("appclient-j2ee-engine.xsd", "com/sap/engine/lib/xml/dtd/appclient-j2ee-engine.xsd");
    stddtd.put("application_1_4.xsd", "com/sap/engine/lib/xml/dtd/application_1_4.xsd");
   	stddtd.put("application-client_1_4.xsd", "com/sap/engine/lib/xml/dtd/application-client_1_4.xsd");
    stddtd.put("application-j2ee-engine.xsd", "com/sap/engine/lib/xml/dtd/application-j2ee-engine.xsd");
    stddtd.put("connector_1_5.xsd", "com/sap/engine/lib/xml/dtd/connector_1_5.xsd");
    stddtd.put("connector-j2ee-engine.xsd", "com/sap/engine/lib/xml/dtd/connector-j2ee-engine.xsd");
   //	stddtd.put("ejb-j2ee-engine.xsd", "com/sap/engine/lib/xml/dtd/ejb-j2ee-engine.xsd");
   // stddtd.put("ejb-jar_2_1.xsd", "com/sap/engine/lib/xml/dtd/ejb-jar_2_1.xsd");
    stddtd.put("j2ee_1_4.xsd", "com/sap/engine/lib/xml/dtd/j2ee_1_4.xsd");
   	stddtd.put("jsp_2_0.xsd", "com/sap/engine/lib/xml/dtd/jsp_2_0.xsd");
   // stddtd.put("persistent.xsd", "com/sap/engine/lib/xml/dtd/persistent.xsd");
    stddtd.put("web-app_2_4.xsd", "com/sap/engine/lib/xml/dtd/web-app_2_4.xsd");
    stddtd.put("web-j2ee-engine.xsd", "com/sap/engine/lib/xml/dtd/web-j2ee-engine.xsd");
   	stddtd.put("http://www.w3.org/2001/xml.xsd", "com/sap/engine/lib/xml/dtd/xml.xsd");
    stddtd.put("http://www.ibm.com/webservices/xsd/j2ee_web_services_client_1_1.xsd", "com/sap/engine/lib/xml/dtd/j2ee_web_services_client_1_1.xsd");
    stddtd.put("web-jsptaglibrary_2_0.xsd", "com/sap/engine/lib/xml/dtd/web-jsptaglibrary_2_0.xsd");
  }

  public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
    if (systemId.startsWith(StandardDOMParser.SAP_DTD_PREFIX)) {
      systemId = systemId.substring(StandardDOMParser.SAP_DTD_PREFIX.length());
    }
    if (stddtd.containsKey(systemId)) {
      return new InputSource(getClass().getClassLoader().getResourceAsStream((String) stddtd.get(systemId)));
    } else {
      return null;
    }
  }

  public static InputSource[] getJ2EESchemas() {
    String[] schemaSID = new String[] {
      "appclient-j2ee-engine.xsd",
      "application_1_4.xsd",
      "application-client_1_4.xsd",
      "application-j2ee-engine.xsd",
      "connector_1_5.xsd",
      "connector-j2ee-engine.xsd",
//      "ejb-j2ee-engine.xsd",
//      "ejb-jar_2_1.xsd",
      "j2ee_1_4.xsd",
      "http://www.ibm.com/webservices/xsd/j2ee_web_services_client_1_1.xsd",
      "jsp_2_0.xsd",
//      "persistent.xsd",
      "web-app_2_4.xsd",
      "web-j2ee-engine.xsd",
      "http://www.w3.org/2001/xml.xsd",
      "web-jsptaglibrary_2_0.xsd"
    };
    InputSource[] result = new InputSource[schemaSID.length];
    for (int i=0; i<result.length; ++i) {
      result[i] = new InputSource(StandardEntityResolver.class.getClassLoader().getResourceAsStream((String) stddtd.get(schemaSID[i])));
      result[i].setSystemId(schemaSID[i]);
    }
    return result;
  }

}

