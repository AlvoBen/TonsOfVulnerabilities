package com.sap.engine.services.httpserver.filters;

import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP;
import static com.sap.engine.services.httpserver.server.Log.getExceptionStackTrace;

import java.io.IOException;

import com.sap.engine.services.httpserver.chain.FilterConfig;
import com.sap.engine.services.httpserver.chain.FilterException;
import com.sap.engine.services.httpserver.chain.HTTPRequest;
import com.sap.engine.services.httpserver.chain.HTTPResponse;
import com.sap.engine.services.httpserver.chain.HostChain;
import com.sap.engine.services.httpserver.chain.HostFilter;
import com.sap.engine.services.httpserver.chain.HostScope;
import com.sap.engine.services.httpserver.exceptions.HttpException;
import com.sap.engine.services.httpserver.interfaces.ErrorData;
import com.sap.engine.services.httpserver.interfaces.SupportabilityData;
import com.sap.engine.services.httpserver.lib.ResponseCodes;
import com.sap.engine.services.httpserver.lib.protocol.Methods;
import com.sap.engine.services.httpserver.server.Log;
import com.sap.engine.services.httpserver.server.RequestAnalizer;
import com.sap.engine.services.httpserver.server.ResponseImpl;
import com.sap.engine.services.httpserver.server.ServiceContext;
import com.sap.engine.services.httpserver.server.memory.impl.RequestMemoryReportManager;
import com.sap.engine.library.bytecode.tracing.Tracer;

public class WebContainerInvoker extends HostFilter {

  private static transient int t$classId;
  static {
    try {
      t$classId = Tracer.register(Class.forName("com.sap.engine.services.httpserver.server.Client"), "com.sap.engine.services.httpserver.server.Client", new String[] { "handle" }, new String[] { "()V" });
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  };

  public void process(HTTPRequest request, HTTPResponse response,
      HostChain chain) throws FilterException, IOException {

    HostScope hostScope = chain.getHostScope();
    ResponseImpl oldResponse = request.getClient().getResponse();
    RequestAnalizer requestAnalizer = request.getClient().getRequestAnalizer();
    try {
      requestAnalizer.parseInitialHeaders();
      // Entering this request session
      requestAnalizer.enterSessionRequest();

      // TODO: This should be done here, but for now method is made public
      

      // Checks for start page and if request path points to the root
      // and start page is allowed returns change location response
      if ((hostScope.getHostProperties().getStartPage() != null)
          && request.getURLPath().equals("/")
          && !request.getMethod().equals(Methods.PUT)) {
        oldResponse.setChangeLocation((hostScope.getHostProperties()
            .getStartPage()).getBytes());
        // TODO: Do it the right way
        oldResponse.makeAnswer(null);
        return;
      }

      // TODO: This should be done here, but for now method is made public
      Tracer.methodStart(t$classId, 0, request.getClient(), Tracer.OBJECT_ARRAY_0);
      try {

        requestAnalizer.findAndInitAlias();
        try {
          if (request.getHTTPParameters().isMemoryTrace()) {
            RequestMemoryReportManager.getInstance().startIntermediateSection(request.getID(), "WebContainer");
          }
        } catch (Exception e) {
          if (LOCATION_HTTP.beDebug()) {
            LOCATION_HTTP.debugT((new StringBuilder()).append("WebContainerInvoker.process(): ")
                .append("Cannot start intermediate section \"WebContainer\" for request ID[")
                .append(request.getID()).append("].").toString());
          }
        }
        long time = System.currentTimeMillis();
        chain.process(request, response);
        if (ServiceContext.getServiceContext().getHttpProperties().getTraceResponseTimeAbove() > -1) {
          time = System.currentTimeMillis() - time;
          requestAnalizer.getTimeStatisticsMap().put("/request/invokeWebContainer", time);
          if (LOCATION_HTTP.beDebug()) {
            LOCATION_HTTP.debugT((new StringBuilder()).append("WebContainerInvoker.process(): ")
                .append("Put new entry '/request/invokeWebContainer' with time [")
                .append(time).append("].").toString());
          }
        }

        Object t$returned = null;
        Tracer.methodEnd(t$classId, 0, t$returned, null);
      } catch (Throwable t$throwable) {
        Tracer.methodEnd(t$classId, 0, null, t$throwable);
        throw t$throwable;
      } finally {
        if (request.getHTTPParameters().isMemoryTrace()) {
          try {
            RequestMemoryReportManager.getInstance().stopIntermediateSection(request.getID());
          } catch (Exception e) {
            if (LOCATION_HTTP.beDebug()) {
              LOCATION_HTTP.debugT((new StringBuilder())
                  .append("WebContainerInvoker.process(): ")
                  .append("Cannot stop intermediate section \"WebContainer\" for request ID[")
                  .append(request.getID()).append("].").toString());
            }
          }
        }
      }

      if (ServiceContext.getServiceContext().getHttpProvider().getWebContainer() != null) {
        ServiceContext.getServiceContext().getHttpProvider().getWebContainer().endRequest(requestAnalizer);
      }
    } catch (OutOfMemoryError e) {
      throw e;
    } catch (ThreadDeath e) {
      throw e;
    } catch (Throwable t) {
      String logID = Log.logWarning("ASJ.http.000001",
        "HTTP request parsing and processing failed. HTTP error response [400 Bad Request] will be returned.",
        t, hostScope.getHostName().getBytes(), null, null);
      HttpException ex = new HttpException(HttpException.HTTP_PROCESSING_ERROR);
      SupportabilityData supportabilityData = new SupportabilityData(true, getExceptionStackTrace(t), logID);
      if (supportabilityData.getMessageId().equals("")) {
        supportabilityData.setMessageId("com.sap.ASJ.http.000001");
      }
      //TODO : Vily G : if there is no DC and CSN in the supportability data
      //and we are responsible for the problem then set our DC and CSN
      //otherwise leave them empty
      oldResponse.sendError(new ErrorData(ResponseCodes.code_bad_request, 
        ex.getLocalizedMessage(), Log.formatException(t, logID), false, supportabilityData));
    } finally {
      requestAnalizer.exitSessionRequest();
    }

  }

  public void init(FilterConfig config) throws FilterException {
  }

  public void destroy() {
  }
}
