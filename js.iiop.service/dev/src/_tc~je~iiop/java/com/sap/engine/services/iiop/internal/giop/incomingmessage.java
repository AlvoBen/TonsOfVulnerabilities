/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.internal.giop;

import com.sap.engine.services.iiop.CORBA.CodeSetChooser;
import com.sap.engine.services.iiop.core.MessageConstants;
import com.sap.engine.services.iiop.internal.SendingContext.RunTimeHepler;
import com.sap.engine.services.iiop.internal.portable.IIOPInputStream;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import com.sap.engine.services.iiop.system.CommunicationLayer;
import com.sap.engine.interfaces.cross.CrossMessage;
import com.sap.engine.interfaces.cross.Connection;
import org.omg.CORBA.*;
import org.omg.CORBA.Object;
import org.omg.IOP.ServiceContext;

/**
 * It is the base class of all incomming messages.
 *
 * @author Georgy Stanev, Ivan Atanassov
 * @version 4.0
 */
public abstract class IncomingMessage  extends IIOPInputStream implements CrossMessage {

  protected byte versionMajor;
  protected byte versionMinor;
  // if the incomming message is fragmented
  protected boolean isFragmented;
  protected boolean noResources = false;
  public CodeSetChooser icsChooser = null;
  protected Connection connection;

  protected boolean toSendCodeBase = false;

  protected static volatile int busyThreads = 0;    //counted only for IncomminRequest and LocateRequest
  //$JL-SER$
  /**
   * Constructor.
   *
   * @param   binaryData  The raw data.
   */
  protected IncomingMessage(byte[] binaryData) {
    super(CommunicationLayer.getORB(), binaryData);
    readFromBinaryData();
  }

  protected IncomingMessage(byte[] binaryData, int size) {
    super(CommunicationLayer.getORB(), binaryData, size);
    readFromBinaryData();
  }

  //////////////////////////// CrossMessage implementations ////////////////////
  public int getLength() {
    return size;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public Connection getConnection() {
    return connection;
  }

  public void setData(byte[] data, int length) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public void setConnection(Connection connection) {
    this.connection = connection;
  }

  public void release() {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  ///////////////////////////////////////////////////////////////////////////


  /**
   * Accessor method.
   *
   * @return     The isFragmented value.
   */
  public boolean fragmented() {
    return isFragmented;
  }

  public static int getBusyThreads() {
    return busyThreads;
  }

  public synchronized void addFragment(FragmentMessage fMessage) {
    // - 16 = 12-GIOP + 4-requestID
    byte[] newData = new byte[getLength() + fMessage.getLength() - 16];
    System.arraycopy(data, 0, newData, 0, getLength());
    System.arraycopy(fMessage.data, 16, newData, getLength(), fMessage.getLength() - 16);
    data = newData;
    setSize(getSize() + fMessage.getLength() - 16);
    isFragmented = fMessage.fragmented();
  }

  /**
   * Reads the GIOP and MESSAGE headers.
   *
   */
  protected final void readFromBinaryData() {
    readGIOPHeader();
  }

  //Mostly used in client implementation of ORB. On server process() do some additional stuff
  public abstract void process_initial();

  /**
   * Base readGIOPHeader method.
   *
   */
  protected abstract void readGIOPHeader();

  /**
   * Base readMessageHeader method.
   *
   */
  protected abstract void readMessageHeader();

  /**
   * Accessor method
   *
   * @return     The reques id.
   */
  public abstract int request_id();

  /**
   * Base method.
   *
   * @return     Outgoing message object.
   */
  public abstract OutgoingMessage getServerReply();

  protected ServiceContext[] readServiceContexts() {
    ServiceContext[] requestContexts = new ServiceContext[read_long()];
    byte[] contextData;
    for (int i = 0; i < requestContexts.length; i++) {
      try {
        int id = read_long();
        contextData = new byte[unaligned_read_long()];
        read_octet_array(contextData, 0, contextData.length);
        if (id == MessageConstants.CODESET_CONTEXT_ID) { // CODE SET CONTEXT
          IIOPInputStream is = new IIOPInputStream(orb, contextData);
          is.setEndian(is.read_boolean());
          icsChooser = new CodeSetChooser();
          icsChooser.setCharCodeset(is.read_long());
          icsChooser.setWCharCodeset(is.unaligned_read_long());
          setCodeSets(icsChooser.charCodeSet(), icsChooser.wcharCodeSet());
        }
        if (id == MessageConstants.CODEBASE_SENDING_CONTEXT_RUN_TIME) { // CODEBASE SENDING_CONTEXT_RUN_TIME
          IIOPInputStream is = new IIOPInputStream(orb, contextData);
          is.setEndian(is.read_boolean());
           setDefault_codebase(RunTimeHepler.narrow(is.read_Object()));
        }
        requestContexts[i] = new ServiceContext(id, contextData);
      } catch (Exception ex) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).errorT("IncommingMessage.readServiceContexts()", "receive service contexts failed: " + LoggerConfigurator.exceptionTrace(ex));
        }
      }
    }

    return requestContexts;
  }

  public boolean isToSendCodeBase() {
    return toSendCodeBase;
  }

  public void setToSendCodeBase(boolean toSendCodeBase) {
    this.toSendCodeBase = toSendCodeBase;
  }

/* Not implemented yet operations and that required from other interfaces */

  //ServerRequestInfoOperations
  public byte[] adapter_id() {
    throw new NO_IMPLEMENT();
  }

  //ServerRequestInfoOperations
  public Any sending_exception() {
    throw new NO_IMPLEMENT();
  }

  //ServerRequestInfoOperations
  public String target_most_derived_interface() {
    throw new NO_IMPLEMENT();
  }

  //ServerRequestInfoOperations
  public boolean target_is_a(String str) {
    throw new NO_IMPLEMENT();
  }

  //ServerRequestInfoOperations
  public Policy get_server_policy(int i) {
    throw new NO_IMPLEMENT();
  }

  //RequestInfoOperations
  public org.omg.Dynamic.Parameter[] arguments() {
   throw new NO_IMPLEMENT();
  }

  //RequestInfoOperations
  public TypeCode[] exceptions() {
    throw new NO_IMPLEMENT();
  }

  //RequestInfoOperations
  public String[] contexts() {
    throw new NO_IMPLEMENT();
  }

  //RequestInfoOperations
  public String[] operation_context() {
    throw new NO_IMPLEMENT();
  }

  //RequestInfoOperations
  public Any result() {
    throw new NO_IMPLEMENT();
  }

  //RequestInfoOperations
  public short sync_scope() {
    throw new NO_IMPLEMENT();
  }

  //RequestInfoOperations
  public short reply_status() {
    throw new NO_IMPLEMENT();
  }

  //RequestInfoOperations
  public Object forward_reference() {
    throw new NO_IMPLEMENT();
  }

  //org.omg.CORBA.Object method
  public boolean _is_equivalent(Object obj) {
    return equals(obj);
  }

  //org.omg.CORBA.Object method
  public boolean _non_existent() {
    return false;
  }

  //org.omg.CORBA.Object method
  public int _hash(int i) {
    return hashCode();
  }

  //org.omg.CORBA.Object method
  public boolean _is_a(String s) {
    return false;
  }

  //org.omg.CORBA.Object method
  public Object _duplicate() {
    return null;
  }

  //org.omg.CORBA.Object method
  public void _release() {

  }

  //org.omg.CORBA.Object method
  public Request _request(String s) {
    return null;
  }

  //org.omg.CORBA.Object method
  public Request _create_request(Context context, String s, NVList nvlist, NamedValue namedvalue) {
    return null;
  }

  //org.omg.CORBA.Object method
  public Request _create_request(Context context, String s, NVList nvlist, NamedValue namedvalue, ExceptionList exceptionlist, ContextList contextlist) {
    return null;
  }

  //org.omg.CORBA.Object method
  public Object _get_interface_def() {
    return null;
  }

  //org.omg.CORBA.Object method
  public Policy _get_policy(int i) {
    return null;
  }

  //org.omg.CORBA.Object method
  public DomainManager[] _get_domain_managers() {
    return null;
  }

  //org.omg.CORBA.Object method
  public Object _set_policy_override(Policy apolicy[], SetOverrideType setoverridetype) {
    return null;
  }

  public void generateNoResoucesErrorReply() {
    noResources = true;
  }
}
