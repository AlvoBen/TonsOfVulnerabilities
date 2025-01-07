package com.sap.jmx.provider;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.sap.pj.jmx.server.interceptor.InvocationContext;

/**
 * This interface is implemented from the ProviderInterceptor and is accessed from the Provider Registry 
 * and Lazy Cache systems.  
 * 
 * @author i024127
 *
 */
public interface MBeanServerAccess {

	/**
	 * Register MBean to the MBean server
	 * 
	 * @param name	Name of the mbean
	 * @param object	Instance of the MBean
	 * 
	 * @throws NotCompliantMBeanException	If exception occurs during registration
	 * @throws InstanceAlreadyExistsException	If exception occurs during registration
	 * @throws MBeanRegistrationException	If exception occurs during registration
	 */
	public void registerToMBeanServer(ObjectName name, Object object) throws NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException;
	
	/**
	 * Unregister MBean from the MBean Server
	 * 
	 * @param name	Name of the mbeam
	 * 
	 * @throws InstanceNotFoundException	If exception occurs during unregistration
	 * @throws MBeanRegistrationException	If exception occurs during unregistration
	 */
	public void unregisterFromMBeanServer(ObjectName name)	throws InstanceNotFoundException, MBeanRegistrationException;
	
	/**
	 * Unregister MBean from the interceptor and MBean server
	 * 
	 * @param context	Invocation context
	 * @param name	Name of the mbean
	 * 
	 * @throws InstanceNotFoundException	If exception occurs during unregistration
	 * @throws MBeanRegistrationException	If exception occurs during unregistration
	 */
	public void unregisterMBean(InvocationContext context, ObjectName name)	throws InstanceNotFoundException, MBeanRegistrationException;
	
	/**
	 * Invalidate MBean from the interceptor and MBean server. If the MBean is not found into the cache
	 * there is no try to unregister it from the MBean Server (this is the difference compare to the 
	 * unregisterFromMBeanServer method) 
	 * 
	 * @param context	Invocation context
	 * @param name	Name of the mbean
	 * @param wait	Wait to finish all calls to the MBean or do the invalidation immediately  
	 * 
	 * @throws InstanceNotFoundException	If exception occurs during unregistration
	 * @throws MBeanRegistrationException	If exception occurs during unregistration
	 */
	public void invalidateMBean(ObjectName name, boolean wait) throws InstanceNotFoundException, MBeanRegistrationException;
	
	/**
	 * Get MBean server default domain
	 * 
	 * @return the default domain
	 */
	public String getDefaultDomain();
	
	public ObjectName getFullName(ObjectName name);
	
	public ObjectName getLocalSAP_ITSAMJ2eeNode();
	
}