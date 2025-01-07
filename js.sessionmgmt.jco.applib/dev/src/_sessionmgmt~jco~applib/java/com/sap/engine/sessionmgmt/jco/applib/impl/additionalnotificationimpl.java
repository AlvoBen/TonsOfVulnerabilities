package com.sap.engine.sessionmgmt.jco.applib.impl;

import com.sap.engine.session.usr.AdditionalNotification;
import com.sap.engine.session.usr.UserContext;
import com.sap.tc.logging.Location;


public class AdditionalNotificationImpl implements AdditionalNotification {

  private Location loc = Location.getLocation(AdditionalNotificationImpl.class);

  private JCoSessionReferenceImpl jCoRef = null;

  public AdditionalNotificationImpl(JCoSessionReferenceImpl jCoRef){
    this.jCoRef = jCoRef;
  }

  public void clearNotification(UserContext uc){
    if(loc.bePath()){
      loc.entering("clearNotification");
    }

    if (this.jCoRef.isActive()) {
      this.jCoRef.passivateConnections();
    }
    
    if(loc.bePath()){
      loc.exiting("clearNotification");
    }
  }
}
