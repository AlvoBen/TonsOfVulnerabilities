package com.sap.engine.sessionmgmt.jco.applib.impl;

import com.sap.conn.jco.session.JCoSessionContainer;
import com.sap.conn.jco.session.JCoConnectionState;
import com.sap.tc.logging.Location;

import java.util.HashMap;
import java.util.Set;
import java.io.Serializable;


public class JCoSessionContainerImpl implements JCoSessionContainer, Serializable {

  private static Location loc = Location.getLocation(JCoSessionContainerImpl.class);
  private HashMap jcoStates = new HashMap();

  protected JCoSessionContainerImpl() {
  }

  public void putState(String key, JCoConnectionState state) {
    if(loc.bePath()){
      loc.pathT("Put key<" + key + "> value<" + state + ">");
    }
    jcoStates.put(key, state);
  }

  public String[] stateKeys() {
    Set keySet = jcoStates.keySet();
    String[] keys = new String[keySet.size()];
    keySet.toArray(keys);

    return keys;
  }

  public void clearStates() {
    jcoStates.clear();
  }

  public void removeState(String key) {
    jcoStates.remove(key);
  }

  public JCoConnectionState getState(String key) {
    JCoConnectionState state = (JCoConnectionState)jcoStates.get(key);

    if(loc.bePath()){
      loc.pathT("Get key<" + key + "> value<" + state + ">");
    }
    return state;
  }

  public String toString(){
    String msg = " JCoSessionContainerImpl";
    if(jcoStates != null && jcoStates.size() > 0){
      msg += " contains states : " + jcoStates;
    } else {
      msg += " - THERE IS NOTHING FOR LOAD";
    }
    return msg;
  }

}
