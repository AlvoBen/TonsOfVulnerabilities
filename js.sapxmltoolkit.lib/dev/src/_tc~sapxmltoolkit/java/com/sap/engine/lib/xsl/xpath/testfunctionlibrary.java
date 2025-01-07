package com.sap.engine.lib.xsl.xpath;

import com.sap.engine.lib.xsl.xpath.functions.*;

/**
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public final class TestFunctionLibrary extends CoreFunctionLibrary {

  public TestFunctionLibrary() {
    super();
    p(new XFF());
    p(new XFIdentity());
    p(new XFDTM());
    // Moved to the core function library
    //p(new XFDetailedDTM());
    //p(new XFDump());
  }

}

