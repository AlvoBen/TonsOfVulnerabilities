/*
 * Created on 2004-9-8
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.mngt_model.service;

import com.sap.engine.services.mngt_model.spi.ActionFilter;
import com.sap.engine.services.mngt_model.spi.ServiceComponent;
import com.sap.engine.services.mngt_model.spi.ServicePlugin;
/**
 * @author hristo-s
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ActionFiltersRepository extends PluginRepository implements ServiceComponent {

	private ActionFilter[]  filters = new ActionFilter[] {
		new com.sap.engine.services.mngt_model.plugins.ApplicationActionFilter(),
		new com.sap.engine.services.mngt_model.plugins.LifecycleActionFilter()
	};
 
	public ActionFiltersRepository() {
		super();
	}
	
	public ActionFilter[] getActionFilters() {
		 return filters;
	}
	
	public ServicePlugin[] getPlugins() {
		return filters;
	}

	public void init() {
		initPlugins();
	}

	public void destroy() {
		destroyPlugins();
	}

}
