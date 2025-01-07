package com.sap.engine.services.rmi_p4;

import com.sap.engine.services.rmi_p4.exception.P4Logger;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.rmi.Remote;

/**
 * Stream for marshaled parameters of the requested method
 * in remote call.All functions use ObjectOutputStream
 * serialization methods
 *
 * @author Nickolay Neychev
 * @version 7.0
 */
public class ReplicateOutputStream extends ObjectOutputStream {

  private static Class remoteRefClass = RemoteRef.class;
  private static Class p4RemoteObjectClass = P4RemoteObject.class;
  public P4ObjectBroker broker = P4ObjectBroker.init();
  private ByteArrayOutput bas = null;
  public boolean check = false;

  public ReplicateOutputStream(ByteArrayOutput bas0) throws IOException {
    super(bas0);
    enableReplaceObject(true);
    bas = bas0;
  }

  public Object replaceObject(Object _obj) {
    try {
      Class objClass = _obj.getClass();

      if (remoteRefClass.isAssignableFrom(objClass)) {
        RemoteObjectInfo info = ((RemoteRef) _obj).getObjectInfo();

        if (info.connectionProfiles == null) {
          info.connectionProfiles = P4ObjectBroker.init().getConnectionProfiles();
        }

//        if (p4RemoteObjectClass.isAssignableFrom(objClass)) {
//          broker.addLink(info.key);
//        }

        return info;
      }

      if (java.rmi.Remote.class.isAssignableFrom(objClass)) {
        RemoteRef obj = P4ObjectBroker.init().loadObject((Remote) _obj);
        RemoteObjectInfo info = obj.getObjectInfo();
        info.connectionProfiles = P4ObjectBroker.init().getConnectionProfiles();
//        broker.addLink(info.key);
        return info;
      }
    } catch (ClassNotFoundException cln) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("ReplicateOutputStream.replaceObject()", P4Logger.exceptionTrace(cln));
      }
      return _obj;
    } catch (Exception ex) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("ReplicateOutputStream.replaceObject()", P4Logger.exceptionTrace(ex));
      }
    }
    return _obj;
  }

  public void writeRemoteObject(RemoteRef ref, Class _class) throws IOException {
    writeObject(ref);
  }

  public void close() throws IOException {
    super.close();
  }

  public byte[] toByteArray() {
    return bas.toByteArray();
  }

  protected void writeData(byte[] target, int offset) {
    bas.writeData(target, offset);
  }

  protected int getSize() {
    return bas.getSize();
  }

  protected byte[] getBuffer() {
    return this.bas.getBuffer();
  }

}

