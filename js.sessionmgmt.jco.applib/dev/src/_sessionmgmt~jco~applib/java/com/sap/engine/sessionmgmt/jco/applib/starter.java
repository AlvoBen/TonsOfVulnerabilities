package com.sap.engine.sessionmgmt.jco.applib;

import com.sap.engine.sessionmgmt.jco.applib.impl.SessionReferenceProviderImpl;
import com.sap.engine.sessionmgmt.jco.applib.impl.JCoCallbackHandler;
import com.sap.engine.session.usr.UserContext;
import com.sap.tc.logging.Location;

public class Starter {

  private static Location loc = Location.getLocation(Starter.class);

  private static SessionReferenceProviderImpl provider = new SessionReferenceProviderImpl();

  public static void start() {
    if(loc.bePath()){
      loc.entering("start");
    }

    com.sap.conn.jco.ext.Environment.registerSessionReferenceProvider(provider);
    try {
      UserContext.addCallbackHandler(new JCoCallbackHandler());
    } catch (Exception e) {
      loc.throwing(e);
    }

    if(loc.bePath()){
      loc.exiting("start");
    }
  }

  public static void stop() {
    if(loc.bePath()){
      loc.entering("stop");
    }

    com.sap.conn.jco.ext.Environment.unregisterSessionReferenceProvider(provider);
    UserContext.removeCallbackHandler(new JCoCallbackHandler());

    if(loc.bePath()){
      loc.exiting("stop");
    }
  }
}