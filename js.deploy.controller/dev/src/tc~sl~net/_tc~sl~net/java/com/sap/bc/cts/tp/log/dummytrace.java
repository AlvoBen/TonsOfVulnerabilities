package com.sap.bc.cts.tp.log;

/**
 * A dummy implementation of the abstract <code>Trace</code> class. To be used
 * when tracing for a particular class is not turned on.
 * 
 * @author Java Change Management May 14, 2004
 */
final class DummyTrace extends Trace {
  final static DummyTrace INSTANCE = new DummyTrace();
  /**
   * @see com.sap.sdm.util.log.Trace#entering(String)
   */
  public void entering(String methodName) { }

  /**
   * @see com.sap.sdm.util.log.Trace#exiting()
   */
  public void exiting() { }

  public void exiting(String methodName) {}

  /**
   * @see com.sap.sdm.util.log.Trace#debug(String)
   */
  public void debug(String message) { }

  public void debug(String debugInfo, Throwable throwable) {}
}
