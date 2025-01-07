package com.sap.engine.services.dc.cm.server.impl;

import com.sap.engine.services.dc.cm.server.OfflineRestartServerRequest;
import com.sap.engine.services.dc.cm.server.OfflineServerModeRequest;
import com.sap.engine.services.dc.cm.server.RequestVisitor;
import com.sap.engine.services.dc.cm.server.RestartServerRequest;
import com.sap.engine.services.dc.cm.server.Server;
import com.sap.engine.services.dc.cm.server.ServerBootstrapRequest;
import com.sap.engine.services.dc.cm.server.ServerModeRequest;
import com.sap.engine.services.dc.cm.server.ServerService;
import com.sap.engine.services.dc.cm.server.ServerServiceRequest;
import com.sap.engine.services.dc.cm.server.ServerStateRequest;
import com.sap.engine.services.dc.cm.server.SoftwareTypeRequest;
import com.sap.engine.services.dc.cm.server.UnsupportedUndeployComponentsRequest;
import com.sap.engine.services.dc.cm.server.spi.SoftwareTypeService;

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
final class ServerImpl implements Server {

	private final ServerServiceRequestVisitor visitor;

	ServerImpl() {
		this.visitor = new ServerServiceRequestVisitor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.server.Server#getServerService(com.sap.
	 * engine.services.dc.cm.server.ServerServiceRequest)
	 */
	public ServerService getServerService(ServerServiceRequest request) {
		request.accept(this.visitor);

		return this.visitor.getServerService();
	}

	public ServerService initServerService(final ServerServiceRequest request) {
		return getServerService(request);
	}

	private static final class ServerServiceRequestVisitor implements
			RequestVisitor {

		private ServerService serverService;
		private static SoftwareTypeService defaultSoftwareTypeService;

		private ServerServiceRequestVisitor() {
		}

		ServerService getServerService() {
			return this.serverService;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.cm.server.RequestVisitor#visit(com.sap
		 * .engine.services.dc.cm.server.SoftwareTypeRequest)
		 */
		public void visit(SoftwareTypeRequest request) {
			if (request.isDefault()) {
				if (defaultSoftwareTypeService == null) {
					defaultSoftwareTypeService = new SoftwareTypeServiceImpl(
							request.getDeployReferencesDocument());
				}
				this.serverService = defaultSoftwareTypeService;
			} else {
				this.serverService = new SoftwareTypeServiceImpl(request
						.getDeployReferencesDocument());
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.cm.server.RequestVisitor#visit(com.sap
		 * .engine.services.dc.cm.server.ServerStateRequest)
		 */
		public void visit(ServerStateRequest request) {
			this.serverService = new ServerStateServiceImpl();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.cm.server.RequestVisitor#visit(com.sap
		 * .engine.services.dc.cm.server.ServerModeRequest)
		 */
		public void visit(ServerModeRequest request) {
			this.serverService = new ServerModeServiceImpl();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.cm.server.RequestVisitor#visit(com.sap
		 * .engine.services.dc.cm.server.OfflineServerModeRequest)
		 */
		public void visit(OfflineServerModeRequest request) {
			this.serverService = new OfflineServerModeServiceImpl(request
					.getConfigurationHandlerFactory());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.cm.server.RequestVisitor#visit(com.sap
		 * .engine.services.dc.cm.server.RestartServerRequest)
		 */
		public void visit(RestartServerRequest request) {
			this.serverService = new RestartServerServiceImpl();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.cm.server.RequestVisitor#visit(com.sap
		 * .engine.services.dc.cm.server.OfflineRestartServerRequest)
		 */
		public void visit(OfflineRestartServerRequest request) {
			this.serverService = new OfflineRestartServerServiceImpl(request
					.getConfigurationHandlerFactory(), request.getOsUserName(),
					request.getOsUserPass());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.cm.server.RequestVisitor#visit(com.sap
		 * .engine.services.dc.cm.server.UnsupportedUndeployComponentsRequest)
		 */
		public void visit(UnsupportedUndeployComponentsRequest request) {
			this.serverService = UnsupportedUndeployComponentsServiceImpl
					.getInstance();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.cm.server.RequestVisitor#visit(com.sap
		 * .engine.services.dc.cm.server.ServerBootstrapRequest)
		 */
		public void visit(ServerBootstrapRequest request) {
			this.serverService = new ServerBootstrapServiceImpl();
		}

	}

}
