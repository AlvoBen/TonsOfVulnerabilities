package com.sap.httpclient.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.Socket;

import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HttpVersion;
import com.sap.httpclient.http.HttpStatus;

/**
 * This request handler can handle the CONNECT method. It does nothing for any
 * other HTTP methods.
 */
public class TransparentProxyRequestHandler implements HttpRequestHandler {

  public boolean processRequest(final SimpleHttpServerConnection conn,
                                final SimpleRequest request) throws IOException {

    RequestLine line = request.getRequestLine();
    HttpVersion ver = line.getHttpVersion();
    String method = line.getMethod();
    if (!"CONNECT".equalsIgnoreCase(method)) {
      return false;
    }
    Socket targetSocket;
    try {
      targetSocket = connect(line.getUri());
    } catch (IOException e) {
      SimpleResponse response = new SimpleResponse();
      response.setStatusLine(ver, HttpStatus.SC_NOT_FOUND);
      response.setHeader(new Header("Server", "test proxy"));
      response.setBodyString("Cannot connect to " + line.getUri());
      conn.writeResponse(response);
      return true;
    }
    SimpleResponse response = new SimpleResponse();
    response.setHeader(new Header("Server", "test proxy"));
    response.setStatusLine(ver, HttpStatus.SC_OK, "Connection established");
    conn.writeResponse(response);
    SimpleHttpServerConnection target = new SimpleHttpServerConnection(targetSocket);
    pump(conn, target);
    return true;
  }

  private void pump(final SimpleHttpServerConnection source, final SimpleHttpServerConnection target)
          throws IOException {

    source.setSocketTimeout(100);
    target.setSocketTimeout(100);
    InputStream sourceIn = source.getInputStream();
    OutputStream sourceOut = source.getOutputStream();
    InputStream targetIn = target.getInputStream();
    OutputStream targetOut = target.getOutputStream();
    byte[] tmp = new byte[1024];
    int l;
    for (; ;) {
      if (!source.isOpen() || !target.isOpen()) {
        break;
      }
      try {
        l = sourceIn.read(tmp);
        if (l == -1) {
          break;
        }
        targetOut.write(tmp, 0, l);
      } catch (InterruptedIOException ignore) {
        if (Thread.interrupted()) {
          break;
        }
      }
      try {
        l = targetIn.read(tmp);
        if (l == -1) {
          break;
        }
        sourceOut.write(tmp, 0, l);
      } catch (InterruptedIOException ignore) {
        if (Thread.interrupted()) {
          break;
        }
      }
    }
  }

  private static Socket connect(final String host) throws IOException {
    String hostname;
    int port;
    int i = host.indexOf(':');
    if (i != -1) {
      hostname = host.substring(0, i);
      try {
        port = Integer.parseInt(host.substring(i + 1));
      } catch (NumberFormatException ex) {
        throw new IOException("Invalid host address: " + host);
      }
    } else {
      hostname = host;
      port = 80;
    }
    return new Socket(hostname, port);
  }

}