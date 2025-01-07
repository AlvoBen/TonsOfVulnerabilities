/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidentonlyial and proprietary information
 * Information and shall use it  in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.csiv2;

import com.sap.engine.interfaces.csiv2.*;
import com.sap.engine.services.iiop.CORBA.SimpleProfile;
import com.sap.engine.services.iiop.CORBA.Profile;
import com.sap.engine.services.iiop.internal.portable.IIOPOutputStream;
import com.sap.engine.services.iiop.internal.portable.IIOPInputStream;
import com.sap.engine.services.iiop.csiv2.CSIIOP.*;
import com.sap.engine.services.iiop.csiv2.GSSUP.*;
import com.sap.engine.services.iiop.server.CommunicationLayerImpl;

import java.io.IOException;
import java.rmi.Remote;
import java.util.Vector;

import org.omg.CORBA.ORB;



public class EJBIORGenerator implements EJBIORGeneratorInterface {

  public static final int SUPPORTED_IDENTITY_TOKEN_TYPES = 15;
  private transient ORB orb = null;
  private int sslPort = 684;
  private int mutualsslPort = 684;
  private String host_name = "127.0.0.1";
  transient CommunicationLayerImpl comLayer;

  public EJBIORGenerator(ORB orb, CommunicationLayerImpl comLayer) {
    this.orb = orb;
    this.comLayer = comLayer;
  }

  public void resetHostAndPort(Object[] sslAddress) {
    if (sslAddress != null) {
      host_name = (String) sslAddress[0];
      sslPort = (Integer) sslAddress[1];
      mutualsslPort = sslPort;
    }
  }

  public void resetHostAndPort(Vector sslAddress) {  //must not be used TODO - to change the interface
    if (sslAddress != null) {
      host_name = (String) sslAddress.elementAt(0);
      sslPort = (Integer) sslAddress.elementAt(1);
      mutualsslPort = sslPort;
    }
  }

  /**
   * Create the security mechanism list tagged component based
   * on the deployer specified configuration information.
   */
  public SimpleProfileInterface generateSecurityTaggedComponent(IORDescriptor[] desc) {
    if (desc == null || desc.length == 0) {
      return null;
    }

    try {
      CompoundSecMech[] mechList = createCompoundSecMechs(desc);
      SimpleProfileInterface sp = createTaggedComponent(mechList);
      return sp;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public void addTaggedComponent(Remote pro, SimpleProfileInterface profile) {
    org.omg.CORBA.portable.ObjectImpl obj = (org.omg.CORBA.portable.ObjectImpl) javax.rmi.CORBA.Util.getTie(pro);
    if (obj != null) {
      orb.connect(obj);
      com.sap.engine.services.iiop.CORBA.portable.DelegateImpl delegate = (com.sap.engine.services.iiop.CORBA.portable.DelegateImpl) obj._get_delegate();
      com.sap.engine.services.iiop.CORBA.IOR info = delegate.getIOR();
      Profile theProfile = info.getProfile();
      theProfile.addSimpleProfile(profile);

      if (sslRequired(profile)) {
        theProfile.setPort(0);
      }
    }
  }

  private SimpleProfileInterface createTaggedComponent(CompoundSecMech[] mechList) {
    IIOPOutputStream out = new IIOPOutputStream(orb);
    out.setEndian(false);
    out.write_boolean(false);
    boolean stateful = false;
    CompoundSecMechList list = new CompoundSecMechList(stateful, mechList);
    CompoundSecMechListHelper.write(out, list);
    byte[] buf = out.toByteArray();
    SimpleProfileInterface sp = new SimpleProfile(TAG_CSI_SEC_MECH_LIST.value, buf);
    return sp;
  }

  /**
   * Create the security mechanisms. Only 1 such mechanism is created
   * although the spec allows multiple mechanisms (in decreasing order
   * of preference)
   */
  private CompoundSecMech[] createCompoundSecMechs(IORDescriptor[] desc) throws IOException {
    CompoundSecMech[] mechList = new CompoundSecMech[desc.length];

    for (int i = 0; i < desc.length; i++) {
      int target_requires = getTargetRequires(desc[i]);
      SimpleProfileInterface transportMech = createSSLInfo(desc[i]);
      AS_ContextSec asContext = createASContextSec(desc[i]);
      SAS_ContextSec sasContext = createSASContextSec(desc[i]);
      int targ_req = target_requires | asContext.target_requires | sasContext.target_requires;
      mechList[i] = new CompoundSecMech((short) targ_req, transportMech, asContext, sasContext);
    } 

    return mechList;
  }

  /**
   * Create the AS layer context within a compound mechanism definition.
   */
  public AS_ContextSec createASContextSec(IORDescriptor iorDesc) throws IOException {
    AS_ContextSec asContext;
    short target_supports = 0;
    short target_requires = 0;
    byte[] client_authentication_mechanism = {};
    byte[] target_name = {};
    String authMethod = null;
    boolean authMethodRequired = false;

    if (iorDesc != null) {
      authMethod = iorDesc.getAuthenticationMethod();
      authMethodRequired = iorDesc.isAuthMethodRequired();
    }

    if (authMethodRequired && authMethod != null) {
      if (authMethod.equals(IORDescriptor.NONE)) {
        asContext = new AS_ContextSec(target_supports, target_requires, client_authentication_mechanism, target_name);
        return asContext;
      }

      client_authentication_mechanism = getMechanism();
      target_name = GSSUtils.createExportedName(GSSUtils.GSSUP_MECH_OID, IORDescriptor.DEFAULT_REALM.getBytes());
      target_supports = EstablishTrustInClient.value;
      target_requires = EstablishTrustInClient.value;
    }

    asContext = new AS_ContextSec(target_supports, target_requires, client_authentication_mechanism, target_name);
    return asContext;
  }

  /**
   * Create the SAS layer context within a compound mechanism definition.
   */
  public SAS_ContextSec createSASContextSec(IORDescriptor iorDesc) throws IOException {
    SAS_ContextSec sasContext;
    int target_supports = 0; //means that target supports ITTAbsent
    int target_requires = 0;
    ServiceConfiguration[] priv = new ServiceConfiguration[0];
    String callerPropagation = null;
    byte[][] mechanisms = {};
    int supported_identity_token_type = 0; //this shall be non-zero if target_supports is non-zero

    if (iorDesc != null) {
      callerPropagation = iorDesc.getCallerPropagation();
    }

    if (callerPropagation != null) {
      if (callerPropagation.equals(IORDescriptor.NONE)) {
        sasContext = new SAS_ContextSec((short) target_supports, (short) target_requires, priv, mechanisms, supported_identity_token_type);
        return sasContext;
      }

      target_supports = IdentityAssertion.value;
      byte[] upm = getMechanism(); //Only username_password mechanism
      mechanisms = new byte[1][upm.length];

      System.arraycopy(upm, 0, mechanisms[0], 0, upm.length);

      if (target_supports != 0) {
        supported_identity_token_type = SUPPORTED_IDENTITY_TOKEN_TYPES;
      }
    }

    sasContext = new SAS_ContextSec((short) target_supports, (short) target_requires, priv, mechanisms, supported_identity_token_type);
    return sasContext;
  }

  /**
   * Create the SSL tagged component within a compound mechanism
   * definition.
   */
  private SimpleProfileInterface createSSLInfo(IORDescriptor iorDesc) {
    resetHostAndPort(comLayer.getSSLAddress());

    int port = sslPort;
    int targetSupports = 0;
    int targetRequires = 0;

    if (iorDesc == null) { // this happens only for nameservice
      targetSupports = Integrity.value | Confidentiality.value | EstablishTrustInClient.value | EstablishTrustInTarget.value;
    } else {
      targetSupports = getTargetSupports(iorDesc);
      targetRequires = getTargetRequires(iorDesc);

      if ((targetRequires & EstablishTrustInClient.value) == EstablishTrustInClient.value) {
        port = mutualsslPort;
      }
    }

    /*
     * if both targetSupports and targetRequires are zero, then the
     * mechanism does not support a transport_mechanism and hence
     * a TAG_NULL_TAG must be generated.
     */
    if ((targetSupports | targetRequires) == 0) {
      byte[] b = {};
      SimpleProfileInterface sp = new SimpleProfile(TAG_NULL_TAG.value, b);
      return sp;
    }

    TransportAddress[] listTa = generateTransportAddresses(host_name, port);
    TLS_SEC_TRANS tls_sec = new TLS_SEC_TRANS((short) targetSupports, (short) targetRequires, listTa);
    IIOPOutputStream out = new IIOPOutputStream(orb);
    out.setEndian(true);
    out.write_boolean(true);
    TLS_SEC_TRANSHelper.write(out, tls_sec);
    byte[] buf = out.toByteArray();
    SimpleProfileInterface tc = new SimpleProfile(TAG_TLS_SEC_TRANS.value, buf);
    return tc;
  }

  private TransportAddress[] generateTransportAddresses(String host, int sslport) {
    TransportAddress ta = new TransportAddress(host, sslport);
    TransportAddress[] listTa = new TransportAddress[1];
    listTa[0] = ta;
    return listTa;
  }

  /**
   * Get the value of target_supports for the transport layer.
   */
  public int getTargetSupports(IORDescriptor iorDesc) {
    if (iorDesc == null) {
      return 0;
    }

    int supports = 0;
    String integrity = iorDesc.getIntegrity();

    if (!integrity.equals(IORDescriptor.NONE)) {
      supports = supports | Integrity.value;
    }

    String confidentiality = iorDesc.getConfidentiality();

    if (!confidentiality.equals(IORDescriptor.NONE)) {
      supports = supports | Confidentiality.value;
    }

    String establishTrustInTarget = iorDesc.getEstablishTrustInTarget();

    if (!establishTrustInTarget.equals(IORDescriptor.NONE)) {
      supports = supports | EstablishTrustInTarget.value;
    }

    String establishTrustInClient = iorDesc.getEstablishTrustInClient();

    if (!establishTrustInClient.equals(IORDescriptor.NONE)) {
      supports = supports | EstablishTrustInClient.value;
    }

    return supports;
  }

  /**
   * Get the value of target_requires for the transport layer.
   */
  public int getTargetRequires(IORDescriptor iorDesc) {
    if (iorDesc == null) {
      return 0;
    }

    int requires = 0;
    String integrity = iorDesc.getIntegrity();

    if (integrity.equals(IORDescriptor.REQUIRED)) {
      requires = requires | Integrity.value;
    }

    String confidentiality = iorDesc.getConfidentiality();

    if (confidentiality.equals(IORDescriptor.REQUIRED)) {
      requires = requires | Confidentiality.value;
    }

    String establishTrustInTarget = iorDesc.getEstablishTrustInTarget();

    if (establishTrustInTarget.equals(IORDescriptor.REQUIRED)) {
      requires = requires | EstablishTrustInTarget.value;
    }

    String establishTrustInClient = iorDesc.getEstablishTrustInClient();

    if (establishTrustInClient.equals(IORDescriptor.REQUIRED)) {
      requires = requires | EstablishTrustInClient.value;
    }

    return requires;
  }

  /**
   * Return the ASN.1 encoded representation of a GSS mechanism identifier.
   * Currently only the GSSUP Mechanism is supported.
   */
  private byte[] getMechanism() throws IOException {
    return GSSUtils.getDER(GSSUtils.GSSUP_MECH_OID);
  }

  private boolean sslRequired(SimpleProfileInterface taggedComponent) {
    byte[] taggedData = taggedComponent.getData();
    IIOPInputStream stream = new IIOPInputStream(orb, taggedData);
    CompoundSecMech[] list = CompoundSecMechListHelper.read(stream).mechanism_list;

    if (list == null || list.length == 0) {
      return false;
    }

    SimpleProfileInterface transportMech = list[0].transport_mech;

    if (transportMech.getTag() == TAG_NULL_TAG.value) {
      return false;
    } else if (transportMech.getTag() == TAG_TLS_SEC_TRANS.value) {
      byte[] transportData = transportMech.getData();
      IIOPInputStream inputStream = new IIOPInputStream(orb, transportData);
      boolean endian = inputStream.read_boolean();
      inputStream.setEndian(endian);
      TLS_SEC_TRANS transport = TLS_SEC_TRANSHelper.read(inputStream);
      return ((transport.target_requires & (Integrity.value | Confidentiality.value)) != 0);
    }

    return false;
  }

}

