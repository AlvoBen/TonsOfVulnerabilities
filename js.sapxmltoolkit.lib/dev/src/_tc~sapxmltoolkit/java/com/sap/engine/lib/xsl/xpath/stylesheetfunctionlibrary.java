package com.sap.engine.lib.xsl.xpath;

import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;
import com.sap.engine.lib.xml.util.FileClassLoader;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xpath.functions.JLBFunction;
import com.sap.engine.lib.xsl.xpath.functions.StylesheetFunction;
import com.sap.engine.lib.xsl.xslt.XSLFunction;

public class StylesheetFunctionLibrary implements FunctionLibrary {

  //public static final CharArray STYLESHEET_LIBRARY = new CharArray("com.sap.engine.lib.xsl.xpath.StylesheetFunctionLibrary");
  private CharArray name = null;
  private Vector stylesheetFunctions = new Vector();

  public StylesheetFunctionLibrary() {
    return;
  }

  public StylesheetFunctionLibrary(String name, Vector stylesheetFunctions) throws ClassNotFoundException {
    this.stylesheetFunctions = stylesheetFunctions;
    this.name = new CharArray(name);
  }

  public XFunction getFunction(CharArray method) throws XPathException {
    return chooseFunction(method.toString());
  }

  public void init(Vector stylesheetFunctions) {
    this.stylesheetFunctions = stylesheetFunctions;
  }

  public void init(String s) {

  }

  public CharArray getName() {
    return name;
  }

  private StylesheetFunction chooseFunction(String name) throws XPathException {
    //    LogWriter.getSystemLogWriter().println("StylesheetFunctionLibrary: " + stylesheetFunctions.size());
    for (int i = 0; i < stylesheetFunctions.size(); i++) {
      XSLFunction next = (XSLFunction) stylesheetFunctions.get(i);
      String qName = next.getName();
      String localName = qName.substring(qName.indexOf(':') + 1);

      //      LogWriter.getSystemLogWriter().println("StylesheetFunctionLibrary local name is: " + localName);
      if (localName.equals(name)) {
        return new StylesheetFunction(next);
      }
    } 

    throw new XPathException("No proper stylesheet function found for name " + name);
  }

}

