package com.sap.engine.services.dc.api.spi.criticalshutdown;

public interface Process {

	/**
	 * 
	 * @return the process id of this process
	 */
	public int getPid();

	/**
	 * 
	 * @return the name of this process
	 */
	public String getName();

	/**
	 * 
	 * @return some additional description of the process state
	 */
	public String getDescription();

}
