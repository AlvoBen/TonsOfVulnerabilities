/*
 * Created on May 5, 2005
 */
package com.sap.engine.services.dc.api.event;

import com.sap.engine.services.dc.api.event.DeploymentEvent;

/**
 *<DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Listens for events that can triggered before or after the deployment.</DD>
 * 
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2005-4-27</DD>
 *</DL>
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 */
public interface DeploymentListener {
	/**
	 * The method is invoked twice per item in normal scenario. Once exactly
	 * before deployment delivery and once again after the deployment. It is
	 * quite possible to receive only the first event on erroneous scenario.
	 * 
	 * @param event
	 *            - identifies the deployment event action and the affected
	 *            deployment Item.
	 */
	public void deploymentPerformed(DeploymentEvent event);
}
