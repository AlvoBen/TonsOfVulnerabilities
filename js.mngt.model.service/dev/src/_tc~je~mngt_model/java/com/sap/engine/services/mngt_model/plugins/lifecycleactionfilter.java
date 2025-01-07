/*
 * Created on 2004-9-9
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.mngt_model.plugins;

import java.util.Hashtable;

import com.sap.engine.management.action.LifecycleAction;
import com.sap.engine.services.mngt_model.spi.ActionDelivery;
import com.sap.engine.services.mngt_model.spi.ActionFilter;
import com.sap.engine.services.mngt_model.spi.DomainAttributes;

/**
 * @author hristo-s
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LifecycleActionFilter implements ActionFilter {

	private Hashtable duplicates = new Hashtable();
	
	public LifecycleActionFilter() {
		super();
		// TODO Auto-generated constructor stub
	}

	public boolean filterAction(ActionDelivery ad, DomainAttributes da) {
		if (!(ad.getDeliveredAction() instanceof LifecycleAction)) return false; //passing it through
		LifecycleAction la = (LifecycleAction)ad.getDeliveredAction();
		if (ad.isMassDelivery())
			return false;
		if(da.getDomainName().equals(ad.getDestinationDomain())) {
			LifecycleAction lastla = (LifecycleAction)duplicates.put(da.getDomainName(), la);
			if (lastla !=null && lastla.getAction() == la.getAction()) {
				return true;
			}
			return false;
		}
		return true;
	}
	
	public void init() {
		
	}
	
	public void destroy() {
	}
}
