package com.sap.i18n.service.tests;

import java.rmi.RemoteException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.sap.security.core.server.destinations.api.DestinationException;
import com.sap.security.core.server.destinations.api.DestinationService;
import com.sap.security.core.server.destinations.api.RFCDestination;

public class TestHelp {
	// ---- client connection data ----
	private static String jcoClient = "000";
	private static String jcoUser = "SAPJI18N4TST";
	private static String jcoPassword = "i18ntester";
	private static String jcoLanguage = "EN";
	
	// load balancing 
	private static String jcoMshost = "b20main.wdf.sap.corp";
	private static String jcoMsserv = "3634";
	private static String jcoGroup = "PUBLIC";
	private static String jcoR3name = "b20";
//	private static String LOAD_BALANCING = "LOAD_BALANCING";
	
	// special destination attributes
	private static String jcoType = "3";
	private static String destPeakLimit = "20";
	private static String destPoolCapacity = "10";
	
	static protected String DESTINATION_NAME = "I18NBackendConnection";
	
	public static void createDestination4Test()throws DestinationException, RemoteException, NamingException{
		Context ctx = new InitialContext();  
		DestinationService dstService =
			(DestinationService) ctx.lookup(DestinationService.JNDI_KEY);

		if (!dstService.getDestinationNames("RFC").contains(DESTINATION_NAME)) {
			RFCDestination dst = (RFCDestination) dstService.createDestination("RFC");
			Properties jcoProperties = getJCoProperties(); 
			dst.setJcoProperties(
					RFCDestination.AUTHENTICATION_MODE_CONFIGURED_USER,
					RFCDestination.CONNECTION_MODE_LOAD_BALANCING,
					jcoProperties);
			dst.setName(DESTINATION_NAME);
			dstService.storeDestination("RFC", dst);
		}
	}
	
	private static Properties getJCoProperties() {
		Properties jcoProperties = new Properties();
		jcoProperties.setProperty("jco.client.client", jcoClient);
		jcoProperties.setProperty("jco.client.user", jcoUser);
		jcoProperties.setProperty("jco.client.passwd", jcoPassword);
		jcoProperties.setProperty("jco.client.lang", jcoLanguage);
		
		jcoProperties.setProperty("jco.client.r3name", jcoR3name);
		jcoProperties.setProperty("jco.client.group", jcoGroup);
		jcoProperties.setProperty("jco.client.mshost", jcoMshost);
		jcoProperties.setProperty("jco.client.msserv", jcoMsserv);
		
		jcoProperties.setProperty("jco.client.type", jcoType);
		jcoProperties.setProperty("jco.destination.peak_limit", destPeakLimit);
		jcoProperties.setProperty("jco.destination.pool_capacity", destPoolCapacity);
		
		return jcoProperties;
	}
}
