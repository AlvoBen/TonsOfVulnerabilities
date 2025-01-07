/*
 * Created on 2004-9-21
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.mngt_model.service;

import com.sap.engine.services.mngt_model.spi.PluginException;
import com.sap.engine.services.mngt_model.spi.PluginNotFoundException;
import com.sap.engine.services.mngt_model.spi.ServicePlugin;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Severity;

/**
 * @author hristo-s
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
abstract class PluginRepository {

	protected abstract ServicePlugin[] getPlugins();
		 
	public synchronized ServicePlugin getPlugin(Class pgc) {
		ServicePlugin[] plugins = getPlugins();
		for (int i = 0; i < plugins.length; i++) {
			if (pgc.isInstance(plugins[i])) {
				return plugins[i];			
			}
		}
		throw new PluginNotFoundException(pgc.getName());
	}
	
	public synchronized void initPlugins() {
		ServicePlugin[] plugins = getPlugins();
		for	(int i = 0; i < plugins.length; i++) {
			try {
				plugins[i].init();	 
			} catch (PluginException ase) {
				Category.SYS_SERVER.logThrowable(Severity.ERROR,
					ServiceFrame.location,(Object)"error.plugin.init", new Object[] {plugins[i].getClass()},
					"Error initializing plugin of type " + plugins[i].getClass() + ". Initialization of other plugins continues.", ase);
			}
		}		
	}
	
	public synchronized void initPluginsStopOnError() throws PluginException {
		ServicePlugin[] plugins = getPlugins();
		for (int i = 0; i < plugins.length; i++) {
			try {
				plugins[i].init();
			} catch (Exception e) {
				Category.SYS_SERVER.logThrowable(Severity.ERROR, ServiceFrame.location,
					(Object)"", new Object[] {plugins[i].getClass()} , "Error while initializing plugin " + plugins[i].getClass() + ".  Initalization of other plugins will not continue. Destroying allready initalized plugins.", e);
					destroyInternal(i);
					throw new PluginException(e);	
			}
		}
	}
	
	private synchronized void destroyInternal(int last) {
		ServicePlugin[] plugins = getPlugins();
		for (int i = 0; i < last; i++) {
			try {
				plugins[i].destroy();
			} catch (Exception e) {
				Category.SYS_SERVER.logThrowable(Severity.ERROR, ServiceFrame.location,
					(Object)"", new Object[] {plugins[i].getClass()}, "Error while destroying plugin " + plugins[i].getClass()  + ". Destroying of other plugins in this repository contineues", e);		
			}
		}
	}
	
	public synchronized void destroyPlugins() {
		destroyInternal(getPlugins().length);	
	}
	
}
