package com.sap.engine.lib.xml.util;

/**
 * @author  Nick Nickolov, nick_nickolov@abv.bg
 * @version Jan 10, 2002, 9:35:49 AM
 */
public final class NS {

  private NS() {

  }

  // XML namespaces
  public static final String XML = "http://www.w3.org/XML/1998/namespace";
  // Bound to the 'xml:' prefix by default
  public static final String XMLNS = "http://www.w3.org/2000/xmlns/";
  // Bound to the 'xmlns:' prefix by default
  // XML Schema
  public static final String XS = "http://www.w3.org/2001/XMLSchema";
  // The newest XML Schema namespace, usually bound to 'xs:'
  public static final String XSI = "http://www.w3.org/2001/XMLSchema-instance";
  // XML Schema namespace for instances, usually bound to 'xsi:'
  public static final String XSD = "http://www.w3.org/2000/10/XMLSchema";
  // An old XML Schema prefix, usually bound to 'xsd:'
  public static final String XSD_OLD = "http://www.w3.org/1999/XMLSchema";
  // An old XML Schema prefix, usually bound to 'xsd:'
  // XML signature
  public static final String DSIG = "http://www.w3.org/2000/09/xmldsig#";
  // The digital signature namespace, usually bound to 'dsig:'
  // XSL
  public static final String XSL = "http://www.w3.org/1999/XSL/Transform";
  // The namespace for XSL stylesheets, usually bound to 'xsl:'
  // SOAP
  public static final String SOAPENC = "http://schemas.xmlsoap.org/soap/encoding/";
  //= "http://www.w3.org/2001/06/soap-encoding";
  public static final String SOAPENV = "http://schemas.xmlsoap.org/soap/envelope/"; // ???
  // WSDL
  public static final String WSDL = "http://schemas.xmlsoap.org/wsdl/";
  // InQMy namespaces
  public static final String INQMY_TYPE_MAPPING_REGISTRY = "http://www.sap.com/soap/type-mapping-registry";
  //The namespace for SOAPBinding extension elements(soap:operaion, soap:body...)
  public static final String WSDL_SOAP_EXTENSION = "http://schemas.xmlsoap.org/wsdl/soap/";
  //The namespace for MIMEBinding extension elements(mime:multypartRelatet....)
  public static final String WSDL_MIME_EXTENSION = "http://schemas.xmlsoap.org/wsdl/mime/";
}

