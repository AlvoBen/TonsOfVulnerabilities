package com.sap.engine.services.httpserver.server.memory;

/**
 * Defines methods to start and stop memory profiling of a particular HTTP request.
 * It also defines the method to obtain the memory report for a specific HTTP request.
 * 
 * @author Michael Herrmann, Diyan Yordanov
 */
public interface IRequestMemoryReportManager {

  /**
   * Starts memory profiling of a HTTP request. The default section mark <code>request</code>
   * will be used as section identifier for the main slot.
   *
   * @param tag user defined name under which the allocation statistic trace
   * is run
   * @exception Exception If there is already a report started for this slot.
   */
  public void startRequestMemoryReport(int tag) throws Exception;

  /**
   * Starts memory profiling of a HTTP request.
   *
   * @param tag user defined name under which the allocation statistic trace
   * is run.
   * @param sectionMark A string serving as identifyer for the section.
   * @exception Exception If there is already a report started for this slot.
   */
  public void startRequestMemoryReport(int tag, String sectionMark)
      throws Exception;

  /**
   * Starts memory profiling of a HTTP request.
   *
   * @param tag user defined name under which the allocation statistic trace
   * is run.
   * @param sectionMark A string serving as identifyer for the section.
   * @param requestURLPath the request URL path to be associated with the report.
   * @exception Exception If there is already a report started for this slot.
   */
  public void startRequestMemoryReport(int tag, String sectionMark, String requestURLPath)
      throws Exception;
  
  /**
   * Starts memory trace of a particular section.
   * 
   * @param tag The slot for which the trace was already started by calling
   * <code>startRequestMemoryReport()</code>.
   * @param sectionMark A string serving as identifyer for the section.
   * @throws Exception If there is no report ongoing for this slot, or if the
   * report is not active, or if this call is coming from another thread as the one for which the report was initiated.
   */
  public void startIntermediateSection(int tag, String sectionMark)
      throws Exception;

  /**
   * Stops memory trace of the last started section.
   * 
   * @param tag The slot for which the trace was already started by
   * calling <code>startRequestMemoryReport()</code>.
   * @throws Exception If there is no report ongoing for this tag, or if the report is not active, or if this call is coming from another thread as the one for which the report was initiated.
   */
  public void stopIntermediateSection(int tag) throws Exception;

  /**
   * Stops memory profiling of a HTTP request. Before the report is terminated a garbage collector run is triggered to free objects
   * and to obtain detailed statistic. 
   *
   * @param tag The slot for which the trace was already started by
   * calling <code>startRequestMemoryReport()</code>.
   * @exception Exception If there is no report for this tag ongoing, or if the call is coming from a different thread than from within the report was started.
   * @exception IllegalStateException  If the report was already stopped.
   */
  public void stopRequestMemoryReport(int tag) throws Exception;

  /**
   * Stops memory profiling of a HTTP request associated with the specified slotName. In order to obtain detailed values such as the number of freed objects, the number of hold objects, etc., specify forceFullGC as true.
   *
   * @param slotName The slot for which the trace was already started by
   * calling <code>startRequestMemoryReport()</code>.
   * @param forceFullGC Specify true if a full-GC should be triggered, otherwise specify false.
   * @exception Exception If there is no report for this tag ongoing, or if the call is coming from a different thread than from within the report was started.
   * @exception IllegalStateException  If the report was already stopped.
   */
  public void stopRequestMemoryReport(int tag, boolean forceFullGC)
      throws Exception;

  /**
   * Sets the given calculated values of the session size to the report identified by the given id (tag). 
   * Should be set only once per http request when its overall size is calculated.
   * @param tag - The slot for which the trace was already started by
   * calling <code>startRequestMemoryReport()</code>.
   * @param calculatedSessionSize - The calculated retain size of the http session mapped to the given tag.
   */
  public void addSessionSizeToReport(int tag, long calculatedSessionSize);
  
  /**
   * Obtains the memory report for a request.
   *
   * @param tag The slot for which the trace was already started by
   * calling <code>startRequestMemoryReport()</code>.
   * @exception Exception If there is no report for this tag, or if the report is still ongoing.
   * @exception RuntimeException If the report is still active, meaning it was not stopped before. 
   */
  public IRequestMemoryReport getRequestMemoryReport(int tag) throws Exception;

  /**
   * Obtains the request memory report storage.
   *
   * @param tag The slot for which the trace was already started by
   * calling <code>startRequestMemoryReport()</code>.
   * @exception Exception If there is no report for this tag, or if the report is still ongoing.
   * @exception RuntimeException If the report is still active, meaning it was not stopped before. 
   */
  public IRequestMemoryReportStorage getRequestMemeoryReportStorage();
}
