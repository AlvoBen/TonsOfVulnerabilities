package com.sap.engine.lib.xml.parser.helpers;

import java.util.Vector;

/**
 * Class description -
 *
 * @author Vladimir Savtchenko
 * @version 1.00
 */
public final class Attribute {

  //public String rawName = null;
  //public String uri = null;
  //public String localName = null;
  //public String prefix = null;
  //public Vector vAttValue = null;
  public final static String defType = "CDATA";
  public final static String sEmpty = "";
  public String sType = defType;
  public CharArray crRawName = new CharArray(20);
  public CharArray crValue = new CharArray(20);
  public CharArray crUri = new CharArray(20);
  public CharArray crLocalName = new CharArray(20);
  public CharArray crPrefix = new CharArray(20);
  public boolean isSpecified = false;
  protected boolean bDefCheck = false;

  public String getQNameStr() {
    return crRawName.getString();
  }

  public String getValueStr() {
    return crValue.getString();
  }

//  public String getUriStr() {
//    return crUri.getString();
//  }

//  public String getLocalNameStr() {
//    return crLocalName.getString();
//  }

//  public String getPrefixStr() {
//    return crPrefix.getString();
//  }

//  public String getTypeStr() {
//    return sType;
//  }

  public Attribute() {

  }

  //  public Attribute(String name, Vector vAttValue) {
  //    this.rawName = name;
  //    prefix = sEmpty;
  //    localName = name;
  //    uri = sEmpty;
  //    this.vAttValue = vAttValue;
  //    type = defType;
  //  }
  //  
  //  public Attribute(String rawName, String prefix, String localname, String uri, Vector vAttValue) {
  //    this.rawName = rawName;
  //    this.vAttValue = vAttValue;
  //    this.uri = uri;
  //    this.prefix = prefix;
  //    this.localName = localname;
  //    type = defType;
  //  }
  //  
  //  public Attribute(String rawName, String uri, Vector vAttValue) {
  //    this.rawName = rawName;
  //    this.vAttValue = vAttValue;
  //    this.uri = uri;
  //    int a = rawName.indexOf(":");
  //    if (a==-1) { 
  //      prefix = sEmpty; localName = rawName; 
  //    } else {
  //      prefix = rawName.substring(0, a);
  //      localName = rawName.substring(a+1);
  //    }
  //    type = defType;
  //  }
  //  public Attribute reuse(String rawName, String prefix, String localname, String uri, Vector vAttValue) {
  //    this.rawName = rawName;
  //    this.vAttValue = vAttValue;
  //    this.uri = uri;
  //    this.prefix = prefix;
  //    this.localName = localname;
  //    type = defType;
  //    return this;
  //  }
  //  public Attribute reuse(String rawName, String uri, Vector vAttValue) {
  //    this.rawName = rawName;
  //    this.vAttValue = vAttValue;
  //    this.uri = uri;
  //    int a = rawName.indexOf(":");
  //    if (a==-1) { 
  //      prefix = ""; localName = rawName; 
  //    } else {
  //      prefix = rawName.substring(0, a);
  //      localName = rawName.substring(a+1);
  //    }
  //    type = defType;
  //    return this;
  //  }
  //  public Attribute reuse(String name, Vector vAttValue) {
  //    this.rawName = name;
  //    prefix = sEmpty;
  //    localName = name;
  //    uri = sEmpty;
  //    this.vAttValue = vAttValue;
  //    type = defType;
  //    return this;
  //  }
  //  
  public Attribute reuse(CharArray crRawName, CharArray crValue, boolean isSpecified) {
    this.crRawName.set(crRawName);
    this.crValue.set(crValue);
    this.crPrefix.clear();
    this.crLocalName.clear();
    this.crUri.clear();
    this.sType = defType;
    this.isSpecified = isSpecified;
    return this;
  }

  public Attribute reuse(CharArray crRawName, CharArray crPrefix, CharArray crLocalName, CharArray uri, CharArray crValue, boolean isSpecified) {
    this.crRawName.set(crRawName);
    this.crPrefix.set(crPrefix);
    this.crLocalName.set(crLocalName);
    this.crUri.set(uri);
    this.crValue.set(crValue);
    this.sType = defType;
    this.isSpecified = isSpecified;
    return this;
  }

//  public Attribute reuse(String crRawName, String crPrefix, String crLocalName, String uri, String crValue, boolean isSpecified) {
//    this.crRawName.set(crRawName);
//    this.crPrefix.set(crPrefix);
//    this.crLocalName.set(crLocalName);
//    this.crUri.set(uri);
//    this.crValue.set(crValue);
//    this.sType = defType;
//    this.isSpecified = isSpecified;
//    return this;
//  }

//  public static String resolve(Vector v) {
//    String a = "";
//    for (int i = 0; i < v.size(); i++) {
//      a += ((Reference) v.get(i)).resolve(); 
//    }
//    return a;
//  }

//  public String resolve() {
//    return getValueStr();
//  }

//  private String getValueString() {
//    String a = "";
//    //for (int i=0; i<vAttValue.size(); i++) a += ((Reference)vAttValue.get(i)).resolve();
//    return a;
//  }

  public void setDefCheck(boolean v) {
    bDefCheck = v;
  }

  public boolean getDefCheck() {
    return bDefCheck;
  }

  public void setUri(CharArray u) {
    crUri.set(u);
  }

}

