package com.sap.engine.lib.xsl.xslt;

import com.sap.engine.lib.xml.parser.helpers.CharArray;

public class InternalAttribute {

  public CharArray qname = new CharArray();
  public CharArray value = new CharArray();
  public CharArray prefix = new CharArray();
  public CharArray uri = new CharArray();
  public CharArray localName = new CharArray();

  public InternalAttribute init(CharArray uri, CharArray prefix, CharArray qname, CharArray localname, CharArray value) {
    this.uri.set(uri);
    this.prefix.set(prefix);
    this.qname.set(qname);
    this.localName.set(localname);
    this.value.set(value);
    return this;
  }

}

