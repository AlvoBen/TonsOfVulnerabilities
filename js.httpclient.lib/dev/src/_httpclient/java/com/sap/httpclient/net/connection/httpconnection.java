/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.httpclient.net.connection;

import com.sap.httpclient.http.HttpParser;
import com.sap.httpclient.net.Protocol;
import com.sap.httpclient.net.ProtocolSocketFactory;
import com.sap.httpclient.net.SecureSocketFactory;
import com.sap.httpclient.uri.EncodingUtil;
import com.sap.httpclient.utils.dump.Dump;
import com.sap.httpclient.utils.dump.DumpOutputStream;
import com.sap.httpclient.HttpClientParameters;
import com.sap.httpclient.HostConfiguration;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * This class represents a HTTP Connection
 *
 * @author Nikolai Neichev
 */
public class HttpConnection {

  /**
   * Log location for this class.
   */
  private static final Location LOG = Location.getLocation(HttpConnection.class);

  private static final byte[] CRLF = new byte[]{(byte) 13, (byte) 10};

  private String host = null;

  private int port = -1;

  private String proxyHost = null;

  private int proxyPort = -1;

  private Socket socket = null;

  private InputStream inputStream = null;

  private OutputStream outputStream = null;

  /**
   * An {@link InputStream} for the response to an individual request.
   */
  private InputStream lastResponseInputStream = null;

  private boolean isOpen = false;

  private Protocol protocolInUse;

  private HttpClientParameters params = new HttpClientParameters();

  private boolean isLocked = false;

  private boolean usingSecureSocket = false;

  private boolean isTunnelEstablished = false;

  private HttpConnectionManager httpConnectionManager;

  private InetAddress localAddress;

  private HostConfiguration hostConfig = null;

  /**
   * Creates a new HTTP connection for the specified host and port with the default protocol "http".
   *
   * @param host the host
   * @param port the port
   */
  public HttpConnection(String host, int port) {
    this(null, -1, host, port, Protocol.getProtocol("http"));
  }

  /**
   * Creates a new HTTP connection for the specified host, port and protocol.
   *
   * @param host     the host
   * @param port     the port
   * @param protocol the protocol
   */
  public HttpConnection(String host, int port, Protocol protocol) {
    this(null, -1, host, port, protocol);
  }

//  /**
//   * Creates a new HTTP connection for the specified host and port via the specified proxy host and port
//   * using the default protocol "http".
//   *
//   * @param proxyHost the proxy host
//   * @param proxyPort the proxy port
//   * @param host      the host
//   * @param port      the port
//   */
//  public HttpConnection(String proxyHost, int proxyPort, String host, int port) {
//    this(proxyHost, proxyPort, host, port, Protocol.getProtocol("http"));
//  }

  /**
   * Creates a new HTTP connection for the specified host configuration.
   *
   * @param hostConfiguration the configuration
   */
  public HttpConnection(HostConfiguration hostConfiguration) {
    this(hostConfiguration.getProxyHost(),
            hostConfiguration.getProxyPort(),
            hostConfiguration.getHost(),
            hostConfiguration.getPort(),
            hostConfiguration.getProtocol());
    this.localAddress = hostConfiguration.getLocalAddress();
    this.hostConfig = hostConfiguration;
  }

  /**
   * Creates a new HTTP connection for the specified proxy host, proxy port, host, port and protocol.
   *
   * @param proxyHost the proxy host
   * @param proxyPort the proxy port
   * @param host      the host , can't be null
   * @param port      the port
   * @param protocol  the protocol , can't be null
   */
  public HttpConnection(String proxyHost, int proxyPort, String host, int port, Protocol protocol) {
    if (host == null) {
      throw new IllegalArgumentException("host parameter is null");
    }
    if (protocol == null) {
      throw new IllegalArgumentException("net is null");
    }
    this.proxyHost = proxyHost;
    this.proxyPort = proxyPort;
    this.host = host;
    this.port = protocol.resolvePort(port);
    protocolInUse = protocol;
  }

  /**
   * Returns the connection socket.
   *
   * @return the socket.
   */
  public Socket getSocket() {
    return this.socket;
  }

  /**
   * Returns the host.
   *
   * @return the host.
   */
  public String getHost() {
    return host;
  }

  /**
   * Sets the host to connect to.
   *
   * @param host the host , can't be null.
   * @throws IllegalStateException if the connection is already open
   */
  public void setHost(String host) throws IllegalStateException {
    if (host == null) {
      throw new IllegalArgumentException("host parameter is null");
    }
    assertNotOpen();
    this.host = host;
  }

  /**
   * Returns the port.
   *
   * @return the port.
   */
  public int getPort() {
    if (port < 0) {
      return isSecure() ? 443 : 80;
    } else {
      return port;
    }
  }

  /**
   * Sets the port.
   *
   * @param port the port to connect to
   * @throws IllegalStateException if the connection is already open
   */
  public void setPort(int port) throws IllegalStateException {
    assertNotOpen();
    this.port = port;
  }

  /**
   * Returns the proxy host.
   *
   * @return the proxy host.
   */
  public String getProxyHost() {
    return proxyHost;
  }

  /**
   * Sets the proxy host.
   *
   * @param host the proxy host.
   * @throws IllegalStateException if the connection is already open
   */
  public void setProxyHost(String host) throws IllegalStateException {
    assertNotOpen();
    proxyHost = host;
  }

  /**
   * Returns the proxy port.
   *
   * @return the proxy port.
   */
  public int getProxyPort() {
    return proxyPort;
  }

  /**
   * Sets the proxy port.
   *
   * @param port the proxy port.
   * @throws IllegalStateException if the connection is already open
   */
  public void setProxyPort(int port) throws IllegalStateException {
    assertNotOpen();
    proxyPort = port;
  }

  /**
   * Checks whether the connection is secure.
   *
   * @return <tt>true</tt> if connected over a secure net, <tt> false </tt> if not
   */
  public boolean isSecure() {
    return protocolInUse.isSecure();
  }

  /**
   * Returns the protocol of the connection .
   *
   * @return The protocol
   */
  public Protocol getProtocol() {
    return protocolInUse;
  }

  /**
   * Sets the protocol of the connection
   *
   * @param protocol The protocol.
   * @throws IllegalStateException if the connection is already open
   */
  public void setProtocol(Protocol protocol) {
    assertNotOpen();
    if (protocol == null) {
      throw new IllegalArgumentException("net is null");
    }
    protocolInUse = protocol;
  }

  /**
   * Return the local address of the connection.
   *
   * @return InetAddress the local address
   */
  public InetAddress getLocalAddress() {
    return this.localAddress;
  }

  /**
   * Set the local address of the connection.
   *
   * @param localAddress the local address to use
   */
  public void setLocalAddress(InetAddress localAddress) {
    assertNotOpen();
    this.localAddress = localAddress;
  }

  /**
   * Check if the connection is open.
   *
   * @return <code>true</code> if the connection is open
   */
  public boolean isOpen() {
    return isOpen;
  }

  /**
   * Closes the connection if stale.
   *
   * @return <code>true</code> if the connection was stale,  <code>false</code> otherwise.
	 * @throws java.io.IOException if an IO exception uccures
   */
  public boolean closeIfStale() throws IOException {
    if (isOpen && isStale()) {
      LOG.debugT("Connection is stale, closing...");
      close();
      return true;
    }
    return false;
  }

  /**
   * Checks if the connection is stale : this means it's either closed or attempt to read would fail
   *
   * @return <tt>true</tt> if the connection is already closed, or a read would fail.
   * @throws IOException if the stale connection test is interrupted.
   */
  protected boolean isStale() throws IOException {
    boolean isStale = true;
    if (isOpen) { // the connection is open, but we have to check if we can read from it
      isStale = false;
      try {
        if (inputStream.available() <= 0) {
          try {
            socket.setSoTimeout(1);
            inputStream.mark(1);
            int byteRead = inputStream.read();
            if (byteRead == -1) { // stream closed - so the connection is stale
              isStale = true;
            } else { // read successfull, reseting stream
              inputStream.reset();
            }
          } finally { // restore the so_timeout setting
            socket.setSoTimeout(this.params.getSoTimeout());
          }
        }
      } catch (InterruptedIOException e) {
        if (e instanceof java.net.SocketTimeoutException) {
          // the connection is not stale - will continue
        } else{
          throw e;
        }
      } catch (IOException e) {
        // the read or so_timeout failed - so the connection is stale
        LOG.traceThrowableT(Severity.DEBUG, "Error while reading from the socket, the connection is stale.", e);
        isStale = true;
      }
    }
    return isStale;
  }

  /**
   * Checks if the connection is established via a proxy.
   *
   * @return <tt>true</tt> if a proxy is used to establish the connection, <tt>false</tt> otherwise.
   */
  public boolean isProxied() {
    return ( (proxyHost != null) && (proxyPort > 0) );
  }

  /**
   * Set the last response stream of the connection.
   *
   * @param inStream The stream associated with an HttpMethod.
   */
  public void setLastResponseInputStream(InputStream inStream) {
    lastResponseInputStream = inStream;
  }

  /**
   * Returns the stream used to read the last response's body.
   *
   * @return An {@link InputStream} corresponding to the body of the last response.
   */
  public InputStream getLastResponseInputStream() {
    return lastResponseInputStream;
  }

  /**
   * Returns the connection's {@link HttpClientParameters}.
   *
   * @return the parameters.
   */
  public HttpClientParameters getParams() {
    return this.params;
  }

  /**
   * Assigns {@link HttpClientParameters} for this connection.
	 *
	 * @param params the parameters to set 
	 */
  public void setParams(final HttpClientParameters params) {
    if (params == null) {
      throw new IllegalArgumentException("Parameters is null");
    }
    this.params = params;
  }

  /**
   * Sets <code>SO_TIMEOUT</code> value to the connection underlying {@link Socket socket}.
   *
   * @param timeout the timeout value
   * @throws SocketException if there is an error.
   * @throws IllegalStateException if not connected
   */
  public void setSocketTimeout(int timeout) throws SocketException, IllegalStateException {
    assertOpen();
    if (this.socket != null) {
      this.socket.setSoTimeout(timeout);
    }
  }

  /**
   * Establishes a phisical connection to the specified host and port
   * The underlying socket is created from the {@link ProtocolSocketFactory}.
   *
   * @throws IOException if an I/O error occurs.
   */
  public void open() throws IOException {
    assertNotOpen();
    String host;
    int port;
    if (isProxied()) { // connect through proxy
      host = proxyHost;
      port = proxyPort;
      if (LOG.beDebug()) {
        LOG.debugT("Open connection to proxy at " + host + ":" + port);
      }
    } else { // direct connection
      host = this.host;
      port = this.port;
      if (LOG.beDebug()) {
        LOG.debugT("Open connection to " + host + ":" + port);
      }
    }
    try {
      if (this.socket == null) {
        usingSecureSocket = isSecure() && !isProxied();
        // use the net's socket factory unless this is a secure proxied connection
        ProtocolSocketFactory socketFactory;
        if (isSecure() && isProxied()) {
          Protocol defaultprotocol = Protocol.getProtocol("http");
          socketFactory = defaultprotocol.getSocketFactory();
        } else {
          socketFactory = this.protocolInUse.getSocketFactory();
        }
        this.socket = socketFactory.createSocket(host, port, localAddress, 0, this.params);
      }
      // setting the socket props, if any
      socket.setTcpNoDelay(this.params.getTcpNoDelay());
      socket.setSoTimeout(this.params.getSoTimeout());
      int linger = this.params.getLinger();
      if (linger >= 0) {
        socket.setSoLinger(linger > 0, linger);
      }
      int sndBufSize = this.params.getSendBufferSize();
      if (sndBufSize >= 0) {
        socket.setSendBufferSize(sndBufSize);
      }
      int rcvBufSize = this.params.getReceiveBufferSize();
      if (rcvBufSize >= 0) {
        socket.setReceiveBufferSize(rcvBufSize);
      }
      int outbuffersize = socket.getSendBufferSize();
      if ((outbuffersize > 2048) || (outbuffersize <= 0)) {
        outbuffersize = 2048;
      }
      int inbuffersize = socket.getReceiveBufferSize();
      if ((inbuffersize > 2048) || (inbuffersize <= 0)) {
        inbuffersize = 2048;
      }
      inputStream = new BufferedInputStream(socket.getInputStream(), inbuffersize);
      outputStream = new BufferedOutputStream(socket.getOutputStream(), outbuffersize);
      isOpen = true;
    } catch (IOException e) {
      // there is a problem, we should clean up
      LOG.traceThrowableT(Severity.DEBUG, "IOException while opening the connection : ", e);
      closeSocketAndStreams();
      throw e;
    }
  }

  /**
   * Used to open a secure socket when a connection through proxy is established
   *
   * @throws IllegalStateException if connection is not secure and proxied or if the socket is already secure.
   * @throws IOException  if an I/O error occurs.
   */
  public void tunnelCreated() throws IllegalStateException, IOException {
    if (!isSecure() || !isProxied()) {
      throw new IllegalStateException("Connection must be secure and proxied to use this feature");
    }
    if (usingSecureSocket) {
      throw new IllegalStateException("Already using a secure socket");
    }
    if (LOG.beDebug()) {
      LOG.debugT("Secure tunnel to " + this.host + ":" + this.port);
    }
    SecureSocketFactory socketFactory = (SecureSocketFactory) protocolInUse.getSocketFactory();
    socket = socketFactory.createSocket(socket, host, port, true);
    int sndBufSize = this.params.getSendBufferSize();
    if (sndBufSize >= 0) {
      socket.setSendBufferSize(sndBufSize);
    }
    int rcvBufSize = this.params.getReceiveBufferSize();
    if (rcvBufSize >= 0) {
      socket.setReceiveBufferSize(rcvBufSize);
    }
    int outbuffersize = socket.getSendBufferSize();
    if (outbuffersize > 2048) {
      outbuffersize = 2048;
    }
    int inbuffersize = socket.getReceiveBufferSize();
    if (inbuffersize > 2048) {
      inbuffersize = 2048;
    }
    inputStream = new BufferedInputStream(socket.getInputStream(), inbuffersize);
    outputStream = new BufferedOutputStream(socket.getOutputStream(), outbuffersize);
    usingSecureSocket = true;
    isTunnelEstablished = true;
  }

  /**
   * Indicates if the connection is completely transparent from end to end.
   *
   * @return <tt>true</tt> if connection is not proxied or tunneled through a transparent proxy,
   * <tt>false<tt> otherwise.
   */
  public boolean isTransparent() {
    return !isProxied() || isTunnelEstablished;
  }

  /**
   * Sends the request to the server.
   *
   * @throws IOException if an I/O problem occurs
   */
  public void flushRequest() throws IOException {
    assertOpen();
    outputStream.flush();
  }

  /**
   * Returns the {@link OutputStream} of the conenction.
   *
   * @return a stream to write the request to
   * @throws IllegalStateException if the connection is not open
   * @throws IOException           if an I/O problem occurs
   */
  public OutputStream getOutputStream() throws IOException, IllegalStateException {
    assertOpen();
    OutputStream out = this.outputStream;
    if (Dump.CONTENT_DUMP.enabled() || Dump.DEBUG) {
      out = new DumpOutputStream(out, Dump.CONTENT_DUMP);
    }
    return out;
  }

  /**
   * Return the {@link InputStream} of the connection.
   *
   * @return InputStream The response incoming stream.
   * @throws IOException If an I/O problem occurs
   * @throws IllegalStateException If the connection isn't open.
   */
  public InputStream getInputStream() throws IOException, IllegalStateException {
    assertOpen();
    return inputStream;
  }

  /**
   * Checks if there is response available.
   *
   * @return boolean <tt>true</tt> if data is available, <tt>false</tt> otherwise.
   * @throws IOException If an I/O problem occurs
   */
  public boolean isResponseAvailable() throws IOException {
		return (this.isOpen) && (this.inputStream.available() > 0);
	}

  /**
   * Tests if there is response available until the specified time in milliseconds pases.
   *
   * @param timeout The milliseconds to wait for incoming data
   * @return boolean <tt>true</tt> if data is availble, <tt>false</tt> otherwise.
   * @throws IOException If an I/O problem occurs
   * @throws IllegalStateException If the connection isn't open.
   */
  public boolean isResponseAvailable(int timeout) throws IOException {
    assertOpen();
    boolean result = false;
    if (this.inputStream.available() > 0) {
      result = true;
    } else {
      try {
        this.socket.setSoTimeout(timeout);
        inputStream.mark(1);
        int byteRead = inputStream.read();
        if (byteRead != -1) {
          inputStream.reset();
          LOG.debugT("Data is available");
          result = true;
        } else {
          LOG.debugT("Data not available");
        }
      } catch (InterruptedIOException e) {
        if (e instanceof java.net.SocketTimeoutException) {
          if (LOG.beDebug()) {
            LOG.debugT("Data not available after " + timeout + " ms");
          }
        } else{
          throw e;
        }
      } finally {
        try {
          socket.setSoTimeout(this.params.getSoTimeout());
        } catch (IOException ioe) {
          LOG.traceThrowableT(Severity.DEBUG, "Socket is bad, no response is available.", ioe);
          result = false;
        }
      }
    }
    return result;
  }

  /**
   * Writes the bytes to the output stream.
   *
   * @param data the data to be written
   * @throws IllegalStateException if not connected
   * @throws IOException if an I/O problem occurs
   */
  public void write(byte[] data) throws IOException, IllegalStateException {
    this.write(data, 0, data.length);
  }

  /**
   * Writes the specified sub byte array to the output stream.
   *
   * @param data array containing the data to be written.
   * @param offset the start offset in the data.
   * @param length the number of bytes to write.
   * @throws IllegalStateException if not connected
   * @throws IOException if an I/O problem occurs
   */
  public void write(byte[] data, int offset, int length) throws IOException, IllegalStateException {
    if (offset < 0) {
      throw new IllegalArgumentException("Array offset may not be negative");
    }
    if (length < 0) {
      throw new IllegalArgumentException("Array length may not be negative");
    }
    if (offset + length > data.length) {
      throw new IllegalArgumentException("Given offset and length exceed the array length");
    }
    assertOpen();
    this.outputStream.write(data, offset, length);
  }

  /**
   * Writes the bytes, followed by [13,10]-(New Line).
   *
   * @param data the bytes to be written
   * @throws IllegalStateException if the connection is not open
   * @throws IOException if an I/O problem occurs
   */
  public void writeLine(byte[] data) throws IOException, IllegalStateException {
    write(data);
    writeLine();
  }

  /**
   * Writes the [13,10] to the outgoing stream.
   *
   * @throws IllegalStateException if the connection is not open
   * @throws IOException if an I/O problem occurs
   */
  public void writeLine() throws IOException, IllegalStateException {
    write(CRLF);
  }

  /**
   * Writes the specified String (as bytes) to the outgoing stream.
   *
   * @param data the string to be written
   * @param charset the charset to use for writing the data
   * @throws IllegalStateException if the connection is not open
   * @throws IOException if an I/O problem occurs
   */
  public void print(String data, String charset) throws IOException, IllegalStateException {
    write(EncodingUtil.getBytes(data, charset));
  }

  /**
   * Writes the specified String (as bytes), followed by [13,10] to the outgoing stream.
   *
   * @param data the data to be written
   * @param charset the charset to use for writing the data
   * @throws IllegalStateException if the connection is not open
   * @throws IOException if an I/O problem occurs
   */
  public void printLine(String data, String charset) throws IOException, IllegalStateException {
    writeLine(EncodingUtil.getBytes(data, charset));
  }

  /**
   * Reads up to <tt>"\n"</tt> from the (unchunked) incoming stream, using the default charset ISO-8859-1.
   *
   * @return a line from the response
   * @throws IllegalStateException if the connection is not open
   * @throws IOException if an I/O problem occurs
   */
  public String readLine() throws IOException, IllegalStateException {
    assertOpen();
    return readLine("ISO-8859-1");
  }

  /**
   * Reads up to <tt>"\n"</tt> from the (unchunked) incoming stream.
   *
   * @param charset the charset to use for reading the data
   * @return a line from the response
   * @throws IllegalStateException if the connection is not open
   * @throws IOException if an I/O problem occurs
   */
  public String readLine(final String charset) throws IOException, IllegalStateException {
    assertOpen();
    return HttpParser.readLine(inputStream, charset);
  }

  /**
   * Closes the connection.
   */
  public void close() {
    closeSocketAndStreams();
  }

  /**
   * Gets the httpConnectionManager.
   *
   * @return HttpConnectionManager
   */
  public HttpConnectionManager getHttpConnectionManager() {
    return httpConnectionManager;
  }

  /**
   * Sets the httpConnectionManager.
   *
   * @param httpConnectionManager The httpConnectionManager to set
   */
  public void setHttpConnectionManager(HttpConnectionManager httpConnectionManager) {
    this.httpConnectionManager = httpConnectionManager;
  }

  /**
   * Releases the connection.
   */
  public void releaseConnection() {
    if (isLocked) {
      LOG.debugT("Connection is locked. Call ignored.");
    } else if (httpConnectionManager != null) {
      LOG.debugT("Releasing connection to connection manager.");
      httpConnectionManager.releaseConnection(this);
    } else {
      LOG.warningT("HttpConnectionManager is null. Connection cannot be released.");
    }
  }

  /**
   * Tests if the connection is locked
   *
   * @return <tt>true</tt> if the connection is locked, <tt>false</tt> otherwise.
   */
  protected boolean isLocked() {
    return isLocked;
  }

  /**
   * Sets the locked flag.
   *
   * @param locked <tt>true</tt> to lock the connection, <tt>false</tt> to unlock the connection.
   */
  public void setLocked(boolean locked) {
    this.isLocked = locked;
  }

  /**
   * Closes everything out.
   */
  protected void closeSocketAndStreams() {
    isOpen = false;
    // no longer care about previous responses...
    lastResponseInputStream = null;
    if (null != outputStream) {
      OutputStream temp = outputStream;
      outputStream = null;
      try {
        temp.close();
      } catch (Exception ex) {
        LOG.traceThrowableT(Severity.DEBUG, "Exception caught when closing outgoing", ex);
        // ignored
      }
    }
    if (null != inputStream) {
      InputStream temp = inputStream;
      inputStream = null;
      try {
        temp.close();
      } catch (Exception ex) {
        LOG.traceThrowableT(Severity.DEBUG, "Exception caught when closing incoming", ex);
        // ignored
      }
    }
    if (null != socket) {
      Socket temp = socket;
      socket = null;
      try {
        temp.close();
      } catch (Exception ex) {
        LOG.traceThrowableT(Severity.DEBUG, "Exception caught when closing socket", ex);
        // ignored
      }
    }
    isTunnelEstablished = false;
    usingSecureSocket = false;
  }

  /**
   * Throws an {@link IllegalStateException} if the connection is already open.
   *
   * @throws IllegalStateException if connected
   */
  public void assertNotOpen() throws IllegalStateException {
    if (isOpen) {
      throw new IllegalStateException("Connection is open");
    }
  }

  /**
   * Throws an {@link IllegalStateException} if the connection is not open.
   *
   * @throws IllegalStateException if not connected
   */
  public void assertOpen() throws IllegalStateException {
    if (!isOpen) {
      throw new IllegalStateException("Connection is not open");
    }
  }

  /**
   * Gets the socket's sendBufferSize.
   *
   * @return the size of the buffer for the socket OutputStream, -1 if the value
   *         has not been set and the socket has not been opened
   * @throws SocketException if an error occurs while getting the socket value
   */
  public int getSendBufferSize() throws SocketException {
    if (socket == null) {
      return -1;
    } else {
      return socket.getSendBufferSize();
    }
  }

  public void reInitConnection(HostConfiguration hostConfig) {
    close();
    setHost(hostConfig.getHost());
    setPort(hostConfig.getPort());
    setProtocol(hostConfig.getProtocol());
    setProxyHost(hostConfig.getProxyHost());
    setProxyPort(hostConfig.getProxyPort());
    setLocalAddress(hostConfig.getLocalAddress());
    setLocked(false);
    this.hostConfig = hostConfig;
  }

  public HostConfiguration getHostConfiguration() {
    if (hostConfig == null) {
      hostConfig = new HostConfiguration();
      hostConfig.setHost(host, port, protocolInUse);
      if (isProxied()) {
        hostConfig.setProxy(proxyHost, proxyPort);
      }
      hostConfig.setLocalAddress(localAddress);
    }
    return hostConfig;
  }

}