package com.sap.httpclient.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Maintains a chain of {@link HttpRequestHandler}s where new request-handlers
 * can be prepended/appended.
 * <p/>
 * For each call to processRequest(ResponseWriter,SimpleHttpServerConnection,RequestLine,Header[])}
 * we iterate over the chain from the start to the end, stopping as soon as a handler
 * has claimed the outgoing.
 */
public class HttpRequestHandlerChain implements HttpRequestHandler {

  private List<HttpRequestHandler> subhandlers = new ArrayList<HttpRequestHandler>();

  public HttpRequestHandlerChain(final HttpRequestHandlerChain chain) {
    super();
    if (chain != null) {
      this.subhandlers.clear();
      this.subhandlers.addAll(chain.subhandlers);
    }
  }

  public HttpRequestHandlerChain() {
    super();
  }

  public synchronized void clear() {
    subhandlers.clear();
  }

  public synchronized void prependHandler(HttpRequestHandler handler) {
    subhandlers.add(0, handler);
  }

  public synchronized void appendHandler(HttpRequestHandler handler) {
    subhandlers.add(handler);
  }

  public synchronized boolean processRequest(final SimpleHttpServerConnection conn,
                                             final SimpleRequest request) throws IOException {
		for (HttpRequestHandler h : subhandlers) {
			boolean stop = h.processRequest(conn, request);
			if (stop) {
				return true;
			}
		}
		return false;
  }
}