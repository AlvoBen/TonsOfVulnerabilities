package com.sap.engine.lib.xsl.xpath;

import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

/**
 * Implementing classes represent XPath functions.
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version June 2001
 */
public interface XFunction {

  String getFunctionName();


  boolean confirmArgumentTypes(XObject[] a);


  XObject execute(XObject[] a, XPathContext context) throws XPathException;

}

