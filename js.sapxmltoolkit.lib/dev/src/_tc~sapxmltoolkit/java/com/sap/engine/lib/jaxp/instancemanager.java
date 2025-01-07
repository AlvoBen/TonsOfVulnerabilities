package com.sap.engine.lib.jaxp;

import com.sap.engine.lib.xsl.xslt.*;
//import com.sap.engine.lib.xsl.xslt.pool.ObjectPool;
import com.sap.engine.lib.xsl.xslt.output.*;

/**
 * Title:        xml2000
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      InQMy
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      May 2001
 */
final public class InstanceManager {

  private static int n = -1;

  //  private static XSLStylesheet[] sheets = new XSLStylesheet[100];
  //private static Thread[]        threads = new Thread[100];
  //private static DocHandlerSerializer[]   outputs = new DocHandlerSerializer[100];
  //private static ObjectPool sheetPool = new ObjectPool(XSLStylesheet.class, 10, 10);
  private InstanceManager() {

  }

  static public XSLStylesheet getXSLStylesheet() throws XSLException {
    //    Thread t = Thread.currentThread();
    //    for (int i = 0; i < n; i++) {
    //      if (t == threads[i]) {
    //        if (sheets[i] == null) {
    //          sheets[i] = new XSLStylesheet();
    //        }
    //        return sheets[i];
    //      }
    //    }
    //    threads[n] = t;
    //    LogWriter.getSystemLogWriter().println("InstanceManager.getXSLStylesheet: n=" + n);
    //    if (n == -1) {
    //      return new XSLStylesheet();
    //    } else {
    //      XSLStylesheet s = sheets[n];
    //      sheets[n] = null;
    //      n--;
    //      return s;
    //    }
    //XSLStylesheet s = (XSLStylesheet)sheetPool.getObject();
    return new XSLStylesheet();
    //    XSLStylesheet s = new XSLStylesheet();
    //n++;
    //s.setPool(sheet);
    //    sheets[n] = s;
    //    n++;
    //    return s;
  }

  static DocHandlerSerializer getDocHandlerSerializer() {
    //    Thread t = Thread.currentThread();
    //    for (int i = 0; i < n; i++) {
    //      if (t == threads[i]) {
    //        if (outputs[i] == null) {
    //          outputs[i] = new DocHandlerSerializer();
    //        }
    //        return outputs[i];
    //      }
    //    }
    //    threads[n] = t;
    return new DocHandlerSerializer();
  }

  static public void releaseXSLStylesheet(XSLStylesheet s) {
    //    LogWriter.getSystemLogWriter().println("InstanceManager.putting Sheet   : n=" + n);
    //    n++;
    //    sheets[n] = s;
    //n--;
    //sheetPool.releaseObject(s);
  }

}

