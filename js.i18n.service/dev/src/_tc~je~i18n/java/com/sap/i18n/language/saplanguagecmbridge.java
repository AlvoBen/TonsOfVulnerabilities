package com.sap.i18n.language;

import com.sap.i18n.cache.BackendDataCache;
import com.sap.i18n.cache.BackendDataCacheFactory;
import com.sap.i18n.service.I18NConfigMngBridge;

public class SAPLanguageCMBridge extends I18NConfigMngBridge {

	private static SAPLanguageCMBridge cmBridge = null;

	private SAPLanguageCMBridge() {
		super();
	};

	public static SAPLanguageCMBridge getInstance() {
		if (cmBridge == null) {
			cmBridge = new SAPLanguageCMBridge();
		} 
		return cmBridge;
	}

	@Override
	protected String getConfigurationName() {
		return "saplanguagekeys";
	}
	
	@Override
	public BackendDataCache getBackendDataCache() {
		return BackendDataCacheFactory.getCacheLanguage();
	}
}
