package com.sap.security.core.server.ume.service.configdiagtool;

public class SimpleCell extends TableCell {

	private String _value;

	private SimpleCell() { /* Must not be used. */ }

	public SimpleCell(String value) {
		_value = value;
	}

	@Override
	public String getCSVRepresentation() {
		return encodeForCSV(_value);
	}

}
