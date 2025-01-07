package com.sap.engine.services.iiop.PortableServer.policy;

import com.sap.engine.services.iiop.PortableServer.POAImpl;
import com.sap.engine.services.iiop.PortableServer.RETAINServantHolder;
import com.sap.engine.services.iiop.PortableServer.ServantHolder;
import com.sap.engine.services.iiop.PortableServer.policy.impl.*;
import com.sap.engine.services.iiop.internal.ClientORB;
import com.sap.engine.lib.lang.Convert;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.IdUniquenessPolicyValue;
import org.omg.PortableServer.ImplicitActivationPolicyValue;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.RequestProcessingPolicyValue;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantRetentionPolicyValue;
import org.omg.PortableServer.ThreadPolicyValue;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Arrays;

public class PolicyState {

  private int idAssignmentPolicyValue = IdAssignmentPolicyValue._SYSTEM_ID;
  private int idUniquenessPolicyValue = IdUniquenessPolicyValue._UNIQUE_ID;
  private int implicitActivationPolicyValue = ImplicitActivationPolicyValue._IMPLICIT_ACTIVATION;
  private int lifespanPolicyValue = LifespanPolicyValue._TRANSIENT;
  private int requestProcessingPolicyValue = RequestProcessingPolicyValue._USE_ACTIVE_OBJECT_MAP_ONLY;
  private int servantRetentionPolicyValue = ServantRetentionPolicyValue._RETAIN;
  private int threadPolicyValue = ThreadPolicyValue._ORB_CTRL_MODEL;

  private Hashtable aom;

  private static int corr;

  private POAImpl poa;

  public PolicyState(Policy[] policies, POAImpl p) {
    this.poa = p;
    parsePolicy(policies);
    if (isServantRetention()) {
      aom = new Hashtable();
    }
  }

  protected void parsePolicy(Policy[] policies) {
    if (policies != null) {
      for (int i = 0; i < policies.length; i++) {
        if (policies[i] instanceof org.omg.PortableServer.ThreadPolicy) {
          threadPolicyValue = ((ThreadPolicy) policies[i]).value().value();
        } else if (policies[i] instanceof org.omg.PortableServer.ServantRetentionPolicy) {
          servantRetentionPolicyValue = ((ServantRetentionPolicy) policies[i]).value().value();
        } else if (policies[i] instanceof org.omg.PortableServer.RequestProcessingPolicy) {
          requestProcessingPolicyValue = ((RequestProcessingPolicy) policies[i]).value().value();
        } else if (policies[i] instanceof org.omg.PortableServer.LifespanPolicy) {
          lifespanPolicyValue = ((LifespanPolicy) policies[i]).value().value();
        } else if (policies[i] instanceof org.omg.PortableServer.ImplicitActivationPolicy) {
          implicitActivationPolicyValue = ((ImplicitActivationPolicy) policies[i]).value().value();
        } else if (policies[i] instanceof org.omg.PortableServer.IdUniquenessPolicy) {
          idUniquenessPolicyValue = ((IdUniquenessPolicy) policies[i]).value().value();
        } else if (policies[i] instanceof org.omg.PortableServer.IdAssignmentPolicy) {
          idAssignmentPolicyValue = ((IdAssignmentPolicy) policies[i]).value().value();
        }
      }
    }
  }

  //---------------------------------------------

  public int getIdAssignmentPolicyValue() {
    return idAssignmentPolicyValue;
  }

  public int getIdUniquenessPolicyValue() {
    return idUniquenessPolicyValue;
  }

  public int getImplicitActivationPolicyValue() {
    return implicitActivationPolicyValue;
  }

  public int getLifespanPolicyValue() {
    return lifespanPolicyValue;
  }

  public int getRequestProcessingPolicyValue() {
    return requestProcessingPolicyValue;
  }

  public int getServantRetentionPolicyValue() {
    return servantRetentionPolicyValue;
  }

  public int getThreadPolicyValue() {
    return threadPolicyValue;
  }


  public boolean isSystemAssignedIds() {
    return idAssignmentPolicyValue == IdAssignmentPolicyValue._SYSTEM_ID;
  }

  public boolean isUniqueIds() {
    return idUniquenessPolicyValue == IdUniquenessPolicyValue._UNIQUE_ID;
  }

  public boolean isImplicitlyActivated() {
    return implicitActivationPolicyValue == ImplicitActivationPolicyValue._IMPLICIT_ACTIVATION;
  }

  public boolean isServantRetention() {
    return servantRetentionPolicyValue == ServantRetentionPolicyValue._RETAIN;
  }

  public boolean useDefaultServant() {
    return requestProcessingPolicyValue == RequestProcessingPolicyValue._USE_DEFAULT_SERVANT;
  }


  public ServantHolder getServantHolder(byte[] key, String repId) {
    String[] ids = new String[]{repId};
    if (isServantRetention()) {
      if (key != null) {
        // Object o = aom.get(key);
        // if (o == null){
        // aom.put(key,repId);
        // }else{
        //   return (ServantHolder)o;
        // }
        RETAINServantHolder sh = new RETAINServantHolder(poa, key, ids);
        //aom.put(new_key,sh);
        return sh;
      } else {
        byte[] new_key = getObjectKey();
        RETAINServantHolder sh = new RETAINServantHolder(poa, new_key, ids);
        //aom.put(new_key,sh);
        return sh;
      }
    } else {
      //just one invoke on a servant without putting in to aom
      if (key != null) {
        return new RETAINServantHolder(poa, key, ids);
      } else {
        byte[] new_key = getObjectKey();
        return new RETAINServantHolder(poa, new_key, ids);
      }
    }
    //return null;
  }

  public byte[] getObjectKey() {
    int serverId = ((ClientORB) poa.orb()).getClusterId();
    byte[] objkey = new byte[12];
    Convert.writeIntToByteArr(objkey, 0, serverId);
    //get time
    long time = System.currentTimeMillis();

    synchronized (this) {
      objkey[4] = (byte) corr++; //only 256 Keys  per mls !!!!!!!!!!!!!!!!
    }
    for (int i = 0; i < 7; i++) {
      objkey[i + 5] = (byte) (((time >> (i * 8))) & 0xffL);
    }

    return objkey;
  }

  public boolean containServant(Servant a) {
    return aom.containsValue(a);
  }

  public byte[] servantToKey(Servant a) {
    Enumeration enm = aom.keys();
    while (enm.hasMoreElements()) {
      Object t = enm.nextElement();
      Servant s = (Servant) aom.get(t);
      if (s.equals(a)) {
        return (byte[]) t;
      }
    }
    return null;
  }

  public boolean validateID(byte[] iod) {
    return true;
  }

  public boolean containKey(byte[] key) {
    return aom.containsKey(key);
  }

  public void putInAOM(byte[] oid, Servant s) {
    aom.put(oid, s);
  }

  public void removeFromAOM(byte[] key) {
    Enumeration enm = aom.keys();
    while (enm.hasMoreElements()) {
      byte[] temp = (byte[]) enm.nextElement();
      if (Arrays.equals(key, temp)) {
        aom.remove(temp);
        return;
      }
    }
  }

  public Servant locateServant(byte[] key) {
    Enumeration enm = aom.keys();
    while (enm.hasMoreElements()) {
      byte[] temp = (byte[]) enm.nextElement();
      if (Arrays.equals(key, temp)) {
        return (Servant) aom.get(temp);
      }
    }

    return null;
  }
}