package com.sap.engine.services.httpserver.server.memory.impl;

import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP;
import static com.sap.engine.services.httpserver.server.Log.LOCATION_MEMORY_STATISTIC;

import java.util.HashMap;

import com.sap.engine.services.httpserver.server.Log;
import com.sap.engine.services.httpserver.server.memory.IRequestMemoryReport;
import com.sap.engine.services.httpserver.server.memory.IRequestMemoryReportManager;
import com.sap.engine.services.httpserver.server.memory.IRequestMemoryReportStorage;


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
//	private static final String LOG_LOCATION  = "com.sap.tools.memory.reporting";
//	private static Location mm_location  = Location.getLocation(LOG_LOCATION);
	private IRequestMemoryReportStorage reportStorage = null;

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


	public void startRequestMemoryReport(int tag) throws Exception
	{
	  startRequestMemoryReport(tag, null);
	}

  /**
   * Starts memory profiling of a HTTP request.
   *
     * @param slotName user defined name under which the allocation statistic trace
     * is run.
     * @param sectionMark A string serving as identifier for the section.
   * @exception Exception If there is already a report started for this slot.
   */
  public void startRequestMemoryReport(int tag, String sectionMark) throws Exception {
    startRequestMemoryReport(tag, sectionMark, "");
  }
  /**
   * Starts memory profiling of a HTTP request.
   *
     * @param slotName user defined name under which the allocation statistic trace
     * is run.
     * @param sectionMark A string serving as identifier for the section.
   * @exception Exception If there is already a report started for this slot.
   */
  public void startRequestMemoryReport(int tag, String sectionMark, String requestURLPath) throws Exception {
    if (LOCATION_MEMORY_STATISTIC.beInfo()) {
      LOCATION_MEMORY_STATISTIC.infoT("RequestMemoryReportManager.startRequestMemoryReport: " + tag);
    }
    // precondition check
    if (mm_reports.get(tag)!= null)
    {
      throw new Exception("There is already a report existing for this request.");
    }

    RequestMemoryReport report = new RequestMemoryReport(tag, sectionMark, requestURLPath, Thread.currentThread());

    report.start();

    // If the max. number of reports is reached, probably a caller causes leaking reports. In this case clean everything.
    if (mm_reports.size()>MAX_REPORTS)
    {
      mm_reports.clear();
    }

    mm_reports.put(new Integer(tag), report);
  }


	public void stopRequestMemoryReport (int tag) throws Exception
	{
	  if (LOCATION_MEMORY_STATISTIC.beInfo()) {
      LOCATION_MEMORY_STATISTIC.infoT("RequestMemoryReportManager.stopRequestMemoryReport: " + tag);
    }
    stopRequestMemoryReport(tag, true);
	}

	public void stopRequestMemoryReport (int tag, boolean forceFullGC) throws Exception
	{
	  if (LOCATION_MEMORY_STATISTIC.beInfo()) {
      LOCATION_MEMORY_STATISTIC.infoT("RequestMemoryReportManager.stopRequestMemoryReport: " + tag + ", force full-GC: " + forceFullGC);
    }
    // precondition check
		RequestMemoryReport report = mm_reports.get(new Integer(tag));


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

    getRequestMemeoryReportStorage().storeReport(tag);
	}

	public void startIntermediateSection (int tag, String sectionMark) throws Exception
	{
	  if (LOCATION_MEMORY_STATISTIC.beInfo()) {
      LOCATION_MEMORY_STATISTIC.infoT("RequestMemoryReportManager.startIntermediateSection: " + tag + ", Section name: " + sectionMark);
    }
    RequestMemoryReport report = mm_reports.get(new Integer(tag));

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

	/**
	 * Adds the overall retain size of an http request to the Memory Statistics Report of the given http request.
	 * @param tag - id of the http request
	 * @param sessionSize - the already calculated retain size of the given http session
	 * */
	public void addSessionSizeToReport(int tag, long sessionSize){
		if (LOCATION_MEMORY_STATISTIC.beInfo()) {
		      LOCATION_MEMORY_STATISTIC.infoT("RequestMemoryReportManager.addSessionSizeToReport: " + tag + ", Session Size: " + sessionSize);
		    }
		RequestMemoryReport report = mm_reports.get(new Integer(tag));
		if (report != null)
		{
			report.setSessionSize(sessionSize);
		}else{
			if (LOCATION_MEMORY_STATISTIC.beWarning()) {
				Log.traceWarning(LOCATION_MEMORY_STATISTIC, "ASJ.http.000412", 
			            "Cannot add session size [{0}] to the memory statistics report with request id [{1}]. " +
			            "There is no report for the given request id. Check if the Memeory Statisctics Report feature is enabled.", new Object[]{sessionSize, tag}, null, null, null);
			    }
		}		 
	}

	public void stopIntermediateSection (int tag) throws Exception
	{
	  if (LOCATION_MEMORY_STATISTIC.beInfo()) {
      LOCATION_MEMORY_STATISTIC.infoT("RequestMemoryReportManager.stopIntermediateSection: " + tag);
    }
    RequestMemoryReport report = mm_reports.get(new Integer(tag));

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

	public IRequestMemoryReport getRequestMemoryReport(int tag) throws Exception
	{
	  if (LOCATION_MEMORY_STATISTIC.beInfo()) {
      LOCATION_MEMORY_STATISTIC.infoT("RequestMemoryReportManager.getRequestMemoryReport: " + tag);
    }
    // get the report
		RequestMemoryReport report = mm_reports.get(new Integer(tag));

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
		mm_reports.remove(new Integer(tag));

		return report;
	}


  public IRequestMemoryReportStorage getRequestMemeoryReportStorage() {
    if (reportStorage == null) {
      reportStorage = RequestMemoryReportStorage.getInstance();
    }
    return reportStorage;
  }
}
