/*
 * Created on 2005-1-3
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.lib.schema.components;

import com.sap.engine.lib.schema.components.impl.LoaderImpl;

/**
 * @author ivan-m
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SchemaLoaderFactory {
	
	public static Loader create() {
		return(new LoaderImpl());
	}
}
