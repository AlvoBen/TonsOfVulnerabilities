package com.sap.httpclient.server;

import java.io.IOException;

/**
 * Defines an HTTP request handler for the SimpleHttpServer
 */
public interface HttpRequestHandler {
  /**
   * The request handler is asked to process this request.
   * <p/>
   * If it is not capable/interested in processing it, this call should be simply ignored.
   * <p/>
   * Any modification of the outgoing stream (via <code>conn.getWriter()</code>)
   * by this request handler will stop the execution chain and return the outgoing
   * to the client.
   * <p/>
   * The handler may also rewrite the request parameters (this is useful in
   * {@link HttpRequestHandlerChain} structures).
   *
   * @param conn    The Connection object to which this request belongs to.
   * @param request The request object.
   * @return true if this handler handled the request and no other handlers in the
   *         chain should be called, false otherwise.
   * @throws IOException if any IOException occures
   */
  public boolean processRequest(final SimpleHttpServerConnection conn,
                                final SimpleRequest request) throws IOException;
}