package com.sap.engine.lib.xml.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      InQMy
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 *               (commented text is not mine)
 * @version      1.0
 */
public class ReaderInputStream extends InputStream {

  private Reader reader;
  // !!! The Following not used by commented code - !!! uncomment when deside using
  //private EncodingHandler encodingHandler = new EncodingHandler();
  //private Encoding encoding = null;
  //private byte[] convarr = new byte[10];
  private char buf[] = new char[8192];
  private int bufpos = 0;
  private int buflen = 0;
  private int len = -1;
  private int current = 0;

  public ReaderInputStream(Reader reader) {
    this.reader = reader;
    //    LogWriter.getSystemLogWriter().println("ReaderInputStream: ===== Reader is:" + reader);
    //    if (reader instanceof InputStreamReader) {
    //    //      LogWriter.getSystemLogWriter().println("ReaderInputStream: ===== Parent Enc: :" + ((InputStreamReader)reader).getEncoding());
    //    //      LogWriter.getSystemLogWriter().println("ReaderInputStream: ===== Using Encoding:" + encoding);
    //      //encodingHandler.setEncoding(new CharArray();
    //      encoding = encodingHandler.getEncoding(new CharArray(((InputStreamReader)reader).getEncoding()));
    //    } else if (reader instanceof StringReader) {
    //      encoding = encodingHandler.getEncoding(new CharArray("iso-8859-1"));
    //    } else {
    //      encoding = encodingHandler.getEncoding(new CharArray("UTF-8"));
    //    }
  }

  public int read() throws IOException {
    //LogWriter.getSystemLogWriter().println("-------------------------RIS: start of reeed");
    //Thread.dumpStack();
    if (buflen == -1) {
      return -1;
    }

    if (bufpos == buflen) {
      buflen = reader.read(buf);
      bufpos = 0;

      if (buflen <= 0) {
        return -1;
      }
    }

    //    if (current < len) {
    //      return convarr[current++] & 0xFF;
    //    }
    int ch = buf[bufpos++];
    //LogWriter.getSystemLogWriter().println("ReaderInputStream.read(): " + (char) ch + "=" + Integer.toHexString(ch));
    //    if (ch > 0xFF) {
    //      len = encoding.reverseEncode(convarr, ch);
    //    //      LogWriter.getSystemLogWriter().println("len = " + len);
    //      if (len == Encoding.UNSUPPORTED_CHAR) {
    //        ch = -1;
    //      } else {
    //  //        LogWriter.getSystemLogWriter().println("ReaderInputStream.read(): Converted " + (char)convarr[0] + "=" + Integer.toHexString(convarr[0]));
    //        ch = convarr[0];
    //        ch &=0xFF;
    //  //        LogWriter.getSystemLogWriter().println("ReaderInputStream.read(): Converted2 " + (char)ch + "=" + Integer.toHexString(ch));
    //      }
    //      len =0;
    //      current = 0;
    //    } else if ( ch > 0x7F && encoding instanceof UTF8Encoding) {
    //      len = encoding.reverseEncode(convarr, ch);
    //      if (len == Encoding.UNSUPPORTED_CHAR) {
    //        ch = -1;
    //      } else {
    //        current = 0;
    //        return convarr[current++] & 0xFF;
    //    //        LogWriter.getSystemLogWriter().println("ReaderInputStream.read(): Converted " + (char)convarr[0] + "=" + Integer.toHexString(convarr[0]));
    //    //        ch = convarr[0];
    //    //        ch &=0xFF;
    //    //        LogWriter.getSystemLogWriter().println("ReaderInputStream.read(): Converted2 " + (char)ch + "=" + Integer.toHexString(ch));
    //      }
    //    }
    //      
    //    //    LogWriter.getSystemLogWriter().println("-" + (char)ch + "-" + (int)ch +"-");
    //    //    LogWriter.getSystemLogWriter().println("ReaderInputStream.read()---------: " + (char)ch + "=" + Integer.toHexString(ch));
    //    //    return reader.read();
    return ch;
  }

  public long skip(long n) throws IOException {
    throw new IOException("Not Supported");
    //    return reader.skip(n);
  }

  public void close() throws IOException {
    //throw new Error();
    reader.close();
  }

  public synchronized void mark(int readlimit) {
    //    throw new IOException("Not Supported");
    //    try {
    //      reader.mark(readlimit);
    //    } catch (IOException e) {
    //      throw new RuntimeException("IOException: " + e.getMessage());
    //    }
  }

  public synchronized void reset() throws IOException {
    throw new IOException("Not Supported");
    //    reader.reset();
  }

  public boolean markSupported() {
    return false;
    //    return reader.markSupported();
  }

  public int read(char[] dest, int off, int len) throws IOException {
    //    LogWriter.getSystemLogWriter().println("ReaderInputStream.read: off= " + off + ", len=" + len + ", buflen=" + buflen + ", bufpos=" + bufpos);
    int cl = Math.min(buflen - bufpos, len - off);
    if (cl == -1) {
      return -1;
    }
    //    LogWriter.getSystemLogWriter().println("ReaderInputStream.read: cl= " + cl);
    System.arraycopy(buf, bufpos, dest, off, cl);
    bufpos += cl;

    if (cl < len - off) {
      buflen = reader.read(buf);
      bufpos = 0;
      if (buflen == -1) {
        return cl;
      }
    }

    off = off + cl;
    //    LogWriter.getSystemLogWriter().println("ReaderInputStream.read 2: off= " + off + ", len=" + len + ", buflen=" + buflen + ", bufpos=" + bufpos);
    int cl2 = Math.min(buflen - bufpos, len - off);
    //    LogWriter.getSystemLogWriter().println("ReaderInputStream.read: cl2= " + cl2);
    System.arraycopy(buf, bufpos, dest, off, cl2);
    bufpos += cl2;
    return cl + cl2;
  }

}

