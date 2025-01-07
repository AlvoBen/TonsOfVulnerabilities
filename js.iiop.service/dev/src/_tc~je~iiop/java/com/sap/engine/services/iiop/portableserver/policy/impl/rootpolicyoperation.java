package com.sap.engine.services.iiop.PortableServer.policy.impl;

import org.omg.CORBA.ContextList;
import org.omg.CORBA.DomainManager;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyOperations;
import org.omg.CORBA.SetOverrideType;

public class RootPolicyOperation implements PolicyOperations,org.omg.CORBA.Object{
  
  public int current_policy_type;
  
  public int policy_type(){
    return current_policy_type;
  }
  
  public Policy copy(){
    return (Policy)this;
  }
  
  public void destroy(){
    
  }
  
  public boolean _is_a(String repositoryIdentifier){
    return false;
  }
  public boolean _is_equivalent(org.omg.CORBA.Object other){
    return false;
  }
  public boolean _non_existent(){
    return false;
  }
  public int _hash(int maximum){
    return 0;
  }
  public org.omg.CORBA.Object _duplicate(){
    return null;
  }
  public void _release(){
  }
  public org.omg.CORBA.Object _get_interface_def(){
    return null;
  }
  public org.omg.CORBA.Request _request(String operation){
    return null;
  }
  public org.omg.CORBA.Request _create_request(org.omg.CORBA.Context ctx,
                               String operation,
                               NVList arg_list,
                               NamedValue result){
    return null;
  }
  public org.omg.CORBA.Request _create_request(org.omg.CORBA.Context ctx,
                               String operation,
                               NVList arg_list,
                               NamedValue result,
                               ExceptionList exclist,
                               ContextList ctxlist){
    return null;
  }
  public org.omg.CORBA.Policy _get_policy(int policy_type){
    return null;
  }
  public DomainManager[] _get_domain_managers(){
    return null;
  }
  public org.omg.CORBA.Object _set_policy_override(org.omg.CORBA.Policy[] policies,
                                   SetOverrideType set_add){
    return null;
  }
}