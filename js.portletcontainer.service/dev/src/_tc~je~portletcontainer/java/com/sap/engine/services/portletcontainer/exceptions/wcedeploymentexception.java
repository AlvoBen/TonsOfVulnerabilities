package com.sap.engine.services.portletcontainer.exceptions;

import com.sap.engine.services.servlets_jsp.webcontainer_api.exceptions.WebContainerExtensionDeploymentException;
import com.sap.localization.LocalizableTextFormatter;

/**
 * @author Violeta Georgieva
 * @version 7.1
 */
public class WCEDeploymentException extends WebContainerExtensionDeploymentException{
  public static String ERROR_LOADING_PORTLET_XML = "portlet_0000";
  public static String CANNOT_LOAD_WEB_MODULE = "portlet_0001";
  public static String ERROR_IN_STARTING_PORTLET_APPLICATION = "portlet_0002";
  public static String APPLICATION_CLASSLOADER_FOR_APPLICATION_IS_NULL = "portlet_0003";
  public static String ERROR_READING_WEB_XML = "portlet_0004";

  public WCEDeploymentException(String s, Object[] args, Throwable t) {
		super(new LocalizableTextFormatter(PortletResourceAccessor.getResourceAccessor(), s, args), t);
  } //end of constructor

  public WCEDeploymentException(String s, Throwable t) {
		super(new LocalizableTextFormatter(PortletResourceAccessor.getResourceAccessor(), s), t);
  } //end of constructor

  public WCEDeploymentException(String s, Object[] args) {
		super(new LocalizableTextFormatter(PortletResourceAccessor.getResourceAccessor(), s, args));
  } //end of constructor


  public WCEDeploymentException(String s) {
		super(new LocalizableTextFormatter(PortletResourceAccessor.getResourceAccessor(), s));
  } //end of constructor

} //end of class

