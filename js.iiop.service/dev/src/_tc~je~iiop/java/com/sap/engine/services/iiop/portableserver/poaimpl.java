/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */


package com.sap.engine.services.iiop.PortableServer;

import com.sap.engine.services.iiop.PortableServer.policy.impl.IdAssignmentPolicy;
import com.sap.engine.services.iiop.PortableServer.policy.impl.IdUniquenessPolicy;
import com.sap.engine.services.iiop.PortableServer.policy.impl.ImplicitActivationPolicy;
import com.sap.engine.services.iiop.PortableServer.policy.impl.LifespanPolicy;
import com.sap.engine.services.iiop.PortableServer.policy.PolicyState;
import com.sap.engine.services.iiop.PortableServer.policy.impl.RequestProcessingPolicy;
import com.sap.engine.services.iiop.PortableServer.policy.impl.ServantRetentionPolicy;
import com.sap.engine.services.iiop.PortableServer.state.POAState;
import com.sap.engine.services.iiop.PortableServer.state.StateAction;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.OBJ_ADAPTER;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SystemException;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POAPackage.*;


public class POAImpl extends POATreeNode {
  //$JL-SER$
  //POA attributes
  private POAManager the_POAManager;
  private AdapterActivator the_Activator;
  private Servant default_servant;

  private ServantManager servantManager;


  private POAState state = new POAState();

  private PolicyState policy;


  POAImpl(ORB orb) {
    super(orb);
    policy = new PolicyState(null, this);
    the_POAManager = new POAManagerImpl("RootPOA");
  }

  private POAImpl(String name, POA parent, POAManager poaManager, Policy[] policies) throws AdapterAlreadyExists {
    super(name, (POATreeNode) parent);
    if (poaManager == null) {
      the_POAManager = new POAManagerImpl(name);
    } else {
      the_POAManager = poaManager;  //ako e null da se pravi new
    }
    policy = new PolicyState(policies, this);
  }


  //POA attributes
  public POAManager the_POAManager() {
    return the_POAManager;
  }

  public AdapterActivator the_activator() {
    return the_Activator;
  }

  public void the_activator(AdapterActivator newThe_activator) {
    the_Activator = newThe_activator;
  }

  //POA operations
  public POA create_POA(String adapter_name, POAManager poaManager, Policy[] policies) throws AdapterAlreadyExists, InvalidPolicy {
    return new POAImpl(adapter_name, this, poaManager, policies);
  }

  public POA find_POA(String adapter_name, boolean activate_it) throws AdapterNonExistent {

    POA poa = findChild(adapter_name);
    if (poa != null) {
      return poa;
    } else if (!activate_it || the_Activator == null) {
      throw new AdapterNonExistent(adapter_name);
    } else {
      try {
        if (the_Activator.unknown_adapter(this, adapter_name)) {
          poa = find_POA(adapter_name, false);
        } else {
          throw new AdapterNonExistent(adapter_name);
        }
      } catch (SystemException se) {
        throw new OBJ_ADAPTER(se.toString(), 1, se.completed); //ref page 418
      }
    }
    return poa;
  }

  //Policy operations
  public org.omg.PortableServer.ThreadPolicy create_thread_policy(ThreadPolicyValue value) {
    return new com.sap.engine.services.iiop.PortableServer.policy.impl.ThreadPolicy(value);
  }

  public org.omg.PortableServer.LifespanPolicy create_lifespan_policy(LifespanPolicyValue value) {
    return new LifespanPolicy(value);
  }

  public org.omg.PortableServer.IdUniquenessPolicy create_id_uniqueness_policy(IdUniquenessPolicyValue value) {
    return new IdUniquenessPolicy(value);
  }

  public org.omg.PortableServer.IdAssignmentPolicy create_id_assignment_policy(IdAssignmentPolicyValue value) {
    return new IdAssignmentPolicy(value);
  }

  public org.omg.PortableServer.ImplicitActivationPolicy create_implicit_activation_policy(ImplicitActivationPolicyValue value) {
    return new ImplicitActivationPolicy(value);
  }

  public org.omg.PortableServer.ServantRetentionPolicy create_servant_retention_policy(ServantRetentionPolicyValue value) {
    return new ServantRetentionPolicy(value);
  }

  public org.omg.PortableServer.RequestProcessingPolicy create_request_processing_policy(RequestProcessingPolicyValue value) {
    return new RequestProcessingPolicy(value);
  }

  //Servant operations
  public ServantManager get_servant_manager() throws WrongPolicy {
    if (policy.getRequestProcessingPolicyValue() == RequestProcessingPolicyValue._USE_SERVANT_MANAGER) {
      return servantManager;
    } else {
      throw new WrongPolicy("USE_SERVANT_MANAGER policy is not specified");
    }
  }

  public void set_servant_manager(ServantManager imgr) throws WrongPolicy {
    if (policy.getRequestProcessingPolicyValue() == RequestProcessingPolicyValue._USE_SERVANT_MANAGER) {
      this.servantManager = imgr;
    } else {
      throw new WrongPolicy("USE_SERVANT_MANAGER policy is not specified");
    }
  }

  public Servant get_servant() throws NoServant, WrongPolicy {
    if (policy.getRequestProcessingPolicyValue() == RequestProcessingPolicyValue._USE_DEFAULT_SERVANT) {
      if (default_servant != null) {
        return default_servant;
      } else {
        throw new NoServant("Default Servant is not set");
      }
    } else {
      throw new WrongPolicy("USE_DEFAULT_SERVANT policy is not specified");
    }
  }

  public void set_servant(Servant p_servant) throws WrongPolicy {
    if (policy.getRequestProcessingPolicyValue() == RequestProcessingPolicyValue._USE_DEFAULT_SERVANT) {
      default_servant = p_servant;
    } else {
      throw new WrongPolicy("USE_DEFAULT_SERVANT policy is not specified");
    }
  }

  //Object Activation
  public byte[] activate_object(Servant p_servant) throws ServantAlreadyActive, WrongPolicy {
    if (policy.isSystemAssignedIds() && policy.isServantRetention()) {
      if (policy.isUniqueIds() && policy.containServant(p_servant)) {
        throw new ServantAlreadyActive("Servant is already in Active Object Map");
      }
      byte[] oid = policy.getObjectKey();
      policy.putInAOM(oid, p_servant);
      return oid;
    } else {
      throw new WrongPolicy("RETAIN and SYSTEM_ID policy are not specified");
    }
  }

  public void activate_object_with_id(byte[] id, Servant p_servant) throws ServantAlreadyActive, ObjectAlreadyActive, WrongPolicy {
    if (policy.isServantRetention()) {
      if (policy.containKey(id)) {
        throw new ObjectAlreadyActive("Object key " + id + " is already in Active Object Map");
      } else {
        if (policy.isUniqueIds() && policy.containServant(p_servant)) {
          throw new ServantAlreadyActive("Servant is already in Active Object Map");
        }
        if (policy.isSystemAssignedIds() && !policy.validateID(id)) {
          throw new BAD_PARAM("Invalid Object ID", 14, CompletionStatus.from_int(CompletionStatus._COMPLETED_NO));
        }
        policy.putInAOM(id, p_servant);
      }
    } else {
      throw new WrongPolicy("RETAIN policy is not specified");
    }

  }

  public void deactivate_object(byte[] oid) throws ObjectNotActive, WrongPolicy {
    if (policy.isServantRetention()) {
      if (policy.containKey(oid)) {
        Servant s = policy.locateServant(oid);
        policy.removeFromAOM(oid);
        orb().disconnect(s._get_delegate().this_object(s));
      } else {
        throw new ObjectNotActive("Object not activated");
      }
    } else {
      throw new WrongPolicy("RETAIN policy is not specified");
    }
  }

  //Reference operations
  public Object create_reference(String intf) throws WrongPolicy {
    //generate byte[] oid
    if (!policy.isSystemAssignedIds()) {
      throw new WrongPolicy("SYSTEM_ID policy is not specified");
    } else {
      ServantHolder sh = policy.getServantHolder(null, intf);
      orb().connect(sh);
      return sh;
    }
  }

  public Object create_reference_with_id(byte[] oid, String intf) {
    if (policy.isSystemAssignedIds() && !policy.validateID(oid)) {
      throw new BAD_PARAM("Invalid Object ID", 14, CompletionStatus.from_int(CompletionStatus._COMPLETED_NO));
    }

    if (policy.containKey(oid)) {
      ServantHolder sh = policy.getServantHolder(oid, intf);
      return sh;
    }
    return null;
  }


  public byte[] servant_to_id(Servant p_servant) throws ServantNotActive, WrongPolicy {
    if (policy.useDefaultServant()) {
      return null;
    } else if (policy.isServantRetention()) {

      if (policy.isUniqueIds() && policy.containServant(p_servant)) { //UNIQUE_IDS
        byte[] oid = policy.servantToKey(p_servant);
        if (oid != null) {
          return oid;
        } else {
          throw new ServantNotActive("Servant is not found in Active Object Map");
        }
      } else if (policy.isImplicitlyActivated()) {
        if (!policy.containServant(p_servant) || !policy.isUniqueIds()) {
          byte[] oid = policy.getObjectKey();
          policy.putInAOM(oid, p_servant);
          return oid;
        } else {
          throw new ServantNotActive("Servant is not found in Active Object Map");
        }
      } else {
        throw new WrongPolicy("RETAIN policy is specified but neither UNIQUIE_ID nor IMPLICIT_ACTIVATION is specified");
      }
    } else {
      throw new WrongPolicy("RETAIN or USE_DEFAULT_SERVANT policy is not specified");
    }
  }

  public Object servant_to_reference(Servant p_servant) throws ServantNotActive, WrongPolicy {
    if (policy.isServantRetention()) {
      if (policy.isUniqueIds()) {
        if (policy.containServant(p_servant)) {
          byte[] oid = policy.servantToKey(p_servant);
          if (oid != null) {
            String[] rep_ids = p_servant._all_interfaces(this, oid);
            ServantHolder sh = policy.getServantHolder(oid, rep_ids[0]);
            orb().connect(sh);
            return sh;
          } else {
            throw new ServantNotActive("Servant is not found in Active Object Map");
          }
        } else {
          byte[] oid = policy.getObjectKey();
          policy.putInAOM(oid, p_servant);
          String[] rep_ids = p_servant._all_interfaces(this, oid);
          ServantHolder sh = policy.getServantHolder(oid, rep_ids[0]);
          orb().connect(sh);
          return sh;
        }
      } else if (policy.isImplicitlyActivated()) {
        if (!policy.containServant(p_servant) || policy.isUniqueIds()) {
          byte[] oid = policy.getObjectKey();
          policy.putInAOM(oid, p_servant);
          String[] rep_ids = p_servant._all_interfaces(this, oid);
          ServantHolder sh = policy.getServantHolder(oid, rep_ids[0]);
          orb().connect(sh);
          return sh;
        } else {
          throw new ServantNotActive("Servant is not found in Active Object Map");
        }
      } else {
        throw new WrongPolicy("RETAIN policy is set but neither UNIQUIE_ID nor IMPLICIT_ACTIVATION is set");
      }
    } else {
      throw new WrongPolicy("RETAIN policy is not specified");
    }
  }

  public Servant reference_to_servant(Object reference) throws ObjectNotActive, WrongPolicy, WrongAdapter {
    if (policy.isServantRetention()) {
      try {
        return ((ServantHolder) reference).locateServant();
      } catch (Exception ex) {
        throw new ObjectNotActive("Servant is not found in Active Object Map");
      }
    } else if (policy.useDefaultServant()) {
      return default_servant;
    } else {
      throw new WrongPolicy("RETAIN or USE_DEFAULT_SERVANT policy is not specified");
    }
  }

  public byte[] reference_to_id(Object reference) throws WrongAdapter, WrongPolicy {
    try {
      Servant s = reference_to_servant(reference);
      try {
        return servant_to_id(s);
      } catch (ServantNotActive servantNotActive) {
        if (LoggerConfigurator.getLocation().beDebug()) {
          LoggerConfigurator.getLocation().debugT("POAImpl(Object)", LoggerConfigurator.exceptionTrace(servantNotActive));
        }
        return ((ServantHolder) reference).oid;
      }
    } catch (ObjectNotActive objectNotActive) {
      if (LoggerConfigurator.getLocation().beDebug()) {
        LoggerConfigurator.getLocation().debugT("POAImpl(Object)", LoggerConfigurator.exceptionTrace(objectNotActive));
      }
      return ((ServantHolder) reference).oid;
    }
  }

  public Servant id_to_servant(byte[] oid) throws ObjectNotActive, WrongPolicy {
    if (policy.isServantRetention()) {
      if (policy.containKey(oid)) {
        return policy.locateServant(oid);
      } else {
        throw new ObjectNotActive("Object not activated");
      }
    } else if (policy.useDefaultServant()) {
    } else {
      throw new WrongPolicy("RETAIN or USE_DEFAULT_SERVANT policy is not specified");
    }
    return null;
  }

  public Object id_to_reference(byte[] oid) throws ObjectNotActive, WrongPolicy {
    if (policy.isServantRetention()) {
      if (policy.containKey(oid)) {
        Servant s = policy.locateServant(oid);
        String[] rep_ids = s._all_interfaces(this, oid);
        ServantHolder sh = policy.getServantHolder(oid, rep_ids[0]);
        orb().connect(sh);
        return sh;
      } else {
        throw new ObjectNotActive("Object not activated");
      }
    } else {
      throw new WrongPolicy("RETAIN or USE_DEFAULT_SERVANT policy is not specified");
    }
  }

  public byte[] id() {
    return new byte[0];
  }

  //----------------custom ? --------------------------

  void setManager(POAManager pm) {
    this.the_POAManager = pm;
  }

  public StateAction get_state() {
    return state.getCurrentState(the_POAManager.get_state().value());
  }

  public PolicyState getPolicyObject() {
    return policy;
  }
}
