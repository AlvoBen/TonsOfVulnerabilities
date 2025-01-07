/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/test/TestResult.java#3 $ SAP*/
package com.sap.engine.services.locking.test;


import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import com.sap.engine.services.locking.exception.SAPLockingUnsupportedOperationException;


/**
 * All results of a test.
 */
public class TestResult implements Serializable
{
  private transient StringWriter _buffer;
  private transient PrintWriter _out;
  
  private Exception _exception;
  private String _log;
  
  /**
   * Creates a new TestResult
   */
  public TestResult()
  {
    _buffer = new StringWriter(4096);
    _out = new PrintWriter(_buffer);
    _out.println("=======================================================");
  }
  
  /**
   * Writes the Object to the log.
   * Must not be called any more after close().
   */
  public void log(Object o)
  {
    if (_out == null)
      throw new SAPLockingUnsupportedOperationException(SAPLockingUnsupportedOperationException.TESTRESULT_CLOSED);
    _out.println(o);
  }
  
  /**
   * Must be called to close this TestResult.
   * The Exception can be null, if the Test finished successfully.
   */
  public void close(Exception e)
  {
    if (_out == null)
      throw new SAPLockingUnsupportedOperationException(SAPLockingUnsupportedOperationException.TESTRESULT_CLOSED);
    _exception = e;
    _out.println("-------------------------------------------------------");
    if (e != null)
    {
      _out.println("FINISHED WITH ERROR:");
      _out.println(e.getMessage());
      e.printStackTrace(_out);
    }
    else
      _out.println("FINISHED SUCCESSFULLY");
    _out.println("=======================================================");

    _out.flush();
    _buffer.flush();
    _log = _buffer.toString();
    _out = null;
    _buffer = null;
  }
  
  /**
   * Returns the Exception of this TestResult.
   * Must not be called before close().
   */
  public Exception getException()
  {
    if (_out != null)
      throw new SAPLockingUnsupportedOperationException(SAPLockingUnsupportedOperationException.TESTRESULT_CLOSED);
    return _exception;
  }
  
  /**
   * Returns the Log of this TestResult.
   * Must not be called before close().
   */
  public String getLog()
  {
    if (_out != null)
      throw new SAPLockingUnsupportedOperationException(SAPLockingUnsupportedOperationException.TESTRESULT_CLOSED);
    return _log;
  }
}
