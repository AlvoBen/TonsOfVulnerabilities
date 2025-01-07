package com.sap.engine.services.dc.cm.server.impl;

import java.io.InputStream;

import org.w3c.dom.Document;

import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.services.dc.cm.server.OfflineRestartServerRequest;
import com.sap.engine.services.dc.cm.server.OfflineServerModeRequest;
import com.sap.engine.services.dc.cm.server.RestartServerRequest;
import com.sap.engine.services.dc.cm.server.Server;
import com.sap.engine.services.dc.cm.server.ServerBootstrapRequest;
import com.sap.engine.services.dc.cm.server.ServerFactory;
import com.sap.engine.services.dc.cm.server.ServerModeRequest;
import com.sap.engine.services.dc.cm.server.ServerStateRequest;
import com.sap.engine.services.dc.cm.server.SoftwareTypeRequest;
import com.sap.engine.services.dc.cm.server.UnsupportedUndeployComponentsRequest;
import com.sap.engine.services.dc.util.FileUtils;
import com.sap.engine.services.dc.util.xml.XMLFactory;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-28
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class ServerFactoryImpl extends ServerFactory {

	private static SoftwareTypeRequest defaultSoftwareTypeRequest = null;

	public ServerFactoryImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.server.ServerFactory#createServer()
	 */
	public Server createServer() {
		return new ServerImpl();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.server.ServerFactory#createSoftwareTypeRequest
	 * ()
	 */

	public synchronized SoftwareTypeRequest createSoftwareTypeRequest() {
		if (defaultSoftwareTypeRequest != null) {
			return defaultSoftwareTypeRequest;
		}

		InputStream xmlInput = null;
		InputStream xsdInput = null;
		try {
			xmlInput = this.getClass().getClassLoader().getResourceAsStream(
					SoftwareTypeRequest.DEFAULT_DEPLOY_REFERENCES_XML);
			xsdInput = this.getClass().getClassLoader().getResourceAsStream(
					SoftwareTypeRequest.DEFAULT_DEPLOY_REFERENCES_XSD);

			defaultSoftwareTypeRequest = new SoftwareTypeRequestImpl(
					createXmlDocument(xmlInput, xsdInput), true);

			return defaultSoftwareTypeRequest;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			FileUtils.safeCloseInputStream(xmlInput);
			FileUtils.safeCloseInputStream(xsdInput);
		}
	}

	public SoftwareTypeRequest createSoftwareTypeRequest(Document cfgDocument) {
		if (cfgDocument == null) {
			return this.createSoftwareTypeRequest();
		}
		return new SoftwareTypeRequestImpl(cfgDocument);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.server.ServerFactory#createServerStateRequest
	 * ()
	 */
	public ServerStateRequest createServerStateRequest() {
		return new ServerStateRequestImpl();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.server.ServerFactory#createServerModeRequest
	 * ()
	 */
	public ServerModeRequest createServerModeRequest() {
		return new ServerModeRequestImpl();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.server.ServerFactory#
	 * createOfflineServerModeRequest()
	 */
	public OfflineServerModeRequest createOfflineServerModeRequest(
			ConfigurationHandlerFactory configurationHandlerFactory) {
		return new OfflineServerModeRequestImpl(configurationHandlerFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.server.ServerFactory#createRestartServerRequest
	 * ()
	 */
	public RestartServerRequest createRestartServerRequest() {
		return new RestartServerRequestImpl();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.server.ServerFactory#
	 * createOfflineRestartServerRequest()
	 */
	public OfflineRestartServerRequest createOfflineRestartServerRequest(
			ConfigurationHandlerFactory configurationHandlerFactory,
			String osUserName, String osUserPass) {
		return new OfflineRestartServerRequestImpl(configurationHandlerFactory,
				osUserName, osUserPass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.server.ServerFactory#
	 * createUnsupportedUndeployComponentsRequest()
	 */
	public UnsupportedUndeployComponentsRequest createUnsupportedUndeployComponentsRequest() {
		return new UnsupportedUndeployComponentsRequestImpl();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.server.ServerFactory#
	 * createServerBootstrapRequest()
	 */
	public ServerBootstrapRequest createServerBootstrapRequest() {
		return new ServerBootstrapRequestImpl();
	}

	private Document createXmlDocument(final InputStream xml,
			final InputStream xsd) throws Exception {
		return XMLFactory.getInstance().createDocument(xml, xsd);
	}

}
