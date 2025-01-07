package com.sap.i18n.saptimezone;

import com.sap.i18n.cache.BackendDataCache;
import com.sap.i18n.cache.BackendDataCacheFactory;
import com.sap.i18n.service.I18NR3BridgeIntf;

public class SAPTimeZoneR3Bridge extends I18NR3BridgeIntf {

	private static I18NR3BridgeIntf SAPTimeZoneR3BridgeInstance = null;

	private SAPTimeZoneR3Bridge() {
	};

	public static I18NR3BridgeIntf getInstance() {
		if (SAPTimeZoneR3BridgeInstance == null) {
			SAPTimeZoneR3BridgeInstance = new SAPTimeZoneR3Bridge();
		}
		return SAPTimeZoneR3BridgeInstance;
	}
	
	@Override
	public BackendDataCache getBackendDataCache() {
		return BackendDataCacheFactory.getCacheTimeZone();
	}
}
