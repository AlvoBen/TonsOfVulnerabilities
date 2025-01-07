package com.sap.engine.services.iiop.PortableServer.policy.impl;

public class LifespanPolicy extends RootPolicyOperation implements org.omg.PortableServer.LifespanPolicy{
  
  private org.omg.PortableServer.LifespanPolicyValue value;

  private LifespanPolicy() {
  }

  public LifespanPolicy(org.omg.PortableServer.LifespanPolicyValue _value) {
    value = _value;
  }
  
   
  public int policy_type() {
    return org.omg.PortableServer.LIFESPAN_POLICY_ID.value;
  }

  public org.omg.PortableServer.LifespanPolicyValue value() {
    return value;
  }
}