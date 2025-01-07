package com.sap.engine.services.iiop.csiv2.CSIIOP;

import org.omg.CORBA.portable.IDLEntity;
import com.sap.engine.interfaces.csiv2.*;

public final class CompoundSecMech implements IDLEntity {

  public CompoundSecMech() {
    target_requires = 0;
    transport_mech = null;
    as_context_mech = null;
    sas_context_mech = null;
  }

  public CompoundSecMech(short word0, SimpleProfileInterface taggedcomponent, AS_ContextSec as_contextsec, SAS_ContextSec sas_contextsec) {
    target_requires = 0;
    transport_mech = null;
    as_context_mech = null;
    sas_context_mech = null;
    target_requires = word0;
    transport_mech = taggedcomponent;
    as_context_mech = as_contextsec;
    sas_context_mech = sas_contextsec;
  }

  public short target_requires;
  public SimpleProfileInterface transport_mech;   //$JL-SER$
  public AS_ContextSec as_context_mech;
  public SAS_ContextSec sas_context_mech;

  public String toString() {
    String s = "<<CompoundSecMech>>\r\n";
    s += "target_requires " + target_requires + "\r\n";
    s += transport_mech.toString() + "\r\n";
    s += as_context_mech.toString() + "\r\n";
    s += sas_context_mech.toString() + "\r\n";
    s += "<<\\CompoundSecMech>>\r\n";
    return s;
  }

}

