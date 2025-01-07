package com.sap.engine.services.iiop.PortableServer.policy.impl;

public class IdUniquenessPolicy extends RootPolicyOperation implements org.omg.PortableServer.IdUniquenessPolicy{
  
  private org.omg.PortableServer.IdUniquenessPolicyValue value;

  private IdUniquenessPolicy() {
  }


  public IdUniquenessPolicy(org.omg.PortableServer.IdUniquenessPolicyValue _value) {
    value = _value;
  }
  
  public int policy_type() {
    return org.omg.PortableServer.ID_UNIQUENESS_POLICY_ID.value;
  }

  public org.omg.PortableServer.IdUniquenessPolicyValue value() {
    return value;
  }
}