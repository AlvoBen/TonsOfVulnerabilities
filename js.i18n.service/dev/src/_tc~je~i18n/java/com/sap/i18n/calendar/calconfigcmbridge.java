package com.sap.i18n.calendar;

import com.sap.i18n.cache.BackendDataCache;
import com.sap.i18n.cache.BackendDataCacheFactory;
import com.sap.i18n.service.I18NConfigMngBridge;

public class CalConfigCMBridge extends I18NConfigMngBridge {

	private static CalConfigCMBridge cmBridge = null;

	private CalConfigCMBridge() {
		super();
	};

	public static CalConfigCMBridge getInstance() {
		if (cmBridge == null) {
			cmBridge = new CalConfigCMBridge();
		}
		return cmBridge;
	}

	@Override
	protected String getConfigurationName() {
		return "calConfig";
	}

	@Override
	public BackendDataCache getBackendDataCache() {
		return BackendDataCacheFactory.getCacheIslamicCalendar();
	}
}
