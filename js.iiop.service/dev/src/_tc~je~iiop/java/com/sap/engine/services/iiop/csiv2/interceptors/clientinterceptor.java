package com.sap.engine.services.iiop.csiv2.interceptors;

import javax.security.auth.*;
import javax.resource.spi.security.PasswordCredential;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Iterator;
/* Import classes required for DER encoding and decoding */
import iaik.asn1.ASN1;
import iaik.asn1.SEQUENCE;
import iaik.asn1.DerCoder;
import org.omg.CORBA.*;
import org.omg.CORBA.LocalObject;
import org.omg.PortableInterceptor.*;
import org.omg.IOP.Codec;
import org.omg.IOP.ServiceContext;
import com.sap.engine.services.iiop.CORBA.IOR;
import com.sap.engine.services.iiop.csiv2.CSIIOP.*;
import com.sap.engine.services.iiop.csiv2.CSI.*;
import com.sap.engine.services.iiop.csiv2.GSSUP.*;
import com.sap.engine.services.iiop.CORBA.Profile;
import com.sap.engine.services.iiop.CORBA.portable.DelegateImpl;
import com.sap.engine.services.iiop.CORBA.portable.CORBAInputStream;
import com.sap.engine.services.iiop.internal.interceptors.MinorCodes;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import com.sap.engine.interfaces.csiv2.*;
import com.sap.engine.interfaces.security.SecurityContextObject;

public abstract class ClientInterceptor extends LocalObject implements ClientRequestInterceptor {

  protected transient ORB orb = null;
  private Codec codec = null;
  private static final int SECURITY_ATTRIBUTE_SERVICE_ID = 15;
  public static final int STATUS_PASSED = 0;
  public static final int STATUS_FAILED = 1;
  public static final int STATUS_RETRY = 2;
  private static final String DEFAULT_REALM = "default";

  public ClientInterceptor(ORB orb, Codec codec) {
    this.orb   = orb;
    this.codec = codec;
  }

  public String name() {
    return "security_client";
  }

  public void send_request(ClientRequestInfo cri) {
    /**
     * CSIV2 level 0 implementation only requires stateless clients.
     * Client context id is therefore always set to 0.
     */
    long cContextId = 0; // CSIV2 requires type to be long
    /**
     * CSIV2 level 0 implementation does not require any authorization
     * tokens to be sent over the wire. So set cAuthzElem to empty.
     */
    AuthorizationElement[] cAuthzElem = {};
    /* Client identity token to be added to the service context field */
    IdentityToken cIdentityToken = null;
    /* Client authentication token to be added to the service context field */
    byte[] cAuthenticationToken = {};
    /* CDR encoded Security Attribute Service element */
    byte[] cdr_encoded_saselm = {};
    IOR ior = null;
    org.omg.CORBA.Object effective_target = cri.effective_target();
    org.omg.CORBA.portable.ObjectImpl oimpl = (org.omg.CORBA.portable.ObjectImpl) (effective_target);
    DelegateImpl delegate = (DelegateImpl) oimpl._get_delegate();
    ior = delegate.getIOR();
    Profile profile = ior.getProfile();
    SimpleProfileInterface[] components = profile.getComponents();
    SimpleProfileInterface tagged = null;

    for (int i = 0; i < components.length; i++) {
      if (components[i].getTag() == TAG_CSI_SEC_MECH_LIST.value) {
        tagged = components[i];
        break;
      }
    }

    if (tagged == null) {
      return;
    }

    CompoundSecMech mechanism = getCompoundSecurityMechanism(tagged);

    if (mechanism == null) {
      return;
    }

    AS_ContextSec asContext = mechanism.as_context_mech;
    SAS_ContextSec sasContext = mechanism.sas_context_mech;

//    if (!isSet(asContext.target_requires, EstablishTrustInClient.value) &&
//         !isSet(sasContext.target_requires, IdentityAssertion.value)) {
//      return;
//    }

    Subject securityContext = null;
    try {
      securityContext = getCurrentSecurityContext().getSession().getSubject();
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).debugT("ClientInterceptor.send_request(ClientRequestInfo)", LoggerConfigurator.exceptionTrace(e));
      }
    }

    cAuthenticationToken = createAuthenticationToken(securityContext, asContext);

    cIdentityToken = createIdentityToken(securityContext, sasContext, mechanism.transport_mech );

    EstablishContext ec = new EstablishContext(cContextId, cAuthzElem, cIdentityToken, cAuthenticationToken);
    SASContextBody sasctxbody = new SASContextBody();
    sasctxbody.establish_msg(ec);
    /* CDR encode the SASContextBody */
    Any SasAny = orb.create_any();
    SASContextBodyHelper.insert(SasAny, sasctxbody);
    try {
      cdr_encoded_saselm = codec.encode_value(SasAny);
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).debugT("ClientInterceptor.send_request(ClientRequestInfo)", LoggerConfigurator.exceptionTrace(e));
      }
      throw new SecurityException(e.getMessage());
    }
    /* add SAS element to service context list*/
    ServiceContext sc = new ServiceContext();
    sc.context_id = SECURITY_ATTRIBUTE_SERVICE_ID;
    sc.context_data = cdr_encoded_saselm;
    boolean no_replace = false;
    cri.add_request_service_context(sc, no_replace);
  }

  public void destroy() {

  }

  public void send_poll(ClientRequestInfo cri) {

  }

  public void receive_reply(ClientRequestInfo cri) {
    ServiceContext sc = null;
    /**
     * get the service context element from the reply and decode the
     * mesage.
     */

    try {
      sc = cri.get_reply_service_context(SECURITY_ATTRIBUTE_SERVICE_ID);
    } catch (BAD_PARAM bp_ex) {
      if (bp_ex.minor != MinorCodes.INVALID_SERVICE_CONTEXT_ID) {
        throw bp_ex;
      } else {
        return;
      }
    }

    Any a = orb.create_any();
    try {
      a = codec.decode_value(sc.context_data, SASContextBodyHelper.type()); //decode the CDR encoding
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).debugT("ClientInterceptor.receive_reply(ClientRequestInfo)", LoggerConfigurator.exceptionTrace(e));
      }
      throw new SecurityException(e.getMessage());
    }
    SASContextBody sasctxbody = SASContextBodyHelper.extract(a);
    short sasdiscr = sasctxbody.discriminator();

    /**
     * Verify that either a CompleteEstablishContext msg or an
     * ContextError message was received.
     */
    /* Check the discriminator value */
    if ((sasdiscr != MTCompleteEstablishContext.value) && (sasdiscr != MTContextError.value)) {
      throw new SecurityException("Reply message is not one of CompleteEstablishContext or ContextError classes");
    }

    /* Map the error code */
    int st = mapreplyStatus(cri.reply_status());
    setreplyStatus(st, cri.effective_target());
  }

  public void receive_exception(ClientRequestInfo cri) {

  }

  public void receive_other(ClientRequestInfo cri) {

  }

  ////////////////////////////////////////////////////////////////////
  private byte[] createAuthenticationToken(Subject subject, AS_ContextSec asContext) {
    boolean establishTrustInClient = isSet(asContext.target_supports, EstablishTrustInClient.value);
    //boolean anonimous = getCurrentSecurityContext().getSession().getPrincipal().getName().equals("Guest");
    boolean anonimous = ((subject == null) || (getCurrentSecurityContext().getSession().getAuthenticationConfiguration() == null));
    byte[] gsstoken = {};// GSS token
    if (establishTrustInClient && !anonimous) {
      PasswordCredential pwdcred = null;
      try {
        Iterator iterator = subject.getPrivateCredentials(PasswordCredential.class).iterator();
        pwdcred = iterator.hasNext() ? (PasswordCredential) iterator.next() : null;
      } catch (Exception ex) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).debugT("ClientInterceptor.createAuthenticationToken(Subject, AS_ContextSec)", LoggerConfigurator.exceptionTrace(ex));
        }
        pwdcred = null;
      }
      if (pwdcred == null) {
        return gsstoken;
    //        throw new SecurityException("can not establish trust in client, no password credential found");
      }
      GSSUPToken tok = new GSSUPToken(orb, asContext, codec, pwdcred);
      try {
        gsstoken = tok.getGSSToken();
      } catch (Exception e) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).debugT("ClientInterceptor.createAuthenticationToken(Subject, AS_ContextSec)", LoggerConfigurator.exceptionTrace(e));
        }
        throw new SecurityException(e.getMessage());
      }
    }
    return gsstoken;
  }

  protected IdentityToken createIdentityToken(Subject subject, SAS_ContextSec sasContext, SimpleProfileInterface transport) {
    boolean identityAssertion = isSet(sasContext.target_supports, IdentityAssertion.value);
    IdentityToken idtok = new IdentityToken();

    if (identityAssertion) {
      Any any = orb.create_any();
      X509Certificate[] certchain = null;
      try {
        Iterator iterator = subject.getPublicCredentials(X509Certificate[].class).iterator();
        certchain = iterator.hasNext() ? (X509Certificate[]) iterator.next() : null;
      } catch (Exception e) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).debugT("ClientInterceptor.createIdentityToken(Subject, SAS_ContextSec, SimpleProfileInterface)", LoggerConfigurator.exceptionTrace(e));
        }
        certchain = null;
      }

      if (certchain != null) {
        try {
          
          /* create a DER encoding */
          SEQUENCE seq = new SEQUENCE();
          for (int i = 0; i < certchain.length; i++) {
            seq.addComponent(new ASN1(certchain[i].getEncoded()).toASN1Object());	
          }
          byte[] derencoded = DerCoder.encode(seq);
          X509CertificateChainHelper.insert(any, derencoded);
          /* IdentityToken with CDR encoded certificate chain */
          idtok.certificate_chain(codec.encode_value(any));
          return idtok;
        } catch (Exception e) {
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).debugT("ClientInterceptor.createIdentityToken(Subject, SAS_ContextSec, SimpleProfileInterface)", LoggerConfigurator.exceptionTrace(e));
          }
          throw new SecurityException(e.getMessage());
        }
      }

      if ((subject == null) || (getCurrentSecurityContext().getSession().getAuthenticationConfiguration() == null)) {
        idtok.anonymous(true);
        return idtok;
      }
      Principal principal = null;
      try {
        principal = (Principal) subject.getPrincipals(com.sap.engine.lib.security.Principal.class).iterator().next();
      } catch (Exception e) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).debugT("ClientInterceptor.createIdentityToken(Subject, SAS_ContextSec, SimpleProfileInterface)", LoggerConfigurator.exceptionTrace(e));
        }
        principal = null;
      }

      if (principal != null) {
        /* create a DER encoding */
        GSSUPName gssname = new GSSUPName(principal.getName(), DEFAULT_REALM);
        byte[] expname = gssname.getExportedName();
        GSS_NT_ExportedNameHelper.insert(any, expname);
        /* IdentityToken with CDR encoded GSSUPName */
        try {
          idtok.principal_name(codec.encode_value(any));
        } catch (Exception e) {
          if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug()) {
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).debugT("ClientInterceptor.createIdentityToken(Subject, SAS_ContextSec, SimpleProfileInterface)", LoggerConfigurator.exceptionTrace(e));
          }
          throw new SecurityException(e.getMessage());
        }
      }
    } else {
      idtok.absent(true);
    }

    return idtok;
  }

  ////////////////////////////////////////////////////////////////////
  /**
   * Map the reply status code to a format suitable for J2EE RI.
   *
   * @param  repst  reply status from the service context field.
   * @return        mapped status code
   *
   */
  private int mapreplyStatus(int repst) {
    int status;

    switch (repst) {
      case SUCCESSFUL.value:
      case USER_EXCEPTION.value: {
        status = STATUS_PASSED;
        break;
      }
      case LOCATION_FORWARD.value:
      case TRANSPORT_RETRY.value: {
        status = STATUS_RETRY;
        break;
      }
      case SYSTEM_EXCEPTION.value: {
        status = STATUS_FAILED;
        break;
      }
      default: {
        status = repst;
        /**
         * There is currently no mapping defined for any other status
         * codes. So map this is to a STATUS_FAILED.
         */
        break;
      }
    }

    return status;
  }

  /**
   * set the reply status
   */
  private void setreplyStatus(int status, org.omg.CORBA.Object target) {
    if (status == STATUS_FAILED) {
      throw new SecurityException("Target did not accept security context");
    } else if (status == STATUS_RETRY) {
      //babe, give it once again
    } else {
      //feel all right now
    }
  }

  private CompoundSecMech getCompoundSecurityMechanism(SimpleProfileInterface tagged) {
    byte[] taggedData = tagged.getData();
    CORBAInputStream stream = new CORBAInputStream(orb, taggedData);
    CompoundSecMech[] list = CompoundSecMechListHelper.read(stream).mechanism_list;

    if (list == null || list.length == 0) {
      return null;
    }

    return list[0];
  }

  private boolean isSet(int val1, int val2) {
    return ((val1 & val2) == val2);
  }

  protected abstract SecurityContextObject getCurrentSecurityContext();
}

