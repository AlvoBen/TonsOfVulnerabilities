package com.sap.engine.services.iiop.csiv2.interceptors;

import java.util.Hashtable;
import javax.security.auth.*;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.security.auth.callback.CallbackHandler;
import javax.resource.spi.security.PasswordCredential;
/* Import classes required for DER encoding and decoding */
import iaik.asn1.DerCoder;
import iaik.asn1.ASN1Object;
import iaik.asn1.ObjectID;
import iaik.asn1.structures.Name;

import java.security.cert.X509Certificate;
import org.omg.CORBA.*;
import org.omg.CORBA.LocalObject;
import org.omg.PortableInterceptor.*;
import org.omg.IOP.*;
import com.sap.engine.lib.security.CSI.*;
import com.sap.engine.lib.security.BasicPasswordCallbackHandler;
import com.sap.engine.services.iiop.csiv2.CSI.*;
import com.sap.engine.services.iiop.csiv2.GSSUP.*;
import com.sap.engine.services.iiop.server.CorbaServiceFrame;
import com.sap.engine.services.iiop.internal.interceptors.MinorCodes;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;

public class SecurityServerRequestInterceptor extends LocalObject implements ServerRequestInterceptor {

  private LoginContext  loginContext = null;   //$JL-SER$
  private transient ORB orb = null;
  private Codec codec = null;
  private static final int SECURITY_ATTRIBUTE_SERVICE_ID = 15;
  // the major and minor codes for a invalid mechanism
  private static final int INVALID_MECHANISM_MAJOR = 2;
  private static final int INVALID_MECHANISM_MINOR = 1;
  /* used when inserting into service context field */
  private static final boolean NO_REPLACE = false;
  /**
   * Define minor codes for errors specified in section 4.5,
   * "ContextError Values and Exceptions"
   *
   * Currently only MessageInContextMinor code is defined since this
   * is the only used by the security interceptors.
   */
  private static final int MessageInContextMinor = 4;
  public static final int STATUS_PASSED = 0;
  public static final int STATUS_FAILED = 1;
  public static final int STATUS_RETRY = 2;

  private Hashtable threadcontext = new Hashtable();

  public SecurityServerRequestInterceptor(ORB orb, Codec codec) {
    this.orb   = orb;
    this.codec = codec;
  }

  public String name() {
    return "security_server";
  }

  public void destroy() {

  }

  public void receive_request_service_contexts(ServerRequestInfo sri) {

  }

  public void receive_request(ServerRequestInfo ri) {
    ServiceContext sc = null; // service context
    int status = 0;
    //boolean raise_no_perm = false;
    /**
     *  An unprotected invocation will not contain anything in the serivce
     *  context field for the security context. This will therefore generate
     *  the exception org.omg.CORBA.BAD_PARAM with a minor error code  15
     *
     *  ISSUE: The minor code should probably be checked.
     */
    try {
      sc = ri.get_request_service_context(SECURITY_ATTRIBUTE_SERVICE_ID);
    } catch (org.omg.CORBA.BAD_PARAM e) {
      if (e.minor != MinorCodes.INVALID_SERVICE_CONTEXT_ID) {
        SASContextBody sasctxbody = createContextError(INVALID_MECHANISM_MAJOR, INVALID_MECHANISM_MINOR);
        sc = createSvcContext(sasctxbody);
        threadcontext.put(Thread.currentThread(), sc);
        throw new NO_PERMISSION(e.toString());
      } else {
        return;
      }
    }

    /* Decode the service context field */
    Any SasAny = orb.create_any();
    try {
      SasAny = codec.decode_value(sc.context_data, SASContextBodyHelper.type());
    } catch (Exception e) {
      throw new SecurityException("CDR Decoding error for SAS context element");
    }
    SASContextBody sasctxbody = SASContextBodyHelper.extract(SasAny);
    short sasdiscr = sasctxbody.discriminator();

    /* Check message type received */
    /**
     *  CSIV2 SPEC NOTE:
     *
     *  Section 4.3 "TSS State Machine" , table 4-4 "TSS State Table"
     *  shows that a MessageInContext can be received. In this case
     *  the table is somewhat unclear. But in this case a ContextError
     *  with the status code "No Context" ( specified in
     *  section 4.5 "ContextError Values and Exceptions" must be sent back.
     *  A NO_PERMISSION exception must also be raised.
     *
     *  ISSUE: should setSecurityContext(null) be called ?
     */
    if (sasdiscr == MTMessageInContext.value) {
      sasctxbody = createContextError(MessageInContextMinor);
      sc = createSvcContext(sasctxbody);
      threadcontext.put(Thread.currentThread(), sc);
      throw new NO_PERMISSION();
    }

    /**
     * CSIV2 SPEC NOTE:
     *
     * CSIV2 spec does not specify the actions for any message other than
     * a MessageInContext and EstablishContext message. So for such messages,
     * this implementation simply drops the message on the floor. No
     * other message is sent back. Neither is an exception raised.
     *
     * ISSUE: Should there be some other action ?
     */
    if (sasdiscr != MTEstablishContext.value) {
      throw new SecurityException("Received message is not an EstablishContext message");
    }

    EstablishContext ec = sasctxbody.establish_msg();
    //Subject subject = null;
    boolean authenticated = false;
    try {
      if (ec.client_authentication_token.length != 0) {
        loginAuthenticationCredential(ec.client_authentication_token);
        authenticated = true;
      }
    } catch (Exception e) {
      throw new SecurityException("Error while creating a JAAS subject credential");
    }
    try {
      if (!authenticated) {
        if (ec.identity_token != null) {
          loginIdentityCredential(ec.identity_token);
        }
      }
    } catch (SecurityException secex) {
      sasctxbody = createContextError(INVALID_MECHANISM_MAJOR, INVALID_MECHANISM_MINOR);
      sc = createSvcContext(sasctxbody);
      threadcontext.put(Thread.currentThread(), sc);
      throw new NO_PERMISSION(secex.toString());
    } catch (Exception e) {
      sasctxbody = createContextError(INVALID_MECHANISM_MAJOR, INVALID_MECHANISM_MINOR);
      sc = createSvcContext(sasctxbody);
      threadcontext.put(Thread.currentThread(), sc);
      throw new NO_PERMISSION(e.toString());
    }

    /**
     * CSIV2 SPEC NOTE:
     *
     * If ec.client_context_id is non zero, then this is a stateful
     * request. As specified in section 4.2.1, a stateless server must
     * attempt to validate the security tokens in the security context
     * field. If validation succeeds then CompleteEstablishContext message
     * is sent back. If validation fails, a ContextError must be sent back.
     */
    if (status == STATUS_FAILED) {
      sasctxbody = createContextError(status);
      sc = createSvcContext(sasctxbody);
      threadcontext.put(Thread.currentThread(), sc);
      throw new NO_PERMISSION();
    }

    sasctxbody = createCompleteEstablishContext(status);
    sc = createSvcContext(sasctxbody);
    threadcontext.put(Thread.currentThread(), sc);
  }

  public void send_reply(ServerRequestInfo sri) {
    ServiceContext sc = (ServiceContext) threadcontext.remove(Thread.currentThread());

    if (sc == null) {
      return;
    }

    sri.add_reply_service_context(sc, NO_REPLACE);
    try {
      if (loginContext != null) {
        loginContext.logout();
      }
    } catch (LoginException l_ex) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).debugT("SecurityServerRequestInterceptor.send_reply(ServerRequestInfo)", LoggerConfigurator.exceptionTrace(l_ex));
      }
      return;
    }
  }

  public void send_exception(ServerRequestInfo sri) {
    ServiceContext sc = (ServiceContext) threadcontext.remove(Thread.currentThread());

    if (sc == null) {
      return;
    }

    sri.add_reply_service_context(sc, NO_REPLACE);
    try {
      if (loginContext != null) {
        loginContext.logout();
      }
    } catch (LoginException l_ex) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).debugT("SecurityServerRequestInterceptor.send_exception(ServerRequestInfo)", LoggerConfigurator.exceptionTrace(l_ex));
      }
      return;
    }
  }

  public void send_other(ServerRequestInfo sri) {
    try {
      if (loginContext != null) {
        loginContext.logout();
      }
    } catch (LoginException l_ex) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).debugT("SecurityServerRequestInterceptor.send_other(ServerRequestInfo)", LoggerConfigurator.exceptionTrace(l_ex));
      }
      return;
    }
  }

  ///////////////////////////////////////////////////////////////
  /**
   *  CDR encode a SAS Context body and then construct a service context
   *  element.
   */
  private ServiceContext createSvcContext(SASContextBody sasctxtbody) {
    ServiceContext sc = null;
    Any a = orb.create_any();
    SASContextBodyHelper.insert(a, sasctxtbody);
    byte[] cdr_encoded_saselm = {};
    try {
      cdr_encoded_saselm = codec.encode_value(a);
    } catch (Exception e) {
      cdr_encoded_saselm = null;
    }
    sc = new ServiceContext();
    sc.context_id = SECURITY_ATTRIBUTE_SERVICE_ID;
    sc.context_data = cdr_encoded_saselm;
    return sc;
  }

  /**
   * Create a ContextError message. This is currently designed to work only
   * for the GSSUP mechanism.
   */
  /* Create a ContexError Message */
  private SASContextBody createContextError(int status) {
    /**
     * CSIV2 SPEC NOTE:
     *
     * Check that CSIV2 spec does not require an error token to be sent
     * for the GSSUP mechanism.
     */
    byte error_token[] = {};
    ContextError ce = new ContextError(0, 
    /* stateless client id */
    1, 
    /* major status is always 1*/
    status, 
    /* minor status */
    error_token);
    SASContextBody sasctxtbody = new SASContextBody();
    sasctxtbody.error_msg(ce);
    return sasctxtbody;
  }

  /* create a context error with the specified major and minor status
   */
  private SASContextBody createContextError(int major, int minor) {
    byte error_token[] = {};
    ContextError ce = new ContextError(0, 
    /* stateless client id */
    major, // major
    minor, // minor
    error_token);
    SASContextBody sasctxtbody = new SASContextBody();
    sasctxtbody.error_msg(ce);
    return sasctxtbody;
  }

  /**
   * Create a CompleteEstablishContext Message. This currently works only
   * for the GSSUP mechanism.
   */
  private SASContextBody createCompleteEstablishContext(int status) {
    /**
     * CSIV2 SPEC NOTE:
     *
     * Check CSIV2 spec to make sure that there is no
     * final_context_token for GSSUP mechanism
     */
    byte[] final_context_token = {};
    CompleteEstablishContext cec = new CompleteEstablishContext(0, // stateless client id
    false, // for stateless 
    final_context_token);
    SASContextBody sasctxtbody = new SASContextBody();
    sasctxtbody.complete_msg(cec);
    return sasctxtbody;
  }

  ///////////////////////////////////////////////////////////////////
  private void loginAuthenticationCredential(byte[] token) throws LoginException {
    GSSUPToken gsstoken = new GSSUPToken(orb, codec, token);
    PasswordCredential passCredential = gsstoken.getPwdcred();
    CallbackHandler callbackHandler = new BasicPasswordCallbackHandler(passCredential.getUserName() , new String(passCredential.getPassword()));
    loginContext = CorbaServiceFrame.getSecurityBasicContext().getAuthenticationContext().getLoginContext(new Subject(), callbackHandler);
    loginContext.login();
  }

  private void loginIdentityCredential(IdentityToken idtok) throws Exception {
    byte[] derenc; // used to hold DER encodings
    Any any; // Any object returned from codec.decode_value()
    CallbackHandler callbackHandler = null;
    switch (idtok.discriminator()) {
      case ITTAbsent.value:
      case ITTAnonymous.value: {
        return;  //TODO
      }
      case ITTDistinguishedName.value: {
        any = codec.decode_value(idtok.dn(), X501DistinguishedNameHelper.type());
        /* Extract CDR encoding */
        derenc = X501DistinguishedNameHelper.extract(any);
        Name name = new Name(derenc);
        callbackHandler = new CSICallbackHandler(new CSIDistinguishedName(name.getRDN(ObjectID.commonName), name));
        loginContext = CorbaServiceFrame.getSecurityIIOPContext().getAuthenticationContext().getLoginContext(new Subject(), callbackHandler);
        break;
      }
      case ITTX509CertChain.value: {
        /* Decode CDR encoding */
        any = codec.decode_value(idtok.certificate_chain(), X509CertificateChainHelper.type());
        /* Extract DER encoding */
        derenc = X509CertificateChainHelper.extract(any);
        ASN1Object seq = DerCoder.decode(derenc);
        /**
         * Size specified for getSequence() is 1 and is just
         * used as a guess by the method getSequence().
         */
        X509Certificate[] certchain = new X509Certificate[seq.countComponents()];

        /**
         * X509Certificate does not have a constructor which can
         * be used to instantiate objects from DER encodings. So
         * use X509CertImpl extends X509Cerificate and also implements
         * DerEncoder interface.
         */
        for (int i = 0; i < certchain.length; i++) {
            certchain[i] = new iaik.x509.X509Certificate();
            ((iaik.x509.X509Certificate) certchain[i]).decode(seq.getComponentAt(i));
        } 

        callbackHandler = new CSICallbackHandler(new CSICertificateChain(certchain));
        loginContext = CorbaServiceFrame.getSecurityIIOPContext().getAuthenticationContext().getLoginContext(new Subject(), callbackHandler);
        break;
      }
      case ITTPrincipalName.value: {
        /* Decode CDR encoding */
        any = codec.decode_value(idtok.principal_name(), GSS_NT_ExportedNameHelper.type());
        byte[] expname = GSS_NT_ExportedNameHelper.extract(any);

        if (!GSSUtils.verifyMechOID(GSSUtils.GSSUP_MECH_OID, expname)) {
          throw new SecurityException("Unknown identity assertion type");
        }

        GSSUPName gssname = new GSSUPName(expname);
        callbackHandler = new CSICallbackHandler(new CSIPrincipalName(gssname.getUser()));
        loginContext = CorbaServiceFrame.getSecurityIIOPContext().getAuthenticationContext().getLoginContext(new Subject(), callbackHandler);        //
        break;
      }
      default: {
        return;
      }
    }
    loginContext.login();
  }

}

