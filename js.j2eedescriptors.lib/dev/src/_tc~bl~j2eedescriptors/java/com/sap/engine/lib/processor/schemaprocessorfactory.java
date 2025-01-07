/*
 * Copyright (c) 2004-2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.processor;

import com.sap.engine.lib.processor.impl.*;

/**
 * @author Viktoriya Ivanova
 */
public class SchemaProcessorFactory {

  public static final int EJB = 1;
  public static final int EJBJ2EE = 2;
  public static final int EJBPERSISTENT = 3;
  public static final int WEB = 4;
  public static final int WEBJ2EE = 5;
  public static final int WEBJSPTLD = 6;
  public static final int APP = 7;
  public static final int APPJ2EE = 8;
  public static final int APPCLIENT = 9;
  public static final int APPCLIENTJ2EE = 10;
  public static final int CONNECTOR = 11;
  public static final int CONNECTORJ2EE = 12;
  public static final int PORTLET = 13;
  public static final int WEBSERVICES = 14;
  public static final int JAXRPCMAPPING = 15;
  public static final int WS04VI = 16;
  public static final int WS04WSD = 17;
  public static final int WS04WSRT = 18;
  public static final int WS04WSDD = 19;
  public static final int WS04CLIENTDD = 20;
  public static final int WS04CLIENTRT = 21;

  //Java EE 5.0
  public static final int WEBJSPTLD5 = 22;
  public static final int WEB5 = 23;
  public static final int APPCLIENT5 = 24;
  public static final int APPCLIENTJ2EE5 = 25;
  public static final int APP5 = 26;
  public static final int JSF = 27;
  public static final int WEBSERVICES5 = 28;

  public static SchemaProcessor getProcessor(int schemaId) {
    switch (schemaId) {
      case EJB: {
        return EjbProcessor.getInstance();
      }
      case EJBJ2EE: {
        return EjbJ2EEEngineProcessor.getInstance();
      }
      case EJBPERSISTENT: {
        return PersistentProcessor.getInstance();
      }
      case WEB: {
        return WebProcessor.getInstance();
      }
      case WEBJ2EE: {
        return WebJ2EEEngineProcessor.getInstance();
      }
      case APP: {
        return ApplicationProcessor.getInstance();
      }
      case APPJ2EE: {
        return ApplicationJ2EEEngineProcessor.getInstance();
      }
      case WEBJSPTLD: {
        return WebJspTLDProcessor.getInstance();
      }
      case APPCLIENT: {
        return AppclientProcessor.getInstance();
      }
      case APPCLIENTJ2EE: {
        return AppclientJ2EEEngineProcessor.getInstance();
      }
      case CONNECTOR: {
        return ConnectorProcessor.getInstance();
      }
      case CONNECTORJ2EE: {
        return ConnectorJ2EEEngineProcessor.getInstance();
      }
      case PORTLET: {
        return PortletProcessor.getInstance();
      }
      case WEBSERVICES: {
        return WebServicesProcessor.getInstance();
      }
      case WEBSERVICES5: {
          return WebServicesProcessor5.getInstance();
      }  
      case JAXRPCMAPPING: {
        return JaxRpcMappingProcessor.getInstance();
      }
      case WS04VI: {
        return WS04VIProcessor.getInstance();
      }
      case WS04WSD: {
        return WS04WSDProcessor.getInstance();
      }
      case WS04WSRT: {
        return WS04WSRTProcessor.getInstance();
      }
      case WS04WSDD: {
        return WS04WSDDProcessor.getInstance();
      }
      case WS04CLIENTDD: {
        return WS04ClientDDProcessor.getInstance();
      }
      case WS04CLIENTRT: {
        return WS04ClientRTProcessor.getInstance();
      }
      case WEBJSPTLD5: {
        return WebJspTLDProcessor5.getInstance();
      }
      case WEB5: {
        return WebProcessor5.getInstance();
      }
      case APPCLIENT5: {
          return AppclientProcessor5.getInstance();
      }
      case APPCLIENTJ2EE5: {
          return AppclientJ2EEEngineProcessor5.getInstance();
      }
      case APP5: {
    	  return ApplicationProcessor5.getInstance();
      }
      case JSF: {
        return WebFacesConfigProcessor.getInstance();
      }
    }
    return null;
  }


}
