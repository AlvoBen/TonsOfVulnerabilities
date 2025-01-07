package com.sap.engine.lib.xsl.xpath;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

/**
 * Represents a <b>/</b> which is not followed by a location step.
 *
 * @see ETItem
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version June 2001
 */
public final class ETDocumentRoot implements ETItem {

  public XObject evaluate(XPathContext context) throws XPathException {
    return context.getXFactCurrent().getXNodeSet(context.dtm, context.dtm.getDocumentElement(context.node));
  }

  public void print(int indent) {
    Symbols.printSpace(indent);
    LogWriter.getSystemLogWriter().println("ETDocumentRoot('/')");
  }

  public boolean match(XPathContext c) throws XPathException {
    return c.node == 0;
  }

}

