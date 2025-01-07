/*
 * Created on 31.01.2006
 * Author D037363
 */
package com.sap.security.core.server.ume.service.monitor.impl;

import com.sap.engine.frame.state.ManagementListener;
import com.sap.security.core.InternalUMFactory;
import com.sap.security.core.persistence.datasource.IPersistenceMonitoring;
import com.sap.security.core.server.ume.service.monitor.SecurityUMEManagementInterface;
import com.sap.tc.logging.Location;

/**
 * @author D037363
 *
 */
public class SecurityUMEManagementInterfaceImpl
	implements SecurityUMEManagementInterface {
		
	private IPersistenceMonitoring monitor = null;
	private Location loc = Location.getLocation(SecurityUMEManagementInterfaceImpl.class);
	private ManagementListener listener = null;
	
	private int DEFAULT = -1;
	
	private boolean instantiated = false;
	
	private void init() {
		try {
			if (loc.beInfo())	
				loc.infoT("init", "Initializing J2EE-UME-Monitoring");
			if (monitor == null) {
				monitor = InternalUMFactory.getMonitoring();
				instantiated = true;
			}	
		}
		catch (Throwable t) {
			loc.errorT("init", "LDAP Monitoring not initialized due to error {0}", new Object[] {t});
		}
	}

	public long getLDAPCommunicationErrors() {
		init();
		if (instantiated)
			return monitor.getLDAPCommunicationErrorCount();
		else return DEFAULT;
	}

	public int getLDAPPoolExhaustionCount() {
		init();
		if (instantiated)
			return monitor.getLDAPPoolExhaustionCount();
		else return DEFAULT;
	}


	public long getLDAPFallbackConnectionCount() {
		init();
		if (instantiated)
			return monitor.getLDAPFallbackConnectionCount();
		else return DEFAULT;
	}


	public long getLDAPMainConnectionCount() {
		init();
		if (instantiated)
			return monitor.getLDAPMainConnectionCount();
		else return DEFAULT; 
	}


	public long getLDAPMainConnectionUsage() {
		init();
		if (instantiated)
			return monitor.getLDAPMainConnectionUsage();
		else return DEFAULT;
	}


	public long getLDAPMainRequestCount() {
		init();
		if (instantiated)
			return monitor.getLDAPMainRequestCount();
		else return DEFAULT;
	}

	
	public long getLDAPRequestCount() {
		init();
		if (instantiated)
			return monitor.getLDAPAllRequestCount();
		else return DEFAULT;
	}


	public long getLDAPMainServerUsage() {
		init();
		if (instantiated)
			return monitor.getLDAPMainRequestUsage();
		else return DEFAULT; 
	}


	public void registerManagementListener(ManagementListener arg0) {
		//do nothing as data is requested by monitoring framework 
	}
}
