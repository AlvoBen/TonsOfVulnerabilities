package com.sap.engine.services.iiop.PortableServer.policy.impl;

public class IdAssignmentPolicy extends RootPolicyOperation implements org.omg.PortableServer.IdAssignmentPolicy{
  
  private org.omg.PortableServer.IdAssignmentPolicyValue value;

  private IdAssignmentPolicy() {
  }


  public IdAssignmentPolicy(org.omg.PortableServer.IdAssignmentPolicyValue _value) {
    value = _value;
  }
  
  public int policy_type() {
    return org.omg.PortableServer.ID_ASSIGNMENT_POLICY_ID.value;
  }

  public org.omg.PortableServer.IdAssignmentPolicyValue value() {
    return value;
  }
}