package com.sap.engine.services.iiop.PortableServer;

import org.omg.CORBA.ContextList;
import org.omg.CORBA.DomainManager;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.SetOverrideType;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAManagerPackage.State;

import java.util.Vector;

public class POAManagerImpl implements POAManager{

  public Vector poas = new Vector();
  private POAManagerImpl poa_manager = null;
  private String manager_name;
  
  
  public POAManagerImpl(POA p){
    poas.add(p);
  }
  
  public POAManagerImpl(String m_name){
    manager_name = m_name;
  }
  
  public POAManager init(POA p){
    if (poa_manager != null){
      poas.add(p);
      return poa_manager;
    }
    poa_manager = new POAManagerImpl(p);
    return poa_manager;
  }
	public void activate()throws AdapterInactive{
   /*
    for(int i = 0;i < poas.size();i++){
     ((POAImpl) poas.elementAt(i)).STATE = 1; //activate;
    }*/
	}

	public void hold_requests(boolean wait_for_completion)throws AdapterInactive{
	}

	public void discard_requests(boolean wait_for_completion)throws AdapterInactive{
	}

	public void deactivate(boolean etherealize_objects,boolean wait_for_completion)throws AdapterInactive{
	}

	public State get_state(){
    return State.from_int(State._ACTIVE);
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
  //------ custom ---------------------------
  
  public void addPOA(POA p){
    poas.add(p);
  }
}
