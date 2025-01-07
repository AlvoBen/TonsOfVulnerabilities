package com.sap.engine.services.dc.jstartup.impl;

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.services.dc.jstartup.JStartupClusterManager;
import com.sap.engine.services.dc.jstartup.JStartupFactory;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-7
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class JStartupFactoryImpl extends JStartupFactory {

	public JStartupFactoryImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.jstartup.JStartupFactory#
	 * createJStartupClusterManager(java.lang.String, int)
	 */
	public JStartupClusterManager createJStartupClusterManager(String msHost,
			int msPort, String osUserName, String osUserPass,
			ThreadSystem threadSystem) {
		return new JStartupClusterManagerImpl(msHost, msPort, osUserName,
				osUserPass, threadSystem);
	}

}
