/*
 * Copyright (c) 2004-2009 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.server.management;

import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP_MBEANS;

import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.services.httpserver.HttpRuntimeInterface;
import com.sap.engine.services.httpserver.server.Log;
import com.sap.engine.services.httpserver.server.logongroups.LogonGroupsManager;
import com.sap.engine.services.httpserver.server.management.beans.ApplicationAliasOpenMBean;
import com.sap.engine.services.httpserver.server.management.beans.HttpAliasOpenMBean;
import com.sap.engine.services.httpserver.server.management.beans.HttpCacheOpenMBean;
import com.sap.engine.services.httpserver.server.management.beans.HttpHostOpenMBean;
import com.sap.engine.services.httpserver.server.management.beans.LogonGroupOpenMBean;
import com.sap.engine.services.httpserver.server.management.beans.MemoryStatisticOpenMBean;
import com.sap.engine.services.httpserver.server.management.beans.QoSStatisticsOpenMBean;
import com.sap.engine.services.httpserver.server.rcm.ThreadUsageMonitor;
import com.sap.jmx.ObjectNameFactory;

/**
 * @author Nikolai Dokovski
 * @version 1.0
 * ServerMBeanManager is responsible for managing the HttpServer's beans lifecycle.
 */
public class ServerMBeanManager {
	private MBeanServer mbs;
	private static ServerMBeanManager manager;
	private static ApplicationAliasOpenMBean alias_bean = null;
	private static HttpAliasOpenMBean http_alias_bean = null;
	private static HttpCacheOpenMBean http_cache_bean = null;
	private static HttpHostOpenMBean http_host_bean = null;
	private static MemoryStatisticOpenMBean memory_statistic_bean = null;
  private static LogonGroupOpenMBean logonGroupMBean = null;
  private static QoSStatisticsOpenMBean qosStatisticsMBean = null;

	private static ObjectName aab_name;
	private static ObjectName hab_name;
	private static ObjectName hcb_name;
	private static ObjectName hhb_name;
	private static ObjectName msb_name;
  private static ObjectName logonGroupBeanName;  // logon_group_bean
  private static ObjectName qosStatisticsBeanName;  // mbean for qos statistics

	private ServerMBeanManager(MBeanServer obj){
		this.mbs = obj;
	}

	public static ServerMBeanManager initManager(MBeanServer obj){
    	if (manager == null){
    		manager = new ServerMBeanManager(obj);
    	}
    	return manager;
    }

	public void startApplicationAliasMBean(ApplicationServiceContext sc, HttpRuntimeInterface ifc){
		if (alias_bean == null){
			try{
			  alias_bean = new ApplicationAliasOpenMBean();
			}catch(Exception e){
				if (LOCATION_HTTP_MBEANS.beWarning()) {
					Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000294",
					  "ServerMBeanManager.startApplicationAliasMBean(): Cannot create ApplicationAliasOpenMBean.", e, null, null, null);
				}
				return;
			}
			alias_bean.setApplicationServiceContext(sc);
			alias_bean.setHttpRuntimeInterface(ifc);
		}
		try {
			aab_name = ObjectNameFactory.getNameForServerChildPerNode("SAP_J2EECommandsGroupPerNode","http_server_applicationalias",null,null);
     	} catch (MalformedObjectNameException e1) {
 			if (LOCATION_HTTP_MBEANS.beWarning()) {
 				Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000281",
 				  "ServerMBeanManager.startApplicationAliasMBean(): Cannot construct ObjectName", e1, null, null, null);
 				}
     	}
		startMBean(alias_bean,aab_name);
	}

	public void stopApplicationAliasMBean(){
		if (mbs != null && aab_name != null){
			try {
				mbs.unregisterMBean(aab_name);
			} catch (Exception e) {
				if (LOCATION_HTTP_MBEANS.beWarning()) {
					Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000282",
					  "ServerMBeanManager.stopApplicationAliasMBean(): Cannot unregister bean [{0}]", new Object[]{aab_name}, e, null, null, null);
				}
			}
		}
	}

	public void startHttpAliasMBean(HttpRuntimeInterface ifc){
		  if (http_alias_bean == null){
		  	try{
		  		http_alias_bean = new HttpAliasOpenMBean();
			}catch(Exception e){
				if (LOCATION_HTTP_MBEANS.beWarning()) {
					Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000283",
					  "ServerMBeanManager.startHttpAliasMBean(): Cannot create HttpAliasOpenMBean.", e, null, null, null);
				}
				return;
			}
		  	http_alias_bean.setHttpRuntimeInterface(ifc);
		  	try {
				hab_name = ObjectNameFactory.getNameForServerChildPerNode("SAP_J2EECommandsGroupPerNode","http_server_httpalias",null,null);
	     	} catch (MalformedObjectNameException e1) {
	 			if (LOCATION_HTTP_MBEANS.beWarning()) {
	 				Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000284",
	 				  "ServerMBeanManager.startHttpAliasMBean(): Cannot construct ObjectName", e1, null, null, null);
				}
	     	}
			startMBean(http_alias_bean,hab_name);
		  }
	}

	public void stopHttpAliasMBean(){
		if (mbs != null && hab_name != null){
			try {
				mbs.unregisterMBean(hab_name);
			} catch (Exception e) {
				if (LOCATION_HTTP_MBEANS.beWarning()) {
					Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000285",
					  "ServerMBeanManager.stopHttpAliasMBean(): Cannot unregister bean [{0}]", new Object[]{hab_name}, e, null, null, null);
				}
			}

		}
	}

	public void startHttpCacheMBean(HttpRuntimeInterface ifc){
		if (http_cache_bean == null){
			try{
				http_cache_bean = new HttpCacheOpenMBean();
			}catch(Exception e){
				if (LOCATION_HTTP_MBEANS.beWarning()) {
					Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000274", "ServerMBeanManager.startHttpCacheMBean():", e, null, null, null);
				}
				return;
			}
			http_cache_bean.setHttpRuntimeInterface(ifc);
			try {
				hcb_name = ObjectNameFactory.getNameForServerChildPerNode("SAP_J2EECommandsGroupPerNode","http_server_httpcache",null,null);
			} catch (MalformedObjectNameException e1) {
				if (LOCATION_HTTP_MBEANS.beWarning()) {
					Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000286",
					  "ServerMBeanManager.startHttpCacheMBean(): Cannot construct ObjectName", e1, null, null, null);
				}
			}
			startMBean(http_cache_bean,hcb_name);
		}
	}

	public void stopHttpCacheMBean(){
		if (mbs != null && hcb_name != null){
			try {
				mbs.unregisterMBean(hcb_name);
			} catch (Exception e) {
				if (LOCATION_HTTP_MBEANS.beWarning()) {
					Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000287",
					  "ServerMBeanManager.stopHttpCacheMBean(): Cannot unregister bean [{0}]", new Object[]{hcb_name}, e, null, null, null);
				}
			}
		}
	}

	public void startHttpHostMBean(HttpRuntimeInterface ifc){
		if (http_host_bean == null){
			try{
				http_host_bean = new HttpHostOpenMBean();
			}catch(Exception e){
				if (LOCATION_HTTP_MBEANS.beWarning()) {
					Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000288",
					  "ServerMBeanManager.startHttpHostMBean(): Cannot create HttpHostOpenMBean.", e, null, null, null);
				}
				return;
			}
		  	http_host_bean.setHttpRuntimeInterface(ifc);
		  	try {
				hhb_name = ObjectNameFactory.getNameForServerChildPerNode("SAP_J2EECommandsGroupPerNode","http_server_httphost",null,null);
	     	} catch (MalformedObjectNameException e1) {
	 			if (LOCATION_HTTP_MBEANS.beWarning()) {
	 				Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000289",
	 				  "ServerMBeanManager.startHttpHostMBean(): Cannot construct ObjectName", e1, null, null, null);
				}
	     	}
			startMBean(http_host_bean,hhb_name);
		}
	}

	public void stopHttpHostMBean(){
		if (mbs != null && hhb_name != null){
			try {
				mbs.unregisterMBean(hhb_name);
			} catch (Exception e) {
				if (LOCATION_HTTP_MBEANS.beWarning()) {
					Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000290",
					  "ServerMBeanManager.stopHttpHostMBean(): Cannot unregister bean [{0}]", new Object[]{hhb_name}, e, null, null, null);
				}
			}
		}
	}

  public void startMemoryStatisticMBean(ApplicationServiceContext sc,HttpRuntimeInterface ifc){
    if (memory_statistic_bean == null){
      try{
        memory_statistic_bean = new MemoryStatisticOpenMBean();
      }catch(Exception e){
        if (LOCATION_HTTP_MBEANS.beWarning()) {
          Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000291",
            "ServerMBeanManager.startMemoryStatisticMBean(): Cannot create MemoryStatisticOpenMBean.", e, null, null, null);
        }
        return;
      }
      memory_statistic_bean.setApplicationServiceContext(sc);
      memory_statistic_bean.setHttpRuntimeInterface(ifc);
    }
    try {
      msb_name = ObjectNameFactory.getNameForServerChildPerNode("SAP_J2EECommandsGroupPerNode","http_server_memorystatistic",null,null);
      } catch (MalformedObjectNameException e1) {
        if (LOCATION_HTTP_MBEANS.beWarning()) {
          Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000292",
            "ServerMBeanManager.startMemoryStatisticMBean(): Cannot construct ObjectName: ", e1, null, null, null);
        }
      }
    startMBean(memory_statistic_bean,msb_name);
  }

  public void stopMemoryStatisticMBean(){
    if (mbs != null && msb_name != null){
      try {
        mbs.unregisterMBean(msb_name);
      } catch (Exception e) {
        if (LOCATION_HTTP_MBEANS.beWarning()) {
          Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000293",
            "ServerMBeanManager.stopMemoryStatisticMBean: Cannot unregister bean [{0}]", new Object[]{mbs}, e, null, null, null);
        }
      }
    }
  }

  /**
   * Initializes and registers the mbean for the logon group
   *
   * @param logonGroupsManager
   *          logon groups manager
   */
  private void startLogonGroupMBean(LogonGroupsManager logonGroupsManager) {
    if (logonGroupMBean == null) {
      try {
        logonGroupMBean = new LogonGroupOpenMBean();
      } catch (Exception e) {
        if (LOCATION_HTTP_MBEANS.beWarning()) {
          Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000343",
            "ServerMBeanManager.startLogonGroupMBean(): Cannot create LogonGroupOpenMBean.", e, null, null, null);
        }
        return;
      }
      logonGroupMBean.setLogonGroupsManager(logonGroupsManager);
      try {
        logonGroupBeanName = ObjectNameFactory.getNameForServerChildPerNode("SAP_J2EECommandsGroupPerNode", "http_server_logon_group", null, null);
      } catch (MalformedObjectNameException e1) {
        if (LOCATION_HTTP_MBEANS.beWarning()) {
          Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000344",
            "ServerMBeanManager.startLogonGroupMBean(): Cannot construct ObjectName", e1, null, null, null);
        }
      }
      startMBean(logonGroupMBean, logonGroupBeanName);
    }
  }

  /**
   * Unregisters the mbean for the logon group
   */
  private void stopLogonGroupMBean() {
    if (mbs != null && logonGroupBeanName != null) {
      try {
        mbs.unregisterMBean(logonGroupBeanName);
      } catch (Exception e) {
        if (LOCATION_HTTP_MBEANS.beWarning()) {
          Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000345",
            "ServerMBeanManager.stopHttpHostMBean(): Cannot unregister bean [{0}]", new Object[] { logonGroupBeanName }, e, null, null, null);
        }
      }
    }
  }


  /**
   * Initializes and registers the mbean for the qos statistics
   */
  private void startQoSMBean(ThreadUsageMonitor monitor) {
    if (qosStatisticsMBean == null) {
      try {
        qosStatisticsMBean = new QoSStatisticsOpenMBean();
      } catch (Exception e) {
        if (LOCATION_HTTP_MBEANS.beWarning()) {
          Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000379",
            "ServerMBeanManager.startQoSMBean(): Cannot create QoSStatisticsOpenMBean.", e, null, null, null);
        }
        return;
      }
      qosStatisticsMBean.setThreadUsageMonitor(monitor);
      try {
        qosStatisticsBeanName = ObjectNameFactory.getNameForServerChildPerNode("SAP_J2EECommandsGroupPerNode", "http_server_QoSStatistics", null, null);
      } catch (MalformedObjectNameException e1) {
        if (LOCATION_HTTP_MBEANS.beWarning()) {
          //TODO log
          Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000378",
            "ServerMBeanManager.startQoSMBean(): Cannot construct ObjectName", e1, null, null, null);
        }
      }
      startMBean(qosStatisticsMBean, qosStatisticsBeanName);
    }
  }

  /**
   * Unregisters the mbean for the qos statistics
   */
  private void stopQoSMBean() {
    if (mbs != null && qosStatisticsBeanName != null) {
      try {
        mbs.unregisterMBean(qosStatisticsBeanName);
      } catch (Exception e) {
        if (LOCATION_HTTP_MBEANS.beWarning()) {
          Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000380",
            "ServerMBeanManager.stopQoSMBean(): Cannot unregister bean [{0}]", new Object[] { qosStatisticsBeanName }, e, null, null, null);
        }
      }
    }
  }


  private void startMBean(DynamicMBean bean, ObjectName name) {
    try {
      if (LOCATION_HTTP_MBEANS.beInfo()) {
        LOCATION_HTTP_MBEANS.infoT("ServerMBeanManager.startMBean(): Registering MBean " + name);
      }
      Object tt = mbs.registerMBean(bean, name);
    } catch (InstanceAlreadyExistsException e2) {
      if (LOCATION_HTTP_MBEANS.beWarning()) {
        Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000275", "ServerMBeanManager.startMBean():", e2, null, null, null);
      }
    } catch (MBeanRegistrationException e2) {
      if (LOCATION_HTTP_MBEANS.beWarning()) {
        Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000276", "ServerMBeanManager.startMBean():", e2, null, null, null);
      }
    } catch (NotCompliantMBeanException e2) {
      if (LOCATION_HTTP_MBEANS.beWarning()) {
        Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000277", "ServerMBeanManager.startMBean():", e2, null, null, null);
      }
    }
  }

  public void startAll(ApplicationServiceContext sc, HttpRuntimeInterface http, LogonGroupsManager logonGroupsManager, ThreadUsageMonitor threadUsageMonitor) {
    startApplicationAliasMBean(sc, http);
    startHttpAliasMBean(http);
    startHttpCacheMBean(http);
    startHttpHostMBean(http);
    startMemoryStatisticMBean(sc, http);
    startLogonGroupMBean(logonGroupsManager);
    startQoSMBean(threadUsageMonitor);
  }

  public void stopAll() {
    stopApplicationAliasMBean();
    stopHttpAliasMBean();
    stopHttpCacheMBean();
    stopHttpHostMBean();
    stopMemoryStatisticMBean();
    stopLogonGroupMBean();
    stopQoSMBean();
  }
}
