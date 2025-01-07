/*
 * Created on 2007-6-25
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.security.jmx.sso2.impl;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;

import com.sap.engine.services.security.jmx.sso2.SystemInfo;
import com.sap.jmx.modelhelper.ChangeableCompositeData;
import com.sap.jmx.modelhelper.OpenTypeFactory;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * @author I043568
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SystemInfoImpl
	extends ChangeableCompositeData
	implements SystemInfo 
{

	private static final String EMPTY = "";
	private static final String SYSTEM_TIME = "SystemTime";
	private static final String DOMAIN_NAME = "DomainName";


	private static transient Location loc = Location.getLocation(SystemInfoImpl.class);

	private static CompositeType COMPOSITE_TYPE;

	static 
	{
		try 
		{
			COMPOSITE_TYPE = OpenTypeFactory.getCompositeType(SystemInfo.class);
		} 
		catch (OpenDataException exc) 
		{
			SimpleLogger.traceThrowable(Severity.ERROR, loc, exc, "ASJ.secsrv.009531", "Failed to get composite type SystemInfo");
		}
	}
	
	
	public SystemInfoImpl() 
	{
		super(COMPOSITE_TYPE);
	}

	
	/**
	 * @param type
	 */
	public SystemInfoImpl(CompositeType type) 
	{
		super(type);
	}


	/**
	 * @param data
	 */
	public SystemInfoImpl(CompositeData data) 
	{
		super(data);
	}


	/**
	 * 
	 * @param systemTime
	 * @param domainName
	 */
	public SystemInfoImpl(long systemTime, String domainName)
	{
		this();
		setSystemTime(systemTime);
		setDomainName(domainName);
	}


	/**
	 * @see com.sap.engine.services.security.jmx.sso2.SystemInfo#getSystemTime()
	 */
	public long getSystemTime() 
	{
		Long systemTime = (Long)get(SYSTEM_TIME);		
		if (systemTime != null) 
		{
			return systemTime.intValue();
		}
		else
		{
			return SYSTEM_TIME_UNKNOWN;			
		}
	}


	/**
	* 
	* @param systemTime
	*/
	public void setSystemTime(long systemTime)
	{
		set(SYSTEM_TIME, new Long(systemTime));
	}


	/**
	 * @see com.sap.engine.services.security.jmx.sso2.SystemInfo#getDomainName()
	 */
	public String getDomainName() 
	{
		return (String)get(DOMAIN_NAME);
	}


	/**
	 * 
	 * @param domainName
	 */
	public void setDomainName(String domainName)
	{
		set(DOMAIN_NAME, domainName);
	}

}
