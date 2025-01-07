package com.sap.engine.services.iiop.csiv2.CSIIOP;

import org.omg.CORBA.portable.IDLEntity;

public final class CompoundSecMechList implements IDLEntity {

  public CompoundSecMechList() {
    stateful = false;
    mechanism_list = null;
  }

  public CompoundSecMechList(boolean flag, CompoundSecMech acompoundsecmech[]) {
    stateful = false;
    mechanism_list = null;
    stateful = flag;
    mechanism_list = acompoundsecmech;
  }

  public boolean stateful;
  public CompoundSecMech mechanism_list[];

}

