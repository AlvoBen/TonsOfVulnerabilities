package com.sap.engine.services.httpserver.server.memory.impl;

import static com.sap.engine.services.httpserver.server.Log.LOCATION_MEMORY_STATISTIC;

import java.io.BufferedReader;
import java.io.IOException;

import com.sap.engine.services.httpserver.server.memory.IRequestMemoryReport;

public class MemoryReportTraceWriter {
  
  public MemoryReportTraceWriter() {
  }
  
  /**
   * Writes specified report to the corresponding trace file
   */
  public void writeReport(IRequestMemoryReport memoryReport) throws IOException {
    if (LOCATION_MEMORY_STATISTIC.beDebug()) {
      LOCATION_MEMORY_STATISTIC.debugT(constructMessage(memoryReport));
    }
  }

  private String constructMessage(IRequestMemoryReport memoryReport) throws IOException {
    StringBuilder sb = new StringBuilder();
    sb.append("\r\n+-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+\r\n");
    sb.append("| URL: ");
    sb.append(memoryReport.getRequestURLPath());
    sb.append(" |");
    sb.append("\r\n+-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+\r\n");
    sb.append("| Approximate Session Size in Bytes: ");
    sb.append(memoryReport.getSessionSize());
    sb.append(" |");
    sb.append("\r\n+-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+\r\n");
    sb.append("| TotalMemoryAllocation: ");
    sb.append(memoryReport.getTotalMemoryAllocation());
    sb.append(" |");
    sb.append("\r\n+-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+\r\n");
    BufferedReader br = new BufferedReader(memoryReport.getFormattedReport());
    String line = br.readLine();
    while (line != null) {
      sb.append("| ");
      sb.append(line);
      sb.append(" |\r\n+-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+\r\n");
      line = br.readLine();
    }
    return sb.toString();
  }
}
