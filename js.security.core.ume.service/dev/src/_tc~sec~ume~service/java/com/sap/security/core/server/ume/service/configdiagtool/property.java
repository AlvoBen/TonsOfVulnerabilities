package com.sap.security.core.server.ume.service.configdiagtool;

import java.util.HashMap;
import java.util.Map;

public class Property {

	private String                    _name;
	private boolean                   _isSecure;
	private PropertyData              _globalData;
	private Map<String, PropertyData> _dataByInstance;

	private Property() { /* Must not be used. */ }

	public Property(String name, boolean isSecret) {
		_name           = name;
		_isSecure       = isSecret;
		_globalData     = null;
		_dataByInstance = new HashMap<String, PropertyData>(1);
	}

	public void setGlobalData(PropertyData data) {
		_globalData = data;
	}

	public void setDataForInstance(String instanceID, PropertyData data) {
		_dataByInstance.put(instanceID, data);
	}

	public String getName() {
		return _name;
	}

	public boolean isSecure() {
		return _isSecure;
	}

	public PropertyData getGlobalData() {
		return _globalData;
	}

	public PropertyData getDataForInstance(String instanceID) {
		return _dataByInstance.get(instanceID);
	}

}
