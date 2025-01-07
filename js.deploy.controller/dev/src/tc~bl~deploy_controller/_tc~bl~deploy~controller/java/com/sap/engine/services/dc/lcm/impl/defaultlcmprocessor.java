package com.sap.engine.services.dc.lcm.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import com.sap.engine.services.dc.lcm.LCMResult;
import com.sap.engine.services.dc.lcm.LCMResultStatus;
import com.sap.engine.services.dc.lcm.LCMStatus;
import com.sap.engine.services.dc.lcm.LifeCycleManagerFactory;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.util.logging.DCLogConstants;
import com.sap.engine.services.dc.util.logging.DCLogResourceAccessor;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-3-29
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
final class DefaultLCMProcessor extends AbstractLCMProcessor {
	
	private Location location = getLocation(this.getClass());

	private static DefaultLCMProcessor INSTANCE;

	static DefaultLCMProcessor getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DefaultLCMProcessor();
		}

		return INSTANCE;
	}

	private DefaultLCMProcessor() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.lcm.impl.AbstractLCMProcessor#doStart(com.
	 * sap.engine.services.dc.repo.Sda)
	 */
	LCMResult doStart(Sda sda) {
		if (location.bePath()) {
			tracePath(location, 
					"System does not support [start] operation for component [{0}]",
					new Object[] { sda });
		}

		return LifeCycleManagerFactory.getInstance().createLCMResult(
				LCMResultStatus.NOT_SUPPORTED,
				DCLogResourceAccessor.getInstance().getMessageText(
						DCLogConstants.LCM_SYSTEM_DOES_NOT_SUPPORT_START_OPER,
						new Object[] { sda }));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.lcm.impl.AbstractLCMProcessor#doStop(com.sap
	 * .engine.services.dc.repo.Sda)
	 */
	LCMResult doStop(Sda sda) {
		if (location.bePath()) {
			tracePath(location, 
					"System does not support [stop] operation for component [{0}]",
					new Object[] { sda });
		}

		return LifeCycleManagerFactory.getInstance().createLCMResult(
				LCMResultStatus.NOT_SUPPORTED,
				DCLogResourceAccessor.getInstance().getMessageText(
						DCLogConstants.LCM_SYSTEM_DOES_NOT_SUPPORT_STOP_OPER,
						new Object[] { sda }));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.lcm.impl.AbstractLCMProcessor#getLCMStatus
	 * (com.sap.engine.services.dc.repo.Sda)
	 */
	LCMStatus getLCMStatus(Sda sda) {
		if (location.beDebug()) {
			traceDebug(
					location,
					"System does not support [getLCMStatus] operation for component [{0}]",
					new Object[] { sda });
		}

		return LCMStatus.NOT_SUPPORTED;
	}

}
