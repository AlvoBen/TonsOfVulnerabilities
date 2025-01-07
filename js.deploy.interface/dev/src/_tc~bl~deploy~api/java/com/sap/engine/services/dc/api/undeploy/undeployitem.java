package com.sap.engine.services.dc.api.undeploy;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Undeploy Item representation.</DD>
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
public interface UndeployItem {
	/**
	 * Returns undeploy item name.
	 * 
	 * @return name
	 */
	public String getName();

	/**
	 * Returns undeploy item vendor.
	 * 
	 * @return vendor
	 */
	public String getVendor();

	/**
	 * Returns undeploy item location.
	 * 
	 * @return location
	 */
	public String getLocation();

	/**
	 * Returns undeploy item version.
	 * 
	 * @return version
	 */
	public String getVersion();

	/**
	 * Returns undeploy item status(relevant after undeploy transaction).
	 * 
	 * @return status
	 * @see #setUndeployItemStatus
	 */
	public UndeployItemStatus getUndeployItemStatus();

	/**
	 * For interna purposes.Developers should not use this method. Set the
	 * status of the undeploy item
	 * 
	 * @param undeployItemStatus
	 *            undeploy item status
	 * @see #getUndeployItemStatus
	 */
	public void setUndeployItemStatus(UndeployItemStatus undeployItemStatus);

	/**
	 * Returns undeploy item description(relevant after undeploy transaction).
	 * 
	 * @return description
	 */
	public String getDescription();

}