package com.sap.engine.services.dc.api.model;

import java.util.Set;

/**
 * <DL>
 * <DT><B>Title:</B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description:</B></DT>
 * <DD>Represents a single Software Component Archive.</DD>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company:</B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date:</B></DT>
 * <DD>2004-9-9</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 */
public interface Sca extends Sdu {
	/**
	 * Returns set of <code>Sda</code> objects with included currently deployed Software
	 * Developement Archives.
	 * 
	 * @return set of <code>Sda</code> objects
	 * @see com.sap.engine.services.dc.api.model.SdaId
	 */
	public Set getSdaIds();
	
	
	/**
	 * Returns set of <code>Sda</code> objects with included original Software
	 * Developement Archives.
	 * 
	 * @return set of <code>Sda</code> objects
	 * @see com.sap.engine.services.dc.api.model.SdaId
	 */
	public Set getOriginalSdaIds();
	
	/**
	 * Returns set of <code>Sda</code> objects with included not deployed Software
	 * Developement Archives (intersection of the original SDA set and the currently deployed SDA set).
	 * 
	 * @return set of <code>Sda</code> objects
	 * @see com.sap.engine.services.dc.api.model.SdaId
	 */
	public Set getNotDeployedSdaIds();

}
