package com.sap.sdm.util.log;

/**
 * An abstract tracing framework. An instance of <code>Trace</code> provides
 * methods for tracing the flow of execution and debug information. To each
 * class an instance of <code>Trace</code> is associated, such that focussing on
 * tracing a subset of all classes is possible.
 * 
 * @author Christian Gabrisch 03.01.2003
 */
public abstract class Trace {
	private static TraceFactory traceFactory;

	/**
	 * Sets a <code>TraceFactory</code>.
	 * 
	 * @param traceFactory
	 *            a <code>TraceFactory</code>
	 */
	public static void setTraceFactory(TraceFactory traceFactory) {
		Trace.traceFactory = traceFactory;

		return;
	}

	/**
	 * Returns the <code>TraceFactory</code> last set to the <code>Trace</code>
	 * class by the method <code>setTraceFactory(TraceFactory)</code>.
	 * 
	 * @return the <code>TraceFactory</code> set to the <code>Trace</code> class
	 */
	public static TraceFactory getTraceFactory() {
		return traceFactory;
	}

	/**
	 * Gets a <code>Trace</code> for the specified <code>Class</code>.
	 * 
	 * @param forClass
	 *            the specified <code>Class</code>
	 * @return a <code>Trace</code> for the specified <code>Class</code>
	 */
	public static Trace getTrace(Class forClass) {
		if (traceFactory.isTracingTurnedOn(forClass) == true) {
			return traceFactory.getTrace(forClass);
		} else {
			return DummyTrace.INSTANCE;
		}
	}

	/**
	 * Traces the invocation of a method. When tracing the invocation of a
	 * method, tracing the return from a method provides more clarity.
	 * 
	 * @param methodName
	 *            the name of the method invoked; may also contain the current
	 *            values of its parameters
	 * @see #exiting()
	 * @see #exiting(String)
	 */
	public abstract void entering(String methodName);

	/**
	 * Traces the return from a method.
	 * 
	 * @see #entering(String)
	 */
	public abstract void exiting();

	/**
	 * Traces the return from a method.
	 * 
	 * @see #entering(String)
	 */
	public abstract void exiting(String methodName);

	/**
	 * Traces a debug information.
	 * 
	 * @param debugInfo
	 *            a <code>String</code> containing the debug information to be
	 *            traced
	 */
	public abstract void debug(String debugInfo);

	/**
	 * Traces a debug information.
	 * 
	 * @param debugInfo
	 *            a <code>String</code> containing the debug information to be
	 *            traced
	 * @param throwable
	 *            a <code>Throwable</code> whose stack trace will be traced
	 */
	public abstract void debug(String debugInfo, Throwable throwable);
}
