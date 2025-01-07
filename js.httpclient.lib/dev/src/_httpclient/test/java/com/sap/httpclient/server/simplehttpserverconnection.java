package com.sap.httpclient.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;

import com.sap.httpclient.ChunkedOutputStream;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HttpParser;
import com.sap.httpclient.http.StatusLine;

/**
 * A connection to the SimpleHttpServer.
 */
public class SimpleHttpServerConnection {

  private static final String HTTP_ELEMENT_CHARSET = "US-ASCII";

  private Socket socket = null;
  private InputStream in = null;
  private OutputStream out = null;
  private boolean keepAlive = false;

  public SimpleHttpServerConnection(final Socket socket) throws IOException {
    super();
    if (socket == null) {
      throw new IllegalArgumentException("Socket may not be null");
    }
    this.socket = socket;
    this.socket.setSoTimeout(500);
    this.in = socket.getInputStream();
    this.out = socket.getOutputStream();
  }

  public synchronized void close() {
    try {
      if (socket != null) {
        in.close();
        out.close();
        socket.close();
        socket = null;
      }
    } catch (IOException e) {
      // $JL-EXC$
    }
  }

  public synchronized boolean isOpen() {
    return this.socket != null;
  }

  public void setKeepAlive(boolean b) {
    this.keepAlive = b;
  }

  public boolean isKeepAlive() {
    return this.keepAlive;
  }

  public InputStream getInputStream() {
    return this.in;
  }

  public OutputStream getOutputStream() {
    return this.out;
  }

  /**
   * Returns the ResponseWriter used to write the outgoing to the socket.
   *
   * @return This connection's ResponseWriter
	 * @throws java.io.UnsupportedEncodingException if the encoding is not supported
   */
  public ResponseWriter getWriter() throws UnsupportedEncodingException {
    return new ResponseWriter(out);
  }

  public SimpleRequest readRequest() throws IOException {
    try {
      String line;
      do {
        line = HttpParser.readLine(in, HTTP_ELEMENT_CHARSET);
      } while (line != null && line.length() == 0);
      if (line == null) {
        setKeepAlive(false);
        return null;
      }
			return new SimpleRequest(RequestLine.parseLine(line),
							HttpParser.parseHeaders(this.in, HTTP_ELEMENT_CHARSET),
							this.in);
    } catch (IOException e) {
      close();
      throw e;
    }
  }

  public SimpleResponse readResponse() throws IOException {
    try {
      String line;
      do {
        line = HttpParser.readLine(in, HTTP_ELEMENT_CHARSET);
      } while (line != null && line.length() == 0);

      if (line == null) {
        setKeepAlive(false);
        return null;
      }
			return new SimpleResponse(new StatusLine(line),
							HttpParser.parseHeaders(this.in, HTTP_ELEMENT_CHARSET),
							this.in);
    } catch (IOException e) {
      close();
      throw e;
    }
  }

  public void writeRequest(final SimpleRequest request) throws IOException {
    if (request == null) {
      return;
    }
    ResponseWriter writer = new ResponseWriter(this.out, HTTP_ELEMENT_CHARSET);
    writer.println(request.getRequestLine().toString());
    Iterator item = request.getHeaderIterator();
    while (item.hasNext()) {
      Header header = (Header) item.next();
      writer.print(header.toText());
    }
    writer.println();
    writer.flush();
    OutputStream outsream = this.out;
    InputStream content = request.getBody();
    if (content != null) {

      Header transferenc = request.getFirstHeader("Transfer-Encoding");
      if (transferenc != null) {
        request.removeHeaders("Content-Length");
        if (transferenc.getValue().indexOf("chunked") != -1) {
          outsream = new ChunkedOutputStream(outsream);
        }
      }
      byte[] tmp = new byte[4096];
      int i;
      while ((i = content.read(tmp)) >= 0) {
        outsream.write(tmp, 0, i);
      }
      if (outsream instanceof ChunkedOutputStream) {
        ((ChunkedOutputStream) outsream).finish();
      }
    }
    outsream.flush();
  }

  public void writeResponse(final SimpleResponse response) throws IOException {
    if (response == null) {
      return;
    }
    ResponseWriter writer = new ResponseWriter(this.out, HTTP_ELEMENT_CHARSET);
    writer.println(response.getStatusLine());
    Iterator item = response.getHeaderIterator();
    while (item.hasNext()) {
      Header header = (Header) item.next();
      writer.print(header.toText());
    }
    writer.println();
    writer.flush();
    OutputStream outsream = this.out;
    InputStream content = response.getBody();
    if (content != null) {
      Header transferenc = response.getFirstHeader("Transfer-Encoding");
      if (transferenc != null) {
        response.removeHeaders("Content-Length");
        if (transferenc.getValue().indexOf("chunked") != -1) {
          outsream = new ChunkedOutputStream(outsream);
        }
      }
      byte[] tmp = new byte[1024];
      int i;
      while ((i = content.read(tmp)) >= 0) {
        outsream.write(tmp, 0, i);
      }
      if (outsream instanceof ChunkedOutputStream) {
        ((ChunkedOutputStream) outsream).finish();
      }
    }
    outsream.flush();
  }

  public int getSocketTimeout() throws SocketException {
    return this.socket.getSoTimeout();
  }

  public void setSocketTimeout(int timeout) throws SocketException {
    if (this.socket != null) {
      this.socket.setSoTimeout(timeout);
    }
  }

}