/*****************************************************************************
 * Project:      SAP Logon TicketVerifier
 *
 * Title:        TicketVerifier
 * Description:  Interface for a TicketVerifier
 * Copyright:    Copyright (c) 2001
 * Company:      SAP AG
 *
 * @author
 *
 ****************************************************************************/

package com.sap.security.api.ticket;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;


/**
 *  <p>This abstract base class supplies an interface to handle and verify
 *     SAP Logon Tickets in standalone Java applications that do not use
 *     the UME or Enterprise portal integrated authentication services.
 *     Within SAP AS Java there is usually no need to use this class.
 *  </p>
 * 
 *  <p><b>DISCLAIMER:</b>
 *  </p>
 *  
 *  <p>This class requires the "IAIK Java Cryptography Extension (IAIK-JCE)"
 *     by "Institute for Applied Information Processing and Communication"
 *     (IAIK) of the Technical University Graz.
 *  </p>
 *  <p><b>The version delivered by SAP AG is RESTRICTED to usage inside of
 *     SAP software. If you plan to use this class in standalone applications
 *     outside of SAP software, you MUST make sure to have a respective
 *     license by IAIK. For details see
 *     <a href="http://jce.iaik.tugraz.at/products/01_jce/index.php">
 *       http://jce.iaik.tugraz.at/products/01_jce/index.php
 *     </a>.</b>
 *  </p>
 * 
 *  <p>In order to use it, you need the following:
 *  </p>
 *  <ul>
 *    <li>an SAP logon ticket to verify
 *    <li>a valid sapum.properties for parametrization of the UMFactory
 *    <li>one or more trusted certificates (either in a keystore or
 *        as files in the file system). Please check the java tools.
 *        E.g. with the keytool executable (located in <JAVA HOME>/bin)
 *        you can create a keystore and import trusted certificates
 *        into it.
 *  </ul>
 *  <p>
 *  Here's the minimum set of properties from the sapum.properties file:
 *  <pre>
 *  ##################
 *  # configuration 
 *  ##################
 *  
 *  ##use the internal ume trace
 *  ume.trace.internal_trace=false
 * 
 *  ###################################################
 *  # new parameter. Valid as of SAP NetWeaver 2004 SP8
 *  ###################################################
 *  login.ticket_standalone=true
 *  
 *  ###############################################################################
 *  #    security parameters
 *  ###############################################################################
 *  # path of your IAIK keystore
 *  login.ticket_keystore = ticketKeyStore
 *  login.ticket_keystore_pw=&lt;your keystore password&gt;
 *  
 *  # Initialize Factory
 *  logonAuthenticationFactory  =empty
 *  authenticationFactory       =empty
 *  userMapping                 =empty
 *  groupFactory                =empty
 *  roleFactory                 =empty
 *  userAccountFactory          =empty
 *  objectFactory               =empty
 *  principalFactory            =empty
 *  userFactory                 =empty
 *  serviceUserFactory          =empty
 *  ume.acl.manager             =empty
 *  </pre>
 *  
 *  Here's a small code snippet that demonstrates how to use this class:
 *      <pre>
 *      // Make sure the IAIK provider is ready
 *      IAIK.addAsProvider ();
 *      // Get a ticketverifier object
 *      // see information to {@link com.sap.security.api.UMFactory#getInstance()} on about how to currectly
 *      // configure the UMFactory singleton
 *      TicketVerifier tv = UMFactory.getInstance ().getTicketVerifier ();
 *      // read ticket (this function is only a place holder...)
 *      // get the ticket from the http request in a servlet or something like
 *      // this. make sure it is unescaped (replace %XX sequences by the
 *      // corresponding ASCII character)
 *      String ticket = getTicketAndUnescapeIt ();
 *      // set ticket
 *      tv.setTicket (ticket);
 *      // use call tv.setCertificates () if you don't want
 *      // to trust certificates in the keystore. In this
 *      // case, you have to provide a list of certificate objects.
 *      
 *      // Get R/3 user
 *      String user     = tv.getUser ();
 *      // Get issuer
 *      String issuer   = tv.getSystemID ();
 *      // Get client
 *      String client   = tv.getSystemClient ();
 *      String portal_user=null;
 *      String issue_instant;
 *      int    iValHours=0;
 *      int    iValMin  =0;
 *      
 *      // Get portal user
 *      InfoUnit iu     = tv.getInfoUnit (0x20);
 *      
 *      if (iu!=null) {
 *          // portal user is in UTF8 encoding
 *          portal_user = iu.getString ("UTF8");
 *          if (0!=portal_user.indexOf ("portal:"))
 *              System.out.println ("Invalid info unit.");
 *          else {
 *              portal_user = portal_user.substring (7);
 *          }
 *      }
 *      
 *      // Get validity stuff
 *      iu = tv.getInfoUnit (InfoUnit.ID_CREATE_TIME);
 *      if (iu==null) {
 *          // This can really be handled as a serious error
 *          throw new IllegalStateException ("Ticket doesn't contain a time stamp!!");
 *      }
 *      issue_instant = iu.getString (tv.getCodepage());
 *      
 *      // Get validity
 *      iu = tv.getInfoUnit (InfoUnit.ID_VALID_TIME);
 *      if (iu!=null)
 *          iValHours = iu.getInt();
 *
 *      // get minute validity ...
 *      iu = tv.getInfoUnit (InfoUnit.ID_VALID_TIME_MIN);
 *      // ... which might not be there!
 *      if (iu!=null)
 *          iValMin = iu.getInt();
 *
 *      System.out.println ("Ticket issued for R/3 user:\t" + user);
 *      System.out.println ("       issued by:\t\t" + issuer + " (" + client + ")");
 *      if (portal_user!=null)
 *          System.out.println("       issued for portal user:\t" + portal_user);
 *      System.out.println ("       issued at:\t\t" + issue_instant);
 *      System.out.println ("  validity period ([H..]H:MM):\t"
 *          + iValHours + (iValMin/10==0?":0":":") + iValMin);
 *    </pre>
 *
 **/
public abstract class TicketVerifier
{
    protected static final int STATE_START       = 0;
    protected static final int STATE_INITIALIZED = 1;
    protected static final int STATE_VERIFIED    = 2;
    
    /**
     *  id for IAIK keystore.
     *  In order to get this you need to install the IAIK cryptography provider.
     */ 
    public static final int KEYTYPE_IKS = 0;  // IAIK keystore
    
    /**
     *  id for the standard SUN JKS keystore.
     */ 
    public static final int KEYTYPE_SUN = 2;  // SUN default keystore
    
    /**
     *  Currently not used.
     */ 
    public static final int KEYTYPE_PSE = 3;  // SECUDE personal security environment
    
    /**
     *  id for a P12 file storing certificates and keys.
     *  Currently not used.
     */    
    public static final int KEYTYPE_P12 = 1;  // PKCS#12 file
    
    /**
     *  id for a DER encoded certificate.
     *  Currently not used.
     */ 
    public static final int KEYTYPE_DER = 4;  // DER encoded X.509 certificate
    
    /**
     *  id for a pkcs\#7 encoded list of certificates.
     *  Currently not used.
     */
    public static final int KEYTYPE_P7C = 5;  // List of X509v3 certificates

    /** State of the Ticket.
     *  <p>
     *  The TicketVerifier has three possible states:
     *  <ul>
     *  <li>It starts in <code>STATE_START</code>
     *  <li>After setting a new Ticket with {@link #setTicket(byte[])} or
     *      {@link #setTicket(String)},
     *      the state changes to <code>STATE_INITIALIZED</code>
     *  <li>If the current Ticket has been successfully verified, the state is
     *      <code>STATE_VERIFIED</code>.
     *  </ul>
     **/
    protected int state = STATE_START;
    
    private boolean _enforceVerify = true;

    //---------------------------------------------------------------------
    //--- init Ticket -----------------------------------------------------

    /** Initialize the Ticket with a base64 encoded String.
     *  @param base64string Ticket string. Can be retrieved from
     *  a servlet request, for instance.
     *  @exception Exception in case of a parsing error.
     */
    public abstract void setTicket(String base64string)
        throws Exception;


    /**
     *  Set the raw ticket.
     *  @param ticket is the ticket after applying the base64 decode
     *  @exception Exception in case a parsing error occurs.
     */
    public abstract void setTicket(byte[] ticket)
        throws Exception;
        
    /**
     *  Gets the SAP codepage used within this ticket.
     *  @return SAP codepage used for content of type CHAR (see <a href="InfoUnit.html#id_table">
     *  type of content</a> and <a href="InfoUnit.html#codepages_table">codepages</a>)
     */ 
    public abstract String getCodepage ();

    /**
     *  Returns the ticket string this object has been fed with.
     *  @return base64 encoded ticket string.
     *  @exception TicketException thrown in case the state is not at least
     *  initialized (by a call to {@link #setTicket(String)}, for instance).
     *             
     */ 
    public abstract String getTicket () throws TicketException;

    //---------------------------------------------------------------------
    //--- access to attributes --------------------------------------------

    /** Get the name of the User.
     *  @return user name of the R/3 user name in the ticket
     *  @throws TicketException in one of the two cases:
     *  <ul>
     *    <li>The ticket not initialized (e.g. {@link #setTicket(String)} has
     *        not been called before).
     *    <li>The state is initialized but not verified (no or no
     *        successful call to {@link #verify()}) <b>and</b> {@link #setEnforceVerify(boolean)}
     *        has been called before (this requires successful verification prior to
     *        this call)
     *  </ul>
     *  Note that the second bullet is true when you call <code>t.setEnforceVerify(true);</code>
     *  and <code>t.verify()</code> throws an exception.
     **/
    public abstract String getUser()
        throws TicketException;


    /** Get the ID of the Ticket-issuing System.
     *  @return the system id of the issuing system as a string. If the ticket was issued by
     *  a UME, this will be the value of the parameter <code>login.ticket_issuer</code>.
     *  @throws TicketException - as in {@link #getTicket()}.
     **/
    public abstract String getSystemID()
        throws TicketException;


    /** Get the client of the Ticket-issuing System.
     *  @return the client of the issuing system as a string. If the ticket was issued by
     *  a UME, this will be the value of the parameter <code>login.ticket_client</code>.
     *  @throws TicketException - as in {@link #getTicket()}.
     **/
    public abstract String getSystemClient()
        throws TicketException;


    /** Get the content of the InfoUnit <code>id</code>
     *  (or null if no such Unit exists). A list of possible ids is
     *  available at {@link InfoUnit}
     *  @return the info unit identified by <code>id</code> or <code>null</code>
     *  if this info unit does not exist in the ticket.
     *  @throws TicketException - as in {@link #getTicket()}.
     **/
    public abstract InfoUnit getInfoUnit(int id)
        throws TicketException;


    /** Get an Enumeration of all (unidentified) InfoUnits.
     *  @return Enumeration of all info units within the ticket.
     *  @throws TicketException - as in {@link #getTicket()}.
     **/
    public abstract Enumeration getInfoUnits()
        throws TicketException;


    /**
     *  Returns a string representation of this ticket.
     *  @return a string representation
     */ 
    public String toString()
    {
        StringBuffer s = new StringBuffer("Ticket ");

        if (state == STATE_START) {
            s.append("[not initialized]\n");
        }
        else if (state == STATE_INITIALIZED){
            s.append("[initialized]\n");
        }
        else if (state == STATE_VERIFIED) {
            s.append("[verified]\n");
            try {
                s.append("  User = " + getUser() + "\n");
                s.append("  Issuing System ID     = " + getSystemID() + "\n");
                s.append("  Issuing System Client = " + getSystemClient() + "\n");

                // print other InfoUnits
                InfoUnit unit;
                Enumeration e = getInfoUnits();

                while(e.hasMoreElements()) {
                    unit = (InfoUnit)e.nextElement();
                    s.append("InfoUnit " + unit.getID() + ", length=" + unit.getContent().length);
                }
            }
            catch(TicketException e) {
//                $JL-EXC$
            }
        }
        else {
            s.append("[???]\n");
        }

        return s.toString();
    }
    
    /**
     *  Set a list of X.509 certificates as trusted ticket issuers.
     *  @param certs list of certificates that is trusted for the verification. For
     *  an example how to get such a list, see <a href="#read_certs_from_fs">
     *  the example</a>.
     */ 
    public abstract void setCertificates(java.security.cert.X509Certificate[] certs);

    /** Set the Certificates used to verify the Signatures.
     *  This method loads all Certificates from a KeyStore.
     *
     *  @param keyStoreName The Name of the KeyStore file.
     *  @param pass The Password used to access the Keystore.
     **/
    public abstract void setCertificates(String keyStoreName, char [] pass)
        throws Exception;

    /** Get the Certificate used to verify the Signature.
     *  @return SignerCertificate or null.
     *  @exception TicketException - same as in {@link #getTicket()}.
     *  @deprecated This method will be removed in the next release. Use
     *  {@link #getSignerCert()} instead.
     **/
    public abstract iaik.x509.X509Certificate getSignerCertificate()
        throws TicketException;

    /** Get the Certificate used to verify the Signature.
     *  @return SignerCertificate or null.
     *  @throws TicketException - same as in {@link #getTicket()}.
     **/
    public abstract X509Certificate getSignerCert()
        throws TicketException;


    /** 
     *  For internal use only. 
     */
    protected java.security.cert.X509Certificate[] getCertsFromKeyStore(KeyStore store)
        throws KeyStoreException
    {
        Vector certs = new Vector();

        Enumeration enumeration = store.aliases();
        String alias;

        while(enumeration.hasMoreElements()) {
            alias = (String) enumeration.nextElement();
            if( store.isCertificateEntry(alias) ) {
                certs.add(store.getCertificate(alias));
            }
            if (store.isKeyEntry(alias)) {
                java.security.cert.Certificate [] certs__ = store.getCertificateChain(alias);
                int i;
                for (i=0; i<certs__.length; i++) {
                    certs.add(certs__[i]);
                }
            }
        }

        return (java.security.cert.X509Certificate[]) certs.toArray(new java.security.cert.X509Certificate[0]);
    }
    
    /** Test if Ticket is valid
     *  This method verifies the ticket (using the
     *  certificates supplied by {@link #setCertificates(String, char[])} or
     *  {@link #setCertificates(java.security.cert.X509Certificate[])}) and checks
     *  whether it is expired or not. To get more specific information in the case of failure,
     *  call {@link #verify}.
     *
     *  @return <code>true</code> if all checks are ok.
     *  @see #verify
     **/
    public boolean isValid() {
        try {
            verify();
        } catch(Exception e) {
            //$JL-EXC$
            return false;
        }

        return true;
    }



    /** Verify the ticket.
     *  This function performs a cryptographic cerification of the ticket signature
     *  and checks whether the ticket is expired or not.
     *  If the verify is successful, this method sets <code>state = STATE_VERIFIED</code>.
     *  @exception NoSuchAlgorithmException
     *  @exception NoSuchProviderException
     *                                      Improper Provider configuration. All used
     *                                      algorithms (per default SHA1 and DSA) need
     *                                      to be available.
     *  @exception SignatureException       A problem with the signature
     *  @exception InvalidKeyException      The keys are not ok, wrong algorithm, for instance.
     *  @exception TicketException          can be caused by various errors.
     *  @exception CertificateNotYetValid
     *  @exception CertificateExpiredException Only there for backward compatibility reasons.
     */
    public abstract void verify()
        throws CertificateException,        NoSuchAlgorithmException,
               InvalidKeyException,         NoSuchProviderException,
               SignatureException,          CertificateExpiredException,
               CertificateNotYetValidException,
               TicketException,
               Exception;    


    /** If set to <code>true</code>, the Ticket MUST be verfied before
     *  the attributes User, System and InfoUnits can be accessed
     *  (default=<code>true</code>).
     *
     **/
    public void setEnforceVerify (boolean val)
    {
        _enforceVerify = val;
    }

    
    public boolean isEnforceVerify ()
    {
        return _enforceVerify;
    }

    //---------------------------------------------------------------------
    //--- utility methods -------------------------------------------------

    /** 
     *  Utility method. 
     *  @deprecated This method will be removed in the next release. Use
     *  {@link #findCertificates(java.security.cert.X509Certificate[], String, BigInteger)}
     *  as replacement.
     */
    public static X509Certificate findCertificate(X509Certificate[] certs, String issuer, BigInteger serial)
    {
        if (certs == null) { return null; }

        for (int i=0; i<certs.length; i++) {

            if (certs[i].getIssuerDN().getName().equals(issuer)
                    && certs[i].getSerialNumber().equals(serial)) {
                return certs[i];
            }
        }

        return null;
    }
    
    /** 
     *  Utility method.
     */
    public static java.security.cert.X509Certificate[] findCertificates(java.security.cert.X509Certificate[] certificates, String issuer, BigInteger serial)
    {
      if (certificates == null || certificates.length == 0) { 
        return null; 
      }

      ArrayList certificateList = new ArrayList();
      for (int i = 0; i < certificates.length; i++) {  
        java.security.cert.X509Certificate certificate = certificates[i];
        	if (certificate.getIssuerDN().getName().equals(issuer) && certificate.getSerialNumber().equals(serial)) {
        	  certificateList.add(certificate);
        	}
      }
        
      if (certificateList.size() == 0) {
        return null;
      }

      java.security.cert.X509Certificate[] matchedCertificates = new java.security.cert.X509Certificate[certificateList.size()];
      certificateList.toArray(matchedCertificates);
      return matchedCertificates;
    }

    /** 
     * Utility method to verifiy a certificate.
     * 
     * @param certs Array of trusted certificates.
     * @param test Certificate to be verified.
     * @param verifyChain If this parameter is <code>true</code> the method
     *        continues verifing until a self-signed Certificate is found
     *        as root.
     * @return <code>true</code> if verification is successful.
     * @deprecated  The method is not used any more. The chain of the ticket signing certificate must not be verified. 
    */
    public static boolean verifyCertificate (X509Certificate[] certs, X509Certificate test, boolean verifyChain)
    {
        // test if certificate is self signed...
        try {
            test.verify(test.getPublicKey());
            test.checkValidity();

            //... and if member of the trusted cert list...
            for (int i=0; i<certs.length; i++) {
                if (test.equals(certs[i])) { return true; }
            }

        }
        catch (Exception e) {
//          $JL-EXC$
            // verify failed... continue
        }

        // ...not self signed --> search in list
        if (certs != null) {
            for (int i=0; i<certs.length; i++) { 
                if (certs[i].getSubjectDN().equals(test.getIssuerDN())) {
                    try {
                        test.verify(certs[i].getPublicKey());
                        test.checkValidity();

                        if (verifyChain) {
                            if (verifyCertificate(certs, certs[i], true)) { return true; }
                        }
                        else {
                            return true;
                        }
                    }
                    catch (Exception e) {
//                      $JL-EXC$
                        // (verify failed: continue...)
                    }

                }//end if
            }//end for
        }//end if

        return false;
    }

}
