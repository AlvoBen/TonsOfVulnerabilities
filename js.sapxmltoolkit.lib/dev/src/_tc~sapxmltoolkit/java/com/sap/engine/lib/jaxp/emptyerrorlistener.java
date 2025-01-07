package com.sap.engine.lib.jaxp;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import com.sap.engine.lib.log.LogWriter;

/**
 * Title:        JAXP
 * Description:  Prints its method calls to System.err and throws back
 *               only fatalErrors.
 *
 * Copyright:    Copyright (c) 2001
 * Company:      InQMy
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      1.0
 */
public final class EmptyErrorListener implements ErrorListener {

  //static ResourceBundle res = ResourceBundle.getBundle("com/inqmy/lib/jaxp/Res");
  public EmptyErrorListener() {

  }

  public void warning(TransformerException e) throws TransformerException {
    LogWriter.getSystemLogWriter().println("Warning: " + e); //$JL-SYS_OUT_ERR$
  }

  public void error(TransformerException e) throws TransformerException {
    LogWriter.getSystemLogWriter().println("Error: " + e); //$JL-SYS_OUT_ERR$
  }

  public void fatalError(TransformerException e) throws TransformerException {
    throw e;
  }

}

