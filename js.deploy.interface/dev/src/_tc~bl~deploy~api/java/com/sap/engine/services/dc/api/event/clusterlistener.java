/*
 * Created on May 5, 2005
 */
package com.sap.engine.services.dc.api.event;

import com.sap.engine.services.dc.api.event.ClusterEvent;

/**
 *<DL>
 *<DT><B>Title: </B></DT>
 *<DD>J2EE Deployment Team</DD>
 *<DT><B>Description: </B></DT>
 *<DD>Listen for events triggered on specific deployment or undeployment events
 * like restart of the cluster due to offline deployment or undeployment.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2005-4-27</DD>
 * </DL>
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * 
 */
public interface ClusterListener {

	/**
	 * Invoked when cluster restart is triggered.
	 * 
	 * @param event
	 *            for cluster restart
	 */
	public void clusterRestartTriggered(ClusterEvent event);
}
