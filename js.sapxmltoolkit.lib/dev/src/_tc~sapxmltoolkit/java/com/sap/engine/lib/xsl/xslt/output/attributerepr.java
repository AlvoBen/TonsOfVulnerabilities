package com.sap.engine.lib.xsl.xslt.output;

import com.sap.engine.lib.xml.parser.helpers.CharArray;

public class AttributeRepr {

  public String uri = null;
  public CharArray prefix = null;
  public CharArray localName = null;
  public CharArray qname = null;
  public String type = null;
  public CharArray value = null;

  public AttributeRepr(String uri, CharArray prefix, CharArray localName, CharArray qname, String type, CharArray value) {
    this.uri = uri;
    this.prefix = prefix;
    this.localName = localName;
    this.qname = qname;
    this.type = type;
    this.value = value;
  }

  public AttributeRepr(String uri, CharArray localName, CharArray qname, CharArray value) {
    this.uri = uri;
    this.localName = localName;
    this.qname = qname;
    this.value = value;
    CharArray pref = new CharArray("");
    CharArray lcl = new CharArray("");
    qname.parseNS(pref, lcl);
    prefix = pref;

    if (prefix.length()==0) {
      prefix = null;
    }
  }

}

