package com.sap.engine.lib.jaxp;

import javax.xml.transform.*;
import com.sap.engine.lib.xml.parser.DocHandler;

/**
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      September 2001
 */
public final class DocHandlerResult implements Result {

  private DocHandler docHandler;

  public DocHandlerResult() {

  }

  public DocHandlerResult(DocHandler docHandler) {
    this.docHandler = docHandler;
  }

  public void setDocHandler(DocHandler docHandler) {
    this.docHandler = docHandler;
  }

  public DocHandler getDocHandler() {
    return docHandler;
  }

  public void setSystemId(String s) {
    throw new UnsupportedOperationException();
  }

  public String getSystemId() {
    return null;
  }

}

