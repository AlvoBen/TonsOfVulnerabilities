/**
 * Title:        xml2000
 * Description:  This class is used for holding Attribute information
 *               it is very simple and is done just like C structs
 *
 * Copyright:    Copyright (c) 2001
 * Company:      InQMy
 * @author       Chavdar Baykov, Chavdarb@abv.bg
 * @version      May 2001
 */
package com.sap.engine.lib.xml.parser.handlers;

//import java.util.*;
//import com.sap.engine.lib.xml.parser.helpers.CharArray;
//import com.sap.engine.lib.xml.parser.helpers.Entity;
//import com.sap.engine.lib.xml.parser.helpers.Reference;
public class SimpleAttr implements java.io.Serializable {

  public String uri;
  public String localName;
  public String value;

  public SimpleAttr(String localName, String value, String uri) {
    this.localName = localName;
    this.value = value;
    this.uri = uri;
  }

  public SimpleAttr() {
    uri = "";
    localName = "";
    value = "";
  }

  public static String getAttribute(String localName, SimpleAttr[] attr, int attrCount) {
    for (int i = 0; i < attrCount; i++) {
      if (attr[i].localName.equals(localName)) {
        return attr[i].value;
      }
    } 

    return null;
  }

  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append("attributeName: {" + this.localName + "} attributeNS: {" + this.uri + "} attributeValue: {" + this.value + "}\n");
    return result.toString();
  }

}

