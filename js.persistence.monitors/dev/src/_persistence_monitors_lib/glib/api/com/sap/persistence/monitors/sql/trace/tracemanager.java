package com.sap.persistence.monitors.sql.trace;

import java.util.SortedMap;
import javax.management.MBeanServerConnection;

import com.sap.persistence.monitors.common.NodeSelection;

public interface TraceManager {
	
	 public void switchOn(NodeSelection nodeSel) throws Exception;
	 
	 public void switchOnHighLevel(NodeSelection nodeSel) throws Exception;
	 
	 public void switchOnUserLevel(NodeSelection nodeSel, String mPat, long tr) throws Exception;
	 
	 public void switchOff(NodeSelection nodeSel) throws Exception;
	 
	 public void setOnOff(NodeSelection nodeSelection,Boolean bOn, Boolean bStack, String mPat, long tr )throws Exception ;
	 
	 public SortedMap<String,NodeStatus> getStatus(NodeSelection nodeSel) throws Exception;
	  
	 public void setMBeanServer(MBeanServerConnection mbs);
	 
	 public String getVersion();
	 
	 public boolean ping();
	 
	 
}
