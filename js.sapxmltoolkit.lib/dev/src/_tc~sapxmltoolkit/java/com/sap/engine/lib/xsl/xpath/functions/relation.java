package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xsl.xpath.XPathException;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 *
 * @deprecated
 */
public interface Relation {

  boolean R(int x) throws XPathException;


  boolean requiresNumbers() throws XPathException;

}

