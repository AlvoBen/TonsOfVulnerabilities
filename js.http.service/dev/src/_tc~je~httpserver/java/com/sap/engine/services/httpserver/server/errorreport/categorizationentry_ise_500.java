package com.sap.engine.services.httpserver.server.errorreport;

import java.util.ArrayList;

/**
 * The class aims to provide a map between the categorization Id, number of the occurrences of problems with the same id.  
 * @author I043824
 *
 */

public class CategorizationEntry_ISE_500 extends ErrorCategorizationEntry implements Comparable<CategorizationEntry_ISE_500>{
	
	//number of occurrences of the problem (entry with the same caregorixationID)
	private long ocurrencesNumber = 0;
	
	//list of error report files
	private ArrayList<String> errorReportFiles = new ArrayList<String>();
	
	public CategorizationEntry_ISE_500(int categorizationId){
		super(categorizationId);
		ocurrencesNumber = 1;
	}

	public void addOccurrence(){
		ocurrencesNumber ++;
	}
	
	public void addErrorReportFile(String fileName){
		if (!errorReportFiles.contains(fileName)){
			errorReportFiles.add(fileName);
		}
	}
	
	//comparison is done based on the number of occurrences
	public int compareTo(CategorizationEntry_ISE_500 ise500) {
		long result = ocurrencesNumber - ise500.getOcurrencesNumber();
	    return ((int) result);
	  }

// ----------------- getters -----------------
	
	public long getOcurrencesNumber() {
		return ocurrencesNumber;
	}
	
	public ArrayList<String> getErrorReportFiles() {
		return errorReportFiles;
	}

}
