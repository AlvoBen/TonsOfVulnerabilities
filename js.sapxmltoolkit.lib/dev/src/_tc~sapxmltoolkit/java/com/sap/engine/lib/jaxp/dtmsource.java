package com.sap.engine.lib.jaxp;

import javax.xml.transform.*;
import com.sap.engine.lib.xsl.xpath.DTM;

/**
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      September 2001
 */
public final class DTMSource implements Source {

  private DTM dtm;

  public DTMSource() {

  }

  public DTMSource(DTM dtm) {
    setDTM(dtm);
  }

  public void setDTM(DTM dtm) {
    this.dtm = dtm;
  }

  public DTM getDTM() {
    return dtm;
  }

  public String getSystemId() {
    return null;
  }

  public void setSystemId(String s) {
    throw new UnsupportedOperationException();
  }

}

