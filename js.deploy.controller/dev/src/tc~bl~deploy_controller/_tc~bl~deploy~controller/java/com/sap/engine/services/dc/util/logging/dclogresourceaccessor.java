/*
 * Created on 2005-5-3 by radoslav-i
 */
package com.sap.engine.services.dc.util.logging;

import java.util.Locale;
import java.text.MessageFormat;

import com.sap.engine.services.dc.util.Constants;
import com.sap.localization.ResourceAccessor;

/**
 * @author radoslav-i
 */
public class DCLogResourceAccessor extends ResourceAccessor {

	private static final long serialVersionUID = 3922579335430129614L;

	private transient static final String BUNDLE_NAME = "com.sap.engine.services.dc.util.logging.resources.ResourceBundle_en";
	private static final DCLogResourceAccessor INSTANCE = new DCLogResourceAccessor();

	private DCLogResourceAccessor() {
		super(BUNDLE_NAME);
	}

	public static DCLogResourceAccessor getInstance() {
		return INSTANCE;
	}

	private String getMessageText(Locale locale, String key, Object[] args) {
		String msg = super.getMessageText(locale, key);
		return MessageFormat.format(msg, args);
	}

	public String getMessageText(String key, Object[] args) {
		return getMessageText(Constants.DC_LOCALE, key, args);
	}

	public String getMessageText(Locale locale, String key) {
		return getMessageText(Constants.DC_LOCALE, key, null);
	}

	public String getMessageText(String key) {
		return getMessageText(key, null);
	}
}
