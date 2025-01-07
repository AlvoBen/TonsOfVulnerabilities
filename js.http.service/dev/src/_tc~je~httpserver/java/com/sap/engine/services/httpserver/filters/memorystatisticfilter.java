package com.sap.engine.services.httpserver.filters;

import static com.sap.engine.services.httpserver.server.Log.LOCATION_MEMORY_STATISTIC;

import java.io.IOException;

import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.accounting.measurement.AMeasurement;
import com.sap.engine.services.httpserver.chain.FilterConfig;
import com.sap.engine.services.httpserver.chain.FilterException;
import com.sap.engine.services.httpserver.chain.HTTPRequest;
import com.sap.engine.services.httpserver.chain.HTTPResponse;
import com.sap.engine.services.httpserver.chain.ServerChain;
import com.sap.engine.services.httpserver.chain.ServerFilter;
import com.sap.engine.services.httpserver.chain.ServerScope;
import com.sap.engine.services.httpserver.server.Log;
import com.sap.engine.services.httpserver.server.ServiceContext;
import com.sap.engine.services.httpserver.server.memory.IRequestMemoryReportManager;
import com.sap.engine.services.httpserver.server.memory.impl.RequestMemoryReportManager;
import com.sap.engine.services.httpserver.server.sessionsize.SessionSizeManager;

public class MemoryStatisticFilter  extends ServerFilter {
  private ServerScope serverScope;
  private IRequestMemoryReportManager memoryReportManager;

  @Override
  public void process(HTTPRequest request, HTTPResponse response,
      ServerChain chain) throws FilterException, IOException {
    
    boolean accounting = serverScope.getHttpProperties().isRequestAccountingEnabled() && Accounting.isEnabled();
    
    try {
      if (accounting) {
        Accounting.beginMeasure("Request in HTTP service", MemoryStatisticFilter.class);
      }
      
      if (serverScope.getHttpProperties().isMemoryTraceEnabled()) {
        request.getHTTPParameters().setMemoryTrace(true);
        try {
          getMemoryReportManager().startRequestMemoryReport(request.getID(), "HTTP", request.getURLPath());
        } catch (Exception e) {
          Log.logError("ASJ.http.000227", "Failed to start memory analysis.", e, null, null, null);
        }
  
        try {
          chain.process(request, response);
        } finally {
          try {
            RequestMemoryReportManager.getInstance().addSessionSizeToReport(request.getID(), 
                SessionSizeManager.getSessionSize(request.getID()));
            getMemoryReportManager().stopRequestMemoryReport(request.getID(), true);
          } catch (Exception e) {
            Log.logError("ASJ.http.000228", "Failed to stop memory analysis.", e, null, null, null);
          }    
        }
      } else {
          chain.process(request, response);
      }
    } finally { //accounting-end
      if (accounting) {
        AMeasurement measurement = Accounting.endMeasure("Request in HTTP service");
        if (LOCATION_MEMORY_STATISTIC.beDebug() && measurement != null) {
          LOCATION_MEMORY_STATISTIC.debugT(measurement.toDocumentAsString());
        }
      }
    }//accounting-end
  }

  private final IRequestMemoryReportManager getMemoryReportManager() {
    if (memoryReportManager == null) {
      memoryReportManager = RequestMemoryReportManager.getInstance();
    }
    return memoryReportManager;
  }
  
  public void destroy() {
    // TODO Auto-generated method stub

  }

  public void init(FilterConfig config) throws FilterException {
    this.serverScope = ServiceContext.getServiceContext();
  }

}
