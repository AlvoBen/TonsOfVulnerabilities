/**
 * 
 */
package com.sap.persistence.monitors.ws.trace_v1;

import javax.ejb.Stateless;


import com.sap.persistence.monitors.ws.TraceHelper;

import com.sap.persistence.monitors.ws.types.trace.TStatus;
import com.sap.persistence.monitors.ws.types.trace.TStatusProperty;
import com.sap.persistence.monitors.ws.types.common.TServiceInfo;

import com.sap.tc.logging.Location;

import javax.jws.WebService;
import com.sap.engine.services.webservices.espbase.configuration.ann.dt.AuthenticationDT;
import com.sap.engine.services.webservices.espbase.configuration.ann.dt.AuthenticationEnumsAuthenticationLevel;

/**
 * @author d022280
 *
 */

@AuthenticationDT(authenticationLevel=AuthenticationEnumsAuthenticationLevel.BASIC)
@WebService(targetNamespace="http://sap.com/persistence/monitors/ws/trace-v1", portName="PMTracePortV10", serviceName="PMTraceServiceV10", endpointInterface="com.sap.persistence.monitors.ws.trace_v1.PersistenceMonitorsTraceV10", wsdlLocation="META-INF/wsdl/com/sap/persistence/monitors/ws/trace_v1/PersistenceMonitorsTrace-v1/PersistenceMonitorsTrace-v1.wsdl")
@Stateless
public class PersistenceMonitorsTraceV10ImplBean {
	
	
	
	private static final Location TRACE = 
        Location.getLocation(PersistenceMonitorsTraceV10ImplBean.class.getName(),"persistence_monitors_lib","BC-JAS-PER-SQL");


	

	public  com.sap.persistence.monitors.ws.types.trace.TStatus getStatus(com.sap.persistence.monitors.ws.types.common.TNodeSelection statusSelection) throws FaultInfoResponse{
		TRACE.debugT("Entering com.sap.persistence.monitors.ws.trace_v1.PersistenceMonitorsTraceV10ImplBean.getStatus()");
		TraceHelper th = new TraceHelper();
		TStatus ts = th.getStatus(statusSelection);
		return ts;
	 }

	public  com.sap.persistence.monitors.ws.types.common.TServiceInfo getServiceInfo(boolean pingMBean) {
		TraceHelper th = new TraceHelper();
		  TServiceInfo si = th.getServiceInfo(pingMBean);
		  return si;
	 }

	public  com.sap.persistence.monitors.ws.types.trace.TStatus switchOn(com.sap.persistence.monitors.ws.types.common.TNodeSelection switchOnSelection) throws FaultInfoResponse{
		TraceHelper th = new TraceHelper();
		
		th.switchOn(switchOnSelection);
		switchOnSelection.setIgnoreNodeFilter(true);
		TStatus ts = th.getStatus(switchOnSelection);
		return ts;
	 }

	public  com.sap.persistence.monitors.ws.types.trace.TStatus switchOnHighLevel(com.sap.persistence.monitors.ws.types.common.TNodeSelection highLevelSelection) throws FaultInfoResponse {
		TraceHelper th = new TraceHelper();
		
		
		th.switchOnHighLevel(highLevelSelection);
		highLevelSelection.setIgnoreNodeFilter(true);
		TStatus ts = th.getStatus(highLevelSelection);
		return ts;
	 }

	public  com.sap.persistence.monitors.ws.types.trace.TStatus switchOff(com.sap.persistence.monitors.ws.types.common.TNodeSelection switchOffSelection) throws FaultInfoResponse{
	TraceHelper th = new TraceHelper();
		
		th.switchOff(switchOffSelection);
		switchOffSelection.setIgnoreNodeFilter(true);
		TStatus ts = th.getStatus(switchOffSelection);
		return ts;
	}
	public com.sap.persistence.monitors.ws.types.trace.TStatusProperty getDetailedStatus(com.sap.persistence.monitors.ws.types.trace.TDetailedStatusSelection detailedStatusSelection) throws FaultInfoResponse{


	
		TRACE.debugT("Entering com.sap.persistence.monitors.ws.trace_v1.PersistenceMonitorsTraceV10ImplBean.getDetailedStatus");
		TraceHelper th = new TraceHelper();
		TStatusProperty gpl = th.getStatusPropertyList(detailedStatusSelection);
		return gpl;
	 }

}
