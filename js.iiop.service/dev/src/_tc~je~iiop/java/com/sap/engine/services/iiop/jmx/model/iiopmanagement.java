package com.sap.engine.services.iiop.jmx.model;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.OpenDataException;
import javax.rmi.CORBA.Tie;

import com.sap.engine.interfaces.cross.jmx.model.compositedata.SAP_ITSAMCrossServiceRemoteObject;
import com.sap.engine.services.iiop.CORBA.CORBAObject;
import com.sap.engine.services.iiop.internal.ORB;
import com.sap.engine.services.iiop.internal.TargetHolder;
import com.sap.engine.services.iiop.internal.store.KeyObject;
import com.sap.engine.services.iiop.server.IIOPGenericTie;

public class IiopManagement implements IiopManagementMBean {
	private ORB orb;

	public IiopManagement(ORB orb) {
	  this.orb = orb;
	}
	
	/*

	@return SAP_ITSAMCrossServiceRemoteObject
	*/
  public CompositeData[] getRemoteObjects(){
    Hashtable<KeyObject, TargetHolder> objects = orb.getExportedObjects();
    Set<KeyObject> keyset = objects.keySet();
    SAP_ITSAMCrossServiceRemoteObject[] itsamObjects = new SAP_ITSAMCrossServiceRemoteObject[keyset.size()];
    Iterator<KeyObject> iterator = keyset.iterator();
    int i = 0;
    while(iterator.hasNext()) {
      KeyObject keyobj = iterator.next();
      Object o = objects.get(keyobj);
      Object remote = null;
      String[] ids = null;
      if (o instanceof CORBAObject) {
        remote = ((CORBAObject) o)._get_delegate();
        ids = ((CORBAObject) o)._ids();
      } else {
        remote = ((TargetHolder) o).getObject();
        ids = ((org.omg.CORBA.portable.ObjectImpl) remote)._ids();
      }

      StringBuffer info = new StringBuffer("Remote object: ");
      info.append((remote instanceof Tie) ? ((Tie) remote).getTarget().toString() : remote.toString());
      info.append("\r\n");
      info.append("Remote IDs:\r\n");
      for (int j = 0; j < ids.length; j++) {
        info.append(" ");
        info.append(ids[j]);
        info.append("\r\n");
      }
      if (remote instanceof IIOPGenericTie) {
        Class[] remoteInterfaces = ((IIOPGenericTie) remote).getRemoteInterfaces();
        info.append("Remote interfaces: ");
        info.append(String.valueOf(remoteInterfaces.length));
        info.append("\r\n");
        for (int j = 0; j < remoteInterfaces.length; j++) {
          info.append(" ");
          info.append(remoteInterfaces[j].toString());
          info.append("\r\n");
        }
      }
      info.append("Key: ");
      info.append(toString(keyobj.getKey()));
      info.append("\r\n");

      //SAP_ITSAMCrossServiceRemoteObject(String Key,String JavaClassName,String Reference,String RemoteObjectDetails,boolean Redirectable,String Caption,String Description,String ElementName)
      SAP_ITSAMCrossServiceRemoteObject itsamobj = new SAP_ITSAMCrossServiceRemoteObject(toString(keyobj.getKey()), (remote instanceof Tie) ? ((Tie) remote).getTarget().getClass().toString() : remote.getClass().toString(), remote.toString(), info.toString(), false, "", "", "");
      itsamObjects[i++]  = itsamobj;
    }

    CompositeData[] data = null;
    try {
      data = SAP_ITSAMIiopManagementServiceWrapper.getCDataArrForSAP_ITSAMCrossServiceRemoteObject(itsamObjects);
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
      sb.append(" ").append(hex.charAt(charAsInt >> 4)).append(hex.charAt(charAsInt & 0x000F));
    }
    return sb.toString();
  }


}
