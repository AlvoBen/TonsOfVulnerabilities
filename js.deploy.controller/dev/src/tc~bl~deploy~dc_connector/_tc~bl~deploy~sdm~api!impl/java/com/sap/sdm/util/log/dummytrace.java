package com.sap.sdm.util.log;

/**
 * A dummy implementation of the abstract <code>Trace</code> class. To be used
 * when tracing for a particular class is not turned on.
 * 
 * @author Christian Gabrisch 03.01.2003
 */
final class DummyTrace extends Trace {
	final static DummyTrace INSTANCE = new DummyTrace();

	/**
	 * @see com.sap.sdm.util.log.Trace#entering(String)
	 */
	public void entering(String methodName) {
	}

	/**
	 * @see com.sap.sdm.util.log.Trace#exiting()
	 */
	public void exiting() {
	}

	public void exiting(String methodName) {
	}

	/**
	 * @see com.sap.sdm.util.log.Trace#debug(String)
	 */
	public void debug(String message) {
	}

	public void debug(String debugInfo, Throwable throwable) {
	}
}
