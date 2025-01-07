package com.sap.engine.services.dc.cm.server;

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
public interface RequestVisitor {

	public void visit(SoftwareTypeRequest request);

	public void visit(ServerStateRequest request);

	public void visit(ServerModeRequest request);

	public void visit(OfflineServerModeRequest request);

	public void visit(RestartServerRequest request);

	public void visit(OfflineRestartServerRequest request);

	public void visit(UnsupportedUndeployComponentsRequest request);

	public void visit(ServerBootstrapRequest request);

}
