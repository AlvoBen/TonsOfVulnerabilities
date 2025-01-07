package com.sap.sdm.util.log;

/**
 * A factory for <code>Trace</code> objects.
 * 
 * @author Christian Gabrisch 07.01.2003
 */
public interface TraceFactory {
	/**
	 * Indicates whether tracing is turned on for the specified
	 * <code>Class</code>.
	 * 
	 * @param forClass
	 *            the specified <code>Class</code>
	 * @return <code>true</code> if tracing is turned on for
	 *         <code>forClass</code>; <code>false</code> otherwise
	 */
	public boolean isTracingTurnedOn(Class forClass);

	/**
	 * Returns a <code>Trace</code> for the specified <code>Class</code>.
	 * 
	 * @param forClass
	 *            the specified <code>Class</code>
	 * @return a <code>Trace</code> for the specified <code>Class</code>
	 */
	public Trace getTrace(Class forClass);
}
