/*
 * Created on 2004-9-2
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.mngt_model.service;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ApplicationServiceFrame;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.services.mngt_model.spi.PluginException;
import com.sap.engine.services.mngt_model.spi.ServiceComponent;
import com.sap.engine.services.mngt_model.spi.ServicePlugin;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * @author Hristo-S
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ServiceFrame extends PluginRepository implements ApplicationServiceFrame {
	
	private static ServiceFrame serviceInstance;
	public static final Location location = Location.getLocation(ServiceFrame.class.getPackage());
	
	private static final int SELF_STATE_RUNNING = 1;
	private static final int SELF_STATE_STOPPING = 2;
	private static final int SELF_STATE_STARTING = 4;
	private static final int SELF_STATE_STARTED = 8;
	private static final int SELF_STATE_STOPPED = 16;
	
	private ApplicationServiceContext asc;
	private int state = SELF_STATE_STOPPED;
	private ServiceComponent[] sc;
	
	static {
		location.setResourceBundleName("com/sap/engine/services/mngt_model/localization.properties");	
	}
	
	public ServiceFrame() {
		super();
	}
	
	public ServicePlugin[] getPlugins() {
		return sc;
	}
	
	private void createServiceComponents() {
		sc	= new ServiceComponent[] {
				new ServiceEventProvider(),
				new ActionFiltersRepository(),
				new DomainRegistry(),
				new ActionSourcesRepository()	
			};
	}
	
	
	public synchronized void start(ApplicationServiceContext asc) throws ServiceException {
		state = SELF_STATE_STARTING;
		synchronized(ServiceFrame.class) {
			assert this.asc == null;
			this.asc = asc;		
			serviceInstance = this;
		}
		
		createServiceComponents();
		
		try {
			this.initPluginsStopOnError();
		} catch(PluginException pe) {
			throw new ServiceException("fatal.service.start", null, pe);
		}
		
		state = SELF_STATE_STARTED;
	}

	public void stop() {
		assertServiceNotStopped();
		try {
			destroyPlugins();
		} finally {
			state = SELF_STATE_STOPPED;
			serviceInstance = null;
			sc = null;
		}
	}

	public synchronized ApplicationServiceContext getServiceContext() {
		assertServiceNotStopped();
		return asc;
	}
	
	public synchronized String getServerName() {
		assertServiceNotStopped();
		return asc.getClusterContext().getClusterMonitor()
			.getCurrentParticipant().getName();
	}
	
	public static ServiceFrame getServiceInstance() {
		serviceInstance.assertServiceNotStopped();	
		return serviceInstance;
	}
	
	private void assertServiceNotStopped() {
		if (serviceInstance != this) {
			throw new ServiceNotStartedException();
		}
		if ((state & SELF_STATE_STOPPED) != 0) {
			throw new ServiceNotStartedException();
		}
	}
	
	public String getServiceName() {
		assertServiceNotStopped();
		return asc.getServiceState().getServiceName();	
	}
	
	public static final boolean debugMode() {
		return ServiceFrame.location.getEffectiveSeverity() <= Severity.DEBUG;	
	}
}
