/*
 * Created on 2004-9-7
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.mngt_model.spi;

import com.sap.engine.services.mngt_model.spi.DomainAttributes;

/**
 * @author hristo-s
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface ActionFilter extends ServicePlugin {
	public boolean filterAction(ActionDelivery ad, DomainAttributes da)
	throws ActionFilteringException;	
}
