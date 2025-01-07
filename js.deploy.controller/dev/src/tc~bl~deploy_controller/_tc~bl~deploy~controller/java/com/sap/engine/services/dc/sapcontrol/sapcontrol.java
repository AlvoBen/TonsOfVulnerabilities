package com.sap.engine.services.dc.sapcontrol;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public interface SapControl {

	/**
	 * Sends event for restarting the instance and returns immediately.
	 * 
	 * @throws SapControlException
	 *             in case of an error
	 */
	public void restartInstance() throws SapControlException;

}
