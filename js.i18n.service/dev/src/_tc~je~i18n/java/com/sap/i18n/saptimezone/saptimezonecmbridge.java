package com.sap.i18n.saptimezone;

import com.sap.i18n.cache.BackendDataCache;
import com.sap.i18n.cache.BackendDataCacheFactory;
import com.sap.i18n.service.I18NConfigMngBridge;

public class SAPTimeZoneCMBridge extends I18NConfigMngBridge {

	private static SAPTimeZoneCMBridge cfgManagerBridge = null;
	
	private SAPTimeZoneCMBridge(){
		super();
	};
	
	public static SAPTimeZoneCMBridge getInstance(){
		if (cfgManagerBridge == null){
			cfgManagerBridge = new SAPTimeZoneCMBridge();
		}
		return cfgManagerBridge;
	}
	
	@Override
	protected String getConfigurationName() {
		return "saptimezone";
	}
	
	@Override
	public BackendDataCache getBackendDataCache() {
		return BackendDataCacheFactory.getCacheTimeZone();
	}
}
