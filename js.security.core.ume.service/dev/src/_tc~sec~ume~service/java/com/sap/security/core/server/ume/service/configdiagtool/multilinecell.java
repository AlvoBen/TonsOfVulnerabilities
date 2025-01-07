package com.sap.security.core.server.ume.service.configdiagtool;

import java.util.Iterator;
import java.util.List;

public class MultiLineCell extends TableCell {

	private List<String> _lines;

	private MultiLineCell() { /* Must not be used. */ }

	public MultiLineCell(List<String> lines) {
		_lines = lines;
	}

	@Override
	public String getCSVRepresentation() {
		StringBuffer resultBuffer = new StringBuffer();

		Iterator<String> linesIterator = _lines.iterator();
		while(linesIterator.hasNext()) {
			resultBuffer.append(linesIterator.next());
			if(linesIterator.hasNext()) {
				resultBuffer.append(Table.LINE_SEPARATOR);
			}
		}

		return encodeForCSV(resultBuffer.toString());
	}

}
