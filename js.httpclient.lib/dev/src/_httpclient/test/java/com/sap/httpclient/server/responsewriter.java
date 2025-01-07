package com.sap.httpclient.server;

import java.io.BufferedWriter;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * Provides a hybrid Writer/OutputStream for sending HTTP response data
 */
public class ResponseWriter extends FilterWriter {
  public static final String CRLF = "\r\n";
  public static final String ISO_8859_1 = "ISO-8859-1";
  private OutputStream outStream = null;
  private String encoding = null;

  public ResponseWriter(final OutputStream outStream) throws UnsupportedEncodingException {
    this(outStream, ISO_8859_1);
  }

  public ResponseWriter(final OutputStream outStream,
                        final String encoding) throws UnsupportedEncodingException {
    super(new BufferedWriter(new OutputStreamWriter(outStream, encoding)));
    this.outStream = outStream;
    this.encoding = encoding;
  }

  public String getEncoding() {
    return encoding;
  }

  public void close() throws IOException {
    if (outStream != null) {
      super.close();
      outStream = null;
    }
  }

  public void flush() throws IOException {
    if (outStream != null) {
      super.flush();
      outStream.flush();
    }
  }

  public void write(byte b) throws IOException {
    super.flush();
    outStream.write((int) b);
  }

  public void write(byte[] b) throws IOException {
    super.flush();
    outStream.write(b);
  }

  public void write(byte[] b, int off, int len) throws IOException {
    super.flush();
    outStream.write(b, off, len);
  }

  public void print(String s) throws IOException {
    if (s == null) {
      s = "null";
    }
    write(s);
  }

  public void print(int i) throws IOException {
    write(Integer.toString(i));
  }

  public void println(int i) throws IOException {
    write(Integer.toString(i));
    write(CRLF);
  }

  public void println(String s) throws IOException {
    print(s);
    write(CRLF);
  }

  public void println() throws IOException {
    write(CRLF);
  }

}