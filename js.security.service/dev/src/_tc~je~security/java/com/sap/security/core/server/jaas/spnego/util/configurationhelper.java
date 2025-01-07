package com.sap.security.core.server.jaas.spnego.util;

import iaik.asn1.ObjectID;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;


import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;

import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.security.core.server.jaas.spnego.IConstants;
import com.sap.tc.logging.Location;

/**
 * Helper for configuration parameters for the SPNegoLoginModule. In addition,
 * this class caches the credentials once acquired for the server.
 */
public class ConfigurationHelper {
  private static Set SUPPORTED_PARAMS = new HashSet();

  private static Map credentialsCache = new HashMap();
  private static volatile int oldHash = -1;
  private static Location location = Location.getLocation(AuthenticationTraces.LOGIN_MODULES_SPNEGO_LOCATION);
  private Properties properties = null;
  private ObjectID[] supportedMechanisms = null;

  static {
    try {
      // Adds all fields starting with "CONF_" of IConstants to
      // the supported params
      Field[] fields = IConstants.class.getDeclaredFields();
      for (int i = 0; i < fields.length; i++) {
        if (fields[i].getName().startsWith("CONF_")) {
          SUPPORTED_PARAMS.add(fields[i].get(null));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Determines whether the given properties object is different from the one
   * that have been processed by this class.
   * 
   * @param p
   *          Properties object
   * @return true if yes, false if not
   */
  public static boolean havePropsChanged(Properties properties) {
    return oldHash != properties.hashCode();
  }

  /**
   * Instantiates a ConfigurationHelper object from the SPNegoLoginModule
   * options. If the SPNegoLoginModule options contain
   * {@link com.sap.security.core.server.jaas.spnego.IConstants#CONF_CREDS_IN_THREAD}
   * ==true then the credentials acquisition will take place in another thread.
   * This is to avoid problems with the SUN where the credential acquisition
   * itself requires a processing of a login module stack. This attaches the
   * credentials of the J2EE Engine's Kerberos principal to the thread and then
   * this user is logged on instead of the user sitting in front of the browser.
   * 
   * @param props
   *          Properties
   */
  public ConfigurationHelper(Properties properties) throws IllegalArgumentException, GSSException {
    this.properties = properties;
    this.loadSupportedMechs();
    this.verifyProps();
    // TODO add check for gssNames format
  }

  public String[] getConfiguredPrincipals() {
    String option = properties.getProperty(IConstants.CONF_GSS_NAME);
    String[] principals = null;
    if (option != null) {
      principals = Utils.stringToArray(option);
    }
    return principals;
  }

  public String[] getConfiguredRealms() {
    String[] principals = getConfiguredPrincipals();
    String[] realms = null;
    if (principals != null) {
      realms = new String[principals.length];
      for (int i = 0; i < principals.length; i++) {
        String principal = principals[i];
        String realm = Utils.getKPNSuffix(principal);
        realms[i] = realm;
      }
    }
    return realms;
  }

  public GSSCredential getCredentials(String realm) throws IllegalArgumentException, GSSException {

    GSSCredential credential = null;
    String jgssNameOption = properties.getProperty(IConstants.CONF_GSS_NAME);
    String[] jgssName = Utils.stringToArray(jgssNameOption);
    location.debugT("Looking for credentials for realm " + realm);
    int i;
    for (i = 0; i < jgssName.length; i++) {
      String gssName = jgssName[i];
      String suffix = Utils.getKPNSuffix(gssName);
      if (suffix.equals(realm)) {
        location.debugT("Looking for credentials for " + gssName);
        // get cached
        credential = (GSSCredential) credentialsCache.get(gssName);
        if (null == credential) {
          credential = acquireCredentials(gssName);
          if (null == credential) {
            location.infoT("Could not acquire credentials for " + gssName);
          }
        } else {
          location.debugT("Found cached credentials for " + gssName + " " + credential);
        }
        break;
      }
    }
    if (jgssName.length == i) {
      location.warningT("No jGSSName found for realm " + realm + ". jGSSNames are: " + jgssNameOption);
      throw new GSSException(GSSException.DEFECTIVE_CREDENTIAL);
    }
    return credential;
  }

  /**
   * Returns a list of supported OIDs. This login module supports the GSS
   * mechanisms represented by the OIDs returned by this functions.
   * 
   * @return array of object ids.
   */
  public ObjectID[] getSupportedMechs() {
    return supportedMechanisms;
  }

  // ////////////////////////////////////////////////////////////////////
  //
  // H E L P E R F U N C T I O N S
  // 
  // ////////////////////////////////////////////////////////////////////

  /**
   * Calls JGSS API calls in order to get a valid GSSCredential object for gss
   * context establishment. At a later point in time, this credential object
   * will be passed to GSSManager.getInstance (). createContext ();
   * 
   * @throws LoginException
   *           in case the GSS name creation or GSS credential acquisition
   *           throws a GSSException.
   */
  private GSSCredential acquireCredentials(String gssName) throws IllegalArgumentException, GSSException {
    GSSCredential res = null;
    if (properties.getProperty(IConstants.CONF_CREDS_IN_THREAD, IConstants.DEFAULT_CREDS_IN_THREAD).equalsIgnoreCase("true")) {
      res = this.acquireCredentialsInNewThread(gssName);
    } else {
      res = this.acquireCredentialsInCurrentThread(gssName);
    }
    return res;
  }

  private GSSCredential acquireCredentialsInCurrentThread(String gssName) throws IllegalArgumentException, GSSException {
    String strGSSNameType = null;
    String strGSSMechOid = null;
    GSSManager gssman = null;
    GSSName gssname = null;
    GSSCredential res = null;
    Oid nametype = null;

    // Read GSS Name
    location.infoT("Acquiring credentials for GSS name " + gssName.toString());
    // Read GSS Name type. Either
    // HOSTBASED_SERVICE (HTTP@p131562.wdf.sap.corp) or USER_NAME
    // (HTTP/p131562.wdf.sap.corp@MSCTSC.SAP.CORP)
    strGSSNameType = properties.getProperty(IConstants.CONF_GSS_NAME_TYPE, IConstants.DEFAULT_GSS_NAME_TYPE);
    location.infoT("GSS name type is: " + strGSSNameType);

    // Set mech type to OID
    if ("0".equals(strGSSNameType)) {
      nametype = GSSName.NT_HOSTBASED_SERVICE;
    } else if ("1".equals(strGSSNameType)) {
      nametype = GSSName.NT_USER_NAME;
    } else {
      if (location.beError()) {
        location.errorT("Unknown name type " + strGSSNameType);
      }
      throw new IllegalArgumentException("Unknown GSS name type " + strGSSNameType);
    }
    location.infoT("GSS name type " + strGSSNameType + " is :" + nametype);

    // Read mechanism to support
    strGSSMechOid = properties.getProperty(IConstants.CONF_GSS_MECH, IConstants.DEFAULT_GSS_MECH);
    if (location.beInfo()) {
      location.infoT("GSS mechanism is: " + strGSSMechOid);
    }

    gssman = GSSManager.getInstance();
    gssname = gssman.createName(gssName, nametype);

    res = gssman.createCredential(gssname, GSSCredential.INDEFINITE_LIFETIME, new Oid(strGSSMechOid), GSSCredential.ACCEPT_ONLY);
    credentialsCache.put(gssName, res);
    if (location.beInfo()) {
      location.infoT("Credentials acquired: {0}", new Object[] { credentialsCache });
    }

    return res;
  }

  /**
   * Starts a new Thread which calls {@link #acquireCredentials()}.
   * 
   * @throws LoginException
   *           in case the GSS name creation or GSS credential acquisition
   *           throws a GSS exception.
   */
  private GSSCredential acquireCredentialsInNewThread(String gssName) throws IllegalArgumentException, GSSException {
    GSSCredential res = null;
    RunnableHelper runnableHelper = new RunnableHelper(gssName);
    runnableHelper.start();

    synchronized (runnableHelper) {
      while (!runnableHelper.isFinished()) {
        try {
          runnableHelper.wait();
        } catch (InterruptedException e) {
          // $JL-EXC$
        }
      }
    }
    if (runnableHelper.getException() != null) {
      throw runnableHelper.getException();
    }
    res = runnableHelper.getResult();
    return res;
  }

  /**
   * Reads and parses the list of supported GSS API mechanisms and fills the
   * instance variable _suppmechs.
   */
  private void loadSupportedMechs() {

    ArrayList mechanismsList = new ArrayList();
    String mechanismsString = properties.getProperty(IConstants.CONF_SUPPORTED_MECHS, IConstants.DEFAULT_SUPPORTED_MECHS);
    ObjectID oid = null;

    if (mechanismsString == null)
      return;

    StringTokenizer tokenizer = new StringTokenizer(mechanismsString, ",");
    while (tokenizer.hasMoreElements()) {
      // Check whether this is a correct oid
      oid = new ObjectID(tokenizer.nextToken());
      mechanismsList.add(oid);
    }
    supportedMechanisms = (ObjectID[]) mechanismsList.toArray(new ObjectID[0]);
  }

  /**
   * Verifies if all the LoginModule options are valid parameter names. In case
   * an unknown parameter is found a LoginException is found. This helps to
   * avoid configuration problems.
   * 
   * @throws LoginException
   *           in case an invalid parameter is found
   */
  private void verifyProps() throws IllegalArgumentException {
    Enumeration enumeration = properties.keys();
    while (enumeration.hasMoreElements()) {
      String key = (String) enumeration.nextElement();
      if (!SUPPORTED_PARAMS.contains(key)) {
        location.warningT("Unknown login module option: " + key);
      }
    }
  }

  class RunnableHelper extends Thread {
    private GSSException gssException = null;
    private boolean hasFinished = false;
    private String gssName = null;
    private GSSCredential credential = null;

    RunnableHelper(String gssName) {
      this.gssName = gssName;
    }

    public GSSCredential getResult() {
      return credential;
    }

    GSSException getException() {
      return gssException;
    }

    boolean isFinished() {
      return hasFinished;
    }

    public void run() {
      try {
        credential = acquireCredentialsInCurrentThread(gssName);
      } catch (GSSException e) {
        gssException = e;
      }
      hasFinished = true;
      synchronized (this) {
        notifyAll();
      }
    }
  }
}
