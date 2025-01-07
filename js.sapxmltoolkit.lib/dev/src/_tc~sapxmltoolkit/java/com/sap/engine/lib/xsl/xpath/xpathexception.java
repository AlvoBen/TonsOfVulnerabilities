package com.sap.engine.lib.xsl.xpath;

import com.sap.engine.lib.xsl.xslt.XSLException;

/**
 * An exception caused by incorrect queries to the XPath engine,
 * or any other unexpected circumstances in the classes of this package.
 * Typically thrown in <tt>ETBuilder</tt>.
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version July 2001
 */
public class XPathException extends XSLException {

  /**
   * A field that specifies the position in the query where the error occurred.
   * If position is equal to its default value, -1, then it is not
   * applicable to the error, i.e. the error occurred while evaluating the
   * query, not while parsing it.
   */
  private int position = -1;

  /**
   * Got to supply a message.
   */
  public XPathException(String s) {
    super(s);
  }

  public XPathException(String s, Throwable thr) {
    super(s, thr);
  }

  public XPathException(Throwable thr) {
    super(thr);
  }

  /**
   * Exception specifying the position of the error.
   */
  public XPathException(String s, int p) {
    super(s + ((p < 0) ? "" : (" (At position " + p + ")")));
  }

}

