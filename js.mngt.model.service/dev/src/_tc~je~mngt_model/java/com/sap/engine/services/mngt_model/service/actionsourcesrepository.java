/*
 * Created on 2004-9-8
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.mngt_model.service;
import com.sap.engine.services.mngt_model.spi.ActionSource;
import com.sap.engine.services.mngt_model.spi.ServiceComponent;
import com.sap.engine.services.mngt_model.spi.ServicePlugin;

/**
 * @author hristo-s
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ActionSourcesRepository extends PluginRepository implements ServiceComponent {
	
	
	private ActionSource[] sources = new ActionSource[] {
		new com.sap.engine.services.mngt_model.plugins.LifecycleActionSource(),
		new com.sap.engine.services.mngt_model.plugins.ApplicationActionSource()
	};
	
	public ActionSourcesRepository() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected ServicePlugin[] getPlugins() {
		return sources;	
	}

	public void init() {
		initPlugins();
	}

	public void destroy() {
		destroyPlugins();
	}
}
