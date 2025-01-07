package com.sap.engine.lib.xsl.xpath;

import com.sap.engine.lib.xml.parser.helpers.CharArray;

public interface FunctionLibrary {

  public void init(String pack);


  public XFunction getFunction(CharArray name) throws XPathException;


  public CharArray getName();

}

