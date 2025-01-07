package com.sap.engine.services.portletcontainer.exceptions;

import com.sap.engine.services.servlets_jsp.webcontainer_api.exceptions.WebContainerExtensionWarningException;
import com.sap.localization.LocalizableTextFormatter;

/**
 * @author Violeta Georgieva
 * @version 7.1
 */
public class WCEWarningException extends WebContainerExtensionWarningException {
  public static String WARNINGS_DURING_STARTING_PORTLET_APPLICATION = "portlet_0002";

  public WCEWarningException(String s, Object[] args, Throwable t) {
		super(new LocalizableTextFormatter(PortletResourceAccessor.getResourceAccessor(), s, args), t);
  } //end of constructor

  public WCEWarningException(String s, Throwable t) {
		super(new LocalizableTextFormatter(PortletResourceAccessor.getResourceAccessor(), s), t);
  } //end of constructor

  public WCEWarningException(String s, Object[] args) {
		super(new LocalizableTextFormatter(PortletResourceAccessor.getResourceAccessor(), s, args));
  } //end of constructor

  public WCEWarningException(String s) {
		super(new LocalizableTextFormatter(PortletResourceAccessor.getResourceAccessor(), s));
  } //end of constructor

} //end of class
