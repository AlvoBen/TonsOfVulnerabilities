package com.sap.engine.services.dc.cm.web_disp.impl;

import com.sap.engine.services.dc.cm.web_disp.WDController;
import com.sap.engine.services.dc.cm.web_disp.WDControllerFactory;
import com.sap.engine.services.dc.cm.web_disp.WDException;

public final class WDControllerFactoryImpl extends WDControllerFactory {

	@Override
	public WDController createWDController(String wdServerInfo)
			throws WDException {
		return new WDControllerImpl(wdServerInfo);
	}

}
