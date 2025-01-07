package com.sap.engine.services.rmi_p4;

import com.sap.engine.lib.lang.Convert;
import com.sap.engine.services.rmi_p4.exception.P4BaseIOException;
import com.sap.engine.services.rmi_p4.exception.P4Logger;
import com.sap.engine.interfaces.cross.CrossCall;
import com.sap.engine.interfaces.cross.ConnectionProperties;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.server.Operation;

/**
 * @author Georgy Stanev
 * @version 7.0
 */
public class LocalDispatch implements com.sap.engine.interfaces.cross.Connection {

  P4RemoteObject p4Object;

  //  Call call;
  public LocalDispatch(P4RemoteObject target) {
    p4Object = target;
  }




  public void send(byte[] reply, Call call) {
    call.setReply(reply);
  }

    public byte[] getId() {
        return new byte[8];  //TODO
    }

    public long getIdAslong() {
        return 0;  //TODO
    }

    public void sendRequest(byte[] messageBody, int size, CrossCall call) throws IOException {
      if (messageBody[ProtocolHeader.MESSAGE_TYPE] == Message.INFORM_MESSAGE) {
        P4ObjectBroker.init().inform(this, new Message(P4ObjectBroker.getBroker().getId(), 0, messageBody));
      } else {
          try {
              MarshalOutputStream out = new MarshalOutputStream(new ByteArrayOutputStream());
              Skeleton skel = p4Object.getInfo().skeleton;
              int opNameSize = (messageBody[0] & 0x00ff) | (messageBody[1] << 8); // byte for size
              String op = Convert.byteArrToUString(messageBody, 2, opNameSize);
              MarshalInputStream in = new MarshalInputStream(new ByteArrayInputStream(messageBody, 2 * opNameSize + 2, size - 2 * opNameSize - 2));
              //in.setClassLoader(P4ObjectBroker.init().getClassLoader(skel));
              in.setClassLoader(skel.getClass().getClassLoader());
              Operation[] operations = skel.getOperations();
              int opnum = 0;
              boolean flag = false;

              while (opnum < operations.length) {
                if (op.equals(operations[opnum].getOperation())) {
                  flag = true;
                  break;
                } else {
                  opnum++;
                }
              }

              if (flag) {
                LocalDispatchImpl dispatchContext = new LocalDispatchImpl(out, in, this);
                try {
                  if (P4Logger.getLocation().beDebug()) {
                    P4Logger.getLocation().debugT("LocalDispatch.reply(int, int, byte[], int, Call)", "Calling remote method " + op + " skeleton:" + skel);
                  }
                  skel.dispatch((Remote) p4Object, dispatchContext, opnum);
                } catch (Exception ex) {
                  if (P4Logger.getLocation().beDebug()) {
                    P4Logger.getLocation().debugT("LocalDispatch.reply(int, int, byte[], int, Call)", P4Logger.exceptionTrace(ex));
                  }
                  call.fail();
                  dispatchContext.throwException(ex, (Call)call);
                }
              } else {
                throw new Exception("Incorrect Operation");
              }

              //      System.out.println("Marshaled Replay");
              byte[] marshaledReply = out.toByteArray();
              send(marshaledReply, (Call)call);
            } catch (Exception ex) {
              if (P4Logger.getLocation().beDebug()) {
                P4Logger.getLocation().debugT("LocalDispatch.reply(int, int, byte[], int, Call)", P4Logger.exceptionTrace(ex));
              }
              throw (P4IOException) P4ObjectBroker.init().getException(P4ObjectBroker.P4_IOException, P4BaseIOException.Could_not_make_reply, ex);
            }
      }
    }

    public void sendReply(byte[] messageBody, int size, byte[] requestId) throws IOException {
        sendRequest(messageBody, size, null);
    }

    public int getPeerId() {
        return 0;  //TODO
    }

    public void close() {
        //TODO
    }

    public void callCompleted(CrossCall call) {
        //TODO
    }

    public void setMetaData(Object metaData) {
        //TODO
    }

    public Object getMetaData() {
        return null;  //TODO
    }

    public boolean isClosed() {
        return false;  //TODO
    }

    public boolean isLocal() {
        return false;  //TODO
    }

    public CrossCall[] getCalls() {
        return new CrossCall[0];  //TODO
    }

    public void addRequestMonitor(Object monitor) // only put ExecutionMonitor here
    {
        //TODO
    }

    public ConnectionProperties getProperties() {
        return null;  //TODO
    }

    public String getUnderlyingProfile() {
      return null; //TODO
    }

    public void setUnderlyingProfile(String profile) {
        //TODO
    }

  protected void finalize() throws Throwable {  //$JL-FINALIZE$
      super.finalize();
      P4ObjectBroker.init().disposeConnection("LocalDispatch@" + this.hashCode());
    }

}


class LocalDispatchImpl implements Dispatch {

  MarshalOutputStream out;
  MarshalInputStream in;
  LocalDispatch localDispatch;

  public LocalDispatchImpl(MarshalOutputStream out, MarshalInputStream in, LocalDispatch localDispatch) {
    this.out = out;
    this.in = in;
    this.localDispatch = localDispatch;
  }

  public P4ObjectOutput getOutputStream() {
    return out;
  }

  /**
   * @return input stream created from the request
   */
  public P4ObjectInput getInputStream() {
    return in;
  }

  public DataOptInputStream getDataInputStream(){
    return null;//todo
  }


  public DataOptOutputStream getDataOutputStream(){
    return null;//todo
  }

  /**
   * closes created input stream
   */
  public void releaseInputStream() {
    try {
      in.close();
    } catch (IOException ioex) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("LocalDispatch.releaseInputStream()", P4Logger.exceptionTrace(ioex));
      }
    }
  }

  protected void throwException(Exception ex, Call call) {
    P4ObjectBroker.init();
    if (P4Logger.getLocation().beDebug()) {
      P4Logger.getLocation().debugT("LocalDispatch.throwException(Exception, Call)", "Call: " + call.call_id + " : " + P4Logger.exceptionTrace(ex));
    }
    byte[] marshaledReply = null;

    if (ex instanceof SecurityException) {
      ex = new java.rmi.RemoteException("SecurityException...", ex);
    }

    try {
      try {
        out.writeObject(ex);
      } catch (Throwable e) { //$JL-EXC$
        out.reset();
        out.writeObject(new Exception("Communication Error: There are problems with sending the original exception to the client. Please, check the server logs"));
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("LocalDispatch.throwException(Exception, Call)", P4Logger.exceptionTrace(ex));
        }
      }
      marshaledReply = out.toByteArray();
      out.close();
    } catch (IOException ioex) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("LocalDispatch.throwException(Exception, Call)", P4Logger.exceptionTrace(ioex));
      }
    }
    try {
      localDispatch.send(marshaledReply, call);
    } catch (Exception io) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("LocalDispatch.throwException(Exception, Call)", P4Logger.exceptionTrace(ex));
        }
    }
  }

}

