package com.sap.engine.services.httpserver.server.sessionsize;

import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP_SESSION_SIZE;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

import com.sap.engine.objectprofiler.controller.impl.ClassesFilter;
import com.sap.engine.objectprofiler.graph.Graph;
import com.sap.engine.objectprofiler.graph.TooManyNodesException;
import com.sap.engine.services.httpserver.server.Log;
import com.sap.engine.services.httpserver.server.ServiceContext;
import com.sap.engine.tools.sharecheck.SessionSerializationReport;
import com.sap.engine.tools.sharecheck.SessionSerializationReportFactory;

/**
 * Stores the info for the calculating size of the object
 * @author Violeta Uzunova (I024174)
 *
 */
public class SessionSizeManager {    
  /**
   * An error code indicating that the session size calculation feature is 
   * disabled. To enable it switch on PollyProperty property
   */
  public static final long ERROR_CODE_FEATURE_DISABLED = -1L;
  /**
   * An error code indicating that the max allowed session size configured in 
   * PollyProprety is reached.  
   */
  public static final long ERROR_CODE_MAX_CONFIGURED_SESSION_SIZE_REACHED = -2L;
  /**
   * An error code indicating that there is no collected info for the session size of this request
   */
  public static final long ERROR_CODE_NO_INFO_FOR_THIS_REQUEST = -3L;  
  /**
   * An error code indicating that configured libs limit has been reached (number of nodes, depth level, ect)  
   */
  public static final long ERROR_CODE_CONFIGURED_LIBS_LIMIT_REACHED = -4L;
  /**
   * An error code indicating an unexpected error in the libs
   */
  public static final long ERROR_CODE_UNEXPECTED_ERROR_IN_LIBS = -5L;
  
  // stores the info for the running requests which processing is still not 
  // reached the end of SessionSizeFilter
  //Hashtable<requestId, ConcurrentReadHashmap<alias, size>> sessionSizes;
  private static Hashtable<Integer, ConcurrentHashMap<String, Long>> sessionSizes 
    = new Hashtable<Integer, ConcurrentHashMap<String, Long>>();      

  // stores the info for those requests which processing is closed to the end 
  // and its processing is over SessionSizeFilter where the session size is 
  // consolidated
  //Hashtable<requestId, sessionSize> calculatedSessionSizes;
  private static Hashtable<Integer, Long> calculatedSessionSizes = new Hashtable<Integer, Long>();
 
  /**
   * Create an entry in the hashtable for the given request. It is invoked in 
   * the beginning of the http request processing only if the session size 
   * calculation feature is enabled. 
   * 
   * Existing an entry for the request is mark during processing of the request
   * that the feature is enabled and the size of the session is calculated.  
   *  
   * @param requestId   the id of the request
   */
  public static void startCalculationInfo(int requestId) {
    ConcurrentHashMap<String, Long> value = new ConcurrentHashMap<String, Long>();
    sessionSizes.put(requestId, value);
  } 
  
  /**
   * Calculates and adds the size of the session in give application. The method
   * is invoked every time the request processing leaves the application code in
   * some web application (after request dispatching or at the end of the
   * request). 
   * 
   * Calculation is done only if the there is an entry for this requestId in the
   * sessionSizes hashtable which is notification that the feature is enabled 
   * and the info for this request is collected  
   *  
   * @param info      data holder for the request parameters (as requestId, 
   *                  sessionId, web application name (alias name), session, 
   *                  session attributes, etc).  Minimum required: requestId,
   *                  application/alias
   */  
  public static void addObjectSize(SessionRequestInfo info) {     
    if (sessionSizes.isEmpty()) {
      // OK the session size calculation feature is off
      return;
    }
    
    ConcurrentHashMap<String, Long> value = sessionSizes.get(info.getRequestId());
    if (value == null) { 
      // keep this check because of postpond requests: The request thread starts 
      // several threads and some of them go to WCE (via WCERequestDispatch) and 
      // are postponded there. The main request thread is returned to the client, 
      // the statistics is consolidated and sent to the components (DSR and 
      // http memory status line). When the postpond threads are resumed and 
      // return back they go over the end of the WCERequestDispatcher. This might 
      // add size to the session. If the request is sent back and the statistics 
      // are already reported, the newly reported data should not be stored in 
      // the hashtable because there will be no call to remove it
      // No action are required any more     
      return;
    }
    
    if (info.getSession() == null) {
      if (LOCATION_HTTP_SESSION_SIZE.beInfo()) {              
      Log.traceInfo(LOCATION_HTTP_SESSION_SIZE, "Session size in application [" + 
          info.getAliasName() + "], request [" + info.getRequestId() + 
          "] is [0] bytes (no session)", null);
      }
      // update the hashtable to overwrite the previous results (if any) for this application      
      if (sessionSizes != null && !sessionSizes.isEmpty()) {      
        value.put(info.getAliasName(), 0L);
      }
      return;
    }
 
    if (!info.isSessionValid()) {
      if (LOCATION_HTTP_SESSION_SIZE.beInfo()) {
      Log.traceInfo(LOCATION_HTTP_SESSION_SIZE, "Session size in application [" + 
          info.getAliasName() + "], request [" + info.getRequestId() +
          "] is [0] bytes (session is not valid)", null);
      }
      // update the hashtable to overwrite the previous results (if any) for this application      
      if (sessionSizes != null && !sessionSizes.isEmpty()) {      
        value.put(info.getAliasName(), 0L);
      }
      return;
    }
    
    long maxSessionSize = ServiceContext.getServiceContext().getHttpProperties().getMaxSessionSizeAllowed();    
    
    // =========== next block depends on the object size implementation ===================
    long wholeSize = 0;
    long infraSize = getObjectSizeSSR(info.getSession());    
    long chunksSize = 0;
    if (infraSize < 0) { 
      // in case of error during calculation of the session size infrastructure
      // keep this result as the result of the whole session size and do not
      // calculate the size of the attributes
      wholeSize = infraSize;
    } else if (maxSessionSize > 0 && infraSize > maxSessionSize) {
      // in case the max allowed size calculation is reached return an error 
      // code and do not calculate the size of the attributes      
      wholeSize = SessionSizeManager.ERROR_CODE_MAX_CONFIGURED_SESSION_SIZE_REACHED;  
    } else { //it's time to calculate the session attributes
    	chunksSize = getChunksSize(info.getChunks(), infraSize, info.getRequestId());    
    	if (chunksSize <0) {  
    		// in case of error during calculation of the size of the attributes 
    		// keep this result as the result of the whole session size 
    		wholeSize = chunksSize;
    	} else {
    		wholeSize = infraSize + chunksSize;      
    		if (maxSessionSize > 0 && wholeSize > maxSessionSize) {
    			//if the max allowed session size is reached, set the appropriate error code 
    			wholeSize = SessionSizeManager.ERROR_CODE_MAX_CONFIGURED_SESSION_SIZE_REACHED;
    		}
    	}
    }
    
    if (LOCATION_HTTP_SESSION_SIZE.beInfo()) {
      Log.traceInfo(LOCATION_HTTP_SESSION_SIZE, "Session size of session [" + 
        info.getSessionId() + "] in application [" + info.getAliasName() +
        "], request [" + info.getRequestId() + "] is [" + wholeSize + 
        "] bytes (session infrastrucuture [" + infraSize + "], application data [" +
        chunksSize + "])", null);
    }  
      
    // double the check here because the sessionSizes could be turned to null
    // during this method execution (by switching off the property)
    if (sessionSizes != null && !sessionSizes.isEmpty()) {      
      value.put(info.getAliasName(), wholeSize);
    }    
  } 

  /**
   * Sumups the session size of all sessions touched by the client. 
   * The session sizes per application are stored in the sessionSizes 
   * hashtable. This method removed the entry about this request from 
   * sessionSizes hashtable. Calculates the size of the whole session
   * with suming-up the existing and add this data in the 
   * calculatedSessionSizes hashtable.
   * 
   * The method is invoked at the end of the request processing, just
   * before the size is set to the DSR and/or HTTP memory status line.  
   *  
   * @param requestId  the id of the request
   */
  public static void calculateSessionSize(int requestId) {
    if (sessionSizes.isEmpty()) {
      // OK the session size calculation feature is off
      return;
    }
    
    ConcurrentHashMap<String, Long> value = sessionSizes.remove(requestId);    
    if (value == null) {
      // OK no entry for this request id; could happen if the feature is enabled
      // when the request has already been started
      return;
    }
    
    long maxSessionSize = ServiceContext.getServiceContext().getHttpProperties().getMaxSessionSizeAllowed();
    long sessionSize = 0;
    String logStr = "";    
    Enumeration<String> keys = value.keys();
    while (keys.hasMoreElements()) {
      String aliasName = keys.nextElement();
      long sz = value.get(aliasName);
      
      if (LOCATION_HTTP_SESSION_SIZE.beInfo()) {  
        // construct the trace string only if the trace level is info 
        logStr += "[" + aliasName + "] \t\t [" + sz + "] \r\n";     
      }
      
      if (sz < 0) {      
        // in case of error keep the error code generated before
        sessionSize = sz;      
        break;
      }
      sessionSize += sz;      
      if (maxSessionSize > 0 && sessionSize > maxSessionSize) {
        // in case the max allowed size is reached return an error code and break further calculation
        sessionSize = SessionSizeManager.ERROR_CODE_MAX_CONFIGURED_SESSION_SIZE_REACHED;
        break;
      }
    }
    
    if (LOCATION_HTTP_SESSION_SIZE.beInfo()) {      
      Log.traceInfo(LOCATION_HTTP_SESSION_SIZE, "ASJ.http.000418", "The accumulated " +
      	"session size for request [{0}] is [{1}] bytes, distributed as follows:\r\n" +
      	"Application \t\t Size\r\n{2}", new Object[]{requestId, sessionSize, logStr}, null, null, null);
    }    
    calculatedSessionSizes.put(requestId, sessionSize);    
  }  
  
  /**
   * Returns the size of the session touched by the request with id 
   * <CODE> requestId </CODE> or an error code if the session size calculation 
   * feature is switched off or there is no statistics for this request.
   *  
   * @param requestId   the id of the request
   * @return            the size of the session or an error code (-1) if the 
   *                    session size calculation feature is switched off or 
   *                    there is no statistics for this request
   */ 
  public static long getSessionSize(int requestId) {
    if (calculatedSessionSizes.isEmpty()) {
      return SessionSizeManager.ERROR_CODE_FEATURE_DISABLED;
    }   
    Long value = calculatedSessionSizes.get(requestId);
    if (value == null) {
      return SessionSizeManager.ERROR_CODE_NO_INFO_FOR_THIS_REQUEST;
    } 
    return value;    
  }
  
  
  /**
   * Removes from the statistics data and returns the size of the session 
   * touched by the request with id <CODE> requestId </CODE> or an error code 
   * if the session size calculation feature is switched off or there is no 
   * statistics for this request.
   *  
   * @param requestId   the id of the request
   * @return            the size of the session or an error code (-1) if the 
   *                    session size calculation feature is switched off or 
   *                    there is no statistics for this request
   */ 
  public static long removeSessionSizeInfo(int requestId) {
    if (calculatedSessionSizes.isEmpty()) {
      return SessionSizeManager.ERROR_CODE_FEATURE_DISABLED;
    }
    Long value = calculatedSessionSizes.remove(requestId);
    if (value == null) {
      return SessionSizeManager.ERROR_CODE_NO_INFO_FOR_THIS_REQUEST;
    }
    return value;    
  }
 
  // private methods  
  /**
   * Calculates the size of the hashtable which contains the attributes. It 
   * iterates over the whole hashtable and each key (the string object) size is 
   * calculated as string length and each value size (could be object of any kind)
   * is calcualted via ObjectAnalyzer library
   * 
   * @param chunks    hashtable containing the attributes
   * @param infraSize the size of the infrastructure; it is needed to check if 
   *                  the maximum allowed session size (configured by service 
   *                  properties is reached)
   * @param requestId the id of the request; needed for tracing only
   * @return          the size of the attributes or an error code
   * 
   */
  private static long getChunksSize(Hashtable<String, Object> chunks, long infraSize, int requestId) {
    long chunksResult = 0;
    long maxSessionSize = ServiceContext.getServiceContext().getHttpProperties().getMaxSessionSizeAllowed();
    
    Enumeration<String> chunkNames = chunks.keys();
    while (chunkNames.hasMoreElements()) {
      String chunk = chunkNames.nextElement();            
      chunksResult += 2 * chunk.getBytes().length;       
      long tempResult = getObjectSizeOA(chunks.get(chunk), requestId);
      if (tempResult < 0) {
        //the whole calculation fails
        return tempResult;      
      } else if (maxSessionSize > 0 && infraSize + tempResult > maxSessionSize) {
        // the max allowed session size is reached, set the appropriate error code
        return SessionSizeManager.ERROR_CODE_MAX_CONFIGURED_SESSION_SIZE_REACHED;
      }
      chunksResult += tempResult;
    }
    return chunksResult;
  }
  
  /**
   * Calculated the size of the object using the Object Analyzer lib (OA)
   * via building graph of the references
   * 
   * @param obj the object to calculate size
   * @param requestId  the id of the request (needed for tracing)
   * @return  the size of the object
   */
  private static long getObjectSizeOA(Object obj, int requestId) {    
    if (obj == null) {      
      return 0;         
    }
    Graph graph = null;
    
    try {
      // Init the measurement lib settings from the http properties
      int nodes = ServiceContext.getServiceContext().getHttpProperties().getMaxGraphNodes();
      int level = ServiceContext.getServiceContext().getHttpProperties().getMaxGraphDepth();
      ArrayList<String> filters = ServiceContext.getServiceContext().getHttpProperties().getSessionSizeFilters();
      ClassesFilter clFilters = new ClassesFilter();
      if (filters!=null){
    	  clFilters.addFilters(filters.toArray(new String[filters.size()]));
      }
      
      // apply the settings to the measurement lib
      Graph.setMaxNodesThreshold(nodes);      
      graph = Graph.buildGraph(obj, level, clFilters);
      
      // this trace gives the idea how (and with which params) the object size is calculated
      // disabled by default because there is no lower severity level less than INFO
//      Log.traceError(LOCATION_HTTP_SESSION_SIZE, "Tsvetko", "[Tsvetko AS] Object [" + obj + "] " +
//         "graphNodes [" + graph.getNodeCount() + "] size = " + graph.getSize(), null, null, null);      
      return graph.getSize();       
    } catch (TooManyNodesException e) {      
      if (LOCATION_HTTP_SESSION_SIZE.beWarning()) {
        Log.traceWarning(LOCATION_HTTP_SESSION_SIZE, "ASJ.http.000423", "Error in calculation the size of " +
            "the session attribute [{0}], requestId [{1}]", new Object[]{obj, requestId}, e, null, null, null);
      }
      return SessionSizeManager.ERROR_CODE_CONFIGURED_LIBS_LIMIT_REACHED;      
    } catch (Exception e) {
      if (LOCATION_HTTP_SESSION_SIZE.beWarning()) {
        Log.traceWarning(LOCATION_HTTP_SESSION_SIZE, "ASJ.http.000411", "Error in calculation the size of " +
            "the session attribute [{0}], requestId [{1}]", new Object[]{obj, requestId}, e, null, null, null);
      }
      return SessionSizeManager.ERROR_CODE_UNEXPECTED_ERROR_IN_LIBS; 
    }
  }
  
  /**
   * Calculated the size of the object using the SessionSerializationReport lib (SSR)
   * via serializing the object
   * 
   * @param obj the object to calculate size
   * @return  the size of the object
   */
  private static int getObjectSizeSSR(Object obj) {    
    if (obj == null) {
      return 0;
    }
    SessionSerializationReportFactory reportFactory = SessionSerializationReportFactory.getInstance();
    int shareabilityProblemsFilter = SessionSerializationReport.FULL;
    
    
//    int level = SessionSerializationReportFactory.SUMMARY_LEVEL; 
//    SessionSerializationReport report = reportFactory.createSerializationReport(obj, shareabilityProblemsFilter, level, null);
//    report.refresh();  
//    if (report.getObjectCountForProblem(level) != 0) {
//      Log.traceError(LOCATION_HTTP_REQUEST, "Villy.02", "[Villy AS]          getObjectCountForProblem() = " + report.getObjectCountForProblem(level), null, null, null);
//      Log.traceError(LOCATION_HTTP_REQUEST, "Villy.03", "[Villy AS]             getSessionSizeInBytes() = " + report.getSessionSizeInBytes(), null, null, null);    
//    }
//  
//    level = SessionSerializationReportFactory.CLASS_LEVEL;
//    report = reportFactory.createSerializationReport(obj, shareabilityProblemsFilter, level, null);
//    if (report.getClassesForProblem(level) != null && report.getClassesForProblem(level).size() != 0) {
//      Log.traceError(LOCATION_HTTP_REQUEST, "Villy.04", "[Villy AS]         getClassesForProblem() = " + report.getClassesForProblem(level), null, null, null);
//      Log.traceError(LOCATION_HTTP_REQUEST, "Villy.05", "[Villy AS]             getSessionSizeInBytes() = " + report.getSessionSizeInBytes(), null, null, null);
//      Log.traceError(LOCATION_HTTP_REQUEST, "Villy.06", "[Villy AS]             getClassReport() = " + report.getClassReport(), null, null, null);
//    }
        
    int level = SessionSerializationReportFactory.OBJECT_LEVEL;
    SessionSerializationReport report = reportFactory.createSerializationReport(obj, shareabilityProblemsFilter, level, null);
    // this trace gives more info which classes cannot be serialized; it is 
    // disabled by default because there is no lower severity level less than INFO
//    if (report.getFullObjectReport() != null && report.getFullObjectReport().size() != 0) {
//      Log.traceError(LOCATION_HTTP_SESSION_SIZE, "Villy.07", "[Villy AS]         getFullObjectReport() = " + report.getFullObjectReport(), null, null, null);
//    }
    
    return report.getSessionSizeInBytes();    
  }
}
