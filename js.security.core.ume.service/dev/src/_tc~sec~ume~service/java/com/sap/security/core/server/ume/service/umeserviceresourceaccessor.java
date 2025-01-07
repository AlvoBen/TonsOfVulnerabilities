package com.sap.security.core.server.ume.service;

import com.sap.localization.ResourceAccessor;

/**
 * @author d031387
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class UMEServiceResourceAccessor extends ResourceAccessor {

	private static final String BUNDLE_NAME = 
			"com.sap.security.core.server.ume.service.UMEServiceResources";
			
	private static ResourceAccessor resAccessor = null;

	/**
	 * Constructor for UserStoreResourceAccessor.
	 * @param bundleName
	 */
	public UMEServiceResourceAccessor() {
		super(BUNDLE_NAME);
	}

	public static synchronized ResourceAccessor getResourceAccessor() {
		if (resAccessor == null) {
			resAccessor = new UMEServiceResourceAccessor();
		}
		return resAccessor;
	}
}
