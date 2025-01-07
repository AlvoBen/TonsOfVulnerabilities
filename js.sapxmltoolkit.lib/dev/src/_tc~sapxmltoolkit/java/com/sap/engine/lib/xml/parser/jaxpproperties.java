package com.sap.engine.lib.xml.parser;

import java.util.Hashtable;
import java.util.HashSet;


/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2004-3-17
 * Time: 10:54:46
 * To change this template use Options | File Templates.
 */
public class JAXPProperties {

  public static final String PROPERTY_DECLARATION_HANDLER = "http://xml.org/sax/properties/declaration-handler";
  public static final String PROPERTY_LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";
  public static final String PROPERTY_XML_STRING = "http://xml.org/sax/properties/xml-string";
  public static final String PROPERTY_PROXY_HOST = "http://inqmy.org/sax/properties/proxy-host";
  public static final String PROPERTY_PROXY_PORT = "http://inqmy.org/sax/properties/proxy-port";
  public static final String PROPERTY_ALTERNATIVE_DTD = "http://inqmy.org/sax/properties/alternative-dtd";
  public static final String PROPERTY_EXTERNAL_SCHEMA_LOCATION = "http://apache.org/xml/properties/schema/external-schemaLocation";
  public static final String PROPERTY_EXTERNAL_NONAMESPACE_SCHEMA_LOCATION = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";
  public static final String PROPERTY_SCHEMA_OBJECT = "http://apache.org/xml/properties/schema/schemaObject";
  public static final String PROPERTY_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	public static final String PROPERTY_DTD_LANGUAGE = "http://sap.com/xml/jaxp/properties/dtdLanguage";
  public static final String PROPERTY_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
  public static final String PROPERTY_DOCUMENT_CLASS_NAME_STRING = "http://inqmy.org/dom/document-class-name-string";
  public static final String PROPERTY_SAP_SCHEMA_SOURCE = "http://sap.com/xml/schema-source";
  public static final String PROPERTY_INQMY_SCHEMA = "http://www.inqmy.com/schema";
  public static final String PROPERTY_ADDITIONAL_DTD = "http://apache.org/xml/properties/schema/additionalDTD";
  public static final String PROPERTY_ADD_NSMAPPINGS = "http://sap.com/xml/add-ns-mappings";
  public static final String PROPERTY_DEFAULT_ATTRIBUTES = "http://sap.com/xml/DefaultAttributes";
  public static final String PROPERTY_MAX_REFERENCES = "http://sap.com/xml/max-references";
  public static final String PROPERTY_REPLACE_NAMESPACE = "http://sap.com/xml/replace-namespace";

  public static HashSet SUPPORTED = new HashSet();
  public static HashSet RECOGNIZED = new HashSet();
  static {
    SUPPORTED.add(PROPERTY_DECLARATION_HANDLER);
    SUPPORTED.add(PROPERTY_LEXICAL_HANDLER);
    SUPPORTED.add(PROPERTY_XML_STRING);
    SUPPORTED.add(PROPERTY_PROXY_HOST);
    SUPPORTED.add(PROPERTY_PROXY_PORT);
    SUPPORTED.add(PROPERTY_ALTERNATIVE_DTD);
    SUPPORTED.add(PROPERTY_EXTERNAL_SCHEMA_LOCATION);
    SUPPORTED.add(PROPERTY_EXTERNAL_NONAMESPACE_SCHEMA_LOCATION);
    SUPPORTED.add(PROPERTY_SCHEMA_OBJECT);
    SUPPORTED.add(PROPERTY_SCHEMA_LANGUAGE);
    SUPPORTED.add(PROPERTY_SCHEMA_SOURCE);
    SUPPORTED.add(PROPERTY_DOCUMENT_CLASS_NAME_STRING);
    SUPPORTED.add(PROPERTY_SAP_SCHEMA_SOURCE);
    SUPPORTED.add(PROPERTY_INQMY_SCHEMA);
    SUPPORTED.add(PROPERTY_ADDITIONAL_DTD);
    SUPPORTED.add(PROPERTY_DEFAULT_ATTRIBUTES);
    SUPPORTED.add(PROPERTY_ADD_NSMAPPINGS);
    SUPPORTED.add(PROPERTY_DTD_LANGUAGE);
    SUPPORTED.add(PROPERTY_MAX_REFERENCES);
    SUPPORTED.add(PROPERTY_REPLACE_NAMESPACE);
    RECOGNIZED.addAll(SUPPORTED);
  }
}
