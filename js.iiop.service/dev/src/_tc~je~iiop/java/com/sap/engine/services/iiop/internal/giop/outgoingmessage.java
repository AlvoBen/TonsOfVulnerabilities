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

import com.sap.engine.services.iiop.internal.portable.IIOPOutputStream;
import org.omg.CORBA.*;
import org.omg.IOP.ServiceContext;
import org.omg.CORBA.Object;
import com.sap.engine.services.iiop.CORBA.CodeSetChooser;
import com.sap.engine.services.iiop.core.MessageConstants;

/**
 * This class implements GIOP message header.
 * It is the base class of all GIOP messages available in CORBA 2.2 specification.
 * It also contains most of the constants used in GIOP protocol. This class is used
 * for parsing/creating GIOP message headers and reading/writing GIOP messages.
 *
 * @author Georgy Stanev, Nikolai Neichev, Ivan Atanassov
 * @version 4.0
 */
public abstract class OutgoingMessage extends IIOPOutputStream {
  //$JL-SER$
  /*tozi class izrazqwa GIOP MEssage Format-a i e bazov za izprashtashti se messagi*/
  protected byte versionMajor;
  protected byte versionMinor;
  protected byte flag; //endian
  protected boolean sendMe = false;
  protected int dataPosition = 0;
  protected org.omg.CORBA.portable.ObjectImpl target;
  public CodeSetChooser ocsChooser = null;

  protected OutgoingMessage(org.omg.CORBA.ORB orb, CodeSetChooser codeSetChooser) {
    super(orb);

    if (codeSetChooser != null) {
      this.ocsChooser = codeSetChooser;
      this.setCodeSets(codeSetChooser.charCodeSet(), codeSetChooser.wcharCodeSet());
    }
  }

  protected void writeServiceContexts(ServiceContext[] contexts) {
    boolean isThereCodeSetContext = false;
    if (contexts == null) {
      contexts = new ServiceContext[0];
    }
    for (ServiceContext aContext : contexts) {
      if (aContext.context_id == 1) {
        isThereCodeSetContext = true;
      }
    }
    if ((versionMinor > 0) && !isThereCodeSetContext) {
      write_long(contexts.length + 1);
      write_long(MessageConstants.CODESET_CONTEXT_ID); // code set ID

      beginEncapsulation(false);
      write_long((ocsChooser != null) ? ocsChooser.charCodeSet() : CodeSetChooser.charNativeCodeSet()); // char native code set
      write_long((ocsChooser != null) ? ocsChooser.wcharCodeSet() : CodeSetChooser.wcharNativeCodeSet()); // char native code set
      endEncapsulation();

    } else {
      write_long(contexts.length);
    }
    for (ServiceContext aContext : contexts) {
      write_long(aContext.context_id); // context id
      write_long(aContext.context_data.length); // context data size
      write_octet_array(aContext.context_data, 0, aContext.context_data.length);
    }
    setEnd_contexts_index(index);
  }

  protected abstract void writeGIOPHeader();

  protected abstract void writeMessageHeader();

  public final void flushData() {
    sendMe = true;
    writeGIOPHeader();
  }

  public boolean forSend() {
    return sendMe;
  }

  public void goToMessageHeaderPosition() {
    index = 12;
  }

  public void goToDataPosition() {
    index = dataPosition;
  }

  public int getDataPosition() {
    return dataPosition;
  }

  public void setTarget(org.omg.CORBA.Object target) {
    this.target = (org.omg.CORBA.portable.ObjectImpl) target;
  }

  //org.omg.PortableInterceptor.RequestInfoOperations not implemented methods
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


  //org.omg.CORBA.Object
  public boolean _is_equivalent(Object obj) {
    return equals(obj);
  }

  //org.omg.CORBA.Object
  public int _hash(int i) {
    return hashCode();
  }

  //org.omg.CORBA.Object
  public boolean _is_a(String s) {
    return false;
  }

  //org.omg.CORBA.Object
  public boolean _non_existent() {
    return false;
  }

  //org.omg.CORBA.Object
  public Object _duplicate() {
    return null;
  }

  //org.omg.CORBA.Object
  public void _release() {
  }

  //org.omg.CORBA.Object
  public Request _request(String s) {
    return null;
  }

  //org.omg.CORBA.Object
  public Request _create_request(Context context, String s, NVList nvlist, NamedValue namedvalue) {
    return null;
  }

  //org.omg.CORBA.Object
  public Request _create_request(Context context, String s, NVList nvlist, NamedValue namedvalue, ExceptionList exceptionlist, ContextList contextlist) {
    return null;
  }

  //org.omg.CORBA.Object
  public Object _get_interface_def() {
    return null;
  }

  //org.omg.CORBA.Object
  public Policy _get_policy(int i) {
    return null;
  }

  //org.omg.CORBA.Object
  public DomainManager[] _get_domain_managers() {
    return null;
  }

  //org.omg.CORBA.Object
  public Object _set_policy_override(Policy apolicy[], SetOverrideType setoverridetype) {
    return null;
  }
}