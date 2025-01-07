package com.sap.tools.memory.reporting;

import java.io.StringReader;
import java.util.List;

/**
 * Defines the methods through which detailed information about the memory consumption caused by a HTTP request can be obtained.  
 * 
 * @author Michael Herrmann
 * @version $Revision: #3 $
 * Last modified by $Author: i022460 $, Change list $Change: 216041 $.
*/
public interface IRequestMemoryReport 
{
	/**
	 * Provides the total memory allocation which is the sum of the still allocated objects and the freed objects.
	 * 
	 * @return Number of bytes.
	 */
	public long getTotalMemoryAllocation ();
	
	
	/**
	 * Provides the number of garbage collected objects. Garbage collected objects are objects that have been created, but the garbage collector has removed them.
	 * 
	 * @return Number of objects.
	 * @exception RuntimeException The report was stopped without triggering the garbage collector. Detailed values can only be obtained if the garbage collector was triggered. 
	 */
	public long getNumberOfFreedObjects();
	
	
	/**
	 * Provides the total size of garbage collected objects. Garbage collected objects are objects that have been created, but the garbage collector has removed them.
	 * 
	 * @return Number of bytes.
	 * @exception RuntimeException The report was stopped without triggering the garbage collector. Detailed values can only be obtained if the garbage collector was triggered.
	 */
	public long getNumberOfFreedBytes();
	
	
	/**
	 * Provides the number of objects which are still allocated, meaning they haven't been freed by the garbage collector.
	 * 
	 * @return Number of objects.
	 * @exception RuntimeException The report was stopped without triggering the garbage collector. Detailed values can only be obtained if the garbage collector was triggered.
	 */
	public long getNumberOfHoldObjects();
	
	
	/**
	 * Provides the size of objects which are still allocated, meaning they haven't been freed by the garbage collector.
	 * 
	 * @return Number of bytes.
	 * @exception RuntimeException The report was stopped without triggering the garbage collector. Detailed values can only be obtained if the garbage collector was triggered.	 
	 */
	public long getNumberOfHoldBytes();
	
	
	/**
	 * Provides a pre-formatted report which contains the processing space of all traced sections.
	 * If the report was stopped with triggering the garbage collector, the report also contains detailed
	 * values such as the number of freed objects.
	 * 
	 * @return The reader from which to read the formatted report.
	 */
	public StringReader getFormattedReport ();
	
	/**
	 * Get the total memory allocation for a particular section. 
	 * 
	 * @param sectionMark A string serving as identifyer for the section.
	 * @return The total memory in bytes.
	 */
	public long getTotalMemoryAllocation (String sectionMark);
	
	/**
	 * Get the number of freed objects for a particular section.
	 *  
	 * @param sectionMark A string serving as identifyer for the section.
	 * @return The number of freed objects.
	 * @exception RuntimeException The report was stopped without triggering the garbage collector. Detailed values can only be obtained if the garbage collector was triggered.
	 */
	public long getNumberOfFreedObjects(String sectionMark);
	
	/**
	 * Get the number of freed bytes for a particular section.
	 *  
	 * @param sectionMark A string serving as identifyer for the section.
	 * @return The number of freed bytes.
	 * @exception RuntimeException The report was stopped without triggering the garbage collector. Detailed values can only be obtained if the garbage collector was triggered.
	 */
	public long getNumberOfFreedBytes(String sectionMark);
	
	/**
	 * Get the number of still allocated objects for a particular section.
	 *  
	 * @param sectionMark A string serving as identifyer for the section.
	 * @return The number of hold objects.
	 * @exception RuntimeException The report was stopped without triggering the garbage collector. Detailed values can only be obtained if the garbage collector was triggered.
	 */
	public long getNumberOfHoldObjects(String sectionMark);
	
	/**
	 * Get the number of still hold bytes for a particular section.
	 * 
	 * @param sectionMark A string serving as identifyer for the section.
	 * @return The number of hold bytes.
	 * @exception RuntimeException The report was stopped without triggering the garbage collector. Detailed values can only be obtained if the garbage collector was triggered.
	 */
	public long getNumberOfHoldBytes(String sectionMark);
	

	/**
	 * Returns the names of all sections that have been traced in this memory report.
	 *  
	 * @return The list of section names.
	 */
	public List<String> getSectionNames();
	
	/**
	 * Returns detailed values for a specific section. The following values are returned:
	 * The number of freed objects, the number of freed bytes, the number of hold objects, and the number of hold bytes.
	 * 
	 * @param sectionName The name of the section for which to obtain the values.
	 * @return Returns in the following order as array: The total memory allocation, the number of freed objects, the number of freed bytes, the number of hold objects, and the number of hold bytes.
	 */
	public long[] getSectionValues(String sectionName);
	
	/*
	 * @deprecated No more used internally. Remains only for backward compatibility.
	 */
	public String BEGIN_MARK = "BeginOfReport";
	
	/*
	 * @deprecated No more used internally. Remains only for backward compatibility.
	 */
	public String END_MARK = "EndOfReport";
}
