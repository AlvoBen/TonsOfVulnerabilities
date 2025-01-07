package com.sap.security.core.server.ume.service.configdiagtool;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Table {

	public static final String LINE_SEPARATOR = "\n";

	// Data structure: The main list represents the table rows,
	// each row consists of a list containing all cells in the row.
	private List<List<TableCell>> _rows;
	private List<TableCell>       _lastRow;

	public Table() {
		_rows = new ArrayList<List<TableCell>>();

		// Add first empty line.
		nextRow();
	}

	public void addCell(TableCell cell) {
		_lastRow.add(cell);
	}

	public void addCell(String content) {
		addCell(new SimpleCell(content));
	}

	public void addCells(String[] contents) {
		for(int i = 0; i < contents.length; i++) {
			addCell(contents[i]);
		}
	}

	public void addMultiLineCell(List<String> lines) {
		addCell(new MultiLineCell(lines));
	}

	public void nextRow() {
		_lastRow = new ArrayList<TableCell>();
		_rows.add(_lastRow);
	}

	public void writeAsCSV(Writer writer) throws IOException {
		Iterator<List<TableCell>> rowIterator = _rows.iterator();
		while(rowIterator.hasNext()) {
			List<TableCell> currentRow = rowIterator.next();

			Iterator<TableCell> cellIterator = currentRow.iterator();
			while(cellIterator.hasNext()) {
				TableCell currentCell = cellIterator.next();
				writer.append("\"").append(currentCell.getCSVRepresentation()).append("\"");
				if(cellIterator.hasNext()) {
					writer.append(';');
				}
			}

			writer.append(LINE_SEPARATOR);
		}
	}

}
