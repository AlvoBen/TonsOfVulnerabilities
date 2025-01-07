/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.jndi.persistentimpl.memory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;
import java.util.StringTokenizer;

import com.sap.engine.lib.lang.Convert;
import com.sap.engine.lib.util.ConcurrentHashMapObjectObject;
import com.sap.engine.services.jndi.Constants;
import com.sap.engine.services.jndi.cache.CacheCommunicatorImpl;
import com.sap.engine.services.jndi.cluster.DirObject;
import com.sap.engine.services.jndi.implserver.ServerContextImpl;
import com.sap.engine.services.jndi.persistent.JNDIHandle;
import com.sap.engine.services.jndi.persistent.JNDIHandleEnumeration;
import com.sap.engine.services.jndi.persistent.JNDIPersistentRepository;
import com.sap.engine.services.jndi.persistent.Serializator;
import com.sap.engine.services.jndi.persistent.exceptions720.JNDIException;
import com.sap.engine.services.jndi.persistentimpl.memory.util.IntByteData;
import com.sap.engine.services.jndi.persistentimpl.memory.util.StringByteData;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import javax.naming.NamingException;

/**
 * Memory implementation of JNDI
 *
 * @author Petio Petev, Panayot Dobrikov, Elitsa Pancheva
 * @version 4.00
 */
public class JNDIMemoryImpl implements JNDIPersistentRepository {
  /**
   * serial version UID
   */
  static final long serialVersionUID = 5826569922198173170L;
  /**
   * Handle to the root
   */
  static JNDIHandle root = null;
  /**
   * Makes the communication in cluster
   */
  private CacheCommunicatorImpl communicator = null;
  /**
   * Stores the data in HashMap name <-> Container
   */
  static ConcurrentHashMapObjectObject containersByName = new ConcurrentHashMapObjectObject();
  /**
   * Flags open state
   */
  static boolean opened = false;
  /**
   * this cluster ID
   */
  static int clusterId = 0;
  
  private final static Location location = Location.getLocation(JNDIMemoryImpl.class);

  // get new connection
  // open it, if it is not open
  /**
   * Openes the JNDI for work
   *
   * @throws NamingException Thrown if a problem occurs.
   */
  public void open() throws javax.naming.NamingException {
    if (opened) {
      return;
    }
    this.opened = true;
  }

  /**
   * Closes the JNDI
   *
   * @throws NamingException Thrown if a problem occurs.
   */
  public void close() throws javax.naming.NamingException {
    this.opened = false;
  }

  /**
   * Constructor
   */
  public JNDIMemoryImpl() {
    if (root == null) {
      try {
        root = this.createContainer("root", new byte[0], false);
      } catch (javax.naming.NamingException e) {
        location.traceThrowableT(Severity.PATH, "", e);
      }
    }
  }

  public void setCommunicator(CacheCommunicatorImpl cc) {
    this.communicator = cc;
    clusterId = communicator.clContext.getClusterMonitor().getCurrentParticipant().getClusterId();
  }

  /**
   * Gets a new connection
   *
   * @return Persistent repository requested
   * @throws NamingException Thrown if a problem occurs.
   */
  public JNDIPersistentRepository getNewConnection() throws javax.naming.NamingException {
    JNDIMemoryImpl memory = new JNDIMemoryImpl();
    memory.setCommunicator(communicator);
    return memory;
  }

  /* util methods */
  // return Container by cid
  /**
   * Gets a container
   *
   * @param cName Container ID
   * @return The requested container
   */
  Container getContainer(String cName) {
    return (Container) containersByName.get(cName);
  }

  /**
   * Returns container by handle
   *
   * @param jh Container handle
   * @return The requested container
   */
  Container getContainer(JNDIHandle jh) {
    return getContainer(jh.getContainerName());
  }

  /* creating and managing Container Objects */
  /* *************************************** */
  /**
   * Creates container object
   *
   * @param name Name of the container
   * @param data Data to be set
   * @return Handle to the container
   * @throws NamingException Thrown if a problem occurs.
   */
  public JNDIHandle createContainer(String name, byte data[], boolean toReplicate) throws javax.naming.NamingException {
    if (location.bePath()) {
      location.pathT("Create container with name [" + name + "], to be replicated [" + toReplicate + "]");
    }
    try {
      communicator.monitor.tryToAccess();
      if (toReplicate) {

        Container c = new Container(name);
        c.setData(data);
        synchronized (this) {
          containersByName.put(name, c);
        }
        //sending to replicate create container
        sendObject(name,
            null,
            null,
            null,
            data,
            communicator.REPLICATE_CONTAINER_CREATE,
            (byte) Constants.REPLICATED_OPERATION,
            0,
            null,
            this.clusterId);
        if (location.bePath()) {
          location.pathT("Local create operation for container with name [" + name + "] finished successfully; broadcast message sent for container replication");
        }
      } else { //not replicated container
        Container c = new Container(name);
        c.setData(data);
        synchronized (this) {
          containersByName.put(name, c);
        }
        if (location.bePath()) {
          location.pathT("Local create operation for container with name [" + name + "] finished successfully");
        }
      }
    } finally {
      communicator.monitor.endAccess();
    }
    return new JNDIHandleImpl(name, null);
  }

  /**
   * Creates named container object
   *
   * @param name Name of the container
   * @param data Data to be set
   * @return Handle to the container
   * @throws NamingException Thrown if a problem occurs.
   */
  public JNDIHandle createNamedContainer(String name, byte data[], boolean toReplicate) throws javax.naming.NamingException {
    return createContainer(name, data, toReplicate);
  }

  /**
   * Modifies container
   *
   * @param handle Handle of the container
   * @param data Data to be set
   * @throws NamingException Thrown if a problem occurs.
   */
  public void modifyContainer(JNDIHandle handle, byte data[], boolean toReplicate) throws javax.naming.NamingException {
    if (location.bePath()) {
      location.pathT("Modify container with handle " + handle + ", to be replicated [" + toReplicate + "]");
    }
    try {
      communicator.monitor.tryToAccess();
      if (toReplicate) {

        synchronized (this) {
          Container c = (Container) containersByName.remove(handle.getContainerName());
          c.setData(data);
          containersByName.put(c.getContainerName(), c);
        }
        // sending to replicate modify container
        sendObject(handle.getContainerName(),
            null,
            null,
            null,
            data,
            communicator.REPLICATE_CONTAINER_MODIFY,
            (byte) Constants.REPLICATED_OPERATION,
            0,
            null,
            this.clusterId);
        if (location.bePath()) {
          location.pathT("Local modify operation for container with name [" + handle.getContainerName() + "] finished successfully; broadcast message sent for modification replication");
        }
      } else {
        synchronized (this) {
          Container c = (Container) containersByName.remove(handle.getContainerName());
          c.setData(data);
          containersByName.put(c.getContainerName(), c);
        }
        if (location.bePath()) {
          location.pathT("Local modify operation for container with cid [" + handle.getContainerName() + "] finished successfully");
        }
      }
    } finally {
      communicator.monitor.endAccess();
    }
  }

  /**
   * Deletes the container
   *
   * @param handle Handle of the container
   * @throws NamingException Thrown if a problem occurs.
   */
  public void deleteContainer(JNDIHandle handle, boolean toReplicate) throws javax.naming.NamingException {
    if (location.bePath()) {
      location.pathT("Delete container with handle " + handle + ", to be replicated [" + toReplicate + "]");
    }
    try {
      communicator.monitor.tryToAccess();
      if (toReplicate) { //global operation

        synchronized (this) {
          Container c = (Container) containersByName.remove(handle.getContainerName());
        }
        sendObject(handle.getContainerName(),
            null,
            null,
            null,
            new byte[0],
            communicator.REPLICATE_CONTAINER_DELETE,
            (byte) Constants.REPLICATED_OPERATION,
            0,
            null,
            this.clusterId);
        if (location.bePath()) {
          location.pathT("Local delete operation for container with name [" + handle.getContainerName() + "] finished successfully; broadcast message sent for modification replication");
        }
      } else {
        synchronized (this) {
          Container c = (Container) containersByName.remove(handle.getContainerName());
        }
        if (location.bePath()) {
          location.pathT("Local delete operation for container with name [" + handle.getContainerName() + "] finished successfully");
        }
      }
    } finally {
      communicator.monitor.endAccess();
    }

    
  }

  /**
   * Reads the container
   *
   * @param handle Handle of the container
   * @return Data stored in the container
   * @throws NamingException Thrown if a problem occurs.
   */
  public byte[] readContainer(JNDIHandle handle) throws javax.naming.NamingException {
    Container c = getContainer(handle);
    return c.getData();
  }

  /* creating and managing Container Objects */
  /**
   * Renames container
   *
   * @param container Container's handle
   * @param newname New name to use
   * @throws NamingException Thrown if a problem occurs.
   */
  public void renameContainer(JNDIHandle container, String newname) throws javax.naming.NamingException {
    // not used
  }

  /**
   * Gets the container's name
   *
   * @param container Handle of the container
   * @return Name of the container
   * @throws NamingException Thrown if a problem occurs.
   */
  public String getContainerName(JNDIHandle container) throws javax.naming.NamingException {
    return container.getContainerName();
  }

  /**
   * Gets the root container
   *
   * @return Handle of the root container
   * @throws NamingException Thrown if a problem occurs.
   */
  public JNDIHandle getRootContainer() throws javax.naming.NamingException {
    return root;
  }

  /* objects operations */
  /**
   * Bind object
   *
   * @param container Container to work with
   * @param name Name to use
   * @param data Data to be set
   * @return Handle to the object
   * @throws NamingException Thrown if a problem occurs.
   */
  public JNDIHandle bindObject(JNDIHandle container, String name, byte data[], short typeOfOperation) throws javax.naming.NamingException {
    if (location.bePath()) {
      location.pathT("Bind object with name [" + name + "] in container with handle " + container + ", type of operation [" + typeOfOperation + "]");
    }
    Container c = getContainer(container);
    try {
      if (c != null) {
      try {
        communicator.monitor.tryToAccess();
          if (typeOfOperation == Constants.REPLICATED_OPERATION || typeOfOperation == Constants.REMOTE_REPLICATED_OPERATION) { 
             // replicated operation
            c.append(name, data, typeOfOperation, this.clusterId);

            sendObject(container.getContainerName(),
                name,
                null,
                null,
                data,
                communicator.REPLICATE_OBJECT_BIND,
                (byte) typeOfOperation,
                0,
                null,
                this.clusterId);
            if (location.bePath()) {
              location.pathT("Bind object with name [" + name + "] in container with name [" + getContainerName(container) + "] finished successfully; broadcast message sent to replicate the object");
            }
          } else { // not a replicated operation
            c.append(name, data, typeOfOperation, this.clusterId);
            if (location.bePath()) {
              location.pathT("Bind object with name [" + name + "] in container with name " + getContainerName(container) + " finished successfully");
            }
          }
        } finally {
          communicator.monitor.endAccess();
        }
      } else {
        if (location.bePath()) {
          location.pathT("Container with handle " + container + " is not found => return null!");
        }
        return null;
      }
    } catch (Exception e) {
      location.traceThrowableT(Severity.PATH, "Exception during bind operation of object with name " + name + ".", e);
      NamingException ne = new NamingException("Exception during bind operation of object with name " + name + ".");
      ne.setRootCause(e);
      throw ne;
    }
    return new JNDIHandleImpl(container.getContainerName(), name);
  }
 
  //adds ints and info about a @byte[]@'s length to a destination byte[] message and returns the changed counter
  private int addInfoBytesToMessage(byte[] source, byte sourceLen, byte[] destination, int counter) {
    destination[counter] = sourceLen;
    counter++;
    if (source.length > 0) {
      for (int j = 0; j < source.length; j++, counter++) {
        destination[counter] = source[j];
      }
    }
    return counter;
  }
  
  //adds the @byte[]@ itself after its data has been inserted to the destination byte[] message and returns the changed counter
  private int addDataBytesToMessage(byte[] source, byte[] destination, int counter) {
    if (source != null && source.length > 0) {
      for (int j = 0; j < source.length; j++, counter++) {
        destination[counter] = source[j];
      }
    }
    return counter;
  }
  
  // constructs a message which is sent to all other servers in order to
  // replicate a naming operation
  public void sendObject(String objContainerName, String objectName, String oldObjectName, byte[] containerData, byte[] objData, int type_, byte operationType, int clusterId, ByteArrayOutputStream collectedMsgs, int cl_ID) {
    int type = type_; //2=bind, 3=rebind, 4=createContainer
    byte[] message;
 
    //clusterID in bytes
    IntByteData clIDProcessor = new IntByteData(cl_ID);
    clIDProcessor.process();
    // object name in bytes
    StringByteData objectNameProcessor = new StringByteData(objectName);
    objectNameProcessor.process();
    //object's container name in bytes
    StringByteData objContainerNameProcessor = new StringByteData(objContainerName);
    objContainerNameProcessor.process();
    //object's data length in bytes
    IntByteData objDataProcessor = new IntByteData(objData.length);
    objDataProcessor.process();
    
    int i = 1;
    
    if (containerData == null) {
      
      // additional String in bytes
      StringByteData oldNameProcessor = new StringByteData(oldObjectName);
      oldNameProcessor.process();      

      message = new byte[clIDProcessor.getIntInBytes().length + oldNameProcessor.getStrBytesLenInBytes().length 
                         + oldNameProcessor.getStringBytesLen() + objectNameProcessor.getStrBytesLenInBytes().length 
                         + objectNameProcessor.getStringBytesLen() + objContainerNameProcessor.getStrBytesLenInBytes().length 
                         + objContainerNameProcessor.getStringBytesLen() + objDataProcessor.getIntInBytes().length + objData.length + 9];
      // add control bit
      message[0] = 0;
      // starts assembling the message
      // additional String in case of rename(representing the old name) or create subcontext operation(representing the link)
      // or in case of destroy subcontext - the container to delete
      i = addInfoBytesToMessage(oldNameProcessor.getStrBytesLenInBytes(), oldNameProcessor.getStrBytesLenInBytesLen(), message, i);
      i = addDataBytesToMessage(oldNameProcessor.getStringBytes(), message, i);      
      
    } else {//operation is createSubcontext
      
      //container's data length in bytes
      IntByteData contDataProcessor = new IntByteData(containerData.length);
      contDataProcessor.process();
      
      message = new byte[clIDProcessor.getIntInBytes().length + objContainerNameProcessor.getStrBytesLenInBytes().length 
                                + objContainerNameProcessor.getStringBytesLen() + objectNameProcessor.getStrBytesLenInBytes().length 
                                + objectNameProcessor.getStringBytesLen() + objDataProcessor.getIntInBytes().length + objData.length 
                                + contDataProcessor.getIntInBytes().length + containerData.length + 9];
      
      //add control bit
      message[0] = 1;
      //starts assembling the message
      //adding object's container data bytes
      i = addInfoBytesToMessage(contDataProcessor.getIntInBytes(), contDataProcessor.getIntInBytesLen(), message, i);
      i = addDataBytesToMessage(containerData, message, i);
      
    }
    
    //add cluster id bytes
    i = addInfoBytesToMessage(clIDProcessor.getIntInBytes(), clIDProcessor.getIntInBytesLen(), message, i);
    //add object name bytes
    i = addInfoBytesToMessage(objectNameProcessor.getStrBytesLenInBytes(), objectNameProcessor.getStrBytesLenInBytesLen(), message, i);
    i = addDataBytesToMessage(objectNameProcessor.getStringBytes(), message, i);
    //add object's container name bytes
    i = addInfoBytesToMessage(objContainerNameProcessor.getStrBytesLenInBytes(), objContainerNameProcessor.getStrBytesLenInBytesLen(), message, i);
    i = addDataBytesToMessage(objContainerNameProcessor.getStringBytes(), message, i);
    //adding object's data bytes
    i = addInfoBytesToMessage(objDataProcessor.getIntInBytes(), objDataProcessor.getIntInBytesLen(), message, i);
    i = addDataBytesToMessage(objData, message, i); 
    
    
    //operation type byte
    message[i] = operationType;    

    if (communicator != null) {
      if (clusterId == 0) {
        String fullPath = objContainerName + "/" + objectName;
        communicator.sendToAll(message, type, message.length, fullPath);
      } else { // replicate all request, will collect in byte array output stream messages up to REPLICATION_MESSAGE_SIZE and will send them together
        if ((collectedMsgs.size() + message.length) > Constants.REPLICATION_MESSAGE_SIZE && collectedMsgs.size() > 0) {
          // it's time to send the big message and reset the output stream
          byte[] bigMsg = collectedMsgs.toByteArray();
          communicator.sendToServer(clusterId, bigMsg, communicator.REPLICATION_MESSAGE, bigMsg.length);
          collectedMsgs.reset();
        }
        //add the type of the operation (bind, rebind, unbind, ...)
        int[] sizeT = new int[1];
        sizeT[0] = type;
        byte[] typeInBytes = Convert.intArrToByteArr(sizeT);
        byte sizeTypeLength = (byte) typeInBytes.length;
        collectedMsgs.write(sizeTypeLength);
        try {
          collectedMsgs.write(typeInBytes);
        } catch (IOException e) {
          location.traceThrowableT(Severity.PATH, "", e);
        }
        //add the length of the message
        int[] sizeM = new int[1];
        sizeM[0] = message.length;
        byte[] sizeMsg = Convert.intArrToByteArr(sizeM);
        byte sizeMsgLength = (byte) sizeMsg.length;
        collectedMsgs.write(sizeMsgLength);
        try {
          collectedMsgs.write(sizeMsg);
        } catch (IOException e) {
          location.traceThrowableT(Severity.PATH, "", e);
        }
        //add the message
        try {
          collectedMsgs.write(message);
        } catch (IOException e) {
          location.traceThrowableT(Severity.PATH, "", e);
        }
      }
    }
  }
  
  /**
   * Creates a new context
   *
   * @param objContainerName    The name of the container where the object linked to the new container is stored
   * @param objectName          The name of the object that is linked to the new container
   * @param containerData       The new container's data
   * @param objData             The object's data
   * @param type                The type of the operation(replicated or not)
   * @return The handle to the container created
   * @throws javax.naming.NamingException
   */
  public JNDIHandle createSubcontext(String objContainerName, String objectName, byte[] containerData, byte[] objData, short type) throws javax.naming.NamingException {
    boolean toReplicate = false;
    StringBuilder fullName = new StringBuilder();
    fullName.append(objContainerName);
    fullName.append("/");
    fullName.append(objectName);
    if (type == Constants.REPLICATED_OPERATION || type == Constants.REMOTE_REPLICATED_OPERATION) {
      toReplicate = true;
    }
    if (location.bePath()) {
      location.pathT("Create container with name [" + fullName.toString() + "], to be replicated [" + toReplicate + "]");
    }
    
    try {      
      communicator.monitor.tryToAccess();
      if (toReplicate) {
        //binding the context Obj
        if (location.bePath()) {
          location.pathT("Bind object with name [" + objectName + "] in container with handle [" + objContainerName + "], type of operation [" + type + "]");
        }
        Container c = getContainer(objContainerName);
        if (c != null) {
          c.append(objectName, objData, type, this.clusterId);
          if (location.bePath()) {
            location.pathT("Bind object with name [" + objectName + "] in container with name [" + objContainerName + "] finished successfully");
          }
        } else {
          //throw exception if the object's parent is not found
          if (location.bePath()) {
            location.pathT("Container with handle [" + objContainerName + "] is not found.");
          }
          throw new JNDIException("Parent context [" + objContainerName + "] of the object [" + objectName + "] is not found during createSubcontext operation");
        }
     
        //Container creation
        Container newContainer = new Container(fullName.toString());
        newContainer.setData(containerData);
        synchronized (this) {
          containersByName.put(fullName.toString(), newContainer);
        }
        if (location.bePath()) {
          location.pathT("Local create operation for container with name [" + fullName.toString() + "] finished successfully");
        }
        
        //linking the Obj and the Container
        if (c != null) {
          c.createLink(new JNDIHandleImpl(objContainerName, objectName), fullName.toString());
          if (location.bePath()) {
            location.pathT("Local link operation for container with name [" + fullName.toString() + "] finished successfully");
          }
        } else {
          //throw an exception since the object's parent cannot be found
          if (location.bePath()) {
            location.pathT("Container with name ["+ objContainerName +"] is not found => cannot finish link object to container operation");
          }
          throw new JNDIException("Parent of the object[" + objectName + "] with container name ["+ objContainerName +"] is not found => cannot finish link object to the new container operation");
        }
        
        //sending to replicate create container
        sendObject(objContainerName,
            objectName,
            null,
            containerData,
            objData,
            communicator.REPLICATE_CONTAINER_CREATE,
            (byte) type,
            0,
            null,
            this.clusterId);
        if (location.bePath()) {
          location.pathT("Broadcast message sent for replication of context with name ["+ fullName.toString() +"]");
        }
        
      } else { //not replicated container
        //binding the context Obj
        if (location.bePath()) {
          location.pathT("Bind object with name [" + objectName + "] in container with handle " + objContainerName + ", type of operation [" + type + "]");
        }
        Container c = getContainer(objContainerName);
        if (c != null) {
          c.append(objectName, objData, type, this.clusterId);
          if (location.bePath()) {
            location.pathT("Bind object with name [" + objectName + "] in container with name " + objContainerName + " finished successfully");
          }
        } else {
        //throw exception if the object's parent is not found
          if (location.bePath()) {
            location.pathT("Container with handle [" + objContainerName + "] is not found.");
          }
          throw new JNDIException("Parent context [" + objContainerName + "] of the object [" + objectName + "] is not found during createSubcontext operation");
        }
        
        //Container creation
        Container newContainer = new Container(fullName.toString());
        newContainer.setData(containerData);
        synchronized (this) {
          containersByName.put(fullName.toString(), newContainer);
        }
        if (location.bePath()) {
          location.pathT("Local create operation for container with name [" + fullName.toString() + "] finished successfully");
        }
        
        //linking the Obj and the Container
        if (c != null) {
          c.createLink(new JNDIHandleImpl(objContainerName, objectName), fullName.toString());
          if (location.bePath()) {
            location.pathT("Local link operation for container with name [" + fullName.toString() + "] finished successfully");
          }
        } else {
        //throw an exception since the object's parent cannot be found
          if (location.bePath()) {
            location.pathT("Container with name ["+ objContainerName +"] is not found => cannot finish link object to container operation");
          }
          throw new JNDIException("Parent of the object[" + objectName + "] with container name ["+ objContainerName +"] is not found => cannot finish link object to the new container operation");
        }
      }      
    } finally {
      communicator.monitor.endAccess();
    }
    return new JNDIHandleImpl(fullName.toString(), null);
  }
  
  public void destroySubcontext(String toDeleteContainer, JNDIHandle lastContainerObject, short type) throws javax.naming.NamingException {
    
    boolean toReplicate = false;
    
    if (type == Constants.REPLICATED_OPERATION || type == Constants.REMOTE_REPLICATED_OPERATION) {
      toReplicate = true;
    }
    if (location.bePath()) {
      location.pathT("Destroy context with fullpath [" + toDeleteContainer + "], to be replicated [" + toReplicate + "]");
    }
    if (location.bePath()) {
      location.pathT("Unbind object with handle [" + lastContainerObject + "] and delete container with handle JNDIHandle[CNAME = " + toDeleteContainer + "][ONAME = null], type of operation is [" + type + "]");
    }
    String name = getObjectName(lastContainerObject);
    
    try {
      Container c = getContainer(lastContainerObject.getContainerName());

      if (c != null) {
        try {
          communicator.monitor.tryToAccess();
          if (type == Constants.REPLICATED_OPERATION || type == Constants.REMOTE_REPLICATED_OPERATION) {
            c.deleteObject(lastContainerObject);
            synchronized (this) {
              containersByName.remove(toDeleteContainer);
            }
            sendObject(c.getContainerName(),
                name,
                toDeleteContainer,
                null,
                new byte[0],
                communicator.REPLICATE_UNBIND_OBJECT,
                (byte) type,
                0,
                null,
                this.clusterId);
            if (location.bePath()) {
              location.pathT("Unbind object with name [" + name + "] in container with name " + c.getContainerName() + " finished successfully; Local container [" + toDeleteContainer + "] deleted successfully; broadcast message sent to replicate the operation");
            }
          } else {
            c.deleteObject(lastContainerObject);
            synchronized (this) {
              containersByName.remove(toDeleteContainer);
            }
            if (location.bePath()) {
              location.pathT("Unbind object with name [" + name + "] in container with name " + c.getContainerName() + " finished successfully; Local container [" + toDeleteContainer + "] deleted successfully.");
            }
          }
        } finally {
          communicator.monitor.endAccess();
        }
      } else {
        if (location.bePath()) {
          location.pathT("Container with ID " + lastContainerObject.getContainerName() + " is not found => no need to make unbind");
        }
        if (location.bePath()) {
          location.pathT("If existing delete [" + toDeleteContainer + "] as its linked object's parent container is not found => the object itself cannot be found");
        }
        synchronized (this) {
          containersByName.remove(toDeleteContainer);
        }
      }
    } catch (Exception e) {
      location.traceThrowableT(Severity.PATH, "Exception during destroySubcontext operation of context with name [" + toDeleteContainer + "].", e);
      NamingException ne = new NamingException("Exception during destroySubcontext operation of context with name [" + toDeleteContainer + "].");
      ne.setRootCause(e);
      throw ne;
    }
  }
    
  // binds an object on this server if in cluster is performed replicated bind operation
  public boolean replicateObjectBind(String containerName, String objectName, byte data[], short operationType, int clID) throws javax.naming.NamingException {
    if (location.bePath()) {
      location.pathT("Replicate bind for object with name [" + objectName + "] in container with name " + containerName + ", operation type is [" + operationType + "]");
    }
    try {
      communicator.replicationCounter.inc();


      Container c = getContainer(containerName);
      if (c != null) {
        synchronized (ServerContextImpl.getLockObject(containerName)) {
          JNDIHandle j = c.getJNDIHandle(objectName);

          if (j == null) {
            c.append(objectName, data, operationType, clID);
            ServerContextImpl.increaseNumberOfBindings();
          }
        }
        if (location.bePath()) {
          location.pathT("Replicate bind for object with name [" + objectName + "] in container with ID [" + c.getContainerName() + "] finished successfully.");
        }
      } else {
        try {
          if (location.bePath()) {
            location.pathT("Container with name [" + containerName + "] was not found => buld subcontext structure");
          }
          buildSubContextsStructure(containerName, clID);
        } catch (NamingException je) {
          location.traceThrowableT(Severity.PATH, "", je);
          return false;
        }
        Container container = getContainer(containerName);
        JNDIHandle j = container.getJNDIHandle(objectName);

        if (j == null) {
          container.append(objectName, data, operationType, clID);
          ServerContextImpl.increaseNumberOfBindings();
          if (location.bePath()) {
            location.pathT("Replicate bind for object with name [" + objectName + "] in container with ID [" + container.getContainerName() + "] finished successfully.");
          }
        }
      }
    } catch (Exception e) {
      location.traceThrowableT(Severity.PATH, "Exception during replicate object bind.", e);
      return false;
    }

    return true;
  }

  // rebinds an object on this server if in cluster is performed replicated
  // rebind operation
  public boolean replicateObjectRebind(String containerName, String objectName, byte[] data, short operationType) throws javax.naming.NamingException {
    if (location.bePath()) {
      location.pathT("Replicate rebind for object with name [" + objectName + "] in container with name " + containerName + ", operation type is [" + operationType + "]");
    }
    try {
      Container c = getContainer(containerName);

      if (c != null) {
        synchronized (ServerContextImpl.getLockObject(c.getContainerName())) {

          JNDIHandle j = c.getJNDIHandle(objectName);

          if (j != null) {
            c.modify(j, data);

            if (getObjectType(j) != operationType) {
              c.setTypeOf(j, operationType);
            }
            if (location.bePath()) {
              location.pathT("Replicate rebind for object with name [" + objectName + "] and handle [" + j + "]");
            }
            return true;
          }
        }
      } else {
        if (location.bePath()) {
          location.pathT("Replicate rebind for object with name [" + objectName + "] fails because no container is found for name [" + containerName + "]");
        }
      }
    } catch (Exception e) {
      location.traceThrowableT(Severity.PATH, "Exception during replicate object rebind", e);
    }
    return true;
  }

  // creates a container on this server if in cluster is performed replicated createContainer operation
  public boolean replicateContainer(String name, byte data[]) {
    if (location.bePath()) {
      location.pathT("Replicate container with name [" + name + "]");
    }
    synchronized (ServerContextImpl.getLockObject(name)) {
      Container c = getContainer(name);
      if (c == null) {
        c = new Container(name);
        c.setData(data);

        synchronized (this) {
          containersByName.put(name, c);
        }
        if (location.bePath()) {
          location.pathT("Replicate container with name [" + name + "] finished successfully");
        }
      } else {
        if (location.bePath()) {
          location.pathT("Container with name [" + name + "] already exists");
        }
      }
      ServerContextImpl.increaseNumberOfBindings();
    }

    communicator.replicationCounter.inc();

    return true;
  }
  
//creates a container on this server if in cluster is performed replicated createContainer operation
  public boolean replicateContext(String objectName, String containerName, byte[] objData, byte[] containerData, int clID, short operationType) {
    StringBuilder fullName = new StringBuilder(containerName);
    fullName.append("/");
    fullName.append(objectName);
    
    if (location.bePath()) {
      location.pathT("Replicate createSubcontext for container with name [" + fullName.toString() + "], operation type is [" + operationType + "]");
    }
    
    try {
      communicator.replicationCounter.inc();
      
      Container parentC = getContainer(containerName);
      if (parentC != null) {
        synchronized (ServerContextImpl.getLockObject(containerName)) {
          //check if the container was deleted before entering the synchronized block
          parentC = getContainer(containerName);
          if (parentC != null) {
            //bind the object that will be linked to the new context
            JNDIHandle j = parentC.getJNDIHandle(objectName);
            if (j == null) {
              parentC.append(objectName, objData, operationType, clID);
              ServerContextImpl.increaseNumberOfBindings();
            }
            if (location.bePath()) {
              location.pathT("Replicate bind for object with name [" + objectName + "] in container with name [" + containerName + "] finished successfully.");
            }
            //create the new container that will be linked to the previously bound object
            synchronized (ServerContextImpl.getLockObject(fullName.toString())) {
              Container c = getContainer(fullName.toString());
              if (c == null) {
                c = new Container(fullName.toString());
                c.setData(containerData);

                synchronized (this) {
                  containersByName.put(fullName.toString(), c);
                }
                if (location.bePath()) {
                  location.pathT("Replicate container with name [" + fullName.toString() + "] finished successfully");
                }
              } else {
                if (location.bePath()) {
                  location.pathT("Container with name [" + fullName.toString() + "] already exists");
                }
              }
              ServerContextImpl.increaseNumberOfBindings();
              
              //replicate the linking of the object and the container that were created above
              parentC.createLink(new JNDIHandleImpl(containerName, objectName), fullName.toString());
              if (location.bePath()) {
                location.pathT("Replicate link[" + fullName.toString() + "] with object named [" + objectName + "] located in container with name [" + containerName + "] finished successfully");
              }
            }
            
          } else {
            //the container was deleted before entering the synchronized block
            if (location.bePath()) {
              location.pathT("Parent container of object [" + objectName + "] with name [" + containerName + "] is not found. Cannot replicate linking object to container operation");
            }
          }          
        }        
      } else {
        try {
          //the container is missing - build it
          if (location.bePath()) {
            location.pathT("Container with name [" + containerName + "] was not found => buld subcontext structure");
          }
          buildSubContextsStructure(containerName, clID);
        } catch (NamingException je) {
          location.traceThrowableT(Severity.PATH, "", je);
          return false;
        }
        //try again to bind the object
        Container container = getContainer(containerName);
        JNDIHandle j = container.getJNDIHandle(objectName);

        if (j == null) {
          container.append(objectName, objData, operationType, clID);
          ServerContextImpl.increaseNumberOfBindings();
          if (location.bePath()) {
            location.pathT("Replicate bind for object with name [" + objectName + "] in container with name [" + container.getContainerName() + "] finished successfully.");
          }
        }
      }
    } catch (Exception e) {
      location.traceThrowableT(Severity.PATH, "Exception during replicate createSubcontext of context with name [" + fullName.toString() + "].", e);
      return false;
    }
    return true;
  }

  //deletes a context on this server if in cluster is performed deleteContext operation
  public void replicateContextDelete(String objContName, String objName, String toDeleteContainer) {
    if (location.bePath()) {
      location.pathT("Replicate delete container with fullname [" + toDeleteContainer + "]; Replicate unbind object with name [" + objName + "] in container with name [" + objName + "]");
    }
    try {
      Container c = getContainer(objContName);

      if (c != null) {

        synchronized (ServerContextImpl.getLockObject(objContName)) {
          c = getContainer(objContName);
          if (c != null) {
            //unbinding object
            c.deleteObject(new JNDIHandleImpl(objContName, objName));
            ServerContextImpl.decreaseNumberOfBindings();
            //deleting the container linked to that object
            synchronized (ServerContextImpl.getLockObject(toDeleteContainer)) {
              try {
                containersByName.remove(toDeleteContainer);
              } finally {
                ServerContextImpl.unlockObject(toDeleteContainer);
              }
            }
            if (location.bePath()) {
              location.pathT("Replicate delete container with fullname [" + toDeleteContainer + "] was successful");
            }
            ServerContextImpl.decreaseNumberOfBindings();
          } else {
          //deleting the container linked to that object 
            synchronized (ServerContextImpl.getLockObject(toDeleteContainer)) {
              try {
                containersByName.remove(toDeleteContainer);
              } finally {
                ServerContextImpl.unlockObject(toDeleteContainer);
              }
            }
            if (location.bePath()) {
              location.pathT("Replicate delete container with fullname [" + toDeleteContainer + "] was successful");
            }
            ServerContextImpl.decreaseNumberOfBindings();
          }
        }
      } else {
        
        if (location.bePath()) {
          location.pathT("Contaner with name [" + objContName + "] is not found. Cannot unbind object with name [" + objName + "]; If existing deleting the container [" + toDeleteContainer + "] that was linked to that object.");
          synchronized (ServerContextImpl.getLockObject(toDeleteContainer)) {
            try {
              containersByName.remove(toDeleteContainer);
              ServerContextImpl.decreaseNumberOfBindings();
            } finally {
              ServerContextImpl.unlockObject(toDeleteContainer);
            }
          }
        }
      }
    } catch (Exception e) {
      location.traceThrowableT(Severity.PATH, "Exception during replicate object unbind", e);
    }
  }
  
  // deletes a container on this server if in cluster is performed deleteContainer bind operation
  public boolean replicateContainerDelete(String name) {
    if (location.bePath()) {
      location.pathT("Replicate delete container with name [" + name + "]");
    }
    Container c;

    synchronized (ServerContextImpl.getLockObject(name)) {
      try {
        c = (Container) containersByName.remove(name);
      } finally {
        ServerContextImpl.unlockObject(name);
      }
    }
    if (location.bePath()) {
      location.pathT("Replicate delete container with ID [" + name + "] was successfull");
    }
    ServerContextImpl.decreaseNumberOfBindings();

    if (c != null) {
      return true;
    }

    return false;
  }

  // links object to container on this server if in cluster is performed replicated linkObjectToContainer operation
  public void replicateLinkObjectToContainer(String containerName, String objectName, String link) throws javax.naming.NamingException {
    if (location.bePath()) {
      location.pathT("Replicate link of object with name [" + objectName + "] located in container with name [" + containerName + "] to container with name [" + link + "]");
    }

    Container c = getContainer(containerName);
    if (c != null) {
      synchronized (ServerContextImpl.getLockObject(containerName)) {
        c = getContainer(containerName);
        if (c != null) {
        c.createLink(new JNDIHandleImpl(containerName, objectName), link);
        if (location.bePath()) {
          location.pathT("Replicate link of object with name [" + objectName + "] located in container with name [" + containerName + "] to container with name [" + link + "] finished successfully");
        }
      }
      }
    } else {
      if (location.bePath()) {
        location.pathT("Contaner with name [" + containerName + "] is not found. Cannot replicate link object to container operation");
      }
    }
  }

  // modifies a container on this server if in cluster is performed replicated modifyContainer operation
  public void replicateModifyContainer(String containerName, byte data[]) {
    if (location.bePath()) {
      location.pathT("Replicate modify container with name [" + containerName + "]");
    }
    Container c = getContainer(containerName);

    if (c != null) {
      synchronized (ServerContextImpl.getLockObject(containerName)) {
        c = getContainer(containerName);
        if (c != null) {
        c.setData(data);
        if (location.bePath()) {
          location.pathT("Replicate modify container with name [" + containerName + "] finished successfully");
        }
      }
      }
    } else {
      if (location.bePath()) {
        location.pathT("Contaner with name [" + containerName + "] is not found. Cannot replicate modify container operation");
      }
    }
  }

  // removes a linked container on this server if in cluster is performed replicated removeLinkedContainer operation
  public void replicateRemoveLinkedContainer(String containerName, String objectName) throws javax.naming.NamingException {
    if (location.bePath()) {
      location.pathT("Replicate remove linked container with name [" + containerName + "] from object with Name [" + objectName + "]");
    }
    Container c = getContainer(containerName);

    if (c != null) {
      synchronized (ServerContextImpl.getLockObject(containerName)) {
        c = getContainer(containerName);
        if (c != null) {
        c.removeLink(new JNDIHandleImpl(containerName, objectName));
        if (location.bePath()) {
          location.pathT("Replicate remove linked container with name [" + containerName + "] from object with name [" + objectName + "] finished successfully");
        }
      }
      }
    } else {
      if (location.bePath()) {
        location.pathT("Contaner with name [" + containerName + "] is not found. Cannot replicate remove linked container operation");
      }
    }
  }

  // renames an object on this server if in cluster is performed replicated renameObject operation
  public void replicateRenameObject(String containerName, String newObjectName, String oldObjectName) {
    if (location.bePath()) {
      location.pathT("Replicate rename object with name [" + oldObjectName + "] to name [" + newObjectName + "]; container name is [" + containerName + "]");
    }
    try {
      Container c = getContainer(containerName);

      if (c != null) {
        synchronized (ServerContextImpl.getLockObject(containerName)) {
          c = getContainer(containerName);
          if (c != null) {
          c.rename(new JNDIHandleImpl(containerName, oldObjectName), newObjectName);
          if (location.bePath()) {
            location.pathT("Replicate rename object with name [" + oldObjectName + "] to name [" + newObjectName + "]; container name is [" + containerName + "]");
          }
        }
        }
      } else {
        if (location.bePath()) {
          location.pathT("Contaner with name [" + containerName + "] is not found. Cannot rename object with name [" + oldObjectName + "]");
        }
      }
    } catch (Exception e) {
      location.traceThrowableT(Severity.PATH, "Exception during replicate object rename", e);
    }
  }

  // unbinds an object on this server if in cluster is performed replicated unbind operation
  public void replicateUnbindObject(String containerName, String objectName) {
    if (location.bePath()) {
      location.pathT("Replicate unbind object with name [" + objectName + "] in container with name [" + containerName + "]");
    }
    try {
      Container c = getContainer(containerName);

      if (c != null) {

        synchronized (ServerContextImpl.getLockObject(containerName)) {
          c = getContainer(containerName);
          if (c != null) {
          c.deleteObject(new JNDIHandleImpl(containerName, objectName));
          ServerContextImpl.decreaseNumberOfBindings();
        }
        }
      } else {
        if (location.bePath()) {
          location.pathT("Contaner with name [" + containerName + "] is not found. Cannot unbind object with name [" + objectName + "]");
        }
      }
    } catch (Exception e) {
      location.traceThrowableT(Severity.PATH, "Exception during replicate object unbind", e);
    }
  }


  public int toSendGlobalObjects(int clusterId, JNDIHandle j) throws javax.naming.NamingException {
    ByteArrayOutputStream collectedMsgs = new ByteArrayOutputStream();
    int numberOfSentObjects = replicateAll(clusterId, j, collectedMsgs);
    if (collectedMsgs.size() != 0) {
      // send the big message and to flush the output stream
      byte[] bigMsg = collectedMsgs.toByteArray();
      communicator.sendToServer(clusterId, bigMsg, communicator.REPLICATION_MESSAGE, bigMsg.length);
      collectedMsgs.reset();
    }
    if (location.bePath()) {
      location.pathT(numberOfSentObjects + " global objects are sent to server [" + clusterId + "]");
    }
    return numberOfSentObjects;
  }

  // sends to the server with given clusterId messages with all the containers and objects which are replicated in cluster
  public int replicateAll(int clusterId, JNDIHandle j, ByteArrayOutputStream collectedMsgs) throws javax.naming.NamingException {
      if (location.bePath()) {
        location.pathT("Collect and send all global objects and containers to server [" + clusterId + "]");
      }
      JNDIHandle root = j;
      int currentItterationCounter = 0;

      if (j == null) { //root context
        root = getRootContainer();
      }

      JNDIHandleEnumeration je = listObjects(root, "");

      while (je.hasMoreElements()) {
        JNDIHandle h = je.nextObject();
        short type = getObjectType(h);

        if (type == Constants.REPLICATED_OPERATION || type == Constants.REMOTE_REPLICATED_OPERATION) {
          if(getLinkedContainer(h) == null) {
            if (location.bePath()) {
              location.pathT("Send bind object with name [" + getObjectName(h) + "] in container with name [" + h.getContainerName() + "]");
            }
            sendObject(h.getContainerName(),
              getObjectName(h),
              null,
              null,
              readObject(h),
              communicator.REPLICATE_OBJECT_BIND,
              (byte) type,
              clusterId,
              collectedMsgs,
              this.clusterId);
            currentItterationCounter++;
          } else {
            JNDIHandle lc = getLinkedContainer(h);
            // send create container
            if (location.bePath()) {
              location.pathT("Send create container with name [" + getContainerName(lc) + "]");
            }
            sendObject(h.getContainerName(),
              getObjectName(h), 
              null,
              readContainer(lc), 
              readObject(h),
              communicator.REPLICATE_CONTAINER_CREATE,
              (byte) Constants.REPLICATED_OPERATION, 
              clusterId,
              collectedMsgs, 
              this.clusterId);
            currentItterationCounter++;
            currentItterationCounter += replicateAll(clusterId, getLinkedContainer(h), collectedMsgs);
          }
        } else {
          if (getLinkedContainer(h) != null) {
            currentItterationCounter += replicateAll(clusterId, getLinkedContainer(h), collectedMsgs);
          }
        }
      }
      return currentItterationCounter;
  }

  public void buildSubContextsStructure(String nameStructure, int clID) throws javax.naming.NamingException {
    try {
    // creates the subcontext structure between the root context and a replicated object
      JNDIHandle containerHandle = getRootContainer();
      JNDIHandle h = findObject(containerHandle, "root");
      Container c = getContainer(getLinkedContainer(h));
      String objectName = "";
      String containerName = "";
      StringTokenizer st = new StringTokenizer(nameStructure.substring(1), "/");

      while (st.hasMoreElements()) {
        objectName = st.nextToken();
        if (c != null) {
          if (c.getObject(objectName) == null) {
            // bind object
            c.append(objectName, DirObject.getNewDirObject(null, null), Constants.NOT_REPLICATED_OPERATION, clID);
            if (location.bePath()) {
              location.pathT("{buildSubContextsStructure} Create object with name [" + objectName + "] in  container with name [" + c.getContainerName() + "]");
            }
            // create container
            containerName += "/" + objectName;
            Container newc = new Container(containerName);
            newc.setData(Serializator.toByteArray(new Properties()));
            synchronized (this) {
              containersByName.put(containerName, newc);
            }
            if (location.bePath()) {
              location.pathT("{buildSubContextsStructure} Create container with name [" + containerName + "]");
            }
            //link object to container
            c.createLink(new JNDIHandleImpl(c.getContainerName(), objectName), containerName);
            if (location.bePath()) {
              location.pathT("{buildSubContextsStructure} link object with id [" + objectName + "] to container [" + containerName + "]");
            }
            c = newc;
          } else {
            c = getContainer(getLinkedContainer(c.getJNDIHandle(objectName)));
            containerName += "/" + objectName;
            if (location.bePath()) {
              location.pathT("{buildSubContextsStructure} context already exists with name [" + (c != null ? c.getContainerName() : "null") + "] and ID [" + (c != null ? c.getContainerName() : "null") + "]");
            }
          }
        }
      }
    } catch (Exception e) {
      NamingException ne = new NamingException("Exception during object serialization [Root Exception is:" + e + "]");
      ne.setRootCause(e);
      throw ne;
    }
  }

  /* unbind */
  /**
   * Unbind object
   *
   * @param container Container to work with
   * @param name Name to use
   * @throws NamingException Thrown if a problem occurs.
   */
  public void unbindObject(JNDIHandle container, String name, short typeOfOperation) throws javax.naming.NamingException {
    if (location.bePath()) {
      location.pathT("Unbind object with name [" + name + "] from container with handle " + container + ", type of operation [" + typeOfOperation + "]");
    }
    try {
      Container c = getContainer(container.getContainerName());

      if (c != null) {
      JNDIHandle j = c.getJNDIHandle(name);
      try {
        communicator.monitor.tryToAccess();
        if (typeOfOperation == Constants.REPLICATED_OPERATION || typeOfOperation == Constants.REMOTE_REPLICATED_OPERATION) {
          c.deleteObject(j);
            sendObject(c.getContainerName(),
                name,
                null,
                null,
                new byte[0],
                communicator.REPLICATE_UNBIND_OBJECT,
                (byte) typeOfOperation,
                0,
                null,
                this.clusterId);
          if (location.bePath()) {
            location.pathT("Unbind object with name [" + name + "] in container with name " + c.getContainerName() + " finished successfully; broadcast message sent to replicate the operation");
          }
        } else {
          c.deleteObject(j);
        }
      } finally {
        communicator.monitor.endAccess();
      }
      } else {
        if (location.bePath()) {
          location.pathT("Container with ID " + container.getContainerName() + " is not found => no need to make unbind");
        }
      }
    } catch (Exception e) {
      location.traceThrowableT(Severity.PATH, "Exception during unbind operation of object with name " + name + ".", e);
      NamingException ne = new NamingException("Exception during unbind operation of object with name " + name + ".");
      ne.setRootCause(e);
      throw ne;
    }
  }

  /**
   * Unbinds object
   *
   * @param objectHandle Handle of the object
   * @throws NamingException Thrown if a problem occurs.
   */
  public void unbindObject(JNDIHandle objectHandle, short typeOfOperation) throws javax.naming.NamingException {
    if (location.bePath()) {
      location.pathT("Unbind object with handle [" + objectHandle + "],  type of operation is [" + typeOfOperation + "]");
    }
    String name = getObjectName(objectHandle);

    try {
      Container c = getContainer(objectHandle.getContainerName());

      if (c != null) {
        try {
          communicator.monitor.tryToAccess();
          if (typeOfOperation == Constants.REPLICATED_OPERATION || typeOfOperation == Constants.REMOTE_REPLICATED_OPERATION) {
            c.deleteObject(objectHandle);
            sendObject(c.getContainerName(),
                name,
                null,
                null,
                new byte[0],
                communicator.REPLICATE_UNBIND_OBJECT,
                (byte) typeOfOperation,
                0,
                null,
                this.clusterId);
            if (location.bePath()) {
              location.pathT("Unbind object with name [" + name + "] in container with name " + c.getContainerName() + " finished successfully; broadcast message sent to replicate the operation");
            }
          } else {
            c.deleteObject(objectHandle);
          }
        } finally {
          communicator.monitor.endAccess();
        }
      } else {
      	if (location.bePath()) {
      	  location.pathT("Container with ID " + objectHandle.getContainerName() + " is not found => no need to make unbind");
      	}
      }
    } catch (Exception e) {
      location.traceThrowableT(Severity.PATH, "Exception during unbind operation of object with name " + name + ".", e);
      NamingException ne = new NamingException("Exception during unbind operation of object with name " + name + ".");
      ne.setRootCause(e);
      throw ne;
    }
  }

  /**
   * Rebind object
   *
   * @param container Container to use
   * @param name Name to work with
   * @param newData New data to set
   * @throws NamingException Thrown if a problem occurs.
   */
  public void rebindObject(JNDIHandle container, String name, byte[] newData, short typeOfOperation) throws javax.naming.NamingException {
    if (location.bePath()) {
      location.pathT("Rebind object with name [" + name + "] in container with handle [" + container + "],  type of operation is [" + typeOfOperation + "]");
    }
    try {
      Container c = getContainer(container.getContainerName());

      if (c != null) {
        JNDIHandle j = c.getJNDIHandle(name);
        try {
          communicator.monitor.tryToAccess();
          if (typeOfOperation == Constants.REPLICATED_OPERATION || typeOfOperation == Constants.REMOTE_REPLICATED_OPERATION) {
            c.modify(j, newData);
            sendObject(c.getContainerName(),
                name,
                null,
                null,
                newData,
                communicator.REPLICATE_OBJECT_REBIND,
                (byte) typeOfOperation,
                0,
                null,
                this.clusterId);
            if (location.bePath()) {
              location.pathT("Rebind object with name [" + name + "] in container with name " + c.getContainerName() + " finished successfully; broadcast message sent to replicate the operation");
            }
          } else {
            c.modify(j, newData);
            if (location.bePath()) {
              location.pathT("Rebind object with name [" + name + "] in container with name " + c.getContainerName() + " finished successfully");
            }
          }
        } finally {
          communicator.monitor.endAccess();
        }
      }
    } catch (Exception e) {
      location.traceThrowableT(Severity.PATH, "Exception during rebind operation of object with name " + name + ".", e);
      NamingException ne = new NamingException("Exception during rebind operation of object with name " + name + ".");
      ne.setRootCause(e);
      throw ne;
    }
  }

  /**
   * Rebinds object
   *
   * @param objectHandle Handle of the object
   * @param newData New data to set
   * @throws NamingException
   */
  public void rebindObject(JNDIHandle objectHandle, byte[] newData, short typeOfOperation) throws javax.naming.NamingException {
    if (location.bePath()) {
      location.pathT("Rebind object with handle [" + objectHandle + "],  type of operation is [" + typeOfOperation + "]");
    }
    String name = getObjectName(objectHandle);

    try {
      Container c = getContainer(objectHandle.getContainerName());

      if (c != null) {
        try {
          communicator.monitor.tryToAccess();
          if (typeOfOperation == Constants.REPLICATED_OPERATION || typeOfOperation == Constants.REMOTE_REPLICATED_OPERATION) {

            c.modify(objectHandle, newData);
            sendObject(c.getContainerName(),
                getObjectName(objectHandle),
                null,
                null,
                newData,
                communicator.REPLICATE_OBJECT_REBIND,
                (byte) typeOfOperation,
                0,
                null,
                this.clusterId);
            if (location.bePath()) {
              location.pathT("Rebind object with name [" + name + "] in container with name " + c.getContainerName() + " finished successfully; broadcast message sent to replicate the operation");
            }
          } else {
            c.modify(objectHandle, newData);
            if (location.bePath()) {
              location.pathT("Rebind object with name [" + name + "] in container with name " + c.getContainerName() + " finished successfully");
            }
          }
        } finally {
          communicator.monitor.endAccess();
        }
      }
    } catch (Exception e) {
      location.traceThrowableT(Severity.PATH, "Exception during rebind operation of object with name " + name + ".", e);
      NamingException ne = new NamingException("Exception during rebind operation of object with name " + name + ".");
      ne.setRootCause(e);
      throw ne;
    }
  }

  /**
   * Renames object
   *
   * @param container Container to work with
   * @param name Name to use
   * @param newName New name to be set
   * @param typeOfOperation the type of the operation
   * @throws NamingException Thrown if a problem occurs.
   */
  public void renameObject(JNDIHandle container, String name, String newName, short typeOfOperation) throws javax.naming.NamingException {
    if (location.bePath()) {
      location.pathT("Rename object with name [" + name + "] to name [" + newName + "] in container with handle [" + container + "], type of operation is [" + typeOfOperation + "]");
    }
    try {
      Container c = getContainer(container.getContainerName());
      if (c != null) {
        JNDIHandle j = c.getJNDIHandle(name);
        try {
          communicator.monitor.tryToAccess();
          if (typeOfOperation == Constants.REPLICATED_OPERATION || typeOfOperation == Constants.REMOTE_REPLICATED_OPERATION) {
            c.rename(j, newName);
            sendObject(c.getContainerName(),
                newName,
                name,
                null,
                new byte[0],
                communicator.REPLICATE_RENAME_OBJECT,
                (byte) typeOfOperation,
                0,
                null,
                this.clusterId);
            if (location.bePath()) {
              location.pathT("Rename object with name [" + name + "] and ID [" + j.getObjectName() + "] in container with name " + c.getContainerName() + " finished successfully; broadcast message sent to replicate the operation");
            }
          } else {
            c.rename(j, newName);
            if (location.bePath()) {
              location.pathT("Rename object with name [" + name + "] and ID [" + j.getObjectName() + "] in container with name " + c.getContainerName() + " finished successfully");
            }
          }
        } finally {
          communicator.monitor.endAccess();
        }
      }
    } catch (Exception e) {
      location.traceThrowableT(Severity.PATH, "Exception during rename operation of object with name " + name + ".", e);
      NamingException ne = new NamingException("Exception during rename operation of object with name " + name + ".");
      ne.setRootCause(e);
      throw ne;
    }
  }

  /**
   * Renames object
   *
   * @param objectHandle Handle of the object
   * @param newName Name to be set
   * @param typeOfOperation the type of the operation
   * @throws NamingException Thrown if a problem occurs.
   */
  public void renameObject(JNDIHandle objectHandle, String newName, short typeOfOperation) throws javax.naming.NamingException {
    if (location.bePath()) {
      location.pathT("Rename object with handle [" + objectHandle + "] to name [" + newName + "], type of operation is [" + typeOfOperation + "]");
    }
    String name = getObjectName(objectHandle);
    try {
      Container c = getContainer(objectHandle.getContainerName());
      if (c != null) {
      try {
        communicator.monitor.tryToAccess();
        if (typeOfOperation == Constants.REPLICATED_OPERATION || typeOfOperation == Constants.REMOTE_REPLICATED_OPERATION) {
          c.rename(objectHandle, newName);
          sendObject(c.getContainerName(),
              newName,
              objectHandle.getObjectName(),
              null,
              new byte[0],
              communicator.REPLICATE_RENAME_OBJECT,
              (byte) typeOfOperation,
              0,
              null,
              this.clusterId);
          if (location.bePath()) {
            location.pathT("Rename object with name [" + name + "] in container with name " + c.getContainerName() + " finished successfully; broadcast message sent to replicate the operation");
          }
        } else {
          c.rename(objectHandle, newName);
          if (location.bePath()) {
            location.pathT("Rename object with name [" + name + "] in container with name " + c.getContainerName() + " finished successfully");
          }
        }
      } finally {
        communicator.monitor.endAccess();
      }
      }
    } catch (Exception e) {
      location.traceThrowableT(Severity.PATH, "Exception during rename operation of object with name " + name + ".", e);
      NamingException ne = new NamingException("Exception during rename operation of object with name " + name + ".");
      ne.setRootCause(e);
      throw ne;
    }
  }

  /**
   * Reads object
   *
   * @param container Container to work with
   * @param name Name to use
   * @return Data read
   * @throws NamingException Thrown if a problem occurs.
   */
  public byte[] readObject(JNDIHandle container, String name) throws javax.naming.NamingException {
    Container c = getContainer(container.getContainerName());

    if (c != null) {
      JNDIHandle j = c.getJNDIHandle(name);
      return c.readObject(j);
    } else {
      return null;
    }
  }

  /**
   * Read object
   *
   * @param objectHandle Handle of the object
   * @return Data read
   * @throws NamingException Thrown if a problem occurs.
   */
  public byte[] readObject(JNDIHandle objectHandle) throws javax.naming.NamingException {
    if (objectHandle == null) {
      return null;
    }

    Container c = getContainer(objectHandle.getContainerName());

    if (c != null) {
      return c.readObject(objectHandle);
    } else {
      return null;
    }
  }

  /* list object */
  /**
   * Lists objects
   *
   * @param container Container to work with
   * @param name Name to use
   * @return Enumeration of handles of objects stored in the container
   * @throws NamingException Thrown if a problem occurs.
   */
  public JNDIHandleEnumeration listObjects(JNDIHandle container, String name) throws javax.naming.NamingException {
    Container c = getContainer(container.getContainerName());

    if (c != null) {
      return c.getAllByName();
    } else {
      return null;
      // this block is empty
    }
  }

  /**
   * Gets the name of the object
   *
   * @param object Handle of the object
   * @return Name of the object
   * @throws NamingException Thrown if a problem occurs.
   */
  public String getObjectName(JNDIHandle object) throws javax.naming.NamingException {
    Container c = getContainer(object.getContainerName());

    if (c != null) {
      return c.getNameOf(object);
    } else {
      return null;
      // this block is empty
    }
  }

  public short getObjectType(JNDIHandle object) throws javax.naming.NamingException {
    Container c = getContainer(object.getContainerName());

    if (c != null) {
      return c.getTypeOf(object);
    } else {
      return -1;
      // this block is empty
    }
  }

  /**
   * Find object
   *
   * @param containerHandle
   * @param name
   * @return JNDIHandle of the found object
   * @throws NamingException
   */
  public JNDIHandle findObject(JNDIHandle containerHandle, String name) throws javax.naming.NamingException {
    if (location.bePath()) {
      location.pathT("Find object with name [" + name + "] in container with handle [" + containerHandle + "]");
    }
    Container currentContainer = getContainer(containerHandle);
    if (currentContainer != null) {

      JNDIHandle object = currentContainer.getJNDIHandle(name);

      if (object != null) {
        if (currentContainer.getTypeOf(object) == Constants.REMOTE_REPLICATED_OPERATION) {
          int clID = currentContainer.getClIdOf(object);
          if (clID != clusterId && clID != 0) {
            if (communicator.clContext.getClusterMonitor().getParticipant(clID) == null) {
              try {
                communicator.monitor.tryToAccess();
                Container c = getContainer(containerHandle);
                c.deleteObject(object);
                sendObject(c.getContainerName(),
                    name,
                    "",
                    null,
                    new byte[0],
                    communicator.REPLICATE_UNBIND_OBJECT,
                    (byte) Constants.REMOTE_REPLICATED_OPERATION,
                    0,
                    null,
                    this.clusterId);
                object = null;
                if (location.bePath()) {
                  location.pathT("object with name [" + name + "] found but its source server [" + containerHandle.getContainerName() + "] is dead => delete the object and send broadcase to replicate delete operation.");
                }
              } finally {
                communicator.monitor.endAccess();
              }
            }
          }
        }
        if (location.bePath()) {
          location.pathT("Object with name [" + name + "] found is [" + object + "]");
        }
        return object;
      }
    }
    if (location.bePath()) {
      location.pathT("Object with name [" + name + "] was not found");
    }
    return null;
  }

  /**
   * Moves object
   *
   * @param container Source container
   * @param name Name to work with
   * @param newContainer Destination container
   * @param newName New name
   * @throws NamingException Thrown if a problem occurs.
   */
  public void moveObject(JNDIHandle container, String name, JNDIHandle newContainer, String newName, short typeOfOperation) throws javax.naming.NamingException {
    if (location.bePath()) {
      location.pathT("Move object with name [" + name + "] from container with handle [" + container + "] to container with handle [" + newContainer + "], type of operation is [" + typeOfOperation + "]");
    }
    try {
      Container oldc = getContainer(container);
      Container newc = getContainer(newContainer);

      if (oldc != null && newc != null) {

      JNDIHandle object = oldc.getJNDIHandle(name);
      byte[] data = readObject(object);
      JNDIHandle newObject = bindObject(newContainer, newName, data, typeOfOperation);
      JNDIHandle linkedContainer = getLinkedContainer(object);

      if (linkedContainer != null) {
        linkObjectToContainer(newObject, linkedContainer, typeOfOperation);
      }

      unbindObject(container, name, typeOfOperation);
      }
    } catch (Exception e) {
      location.traceThrowableT(Severity.PATH, "Exception during move operation of object with name " + name + ".", e);
      NamingException ne = new NamingException("Exception during move operation of object with name " + name + ".");
      ne.setRootCause(e);
      throw ne;
    }
  }

  /**
   * Moves object
   *
   * @param objectHandle Handle to the object
   * @param newContainer Destination container
   * @param newName Name to set
   * @throws NamingException Thrown if a problem occurs.
   */
  public void moveObject(JNDIHandle objectHandle, JNDIHandle newContainer, String newName, short typeOfOperation) throws javax.naming.NamingException {

  }

  /**
   * Links object to container
   *
   * @param objectHandle Handle of the object
   * @param containerHandle Handle of the container
   * @throws NamingException Thrown if a problem occurs.
   */
  public void linkObjectToContainer(JNDIHandle objectHandle, JNDIHandle containerHandle, short typeOfOperation) throws javax.naming.NamingException {
    if (location.bePath()) {
      location.pathT("Link object with handle [" + objectHandle + "] to container with handle [" + containerHandle + "], type of operation is [" + typeOfOperation + "]");
    }
    String cid = objectHandle.getContainerName();
    Container c = getContainer(cid);

    if (c != null) {
      try {
        communicator.monitor.tryToAccess();
        if (typeOfOperation == Constants.REPLICATED_OPERATION || typeOfOperation == Constants.REMOTE_REPLICATED_OPERATION) {
          c.createLink(objectHandle, containerHandle.getContainerName());
          sendObject(c.getContainerName(),
            objectHandle.getObjectName(),
            containerHandle.getContainerName(),
            null,
            new byte[0],
            communicator.REPLICATE_LINK_OBJECT_TO_CONTAINER,
            (byte) typeOfOperation,
            0,
            null,
            this.clusterId);
          if (location.bePath()) {
            location.pathT("Link object with name [" + objectHandle.getObjectName() + "] to container with name " + c.getContainerName() + " and link name [" + containerHandle.getContainerName() + "] finished successfully; broadcast message sent to replicate the operation");
          }
        } else {
          c.createLink(objectHandle, containerHandle.getContainerName());
          if (location.bePath()) {
            location.pathT("Link object with name [" + objectHandle.getObjectName() + "] to container with name " + c.getContainerName() + " and link name [" + containerHandle.getContainerName() + "] finished successfully");
          }
        }
      } finally {
        communicator.monitor.endAccess();
      }
    } else {
    	if (location.bePath()) {
    	  location.pathT("Container with name ["+ cid +"] is not found => cannot finish link object to container operation");
    	}
    }
  }

  /**
   * Removes linked container
   *
   * @param objectHandle Handle of the object
   * @throws NamingException Thrown if a problem occurs.
   */
  synchronized public void removeLinkedContainer(JNDIHandle objectHandle, short typeOfOperation) throws javax.naming.NamingException {
    if (location.bePath()) {
      location.pathT("Remove Linked container from object with handle [" + objectHandle + "], type of operation is [" + typeOfOperation + "]");
    }
    Container c = getContainer(objectHandle.getContainerName());
    try {
      communicator.monitor.tryToAccess();
      if (typeOfOperation == Constants.REPLICATED_OPERATION || typeOfOperation == Constants.REMOTE_REPLICATED_OPERATION) {
        c.removeLink(objectHandle);
        sendObject(objectHandle.getContainerName(),
            null,
            null,
            null,
            new byte[0],
            communicator.REPLICATE_REMOVE_LINKED_CONTAINER,
            (byte) typeOfOperation,
            0,
            null,
            this.clusterId);
        if (location.bePath()) {
          location.pathT("Remove Linked container from object with name [" + objectHandle.getObjectName() + "] in container with name [" + objectHandle.getContainerName() + "] finished successfully; broadcast message sent to replicate the operation");
        }
      } else {
        c.removeLink(objectHandle);
        if (location.bePath()) {
          location.pathT("Remove Linked container from object with name [" + objectHandle.getObjectName() + "] in container with name [" + objectHandle.getContainerName() + "] finished successfully");
        }
      }
    } finally {
      communicator.monitor.endAccess();
    }
  }

  /**
   * Gets the linked container
   *
   * @param objectHandle Handle of the object
   * @return Handle of the container linked to the object
   * @throws NamingException
   */
  public JNDIHandle getLinkedContainer(JNDIHandle objectHandle) throws javax.naming.NamingException {
    Container c = getContainer(objectHandle.getContainerName());
    if (c != null) {
      return c.getLinkedContainer(objectHandle);
    }
    return null;
  }

  /**
   * Creates a handle
   *
   * @return Handle created
   * @throws NamingException Thrown if a problem occurs.
   */
  public JNDIHandle createHandle() throws javax.naming.NamingException {
    return new JNDIHandleImpl(null, null);
  }

  /* debuging methods */
  /**
   * Prints data stored
   */
  public void printAll() {
    System.out.println(" *** Persistent base *** "); //$JL-SYS_OUT_ERR$
    Object[] containers = containersByName.getAllValues();

    for (int i = 0; i < containers.length; i++) {
      Container c = (Container) containers[i];
      System.out.print(c.getContainerName() + " [ "); // $JL-SYS_OUT_ERR$
      c.printAll();
      System.out.println(" ]"); //$JL-SYS_OUT_ERR$
    }
  }

  /**
   * Gets description of the data
   *
   * @param j Handle to use
   * @return Description requested
   * @throws NamingException
   */
  private String datadesc(JNDIHandle j) throws javax.naming.NamingException {
    try {
      switch (getObjectType(j)) {
        case 2: {
          return "O-REPLO";
        }
        case 3: {
          return "O-LOCO";
        }
        case 4: {
          return "O-REPLREM";
        }
      }
    } catch (ClassCastException c) {
      // Excluding this catch block from JLIN $JL-EXC$ since there's no
      // need to log this exception
      return "NCLU";
    }
    return "UNK";
  }

  /**
   * Prins the data as a tree
   *
   * @param cont Container to start from
   * @param level Level of recursion
   * @throws NamingException Thrown if a problem occurs.
   */
  private void printRec(JNDIHandle cont, int level, PrintStream out) throws javax.naming.NamingException {
    JNDIHandleEnumeration je = listObjects(cont, "");

    while (je.hasMoreElements()) {
      JNDIHandle j = je.nextObject();

      if (getLinkedContainer(j) != null) {
        for (int i = 0; i < level + 1; i++) {
          out.print("   ");
        }

        out.println("[CONT " + getObjectName(j) + " " + ((JNDIHandleImpl) getLinkedContainer(j)).getContainerName() + "]" + (datadesc(j).equals(
            "O-LOCO") ? "CO-LOCC" : "CO-REPLC"));
        printRec(getLinkedContainer(j), level + 1, out);
      } else {
        for (int i = 0; i < level + 1; i++) {
          out.print("   ");
        }

        out.println("*(" + getObjectName(j) + " " + ((JNDIHandleImpl) j).getObjectName() + ")" + datadesc(j));
      }
    }
  }

  /**
   * Prints a tree
   *
   * @throws NamingException Thrown if a problem occurs.
   */
  public void printTree(PrintStream out) throws javax.naming.NamingException {
    out.println("*** TREE *** ");
    out.println("[CONT " + "root" + " " + ((JNDIHandleImpl) getRootContainer()).getContainerName() + "]" + "ROOT_CONTEXT");
    printRec(getRootContainer(), 0, out);
  }

  /**
   * Creates container Handle from given Object Handle
   *
   * @param j create a handle for a container
   */
  public JNDIHandle createContianerHandle(JNDIHandle j) {
    return null;
  }

  // converts two integers (clusterId, container/object id) into a long
  public static final long twoIntsToLong(int a, int b) {
    /*
    * int li, ri; ri = (a & 0x000000ff) | ((a & 0x0000ff00) << 8) | ((a &
    * 0x00ff0000) << 16) | (a << 32); li = (b & 0x000000ff) | ((b &
    * 0x0000ff00) << 8) | ((b & 0x00ff0000) << 16) | (b << 32); return
    * ((long) li << 32) | (ri & 0xFFFFFFFFL);
    */
    return ((long) b << 32) | a;
  }

  // converts a long into two integers (clusterId, container/object id)
  public static final int[] longTo2Ints(long a) {
    int[] rez = new int[2];
    rez[0] = (((byte) a) & 0x000000ff) | ((((byte) (a >> 8)) & 0x000000ff) << 8) | ((((byte) (a >> 16)) & 0x000000ff) << 16) | (((byte) (a >> 24)) << 24);
    rez[1] = ((((byte) (a >> 32)) & 0x000000ff) << 32) | ((((byte) (a >> 40)) & 0x000000ff) << 40) | ((((byte) (a >> 48)) & 0x000000ff) << 48) | (((byte) (a >> 56)) << 56);
    return rez;
  }


  /**
   * Used for debugging purposes
   *
   * @param j create a handle for a container
   */
  private void writeDebugLog(String s) {
    if (location.bePath()) {
      location.pathT(s);
    }
  }
}