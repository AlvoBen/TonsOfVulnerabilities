/*
 * Created on 2004-9-10
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.mngt_model.spi;

import com.sap.engine.management.action.ModelAction;

/**
 * @author hristo-s
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ActionDelivery {

	private final ModelAction ma;
	private String domain;
	
	public ActionDelivery(ModelAction ma) {
		if (ma == null)
			throw new NullPointerException("ma");
		this.ma = ma;	
	}

	public ActionDelivery(ModelAction ma, String domain) {
		this (ma);
		if (domain == null) throw new NullPointerException("domain");
		if (domain.length() == 0) throw new IllegalArgumentException("domain");
		this.domain = domain;	
	}
	
	public ModelAction getDeliveredAction() {
		return ma;
	}
	
	public boolean isMassDelivery() {
		return domain == null;
	}
	
	public String getDestinationDomain() {
		if (isMassDelivery())
			throw new IllegalStateException();
		return domain;
	}
}
