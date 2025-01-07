package com.sap.engine.services.iiop.PortableServer.policy.impl;

public class ImplicitActivationPolicy extends RootPolicyOperation implements org.omg.PortableServer.ImplicitActivationPolicy{
  
  private org.omg.PortableServer.ImplicitActivationPolicyValue value;

  private ImplicitActivationPolicy() {
  }


  public ImplicitActivationPolicy(org.omg.PortableServer.ImplicitActivationPolicyValue _value) {
    value = _value;
  }
  
  public int policy_type() {
    return org.omg.PortableServer.IMPLICIT_ACTIVATION_POLICY_ID.value;
  }

  public org.omg.PortableServer.ImplicitActivationPolicyValue value() {
    return value;
  }
}