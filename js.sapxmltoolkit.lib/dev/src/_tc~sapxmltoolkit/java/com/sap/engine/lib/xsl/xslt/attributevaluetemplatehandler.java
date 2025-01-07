/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.xsl.xslt;

import java.util.Vector;

import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xpath.ETBuilder;
import com.sap.engine.lib.xsl.xpath.ETObject;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.XPathProcessor;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

public class AttributeValueTemplateHandler {

  private Vector tokens = null;
  private Object oneToken = null;
  //private CharArray orgname = new CharArray();

  public AttributeValueTemplateHandler() {

  }

  public AttributeValueTemplateHandler(CharArray name, ETBuilder builder) throws XPathException {
    init(name, builder);
  }

  public void init(CharArray name, ETBuilder builder) throws XPathException {
//    boolean bEvalSection = false;
//    orgname.set(name);
    CharArray buf = null;
    if (tokens != null) {
      tokens.clear();
    }
    
    oneToken = null;
    if (name.length() == 0) {
      return;
    }

    //    LogWriter.getSystemLogWriter().println("AttrValueTemplateGandler.init: name=" + name);
    for (int i = 0; i < name.length(); i++) {
      if (name.charAt(i) == '{' && (i + 1 == name.length() || name.charAt(i + 1) != '{')) {
        if (buf == null) {
          buf = new CharArray(30);
          buf.set(name, 0, i);
        }
        if (buf.length() > 0) {
          if (tokens == null) {
            tokens = new Vector();
            if (oneToken != null) {
              tokens.add(oneToken);
              oneToken = null;
            }
          }
          tokens.add(buf.copy());
          buf.clear();
        }
      } else if (name.charAt(i) == '}' && (i + 1 == name.length() || name.charAt(i + 1) != '}')) {
        Object o = builder.process(buf.copy());
        if (oneToken == null && tokens == null) {
          oneToken = o;
        } else if (tokens == null) {
          tokens = new Vector();
          tokens.add(oneToken);
          tokens.add(o);
          oneToken = null;
        } else {
          tokens.add(o);
        }
        buf.clear();
      } else {
        if (name.charAt(i) == '{' || name.charAt(i) == '}') {
          i++;
        }
        if (buf != null) {
          buf.append(name.charAt(i));
        }
      }
    }
    
    if (buf == null) {
//      if (name != null && name.length() > 0) {
        oneToken = name;
//      }
    } else if (buf.length() > 0) {
      if (oneToken != null) {
        tokens = new Vector();
        tokens.add(oneToken);
        oneToken = null;
      }
      tokens.add(buf.copy());
    }
  }
  
  static int x =0;

  public void processTo(CharArray result, XPathProcessor xpath, XPathContext xcont, VariableContext varContext) throws XSLException {
    if (oneToken != null) {
      if (result == oneToken) {
        return;
      } else if (oneToken instanceof CharArray) {
        result.set((CharArray)oneToken);
        return;
      } else {
        XObject xo = xpath.process((ETObject) oneToken, xcont, varContext);
        result.set(xo.toXString().getValue());
        return;
      }
    } else if (tokens != null && tokens.size() == 1) {
      if (result == tokens.get(0)) {
        return;
      } else if (tokens.get(0) instanceof CharArray){
        result.set((CharArray)tokens.get(0));
        return;
      }
    } 
      result.clear();
      if (tokens == null) {
        return;
        //LogWriter.getSystemLogWriter().println("Attval template:");
      }
      for (int i = 0; i < tokens.size(); i++) {
        if (tokens.get(i) instanceof CharArray) {
          result.append((CharArray) tokens.get(i));
        } else {
          XObject xo = xpath.process((ETObject) tokens.get(i), xcont, varContext);
          result.append(xo.toXString().getValue());
        }
      }
    
    
  }

  public int countTokens() {
    if (tokens != null) {
      return tokens.size();
    } else if (oneToken != null) {
      return 1;
    } else {
      return 0;
    }
  }

}

