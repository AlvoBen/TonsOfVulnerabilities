package com.sap.engine.services.rmi_p4.server;

import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.frame.cluster.ClusterException;
import com.sap.engine.frame.cluster.message.MessageAnswer;
import com.sap.engine.frame.cluster.message.MultipleAnswer;
import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.configuration.InconsistentReadException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.interfaces.cross.CrossInterface;
import com.sap.engine.interfaces.cross.CrossObjectFactory;
import com.sap.engine.interfaces.cross.CrossObjectFactoryExt;
import com.sap.engine.interfaces.cross.Connection;
import com.sap.engine.lib.lang.Convert;
import com.sap.engine.services.rmi_p4.DispatchImpl;
import com.sap.engine.services.rmi_p4.Message;
import com.sap.engine.services.rmi_p4.P4ObjectBroker;
import com.sap.engine.services.rmi_p4.all.MessageConstants;
import com.sap.engine.services.rmi_p4.exception.P4BaseRuntimeException;
import com.sap.engine.services.rmi_p4.exception.P4Logger;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.SimpleLogger;

import java.rmi.NoSuchObjectException;

import java.rmi.Remote;

public class ServerDispatchImpl extends DispatchImpl {

  public static final byte[] ascii_digits = new byte[]{
                                48, 49, 50, 51, 52, 53, 54, 55, 56, 57, // '0','1','2','3','4','5','6','7','8','9'
                                97, 98, 99, 100, 101, 102, 103, 104, 105, 106, // 'a','b','c','d','e','f','g','h','i','j',
                                107, 108, 109, 110, 111, 112, 113, 114, 115, 116, // 'k','l','m','n','o','p','q','r','s','t',
                                117, 118, 119, 120, 121, 122, // 'u','v','w','x','y','z',
                                65, 66, 67, 68, 69, 70, 71, 72, 73, 74, // 'A','B','C','D','E','F','G','H','I','J',
                                75, 76, 77, 78, 79, 80, 81, 82, 83, 84, // 'K','L','M','N','O','P','Q','R','S','T',
                                85, 86, 87, 88, 89, 90, 123, 125, // 'U','V','W','X','Y','Z','{','}',
  };

  public static String digits = null;
  public static ConfigurationHandlerFactory factory = null;
  public static ConfigurationHandler handler = null;
  public static Configuration rootConfiguration = null;
  public P4SessionProcessor process = null;
  private final static String p4_Entity = "P4_PersistentObject";

  public static final int mask_8 = 0x000000FF;
  public static final long lmask_ = 0x3FL;
  public static final int mask_ = 0x3F;
  public static final int digit_ln = 0x6;
  //private final static String owner = "P4";
  //private final static String name = "P4_PersistentObject_Lock";

  public long timeout = 240000; // 4 mins
  public long one_sleep = 500;

  static Object o = new Object();

  public ServerDispatchImpl(Message msg, P4ObjectBroker broker, P4SessionProcessor processor, Connection rep) {
    super(msg, broker, rep);
    process = processor;
  }

  public synchronized void run() {
     _runInternal();
      if (message.type == Message.REQUEST) {
          if (isCallOK()) {
              process.incRequestCount();
          } else {
              process.incErrorRequestCount();
          }
      }
  }

  public void closeAllConfigurations() {
    try {
      handler.closeAllConfigurations();
    } catch(com.sap.engine.frame.core.configuration.ConfigurationException ce) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("ServerDispatchImpl.closeAllConfigurations()", P4Logger.exceptionTrace(ce));
      }
    }
  }

  public void findServer() throws NoSuchObjectException {   
    int groupId = this.process.organizer.clusterContext.getClusterMonitor().getCurrentParticipant().getGroupId();
    MultipleAnswer answer = null;
    try {
      /* send a msg to other servers in this cluster, try to find redirected factory */
      String baseMsg = null;
    if (message.getRedirFlag() == 2) {
      if (message.getFactoryName() == null) {
        message.setFactoryName(Convert.byteArrToUString(message.request, message.getFactoryPos(), message.getFactorySize()));
      }
      if (message.getObjIdentity() == null) {
        byte[] bb = new byte[message.getIdObjSize()];
        System.arraycopy(message.request, message.getIdObjPos(), bb, 0, message.getIdObjSize());
        CrossObjectFactory factoryInstance = ((CrossInterface) P4ObjectBroker.init().getCrossInterface()).getObjectFactory(message.getFactoryName());
        if(factoryInstance == null){// maybe someone is unregistered this factory
          P4Logger.getLocation().debugT("ServerDispatchImpl.findServer()", "Factory: " + message.getFactoryName() + " does not exist");
          throw new NoSuchObjectException("No such factory instance in cross: " + message.getFactoryName());
        }
        ClassLoader loader = factoryInstance.getClass().getClassLoader();
        message.setObjIdentity(message.raiseObject(bb, loader));
      }
    } else if (message.getRedirFlag() == 1 && message.ident == "") {
      message.ident = Convert.byteArrToUString(message.request, message.getNamePos(), message.getNameSize());
    }
      if(message.getFactoryName() != null){
        baseMsg = message.getFactoryName();
      } else {
        baseMsg = getFactoryPart(message.ident);
      }
      answer = this.process.organizer.messageContext.sendAndWaitForAnswer(groupId, ClusterElement.SERVER, MessageConstants.SEARCH_OTHER_REDIRECTED_SERVERS, baseMsg.getBytes(), 0, baseMsg.length(), 30000);
    } catch(ClusterException e) {
      if (P4Logger.getLocation().bePath()) {
        P4Logger.getLocation().pathT("ServerDispatchImpl.findServer()", "Didn't received an answer from other server nodes when try to find redirected factory. " + P4Logger.exceptionTrace(e));
      }
    }
    int[] clusters = answer.participants();
    if(clusters != null && clusters.length > 0) {
      boolean found = false;
      for(int i = 0; i < clusters.length; i++) {
        try {
          MessageAnswer cAns = answer.getAnswer(clusters[i]);
          if(cAns.getMessage() != null && cAns.getMessage().length > cAns.getOffset() && cAns.getMessage()[cAns.getOffset()] == 1) {
            found = true;
            /* ok, we found one server which is currently redirected searched factory on*/
            try {
              byte[] data = new byte[message.request.length + 8];
              Convert.writeIntToByteArr(data, 0, message.clusterEl_id);
              Convert.writeIntToByteArr(data, 4, message.client_id);
              System.arraycopy(message.request, 0, data, 8, message.request.length);
              this.process.organizer.messageContext.send(clusters[i], ClusterOrganizer.REDIRECTABLE_OBJECT, data, 0, data.length);
              break;
            } catch(ClusterException e) {
              if (P4Logger.getLocation().beDebug()) {
                P4Logger.getLocation().debugT("ServerDispatchImpl.findServer()", "Cannot send a message to: " + clusters[i] + ". " + P4Logger.exceptionTrace(e));
              }
            }
          }
          if(i == clusters.length - 1 && !found) {
            throw new NoSuchObjectException("No registered factory for this object");
          }
        } catch(ClusterException e) {
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("ServerDispatchImpl.findServer()", P4Logger.exceptionTrace(e));
          }
        }
      }
    } else {
      /* no more cluster elements that have registered factory*/
      throw new NoSuchObjectException("No registered factory for this object");
    }
  }

  private void processHere() throws NoSuchObjectException {
    CrossInterface cctx = (CrossInterface) broker.getCrossInterface();
    CrossObjectFactory cof = null;
    if (message.getRedirFlag() == 2) {
      if (message.getFactoryName() == null) {
        message.setFactoryName(Convert.byteArrToUString(message.request, message.getFactoryPos(), message.getFactorySize()));
      }
      if (message.getObjIdentity() == null) {
        byte[] bb = new byte[message.getIdObjSize()];
        System.arraycopy(message.request, message.getIdObjPos(), bb, 0, message.getIdObjSize());
        CrossObjectFactory factoryInstance = ((CrossInterface) P4ObjectBroker.init().getCrossInterface()).getObjectFactory(message.getFactoryName());
        if(factoryInstance == null){// maybe someone is unregistered this factory
          P4Logger.getLocation().debugT("ServerDispatchImpl.processHere() the factory:" + message.getFactoryName() + " does not already exist.");
          throw new NoSuchObjectException("No such factory instance in cross: " + message.getFactoryName());
        }
        ClassLoader loader = factoryInstance.getClass().getClassLoader();
        message.setObjIdentity(message.raiseObject(bb, loader));
      }
    } else if (message.getRedirFlag() == 1 && message.ident.equals("")) {
      message.ident = Convert.byteArrToUString(message.request, message.getNamePos(), message.getNameSize());
    }
    if(message.getFactoryName() != null){
      cof = cctx.getObjectFactory(message.getFactoryName());
    } else {
      cof = cctx.getObjectFactory(super.getFactoryPart(message.ident));
    }
    if(cof != null) {
      Remote rem_object = null;
      if(message.getObjIdentity() != null){
        rem_object = (Remote) ((CrossObjectFactoryExt)cof).getObject(message.getObjIdentity());
      } else {
        rem_object = (Remote) cof.getObject(super.getObjectPart(message.ident));
      }
      if(rem_object != null) {
        try {
          object = (P4ObjectBroker.init()).loadObject(rem_object);
        } catch(Exception e) {
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("ServerDispatchImpl.processHere()", P4Logger.exceptionTrace(e));
          }
          throw (RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, P4BaseRuntimeException.Failed_to_load_Object, e, new Object[]{rem_object});
        }
      } else {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("ServerDispatchImpl.processHere()", " CrossObjectFactory : " + message.getFactoryName() + " returned null object. Exception will be thrown to the client.");
        }
        throw (RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, P4BaseRuntimeException.Failed_to_load_Object, null, new Object[] {message.getFactoryName()});
      }
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("ServerDispatchImpl.processHere()", "Call " + message.getCallId() + " was redirected to this server node.");
        if (object != null) {
            P4Logger.getLocation().debugT("The redirected object is: " + object + " delegate: " + object.delegate + " info: " + object.info);
        }
      }
    } else {
      findServer();
    }
  }

  private void loadOrRedirect(byte[] value) throws NoSuchObjectException  {
    int id = Convert.byteArrToInt(value, 0);
    Convert.writeIntToByteArr(message.request, 6, id);
    System.arraycopy(value, 4, message.objectKey, 0, (value.length - 4));
    if(id != broker.id) {
      try {
        /* id is different with server Id. Redirect to other server */
        byte[] data = new byte[message.request.length + 8];
        Convert.writeIntToByteArr(data, 0, message.clusterEl_id);

        Convert.writeIntToByteArr(data, 4, message.client_id);
        System.arraycopy(message.request, 0, data, 8, message.request.length);
        if (P4Logger.getLocation().bePath()) {
          P4Logger.getLocation().pathT("ServerDispatchImpl.loadOrRedirect(byte[])", "Will try to redirect request to server with id : " + id);
        }
        //TODO Vancho set new redirected id        
        this.process.organizer.messageContext.send(id, ClusterOrganizer.REDIRECTABLE_OBJECT, data, 0, data.length);
        if (P4Logger.getLocation().bePath()) {
          P4Logger.getLocation().pathT("ServerDispatchImpl.loadOrRedirect(byte[])", "Redirected request to server with id : " + id);
        }
      } catch(ClusterException ex) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("ServerDispatchImpl.loadOrRedirect(byte[])", P4Logger.exceptionTrace(ex));
        }
        if (P4Logger.getLocation().bePath()) {
          P4Logger.getLocation().pathT("ServerDispatchImpl.loadOrRedirect(byte[])", "Cannot redirect the object : " + ex.getMessage());
        }
        /*cannot find other server with working application - try to process in this server element*/
        processHere();//findServer();//processHere();
      }
    } else {
      processHere();
    }
  }

  private void prepareAndStore(byte[] search_key, Configuration currentConfiguration) throws NoSuchObjectException, ConfigurationException {
    CrossInterface cctx = (CrossInterface) broker.getCrossInterface();
    CrossObjectFactory cof = null;

    if (message.getRedirFlag() == 2) {
      if (message.getFactoryName() == null) {
        message.setFactoryName(Convert.byteArrToUString(message.request, message.getFactoryPos(), message.getFactorySize()));
      }
      if (message.getObjIdentity() == null) {
        byte[] bb = new byte[message.getIdObjSize()];
        System.arraycopy(message.request, message.getIdObjPos(), bb, 0, message.getIdObjSize());
        CrossObjectFactory factoryInstance = ((CrossInterface) P4ObjectBroker.init().getCrossInterface()).getObjectFactory(message.getFactoryName());
        if(factoryInstance == null){// maybe someone is unregistered this factory
          P4Logger.trace(P4Logger.ERROR, "ServerDispatchImpl.prepareAndStore()", "Factory: {0} does not exist. Application will register CrossObjectFactory for its redirectable objects. Redirectable RMI-P4 message will fail", "ASJ.rmip4.rt1032", new Object[]{message.getFactoryName()});
          throw new NoSuchObjectException("No such factory instance in the cross: " + message.getFactoryName());
        }
        ClassLoader loader = factoryInstance.getClass().getClassLoader();
        message.setObjIdentity(message.raiseObject(bb, loader));
      }
    } else if (message.getRedirFlag() == 1 && message.ident == "") {
      message.ident = Convert.byteArrToUString(message.request, message.getNamePos(), message.getNameSize());
    }
    if(message.getFactoryName() != null){
      cof = cctx.getObjectFactory(message.getFactoryName());
    } else {//try again
      cof = cctx.getObjectFactory(getFactoryPart(message.ident));
    }

    if(cof == null) {
      findServer();// find other server and send the message to this server
      if(handler != null) {
        try {
          handler.commit();
          handler.closeConfiguration(currentConfiguration);
        } catch(ConfigurationException e) {
          /* problems with committing in the Configuration */
          throw e;
        }
      }
    } else { // the object is redirected and found here
      Remote rem_object = null;
      if(message.getObjIdentity() != null){
        rem_object = (Remote) ((CrossObjectFactoryExt)cof).getObject(message.getObjIdentity());
      } else {
        rem_object = (Remote) cof.getObject(getObjectPart(message.ident));
      }
      if(rem_object != null) {
        try {
          object = (P4ObjectBroker.init()).loadObject(rem_object);
          object.getObjectInfo().client_id = message.client_id;
        } catch(Exception e) {
           P4Logger.trace(P4Logger.ERROR, "ServerDispatchImpl.prepareAndStore()", "Failed to load object {0}. Factory {1} constructed redirectable object that cannot be exported", "ASJ.rmip4.rt1033", new Object []{rem_object, cof.toString()}, rem_object.getClass().getClassLoader(), null);
           if (P4Logger.getLocation().beDebug()) {
               P4Logger.getLocation().debugT(P4Logger.exceptionTrace(e));
           }
          throw (RuntimeException) broker.getException(P4ObjectBroker.P4_RuntimeException, P4BaseRuntimeException.Failed_to_load_Object, e, new Object[]{rem_object});
        }
      } else {
          P4Logger.trace(P4Logger.ERROR, "ServerDispatchImpl.prepareAndStore()", "Object redirect failed. The factory {0} failed to create object instance", "ASJ.rmip4.rt1034", new Object []{cof}, cof.getClass().getClassLoader(), null);
          throw new NoSuchObjectException("Object redirect failed. The factory " + cof + " failed to create object instance");
      }
      /* composite key_id which will be store by. [server_id, objectKey] */
      byte[] value = new byte[object.getObjectInfo().key.length + 4];
      com.sap.engine.lib.lang.Convert.writeIntToByteArr(value, 0, broker.id);
      System.arraycopy(object.getObjectInfo().key, 0, value, 4, object.getObjectInfo().key.length);

      try {
        currentConfiguration.addConfigEntry(Convert.byteArrToAString(search_key), value);
      } catch(ConfigurationException e) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("ServerDispatchImpl.prepareAndStore(byte[], Configuration)", P4Logger.exceptionTrace(e));
        }
      } finally {
        if(handler != null) {
          handler.commit();
          handler.closeConfiguration(currentConfiguration);
        }
      }
    }
  }

  public synchronized void writeEntry(byte[] search_key) throws ConfigurationException, NoSuchObjectException{
    boolean b = true;
    long begin_time = System.currentTimeMillis();
    Configuration currentConfiguration = null;
    while(b) {
      synchronized(o) {
        try {
          /* try to read the id(ConfigEntry) from the db */
          byte[] value = (byte[]) rootConfiguration.getConfigEntry(Convert.byteArrToAString(search_key));
          /* load Object in the current server element or will try redirect to other server */
          loadOrRedirect(value);
          break;
        } catch(NameNotFoundException e) {    //nnf
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("ServerDispatchImpl.writeEntry(byte[])", P4Logger.exceptionTrace(e));
          }
          /* cannot getConfigEntry from rootConfigurator */
          try {
            /*try to lock, because the entry is not stored*/

            currentConfiguration = handler.openConfiguration(p4_Entity, ConfigurationHandler.WRITE_ACCESS);
            try {
              byte[] value = (byte[]) rootConfiguration.getConfigEntry(Convert.byteArrToAString(search_key));
              loadOrRedirect(value);
              break;  // it's ok, load or redirect
            } catch(NameNotFoundException e1) {
              if (P4Logger.getLocation().beDebug()) {
                P4Logger.getLocation().debugT("ServerDispatchImpl.writeEntry(byte[])", P4Logger.exceptionTrace(e));
              }
              prepareAndStore(search_key, currentConfiguration);

              break;
            } finally{
              if(currentConfiguration != null){
                try {
                  currentConfiguration.close();
                } catch(ConfigurationException e1) {
                  if (P4Logger.getLocation().beDebug()) {
                    P4Logger.getLocation().debugT("ServerDispatchImpl.writeEntry(byte[])", P4Logger.exceptionTrace(e));
                  }
                }
              }
            }
          } catch(ConfigurationException e1) {//catch(ConfigurationLockedException e1) { // catch ConfigExc becuase in one thread it isn;t locked
            if (P4Logger.getLocation().beDebug()) {
              P4Logger.getLocation().debugT("ServerDispatchImpl.writeEntry(byte[])", P4Logger.exceptionTrace(e));
            }
            try {
              Thread.sleep(one_sleep);
            } catch(InterruptedException ie) {
              if (P4Logger.getLocation().beDebug()) {
                P4Logger.getLocation().debugT("ServerDispatchImpl.writeEntry(byte[])", P4Logger.exceptionTrace(ie));
              }
            }
            if(System.currentTimeMillis() - begin_time > timeout) {
              b = false;
              /*attempt fails and throw ConfigurationException*/
              SimpleLogger.log(Severity.ERROR, Category.SYS_SERVER, P4Logger.getLocation(), "ASJ.rmip4.cf0006", "RMI-P4 failed to operate with its configuration in database. Operation timeout exceeded. Redirection of {0} object to current server process will fail. Possible problem with database: {1}", new Object []{new String (search_key), e1.toString()});
              throw new ConfigurationException(e1);
            }
          }
        } catch(InconsistentReadException ire) {
          if (P4Logger.getLocation().beDebug()) {
            P4Logger.getLocation().debugT("ServerDispatchImpl.writeEntry(byte[])", P4Logger.exceptionTrace(ire));
          }

          try {
            Thread.sleep(one_sleep);
          } catch(InterruptedException ie) {
            if (P4Logger.getLocation().beDebug()) {
              P4Logger.getLocation().debugT("ServerDispatchImpl.writeEntry(byte[])", P4Logger.exceptionTrace(ie));
            }
          }
          try {
            rootConfiguration.close();
          } catch(ConfigurationException e) {
            if (P4Logger.getLocation().beDebug()) {
              P4Logger.getLocation().debugT("ServerDispatchImpl.writeEntry(byte[])", P4Logger.exceptionTrace(e));
            }
          }
          rootConfiguration = openConfiguration();
        }
      }
    }
  }

  public void checkDB() throws NoSuchObjectException, ConfigurationException {
    if ( P4Logger.getLocation().bePath() ){
      P4Logger.getLocation().pathT("ServerDispatchImpl.checkDB()", "Begin to redirect object...");
    }
    initDB();
    byte[] search_key = new byte[message.objectKey.length + 4];
    com.sap.engine.lib.lang.Convert.writeIntToByteArr(search_key, 0, message.own_id);
    System.arraycopy(message.objectKey, 0, search_key, 4, message.objectKey.length); //after first 4 bytes copy objectKey
    writeEntry(search_key);
  }

  private void initDB() throws ConfigurationException {

    if(factory == null) {
      factory = process.getServiceContext().getCoreContext().getConfigurationHandlerFactory();
    }

    if(handler == null) {
      handler = factory.getConfigurationHandler();
    }
    if(rootConfiguration == null) {
      synchronized(o) {
        if(rootConfiguration == null) {
          rootConfiguration = openConfiguration();
        }
      }
    }
  }

  private static Configuration openConfiguration() throws ConfigurationException {
    if(rootConfiguration != null){
      try {
        rootConfiguration.close();
      } catch(ConfigurationException e) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("ServerDispatchImpl.openConfiguration()", P4Logger.exceptionTrace(e));
        }
      }
    }
    return handler.openConfiguration(p4_Entity, ConfigurationHandler.READ_ACCESS);
  }

}