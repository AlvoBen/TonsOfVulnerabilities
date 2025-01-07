package com.sap.engine.services.httpserver.server.memory;

/**
 * A storage for available request memory consumption reports.
 * 
 * @author I028673, Diyan Yordanov
 *
 */
public interface IRequestMemoryReportStorage {
  /**
   * Stores reguest memory statistic report for the specified tag.
   * @param tag the slot name for which the report is saved.
   * @throws Exception If there is no report for this tag ongoing, 
   * or if the call is coming from a different thread than from within the report was started.
   */
  public void storeReport(int tag) throws Exception;
  
  /**
   * Obtains the memory report for a request. Removes stored report from inmemory
   * storage.
   * @param tag the slot name under which the report is saved.
   * @return String representation of the report
   * @throws Exception If there is no report for this tag.
   * 
   */
  public String getReport(Integer tag) throws Exception;
  
  /**
   * Obtains the session size stored in a memory statistics report identified by the given id.
   * @param tag - tag the slot name under which the report is saved (id of the http request).
   * @return Exception If there is no report for this tag.
   */
  public long getSessionSizeFromReport(Integer tag)throws Exception;
  
  /**
   * Returns list of ids (tags) for inmemory stored reports.
   * @return
   */
  public Object[] getReportIds();

}
