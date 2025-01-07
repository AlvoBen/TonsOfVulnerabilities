package com.sap.engine.services.iiop.PortableServer.policy.impl;

import org.omg.PortableServer.Servant;

public class RequestProcessingPolicy extends RootPolicyOperation implements org.omg.PortableServer.RequestProcessingPolicy{
  
  private org.omg.PortableServer.RequestProcessingPolicyValue value;

  private RequestProcessingPolicy() {
  }


  public RequestProcessingPolicy(org.omg.PortableServer.RequestProcessingPolicyValue _value) {
    value = _value;
  }
  
  public int policy_type() {
    return org.omg.PortableServer.REQUEST_PROCESSING_POLICY_ID.value;
  }

  public org.omg.PortableServer.RequestProcessingPolicyValue value() {
    return value;
  }

  public Servant locateServant() {
    return null;
  }
}