package com.sap.engine.sessionmgmt.jco.applib.impl;

import com.sap.conn.jco.ext.JCoSessionReference;
import com.sap.conn.jco.session.JCoSession;
import com.sap.engine.session.usr.JCoSessionBridge;
import com.sap.engine.session.exec.SessionExecContext;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.io.Serializable;


public class JCoSessionReferenceImpl extends JCoSessionBridge implements JCoSessionReference, Serializable {

  private static Location loc = Location.getLocation(JCoSessionReferenceImpl.class);

  /* set to true when JCo call contextStarted */
  private boolean isActive = false;
  private boolean isPersisted = false;

  private String sessionID = null;

  /* container which keeps some entries/states */
  private JCoSessionContainerImpl container = null;

  public JCoSessionReferenceImpl(String sessionID) {
    this.sessionID = sessionID;
    container = new JCoSessionContainerImpl();
  }

  public String getID() {
    return sessionID;
  }

  /**
   * called from JCo when this session have to persist some info about the connection when it is freed
   */
  public void contextStarted() {
    if(loc.bePath()){
      loc.entering("contextStarted");
    }
    isActive = true;

    SessionExecContext.getExecutionContext().concernAdditionalStates(new AdditionalNotificationImpl(this));
    if(loc.beDebug()){
      loc.debugT("Register Additional Notificator in the current UserContext which have to be called when the context is freed");
    }

    if(loc.bePath()){
      loc.exiting("contextStarted");
    }
  }


  public void contextFinished() {
    //called frm JCo so it is redundant to call release 
    if(loc.bePath()){
      loc.entering("contextFinished");
    }
    container.clearStates();
    isActive = false;
    // remove from current UC
    
    if(loc.bePath()){
      loc.exiting("contextFinished");
    }
  }

  public JCoSessionContainerImpl getJCoSessionContainer() {
    return container;
  }

  public boolean isActive() {
    return isActive;
  }

  public String handlerName() {
    return SessionReferenceProviderImpl.JCO_SESSION_REF_KEY;
  }

  public boolean isPersisted(){
    return this.isPersisted;
  }

  protected void passivateConnections() {
    if(loc.bePath()){
      loc.entering("Before passivateConnections States container<" + container + ">\r\n sessionID:" + sessionID);
    }

    try {
      JCoSession.passivateConnections(sessionID, container);
      isPersisted = true;
    } catch (Exception e) {
      if(loc.beWarning()){
        String msg = "There are some problem over RFC connection in passivateConnections.";
        loc.traceThrowableT(Severity.WARNING, msg, new Exception());
      }
    }

    if(loc.bePath()){
      loc.exiting("after passivateConnections States container<" + container + ">\r\n sessionID:" + sessionID+ "| isPersisted:" + isPersisted);
    }
  }


  protected void restoreConnections(){
    if(loc.bePath()){
      loc.entering("Before restoreConnections States container<" + container + ">\r\n sessionID:" + sessionID);
    }

    JCoSession.restoreConnections(sessionID, container);
    contextStarted();
    isPersisted = false;
    if(loc.bePath()){
      loc.exiting("After restoreConnections States container<" + container + ">\r\n sessionID:" + sessionID + "| isPersisted:" + isPersisted);
    }

  }

  protected void releaseConnections() {
    try {
      JCoSession.releaseConnections(sessionID, container);
    } catch (Exception e) {
      if(loc.beWarning()){
        String msg = "There are some problem over RFC connection in releaseConnections.";
        loc.traceThrowableT(Severity.WARNING, msg, new Exception());
      }
    }
  }

  protected void clear(){
    try {
      JCoSession.clear(sessionID);
    } catch (Exception e) {
      if(loc.beWarning()){
        String msg = "There are some problem over RFC connection in clear.";
        loc.traceThrowableT(Severity.WARNING, msg, new Exception());
      }
    }
  }

  public String toString(){
    String msg = "";
    msg += "JCoSessionRefererenceImpl<" + this.hashCode() + "> ";
    if(isActive){
      msg += " [isActive] - ";
    } else {
      msg += " [notActive] - ";
    }

    if(isPersisted){
      msg += " [isPersisted] - ";
    } else {
      msg += " [notPersisted] - ";
    }
    msg += "sessionID<" + this.sessionID + ">";
    msg += container.toString();
    return msg;
  }

}
