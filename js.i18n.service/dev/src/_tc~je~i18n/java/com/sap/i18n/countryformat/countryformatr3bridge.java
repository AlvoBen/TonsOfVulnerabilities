package com.sap.i18n.countryformat;

import com.sap.i18n.cache.BackendDataCache;
import com.sap.i18n.cache.BackendDataCacheFactory;
import com.sap.i18n.service.I18NR3BridgeIntf;

public class CountryFormatR3Bridge extends I18NR3BridgeIntf {
	
private static I18NR3BridgeIntf m_R3Bridge = null;
	
	private CountryFormatR3Bridge(){};
	
	public static I18NR3BridgeIntf getInstance(){
		if(m_R3Bridge == null){
			m_R3Bridge = new CountryFormatR3Bridge();
		}return m_R3Bridge;
	}

	@Override
	public BackendDataCache getBackendDataCache() {
		return BackendDataCacheFactory.getCacheCountryFormat();
	}
}
