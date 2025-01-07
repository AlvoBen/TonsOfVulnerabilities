package com.sap.engine.services.rmi_p4.finalizer;

import com.sap.engine.interfaces.cross.Connection;
import com.sap.engine.interfaces.cross.CrossCall;
import com.sap.engine.interfaces.cross.ConnectionProperties;

import java.io.IOException;

/**
 * Copyright: Copyright (c) 2003
 * Company: SAP AG - Sap Labs Bulgaria
 *
 * @author Ivan Atanassov
 * @version 7.1
 */
public class ConnectionWrapper implements Connection {

  private boolean alive;
  public static volatile Integer invokations = 0;
  public static volatile Integer deathInvokations = 0;

  public boolean isAlive() {
    return alive;
  }

  public void setAlive(boolean isAlive) {
    alive = isAlive;
  }



  public ConnectionWrapper(boolean alive) {
    this.alive = alive;
  }


  //Vancho - tova e vazno da go implementna
  public void sendRequest(byte[] messageBody, int size, CrossCall call) throws IOException {
    if (alive) {
      synchronized(invokations) {
        invokations ++;
      }
    } else {
      try {
        Thread.sleep(100*60*1000); //sleep like forever
        throw new RuntimeException("This is wrong executed!");
      } catch (InterruptedException e) {
        synchronized(deathInvokations) {
          deathInvokations ++;
        }
      } catch (ThreadDeath tr) {
        synchronized(deathInvokations) {
          deathInvokations ++;
        }
        throw tr;
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }
  }

  public String toString() {
    return " isAlive " + this.isAlive();
  }


  public byte[] getId() {
    return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
  }

  public long getIdAslong() {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public void sendReply(byte[] messageBody, int size, byte[] requestId) throws IOException {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public int getPeerId() {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public void close() {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void callCompleted(CrossCall call) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void setMetaData(Object metaData) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public Object getMetaData() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean isClosed() {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public boolean isLocal() {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public CrossCall[] getCalls() {
    return new CrossCall[0];  //To change body of implemented methods use File | Settings | File Templates.
  }

  public void addRequestMonitor(Object monitor) // only put ExecutionMonitor here
  {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public ConnectionProperties getProperties() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public String getUnderlyingProfile() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public void setUnderlyingProfile(String profile) {
    //To change body of implemented methods use File | Settings | File Templates.
  }


}
