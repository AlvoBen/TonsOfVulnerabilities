package com.sap.bc.cts.tp.log;

/**
 * A dummy implementation of the <code>TraceFactory</code> interfaces. Creates
 * <code>Trace</code> objects that don't do anything.
 * 
 * @author Java Change Management May 14, 2004
 */
final class DummyTraceFactory implements TraceFactory {

  public boolean isTracingTurnedOn(Class forClass) {
    return false;
  }

  public Trace getTrace(Class forClass) {
    return DummyTrace.INSTANCE;
  }

}
