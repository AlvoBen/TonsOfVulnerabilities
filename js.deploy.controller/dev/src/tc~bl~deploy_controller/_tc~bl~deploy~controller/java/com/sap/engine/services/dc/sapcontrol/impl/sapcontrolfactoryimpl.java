package com.sap.engine.services.dc.sapcontrol.impl;

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.services.dc.sapcontrol.SapControl;
import com.sap.engine.services.dc.sapcontrol.SapControlException;
import com.sap.engine.services.dc.sapcontrol.SapControlFactory;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public final class SapControlFactoryImpl extends SapControlFactory {

	@Override
	public SapControl createSapControl(ThreadSystem threadSystem,
			String runDir, String host, String user, String pass, String instNum)
			throws SapControlException {
		return new SapControlImpl(threadSystem, runDir, host, user, pass,
				instNum);
	}

}
