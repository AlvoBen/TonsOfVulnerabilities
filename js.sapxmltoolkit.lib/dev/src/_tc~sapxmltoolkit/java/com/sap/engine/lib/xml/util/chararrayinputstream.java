package com.sap.engine.lib.xml.util;

import java.io.*;
import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      InQMy
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      1.0
 */
public class CharArrayInputStream extends InputStream {

  private CharArray data = new CharArray();
  private int idx = 0;
  private int marked = 0;

  public CharArrayInputStream(CharArray input) {
    reuse(input);
  }

  public CharArrayInputStream reuse(CharArray input) {
    data.set(input);
    idx = 0;
    return this;
  }

  public int read() throws IOException {
    //LogWriter.getSystemLogWriter().println("-------------------------RIS: start of reeed");
    //Thread.dumpStack();
    //    int ch = reader.read();
    //    if (ch > 0xFF) {
    //      ch = encodingHandler.reverseEncode(ch);
    //    }
    //    LogWriter.getSystemLogWriter().println("-" + (char)ch + "-" + (int)ch +"-");
    //    LogWriter.getSystemLogWriter().println("ReaderInputStream.read(): " + (char)ch + "=" + Integer.toHexString(ch));
    //    return reader.read();
    if (idx >= data.length()) {
      return -1;
    } else {
      return data.charAt(idx++);
    }
  }

  public long skip(long n) throws IOException {
    idx += n;
    return n;
  }

  public void close() throws IOException {
    //throw new Error();
    //reader.close();
  }

  public synchronized void mark(int readlimit) {
    marked = idx;
  }

  public synchronized void reset() throws IOException {
    idx = marked;
    //reader.reset();
  }

  public boolean markSupported() {
    return true;
  }

}

