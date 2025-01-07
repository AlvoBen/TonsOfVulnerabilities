package com.sap.engine.services.dc.api.spi;

/**
 * 
 * Deploy controller API always connects to a particular java instance. This
 * interface encapsulates connection information for the current java instance
 * 
 * @author I040924
 * 
 */
public interface ConnectionInfo {

	/**
	 * 
	 * @return the instance host
	 */
	public String getInstanceHost();

	/**
	 * 
	 * 
	 * @return the port on which the sapcontrol webservice is listening on the
	 *         current java instance
	 */
	public int getSapcontrolPort();

	/**
	 * 
	 * @return the p4 port of the current instance
	 */
	public int getP4Port();

}
