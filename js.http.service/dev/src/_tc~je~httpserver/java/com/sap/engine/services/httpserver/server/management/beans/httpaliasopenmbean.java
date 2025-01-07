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

import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.services.httpserver.HostPropertiesRuntimeInterface;
import com.sap.engine.services.httpserver.HttpRuntimeInterface;
import com.sap.engine.services.httpserver.server.Log;

/**
 * @author Nikolai Dokovski
 * @version 1.0
 *
 * HttpAliasOpenMBean provides operations found in http_alias telnet command
 * In general, the HttpAliasOpenMBean manages the availability of http aliases.
 */
public class HttpAliasOpenMBean implements DynamicMBean , NotificationBroadcaster {
	private static MBeanInfo mbean_info = null;

	private HttpRuntimeInterface http;

	//list_http_alias
	private static String[] lha_names;
	private static String[] lha_descriptions;
	private static OpenType[] lha_types;
	private static CompositeType ctype_lha;
	private static OpenType array_ctype_lha;

	private static Object lock = new Object();

	static{
		try{
			lha_names = new String[] {"alias","path"};
			lha_descriptions = new String[] {"Http Alias Name","Local Path"};
			lha_types = new OpenType[] {SimpleType.STRING,SimpleType.STRING};
			ctype_lha = new CompositeType("ctype_lha",
					"Composite Type of list_http_alias operation",
					lha_names,lha_descriptions,lha_types);
			array_ctype_lha = new ArrayType(1,ctype_lha);
		}catch (OpenDataException e) {
			if (LOCATION_HTTP_MBEANS.beWarning()) {
				Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000246", "Error in initializing HttpAliasOpenMBean", e, null, null, null);
			}
		}
	}

	public HttpAliasOpenMBean(){
		init();
	}

	public HttpAliasOpenMBean(HttpRuntimeInterface ifc){
		init();
		this.http = ifc;
	}

	/**
	 * list_http_alias
	 * Lists http aliases available on the virtual host. If the host name
	 * is not specified the "default" value is used instead.  Alias name can be used to filter the
	 * result of the operation.
	 *
	 * @param host_name - virtual host name.
	 * @param alias_name - http alias
	 *
	 * @return array of CompositeData  elements. Each elements has following structure
	 * <pre>
	 * 		java.lang.String  alias - Http Alias Name
	 *		java.lang.String  path - Local Path
	 * </pre>
	 */
	public CompositeData[] list_http_alias(String host_name,String alias_name){
		CompositeData[] ret_data = new CompositeData[0];
		HostPropertiesRuntimeInterface hprifc = null;
		Vector tmp = new Vector();
		String path = null;
		Object[] lha_values = null;

		hprifc = findHostPropertiesRuntimeInterface(host_name);
		if (hprifc == null){
			return ret_data;
		}

		try{
			if (alias_name != null && alias_name.length() > 0 ){
				path = hprifc.getAliasValue(alias_name);
				if (path != null){
					lha_values = new Object[] {alias_name,path};
					ret_data = new CompositeData[] {new CompositeDataSupport(ctype_lha,lha_names,lha_values)};
				}
			}else{
				String[] alias_names = hprifc.getAliasNames();
				for(int indx = 0;alias_names != null && indx < alias_names.length;indx++){
					if (!hprifc.isApplicationAlias(alias_names[indx])){
						lha_values = new Object[]{alias_names[indx],hprifc.getAliasValue(alias_names[indx])};
						tmp.add(new CompositeDataSupport(ctype_lha,lha_names,lha_values));
					}
				}
			}
		}catch (RemoteException e){
			if (LOCATION_HTTP_MBEANS.beWarning()) {
				Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000247", 
				  "HttpAliasOpenMBean.list_http_alias(): Cannot obtain http aliases", e, null, null, null);
			}
		} catch (OpenDataException e) {
			if (LOCATION_HTTP_MBEANS.beWarning()) {
				Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000248", 
				  "HttpAliasOpenMBean.list_http_alias(): Cannot construct composite data", e, null, null, null);
			}
		}

		if (tmp.size() > 0 ){
			ret_data = (CompositeData[]) tmp.toArray(ret_data);
		}

		return ret_data;
	}


	public String remove_http_alias(String host_name,String alias_name){
		String ret_data = "invoked";
		HostPropertiesRuntimeInterface hprifc = null;

		if (alias_name == null){
			alias_name = new String();
		}

		hprifc = findHostPropertiesRuntimeInterface(host_name);
		if (hprifc == null){
			return ret_data;
		}

		try {
			if (!hprifc.isApplicationAlias(alias_name) && hprifc.getAliasValue(alias_name) != null){
				hprifc.removeHttpAlias(alias_name);
				http.clearCache();
				ret_data = "removed";
			}
		}catch(RemoteException e){
			if (LOCATION_HTTP_MBEANS.beWarning()) {
				Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000249", 
				  "HttpAliasOpenMBean.remove_http_alias(): Cannot verify alias name", e, null, null, null);
			}
		} catch (ConfigurationException e) {
			if (LOCATION_HTTP_MBEANS.beWarning()) {
				Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000250", 
				  "HttpAliasOpenMBean.remove_http_alias(): Cannot remove http alias", e, null, null, null);
			}
		}

		return ret_data;
	}

	public String create_http_alias(String host_name, String alias_name, String path) {
		String ret_data = "invoked";
		HostPropertiesRuntimeInterface hprifc = null;

		hprifc = findHostPropertiesRuntimeInterface(host_name);
		if (hprifc == null){
			return ret_data;
		}

		try{
			if (path != null && path.length() > 0){
				hprifc.addHttpAlias(alias_name,path);
				http.clearCache();
				ret_data = "success";
			}
		} catch (Exception e) {
			if (LOCATION_HTTP_MBEANS.beWarning()) {
				Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000254", 
				  "HttpAliasOpenMBean.create_http_alias(): Cannot create http alias.", e, null, null, null);
			}
		}
		return ret_data;
	}

	/* HttpAliasOpenMBean does not provide attributes. The getter method has no impact.
	 * @see javax.management.DynamicMBean#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String arg0) throws AttributeNotFoundException,
			MBeanException, ReflectionException {
		throw new AttributeNotFoundException("No such attribute.");
	}

	/* HttpAliasOpenMBean does not provide attributes. The setter method has no impact. (non-Javadoc)
	 * @see javax.management.DynamicMBean#setAttribute(javax.management.Attribute)
	 */
	public void setAttribute(Attribute arg0) throws AttributeNotFoundException,
			InvalidAttributeValueException, MBeanException, ReflectionException {
		throw new AttributeNotFoundException("No such attribute.");
	}

	/* HttpAliasOpenMBean does not provide attributes. The getter method has no impact.
	 * @see javax.management.DynamicMBean#getAttributes(java.lang.String[])
	 */
	public AttributeList getAttributes(String[] arg0) {
		return null;
	}

	/* HttpAliasOpenMBean does not provide attributes. The setter method has no impact.
	 * @see javax.management.DynamicMBean#setAttributes(javax.management.AttributeList)
	 */
	public AttributeList setAttributes(AttributeList arg0) {
		return null;
	}

	/*
	 * @see javax.management.DynamicMBean#invoke(java.lang.String, java.lang.Object[], java.lang.String[])
	 */
	public Object invoke(String op_name, Object[] params, String[] signature)
			throws MBeanException, ReflectionException {
		synchronized(lock){
			if (op_name == null) {
				throw new RuntimeOperationsException(new IllegalArgumentException("Operation name cannot be null"),
				"Cannot call invoke with null operation name on HttpAliasOpenMBean");
			}else if (op_name.equals("list_http_alias")){
				checkParameter(2,params);
				return list_http_alias((String)params[0],(String)params[1]);
			}else if (op_name.equals("remove_http_alias")){
				checkParameter(2,params);
				return remove_http_alias((String)params[0],(String)params[1]);
			}else if (op_name.equals("create_http_alias")){
				checkParameter(3,params);
				return create_http_alias((String)params[0],(String)params[1],(String)params[2]);
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.management.DynamicMBean#getMBeanInfo()
	 */
	public MBeanInfo getMBeanInfo() {
		return mbean_info;
	}

	public void setHttpRuntimeInterface(HttpRuntimeInterface ifc){
		this.http = ifc;
	}

	private void init() {
		if (mbean_info != null){
			return;
		}
		//Building OpenMBeanInfo
		OpenMBeanAttributeInfoSupport[]   attributes    = new OpenMBeanAttributeInfoSupport[0];
		OpenMBeanConstructorInfoSupport[] constructors  = new OpenMBeanConstructorInfoSupport[1];
		OpenMBeanOperationInfoSupport[]   operations    = new OpenMBeanOperationInfoSupport[3];
		MBeanNotificationInfo       []    notifications = new MBeanNotificationInfo[0];


		//MBean constructor
		constructors[0] = new OpenMBeanConstructorInfoSupport("HttpAliasOpenMBean",
			      "Constructs a HttpAliasOpenMBean instance.",
			      new OpenMBeanParameterInfoSupport[0]);

		//Parameters for list_http_alias,  remove_http_alias operations
		OpenMBeanParameterInfo[] params_lha = new OpenMBeanParameterInfoSupport[2];
		params_lha[0] = new OpenMBeanParameterInfoSupport("host","Host name",SimpleType.STRING);
		params_lha[1] = new OpenMBeanParameterInfoSupport("alias","Http Alias name",SimpleType.STRING);

		//Parameters for create_http_alias operations
		OpenMBeanParameterInfo[] params_cha = new OpenMBeanParameterInfoSupport[3];
		params_cha[0] = new OpenMBeanParameterInfoSupport("host","Host name",SimpleType.STRING);
		params_cha[1] = new OpenMBeanParameterInfoSupport("alias","Http Alias name",SimpleType.STRING);
		params_cha[2] = new OpenMBeanParameterInfoSupport("path","Http Path",SimpleType.STRING);

		operations[0] = new OpenMBeanOperationInfoSupport("list_http_alias",
				"lists http aliases",
				params_lha,
				array_ctype_lha,
				MBeanOperationInfo.INFO);

		operations[1] = new OpenMBeanOperationInfoSupport("remove_http_alias",
				"removes http aliases",
				params_lha,
				SimpleType.STRING,
				MBeanOperationInfo.ACTION);

		operations[2] = new OpenMBeanOperationInfoSupport("create_http_alias",
				"creates http aliases",
				params_cha,
				SimpleType.STRING,
				MBeanOperationInfo.ACTION);

		mbean_info = new OpenMBeanInfoSupport(this.getClass().getName(),
				   "Http Alias Open MBean",
				   attributes,
				   constructors,
				   operations,
				   notifications);
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
				Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000251", 
				  "HttpAliasOpenMBean.findHostPropertiesRuntimeInterface(): Cannot obtain host properties.", e, null, null, null);
			}
			return null;
		}

		try {
			host_index = findHostIndex(hosts, host_name);
		} catch (RemoteException e1) {
			if (LOCATION_HTTP_MBEANS.beWarning()) {
				Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000252", 
				  "HttpAliasOpenMBean.findHostPropertiesRuntimeInterface(): Cannot find host index", e1, null, null, null);
			}
			return null;
		}

	    if (host_index == -1) {
	        if (LOCATION_HTTP_MBEANS.beWarning()) {
	        	Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000253", 
	        	  "HttpAliasOpenMBean.findHostPropertiesRuntimeInterface(): Virtual Host [{0}] Not Found", 
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

	private void checkParameter(int param_number,Object[] params) throws RuntimeOperationsException {
		if (params.length != param_number){
			throw new RuntimeOperationsException(new IllegalArgumentException("Illegal parameters"),
			"Cannot invoke http alias mbean operation");
		}
		for(int idx = 0;idx < param_number;idx++){
			if (params[idx]!= null && !(params[idx] instanceof String)){
				throw new RuntimeOperationsException(new IllegalArgumentException("Illegal type of the parameter:"+idx+
						" The type is:"+params[1].getClass().getName()+". Should be java.lang.String"),
				"Cannot invoke application alias mbean operation");
			}
		}
	}



	/* HttpAliasOpenMBean is not a notification broadcaster. Therefore this method does nothing.
	 * @see javax.management.NotificationBroadcaster#addNotificationListener(javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
	 */
	public void addNotificationListener(NotificationListener arg0, NotificationFilter arg1, Object arg2) throws IllegalArgumentException {
	}

	/* HttpAliasOpenMBean is not a notification broadcaster. Therefore this method does nothing.
	 * @see javax.management.NotificationBroadcaster#removeNotificationListener(javax.management.NotificationListener)
	 */
	public void removeNotificationListener(NotificationListener arg0) throws ListenerNotFoundException {
	}

	/* HttpAliasOpenMBean is not a notification broadcaster. Therefore this method returns null.
	 * @see javax.management.NotificationBroadcaster#getNotificationInfo()
	 */
	public MBeanNotificationInfo[] getNotificationInfo() {
		return null;
	}
}
