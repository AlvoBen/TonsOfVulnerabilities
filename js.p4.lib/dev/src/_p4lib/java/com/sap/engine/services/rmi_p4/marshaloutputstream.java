package com.sap.engine.services.rmi_p4;

import com.sap.engine.services.rmi_p4.exception.P4Logger;
import com.sap.engine.services.rmi_p4.reflect.LocalInvocationHandler;
import org.omg.CORBA.ORB;

import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import javax.rmi.PortableRemoteObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.rmi.Remote;
import java.lang.reflect.Proxy;

//import com.inqmy.services.cross.communication.Delegate;
//import com.inqmy.services.cross.communication.Communicator;

/**
 * Stream for marshaled parameters of the requested method
 * in remote call.All functions use ObjectOutputStream
 * serialization methods
 *
 * @author Ivo Simeonov, Georgy Stanev
 * @version 7.0
 */
public class MarshalOutputStream extends ObjectOutputStream implements P4ObjectOutput {

  private static Class remoteRefClass = RemoteRef.class;
  private static Class p4RemoteObjectClass = P4RemoteObject.class;
  public P4ObjectBroker broker = P4ObjectBroker.getBroker();
  public ByteArrayOutputStream outStream = null;
  public boolean check = false;

  public MarshalOutputStream() throws IOException {

  }

  public MarshalOutputStream(ByteArrayOutputStream _out) throws IOException {
    super(_out);
    outStream = _out;
    enableReplaceObject(true);
  }


  protected void annotateClass(Class cl) {
  }

  protected void annotateProxyClass(Class cl) throws IOException {
    Class[] cc = cl.getInterfaces();
    writeObject(cc);

  }

  public Object replaceObject(Object _obj) {
    try {
      Class objClass = _obj.getClass();
      if (com.sap.engine.services.rmi_p4.P4ClassWrapper.class.isAssignableFrom(objClass)) {
        ((com.sap.engine.services.rmi_p4.P4ClassWrapper) _obj).prepare();
      }
      if (remoteRefClass.isAssignableFrom(objClass)) {
        RemoteObjectInfo info = ((RemoteRef) _obj).getObjectInfo();
        if (info.getUrls() == null && _obj instanceof StubImpl) {
          StubImpl stub = (StubImpl) _obj;
          if (stub.isLocal) {
            broker.setURLList(stub.p4remote);
          }
        }

        if (info.connectionProfiles == null) {
          info.connectionProfiles = broker.getConnectionProfiles();
        }

        if ( (p4RemoteObjectClass.isAssignableFrom(objClass)) ||          //for remote obj
             (_obj instanceof StubImpl && ((StubImpl) _obj).isLocal) ||   //for generated local stus
             ( (Proxy.isProxyClass(_obj.getClass())) &&                   //for local proxies
               (Proxy.getInvocationHandler(_obj) instanceof LocalInvocationHandler)) ){

          broker.addLink(info.key);
        }

        return info;
      }
      if (com.sap.engine.services.rmi_p4.P4ClassWrapper.class.isAssignableFrom(objClass)) {
        ((com.sap.engine.services.rmi_p4.P4ClassWrapper) _obj).prepare();
      }
      if (java.rmi.Remote.class.isAssignableFrom(objClass)) {
        P4RemoteObject obj = broker.loadObject((Remote) _obj);
        broker.setURLList(obj);
        RemoteObjectInfo info = obj.getObjectInfo();
        info.connectionProfiles = broker.getConnectionProfiles();
        broker.addLink(info.key);
        return info;
      }
      if (objClass.isArray()) {
        if (java.lang.Class.class.equals(objClass.getComponentType())) {
          Class[] cc = (Class[]) _obj;
          return new P4ProxyWrapper(cc);
        }

      }
//      if (com.sap.engine.services.rmi_p4.P4ClassWrapper.class.isAssignableFrom(objClass)) {
//        ((com.sap.engine.services.rmi_p4.P4ClassWrapper) _obj).prepare();
//      }

    } catch (ClassNotFoundException clsnotfound) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("MarshalOutputStream.replaceObject(Object)", P4Logger.exceptionTrace(clsnotfound));
      }
      Object obj = null;
      try {
        Tie t = Util.getTie((Remote) _obj);
        org.omg.CORBA.ORB orb1 = ORB.init(new String[0], null);
        orb1.connect((org.omg.CORBA.Object) t);
        obj = PortableRemoteObject.toStub((Remote) _obj);
        ((Stub) obj).connect(t.orb());
      } catch (Exception nso) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("MarshalOutputStream.replaceObject(Object)", P4Logger.exceptionTrace(clsnotfound));
      }
      }

      if (obj != null) {
        return obj;
      }

      return _obj;
    } catch (Exception ex) {
      Object obj = null;
      try {
        Tie t = Util.getTie((Remote) _obj);
        org.omg.CORBA.ORB orb1 = ORB.init(new String[0], null);
        orb1.connect((org.omg.CORBA.Object) t);
        obj = PortableRemoteObject.toStub((Remote) _obj);
        ((Stub) obj).connect(t.orb());
      } catch (Exception nso) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("MarshalOutputStream.replaceObject(Object)", P4Logger.exceptionTrace(nso));
        }
      }

      if (obj != null) {
        return obj;
      }
      if (P4Logger.getLocation().bePath()) {
        P4Logger.getLocation().pathT("MarshalOutputStream.replaceObject(Object)", P4Logger.exceptionTrace(ex));
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
    return outStream.toByteArray();
  }

  protected void writeData(byte[] target, int offset) {
    ((ByteArrayOutput) outStream).writeData(target, offset);
  }

  protected int getSize() {
    return ((ByteArrayOutput) outStream).getSize();
  }

  protected byte[] getBuffer() {
    return ((ByteArrayOutput) outStream).getBuffer();
  }

}

