package com.sap.tools.memory.reporting.impl;

import java.io.StringReader;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import com.sap.tc.logging.Location;
import com.sap.tools.memory.reporting.IRequestMemoryReport;
import com.sap.tools.memory.trace.AllocationStatisticRegistry;
import com.sap.tools.memory.trace.AllocationStatisticRecord;

/**
 * The class RequestMemoryReport provides detailed information about the memory consumption caused by a HTTP request.
 * Detailed values are provided by getter-methods as well as part of a summary report which can be
 * obtained by calling getFormattedReport().
 * 
 * @author Michael Herrmann
 * @version $Revision: #3 $
 * Last modified by $Author: i022460 $, Change list $Change: 216041 $.
*/
public class RequestMemoryReport implements IRequestMemoryReport 
{
	private long mm_threadID = 0;
	private boolean mm_isActive = false;
	private static final String LOG_LOCATION  = "com.sap.tools.memory.reporting";
	private static Location mm_location  = Location.getLocation(LOG_LOCATION);
	private String mm_tag = "";
	private Integer mm_request_hashCode;
	private boolean mm_hasDetailedValues = false;
    
    private long totalNumberOfFreedInstances = 0;
    private long totalNumberOfFreedBytes = 0;
    private long totalNumberOfHoldInstances = 0;
    private long totalNumberOfHoldBytes = 0;
    private boolean mm_summaryAvailable = false;
    
    private StringBuffer mm_detailedReport = new StringBuffer();
    
    private Map<String,AllocationStatisticRecord> mm_statistic; 
    
    /**
     * Constructor. The tag for the allocation statistic is calculated from the hash code of the request.
     * The thread is a parameter to obtain the thread ID. 
     */
    public RequestMemoryReport (HttpServletRequest request, Thread thread)
	{
		mm_threadID = thread.getId();
		
		mm_request_hashCode = new Integer(request.hashCode());
		mm_tag = mm_request_hashCode.toString() + ":";
		
		setIsActive(true);
	}
	
	/**
	 * Starts the reporting by setting the allocation ID. 
	 */
    public void start()
	{
    	String localTag = mm_tag + "request";
		AllocationStatisticRegistry.pushThreadTag(localTag, false);
		
		mm_location.infoT("RequestMemoryReport.start: tag: " + localTag);
	}
	    
    /**
	 * Stops the reporting. Cleans all data in the tag lib.
	 */
	public void terminate(boolean forceFullGC)
	{
		AllocationStatisticRegistry.popThreadTag();
	
		// get and remove data
		mm_statistic = AllocationStatisticRegistry.getAllocationStatistic(mm_tag + ".*", forceFullGC, true, true);
		mm_hasDetailedValues = forceFullGC;
	}
    
    private void getSummaryValues()  // this also creates the detailed report
	{
		long numberOfFreedInstances = 0;
	    long numberOfFreedBytes = 0;
	    long numberOfHoldInstances = 0;
	    long numberOfHoldBytes = 0;
   
		Iterator<Map.Entry<String,AllocationStatisticRecord>> iter = mm_statistic.entrySet().iterator();
		
		while (iter.hasNext())
		{ 
			Map.Entry<String,AllocationStatisticRecord> mapEntry = iter.next();
			
			String tag = mapEntry.getKey();
			AllocationStatisticRecord entry = mapEntry.getValue();
			
			numberOfFreedInstances = entry.getFreedObjects();
			numberOfFreedBytes = entry.getFreedBytes();
		    numberOfHoldInstances = entry.getHoldObjects();
		    numberOfHoldBytes = entry.getHoldBytes();
		    
			mm_location.infoT("RequestMemoryReport.getSummaryValues: Section name: " + tag);
			mm_location.infoT("RequestMemoryReport.getSummaryValues: Processing space: " + (numberOfFreedBytes + numberOfHoldBytes));
		    
		    if (mm_hasDetailedValues)  // only trace detailed values if the garbage collector was triggered
		    {
		    	mm_location.infoT("RequestMemoryReport.getSummaryValues: numberOfFreedInstances: " + numberOfFreedInstances);
		    	mm_location.infoT("RequestMemoryReport.getSummaryValues: numberOfFreedBytes: " + numberOfFreedBytes);
			    mm_location.infoT("RequestMemoryReport.getSummaryValues: numberOfHoldInstances: " + numberOfHoldInstances);
			    mm_location.infoT("RequestMemoryReport.getSummaryValues: numberOfHoldBytes: " + numberOfHoldBytes);
		    }
		    		    
		    // Create detailed report
		    mm_detailedReport.append("Section tag: " + tag);
		    mm_detailedReport.append("\t");
		    mm_detailedReport.append("Processing space: " + (numberOfFreedBytes + numberOfHoldBytes));
		    mm_detailedReport.append("\t");
		    
		    if (mm_hasDetailedValues)  // only add detailed values to the report if the garbage collector was triggered
		    {
		    	mm_detailedReport.append("Number of freed instances: " + numberOfFreedInstances);
			    mm_detailedReport.append("\t");
			    mm_detailedReport.append("Number of freed bytes: " + numberOfFreedBytes);
			    mm_detailedReport.append("\t");
			    mm_detailedReport.append("Number of hold instances: " + numberOfHoldInstances);
			    mm_detailedReport.append("\t");
			    mm_detailedReport.append("Number of hold bytes: " + numberOfHoldBytes);
		    }
		    mm_detailedReport.append("\n");
		    
		    // calculate no. of hold and freed bytes also in the case of non-detailed values, because they are needed for getTotalMemoryAllocation();
		    totalNumberOfFreedBytes = totalNumberOfFreedBytes + numberOfFreedBytes;
		    totalNumberOfHoldBytes = totalNumberOfHoldBytes + numberOfHoldBytes;
		    
		    if (mm_hasDetailedValues)  // only calculate the other detailed values if the garbage collector was triggered
		    {
		    	totalNumberOfFreedInstances = totalNumberOfFreedInstances + numberOfFreedInstances;
			    totalNumberOfHoldInstances = totalNumberOfHoldInstances + numberOfHoldInstances;
			}
		}
		mm_location.infoT("RequestMemoryReport.getSummaryValues: Formatted report: \n" + reportAsString());
	}
	
	public void startIntermediateSection (String sectionMark) throws Exception
	{
		AllocationStatisticRegistry.pushThreadTag(sectionMark, true);	
	}
	
	
	public void stopIntermediateSection () throws Exception
	{
		AllocationStatisticRegistry.popThreadTag();
	}
	
	
	/*
	 *  (non-Javadoc)
	 * @see com.sap.tools.memory.reporting.IRequestMemoryReport#getTotalMemoryAllocation()
	 */
	public long getTotalMemoryAllocation ()
	{
		assert isActive()==false;

		if (!mm_summaryAvailable)
		{
			getSummaryValues();
			mm_summaryAvailable = true;
		}
		
		long memoryAllocation = internalGetNumberOfHoldBytes() + internalGetNumberOfFreedBytes();
		
		mm_location.infoT("RequestMemoryReport.getTotalMemoryAllocation: " + memoryAllocation);
		
		return memoryAllocation;
	}
	
	
	/*
	 *  (non-Javadoc)
	 * @see com.sap.tools.memory.reporting.IRequestMemoryReport#getNumberOfFreedInstances()
	 */
	public long getNumberOfFreedObjects()
	{
		assert isActive()==false;
		
		if (!mm_hasDetailedValues)
		{
			throw new RuntimeException("The report was stopped without triggering the garbage collector. Therefore detailed values for freed and hold objects and bytes are not available."); 
		}
		
		if (!mm_summaryAvailable)
		{
			getSummaryValues();
			mm_summaryAvailable = true;
		}
		return totalNumberOfFreedInstances;
	}
	
	
	/*
	 *  (non-Javadoc)
	 * @see com.sap.tools.memory.reporting.IRequestMemoryReport#getNumberOfFreedBytes()
	 */
	public long getNumberOfFreedBytes()
	{
		assert isActive()==false;
		
		if (!mm_hasDetailedValues)
		{
			throw new RuntimeException("The report was stopped without triggering the garbage collector. Therefore detailed values for freed and hold objects and bytes are not available."); 
		}
		
		if (!mm_summaryAvailable)
		{
			getSummaryValues();
			mm_summaryAvailable = true;
		}
		return internalGetNumberOfFreedBytes();
	}
	
	
	// call this method only after getSummaryValues() has been called!
	private long internalGetNumberOfFreedBytes()
	{
		return totalNumberOfFreedBytes;
	}
	
	
	/*
	 *  (non-Javadoc)
	 * @see com.sap.tools.memory.reporting.IRequestMemoryReport#getNumberOfHoldInstances()
	 */
	public long getNumberOfHoldObjects()
	{
		assert isActive()==false;
		
		if (!mm_hasDetailedValues)
		{
			throw new RuntimeException("The report was stopped without triggering the garbage collector. Therefore detailed values for freed and hold objects and bytes are not available."); 
		}
		
		if (!mm_summaryAvailable)
		{
			getSummaryValues();
			mm_summaryAvailable = true;
		}
		return totalNumberOfHoldInstances;
	}
	
	
	/*
	 *  (non-Javadoc)
	 * @see com.sap.tools.memory.reporting.IRequestMemoryReport#getNumberOfHoldBytes()
	 */
	public long getNumberOfHoldBytes()
	{
		assert isActive()==false;

		if (!mm_hasDetailedValues)
		{
			throw new RuntimeException("The report was stopped without triggering the garbage collector. Therefore detailed values for freed and hold objects and bytes are not available."); 
		}
		
		if (!mm_summaryAvailable)
		{
			getSummaryValues();
			mm_summaryAvailable = true;
		}
		return internalGetNumberOfHoldBytes();
	}
	
	
	// call this method only after getSummaryValues() has been called!
	private long internalGetNumberOfHoldBytes()
	{
		return totalNumberOfHoldBytes;
	}
	
	
	public long getTotalMemoryAllocation (String sectionMark)
	{
		assert isActive()==false;
		
		return internalGetNumberOfHoldBytes(sectionMark) + internalGetNumberOfFreedBytes(sectionMark); 
	}
	
	
	public long getNumberOfFreedObjects(String sectionMark)
	{
		assert isActive()==false;
		
		if (!mm_hasDetailedValues)
		{
			throw new RuntimeException("The report was stopped without triggering the garbage collector. Therefore detailed values for freed and hold objects and bytes are not available."); 
		}
		
		AllocationStatisticRecord record = mm_statistic.get(mm_tag+sectionMark); 
		
		if (record==null)
		{
			throw new RuntimeException("No data available for section mark: " + sectionMark);
		}
		
		return record.getFreedObjects();
	}
	
	
	public long getNumberOfFreedBytes(String sectionMark)
	{
		assert isActive()==false;
		
		if (!mm_hasDetailedValues)
		{
			throw new RuntimeException("The report was stopped without triggering the garbage collector. Therefore detailed values for freed and hold objects and bytes are not available."); 
		}
		return internalGetNumberOfFreedBytes(sectionMark);
		
	}
	
	
	// call this method only after getSummaryValues() has been called!
	private long internalGetNumberOfFreedBytes(String sectionMark)
	{
		AllocationStatisticRecord record = mm_statistic.get(mm_tag+sectionMark);
		
		if (record==null)
		{
			throw new RuntimeException("No data available for section mark: " + sectionMark);
		}
		
		return record.getFreedBytes();
	}
	
	
	// call this method only after getSummaryValues() has been called!
	private long internalGetNumberOfHoldBytes(String sectionMark)
	{
		AllocationStatisticRecord record = mm_statistic.get(mm_tag+sectionMark);
		
		if (record==null)
		{
			throw new RuntimeException("No data available for section mark: " + sectionMark);
		}
		
		return record.getHoldBytes();
	}
	
	
	
	
	public long getNumberOfHoldObjects(String sectionMark)
	{
		assert isActive()==false;
		
		if (!mm_hasDetailedValues)
		{
			throw new RuntimeException("The report was stopped without triggering the garbage collector. Therefore detailed values for freed and hold objects and bytes are not available."); 
		}
		
		AllocationStatisticRecord record = mm_statistic.get(mm_tag+sectionMark); 
		
		if (record==null)
		{
			throw new RuntimeException("No data available for section mark: " + sectionMark);
		}

		return record.getHoldObjects();
	}
	
	
	public long getNumberOfHoldBytes(String sectionMark)
	{
		assert isActive()==false;
		
		if (!mm_hasDetailedValues)
		{
			throw new RuntimeException("The report was stopped without triggering the garbage collector. Therefore detailed values for freed and hold objects and bytes are not available."); 
		}
		
		return internalGetNumberOfHoldBytes(sectionMark);	
	}
	
	
	public long[] getSectionValues(String sectionName)
	{
		if (!mm_hasDetailedValues)
		{
			throw new RuntimeException("The report was stopped without triggering the garbage collector. Therefore detailed values for freed and hold objects and bytes are not available."); 
		}
		
		long[] ret = new long[5];
		
		long freedBytes = mm_statistic.get(sectionName).getFreedBytes();
		long holdBytes = mm_statistic.get(sectionName).getHoldBytes();
		
		ret[0] = holdBytes + freedBytes;
		ret[1] = mm_statistic.get(sectionName).getFreedObjects();
		ret[2] = freedBytes;
		ret[3] = mm_statistic.get(sectionName).getHoldObjects();
		ret[4] = holdBytes;
		
		return ret;
	}
	
	
	public List<String> getSectionNames()
	{
		return new ArrayList<String> (mm_statistic.keySet());
	}
	
	
	/*
	 *  (non-Javadoc)
	 * @see com.sap.tools.memory.reporting.IRequestMemoryReport#getFormattedReport()
	 */
	public StringReader getFormattedReport ()
	{
		assert isActive()==false;
		
		mm_location.infoT("RequestMemoryReport.getFormattedReport");
		
		if (!mm_summaryAvailable)
		{
			getSummaryValues();
			mm_summaryAvailable = true;
		}
		
		mm_location.infoT("RequestMemoryReport.getFormattedReport: report: \n" + reportAsString());
		
		return new StringReader (reportAsString());
	}
	
	
	/**
	 *  Provides the preformatted report as string.
	 */
	private String reportAsString()
	{
		return mm_detailedReport.toString();
	}

	
	/**
	 * Provides the thread ID of the thread in which the report was created.
	 */
	public long getThreadID ()
	{
		assert mm_threadID != 0;
		
		return mm_threadID;
	}

	
	/**
	 * Marks the report as active. Only active reports can be stopped. Only inactive reports can be obtained.
	 */
	public void setIsActive (boolean active)
	{
		mm_isActive = active;
	}
	

	/**
	 * Returns the active-state.
	 */
	public boolean isActive ()
	{
		return mm_isActive;
	}
	
}
