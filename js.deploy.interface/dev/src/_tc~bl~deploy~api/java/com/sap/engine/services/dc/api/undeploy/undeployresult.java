package com.sap.engine.services.dc.api.undeploy;

import com.sap.engine.services.dc.api.util.measurement.DAMeasurement;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>An instance of <code>UndeployResult</code> is returned at the end of the
 * UndeployItem undeployment (
 * {@link com.sap.engine.services.dc.api.undeploy.UndeployProcessor#undeploy(UndeployItem[])}
 * ).</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2004-9-9</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public interface UndeployResult {
	/**
	 * Returns status of the undeploy result.
	 * 
	 * @return undeploy result
	 */
	public UndeployResultStatus getUndeployStatus();

	/**
	 * Returns undeploy result description.
	 * 
	 * @return result description
	 */
	public String getDescription();

	/**
	 * Returns array with the items which are passed for undeploy for this
	 * transaction.
	 * 
	 * @return array of undeploy items
	 */
	public UndeployItem[] getUndeployItems();

	/**
	 * Returns ordered undeploy items list according to how they are undeployed.
	 * 
	 * @return ordered undeploy items list
	 */
	public UndeployItem[] getOrderedUndeployItems();
	
	/**
	 * Returns undeploy measurement.
	 * 
	 * @return undeploy measurement
	 */
	public DAMeasurement getMeasurement();

}