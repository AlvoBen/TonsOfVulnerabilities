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
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.management.openmbean.OpenMBeanConstructorInfoSupport;
import javax.management.openmbean.OpenMBeanInfoSupport;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.OpenMBeanParameterInfo;
import javax.management.openmbean.OpenMBeanParameterInfoSupport;
import javax.management.openmbean.SimpleType;

import com.sap.engine.services.httpserver.HttpRuntimeInterface;
import com.sap.engine.services.httpserver.server.Log;

/**
 * @author Nikolai Dokovski
 * @version 1.0
 * 
 * HttpCacheOpenMBean provides operations found in clear_http_cache telnet command
 * HttpCacheOpenMBean clears http cache.
 */
public class HttpCacheOpenMBean implements DynamicMBean,
		NotificationBroadcaster {
	private HttpRuntimeInterface http;
	
	private static MBeanInfo mbean_info = null;

	private static Object lock = new Object();
	
	
	public HttpCacheOpenMBean(){
		init();
	}
	
	public void setHttpRuntimeInterface(HttpRuntimeInterface ifc){
		this.http = ifc;
	}
	
	/**
	 * clear_http_cache
	 * Clears Http cache on current node.
	 * @return status. Status can be "invoked", "success".
	 */
	public String clear_http_cache() {
		String ret_data = "invoked";
			try {
				http.clearCache();
				ret_data = "success";
			} catch (RemoteException e) {
				if (LOCATION_HTTP_MBEANS.beWarning()) {
					Log.traceWarning(LOCATION_HTTP_MBEANS, "ASJ.http.000255", 
					  "HttpCacheOpenMBean.clear_http_cache(): Can not clear http cache.", e, null, null, null);
				}
			}
		return ret_data;
	}

	/* HttpCacheOpenMBean does not provide attributes. The getter method has no impact.
	 * @see javax.management.DynamicMBean#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String arg0) throws AttributeNotFoundException,
			MBeanException, ReflectionException {
		throw new AttributeNotFoundException("No such attribute.");
	}

	/* HttpCacheOpenMBean does not provide attributes. The setter method has no impact.
	 * @see javax.management.DynamicMBean#setAttribute(javax.management.Attribute)
	 */
	public void setAttribute(Attribute arg0) throws AttributeNotFoundException,
			InvalidAttributeValueException, MBeanException, ReflectionException {
		throw new AttributeNotFoundException("No such attribute.");
	}

	/* HttpCacheOpenMBean does not provide attributes. The getter method has no impact.
	 * @see javax.management.DynamicMBean#getAttributes(java.lang.String[])
	 */
	public AttributeList getAttributes(String[] arg0) {
		return null;
	}

	/* HttpCacheOpenMBean does not provide attributes. The setter method has no impact.
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
			}else if (op_name.equals("clear_http_cache")){
				return clear_http_cache();
			}
			return null;
		}	
	}

	/* 
	 * @see javax.management.DynamicMBean#getMBeanInfo()
	 */
	public MBeanInfo getMBeanInfo() {
		return mbean_info;
	}

	/* HttpCacheOpenMBean is not a notification broadcaster. Therefore this method does nothing.
	 * @see javax.management.NotificationBroadcaster#addNotificationListener(javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
	 */
	public void addNotificationListener(NotificationListener arg0,
			NotificationFilter arg1, Object arg2)
			throws IllegalArgumentException {
	}

	/* HttpCacheOpenMBean is not a notification broadcaster. Therefore this method does nothing.
	 * @see javax.management.NotificationBroadcaster#removeNotificationListener(javax.management.NotificationListener)
	 */
	public void removeNotificationListener(NotificationListener arg0)
			throws ListenerNotFoundException {
	}

	/* HttpCacheOpenMBean is not a notification broadcaster. Therefore this method returns null.
	 * @see javax.management.NotificationBroadcaster#getNotificationInfo()
	 */
	public MBeanNotificationInfo[] getNotificationInfo() {
		return null;
	}
	
	private void init() {
		
		if (mbean_info != null){
			return;
		}
		//Building OpenMBeanInfo
		OpenMBeanAttributeInfoSupport[]   attributes    = new OpenMBeanAttributeInfoSupport[0];
		OpenMBeanConstructorInfoSupport[] constructors  = new OpenMBeanConstructorInfoSupport[1];
		OpenMBeanOperationInfoSupport[]   operations    = new OpenMBeanOperationInfoSupport[1];
		MBeanNotificationInfo       []    notifications = new MBeanNotificationInfo[0];
		
		//MBean constructor
		constructors[0] = new OpenMBeanConstructorInfoSupport("HttpCacheOpenMBean",
			      "Constructs a HttpCacheOpenMBean instance.",
			      new OpenMBeanParameterInfoSupport[0]);
		
		//Parameters for clear_http_cache operations
		OpenMBeanParameterInfo[] params_chc = new OpenMBeanParameterInfoSupport[0];
		
		operations[0] = new OpenMBeanOperationInfoSupport("clear_http_cache",
				"clears http cache",
				params_chc,
				SimpleType.STRING,
				MBeanOperationInfo.ACTION);
		mbean_info = new OpenMBeanInfoSupport(this.getClass().getName(),
				   "Http Cache Open MBean",
				   attributes,
				   constructors,
				   operations,
				   notifications);
	}
}
