package com.sap.engine.lib.deploy.sda.exceptions;

import com.sap.localization.ResourceAccessor;

/**
 * @author Radoslav Popov
 * 
 */
public class SdaResourceAccessor extends ResourceAccessor {
	static final long serialVersionUID = 6551879881371492666L;

	private static final String BUNDLE_NAME = "com.sap.engine.lib.deploy.sda.exceptions.SdaResourceBundle";

	private static final SdaResourceAccessor instance = new SdaResourceAccessor();

	private SdaResourceAccessor() {
		super(BUNDLE_NAME);
	}

	public static SdaResourceAccessor getInstance() {
		return instance;
	}

}