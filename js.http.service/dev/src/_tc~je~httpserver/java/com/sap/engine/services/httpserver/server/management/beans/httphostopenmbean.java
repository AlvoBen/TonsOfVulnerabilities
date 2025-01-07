/*
 * Copyright (c) 2004 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.server.management.beans;

import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP_MBEANS;

import java.rmi.RemoteException;
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

import com.sap.engine.services.httpserver.HostPropertiesRuntimeInterface;
import com.sap.engine.services.httpserver.HttpRuntimeInterface;
import com.sap.engine.services.httpserver.server.Log;

/**
 * @author Nikolai Dokovski
 * @version 1.0
 *
 * HttpHostOpenMBean provides operations found in host telnet command
 * In general, the HttpHostOpenMBean manages the host property.
 */
public class HttpHostOpenMBean implements DynamicMBean, NotificationBroadcaster {
	private HttpRuntimeInterface http;

	private static MBeanInfo mbean_info;

	private static 	HttpAliasOpenMBean http_host_bean = null;
	private static  ApplicationAliasOpenMBean alias_bean  = null;

	private static Object lock = new Object();

	private static String[] lha_names;
	private static String[] lha_descriptions;
	private static OpenType[] lha_types;
	private static CompositeType ctype_lha;
	private static OpenType array_ctype_lha;

	private static String[]   laa_names;
	private static String[]   laa_descriptions;
	private static OpenType[] laa_types;
	private static CompositeType ctype_laa;
	private static OpenType array_ctype_laa;

	//list_host
	private static String [] lh_names;
	private static String [] lh_description;
	private static CompositeType ctype_lh;
	private static OpenType[] lh_types;
	private static OpenType array_ctype_lh;

	static{
		try {
			//host aliases
			lha_names = new String[] {"alias","path"};
			lha_descriptions = new String[] {"Http Alias Name","Local Path"};
			lha_types = new OpenType[] {SimpleType.STRING,SimpleType.STRING};
			ctype_lha = new CompositeType("ctype_lha",
					"Composite Type of list_http_alias operation",
					lha_names,lha_descriptions,lha_types);
			array_ctype_lha = new ArrayType(1,ctype_lha);
			//application aliases
			laa_names = new String[] {"alias","status"};
			laa_descriptions = new String[] {"Alias Name","Alias Status"};
			laa_types = new OpenType[] {SimpleType.STRING,SimpleType.STRING};
			ctype_laa = new CompositeType("ctype_laas",
					"Composite Type of list_application_alias operation",
					laa_names,laa_descriptions,laa_types);
			array_ctype_laa = new ArrayType(1,ctype_laa);


			//list_host
			lh_names = new String[] {"host","root","start_page","log_enabled","cache_enabled",
					"dirlist_enabled","keep_alive_enabled",	"http_alias","app_alias"};
			lh_description = new String [] {"host name","root","start page","log enbabled","cache disabled","dir list enabled",
					"keep alive enabled","http aliases","application aliases"};
			lh_types = new OpenType[] {SimpleType.STRING,SimpleType.STRING,SimpleType.STRING,
					SimpleType.BOOLEAN,SimpleType.BOOLEAN,SimpleType.BOOLEAN,SimpleType.BOOLEAN,
					array_ctype_lha,array_ctype_laa};
			ctype_lh = new CompositeType("ctype_lh",
					"Composite Type of list_host operation",
					lh_names,lh_description,lh_types);
			array_ctype_lh = new ArrayType(1,ctype_lh);
		} catch (OpenDataException e) {
			if (LOCATION_HTTP_MBEANS.beWarning()) {
				Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000256", "Error in initializing HttpHostOpenMBean", e, null, null, null);
			}
		}
	}

	public HttpHostOpenMBean(){
		init();
	}

	/**
	 * list_host
	 * Lists all virtual hosts properties. Virtual host name can be used to filter the
	 * result of the operation.
	 *
	 * @param name - virtual host name
	 *
	 * @return array of CompositeData  elements. Each elements has following structure
	 * <pre>
	 * 		java.lang.String  host - name
	 * 		java.lang.String  root - root directory
	 * 		java.lang.String  start_page -  start page
	 *      java.lang.Boolean log_enabled - log status
	 * 		java.lang.Boolean cache_enabled - cache status
	 * 		java.lang.Boolean dirlist_enabled - directory listing status
	 * 	    java.lang.Boolean keep_alive_enabled - keep alive status
	 * 		javax.management.CompositeData[] http_alias - http aliases
	 *      javax.management.CompositeData[] app_alias - application aliases
	 * </pre>
	 *
	 * 		each "http_alias" element has following structure
	 * <pre>
	 * 		java.lang.String  alias - Http Alias Name
	 *		java.lang.String  path  - Http Path
	 * </pre>
	 *
	 * 		each "app_alias" element has following structure
	 * <pre>
	 * 		java.lang.String  alias - Alias Name
	 *		java.lang.String  status  - Alias Status
	 * </pre>
	 *

	 */
	public CompositeData[] list_host(String name){
		CompositeData[] ret_data = new CompositeData[0];
		HostPropertiesRuntimeInterface[] hprifc = null;
		Vector temp = new Vector();

		if (name != null && name.length() >0){
			hprifc = new HostPropertiesRuntimeInterface [] {findHostPropertiesRuntimeInterface(name)};
			if (hprifc == null || hprifc[0] == null){
				return ret_data;
			}
		} else {
			try {
				hprifc = http.getAllHostsTemp();
			} catch (RemoteException e) {
				if (LOCATION_HTTP_MBEANS.beWarning()) {
					Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000278", 
					  "HttpHostOpenMBean.list_host(): Can not obtain host properties runtime interface.", e, null, null, null);
				}
				return ret_data;
			}
		}

		try {
			for (int indx = 0;indx < hprifc.length;indx++){
				String hname = hprifc[indx].getHostName();
				String root = hprifc[indx].getRootDir();
				String spage = hprifc[indx].getStartPage();
				Boolean le = new Boolean(hprifc[indx].isLogEnabled());
				Boolean dle = new Boolean(hprifc[indx].isList());
				Boolean ce = new Boolean(hprifc[indx].isUseCache());
				Boolean kae = new Boolean(hprifc[indx].isKeepAliveEnabled());
				CompositeData[] aa = getApplicationAliases(hprifc[indx]);
				CompositeData[] ha = getHttpAliases(hprifc[indx]);
				Object[] lh_value = new Object[]{hname,root,spage,le,ce,dle,kae,ha,aa};
				temp.add(new CompositeDataSupport(ctype_lh,lh_names,lh_value));
			}
		} catch (RemoteException e) {
			if (LOCATION_HTTP_MBEANS.beWarning()) {
				Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000279", 
				  "HttpHostOpenMBean.list_host(): Can not get host properties.", e, null, null, null);
			}
		} catch (OpenDataException e) {
			if (LOCATION_HTTP_MBEANS.beWarning()) {
				Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000280", 
				  "HttpHostOpenMBean.list_host(): Can not construct composite type for host operation.", e, null, null, null);
			}
		}

		if (temp.size() > 0 ){
			ret_data = (CompositeData []) temp.toArray(ret_data);
		}

		return ret_data;
	}

	/**
	 * add_host
	 * Adds virtual host
	 * @param host_name - virtual host name
	 * @return status. Status can be "invoked","success","error"
	 */
	public String add_host(String host_name){
		String ret_dat = "invoked";
		HostPropertiesRuntimeInterface[] hprifc = null;

			hprifc = new HostPropertiesRuntimeInterface [] {findHostPropertiesRuntimeInterface(host_name)};
			if (hprifc == null || hprifc[0] == null){
				try {
					http.createHost(host_name);
					ret_dat = "success";
				} catch (Exception e) {
					if (LOCATION_HTTP_MBEANS.beWarning()) {
						Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000260", "HttpHostOpenMBean.add_host()", e, null, null, null);
						
					}
					ret_dat = "error";
				}
			}
		return ret_dat;
	}

	/**
	 * remove_host
	 * Removes virtual host
	 * @param host_name - virtual host name
	 * @return status. Status can be "invoked","success","error"
	 */
	public String remove_host(String host_name){
		String ret_dat = "invoked";
		HostPropertiesRuntimeInterface[] hprifc = null;

			hprifc = new HostPropertiesRuntimeInterface [] {findHostPropertiesRuntimeInterface(host_name)};
			if (hprifc != null || hprifc[0] != null){
				try {
					http.removeHost(host_name);
					ret_dat = "success";
				} catch (Exception e) {
					if (LOCATION_HTTP_MBEANS.beWarning()) {
						Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000269", "HttpHostOpenMBean.remove_host():", e, null, null, null);
					}
					ret_dat = "error";
				}
			}
		return ret_dat;
	}

	/**
	 * set_host_root
	 * Sets root directory of virtual host.
	 * @param host_name - virtual host name
	 * @return status. Status can be "invoked","success","error"
	 */
	public String set_host_root(String host_name,String root){
		String ret_dat = "invoked";
		HostPropertiesRuntimeInterface[] hprifc = null;

			hprifc = new HostPropertiesRuntimeInterface [] {findHostPropertiesRuntimeInterface(host_name)};
			if (hprifc != null || hprifc[0] != null){
				try {
					if (root != null){
						hprifc[0].setRootDir(root);
						ret_dat = "success";
					}else{
						if (LOCATION_HTTP_MBEANS.beWarning()) {
							Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000270", "HttpHostOpenMBean.set_host_root(): Bad root parameter.", null, null, null);
						}
					}
				} catch (Exception e) {
					if (LOCATION_HTTP_MBEANS.beWarning()) {
						Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000271", "HttpHostOpenMBean.set_host_root():", e, null, null, null);
					}
					ret_dat = "error";
				}
			}
		return ret_dat;
	}

	/**
	 * set_host_startpage
	 * Sets start page of virtual host.
	 * @param host_name - virtual host name
	 * @return status. Status can be "invoked","success","error"
	 */
	public String set_host_startpage(String host_name,String startpage){
		String ret_dat = "invoked";
		HostPropertiesRuntimeInterface[] hprifc = null;

			hprifc = new HostPropertiesRuntimeInterface [] {findHostPropertiesRuntimeInterface(host_name)};
			if (hprifc != null || hprifc[0] != null){
				try {
					if (startpage != null){
						hprifc[0].setStartPage(startpage);
						ret_dat = "success";
					}else{
						if (LOCATION_HTTP_MBEANS.beWarning()) {
							Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000272", "HttpHostOpenMBean.set_host_startpage(): Bad start page parameter.", null, null, null);
						}
					}
				} catch (Exception e) {
					if (LOCATION_HTTP_MBEANS.beWarning()) {
						Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000273", "HttpHostOpenMBean.set_host_startpage():", e, null, null, null);
					}
					ret_dat = "error";
				}
			}
		return ret_dat;
	}

	/**
	 * enable_host_log
	 * Enables  virtual host logging.
	 * @param host_name - virtual host name
	 * @return status. Status can be "invoked","success","error"
	 */
	public String enable_host_log(String host_name){
		String ret_dat = "invoked";
		HostPropertiesRuntimeInterface[] hprifc = null;

			hprifc = new HostPropertiesRuntimeInterface [] {findHostPropertiesRuntimeInterface(host_name)};
			if (hprifc != null || hprifc[0] != null){
				try {
					hprifc[0].setLogEnabled(true);
					ret_dat = "success";
				} catch (Exception e) {
					if (LOCATION_HTTP_MBEANS.beWarning()) {
						Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000268", "HttpHostOpenMBean.enable_host_log():", e, null, null, null);
					}
					ret_dat = "error";
				}
			}
		return ret_dat;
	}

	/**
	 * disable_host_log
	 * Disables  virtual host logging.
	 * @param host_name - virtual host name
	 * @return status. Status can be "invoked","success","error"
	 */
	public String disable_host_log(String host_name){
		String ret_dat = "invoked";
		HostPropertiesRuntimeInterface[] hprifc = null;

			hprifc = new HostPropertiesRuntimeInterface [] {findHostPropertiesRuntimeInterface(host_name)};
			if (hprifc != null || hprifc[0] != null){
				try {
					hprifc[0].setLogEnabled(false);
					ret_dat = "success";
				} catch (Exception e) {
					if (LOCATION_HTTP_MBEANS.beWarning()) {
						Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000264", "HttpHostOpenMBean.disable_host_log():", e, null, null, null);
					}
					ret_dat = "error";
				}
			}
		return ret_dat;
	}

	/**
	 * enable_host_cache
	 * Enables  virtual host caching.
	 * @param host_name - virtual host name
	 * @return status. Status can be "invoked","success","error"
	 */
	public String enable_host_cache(String host_name){
		String ret_dat = "invoked";
		HostPropertiesRuntimeInterface[] hprifc = null;

			hprifc = new HostPropertiesRuntimeInterface [] {findHostPropertiesRuntimeInterface(host_name)};
			if (hprifc != null || hprifc[0] != null){
				try {
					hprifc[0].setUseCache(true);
					ret_dat = "success";
				} catch (Exception e) {
					if (LOCATION_HTTP_MBEANS.beWarning()) {
						Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000265", "HttpHostOpenMBean.enable_host_cache():", e, null, null, null);
					}
					ret_dat = "error";
				}
			}
		return ret_dat;
	}

	/**
	 * disable_host_cache
	 * Disable  virtual host caching.
	 * @param host_name - virtual host name
	 * @return status. Status can be "invoked","success","error"
	 */
	public String disable_host_cache(String host_name){
		String ret_dat = "invoked";
		HostPropertiesRuntimeInterface[] hprifc = null;

			hprifc = new HostPropertiesRuntimeInterface [] {findHostPropertiesRuntimeInterface(host_name)};
			if (hprifc != null || hprifc[0] != null){
				try {
					hprifc[0].setUseCache(false);
					ret_dat = "success";
				} catch (Exception e) {
					if (LOCATION_HTTP_MBEANS.beWarning()) {
						Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000261", "HttpHostOpenMBean.disable_host_cache():", e, null, null, null);
					}
					ret_dat = "error";
				}
			}
		return ret_dat;
	}

	/**
	 * enable_host_dirlist
	 * Enables  virtual host directory listing.
	 * @param host_name - virtual host name
	 * @return status. Status can be "invoked","success","error"
	 */
	public String enable_host_dirlist(String host_name){
		String ret_dat = "invoked";
		HostPropertiesRuntimeInterface[] hprifc = null;

			hprifc = new HostPropertiesRuntimeInterface [] {findHostPropertiesRuntimeInterface(host_name)};
			if (hprifc != null || hprifc[0] != null){
				try {
					hprifc[0].setList(true);
					ret_dat = "success";
				} catch (Exception e) {
					if (LOCATION_HTTP_MBEANS.beWarning()) {
						Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000266", "HttpHostOpenMBean.enable_host_dirlist():", e, null, null, null);
					}
					ret_dat = "error";
				}
			}
		return ret_dat;
	}

	/**
	 * disable_host_dirlist
	 * Disable  virtual host directory listing.
	 * @param host_name - virtual host name
	 * @return status. Status can be "invoked","success","error"
	 */
	public String disable_host_dirlist(String host_name){
		String ret_dat = "invoked";
		HostPropertiesRuntimeInterface[] hprifc = null;

			hprifc = new HostPropertiesRuntimeInterface [] {findHostPropertiesRuntimeInterface(host_name)};
			if (hprifc != null || hprifc[0] != null){
				try {
					hprifc[0].setList(false);
					ret_dat = "success";
				} catch (Exception e) {
					if (LOCATION_HTTP_MBEANS.beWarning()) {
						Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000262", "HttpHostOpenMBean.disable_host_dirlist():", e, null, null, null);
					}
					ret_dat = "error";
				}
			}
		return ret_dat;
	}

	/**
	 * enable_host_keepalive
	 * Enables  virtual host keep alive.
	 * @param host_name - virtual host name
	 * @return status. Status can be "invoked","success","error"
	 */
	public String enable_host_keepalive(String host_name){
		String ret_dat = "invoked";
		HostPropertiesRuntimeInterface[] hprifc = null;

			hprifc = new HostPropertiesRuntimeInterface [] {findHostPropertiesRuntimeInterface(host_name)};
			if (hprifc != null || hprifc[0] != null){
				try {
					hprifc[0].setKeepAliveEnabled(true);
					ret_dat = "success";
				} catch (Exception e) {
					if (LOCATION_HTTP_MBEANS.beWarning()) {
						Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000267", "HttpHostOpenMBean.enable_host_keepalive():", e, null, null, null);
					}
					ret_dat = "error";
				}
			}
		return ret_dat;
	}

	/**
	 * disable_host_keepalive
	 * Disable  virtual host directory keep alive.
	 * @param host_name - virtual host name
	 * @return status. Status can be "invoked","success","error"
	 */
	public String disable_host_keepalive(String host_name){
		String ret_dat = "invoked";
		HostPropertiesRuntimeInterface[] hprifc = null;

			hprifc = new HostPropertiesRuntimeInterface [] {findHostPropertiesRuntimeInterface(host_name)};
			if (hprifc != null || hprifc[0] != null){
				try {
					hprifc[0].setKeepAliveEnabled(false);
					ret_dat = "success";
				} catch (Exception e) {
					if (LOCATION_HTTP_MBEANS.beWarning()) {
						Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000263", "HttpHostOpenMBean.disable_host_keepalive():", e, null, null, null);
					}
					ret_dat = "error";
				}
			}
		return ret_dat;
	}

	private CompositeData [] getHttpAliases(HostPropertiesRuntimeInterface hp) throws RemoteException, OpenDataException {
		if (http_host_bean != null){
			return http_host_bean.list_http_alias(hp.getHostName(),null);
		}else{
			return new CompositeData[0];
		}
	}


	private CompositeData[] getApplicationAliases(HostPropertiesRuntimeInterface hp) throws RemoteException {
		if (alias_bean != null){
		  return alias_bean.list_application_alias(hp.getHostName(),null);
		}else {
			return new CompositeData[0];
		}

	}



	/* HttpHostOpenMBean does not provide attributes. The getter method returns null.
	 * @see javax.management.DynamicMBean#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String arg0) throws AttributeNotFoundException,
			MBeanException, ReflectionException {
		throw new AttributeNotFoundException("No such attribute.");
	}

	/* HttpHostOpenMBean does not provide attributes. The setter method has no impact.
	 * @see javax.management.DynamicMBean#setAttribute(javax.management.Attribute)
	 */
	public void setAttribute(Attribute arg0) throws AttributeNotFoundException,
			InvalidAttributeValueException, MBeanException, ReflectionException {
		throw new AttributeNotFoundException("No such attribute.");
	}

	/* HttpHostOpenMBean does not provide attributes. The getter method returns null.
	 * @see javax.management.DynamicMBean#getAttributes(java.lang.String[])
	 */
	public AttributeList getAttributes(String[] arg0) {
		return null;
	}

	/* HttpHostOpenMBean does not provide attributes. The setter method has no impact.
	 * @see javax.management.DynamicMBean#setAttributes(javax.management.AttributeList)
	 */
	public AttributeList setAttributes(AttributeList arg0) {
		return null;
	}

	/*
	 * @see javax.management.DynamicMBean#invoke(java.lang.String, java.lang.Object[], java.lang.String[])
	 */
	public Object invoke(String op_name, Object[] params, String[] signature){

		synchronized(lock){
			if (op_name == null) {
				throw new RuntimeOperationsException(new IllegalArgumentException("Operation name cannot be null"),
				"Cannot call invoke with null operation name on HttpHostOpenMBean");
			}else if (op_name.equals("list_host")){
				checkSingleParam(params,true);
				return list_host((String)params[0]);
			}else if (op_name.equals("add_host")){
				checkSingleParam(params,false);
				return add_host((String)params[0]);
			}else if (op_name.equals("remove_host")){
				checkSingleParam(params,false);
				return remove_host((String)params[0]);
			}else if (op_name.equals("set_host_root")){
				checkDoubleParam(params);
				return set_host_root((String)params[0],(String)params[1]);
			}else if (op_name.equals("set_host_startpage")){
				checkDoubleParam(params);
				return set_host_startpage((String)params[0],(String)params[1]);
			}else if (op_name.equals("enable_host_log")){
				checkSingleParam(params,true);
				return enable_host_log((String)params[0]);
			}else if (op_name.equals("disable_host_log")){
				checkSingleParam(params,true);
				return disable_host_log((String)params[0]);
			}else if (op_name.equals("enable_host_cache")){
				checkSingleParam(params,true);
				return enable_host_cache((String)params[0]);
			}else if (op_name.equals("disable_host_cache")){
				checkSingleParam(params,true);
				return disable_host_cache((String)params[0]);
			}else if (op_name.equals("enable_host_keepalive")){
				checkSingleParam(params,true);
				return enable_host_keepalive((String)params[0]);
			}else if (op_name.equals("disable_host_keepalive")){
				checkSingleParam(params,true);
				return disable_host_keepalive((String)params[0]);
			}else if (op_name.equals("enable_host_dirlist")){
				checkSingleParam(params,true);
				return enable_host_dirlist((String)params[0]);
			}else if (op_name.equals("disable_host_dirlist")){
				checkSingleParam(params,true);
				return disable_host_dirlist((String)params[0]);
			}
			return null;
		}
	}

	private void checkSingleParam(Object[] params,boolean null_allowed) throws RuntimeOperationsException {
		if (params.length != 1){
			throw new RuntimeOperationsException(new IllegalArgumentException("Illegal number of parameters"),
			"Cannot invoke application alias mbean operation");
		}

		if (params[0] != null && !(params[0] instanceof String)){
				throw new RuntimeOperationsException(new IllegalArgumentException("Illegal type of the first parameter."+
					"The type is:"+params[0].getClass().getName()+". Should be java.lang.String"),
					"Cannot invoke application alias mbean operation");
		}
		if (!null_allowed && params[0] == null){
			throw new RuntimeOperationsException(new IllegalArgumentException("Illegal type of the first parameter."+
					"The parameter can not be null"),
					"Cannot invoke application alias mbean operation");
		}
	}

	private void checkDoubleParam(Object[] params) throws RuntimeOperationsException {
		if (params.length != 2){
			throw new RuntimeOperationsException(new IllegalArgumentException("Illegal number of parameters"),
			"Cannot invoke application alias mbean operation");
		}
		if (params[0] != null && !(params[0] instanceof String)){
			throw new RuntimeOperationsException(new IllegalArgumentException("Illegal type of the first parameter."+
					"The type is:"+params[0].getClass().getName()+". Should be java.lang.String"),
			"Cannot invoke application alias mbean operation");
		}
		if (params[1]!= null && !(params[1] instanceof String)){
			throw new RuntimeOperationsException(new IllegalArgumentException("Illegal type of the second parameter."+
					"The type is:"+params[1].getClass().getName()+". Should be java.lang.String"),
			"Cannot invoke application alias mbean operation");
		}
		if (params[1] == null){
			throw new RuntimeOperationsException(new IllegalArgumentException("Illegal type of the second parameter."+
				"The parameter can not be null"),
			"Cannot invoke application alias mbean operation");

		}
	}

	/*
	 * @see javax.management.DynamicMBean#getMBeanInfo()
	 */
	public MBeanInfo getMBeanInfo() {
		return mbean_info;
	}

	/* HttpHostOpenMBean is not a notification broadcaster. Therefore this method does nothing.
	 * @see javax.management.NotificationBroadcaster#addNotificationListener(javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
	 */
	public void addNotificationListener(NotificationListener arg0,
			NotificationFilter arg1, Object arg2)
			throws IllegalArgumentException {
	}

	/* HttpHostOpenMBean is not a notification broadcaster. Therefore this method does nothing.
	 * @see javax.management.NotificationBroadcaster#removeNotificationListener(javax.management.NotificationListener)
	 */
	public void removeNotificationListener(NotificationListener arg0)
			throws ListenerNotFoundException {
	}

	/* HttpHostOpenMBean is not a notification broadcaster. Therefore this method returns null.
	 * @see javax.management.NotificationBroadcaster#getNotificationInfo()
	 */
	public MBeanNotificationInfo[] getNotificationInfo() {
		return null;
	}

	private void init(){
		if (mbean_info != null){
			return;
		}
		//Building OpenMBeanInfo
		OpenMBeanAttributeInfoSupport[]   attributes    = new OpenMBeanAttributeInfoSupport[0];
		OpenMBeanConstructorInfoSupport[] constructors  = new OpenMBeanConstructorInfoSupport[1];
		OpenMBeanOperationInfoSupport[]   operations    = new OpenMBeanOperationInfoSupport[13];
		MBeanNotificationInfo       []    notifications = new MBeanNotificationInfo[0];

		//MBean constructor
		constructors[0] = new OpenMBeanConstructorInfoSupport("HttpHostOpenMBean",
			      "Constructs a HttpHostOpenMBean instance.",
			      new OpenMBeanParameterInfoSupport[0]);

		//Parameters for list_host,add_host,remove_host operations
		OpenMBeanParameterInfo[] params_lh = new OpenMBeanParameterInfoSupport[1];
		params_lh[0] = new OpenMBeanParameterInfoSupport("host","Host name",SimpleType.STRING);

		//Parameters for set_host_root operation
		OpenMBeanParameterInfo[] params_shr = new OpenMBeanParameterInfoSupport[2];
		params_shr[0] = new OpenMBeanParameterInfoSupport("host","Host name",SimpleType.STRING);
		params_shr[1] = new OpenMBeanParameterInfoSupport("root","Host name",SimpleType.STRING);

		//Parameters for set_host_startpage operation
		OpenMBeanParameterInfo[] params_shsp = new OpenMBeanParameterInfoSupport[2];
		params_shsp[0] = new OpenMBeanParameterInfoSupport("host","Host name",SimpleType.STRING);
		params_shsp[1] = new OpenMBeanParameterInfoSupport("start_page","Host Start Page",SimpleType.STRING);

		operations[0] = new OpenMBeanOperationInfoSupport("list_host",
				"lists http hosts",
				params_lh,
				array_ctype_lh,
				MBeanOperationInfo.INFO);

		operations[1] = new OpenMBeanOperationInfoSupport("add_host",
				"add http virtual host",
				params_lh,
				SimpleType.STRING,
				MBeanOperationInfo.ACTION);

		operations[2] = new OpenMBeanOperationInfoSupport("remove_host",
				"removes http virtual hosts",
				params_lh,
				SimpleType.STRING,
				MBeanOperationInfo.ACTION);

		operations[3] = new OpenMBeanOperationInfoSupport("set_host_root",
				"sets http virtual host root directory",
				params_shr,
				SimpleType.STRING,
				MBeanOperationInfo.ACTION);

		operations[4] = new OpenMBeanOperationInfoSupport("set_host_startpage",
				"sets http virtual host start page",
				params_shsp,
				SimpleType.STRING,
				MBeanOperationInfo.ACTION);

		operations[5] = new OpenMBeanOperationInfoSupport("enable_host_log",
				"enable host log",
				params_lh,
				SimpleType.STRING,
				MBeanOperationInfo.ACTION);

		operations[6] = new OpenMBeanOperationInfoSupport("disable_host_log",
				"disable host log",
				params_lh,
				SimpleType.STRING,
				MBeanOperationInfo.ACTION);

		operations[7] = new OpenMBeanOperationInfoSupport("enable_host_cache",
				"enable host cache",
				params_lh,
				SimpleType.STRING,
				MBeanOperationInfo.ACTION);

		operations[8] = new OpenMBeanOperationInfoSupport("disable_host_cache",
				"disable host cache",
				params_lh,
				SimpleType.STRING,
				MBeanOperationInfo.ACTION);

		operations[9] = new OpenMBeanOperationInfoSupport("enable_host_dirlist",
				"enable host directory listing",
				params_lh,
				SimpleType.STRING,
				MBeanOperationInfo.ACTION);

		operations[10] = new OpenMBeanOperationInfoSupport("disable_host_dirlist",
				"disable host directory listing",
				params_lh,
				SimpleType.STRING,
				MBeanOperationInfo.ACTION);

		operations[11] = new OpenMBeanOperationInfoSupport("enable_host_keepalive",
				"enable host keep alive",
				params_lh,
				SimpleType.STRING,
				MBeanOperationInfo.ACTION);

		operations[12] = new OpenMBeanOperationInfoSupport("disable_host_keepalive",
				"disable host keep alive",
				params_lh,
				SimpleType.STRING,
				MBeanOperationInfo.ACTION);

		mbean_info = new OpenMBeanInfoSupport(this.getClass().getName(),
				   "Http Host Open MBean",
				   attributes,
				   constructors,
				   operations,
				   notifications);
	}

	public void setHttpRuntimeInterface(HttpRuntimeInterface ifc) {
		http = ifc;
		http_host_bean = new HttpAliasOpenMBean(ifc);
		alias_bean = new ApplicationAliasOpenMBean(ifc);
	}

	/**
	 * @param host_name
	 * @return
	 */
	private HostPropertiesRuntimeInterface findHostPropertiesRuntimeInterface(String host_name) {
		HostPropertiesRuntimeInterface[] hosts = null;
		int host_index = -1;

		if (host_name == null || host_name.length() == 0){
			host_name = "default";
		}

		try {
			hosts = http.getAllHostsTemp();
		} catch (Exception e) {
			if (LOCATION_HTTP_MBEANS.beWarning()) {
				Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000257", 
				  "HttpHostOpenMBean.findHostPropertiesRuntimeInterface(): Can not obtain host properties.", e, null, null, null);
			}
			return null;
		}

		try {
			host_index = findHostIndex(hosts, host_name);
		} catch (RemoteException e1) {
			if (LOCATION_HTTP_MBEANS.beWarning()) {
				Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000258", 
				  "HttpHostOpenMBean.findHostPropertiesRuntimeInterface(): Can not find host index", e1, null, null, null);
			}
			return null;
		}

	    if (host_index == -1) {
	        if (LOCATION_HTTP_MBEANS.beWarning()) {
	        	Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000259", 
	        	  "HttpHostOpenMBean.findHostPropertiesRuntimeInterface(): Virtual Host [{0}] Not Found", 
	        	  new Object[]{host_name}, null, null, null);
	        }
					return null;
	    }

	    return hosts[host_index];
	}

	private int findHostIndex(HostPropertiesRuntimeInterface[] hosts,String host) throws RemoteException {
		int ret = -1;
		for (int t = 0;t < hosts.length; t++) {
	        if (hosts[t].getHostName().equals(host)) {
	          ret = t;
	          break;
	        }
	      }
		return ret;
	}
}
