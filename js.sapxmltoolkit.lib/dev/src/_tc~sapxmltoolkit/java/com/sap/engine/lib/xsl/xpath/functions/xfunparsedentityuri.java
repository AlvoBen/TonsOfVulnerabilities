﻿package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xsl.xpath.XFunction;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XFUnparsedEntityUri implements XFunction {

  public boolean confirmArgumentTypes(XObject[] a) {
    return (a.length == 1);
  }

  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
    String name = a[0].toXString().getValue().getString();
    return context.getXFactCurrent().getXString("");
  }

  public String getFunctionName() {
    return "unparsed-entity-uri";
  }

}

