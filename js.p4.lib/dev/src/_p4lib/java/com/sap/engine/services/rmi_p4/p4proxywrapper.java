package com.sap.engine.services.rmi_p4;

import java.io.Serializable;

public class P4ProxyWrapper implements Serializable {

  static final long serialVersionUID = 3979579356135093453L;

  public P4ClassWrapper cw[] = null;
  private int remoteBrokerId = -1;

  public P4ProxyWrapper(Class[] cl) {
    cw = new P4ClassWrapper[cl.length];
    for (int i = 0; i < cl.length; i++) {
      cw[i] = (cl[i] != null ? new P4ClassWrapper(cl[i]) : null);
    }
  }
  
  public void setRemoteBrokerId(int remoteBrokerId) {
    this.remoteBrokerId = remoteBrokerId;
  }

  public int getRemoteBrokerId() {
    return remoteBrokerId;
  }

  public Class[] getCarriedClasses() throws ClassNotFoundException{
    Class[] ret = new Class[cw.length];
    for(int i = 0; i < cw.length; i++){
      if(cw[i] != null){
        cw[i].setRemoteBrokerId(this.remoteBrokerId);
        ret[i] = cw[i].getCarriedClass();
      } else {
        ret[i] = null;
      }
    }
    return ret;
  }
}
