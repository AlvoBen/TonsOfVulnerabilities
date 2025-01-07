package com.sap.httpclient;

/**
 * Executes a method from a new thread.
 */
class ExecuteMethodThread extends Thread {

  private HttpMethod method;
  private HttpClient client;
  private Exception exception;

  public ExecuteMethodThread(HttpMethod method, HttpClient client) {
    this.method = method;
    this.client = client;
  }

  public void run() {
    try {
      client.executeMethod(method);
      method.getResponseBodyAsString();
    } catch (Exception e) {
      e.printStackTrace();
      this.exception = e;
    } finally {
      method.releaseConnection();
    }
  }

  /**
   * @return HttpMethod
   */
  public HttpMethod getMethod() {
    return method;
  }

  /**
   * Gets the exception that occurred when executing the method.
   *
   * @return an Exception or <code>null</code> if no exception occurred
   */
  public Exception getException() {
    return exception;
  }

}