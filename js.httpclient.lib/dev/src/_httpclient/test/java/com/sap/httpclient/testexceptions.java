package com.sap.httpclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.sap.httpclient.exception.*;

public class TestExceptions extends TestCase {

  public TestExceptions(String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestExceptions.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestExceptions.class);
  }

  /**
   * Make sure that you can retrieve the "cause" from an HttpException
   */
  public void testGetCause() {
    Exception aCause = new IOException("the cause");
    try {
      throw new HttpException("http exception", aCause);
    } catch (HttpException e) {
      assertEquals("Retrieve cause from caught exception", e.getCause(), aCause);
    }
  }

  /**
   * Make sure HttpConnection prints its stack trace to a PrintWriter properly
   */
  public void testStackTraceWriter() {
    Exception aCause = new IOException("initial exception");
    try {
      throw new HttpException("http exception", aCause);
    } catch (HttpException e) {
      // Get the stack trace printed into a string
      StringWriter stringWriter = new StringWriter();
      PrintWriter writer = new PrintWriter(stringWriter);
      e.printStackTrace(writer);
      writer.flush();
      String stackTrace = stringWriter.toString();
      // Do some validation on what got printed
      validateStackTrace(e, stackTrace);
    }
  }

  /**
   * Make sure HttpConnection prints its stack trace to a PrintStream properly
   */
  public void testStackTraceStream() {
    Exception aCause = new IOException("initial exception");
    try {
      throw new HttpException("http exception", aCause);
    } catch (HttpException e) {
      // Get the stack trace printed into a string
      ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
      PrintStream stream = new PrintStream(byteStream);
      e.printStackTrace(stream);
      stream.flush();
      String stackTrace = byteStream.toString();  // Assume default charset
      // Do some validation on what got printed
      validateStackTrace(e, stackTrace);
    }
  }

  public void testExceptionInstantiation() {
    assertNotNull(new CredentialsNotAvailableException());
    assertNotNull(new InvalidCredentialsException());
    assertNotNull(new HttpClientError());
    URIException uriEx = new URIException();
    assertEquals(uriEx.getReasonCode(), URIException.UNKNOWN);
    assertNotNull( new URLEncodeException("test"));
    assertNotNull(new URLDecodeException("test"));
  }

  /**
   * Make sure an HttpException stack trace has the right info in it.
   * This doesn't bother parsing the whole thing, just does some sanity checks.
	 * @param exception the exception
	 * @param stackTrace the stack trace
	 */
  private void validateStackTrace(HttpException exception, String stackTrace) {
    assertTrue("Starts with exception string", stackTrace.startsWith(exception.toString()));
    Throwable cause = exception.getCause();
    if (cause != null) {
      assertTrue("Contains 'cause'", stackTrace.toLowerCase().indexOf("cause") != -1);
      assertTrue("Contains cause.toString()", stackTrace.indexOf(cause.toString()) != -1);
    }
  }
}