package com.sap.httpclient.server;

import java.io.IOException;

import com.sap.httpclient.auth.Credentials;

/**
 * Simple server that registers default request handlers to act as a proxy.
 */
public class SimpleProxy extends SimpleHttpServer {

  private SimpleConnManager connmanager = null;
  private HttpRequestHandlerChain stdchain = null;

  public SimpleProxy(int port) throws IOException {
    super(port);
    this.connmanager = new SimpleConnManager();
    this.stdchain = new HttpRequestHandlerChain();
    this.stdchain.appendHandler(new TransparentProxyRequestHandler());
    this.stdchain.appendHandler(new ProxyRequestHandler(this.connmanager));
    setRequestHandler(this.stdchain);
  }

  public SimpleProxy() throws IOException {
    this(0);
  }

  public void requireAuthentication(final Credentials creds, final String realm, boolean keepalive) {
    HttpRequestHandlerChain chain = new HttpRequestHandlerChain(this.stdchain);
    chain.prependHandler(new ProxyAuthRequestHandler(creds, realm, keepalive));
    setRequestHandler(chain);
  }

  public void requireAuthentication(final Credentials creds) {
    HttpRequestHandlerChain chain = new HttpRequestHandlerChain(this.stdchain);
    chain.prependHandler(new ProxyAuthRequestHandler(creds));
    setRequestHandler(chain);
  }

  public void destroy() {
    super.destroy();
    this.connmanager.shutdown();
  }

  public void addHandler(final HttpRequestHandler handler) {
    this.stdchain.prependHandler(handler);
  }

}