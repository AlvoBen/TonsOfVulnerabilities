/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * Created on Jan 4, 2006
 */
package com.sap.engine.services.dc.api.lcm.impl;

import java.util.HashMap;

import com.sap.engine.services.dc.api.event.LCEvent;
import com.sap.engine.services.dc.api.model.Sdu;
import com.sap.engine.services.dc.api.model.impl.SduMapperVisitor;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD></DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2005, SAP-AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>Jan 4, 2006</DD>
 * </DL>
 * 
 * @author Boris Savov( i030791 )
 * @version 1.0
 * @since 7.1
 * 
 */

public final class LCMEventMapper {
	private static final HashMap lcEventActions = new HashMap();
	static {
		lcEventActions
				.put(
						com.sap.engine.services.dc.event.LCEventAction.COMPONENT_STARTED,
						com.sap.engine.services.dc.api.event.LCEventAction.COMPONENT_STARTED);

		lcEventActions
				.put(
						com.sap.engine.services.dc.event.LCEventAction.COMPONENT_STOPPED,
						com.sap.engine.services.dc.api.event.LCEventAction.COMPONENT_STOPPED);
	}

	public static LCEvent mapLCEvent(
			com.sap.engine.services.dc.event.LCEvent dcEvent) {
		SduMapperVisitor sduMapperVisitor = new SduMapperVisitor();
		com.sap.engine.services.dc.repo.Sdu dcSdu = dcEvent.getSdu();
		if (dcSdu != null) {
			dcSdu.accept(sduMapperVisitor);
		}
		Sdu daSdu = sduMapperVisitor.getGeneratedSdu();
		com.sap.engine.services.dc.api.event.LCEventAction daEventAction = mapLCEventAction(dcEvent
				.getLCEventAction());
		LCEvent daEvent = new LCEvent(daSdu, daEventAction,
				dcEvent.getErrors(), dcEvent.getWarnings());
		return daEvent;
	}

	public static com.sap.engine.services.dc.api.event.LCEventAction mapLCEventAction(
			com.sap.engine.services.dc.event.LCEventAction dcEventAction) {
		com.sap.engine.services.dc.api.event.LCEventAction daEventAction = (com.sap.engine.services.dc.api.event.LCEventAction) lcEventActions
				.get(dcEventAction);
		if (daEventAction == null) {
			throw new RuntimeException(
					"[ERROR CODE DPL.DCAPI.1055] Unknown LCEvent "
							+ dcEventAction + " detected");
		}

		return daEventAction;
	}

}
