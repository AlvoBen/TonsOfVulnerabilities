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
package com.sap.engine.services.iiop.CORBA;

import com.sap.engine.services.iiop.PortableServer.RootPOA;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import com.sap.engine.services.iiop.internal.interceptors.PICurrentImpl;
import com.sap.engine.services.iiop.internal.portable.IIOPOutputStream;
import com.sap.engine.services.iiop.internal.giop.ClientRequest;
import com.sap.engine.services.iiop.core.MessageConstants;
import org.omg.CORBA.*;
import org.omg.CORBA.portable.Delegate;
import org.omg.PortableServer.POA;
import org.omg.IOP.ServiceContext;

import javax.rmi.CORBA.Util;
import java.lang.Object;

/**
 * A class providing APIs for the CORBA Object Request Broker
 * features.
 * <P>
 * An ORB makes it possible for CORBA objects to communicate
 * with each other by connecting objects making requests (clients) with
 * objects servicing requests (servers).
 * <P>
 *
 * The <code>ORB</code> class, which
 * encapsulates generic CORBA functionality, does the following:
 * <OL>
 * <li> initializes the ORB implementation by supplying values for
 *      predefined properties and environmental parameters
 * <li> obtains initial object references to services such as
 * the NameService using the method <code>resolve_initial_references</code>
 * <li> converts object references to strings and back
 * <li> connects the ORB to a servant (an instance of a CORBA object
 * implementation) and disconnects the ORB from a servant
 * <li> creates objects such as
 *   <ul>
 *   <li><code>TypeCode</code>
 *   <li><code>Any</code>
 *   <li><code>NamedValue</code>
 *   <li><code>Context</code>
 *   <li><code>Environment</code>
 *   <li>lists (such as <code>NVList</code>) containing these objects
 *   </ul>
 * <li> sends multiple messages in the DII
 * </OL>
 *
 * <P>
 * The <code>ORB</code> class can be used to obtain references to objects
 * implemented anywhere on the network.
 * <P>
 *
 * @author Georgy Stanev
 * @version 4.0
 */
public abstract class ORB extends org.omg.CORBA_2_3.ORB {
  public static int defaultPort = 900;
  protected static final String defaultHost = "127.0.0.1";
  protected static String[] initialArguments = {"-ORBInitialPort", "-ORBInitialHost"};
  protected int initialPort = defaultPort;
  protected String initialHost = defaultHost;

  protected POA rootPOA = new RootPOA(this);

  protected PICurrentImpl piCurrent = null;

  protected boolean isInitialized = false;
  protected int status = STATUS_OPERATING;

  protected static final byte STATUS_OPERATING = 1;
  protected static final byte STATUS_SHUTTING_DOWN = 2;
  protected static final byte STATUS_SHUTDOWN = 3;
  protected static final byte STATUS_DESTROYED = 4;


  public void set_parameters(java.applet.Applet app, java.util.Properties prop) {
    throw new NO_IMPLEMENT("ID019035: set_parameters() is not implemented");
  }

  public NVList create_list(int count) {
    return new NVListImpl(this);
  }

  public NamedValue create_named_value(String s, Any any, int flags) {
    return new NamedValueImpl(this, s, any, flags);
  }

  public ExceptionList create_exception_list() {
    return new ExceptionListImpl();
  }

  public ContextList create_context_list() {
    return new ContextListImpl();
  }

  public Context get_default_context() {
    throw new NO_IMPLEMENT("ID019094: get_default_context() is not implemented");
  } // abstract

  public Environment create_environment() {
    return new EnvironmentImpl();
  }

  public void send_multiple_requests_oneway(Request[] req) {
    throw new NO_IMPLEMENT("ID019095: send_miltiple_requests_oneway() is not implemented");
  } // abstract

  public void send_multiple_requests_deferred(Request[] req) {
    throw new NO_IMPLEMENT("ID019096: send_miltiple_requests_deffered() is not implemented");
  } // abstract

  public boolean poll_next_response() {
    return true;
  } // abstract

  public Request get_next_response() throws WrongTransaction {
    throw new NO_IMPLEMENT("ID019097: get_next_response() is not implemented");
  } // abstract

  public TypeCode get_primitive_tc(TCKind tcKind) {
    return new TypeCodeImpl(this, tcKind);
  } // abstract

  public TypeCode create_struct_tc(String id, String name, StructMember[] members) {
    return new TypeCodeImpl(this, TCKind._tk_struct, id, name, members);
  } // abstract

  public TypeCode create_union_tc(String id, String name, TypeCode discriminator_type, UnionMember[] members) {
    return new TypeCodeImpl(this, TCKind._tk_union, id, name, discriminator_type, members);
  } // abstract

  public TypeCode create_enum_tc(String id, String name, String[] members) {
    return new TypeCodeImpl(this, TCKind._tk_enum, id, name, members);
  } // abstract

  public TypeCode create_alias_tc(String id, String name, TypeCode original_type) {
    return new TypeCodeImpl(this, TCKind._tk_alias, id, name, original_type);
  } // abstract

  public TypeCode create_exception_tc(String id, String name, StructMember[] members) {
    return new TypeCodeImpl(this, TCKind._tk_except, id, name, members);
  } // abstract

  public TypeCode create_interface_tc(String id, String name) {
    return new TypeCodeImpl(this, TCKind._tk_objref, id, name);
  } // abstract

  public TypeCode create_abstract_interface_tc(String id, String name) {
    return new TypeCodeImpl(this, TCKind._tk_abstract_interface, id, name);
  } // abstract

  public TypeCode create_string_tc(int bound) {
    return new TypeCodeImpl(this, TCKind._tk_string, bound);
  } // abstract

  public TypeCode create_wstring_tc(int bound) {
    return new TypeCodeImpl(this, TCKind._tk_wstring, bound);
  }

  public TypeCode create_sequence_tc(int bound, TypeCode element_type) {
    return new TypeCodeImpl(this, TCKind._tk_sequence, bound, element_type);
  } // abstract

  public TypeCode create_recursive_sequence_tc(int bound, int offset) {
    throw new NO_IMPLEMENT("ID019099: create_recursive_sequence_tc() is not implemented");
  } // abstract

  public TypeCode create_array_tc(int length, TypeCode element_type) {
    return new TypeCodeImpl(this, TCKind._tk_array, length, element_type);
  } // abstract

  public org.omg.CORBA.TypeCode create_native_tc(String id, String name) {
    return new TypeCodeImpl(this, TCKind._tk_native, id, name);
  }


  public TypeCode create_value_box_tc(String id, String name, TypeCode boxed_type) {
    return new TypeCodeImpl(this, TCKind._tk_value_box, id, name, boxed_type);
  }

  public TypeCode create_value_tc(String id, String name, short type_modifier, TypeCode concrete_base, ValueMember[] members) {
    return new TypeCodeImpl(this, TCKind._tk_value, id, name, type_modifier, concrete_base, members);
  }

  public Any create_any() {
    return new AnyImpl(this);
  }

  public synchronized PICurrentImpl getPICurrent() {
    if (piCurrent == null) {
      synchronized (this) {
        if (piCurrent == null) {
          piCurrent = new PICurrentImpl(this);
        }
      }
    }

    return piCurrent;
  }

  public boolean isInitialized() {
    return isInitialized;
  }

  public abstract boolean isServerORB();

  public abstract Delegate getDelegate(IOR ior);

  public abstract boolean is_local(byte[] key);

  public abstract org.omg.CORBA.Object getObject(byte[] bKey);

  Object sync = new Object();

  public void run() {
    synchronized (sync) {
      try {
        sync.wait();
      } catch (InterruptedException e) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("ORB.run()", LoggerConfigurator.exceptionTrace(e));
        }
      }
    }
  }

  public void shutdown(boolean wait_for_completion) {
    status = STATUS_SHUTTING_DOWN;
    piCurrent = null;
    synchronized (sync) {
      sync.notifyAll();
    }
    status = STATUS_SHUTDOWN;
  }


  public void finalize() throws Throwable {
    piCurrent = null;
    super.finalize();
  }

  public POA getRootPOA() {
    return rootPOA;
  }

  private byte[] dataRuntimeCodebase = null;

  public void initializeRuntimeCodebase(ClientRequest clRequest) {
    if (dataRuntimeCodebase == null) {
      try {
        org.omg.CORBA.portable.ObjectImpl runtimeCodebase = (org.omg.CORBA.portable.ObjectImpl) Util.createValueHandler().getRunTimeCodeBase();
        if (runtimeCodebase != null) {
          org.omg.CORBA.ORB _orb = org.omg.CORBA.ORB.init(new String[0], null); //needed to differ server impl and client impl with different ORB provider
          _orb.connect(runtimeCodebase);
           org.omg.CORBA.Object codebaseObject = this.string_to_object(_orb.object_to_string(runtimeCodebase));
          IIOPOutputStream streamedRuntimeCodebase = new IIOPOutputStream(this);
          streamedRuntimeCodebase.set_encapsulation();
          streamedRuntimeCodebase.write_boolean(streamedRuntimeCodebase.getEndian());
          streamedRuntimeCodebase.write_Object(codebaseObject);
          dataRuntimeCodebase = streamedRuntimeCodebase.toByteArray();
          clRequest.add_request_service_context(new ServiceContext(MessageConstants.CODEBASE_SENDING_CONTEXT_RUN_TIME, dataRuntimeCodebase), false);
        }
      } catch (Exception e) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("Delegate.createRequest(). Cannot send a codebase context, caused by: ", LoggerConfigurator.exceptionTrace(e));
        }
      }
    } else {
      clRequest.add_request_service_context(new ServiceContext(MessageConstants.CODEBASE_SENDING_CONTEXT_RUN_TIME, dataRuntimeCodebase), false);
    }
  }
}
