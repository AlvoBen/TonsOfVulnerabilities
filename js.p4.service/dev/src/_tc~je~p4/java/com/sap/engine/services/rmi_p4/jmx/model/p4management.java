package com.sap.engine.services.rmi_p4.jmx.model;

import java.lang.ref.WeakReference;
import java.rmi.Remote;
import java.util.HashSet;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.OpenDataException;

import com.sap.engine.interfaces.cross.jmx.model.compositedata.SAP_ITSAMCrossServiceRemoteObject;
import com.sap.engine.services.rmi_p4.ObjectEntry;
import com.sap.engine.services.rmi_p4.P4RemoteObject;
import com.sap.engine.services.rmi_p4.server.P4SessionProcessor;

public class P4Management implements P4ManagementMBean {

	private P4SessionProcessor processor;

	public P4Management(P4SessionProcessor processor) {
	  this.processor = processor;
	}

	/*

	@return SAP_ITSAMRemoteObject
	*/
	public CompositeData[] getRemoteObjects(){
	    ObjectEntry[] objects = processor.broker.objManager.getEntries();
	    //MonitorObject[] result = new MonitorObject[objects.length];
	    HashSet remoteObjects = new HashSet();
	    for (int i = 0; i < objects.length; i++) {
	      if (objects[i].isValid()) {
	        P4RemoteObject p4Obj = ((P4RemoteObject) ((WeakReference) objects[i].reference).get());
	        Remote remote = p4Obj.getDelegate();
	        StringBuffer info = new StringBuffer("Remote object: ");
	        info.append(remote.toString());
	        info.append("\r\n");
	        info.append("Remote interfaces:\r\n");
	        for (int j=0; j < p4Obj.getObjectInfo().stubs.length; j++) {
	          info.append(" ");
	          info.append(p4Obj.getObjectInfo().stubs[j]);
	          info.append("\r\n");
	        }
	        info.append("Active links: ");
	        info.append(String.valueOf(objects[i].links));
	        info.append("\r\n");
	        Object[] o = objects[i].connectionStatistics.keySet().toArray();
	        for (int j=0; j < o.length; j++) {
	          info.append(" ");
	          info.append(o[j].toString());
	          info.append("\r\n");
	        }
	        //String Key,String JavaClassName,String Reference,String RemoteObjectDetails,boolean Redirectable,String Caption,String Description,String ElementName
	        SAP_ITSAMCrossServiceRemoteObject jmxObject = new SAP_ITSAMCrossServiceRemoteObject(toString(p4Obj.getObjectInfo().key), remote.getClass().toString(), remote.toString(), info.toString(), p4Obj.getObjectInfo().isRedirectable, "RMI/P4", "RMI/P4", "RMI/P4" );
	        remoteObjects.add(jmxObject);
	      }
	    }

	    CompositeData[] data = null;
	    try {
	    	data = SAP_ITSAMP4ManagementServiceWrapper.getCDataArrForSAP_ITSAMCrossServiceRemoteObject((SAP_ITSAMCrossServiceRemoteObject[])remoteObjects.toArray(new SAP_ITSAMCrossServiceRemoteObject[remoteObjects.size()]));
	    } catch (OpenDataException ode) {
	       // $JL-EXC$  	
	    }
	    return data;

	}

	
	public static String toString(byte[] bytes) {
	    String hex = "0123456789ABCDEF";
	    if (bytes == null) {
	      return "";
	    }
	    StringBuffer sb = new StringBuffer();

	    for (int c = 0; c < bytes.length; c ++) {
	      int charAsInt = ((int) bytes[c]) & 0x00FF;
	      sb.append(" " + hex.charAt(charAsInt >> 4) + hex.charAt(charAsInt & 0x000F));
	    }
	    return sb.toString();
	}


}
