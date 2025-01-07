package com.sap.tools.memory.reporting;

import java.util.HashMap;
import com.sap.tools.memory.reporting.impl.RequestMemoryReport;
import javax.servlet.http.HttpServletRequest;
import com.sap.tc.logging.Location;


/**
 * See interface IRequestMemoryReportManager for a description.
 * 
 * @author Michael Herrmann
 * @version $Revision: #3 $
 * Last modified by $Author: i022460 $, Change list $Change: 216041 $.
 */
public class RequestMemoryReportManager implements IRequestMemoryReportManager
{
	private static int INITIAL_REPORTS = 20;
	private static int MAX_REPORTS = 1000;
	
	// the singleton
	private static IRequestMemoryReportManager mm_instance = null;
	// all reports are stored in a hash table
	private HashMap<Integer, RequestMemoryReport> mm_reports = new HashMap<Integer, RequestMemoryReport> (INITIAL_REPORTS);
	private static final String LOG_LOCATION  = "com.sap.tools.memory.reporting";
	private static Location mm_location  = Location.getLocation(LOG_LOCATION);
	
	
	/*
	 * Private constructor to have singleton.
	 */
	private RequestMemoryReportManager ()
	{		
	}
	
	
	public synchronized static IRequestMemoryReportManager getInstance ()
	{
		if (mm_instance == null)
		{
			mm_instance = new RequestMemoryReportManager();
		}
	
		return mm_instance;
	}
	
	
	public void startRequestMemoryReport (HttpServletRequest request) throws Exception
	{
		mm_location.infoT("RequestMemoryReportManager.startRequestMemoryReport: " + request.hashCode());
		
		// precondition check
		if (mm_reports.get(new Integer(request.hashCode()))!= null)
		{
			throw new Exception("There is already a report existing for this request.");
		}
		
		RequestMemoryReport report = new RequestMemoryReport(request, Thread.currentThread());
		
		report.start();
		
		// If the max. number of reports is reached, probably a caller causes leaking reports. In this case clean everything.
		if (mm_reports.size()>MAX_REPORTS)
		{
			mm_reports.clear();
		}
		
		mm_reports.put(new Integer(request.hashCode()), report);
	}
	
	
	public void stopRequestMemoryReport (HttpServletRequest request) throws Exception
	{
		mm_location.infoT("RequestMemoryReportManager.stopRequestMemoryReport: " + request.hashCode());
		stopRequestMemoryReport(request, true);
	}
	
	public void stopRequestMemoryReport (HttpServletRequest request, boolean forceFullGC) throws Exception
	{
		mm_location.infoT("RequestMemoryReportManager.stopRequestMemoryReport: " + request.hashCode() + ", force full-GC: " + forceFullGC);
		
		// precondition check
		RequestMemoryReport report = mm_reports.get(new Integer(request.hashCode()));
		
		if (report== null)
		{
			throw new Exception("There is no report ongoing for this request.");
		}
		
		if (report.getThreadID()!= Thread.currentThread().getId())
		{
			throw new Exception("The call is coming from a different thread than the report was initiated.");
		}
		
		if (report.isActive()==false)
		{
			throw new IllegalStateException("The report for this request was already stopped.");
		}
		
		// this fills in the statistic data from the tag lib
		report.terminate(forceFullGC);
		
		report.setIsActive(false);
	}
	
	public void startIntermediateSection (HttpServletRequest request, String sectionMark) throws Exception
	{
		mm_location.infoT("RequestMemoryReportManager.startIntermediateSection: " + request.hashCode() + ", Section name: " + sectionMark);
		
		RequestMemoryReport report = mm_reports.get(new Integer(request.hashCode()));
		
		// precondition check
		if (report == null)
		{
			throw new Exception("There is no report existing for this request.");
		}

		if (report.getThreadID()!= Thread.currentThread().getId())
		{
			throw new Exception("The call is coming from a different thread than the report was initiated.");
		}
		
		if (!report.isActive())
		{
			throw new Exception("Report is not active.");
		}
				
		report.startIntermediateSection(sectionMark);
	}
	
	
	public void stopIntermediateSection (HttpServletRequest request) throws Exception
	{
		mm_location.infoT("RequestMemoryReportManager.stopIntermediateSection: " + request.hashCode());
		
		RequestMemoryReport report = mm_reports.get(new Integer(request.hashCode()));
		
		// precondition check
		if (report == null)
		{
			throw new Exception("There is no report existing for this request.");
		}
		
		if (report.getThreadID()!= Thread.currentThread().getId())
		{
			throw new Exception("The call is coming from a different thread than the report was initiated.");
		}

		if (!report.isActive())
		{
			throw new Exception("Report is not active.");
		}
				
		report.stopIntermediateSection();
	}
	
	public IRequestMemoryReport getRequestMemoryReport(HttpServletRequest request) throws Exception
	{
		mm_location.infoT("RequestMemoryReportManager.getRequestMemoryReport: " + request.hashCode());
		
		// get the report
		RequestMemoryReport report = mm_reports.get(new Integer(request.hashCode()));
		
		// preconditions
		if (report==null)
		{
			throw new Exception ("There is no report for this request available");
		}
		if (report.isActive())
		{
			throw new RuntimeException ("Report is still active. The report needs to be stopped before it can be obtained.");
		}
		
		// remove the report from the table
		mm_reports.remove(new Integer(request.hashCode()));
		
		return report;
	}
}
