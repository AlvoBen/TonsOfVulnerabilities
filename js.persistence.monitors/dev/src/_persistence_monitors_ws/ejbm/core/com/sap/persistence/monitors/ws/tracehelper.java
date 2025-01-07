package com.sap.persistence.monitors.ws;



import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;

import com.sap.persistence.monitors.common.NodeSelection;
import com.sap.persistence.monitors.common.PerMonException;
import com.sap.persistence.monitors.common.PerMonFactory;
import com.sap.persistence.monitors.sql.trace.NodeStatus;
import com.sap.persistence.monitors.sql.trace.TraceManager;


import com.sap.persistence.monitors.ws.trace_v1.FaultInfoResponse;
import com.sap.persistence.monitors.ws.types.common.TFaultInfo;
import com.sap.persistence.monitors.ws.types.common.TNodeSelection;
import com.sap.persistence.monitors.ws.types.common.TServiceInfo;
import com.sap.persistence.monitors.ws.types.trace.TDetailedStatusSelection;
import com.sap.persistence.monitors.ws.types.trace.TNodeStatus;
import com.sap.persistence.monitors.ws.types.trace.TNodeStatusProperty;
import com.sap.persistence.monitors.ws.types.trace.TStatus;
import com.sap.persistence.monitors.ws.types.trace.TStatusProperty;
import com.sap.tc.logging.Location;

public class TraceHelper {
	
	
	
	private static final Location TRACE = 
        Location.getLocation(TraceHelper.class.getName(),"persistence_monitors_ws_app","BC-JAS-PER-SQL");


	public TStatus getStatus(TNodeSelection nodeSelection) throws FaultInfoResponse {
	
		TStatus tos = new TStatus();		
		SortedMap<String,NodeStatus> result = null;
		TraceManager tm = null;
		
		TRACE.debugT("Entering com.sap.persistence.monitors.ws.TraceHelper.getStatus()");
		
		
		
		try {
			tm = PerMonFactory.createTraceManager();
		} catch (PerMonException e1) {
		
	
			String excText = "Error calling getStatus - Error creating the Trace Manager" ;
			TRACE.errorT(excText);
			TFaultInfo tfi = new TFaultInfo();
			tfi.setText(excText);
			tfi.setRetcode(-1);
			
			FaultInfoResponse fie = new FaultInfoResponse(
					e1.getMessage(),
					tfi,
					e1);
					
					throw fie;
			
		}
		
		
		try {
			 NodeSelection nodeSel = obtainNodeSelection(nodeSelection) ;
			 TRACE.debugT("com.sap.persistence.monitors.ws.TraceHelper.getStatus() - Init finished");
			 result = tm.getStatus(nodeSel);
			 TRACE.debugT("com.sap.persistence.monitors.ws.TraceHelper.getStatus() - Exec ok");
			 
			 if (result == null){
				
				 String excText = "Error calling getStatus - response is null";
				 TRACE.errorT(excText);
				 
				 TFaultInfo tfi = new TFaultInfo();
				 tfi.setText(excText);
				 tfi.setRetcode(-1);
					
				 FaultInfoResponse fie = new FaultInfoResponse(
							"Response is null",
							tfi	);
					 
					throw fie;
			 } else {
				 TRACE.debugT("com.sap.persistence.monitors.ws.TraceHelper.getStatus() - result has "+result.size()+" entries"); 
			 }
			 
		} catch (PerMonException e0){
			
			String excText = "Error calling getStatus - Error creating the Node Selection";
			TRACE.errorT(excText);
			
			TFaultInfo tfi = new TFaultInfo();
			tfi.setText(excText);
			tfi.setRetcode(-1);
			
			FaultInfoResponse fie = new FaultInfoResponse(
					e0.getMessage(),
					tfi,
					e0);
					
					throw fie;
		} catch (Exception e) {
			// e.printStackTrace();
			
			String excText = "Error calling getStatus -  Reason: "+e.getMessage();
			TRACE.errorT(excText);
			
			TFaultInfo tfi = new TFaultInfo();
			tfi.setText(excText);
			tfi.setRetcode(-1);
			
			FaultInfoResponse fie = new FaultInfoResponse(
					e.getMessage(),
					tfi,
					e);
			throw fie;
					
			
			
		}
		
		if (result != null){
		 if (!result.isEmpty()){
			 	
		
            Set<String> keys =  (result.keySet());
		
			Iterator<String> iterator = keys.iterator();
		
			while (iterator.hasNext()){
				String key = iterator.next();
				NodeStatus stat  = result.get(key);
				
				TNodeStatus o = new TNodeStatus(key, stat);
				tos.getStatusList().add(o);
			}
		 }
		
		}
		return tos;
	}

	public TServiceInfo getServiceInfo(boolean pingMBean) {

		TServiceInfo ti = new TServiceInfo();
		String text = "This is the Persistence Monitoring Trace Service";
		
		
		boolean success = false;
		if (pingMBean){
			TraceManager tm = null;
			try {
				tm = PerMonFactory.createTraceManager();
				success = tm.ping();
				
			} catch (PerMonException e1) {
				success = false;
				
			} catch (Exception e1){
				success = false;
			}
		}
		
		ti.setText(text);
		ti.setVersion("1.0");
		ti.setPingSuccess(success);
		return ti;
	}
	
	public void setOnOff(TNodeSelection nodeSelection, Boolean bOn, Boolean bStack, String mPat, int tr) throws FaultInfoResponse{
		TraceManager tm = null;
		NodeSelection nodeSel= null;
		
		TRACE.debugT("Entering com.sap.persistence.monitors.ws.TraceHelper.setOnOff()");
		
		 try {
			 tm = PerMonFactory.createTraceManager(); 
		
			 
		} catch (PerMonException e) {
			
			
			String excText = "Error calling setOnOff - Error creating the Trace Manager" ;
			TRACE.errorT(excText);
			TFaultInfo tfi = new TFaultInfo();
			tfi.setText(excText);
			tfi.setRetcode(-1);
			
			FaultInfoResponse fie = new FaultInfoResponse(
					e.getMessage(),
					tfi,
					e);
					
					throw fie;
		
		}
		
		
		try {
			 nodeSel = obtainNodeSelection(nodeSelection) ;
			 
			 TRACE.debugT("com.sap.persistence.monitors.ws.TraceHelper.setOnOff() - Init finished");
			 tm.setOnOff(nodeSel,bOn,bStack,mPat,tr);
			 TRACE.debugT("com.sap.persistence.monitors.ws.TraceHelper.setOnOff() - Exec ok");
		}  catch (PerMonException e) {
			
		
			
			String excText = "Error calling setOnOff - Error creating the Node Selection" ;
			TRACE.errorT(excText);
			
			TFaultInfo tfi = new TFaultInfo();
			tfi.setText(excText);
			tfi.setRetcode(-1);
			
			FaultInfoResponse fie = new FaultInfoResponse(
					e.getMessage(),
					tfi,
					e);
					
					throw fie;
		} catch (Exception e) {
		
		
			String excText = "Error calling setOnOff - Reason: "+e.getMessage() ;
			TRACE.errorT(excText);
			TFaultInfo tfi = new TFaultInfo();
			tfi.setText(excText);
			tfi.setRetcode(-1);
			
			FaultInfoResponse fie = new FaultInfoResponse(
					e.getMessage(),
					tfi,
					e);
			throw fie;
		}
					
			
			
		
	}

	public void switchOn(TNodeSelection nodeSelection) throws FaultInfoResponse{
		TraceManager tm = null;
		NodeSelection nodeSel= null;
		
		TRACE.debugT("Entering com.sap.persistence.monitors.ws.TraceHelper.setOnOff()");
		 try {
			 tm = PerMonFactory.createTraceManager(); 
			
			 
		} catch (PerMonException e) {
		
			
			String excText = "Error calling switchOn - Error creating the Trace Manager" ;
			TRACE.errorT(excText);
			TFaultInfo tfi = new TFaultInfo();
			tfi.setText(excText);
			tfi.setRetcode(-1);
			
			FaultInfoResponse fie = new FaultInfoResponse(
					e.getMessage(),
					tfi,
					e);
					
					throw fie;
		}
		
		
		try {
			 nodeSel = obtainNodeSelection(nodeSelection) ;
			 TRACE.debugT("com.sap.persistence.monitors.ws.TraceHelper.switchOn() - Init finished");
			 tm.switchOn(nodeSel);
			 TRACE.debugT("com.sap.persistence.monitors.ws.TraceHelper.switchOn() - Exec ok");
		}   catch (PerMonException e) {
			
	
			
			String excText = "Error calling switchOn - Error creating the Node Selection" ;
			TRACE.errorT(excText);
			TFaultInfo tfi = new TFaultInfo();
			tfi.setText(excText);
			tfi.setRetcode(-1);
			
			FaultInfoResponse fie = new FaultInfoResponse(
					e.getMessage(),
					tfi,
					e);
					
					throw fie;
		} catch (Exception e) {
		  
			String excText = "Error calling switchOn - Reason: "+e.getMessage() ;
			TRACE.errorT(excText);
			TFaultInfo tfi = new TFaultInfo();
			tfi.setText(excText);
			tfi.setRetcode(-1);
			
			FaultInfoResponse fie = new FaultInfoResponse(
					e.getMessage(),
					tfi,
					e);
			throw fie;
		}
	}

	public void switchOff(TNodeSelection nodeSelection) throws FaultInfoResponse{
		TraceManager tm = null;
		NodeSelection nodeSel= null;
		
		TRACE.debugT("Entering com.sap.persistence.monitors.ws.TraceHelper.switchOff()");
		
		 try {
			 tm = PerMonFactory.createTraceManager(); 
		
			 
		} catch (PerMonException e) {
			
		
			String excText = "Error calling switchOff - Error creating the Trace Manager" ;
			TRACE.errorT(excText);
			TFaultInfo tfi = new TFaultInfo();
			tfi.setText(excText);
			tfi.setRetcode(-1);
			
			FaultInfoResponse fie = new FaultInfoResponse(
					e.getMessage(),
					tfi,
					e);
					
					throw fie;
		}
		
		
		try {
			 nodeSel = obtainNodeSelection(nodeSelection) ;
			 TRACE.debugT("com.sap.persistence.monitors.ws.TraceHelper.switchOff() - Init finished");
			 tm.switchOff(nodeSel);
			 TRACE.debugT("com.sap.persistence.monitors.ws.TraceHelper.switchOff() - Exec ok");
		}   catch (PerMonException e) {
			
		
			String excText = "Error calling switchOff - Error creating the Node Selection" ;
			TRACE.errorT(excText);
			TFaultInfo tfi = new TFaultInfo();
			tfi.setText(excText);
			tfi.setRetcode(-1);
			
			FaultInfoResponse fie = new FaultInfoResponse(
					e.getMessage(),
					tfi,
					e);
					
					throw fie;
		} catch (Exception e) {
			
		
			String excText = "Error calling switchOff -  Reason: "+e.getMessage();
			TRACE.errorT(excText);
			TFaultInfo tfi = new TFaultInfo();
			tfi.setText(excText);
			tfi.setRetcode(-1);
			
			FaultInfoResponse fie = new FaultInfoResponse(
					e.getMessage(),
					tfi,
					e);
			throw fie;
		}
	}

	public void switchOnHighLevel(TNodeSelection nodeSelection)throws FaultInfoResponse{
		TraceManager tm = null;
		NodeSelection nodeSel= null;
		
		TRACE.debugT("Entering com.sap.persistence.monitors.ws.TraceHelper.switchOnHighLevel()");
		
		 try {
			 tm = PerMonFactory.createTraceManager(); 
			
			 
		} catch (PerMonException e) {
			String excText = "Error calling switchOnHighLevel - Error creating the Trace Manager" ;
			TRACE.errorT(excText);
			
			TFaultInfo tfi = new TFaultInfo();
			tfi.setText(excText);
			tfi.setRetcode(-1);
			
			FaultInfoResponse fie = new FaultInfoResponse(
					e.getMessage(),
					tfi,
					e);
					
					throw fie;
		}
		
		
		
		try {
			 nodeSel = obtainNodeSelection(nodeSelection) ;
			 
			 TRACE.debugT("com.sap.persistence.monitors.ws.TraceHelper.switchOnHighLevel() - Init finished");
			 tm.switchOnHighLevel(nodeSel);
			 TRACE.debugT("com.sap.persistence.monitors.ws.TraceHelper.switchOnHighLevel() - Exec ok");
		}   catch (PerMonException e) {
			
		
			String excText = "Error calling switchOnHighLevel - Error creating the Node Selection" ;
			TRACE.errorT(excText);
			TFaultInfo tfi = new TFaultInfo();
			tfi.setText(excText);
			tfi.setRetcode(-1);
			
			FaultInfoResponse fie = new FaultInfoResponse(
					e.getMessage(),
					tfi,
					e);
					
					throw fie;
		} catch (Exception e) {
			
			// e.printStackTrace();
			String excText = "Error calling switchOnHighLevel - Reason: "+e.getMessage() ;
			TRACE.errorT(excText);
			TFaultInfo tfi = new TFaultInfo();
			tfi.setText(excText);
			tfi.setRetcode(-1);
			
			FaultInfoResponse fie = new FaultInfoResponse(
					e.getMessage(),
					tfi,
					e);
			throw fie;
		}
	}
	
	
	
	private NodeSelection obtainNodeSelection(TNodeSelection wsNodeSelection) throws PerMonException{
		
		     boolean useFilter = false;
		     
		     if ( !wsNodeSelection.isIgnoreNodeFilter()){
		    	 useFilter = true;
		     } 
		
			 NodeSelection nodeSel = PerMonFactory.createNodeSelection();
			 if ( useFilter ){
				 nodeSel.setAllNodes(false);
				 nodeSel.setNodes(wsNodeSelection.getNodeList());
			 } else {
				 nodeSel.setAllNodes(true);
			 }
	         
			 return nodeSel;
	}

	public TStatusProperty getStatusPropertyList (
			TDetailedStatusSelection detailedStatusSelection) throws FaultInfoResponse {
		TStatusProperty tpl = new TStatusProperty();
		
		TNodeSelection nodeSelection = detailedStatusSelection.getNodeSelection();
		String property = detailedStatusSelection.getProperty();
		
		SortedMap<String,NodeStatus> result = null;
		TraceManager tm = null;
		
		TRACE.debugT("Entering com.sap.persistence.monitors.ws.TraceHelper.getStatusPropertyList()");
		
		
		
		try {
			tm = PerMonFactory.createTraceManager();
		} catch (PerMonException e1) {
		
			
			String excText = "Error calling getStatusPropertyList - Error creating the Trace Manager" ;
			TRACE.errorT(excText);
			TFaultInfo tfi = new TFaultInfo();
			tfi.setText(excText);
			tfi.setRetcode(-1);
			
			FaultInfoResponse fie = new FaultInfoResponse(
					e1.getMessage(),
					tfi,
					e1);
					
					throw fie;
		}
		
		
		try {
			 NodeSelection nodeSel = obtainNodeSelection(nodeSelection) ;
			
			 result = tm.getStatus(nodeSel);
			 
		
		}   catch (PerMonException e) {
				
				
			String excText = "Error calling getStatusPropertyList - Error creating the Node Selection" ;
			TRACE.errorT(excText);
			TFaultInfo tfi = new TFaultInfo();
			tfi.setText(excText);
			tfi.setRetcode(-1);
				
				FaultInfoResponse fie = new FaultInfoResponse(
						e.getMessage(),
						tfi,
						e);
						
						throw fie;
			} catch (Exception e) {
				
				String excText = "Error calling getStatusPropertyList - Reason: "+e.getMessage() ;
				TRACE.errorT(excText);
				TFaultInfo tfi = new TFaultInfo();
				tfi.setText(excText);
				tfi.setRetcode(-1);
				
				FaultInfoResponse fie = new FaultInfoResponse(
						e.getMessage(),
						tfi,
						e);
				throw fie;
			}
		
		
		if (result != null){
		 if (!result.isEmpty()){
			 	
		
            Set<String> keys =  (result.keySet());
		
			Iterator<String> iterator = keys.iterator();
		
			while (iterator.hasNext()){
				String key = iterator.next();
				NodeStatus stat  = result.get(key);
				
				if (stat != null){
								
					TNodeStatusProperty tsp = new TNodeStatusProperty(key,getStatusProperty(stat,property));
				
					tpl.getStatusPropertyList().add(tsp);
				}
				
			}
		 }
		
		}
		return tpl;
		
	}

	private String getStatusProperty(NodeStatus stat, String prop) {
		String ret = "";
	    
		
		if (prop.equalsIgnoreCase("stacktrace")){
			if (stat.isStackTrace()){
				ret = "true";
			} else {
				ret = "false";
			}
		} else 
		if (prop.equalsIgnoreCase("method")){	
			ret = stat.getMethodPattern();
		} else 
		if (prop.equalsIgnoreCase("threshold")){
			ret = Long.toString(stat.getThreshold());
		}  else 
		if (prop.equalsIgnoreCase("on")){
			if (stat.isOn()){
				ret = "true";
			} else {
				ret = "false";
			}
		} else 
		if (prop.equalsIgnoreCase("traceId")){
				ret = stat.getCurrentPrefix();
				
		} 
		    
		
		return ret;
	}

}
