/*
 * Created on 2004-9-2
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.mngt_model.spi;

/**
 * @author Hristo-S
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface ActionSource extends ServicePlugin {
	public void init() throws ActionSourceException;
	public void destroy() throws ActionSourceException;
}
