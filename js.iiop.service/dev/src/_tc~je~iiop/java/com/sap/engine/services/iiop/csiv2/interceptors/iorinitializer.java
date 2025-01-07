/**
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.services.iiop.csiv2.interceptors;

import org.omg.PortableInterceptor.ORBInitializer;
import org.omg.PortableInterceptor.ORBInitInfo;
import org.omg.PortableInterceptor.IORInterceptor;
import org.omg.PortableInterceptor.IORInfo;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.IOP.*;
import org.omg.CORBA.ORB;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.CompletionStatus;
import com.sap.engine.services.iiop.internal.portable.IIOPOutputStream;
import com.sap.engine.services.iiop.internal.ClientORB;
import com.sap.engine.services.iiop.csiv2.CSIIOP.*;

import java.util.Properties;
import java.io.IOException;

import iaik.security.provider.IAIK;


/*

 * @author Ivan Atanassov
 * @version 4.0
 */
public class IORInitializer extends org.omg.CORBA.LocalObject implements ORBInitializer, IORInterceptor {
  public static final String NONE = "none";
  public static final String SUPPORTED = "supported";
  public static final String REQUIRED = "required";

  public static int sslport = 0;

  public static String integrity = REQUIRED;
  public static String confidentiality = REQUIRED;
  public static String establishTrustInTarget = REQUIRED;
  public static String establishTrustInClient = REQUIRED;

  private transient ORB orb = null;
  private int target_supports;    //106
  private int target_requires;    //106



  /**
   * Called during ORB initialization.  If it is expected that initial
   * services registered by an interceptor will be used by other
   * interceptors, then those initial services shall be registered at
   * this point via calls to
   * <code>ORBInitInfo.register_initial_reference</code>.
   *
   * @param info provides initialization attributes and operations by
   *     which Interceptors can be registered.
   */
  public void pre_init(ORBInitInfo info) {
  }

  /**
   * Called during ORB initialization. If a service must resolve initial
   * references as part of its initialization, it can assume that all
   * initial references will be available at this point.
   * <p>
   * Calling the <code>post_init</code> operations is not the final
   * task of ORB initialization. The final task, following the
   * <code>post_init</code> calls, is attaching the lists of registered
   * interceptors to the ORB. Therefore, the ORB does not contain the
   * interceptors during calls to <code>post_init</code>. If an
   * ORB-mediated call is made from within <code>post_init</code>, no
   * request interceptors will be invoked on that call.
   * Likewise, if an operation is performed which causes an IOR to be
   * created, no IOR interceptors will be invoked.
   *
   * @param info provides initialization attributes and
   *     operations by which Interceptors can be registered.
   */
  public void post_init(ORBInitInfo info) {
    target_supports = getTargetSupports();
    target_requires = getTargetRequires();

    try {
      sslport = ClientORB.communicationLayer.openSSLServerSocket(sslport);
      info.add_ior_interceptor(this);
    } catch (DuplicateName e) {
      throw new INTERNAL("Duplicated name", 0, CompletionStatus.COMPLETED_NO);
    } catch (IOException ioex) {
      ioex.printStackTrace();
      throw new INTERNAL("Cannot open SSL server socket: " + ioex.toString());
    }
  }

  /**
   * A server side ORB calls the <code>establish_components</code>
   * operation on all registered <code>IORInterceptor</code> instances
   * when it is assembling the list of components that will be included
   * in the profile or profiles of an object reference. This operation
   * is not necessarily called for each individual object reference.
   * For example, the POA specifies policies at POA granularity and
   * therefore, this operation might be called once per POA rather than
   * once per object. In any case, <code>establish_components</code> is
   * guaranteed to be called at least once for each distinct set of
   * server policies.
   * <p>
   * An implementation of <code>establish_components</code> must not
   * throw exceptions. If it does, the ORB shall ignore the exception
   * and proceed to call the next IOR Interceptor's
   * <code>establish_components</code> operation.
   *
   * @param info The <code>IORInfo</code> instance used by the ORB
   *     service to query applicable policies and add components to be
   *     included in the generated IORs.
   */
  public void establish_components(IORInfo info) {
    IIOPOutputStream out = new IIOPOutputStream(orb);
    out.setEndian(false);
    out.write_boolean(false);
    SSL sslComp = new SSL(sslport, (short) target_requires, (short) target_supports);
    SSLHelper.write(out, sslComp);
    info.add_ior_component_to_profile(new TaggedComponent(TAG_SSL_SEC_TRANS.value, out.toByteArray()), TAG_INTERNET_IOP.value);
  }

  /**
   * Provides an opportunity to destroy this interceptor.
   * The destroy method is called during <code>ORB.destroy</code>. When an
   * application calls <code>ORB.destroy</code>, the ORB:
   * <ol>
   *   <li>waits for all requests in progress to complete</li>
   *   <li>calls the <code>Interceptor.destroy</code> operation for each
   *       interceptor</li>
   *   <li>completes destruction of the ORB</li>
   * </ol>
   * Method invocations from within <code>Interceptor.destroy</code> on
   * object references for objects implemented on the ORB being destroyed
   * result in undefined behavior. However, method invocations on objects
   * implemented on an ORB other than the one being destroyed are
   * permitted. (This means that the ORB being destroyed is still capable
   * of acting as a client, but not as a server.)
   */
  public void destroy() {
  }

  /**
   * Returns the name of the interceptor.
   * <p>
   * Each Interceptor may have a name that may be used administratively
   * to order the lists of Interceptors. Only one Interceptor of a given
   * name can be registered with the ORB for each Interceptor type. An
   * Interceptor may be anonymous, i.e., have an empty string as the name
   * attribute. Any number of anonymous Interceptors may be registered with
   * the ORB.
   *
   * @return the name of the interceptor.
   */
  public String name() {
    return "ssl_ior";
  }

    /**
   * Get the value of target_supports for the transport layer.
   */
  public int getTargetSupports() {
    int supports = 0;

    if (!integrity.equals(NONE)) {
      supports = supports | Integrity.value;
    }

    if (!confidentiality.equals(NONE)) {
      supports = supports | Confidentiality.value;
    }

    if (!establishTrustInTarget.equals(NONE)) {
      supports = supports | EstablishTrustInTarget.value;
    }

    if (!establishTrustInClient.equals(NONE)) {
      supports = supports | EstablishTrustInClient.value;
    }

    return supports;
  }

  /**
   * Get the value of target_requires for the transport layer.
   */
  public int getTargetRequires() {
    int requires = 0;

    if (integrity.equals(REQUIRED)) {
      requires = requires | Integrity.value;
    }

    if (confidentiality.equals(REQUIRED)) {
      requires = requires | Confidentiality.value;
    }

    if (establishTrustInTarget.equals(REQUIRED)) {
      requires = requires | EstablishTrustInTarget.value;
    }

    if (establishTrustInClient.equals(REQUIRED)) {
      requires = requires | EstablishTrustInClient.value;
    }

    return requires;
  }
}
