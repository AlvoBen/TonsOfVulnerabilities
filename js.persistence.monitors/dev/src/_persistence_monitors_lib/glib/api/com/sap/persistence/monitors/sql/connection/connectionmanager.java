package com.sap.persistence.monitors.sql.connection;

import java.util.SortedMap;

public interface ConnectionManager {
	public SortedMap getDataSourceInfo() throws Exception;
}
