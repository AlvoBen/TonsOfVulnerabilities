package com.sap.tools.memory.reporting;

import javax.servlet.http.HttpServletRequest;

/**
 * Defines methods to start and stop memory profiling of a particular HTTP request.
 * It also defines the method to obtain the memory report for a specific HTTP request.
 * 
 * @author Michael Herrmann
 * @version $Revision: #3 $
 * Last modified by $Author: i022460 $, Change list $Change: 216041 $.
 */
public interface IRequestMemoryReportManager
{
	/**
	 * Starts memory profiling of a HTTP request.
	 * 
	 * @exception Exception If there is already a report started for this request.
	 */
	public void startRequestMemoryReport (HttpServletRequest request) throws Exception;
	
	
	/**
	 * Starts memory trace of a particular section.
	 * 
	 * @param request The HTTP request for which the trace was already started by calling startRequestMemoryReport(). 
	 * @param sectionMark A string serving as identifyer for the section.
	 * @throws Exception If there is no report ongoing for this request, or if the report is not active, or if this call is coming from another thread as the one for which the report was initiated.
	 */
	public void startIntermediateSection (HttpServletRequest request, String sectionMark) throws Exception;
	
	/**
	 * Stops memory trace of the last started section.
	 * 
	 * @param request The HTTP request for which the trace was already started by calling startRequestMemoryReport(). 
	 * @throws Exception If there is no report ongoing for this request, or if the report is not active, or if this call is coming from another thread as the one for which the report was initiated.
	 */
	public void stopIntermediateSection (HttpServletRequest request) throws Exception;
	
	
	/**
	 * Stops memory profiling of a HTTP request. Before the report is terminated a garbage collector run is triggered to free objects
	 * and to obtain detailed statistic. 
	 * 
	 * @exception Exception If there is no report for this request ongoing, or if the call is coming from a different thread than from within the report was started.
	 * @exception IllegalStateException  If the report was already stopped.
	 */
	public void stopRequestMemoryReport (HttpServletRequest request) throws Exception;
	
	/**
	 * Stops memory profiling of a HTTP request. In order to obtain detailed values such as the number of freed objects, the number of hold objects, etc., specify forceFullGC as true. 
	 * 
	 * @param forceFullGC Specify true if a full-GC should be triggered, otherwise specify false.
	 * @exception Exception If there is no report for this request ongoing, or if the call is coming from a different thread than from within the report was started.
	 * @exception IllegalStateException  If the report was already stopped.
	 */
	public void stopRequestMemoryReport (HttpServletRequest request, boolean forceFullGC) throws Exception;
	
	/**
	 * Obtains the memory report for a request.
	 * 
	 * @exception Exception If there is no report for this request, or if the report is still ongoing.
	 * @exception RuntimeException If the report is still active, meaning it was not stopped before. 
	 */
	public IRequestMemoryReport getRequestMemoryReport(HttpServletRequest request) throws Exception; 
}
