package com.sap.engine.services.dc.api.util;

/**
 * The timer can be used in iterative tasks in order to make sure that some task
 * within the iteration is not carried out in intervals shorter than the timer
 * suggests.
 * 
 * @author I040924
 * 
 */
public class LimitingTimer {

	private final long interval;
	private long lastSuccessfulRollTime = 0;

	/**
	 * Creates an instance of this class
	 * 
	 * @param interval
	 *            the limiting interval of this timer
	 */
	public LimitingTimer(long interval) {
		this.interval = interval;
	}

	/**
	 * This method implements a non blocking time interval limiter When you call
	 * it it will check if the time of the last successful invocation of the
	 * method plus the time elapsed are greater than the limiting interval of
	 * this timer. If this is true the last successful time will be updated and
	 * true will be returned. Otherwise false will be returned.
	 * 
	 * @return true if the task should be carried out false otherwise
	 */
	public synchronized boolean roll() {

		long now = System.currentTimeMillis();
		if (now > lastSuccessfulRollTime + interval) {

			this.lastSuccessfulRollTime = now;
			return true;
		}

		return false;
	}

}
