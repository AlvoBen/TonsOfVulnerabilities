package com.sap.engine.session.exec;

import java.security.Principal;

public class DummyPrincipal implements Principal{

  public String getName() {
    return "Pesho";
  }

}
