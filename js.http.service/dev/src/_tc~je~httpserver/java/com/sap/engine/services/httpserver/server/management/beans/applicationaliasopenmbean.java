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

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.services.deploy.DeployService;
import com.sap.engine.services.httpserver.HostPropertiesRuntimeInterface;
import com.sap.engine.services.httpserver.HttpRuntimeInterface;
import com.sap.engine.services.httpserver.server.Log;

/**
 * @author Nikolai Dokovski
 * @version 1.0
 *
 * ApplicationAliasOpenMBean provides operations found in application_alias telnet command
 * In general, the ApplicationAliasOpenMBean manages the availability of application aliases.
 */
public class ApplicationAliasOpenMBean implements DynamicMBean, NotificationBroadcaster {
	private ApplicationServiceContext context;
	private HttpRuntimeInterface http;

	//DynamicMBean information holder
	private static OpenMBeanInfoSupport mbean_info = null;

	//list_application_alias composite data
	private static String[]   laa_names;
	private static String[]   laa_descriptions;
	private static OpenType[] laa_types;
	private static CompositeType ctype_laa;
	private static OpenType array_ctype_laa;


	//list_application_alias_by_appname
	private static String[] laan_names;
	private static String[] laan_descriptions;
	private static OpenType[] laan_types;
	private static CompositeType ctype_laan;
	private static OpenType array_ctype_laan;

	//synchronization lock;
	private static Object lock = new Object();
	static{
		try{
			//list_application_alias init
			laa_names = new String[] {"alias","status"};
			laa_descriptions = new String[] {"Alias Name","Alias Status"};
			laa_types = new OpenType[] {SimpleType.STRING,SimpleType.STRING};
			ctype_laa = new CompositeType("ctype_laas",
					"Composite Type of list_application_alias operation",
					laa_names,laa_descriptions,laa_types);
			array_ctype_laa = new ArrayType(1,ctype_laa);

			//list_application_alias_by_appname
			laan_names = new String[]{"appname","alias"};
			laan_descriptions = new String[]{"Application Name","Application Aliases"};
			laan_types = new OpenType[] {SimpleType.STRING,array_ctype_laa};
			ctype_laan = new CompositeType("ctype_laans",
					"Composite Type of list_application_alias_by_appname operation",
					laan_names,laan_descriptions,laan_types);
			array_ctype_laan = new ArrayType(1,ctype_laan);
		} catch (OpenDataException e) {
			if (LOCATION_HTTP_MBEANS.beWarning()) {
				Log.traceWarning(LOCATION_HTTP_MBEANS,"ASJ.http.000230", "Error in initializing ApplicationAliasOpenMBean", e, null, null, null);
			}
		}
	}

	public ApplicationAliasOpenMBean(){
			init();
	}

	public ApplicationAliasOpenMBean(HttpRuntimeInterface ifc) {
		init();
		this.http = ifc;
	}

	public void setApplicationServiceContext(ApplicationServiceContext ctx){
		this.context  = ctx;
	}

	public void setHttpRuntimeInterface(HttpRuntimeInterface ifc){
		this.http = ifc;
	}

	/**
	 * list_application_alias
	 * Lists application aliases available on the virtual host. If the host name
	 * is not specified the "default" value is used instead. Alias name can be used to filter the
	 * result of the operation.
	 *
	 * @param host_name - virtual host name
	 * @param alias_name - application alias
	 *
	 * @return array of CompositeData  elements. Each elements has following structure
	 * <pre>
	 * 		java.lang.String  alias - Alias Name
	 *		java.lang.String  status - Alias Status
	 * </pre>
	 *
	 */
	public CompositeData[] list_application_alias(String host_name,String alias_name){
		CompositeData[] ret_data = new CompositeData[0];
		Vector temp = new Vector();
		HostPropertiesRuntimeInterface hprifc = null;

		hprifc = findHostPropertiesRuntimeInterface(host_name);
		if (hprifc == null){
			return ret_data;
		}

		String[] en = new String [0];
		if (alias_name == null){
			alias_name = new String();
		}
		if (alias_name.length() > 0){
			en = new String []{alias_name};
		}else {
			try {
				String[] tstr = hprifc.getAliasNames();
				Vector tvec = new Vector();
				for (int tindx = 0; tindx < tstr.length;tindx++){
					if (hprifc.isApplicationAlias(tstr[tindx])){
						tvec.add(tstr[tindx]);
					}
				}
				if (tvec.size() > 0){
					en = (String[]) tvec.toArray(en);
				}
			} catch (RemoteException e2) {
				if (LOCATION_HTTP_MBEANS.beWarning()) {
					Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000231", 
					  "ApplicationAliasOpenMBean.list_application_alias(): Cannot get alias name.", e2, null, null, null);
				}
				return ret_data;
			}
		}

		for (int i = 0; en != null && i < en.length; i++) {
		  String alias = en[i];
		  String status = "unknown";
		  try {
			if (!hprifc.isApplicationAlias(alias)) {
		    if (LOCATION_HTTP_MBEANS.beWarning()) {
		    	Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000232", 
		    	  "ApplicationAliasOpenMBean.list_application_alias(): [{0}] is not an application alias.", new Object[]{alias}, null, null, null);
				}
			}else {
				if (hprifc.isApplicationAliasEnabled(alias)) {
					status = "active";
				}else{
					status = "inactive";
				}
			}
		  } catch (RemoteException e3) {
				if (LOCATION_HTTP_MBEANS.beWarning()) {
					Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000235", 
					  "ApplicationAliasOpenMBean.list_application_alias(): Can not verify application alias", e3, null, null, null);
				}
			continue;
		  }

		  // constructing composite data
		  Object[] laa_values = new Object[] {alias,status};
		  CompositeData tmp;
		  try {
		  	    if (!status.equals("unknown")){
		  	    	tmp = new CompositeDataSupport(ctype_laa,laa_names,laa_values);
		  			temp.add(tmp);
		  		}
		  } catch (OpenDataException e5) {
	  		if (LOCATION_HTTP_MBEANS.beWarning()) {
	  			Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000236", 
	  			  "ApplicationAliasOpenMBean.list_application_alias(): Cannot construct composite data.", e5, null, null, null);
				}
		  }
		}

		if (!temp.isEmpty()){
		    ret_data = (CompositeData[]) temp.toArray(ret_data);
		}
		return ret_data;
	}


	/**
	 * list_application_alias_by_appname
	 * Lists application aliases grouped by application name available on the virtual host. If the host name
	 * is not specified the "default" value is used instead. Alias name can be used to filter the
	 * result of the operation.
	 *
	 * @param host_name - virtual host name.
	 * @param alias_name - application alias
	 *
	 * @return array of CompositeData  elements. Each elements has following structure
	 * <pre>
	 * 		java.lang.String  appname - Application Name
	 * 		javax.management.openmbean.CompositeData[] - alias Application Aliases
	 * </pre>
	 * 		where each "alias" element has following structure
	 * <pre>
	 * 		java.lang.String  alias - Alias Name
	 *		java.lang.String  status - Alias Status
	 * </pre>
	 *
	 */
	public CompositeData[] list_application_alias_by_appname(String host_name, String alias_name) {
		CompositeData[] ret_data = new CompositeData[0];
		CompositeData[] alias_data = null;
		Vector temp = new Vector();
		HostPropertiesRuntimeInterface hprifc = null;

		if(alias_name == null){
			alias_name = new String();
		}
		hprifc = findHostPropertiesRuntimeInterface(host_name);
		if (hprifc == null){
			return ret_data;
		}

		String container = "servlet_jsp";
		String[] servers = new String[1];
	    servers[0] = context.getClusterContext().getClusterMonitor()
	        .getCurrentParticipant().getName();
	    String[] apps = null;
	    DeployService deploy = (DeployService) context.getContainerContext()
	        .getObjectRegistry().getServiceInterface("deploy");

	    try {
			apps = deploy.listApplications(container, servers);
		} catch (RemoteException e) {
			if (LOCATION_HTTP_MBEANS.beWarning()) {
				Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000242", 
				  "ApplicationAliasOpenMBean.list_application_alias_by_appname(): Cannot obtain application list.", e, null, null, null);
			}
			return ret_data;
		}

		boolean single_app_required = false;
		boolean found = false;
		if (alias_name.length() > 0){
			single_app_required = true;
		}


		try{
			for (int indx = 0;apps != null && indx < apps.length;indx++){
				String app_name = apps[indx];
				Vector t = new Vector();
				String[] app_name_aliases = deploy.listElements(container,app_name,servers);
				//aliases per application
				for (int indx1 = 0; indx1 < app_name_aliases.length;indx1++){
					String tmp_element = app_name_aliases[indx1];
					String tmp_alias_name = tmp_element.substring(0,tmp_element.lastIndexOf(" - web"));
					//looking for particular alias
					if (single_app_required){
						if (alias_name.equals(tmp_alias_name)){
							found = true;
						}else{
							continue;
						}
					}

					if (hprifc.isApplicationAlias(tmp_alias_name)){
						alias_data = list_application_alias(host_name,tmp_alias_name);
						for (int indx2 = 0;alias_data!=null && indx2 < alias_data.length;indx2++){
							t.add(alias_data[indx2]);
						}
					}
				}
				//found some alias information
			    if (!single_app_required){
			    	CompositeData[] d = new CompositeData[0];
			    	if (t.size() > 0){
			    		d = (CompositeData[]) t.toArray(d);
			    	}
			    	Object[] laan_values = new Object[] {app_name,d};
			    	CompositeData tmp;
			    	try {
			    		tmp = new CompositeDataSupport(ctype_laan,laan_names,laan_values);
			    		temp.add(tmp);
			    	} catch (OpenDataException e5) {
			    		if (LOCATION_HTTP_MBEANS.beWarning()) {
			    			Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000243", 
			    			  "ApplicationAliasOpenMBean.list_application_alias_by_appname(): Cannot construct composite data.", e5, null, null, null);
			    		}
			    	}
			    }else if(found){
			    	CompositeData[] d = new CompositeData[0];
			    	if (t.size() > 0){
			    		d = (CompositeData[]) t.toArray(d);
			    	}
			    	Object[] laan_values = new Object[] {app_name,d};
			    	CompositeData tmp;
			    	try {
			    		tmp = new CompositeDataSupport(ctype_laan,laan_names,laan_values);
			    		temp.add(tmp);
			    	} catch (OpenDataException e5) {
			    		if (LOCATION_HTTP_MBEANS.beWarning()) {
			    			Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000244", 
			    			  "ApplicationAliasOpenMBean.list_application_alias_by_appname(): Cannot construct composite data.", e5, null, null, null);
							}
			    	}

			    	break;
			    }
			}
		}catch (Exception e){
			if (LOCATION_HTTP_MBEANS.beWarning()) {
				Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000245", 
				  "ApplicationAliasOpenMBean.list_application_alias_by_appname():", e, null, null, null);
			}
		}

		if (temp.size() > 0){
			ret_data = (CompositeData[]) temp.toArray(ret_data);
		}

		return ret_data;
	}


	/**
	 * enable_application_alias
	 * Enables application aliases on the virtual host (if specified). Alias name should
	 * be provided. This operation also clears http cache.
	 *
	 * @param host_name - virtual host name. If the host name
	 * 		  is not specified the "default" value is used instead.
	 * @param alias_name - application alias
	 *
	 * @return status String. Possible values for the status are
	 * "unknown","active","inactive"
	 *
	 */
	public String enable_applicaiton_alias(String host_name, String alias) {
		String status = "invoked";
		HostPropertiesRuntimeInterface hprifc = null;

		hprifc = findHostPropertiesRuntimeInterface(host_name);
		if (hprifc == null){
			return status;
		}

		try {
			if (hprifc.isApplicationAlias(alias)){
				if(!hprifc.isApplicationAliasEnabled(alias)){
					hprifc.enableApplicationAlias(alias);
					http.clearCache();
				}
				if (hprifc.isApplicationAliasEnabled(alias)){
					status = "active";
				}else{
					status = "inactive";
				}
			}
		} catch (ConfigurationException e) {
			if (LOCATION_HTTP_MBEANS.beWarning()) {
				Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000237", 
				  "ApplicationAliasOpenMBean.enable_applicaiton_alias(): Cannot change alias status.", e, null, null, null);
			}
		} catch (RemoteException e) {
			if (LOCATION_HTTP_MBEANS.beWarning()) {
				Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000238", 
				  "ApplicationAliasOpenMBean.enable_applicaiton_alias(): Cannot change alias status.", e, null, null, null);
			}
		}
		return status;
	}

	/**
	 * disable_applicaiton_alias
	 * Disables application aliases on the virtual host (if specified). Alias name should
	 * be provided. This operation also clears http cache.
	 *
	 * @param host_name - virtual host name. If the host name
	 * 		  is not specified the "default" value is used instead.
	 * @param alias_name - application alias
	 *
	 * @return status String. Possible values for the status are
	 * "unknown","active","inactive"
	 *
	 */
	public String disable_applicaiton_alias(String host_name, String alias) {
		String status = "invoked";
		HostPropertiesRuntimeInterface hprifc = null;

		hprifc = findHostPropertiesRuntimeInterface(host_name);
		if (hprifc == null){
			return status;
		}

		try {
			if (hprifc.isApplicationAlias(alias)){
				if(hprifc.isApplicationAliasEnabled(alias)){
					hprifc.disableApplicationAlias(alias);
					http.clearCache();
				}
				if (hprifc.isApplicationAliasEnabled(alias)){
					status = "active";
				}else{
					status = "inactive";
				}
			}
		} catch (ConfigurationException e) {
			if (LOCATION_HTTP_MBEANS.beWarning()) {
				Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000233", 
				  "ApplicationAliasOpenMBean.disable_applicaiton_alias(): Cannot change alias status.", e, null, null, null);
			}
		} catch (RemoteException e) {
			if (LOCATION_HTTP_MBEANS.beWarning()) {
				Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000234", 
				  "ApplicationAliasOpenMBean.disable_applicaiton_alias(): Cannot change alias status.", e, null, null, null);
			}
		}
		return status;
	}

	/* ApplicationAliasOpenMBean does not provide attributes. The getter method has no impact.
	 * @see javax.management.DynamicMBean#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String arg0) throws AttributeNotFoundException,
			MBeanException, ReflectionException {
		throw new AttributeNotFoundException("No such attribute.");
	}

	/* ApplicationAliasOpenMBean does not provide attributes. The setter method has no impact.
	 * @see javax.management.DynamicMBean#setAttribute(javax.management.Attribute)
	 */
	public void setAttribute(Attribute arg0) throws AttributeNotFoundException,
			InvalidAttributeValueException, MBeanException, ReflectionException {
		throw new AttributeNotFoundException("No such attribute.");
	}

	/* ApplicationAliasOpenMBean does not provide attributes. The getter method has no impact.
	 * @see javax.management.DynamicMBean#getAttributes(java.lang.String[])
	 */
	public AttributeList getAttributes(String[] arg0) {
		return null;
	}

	/* ApplicationAliasOpenMBean does not provide attributes. The setter method has no impact.
	 * @see javax.management.DynamicMBean#setAttributes(javax.management.AttributeList)
	 */
	public AttributeList setAttributes(AttributeList arg0) {
		return null;
	}

	/* ApplicationAliasOpenMBean is not a notification broadcaster. Therefore this method does nothing.
	 * @see javax.management.NotificationBroadcaster#addNotificationListener(javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
	 */
	public void addNotificationListener(NotificationListener arg0, NotificationFilter arg1, Object arg2) throws IllegalArgumentException {
	}

	/*  ApplicationAliasOpenMBean is not a notification broadcaster. Therefore this method does nothing.
	 * @see javax.management.NotificationBroadcaster#removeNotificationListener(javax.management.NotificationListener)
	 */
	public void removeNotificationListener(NotificationListener arg0) throws ListenerNotFoundException {
	}

	/* ApplicationAliasOpenMBean is not a notification broadcaster. Therefore this method return null.
	 * @see javax.management.NotificationBroadcaster#getNotificationInfo()
	 */
	public MBeanNotificationInfo[] getNotificationInfo() {
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
				"Cannot call invoke with null operation name on ApplicationAliasOpenMBean");
			}else if (op_name.equals("list_application_alias")){
				checkParameter(params);
				return list_application_alias((String)params[0],(String)params[1]);
			}else if (op_name.equals("list_application_alias_by_appname")){
				checkParameter(params);
				return list_application_alias_by_appname((String)params[0],(String)params[1]);
			}else if (op_name.equals("enable_application_alias")){
				checkParameter(params);
				return enable_applicaiton_alias((String)params[0],(String)params[1]);

			}else if (op_name.equals("disable_application_alias")){
				checkParameter(params);
				return disable_applicaiton_alias((String)params[0],(String)params[1]);

			}
	        return null;
		}
	}

	/*
	 * @see javax.management.DynamicMBean#getMBeanInfo()
	 * ApplicationAliasOpenMBean MBeanInfo
	 */
	public MBeanInfo getMBeanInfo() {
		return mbean_info;
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
		OpenMBeanOperationInfoSupport[]   operations    = new OpenMBeanOperationInfoSupport[4];
		MBeanNotificationInfo       []    notifications = new MBeanNotificationInfo[0];

		//MBean constructor
		constructors[0] = new OpenMBeanConstructorInfoSupport("ApplicationAliasOpenMBean",
			      "Constructs a ApplicationOpenMBean instance.",
			      new OpenMBeanParameterInfoSupport[0]);

		//Parameters for list_application_alias,  list_application_alias_by_appname operations
		OpenMBeanParameterInfo[] params_laa = new OpenMBeanParameterInfoSupport[2];
		params_laa[0] = new OpenMBeanParameterInfoSupport("host","Host name",SimpleType.STRING);
		params_laa[1] = new OpenMBeanParameterInfoSupport("alias","Alias name",SimpleType.STRING);

		operations[0] = new OpenMBeanOperationInfoSupport("list_application_alias",
				"lists application aliases",
				params_laa,
				array_ctype_laa,
				MBeanOperationInfo.INFO);

		operations[1] = new OpenMBeanOperationInfoSupport("list_application_alias_by_appname",
				"lists application aliases with application name",
				params_laa,
				array_ctype_laan,
				MBeanOperationInfo.INFO);

		operations[2] = new OpenMBeanOperationInfoSupport("enable_application_alias",
				"enables application alias",
				params_laa,
				SimpleType.STRING,
				MBeanOperationInfo.ACTION);

		operations[3] = new OpenMBeanOperationInfoSupport("disable_application_alias",
				"disables application alias",
				params_laa,
				SimpleType.STRING,
				MBeanOperationInfo.ACTION);

		mbean_info = new OpenMBeanInfoSupport(this.getClass().getName(),
				   "Application Alias Open MBean",
				   attributes,
				   constructors,
				   operations,
				   notifications);

	}

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
				Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000239", 
				  "ApplicationAliasOpenMBean.findHostPropertiesRuntimeInterface(): Cannot obtain host properties.", e, null, null, null);
			}
			return null;
		}

		try {
			host_index = findHostIndex(hosts, host_name);
		} catch (RemoteException e1) {
			if (LOCATION_HTTP_MBEANS.beWarning()) {
				Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000240", 
				  "ApplicationAliasOpenMBean.findHostPropertiesRuntimeInterface(): Cannot find host index", e1, null, null, null);
			}
			return null;
		}

	    if (host_index == -1) {
	        if (LOCATION_HTTP_MBEANS.beWarning()) {
	        	Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000241", 
	        	  "ApplicationAliasOpenMBean.findHostPropertiesRuntimeInterface(): Virtual Host [{0}] Not Found", 
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

	private void checkParameter(Object[] params) throws RuntimeOperationsException {
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
	}
}
