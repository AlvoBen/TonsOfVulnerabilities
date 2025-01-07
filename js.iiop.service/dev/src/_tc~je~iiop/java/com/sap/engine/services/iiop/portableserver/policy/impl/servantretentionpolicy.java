package com.sap.engine.services.iiop.PortableServer.policy.impl;

import com.sap.engine.services.iiop.PortableServer.ServantHolder;
import com.sap.engine.services.iiop.PortableServer.policy.impl.RootPolicyOperation;

public class ServantRetentionPolicy extends RootPolicyOperation implements org.omg.PortableServer.ServantRetentionPolicy{
  
  private org.omg.PortableServer.ServantRetentionPolicyValue value;

  private ServantRetentionPolicy() {
  }


  public ServantRetentionPolicy(org.omg.PortableServer.ServantRetentionPolicyValue _value) {
    value = _value;
  }

  public int policy_type() {
    return org.omg.PortableServer.SERVANT_RETENTION_POLICY_ID.value;
  }

  public org.omg.PortableServer.ServantRetentionPolicyValue value() {
    return value;
  }

  public ServantHolder getServantHolder(byte[] oid, String repId) {
    return null;
  }
}