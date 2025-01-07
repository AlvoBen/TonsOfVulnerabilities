package com.sap.engine.services.iiop.PortableServer.policy.impl;

import org.omg.PortableServer.ThreadPolicyValue;
import com.sap.engine.services.iiop.PortableServer.policy.impl.RootPolicyOperation;

public class ThreadPolicy extends RootPolicyOperation implements org.omg.PortableServer.ThreadPolicy{
  
  private org.omg.PortableServer.ThreadPolicyValue value;
  
  private ThreadPolicy() {
  }
  
  public ThreadPolicy(org.omg.PortableServer.ThreadPolicyValue _value) {
    value = _value;
  }
  
  public ThreadPolicyValue value(){
    return value;
  }
  
  public int policy_type() {
    return org.omg.PortableServer.THREAD_POLICY_ID.value;
  }
	

}