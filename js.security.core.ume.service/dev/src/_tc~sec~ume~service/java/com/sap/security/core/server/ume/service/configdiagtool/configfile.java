package com.sap.security.core.server.ume.service.configdiagtool;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.sap.security.core.util.imp.Util;

public class ConfigFile {

	private String              _name;
	private byte[]              _globalData;
	private Map<String, byte[]> _dataByInstance;

	public ConfigFile() { /* Must not be used. */ }

	public ConfigFile(String name) {
		_name           = name;
		_globalData     = null;
		_dataByInstance = new HashMap<String, byte[]>(1);
	}

	public void setGlobalData(InputStream dataStream) throws IOException {
		_globalData = Util.readInputStreamToBytes(dataStream);
	}

	public void setDataForInstance(String instanceID, InputStream dataStream) throws IOException {
		_dataByInstance.put(instanceID, Util.readInputStreamToBytes(dataStream));
	}

	public String getName() {
		return _name;
	}

	public byte[] getGlobalData() {
		return _globalData;
	}

	public byte[] getDataForInstance(String instanceID) {
		return _dataByInstance.get(instanceID);
	}

}
