package com.sap.i18n.language;

import com.sap.i18n.cache.BackendDataCache;
import com.sap.i18n.cache.BackendDataCacheFactory;
import com.sap.i18n.service.I18NR3BridgeIntf;

public class SAPLanguageR3Bridge extends I18NR3BridgeIntf {
	
	private static I18NR3BridgeIntf SAPLanguageR3Bridge = null;
	
	private SAPLanguageR3Bridge(){};
	
	public static I18NR3BridgeIntf getInstance(){
		if(SAPLanguageR3Bridge == null){
			SAPLanguageR3Bridge = new SAPLanguageR3Bridge();
		}return SAPLanguageR3Bridge;
	}
	
	@Override
	public BackendDataCache getBackendDataCache() {
		return BackendDataCacheFactory.getCacheLanguage();
	}
}
