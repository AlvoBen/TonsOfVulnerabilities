package com.sap.httpclient.net.connection.pool;

import com.sap.httpclient.HostConfiguration;

/**
 * Used to count waiting threads for connection to a specified host configuration
 *
 * @author Nikolai Neichev
 */
public class MonitorCounter {

  private int waitingThreads;
  private HostConfiguration hostConfig;

  public MonitorCounter(HostConfiguration hostConfig) {
    this.hostConfig = hostConfig;
  }

	/**
	 * Increases the threads waiting on this monitor
	 */
	public synchronized void increase() {
    waitingThreads++;
  }

	/**
	 * Decreases threads waiting on this monitor
	 */
	public synchronized void decrease() {
    waitingThreads--;
  }

	/**
	 * Gets the waiting threads count
	 * @return the thread count
	 */
	public synchronized int getWaitingThreads() {
    return waitingThreads;
  }

	/**
	 * Checks if there are waiting threads
	 * @return TRUE if there are waiting threads, FALSE if not
	 */
	public synchronized boolean hasWaitingThreads() {
		return (waitingThreads > 0);
	}

	/**
	 * Returns a string representation of this object 
	 * @return the string representation
	 */
	public String toString() {
    return hostConfig + " / threads : " + waitingThreads;
  }

}
