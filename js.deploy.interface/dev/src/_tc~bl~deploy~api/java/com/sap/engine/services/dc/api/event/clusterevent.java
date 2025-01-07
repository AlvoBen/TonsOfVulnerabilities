/*
 * Created on May 5, 2005
 */
package com.sap.engine.services.dc.api.event;

import com.sap.engine.services.dc.api.event.ClusterEventAction;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Event object passed to the Cluster Event listener implementations
 * registered to the Un/DeployProcessor.</DD>
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
 * @see com.sap.engine.services.dc.api.event.ClusterListener
 */
public class ClusterEvent extends DAEvent {

	/**
	 * Constructs a ClusterEvent object.
	 * 
	 * @param clusterEventAction
	 *            a ClusterEventAction object
	 */
	public ClusterEvent(ClusterEventAction clusterEventAction) {
		super(null, clusterEventAction);
	}
}