package com.sap.engine.services.rmi_p4;

import com.sap.engine.lib.lang.Convert;
import com.sap.engine.services.rmi_p4.exception.P4BaseIOException;
import com.sap.engine.services.rmi_p4.exception.P4BaseMarshalException;
import com.sap.engine.interfaces.cross.Connection;
import com.sap.engine.system.ThreadWrapper;

/**
 * @author Georgy Stanev
 * @version 7.0
 */
public class InitialCall extends P4Call {

  private int dispId;
  private int clientId;
  private int destServerId;
  private String name;

  InitialCall(long id) {
    this.call_id = id;
  }

  public int getDispId() {
    return dispId;
  }

  public void setDispId(int dispId) {
    this.dispId = dispId;
  }

  public int getClientId() {
    return clientId;
  }

  public void setClientId(int clientId) {
    this.clientId = clientId;
  }

  public int getDestServerId() {
    return destServerId;
  }

  public void setDestServerId(int destServerId) {
    this.destServerId = destServerId;
  }

  //TODO get rid of this "con"
  public synchronized byte[] getResult(ClientConnection con) throws InterruptedException, P4IOException, Exception {
    try {
      try{
        ThreadWrapper.pushSubtask("RMI call to " + P4ObjectBroker.getRemoteHost(this) + name , ThreadWrapper.TS_WAITING_ON_IO);
        if ((replyMessage == null) && (con == null || con.isAlive()) && !exception) {
          try {
            this.wait(P4Call.TIMEOUT);
          } catch (InterruptedException iex) {
            throw (P4IOException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_IOException, P4BaseIOException.Interrupted_While_Wait_Message, iex);
          }
        }
      } finally {
        ThreadWrapper.popSubtask();
      }

      if (exception) {
        if (this.ex != null) {
          throw this.ex;
        } else {
          if (replyMessage != null) {
            try {
              MarshalInputStream in = new MarshalInputStream(replyMessage.getByteArrayInputStream());
              in.setClassLoader(Thread.currentThread().getContextClassLoader());
              ex = (Exception) in.readObject();
            } catch (Exception exe) {
              throw (MarshalException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_MarshalException, P4BaseMarshalException.Exception_in_Execution_Process, exe);
            }
            throw ex;
          } else {
             throw (MarshalException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_MarshalException, P4BaseMarshalException.Exception_in_Execution_Process, new NullPointerException());
          }
        }
      }
      if (replyMessage == null) {
        throw (MarshalException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_MarshalException, P4BaseMarshalException.Exception_in_Execution_Process, new NullPointerException());
      }
      return replyMessage.getUnmarshaledRequest();
    } finally {
      removeCall(call_id);
    }
  }

  public void writeId(byte[] where, int off) {
    Convert.writeLongToByteArr(where, ProtocolHeader.HEADER_SIZE, call_id);
  }


  public static synchronized InitialCall getInitialCall(Connection repliable, String name) {
    long newId = getNewId();
    InitialCall call = new InitialCall(newId);
    call.repliable = repliable;
    call.name = name;
    calls.put(newId, call);
    return call;
  }

  public boolean isFrom(Connection repliable) {
    if (repliable.equals(this.repliable)) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isToConnection(int connectionId) {
    if (clientId == connectionId) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isToClusterElement(int clusterElement) {
    if (clientId == -1 && destServerId == clusterElement) {
      return true;
    } else {
      return false;
    }
  }

  public String toString() {
   if (repliable != null) {
     if (repliable instanceof ClientConnection) {
        return "Id: " + call_id + " Resolve inital reference request to " + ((ClientConnection)repliable).connectionId + " object: " + name;
     }  else  {
     //if (repliable instanceof Messenger) {
         return "Id: " + call_id + " Resolve initial reference to server node " + destServerId + " object: " + name;
    // } else {
     //  return  "Id: " + call_id + " Resolve initial reference for object: " + name;
     }
   } else {
     return  "Id: " +call_id + " Resolve inital reference for object: " + name;
   }
  }


}



