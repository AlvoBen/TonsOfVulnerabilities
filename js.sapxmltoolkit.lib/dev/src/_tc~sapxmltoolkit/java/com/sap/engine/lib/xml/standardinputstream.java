package com.sap.engine.lib.xml;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StandardInputStream extends BufferedInputStream {
  //private BufferedInputStream bin = null;

  public StandardInputStream(InputStream in) throws IOException {
    super(in);
    mark(3);
    if (read() == 0xEF && read() == 0xBB && read() == 0xBF) {
      reset();
      read();
      read();
      read();
    } else{
      reset();
    }
  }
}
