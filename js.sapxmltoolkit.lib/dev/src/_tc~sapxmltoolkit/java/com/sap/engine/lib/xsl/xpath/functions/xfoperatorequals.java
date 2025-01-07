package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XFOperatorEquals extends ComparisonOperator {

  /**
   * State that there should be no conversion to <b>number</b> of the
   * arguments before comparison is performed.
   *
   * @see ComparisonOperator#requiresNumbers()
   */
  public boolean requiresNumbers() {
    return false;
  }

  public String getFunctionName() {
    return "=";
  }

  public boolean rStrings(CharArray a, CharArray b) {
    return a.equals(b);
  }

  public boolean rDoubles(double a, double b) {
    return (a == b);
  }

  public boolean rBooleans(boolean a, boolean b) {
    return (a == b);
  }

}

