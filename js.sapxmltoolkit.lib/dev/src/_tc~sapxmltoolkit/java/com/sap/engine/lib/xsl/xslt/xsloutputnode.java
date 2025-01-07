package com.sap.engine.lib.xsl.xslt;

import org.w3c.dom.Element;

/**
 * @author Vladimir Savtchenko   e-mail: vladimir.savchenko@sap.com
 */
public final class XSLOutputNode {

  private String method = null;
  private String version = null;
  private String encoding = null;
  private String omitXmlDeclaration = null;
  private String standalone = null;
  private String doctypePublic = null;
  private String doctypeSystem = null;
  private String cdataSectionElements = null;
  private String indent = null;
  private String mediaType = null;
  private XSLStylesheet owner = null;

  public XSLOutputNode(XSLStylesheet owner, Element el) throws XSLException {
    this.owner = owner;
    init(el);
  }

  public void init(Element el) throws XSLException {
    method = el.getAttribute("method");
    version = el.getAttribute("version");
    encoding = el.getAttribute("encoding");
    omitXmlDeclaration = el.getAttribute("omit-xml-declaration");
    standalone = el.getAttribute("standalone");
    doctypePublic = el.getAttribute("doctype-public");
    doctypeSystem = el.getAttribute("doctype-system");
    cdataSectionElements = el.getAttribute("cdata-section-elements");
    indent = el.getAttribute("indent");
    mediaType = el.getAttribute("media-type");
  }

  public String getCdataSectionElements() {
    return cdataSectionElements;
  }

  public String getDoctypePublic() {
    return doctypePublic;
  }

  public String getEncoding() {
    return encoding;
  }

  public String getDoctypeSystem() {
    return doctypeSystem;
  }

  public String getIndent() {
    return indent;
  }

  public String getMediaType() {
    return mediaType;
  }

  public String getMethod() {
    return method;
  }

  public String getOmitXmlDeclaration() {
    return omitXmlDeclaration;
  }

  public String getVersion() {
    return version;
  }

  public String getStandalone() {
    return standalone;
  }

}

