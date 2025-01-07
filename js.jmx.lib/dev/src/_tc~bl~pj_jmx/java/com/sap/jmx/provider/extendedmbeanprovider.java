package com.sap.jmx.provider;

import java.util.Set;

import javax.management.ObjectName;
import javax.management.QueryExp;

import com.sap.pj.jmx.server.interceptor.InvocationContext;

public interface ExtendedMBeanProvider extends StandardMBeanProvider {

	/**
	 * Query names with query pattern that is recognized as associated with the provider (based on recognized keys and values) 
	 * 
	 * @param name	MBean ObjectName query pattern
	 *  
	 * @return	Set containing the ObjectNames of all lazy mbeans that fit to the specified pattern 
	 */
	public Set queryNames(ObjectName name, QueryExp filter);
	
	public boolean isLazyRegistered(ObjectName name);
	
	public int getLazyMBeanCount();	
}