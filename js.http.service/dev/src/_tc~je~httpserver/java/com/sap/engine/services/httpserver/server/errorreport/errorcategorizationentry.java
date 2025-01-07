package com.sap.engine.services.httpserver.server.errorreport;

import java.util.ArrayList;
import java.util.Date;

/**
 * ErrorCategorizationEntry is meant to encapsulate all the information related to each categorization entry generated as a result of an error situation.
 * 
 * @author Polina Genova, I043824
 *
 */
public class ErrorCategorizationEntry {
	//unique identifier of the categorization enrtry
	private int categorizationId;
	
	//the name of the last error report file
	private String lastErrorReportName  = null;
	
	//the time the last error report file was added to the categorization entry
	private Date lastErrorReportTime = null;
	
	//------------------------------------------
	
	public ErrorCategorizationEntry(int categorizationId){
		this.categorizationId = categorizationId ;
	}
	
	public ErrorCategorizationEntry(int categorizationId, String fileName){
		this.categorizationId = categorizationId ;
		newErrorReport(fileName);
	}
	
	/** 
	 * stores the error report file name and the date it is added to the categorization entry
	 * @param fileName
	 */
	public void newErrorReport(String fileName){
		lastErrorReportName = fileName;
		lastErrorReportTime = new Date();
	}

	// ----------------- getters -----------------
		
		public int getCategorizationId() {
			return categorizationId;
		}

		public String getLastErrorReportName() {
			return lastErrorReportName;
		}

		public Date getLastErrorReportTime(){
			return lastErrorReportTime;
		}
	
}

