package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class XFOperatorNotEquals extends ComparisonOperator {

  public boolean requiresNumbers() {
    return false;
  }

  public String getFunctionName() {
    return "!=";
  }

  public boolean rStrings(CharArray a, CharArray b) {
    return !a.equals(b);
  }

  public boolean rDoubles(double a, double b) {
    return (a != b);
  }

  public boolean rBooleans(boolean a, boolean b) {
    return (a != b);
  }

}

