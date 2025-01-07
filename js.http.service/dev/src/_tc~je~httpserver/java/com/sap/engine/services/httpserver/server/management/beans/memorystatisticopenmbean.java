package com.sap.engine.services.httpserver.server.management.beans;

import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP_MBEANS;

import java.util.HashMap;
import java.util.Vector;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.management.openmbean.OpenMBeanConstructorInfoSupport;
import javax.management.openmbean.OpenMBeanInfoSupport;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.OpenMBeanParameterInfo;
import javax.management.openmbean.OpenMBeanParameterInfoSupport;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.services.httpserver.HttpRuntimeInterface;
import com.sap.engine.services.httpserver.server.Log;
import com.sap.engine.services.httpserver.server.memory.IRequestMemoryReportStorage;
import com.sap.engine.services.httpserver.server.memory.impl.RequestMemoryReport;
import com.sap.engine.services.httpserver.server.memory.impl.RequestMemoryReportManager;
import com.sap.engine.services.httpserver.server.sessionsize.SessionSizeManager;
import com.sap.tc.logging.Location;

public class MemoryStatisticOpenMBean  implements DynamicMBean, NotificationBroadcaster {
  
  private ApplicationServiceContext context;
  private HttpRuntimeInterface http;
  
  //DynamicMBean information holder
  private static OpenMBeanInfoSupport mbean_info = null;
  
  //synchronization lock;
  private static Object lock = new Object();
  
  //statistic report composite data
  private static String[]   mr_names;
  private static String[]   mr_descriptions;
  private static OpenType[] mr_types;
  private static CompositeType ctype_mr;
  private static OpenType array_ctype_mr;
  
  //list ids composite data
  private static String[]   ids_names;
  private static String[]   ids_descriptions;
  private static OpenType[] ids_types;
  private static CompositeType ctype_ids;
  private static OpenType array_ctype_ids;
  
  private IRequestMemoryReportStorage memeoryReportStorage = null;
  
  static{
    try{
      //statistic report init
      mr_names = new String[] {"report"};
      mr_descriptions = new String[] {"Report"};
      mr_types = new OpenType[] {SimpleType.STRING};
      ctype_mr = new CompositeType("ctype_mr",
          "Composite Type of get_memory_report operation",
          mr_names,mr_descriptions,mr_types);
      array_ctype_mr = new ArrayType(1,ctype_mr);
      
      //list ids init
      ids_names = new String[] {"ids"};
      ids_descriptions = new String[] {"IDs"};
      ids_types = new OpenType[] {SimpleType.INTEGER};
      ctype_ids = new CompositeType("ctype_ids",
          "Composite Type of list_memory_report_ids operation",
          ids_names,ids_descriptions,ids_types);
      array_ctype_ids = new ArrayType(1,ctype_ids);
      
    } catch (OpenDataException e) {
      if (LOCATION_HTTP_MBEANS.beWarning()) {
        Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000295", "Error in initializing MemoryStatisticOpenMBean", e, null, null, null);
      }
    }
  }
  
  
  public MemoryStatisticOpenMBean(){
    init();
  }
  
  public MemoryStatisticOpenMBean(HttpRuntimeInterface ifc) {
    init();
    this.http = ifc;
  }

  public void setApplicationServiceContext(ApplicationServiceContext ctx){
    this.context  = ctx;
  }
  
  public void setHttpRuntimeInterface(HttpRuntimeInterface ifc){
    this.http = ifc;
  }
  
  public CompositeData[] get_memory_report(Integer report_id){
    CompositeData[] ret_data = new CompositeData[0];
    CompositeData tmp;
    Vector temp = new Vector();
    try {
      String report_value = getMemeoryReportStorage().getReport(report_id);
      Object[] mr_values = new Object[] {report_value}; 
      tmp = new CompositeDataSupport(ctype_mr, mr_names, mr_values);
      temp.add(tmp);
    } catch (OpenDataException e5) {
      if (LOCATION_HTTP_MBEANS.beWarning()) {
        Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000296", "Cannot construct composite data.", e5, null, null, null);
      }
    } catch (Exception e) {
      if (LOCATION_HTTP_MBEANS.beWarning()) {
        Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000297", 
          "Cannot get Request Memory Report for tag [{0}].", new Object[]{report_id}, e, null, null, null);
      }
    }
    if (temp.size() > 0){
      ret_data = (CompositeData[]) temp.toArray(ret_data);
    } 
    return ret_data;
  }
  
  /**
   * Obtains from the memory statistics report the session size value.
   *  
   * @param report_id
   * @return
   */
  public long getSessionSize(Integer report_id){
	  long sessionSize = SessionSizeManager.ERROR_CODE_FEATURE_DISABLED;
	  try {
		  sessionSize = getMemeoryReportStorage().getSessionSizeFromReport(report_id);
	  }catch (Exception e){
		  //This error code indicates that no report is stored for the given id.
		  sessionSize=SessionSizeManager.ERROR_CODE_NO_INFO_FOR_THIS_REQUEST;
		  if (LOCATION_HTTP_MBEANS.beWarning()) {
		        Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000413", 
		          "Cannot obtain the calculated session size from the Request Memory Report with tag [{0}].", new Object[]{report_id}, e, null, null, null);
		      }
	  }
	  return sessionSize;
  }
  
  public CompositeData [] list_memeory_reports_ids() {
    CompositeData[] ret_data = new CompositeData[0];
    Vector temp = new Vector();
    try {
      Object [] listIds = getMemeoryReportStorage().getReportIds();
      for (int i=0; i < listIds.length; i++) {
        Object[] values = new Object[] {listIds[i]};
        temp.add(new CompositeDataSupport(ctype_ids, ids_names, values));
      }
    } catch (OpenDataException e1) {
      if (LOCATION_HTTP_MBEANS.beWarning()) {
        Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000298", "Can not construct composite type for list Memory Report IDs.", e1, null, null, null);
      }
    } 
    if (temp.size() > 0){
      ret_data = (CompositeData[]) temp.toArray(ret_data);
    } 
    return ret_data;
  }
  
  private IRequestMemoryReportStorage getMemeoryReportStorage() {
    if (memeoryReportStorage == null) {
      memeoryReportStorage = RequestMemoryReportManager.getInstance().getRequestMemeoryReportStorage();
    }
    return memeoryReportStorage;
  }
  
  public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
    throw new AttributeNotFoundException("No such attribute.");
  }

  public AttributeList getAttributes(String[] attributes) {
    return null;
  }

  public MBeanInfo getMBeanInfo() {
    return mbean_info;
  }

  public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
    synchronized(lock){
      if (actionName == null) {
        throw new RuntimeOperationsException(new IllegalArgumentException("Operation name cannot be null"), 
        "Cannot call invoke with null operation name on MemoryStatisticOpenMBean");
      }else if (actionName.equals("get_memory_report")){
        checkParameter(params); 
        return get_memory_report((Integer)params[0]);
      }else if (actionName.equals("list_memory_report_ids")){
        return list_memeory_reports_ids();
      }else if (actionName.equals("get_session_size")){
    	return getSessionSize((Integer)params[0]);
      }
      return null;
    }
  }

  public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
    throw new AttributeNotFoundException("No such attribute.");
    
  }

  public AttributeList setAttributes(AttributeList attributes) {
    return null;
  }

  public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws IllegalArgumentException {
    
  }

  public MBeanNotificationInfo[] getNotificationInfo() {
    return null;
  }

  public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
    
  }

  /**
   * Constructs MBeanInfo for ApplicationAliasOpenMBean.
   * @throws OpenDataException
   *  
   */
  private void init(){
    if (mbean_info != null){
      return;
    }
    
    //Building OpenMBeanInfo
    OpenMBeanAttributeInfoSupport[]   attributes    = new OpenMBeanAttributeInfoSupport[0];
    OpenMBeanConstructorInfoSupport[] constructors  = new OpenMBeanConstructorInfoSupport[1];
    OpenMBeanOperationInfoSupport[]   operations    = new OpenMBeanOperationInfoSupport[3];
    MBeanNotificationInfo       []    notifications = new MBeanNotificationInfo[0];
    
    //MBean constructor
    constructors[0] = new OpenMBeanConstructorInfoSupport("MemoryStatisticOpenMBean",
            "Constructs a MemoryStatisticOpenMBean instance.",
            new OpenMBeanParameterInfoSupport[0]);
    
    //Parameters for get_memory_report operation
    OpenMBeanParameterInfo[] params_gmr = new OpenMBeanParameterInfoSupport[1];
    params_gmr[0] = new OpenMBeanParameterInfoSupport("report_id","report id",SimpleType.INTEGER);
    
    operations[0] = new OpenMBeanOperationInfoSupport("get_memory_report",
        "get memory report",
        params_gmr,
        SimpleType.STRING,
        MBeanOperationInfo.INFO);
    
    OpenMBeanParameterInfo[] params_ids = new OpenMBeanParameterInfoSupport[0];
    operations[1] = new OpenMBeanOperationInfoSupport("list_memory_report_ids",
        "list memory report ids",
        params_ids,
        SimpleType.INTEGER,
        MBeanOperationInfo.INFO);
    
    //init parameters needed for the session_size operation
    OpenMBeanParameterInfo[] ss_param = new OpenMBeanParameterInfoSupport[1];
    ss_param[0] = new OpenMBeanParameterInfoSupport("report_id","report id",SimpleType.INTEGER);
    //declare session size operation
    operations[2] = new OpenMBeanOperationInfoSupport("get_session_size",
        "dispays the approximate http session size per request",
        ss_param,
        SimpleType.LONG,
        MBeanOperationInfo.INFO);
    
    mbean_info = new OpenMBeanInfoSupport(this.getClass().getName(),
           "Memory Statistic Open MBean",
           attributes,
           constructors,
           operations,
           notifications);
    
  }
  
  private void checkParameter(Object[] params) throws RuntimeOperationsException {
    if (params.length != 1){
      throw new RuntimeOperationsException(new IllegalArgumentException("Illegal number of parameters"), 
      "Cannot invoke memory report mbean operation");
    }
    if (params[0] != null && !(params[0] instanceof Integer)){
      throw new RuntimeOperationsException(new IllegalArgumentException("Illegal type of the first parameter."+
          "The type is:"+params[0].getClass().getName()+". Should be java.lang.Integer"), 
      "Cannot invoke memory report mbean operation");
    }
  }

}
