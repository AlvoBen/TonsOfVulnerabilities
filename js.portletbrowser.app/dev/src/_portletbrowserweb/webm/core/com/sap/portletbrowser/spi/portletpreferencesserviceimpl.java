package com.sap.portletbrowser.spi;

import javax.portlet.PortletPreferences;

import com.sap.engine.services.portletcontainer.spi.PortletPreferencesService;

public class PortletPreferencesServiceImpl implements PortletPreferencesService
{

	private PortletPreferences portletPreferences;
	
	public PortletPreferencesServiceImpl(PortletPreferences portletPreferences) {
		this.portletPreferences = portletPreferences;
	}
	
	public PortletPreferences getPortletPreferences() {
		return portletPreferences;
	}

	public void setPortletPreferences(PortletPreferences portletPreferences) {
		this.portletPreferences = portletPreferences;
	}
}
