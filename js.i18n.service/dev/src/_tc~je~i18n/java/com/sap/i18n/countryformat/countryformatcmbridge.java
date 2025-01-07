package com.sap.i18n.countryformat;

import com.sap.i18n.cache.BackendDataCache;
import com.sap.i18n.cache.BackendDataCacheFactory;
import com.sap.i18n.service.I18NConfigMngBridge;

public class CountryFormatCMBridge extends I18NConfigMngBridge {
	
	private static CountryFormatCMBridge cmBridge = null;

	private CountryFormatCMBridge() {
		super();
	};

	public static CountryFormatCMBridge getInstance() {
		if (cmBridge != null) {
			return cmBridge;
		} else {
			return cmBridge = new CountryFormatCMBridge();
		}
	}

	@Override
	public BackendDataCache getBackendDataCache() {
		return BackendDataCacheFactory.getCacheCountryFormat();
	}

	@Override
	protected String getConfigurationName() {
		return "countryformat";
	}

}
