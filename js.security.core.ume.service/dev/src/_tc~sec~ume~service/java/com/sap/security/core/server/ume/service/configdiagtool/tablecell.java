package com.sap.security.core.server.ume.service.configdiagtool;

public abstract class TableCell {

	public abstract String getCSVRepresentation();

	protected String encodeForCSV(String string) {
		// "Double quotes" must be doubled for CSV.
		if(string.indexOf('\"') > -1) {
			string = string.replace("\"", "\"\"");
		}

		return string;
	}

}
