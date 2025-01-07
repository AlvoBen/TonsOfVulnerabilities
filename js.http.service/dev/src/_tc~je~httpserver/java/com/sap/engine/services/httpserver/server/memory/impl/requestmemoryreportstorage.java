package com.sap.engine.services.httpserver.server.memory.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.sap.engine.services.httpserver.server.memory.IRequestMemoryReport;
import com.sap.engine.services.httpserver.server.memory.IRequestMemoryReportManager;
import com.sap.engine.services.httpserver.server.memory.IRequestMemoryReportStorage;

public class RequestMemoryReportStorage implements IRequestMemoryReportStorage {
  private static int INITIAL_REPORTS = 20;

  private static int MAX_REPORTS = 1000;

  // the singleton
  private static IRequestMemoryReportStorage reportStorage = null;

  private IRequestMemoryReportManager memoryReportManager;

  // all reports are stored in a hash table - inmemory storage
  private LRUMap<Integer, IRequestMemoryReport> reports = new LRUMap<Integer, IRequestMemoryReport>(
      INITIAL_REPORTS);

  private MemoryReportTraceWriter memoryReportTraceWriter = null;

  /*
   * Private constructor to have singleton.
   */
  private RequestMemoryReportStorage() {
    memoryReportTraceWriter = new MemoryReportTraceWriter();
  }

  public synchronized static IRequestMemoryReportStorage getInstance() {
    if (reportStorage == null) {
      reportStorage = new RequestMemoryReportStorage();
    }

    return reportStorage;
  }

  public String getReport(Integer tag) throws Exception {
    IRequestMemoryReport mReport = reports.get(tag);

    // preconditions
    if (mReport == null) {
      throw new Exception("There is no report for this request available");
    }

    // remove the report from the table
    reports.remove(tag);

    String report = writeReportHtml(mReport);

    return report;
  }
  
  public long getSessionSizeFromReport(Integer tag) throws Exception {
	    IRequestMemoryReport mReport = reports.get(tag);

	    // preconditions
	    if (mReport == null) {
	      throw new Exception("There is no report for this request available");
	    }
	    
	    //TODO - clarify if the report should be removed or not.
	    //Currently it is not removed - thus the getSessionSize can be obtained several times.
	    //reports.remove(tag);
	    
	    return mReport.getSessionSize();
 }
  

  public void storeReport(int tag) throws Exception {
    IRequestMemoryReport memoryReport = getMemoryReportManager()
        .getRequestMemoryReport(tag);
    writeReport(memoryReport);
    reports.put(new Integer(tag), memoryReport);
  }

  public Object[] getReportIds() {
    return reports.keySet().toArray();
  }

  public Integer[] getTaggedSpaces() {
    return (Integer[]) (reports.keySet().toArray());
  }

  private void writeReport(IRequestMemoryReport memoryReport)
      throws IOException {
    memoryReportTraceWriter.writeReport(memoryReport);
  }

  private String writeReportHtml(IRequestMemoryReport memoryReport)
      throws IOException {
    StringBuilder sb = new StringBuilder();
    sb.append("<table border=\"1\">");
    sb.append("<tr><td>");
    sb.append(" Approximate Session Size in Bytes: ");
    sb.append(memoryReport.getSessionSize());
    sb.append("</td></tr>");
    sb.append("<tr><td>");
    sb.append(" TotalMemoryAllocation: ");
    sb.append(memoryReport.getTotalMemoryAllocation());
    sb.append(" </td></tr>");
    BufferedReader br = new BufferedReader(memoryReport.getFormattedReport());
    String line = br.readLine();
    while (line != null) {
      sb.append("<tr><td>");
      sb.append(line);
      sb.append("</td></tr>");
      line = br.readLine();
    }
    sb.append("</table>");
    return sb.toString();
  }

  private final IRequestMemoryReportManager getMemoryReportManager() {
    if (memoryReportManager == null) {
      memoryReportManager = RequestMemoryReportManager.getInstance();
    }
    return memoryReportManager;
  }

  // ------------------------------------------------------------------------------

  private final class LRUMap<K, V> extends LinkedHashMap<K, V> {

    /**
     * 
     */
    private static final long serialVersionUID = 4372455368577337965L;

    private int mMaxCapacity;

    public LRUMap(int initCapacity) {
      super(initCapacity, 1.0f, true);
      mMaxCapacity = 50 * initCapacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
      return size() > mMaxCapacity;
    }

  }
}
