/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.jmx.sso2.impl;

import com.sap.engine.interfaces.keystore.KeystoreManager;
import com.sap.engine.services.security.jmx.auth.AuthStackEntry;
import com.sap.engine.services.security.jmx.auth.AuthenticationManagerMBean;
import com.sap.engine.services.security.jmx.auth.LoginModule;
import com.sap.engine.services.security.jmx.auth.MapEntry;
import com.sap.engine.services.security.jmx.auth.PolicyConfiguration;
import com.sap.engine.services.security.jmx.auth.impl.AuthStackEntryImpl;
import com.sap.engine.services.security.jmx.auth.impl.AuthUtil;
import com.sap.engine.services.security.jmx.auth.impl.LoginModuleImpl;
import com.sap.engine.services.security.jmx.auth.impl.MapEntryImpl;
import com.sap.engine.services.security.jmx.auth.impl.PolicyConfigurationImpl;
import com.sap.engine.services.security.jmx.sso2.CertificateInfo;
import com.sap.engine.services.security.jmx.sso2.LogonTicketIssuer;
import com.sap.engine.services.security.jmx.sso2.SSO2Management;
import com.sap.engine.services.security.jmx.sso2.SystemInfo;
import com.sap.engine.services.security.jmx.sso2.TicketIssuer;
import com.sap.engine.services.security.jmx.sso2.TicketIssuerCertificate;
import com.sap.engine.services.security.jmx.sso2.TicketIssuersInfo;
import com.sap.engine.services.security.server.SecurityContextImpl;
import com.sap.jmx.ObjectNameFactory;
import com.sap.security.api.logon.ILoginConstants;
import com.sap.security.core.InternalUMFactory;
import com.sap.security.core.server.jaas.EvaluateAssertionTicketLoginModule;
import com.sap.security.core.server.jaas.EvaluateTicketLoginModule;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;
import static com.sap.tc.logging.Severity.INFO;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import javax.management.openmbean.CompositeData;
import javax.naming.CompoundName;
import java.rmi.RemoteException;
import java.security.AccessController;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Krasimira Velikova
 *
 *
 */
public class J2EESSO2Management
    extends StandardMBean
    implements SSO2Management
{

  private static final Location TRACE = Location.getLocation(J2EESSO2Management.class);

  private static final String AUTHENTICATION_CATEGORY = "Authentication";
  private static final Category CATEGORY = Category.getCategory(Category.SYS_SECURITY, AUTHENTICATION_CATEGORY);
  private final static Category CHANGE_LOG_CATEGORY = Category.getCategory(Category.SYS_CHANGES, "Properties/Security");


  /** default keypair alias */
  static final String DEFAULT_KEYPAIR_ALIAS = "SAPLogonTicketKeypair";

  /** default KEYSTORE_MANAGER view */
  static final String DEFAULT_KEYSTORE_VIEW = "TicketKeystore";

  //** default client */
  static final String DEFAULT_CLIENT = "000";

  private static final String CLUSTER_MANAGER = "ClusterManager";

  private static final String TRUSTED_SYSTEM = "trustedsys";
  private static final String TRUSTED_ISSUER_DN = "trustediss";
  private static final String TRUSTED_SYSTEM_DN = "trusteddn";
  private static final String UME_CONFIGUARATION_ACTIVE = "ume.configuration.active";

  private static final String EVALUATE_TICKET_LM = EvaluateTicketLoginModule.class.getName();
  private static final String EVALUATE_ASSERTION_TICKET_LM = EvaluateAssertionTicketLoginModule.class.getName();

  private static final String ETLM_DISPLAY_NAME = "EvaluateTicketLoginModule";
  private static final String ETLM_DESCRIPTION = "Login module to evaluate SAP Logon Tickets";
  private static final String EATLM_DISPLAY_NAME = "EvaluateAssertionTicketLoginModule";
  private static final String EATLM_DESCRIPTION = "This login module verifies SAP Authentication Assertion tickets.";


  private static final int CERT_NOT_FOUND = 0;
  private static final int CERT_FOUND = 1;
  private static final int CERT_FOUND_BINARY_DIFF = 2;

  private static final int MODE_UME_OPTION_CHECK = 2;

  private static final String PATTERN_TRUSTED_KEY = "trusted((?:sys)|(?:dn)|(?:iss))(\\d+)";
  private static final String PATTERN_TRUSTED_SYSTEM_VALUE = "\\s*(\\S+)\\s*,\\s*(\\d{3})";

  private static KeystoreManager KEYSTORE_MANAGER;

  private Pattern trustedKeyPattern;
  private Pattern trustedSystemValuePattern;

  private AuthenticationManagerMBean authMBean;

  public static void setKeyStoreManager(KeystoreManager keystoreManager) {
    if (keystoreManager == null) {
      return;
    }
    KEYSTORE_MANAGER = keystoreManager;
  }

  public static void register(MBeanServer mbs, AuthenticationManagerMBean mBean ) throws JMException {
    ObjectName sso2ObjName = ObjectNameFactory.getNameForServerChild(
        SSO2Management.JMX_J2EETYPE, SSO2Management.MBEAN_NAME, null);

    if (!mbs.isRegistered(sso2ObjName)) {
      SSO2Management sso2MBean = new J2EESSO2Management(mBean);

      mbs.registerMBean(sso2MBean, sso2ObjName);
    }
  }

  public J2EESSO2Management(AuthenticationManagerMBean authMBean) throws NotCompliantMBeanException {
    super(SSO2Management.class);
    this.authMBean = authMBean;

    trustedKeyPattern = Pattern.compile(PATTERN_TRUSTED_KEY);
    trustedSystemValuePattern = Pattern.compile(PATTERN_TRUSTED_SYSTEM_VALUE);
  }


  /**
   * @see com.sap.engine.services.security.jmx.sso2.SSO2Management#getVersion()
   */
  public int getVersion()
  {
    return SSO2_JMX_MODEL_VERSION_1;
  }


  /**
   * @see com.sap.engine.services.security.jmx.sso2.SSO2Management#getSystemInfo()
   */
  public CompositeData getSystemInfo()
      throws Exception
  {

    // get domain name
    String domainName = "";
    try
    {
      java.net.InetAddress address = java.net.InetAddress.getLocalHost();
      String canonicalHostName = address.getCanonicalHostName();
      int index = canonicalHostName.indexOf(".");
      if (index > 0)
      {
        domainName = canonicalHostName.substring(index + 1);
      }
    }
    catch (Exception e)
    {
      TRACE.catching("Failed to resolve domain name", e);
    }

    // get system time
    long systemTime = System.currentTimeMillis();

    SystemInfo sytemInfo = new SystemInfoImpl(systemTime, domainName);
    return JmxUtil.getCDataForSystemInfo(sytemInfo);
  }



  /**
   * @see com.sap.engine.services.security.jmx.sso2.SSO2Management#getTicketIssuerInfo()
   */
  public synchronized CompositeData getLogonTicketIssuerInfos() throws Exception {
    checkInitialized();
    X509Certificate cert = null;
    try {
      String keyPairAlias = getUMEPropertyAsString(ILoginConstants.SSOTICKET_KEYALIAS, DEFAULT_KEYPAIR_ALIAS);
      cert = getCertificate(keyPairAlias);


      Boolean b = (Boolean)invokeKeystoreMethod(KEYSTORE_IS_KEY_ENTRY, keyPairAlias);
      if(!b.booleanValue()){
        cert = null;
        SimpleLogger.trace(Severity.ERROR, TRACE, "ASJ.secsrv.009507", "Failed to get signing certificate. It is not a key entry.");
      }

    } catch (KeyStoreException e) {
      SimpleLogger.traceThrowable(Severity.ERROR, TRACE, "ASJ.secsrv.009508", "Failed to get signing certificate. It is not a key entry.", e);
    }

    String client = getUMEPropertyAsString(ILoginConstants.SSOTICKET_ISSUER_CLIENT, DEFAULT_CLIENT);
    String systemID = SecurityContextImpl.getSystemID();
    LogonTicketIssuer lti = new LogonTicketIssuerImpl(systemID, client, cert);
    return JmxUtil.getCDataForLogonTicketIssuer(lti);
  }

  /**
   * @see com.sap.engine.services.security.jmx.sso2.SSO2Management#getTrustedTicketIssuers()
   *
   * @deprecated
   *
   */
  public synchronized CompositeData[] getListOfTrustedTicketIssuers()
      throws Exception
  {
    checkInitialized();
    LoginModule ticketLM = getEvaluateTicketLoginModule();
    LoginModule assertionLM = getEvaluateAssertionTicketLoginModule();
    TicketIssuer[] acl = consistencyCheck(ticketLM, assertionLM);
    return JmxUtil.getCDataArrForTicketIssuer(acl);
  }


  /**
   * @see com.sap.engine.services.security.jmx.sso2.SSO2Management#getTrustedTicketIssuersInfo(int)
   *
   */
  public synchronized CompositeData getTrustedTicketIssuersInfo(int checkMode)
      throws Exception
  {
    checkInitialized();

    // get all certificates from the configured keystore view
    Map ticketCertificates = new HashMap();
    Map caCerts = new HashMap();

    getClassifiedTicketCertificates(ticketCertificates, caCerts);

    // check certificate consistency
    TicketIssuersInfo inconsistentCertificates = checkCertificateConsistency(ticketCertificates, caCerts);
    if (inconsistentCertificates != null)
    {
      // found certificate inconsistency
      TRACE.warningT("Found certificate inconsistency");
      return JmxUtil.getCDataForTicketIssuersInfo(inconsistentCertificates);
    }

    // certificate consistency check pass OK continue with SSO2 consistency check

    boolean isExistsUserstoreSSO2LMs;
    List sso2LMOptions;
    try
    {
      isExistsUserstoreSSO2LMs = isExistsUserstoreSSO2LMs();

      // get from US and PCs all ETLM and EATLM options
      sso2LMOptions = getSSO2LMOptions(checkMode);
    }
    catch (Exception err)
    {
      SimpleLogger.traceThrowable(Severity.ERROR, TRACE, err, "ASJ.secsrv.009509", "Failed to check SSO2 consistency. Exception message: {0}", new Object[]{err.getMessage()});
      throw new Exception("Failed to check SSO2 consistency", err);
    }

    if (!isExistsUserstoreSSO2LMs || sso2LMOptions.size() > 1)
    {
      // SSO2 inconsistency found -> find trusted system union
      if (!isExistsUserstoreSSO2LMs)
      {
        TRACE.warningT("Found SSO2 inconsistency caused by missing EvaluateTicketLoginModule or EvaluateAssertionTicketLoginModule in the Userstore");
      }
      if (sso2LMOptions.size() > 1)
      {
        TRACE.warningT("Found SSO2 inconsistency caused by different option set in the EvaluateTicketLoginModules and EvaluateAssertionTicketLoginModules");
      }

      TicketIssuersInfo result = findTrustedSystemsUnion(sso2LMOptions, ticketCertificates, caCerts);
      return JmxUtil.getCDataForTicketIssuersInfo(result);
    }

    // certificate and SSO2 consistency checks pass OK return configured trusted systems
    TicketIssuersInfo result = findTrustedSystems(ticketCertificates, caCerts);
    return JmxUtil.getCDataForTicketIssuersInfo(result);
  }


  /**
   * @see com.sap.engine.services.security.jmx.sso2.SSO2Management#storeTrustedTicketIssuers(com.sap.engine.services.security.jmx.sso2.TicketIssuerCertificate[])
   *
   */
  public synchronized void storeTrustedTicketIssuers(CompositeData[] issuers)
      throws Exception
  {
    final String SIGNATURE = "storeTrustedTicketIssuers(CompositeData[])";

    if (TRACE.bePath())
    {
      TRACE.entering(SIGNATURE, issuers);
    }

    checkInitialized();

    TicketIssuerCertificate[] issuersCert = new TicketIssuerCertificate[issuers.length];
    for (int i = 0; i < issuersCert.length; i++)
    {
      issuersCert[i] = new TicketIssuerCertificateImpl(issuers[i]);
    }

    // convert trusted systems to MapEntry array of options
    MapEntry[] options = convertTicketIssuerToMapEntries(issuersCert);

    // key->LM name, value->classname
    Map userStoreSSO2LMs = new HashMap();

    // 1. process all entries in the US with either ETLM or EATLM classname
    LoginModule[] userStoreLMs = authMBean.getLoginModules();

    LoginModule[] sso2LMs = getUserstoreSSO2LMs();

    // check for available at least one ETLM and EATLM, if not create
    boolean isExistsETLM = isExistsETLM(sso2LMs);
    boolean isExistsEATLM = isExistsEATLM(sso2LMs);
    if (!isExistsETLM)
    {
      TRACE.warningT(SIGNATURE, "Missing EvaluateTicketLoginModule in the Userstore. Will be created a new one.");
      LoginModule updatedLM = new LoginModuleImpl(ETLM_DISPLAY_NAME, EVALUATE_TICKET_LM, ETLM_DESCRIPTION, options);
      authMBean.saveLoginModule(updatedLM);

      SimpleLogger.log(Severity.INFO, CATEGORY, TRACE, "ASJ.secsrv.009535", "A new EvaluateTicketLoginModule is created successfully");
    }
    if (!isExistsEATLM)
    {
      TRACE.warningT(SIGNATURE, "Missing EvaluateAssertionTicketLoginModule in the Userstore. Will be created a new one.");
      LoginModule updatedLM = new LoginModuleImpl(EATLM_DISPLAY_NAME, EVALUATE_ASSERTION_TICKET_LM, EATLM_DESCRIPTION, options);
      authMBean.saveLoginModule(updatedLM);

      SimpleLogger.log(Severity.INFO, CATEGORY, TRACE, "ASJ.secsrv.009536", "A new EvaluateAssertionTicketLoginModule is created successfully");
    }

    for (int i = 0; i < userStoreLMs.length; i++)
    {
      String loginModuleClassName = userStoreLMs[i].getClassName();

      boolean isETLM = EVALUATE_TICKET_LM.equals(loginModuleClassName);
      boolean isEATLM = EVALUATE_ASSERTION_TICKET_LM.equals(loginModuleClassName);

      if (isETLM || isEATLM)
      {
        // store display name for further policy configuration LMs checking
        String displayName = userStoreLMs[i].getDisplayName();
        userStoreSSO2LMs.put(displayName, loginModuleClassName);

        String description = userStoreLMs[i].getDescription();

        try
        {
          LoginModule updatedLM = new LoginModuleImpl(displayName, loginModuleClassName, description, options);
          authMBean.saveLoginModule(updatedLM);
        }
        catch (Exception err)
        {
          SimpleLogger.traceThrowable(Severity.ERROR, TRACE, err, "ASJ.secsrv.009510", "Failed to update login module options. Exception message: {0}", new Object[]{err.getMessage()});
          throw err;
        }
      }
    }


    // 2. process all PCs, which not use Template and contain either classnames or display names,
    // which are found in the US
    PolicyConfiguration[] policyConfigurations = authMBean.getPolicyConfigurations();

    for (int i = 0; i < policyConfigurations.length; i++)
    {
      PolicyConfiguration policyConfiguration = policyConfigurations[i];

      String template = policyConfiguration.getTemplate();

      if (template == null || template.length() < 1)
      {
        boolean isChanged = false;

        AuthStackEntry[] authStack = policyConfiguration.getAuthStack();
        for (int j = 0; j < authStack.length; j++)
        {
          String loginModuleClassName = authStack[j].getClassName();
          String flag = authStack[j].getFlag();

          if (EVALUATE_TICKET_LM.equals(loginModuleClassName) ||
              EVALUATE_ASSERTION_TICKET_LM.equals(loginModuleClassName))
          {
            AuthStackEntry entry = new AuthStackEntryImpl(loginModuleClassName, options, flag);
            authStack[j] = entry;
            isChanged = true;
          }
          else if (userStoreSSO2LMs.containsKey(loginModuleClassName))
          {
            String className = (String)userStoreSSO2LMs.get(loginModuleClassName);
            AuthStackEntry entry = new AuthStackEntryImpl(className, options, flag);
            authStack[j] = entry;
            isChanged = true;
          }
        }

        if (isChanged)
        {
          String pcName = policyConfiguration.getName();
          Byte type = policyConfiguration.getType();
          MapEntry[] properties = policyConfiguration.getProperties();

          PolicyConfiguration updatedPC = new PolicyConfigurationImpl(pcName, type, template, authStack, properties);
          authMBean.savePolicyConfiguration(updatedPC);
        }
      }
      else
      {
        // save the same PC to perform update due to changed Template
        authMBean.savePolicyConfiguration(policyConfiguration);
      }
    }

    SimpleLogger.log(Severity.INFO, CATEGORY, TRACE, "ASJ.secsrv.009537", "SSO2 repair operation finished successfully");

    try
    {
      if (CATEGORY.beInfo())
      {
        for (int i = 0; i < issuersCert.length; i++)
        {
          String systemId = issuersCert[i].getSystemId();
          String client = issuersCert[i].getClientId();

          CertificateInfo certInfo = issuersCert[i].getCertificates()[0];
          String keyStoreEntryName = certInfo.getKeystoreEntryName();
          String subjectDN = certInfo.getCertificateSubject();
          String issuerDN = certInfo.getCertificateIssuer();
          int status = certInfo.getStatus();
          String certificate = certInfo.getCertificate();

          SimpleLogger.log(Severity.INFO, CATEGORY, TRACE, "ASJ.secsrv.009538", "Trusted system is stored successfully with the following data:\nSystemId: {0}\n" +
              "Client: {1}\nKeystore entry name: {2}\n + Subject DN: {3}\n Issuer DN: {4}\nStatus: {5}\nCertificate: {6}\n", new Object[] {systemId, client, keyStoreEntryName, subjectDN, issuerDN, new Integer(status), certificate});

          SimpleLogger.log(INFO
              , CHANGE_LOG_CATEGORY
              , TRACE
              , "ASJ.secsrv.000217"
              , "Trust to system [{0}, {1}] was established.Used certificate: [\n{2}]."
              , new Object[]{systemId, client, issuersCert[i]});
        }
      }
    }
    catch (Exception err)
    {
      TRACE.warningT(SIGNATURE, "Unable to log information for the stored trusted systems. Reason: {0}", new Object[]{err.getMessage()});
    }

    if (TRACE.bePath())
    {
      TRACE.exiting(SIGNATURE);
    }
  }


  /**
   * @see com.sap.engine.services.security.jmx.sso2.SSO2Management#addTrustedTicketIssuer(java.lang.String, java.lang.String, byte[], boolean)
   */
  public synchronized void addTrustedTicketIssuer(String systemID, String client, String certDerEnc, boolean replaceAllowed)
      throws Exception
  {

    final String SIGNATURE = "addTrustedTicketIssuer(String, String, String, boolean)";

    if (TRACE.bePath())
    {
      TRACE.entering(SIGNATURE, new Object[]{systemID, client, certDerEnc, new Boolean(replaceAllowed)});
    }

    checkInitialized();

    // ??? force systemId to UPPER case
    // systemID = systemID.toUpperCase();

    LoginModule[] sso2LMs = getUserstoreSSO2LMs();

    boolean isExistsETLM = isExistsETLM(sso2LMs);
    boolean isExistsEATLM = isExistsEATLM(sso2LMs);
    boolean hasEqualOptions = hasEqualOptions(sso2LMs);
    if (!isExistsETLM || !isExistsEATLM || !hasEqualOptions)
    {
      String reason;
      String errMsg;
      if (!isExistsETLM)
      {
        reason = "missing EvaluateTicketLoginModule in the Userstore";
        errMsg = USERSTORE_ETLM_NOT_FOUND;
      }
      else if (!isExistsEATLM)
      {
        reason = "missing EvaluateAssertionTicketLoginModule in the Userstore";
        errMsg = USERSTORE_EATLM_NOT_FOUND;
      }
      else
      {
        reason = "different EvaluateTicketLoginModule or EvaluateAssertionTicketLoginModule option sets";
        errMsg = CONSISTENCY_CHECK_FAILED;
      }

      if (TRACE.beWarning())
      {
        TRACE.warningT(SIGNATURE, "Unable to add new trusted system with systemId: {0} and client {1} due to {2}",
                       new Object[]{systemID, client, reason});
      }
      throw new Exception(errMsg);
    }

    X509Certificate signingCert;

    try {
      signingCert = LogonTicketIssuerImpl.decode(certDerEnc);
    } catch (Exception e2) {
      SimpleLogger.traceThrowable(Severity.ERROR, TRACE, e2, "ASJ.secsrv.009511", "Failed to decode certificate");
      throw new Exception(CERTIFICATE_SYNTAX_ERROR);
    }
    
    boolean foundStatus = hasCertificate(signingCert, replaceAllowed);

    String certAliasBase = systemID + "_" + client;

    if (!foundStatus) {
      String alias = certAliasBase;
      int counter = 1;

      try {
        while (containsAlias(alias)) {
          alias = certAliasBase + "_" + counter;
          counter++;
        }
      } catch (KeyStoreException e) {
        SimpleLogger.traceThrowable(Severity.ERROR, TRACE, e, "ASJ.secsrv.009512", "Failed to check alias {0}", alias);
        throw e;
      }

      try {
        setCertificateEntry(alias, signingCert);
      } catch (KeyStoreException e1) {
        SimpleLogger.traceThrowable(Severity.ERROR, TRACE, e1, "ASJ.secsrv.009513", "Failed to store certificate in ticket KEYSTORE_MANAGER under alias {0}", alias);
        throw e1;
      }
    }

    // !!! add ACL to the all ETLMs and EATLMs in the Userstore
    for (LoginModule sso2LM : sso2LMs) {
      addACL(sso2LM, systemID, client, signingCert, replaceAllowed);
    }

    SimpleLogger.log(Severity.INFO, CATEGORY, TRACE, "ASJ.secsrv.009539", "A new trusted system is added successfully with the following data:\nSystemId: {0}\nClient: {1}\nCertificate: {2}\n", new Object[] {systemID, client, certDerEnc});

    SimpleLogger.log(INFO
        , CHANGE_LOG_CATEGORY
        , TRACE
        , "ASJ.secsrv.000217"
        , "Trust to system [{0}, {1}] was established.Used certificate: [\n{2}]."
        , new Object[]{systemID, client, signingCert});


    if (TRACE.bePath())
    {
      TRACE.exiting(SIGNATURE);
    }

  }


  private void addACL(LoginModule loginModule, String systemID, String client, X509Certificate signingCert, boolean replaceAllowed) throws Exception {
    int index = getACLNumber(systemID, client, loginModule);

    if (index != -1 && !replaceAllowed) {
      TRACE.warningT("Adding new ACL to " + loginModule.getClassName() + " failed due to: ACL entry already exists");
      throw new Exception(ACL_ENTRY_ALREADY_EXISTS);
    }

    Map options = AuthUtil.convertMapEntriesToMap(loginModule.getOptions());

    if (index == -1) {
      index = 1;

      while (options.containsKey(TRUSTED_SYSTEM + index)) {
        index++;
      }
    }

    options.put(TRUSTED_SYSTEM + index, systemID + "," + client);
    options.put(TRUSTED_ISSUER_DN + index, signingCert.getIssuerDN().getName());
    options.put(TRUSTED_SYSTEM_DN + index, signingCert.getSubjectDN().getName());

    try
    {
      LoginModule updated = new LoginModuleImpl(
          loginModule.getDisplayName(),
          loginModule.getClassName(),
          loginModule.getDescription(),
          AuthUtil.convertMapToMapEntries(options));
      authMBean.saveLoginModule(updated);
    } catch (Exception e) {
      SimpleLogger.traceThrowable(Severity.ERROR, TRACE, e, "ASJ.secsrv.009514", "Failed to add ACL");
      throw e;
    }
  }


  /**
   * @see com.sap.engine.services.security.jmx.sso2.SSO2Management#removeTrustedTicketIssuer(java.lang.String, java.lang.String)
   */
  public synchronized void removeTrustedTicketIssuer(String systemID, String client)
      throws Exception
  {
    final String SIGNATURE = "removeTrustedTicketIssuer(String, String)";

    checkInitialized();

    LoginModule[] sso2LMs = getUserstoreSSO2LMs();

    boolean hasEqualOptions = hasEqualOptions(sso2LMs);
    if (!hasEqualOptions)
    {
      if (TRACE.beWarning())
      {
        TRACE.warningT(SIGNATURE, "Unable to remove trusted system with systemId: {0} and client {1} due to " +
        		           "different EvaluateTicketLoginModule or EvaluateAssertionTicketLoginModule option sets",
                       new Object[]{systemID, client});
      }
      throw new Exception(CONSISTENCY_CHECK_FAILED);
    }

    // !!! remove ACL from the all ETLMs and EATLMs in the Userstore
    for (int i = 0; i < sso2LMs.length; i++)
    {
      removeACL(sso2LMs[i], systemID, client);
    }

    SimpleLogger.log(Severity.INFO, CATEGORY, TRACE, "ASJ.secsrv.009540", "Trusted system is removed successfully with SystemId: {0} and Client: {1}", new Object[] {systemID, client});
    SimpleLogger.log(INFO
        , CHANGE_LOG_CATEGORY
        , TRACE
        , "ASJ.secsrv.000216"
        , "Trust to system [{0}, {1}] was removed."
        , new Object[]{systemID, client});

  }


  private void removeACL(LoginModule loginModule, String systemID, String client) throws Exception {
    int number = getACLNumber(systemID, client, loginModule);

    if (number == -1) {
      TRACE.infoT("removing ACL from " + loginModule.getClassName() + " failed due to: ACL entry not found");
      throw new Exception(ACL_ENTRY_NOT_FOUND);
    }

    Map options = AuthUtil.convertMapEntriesToMap(loginModule.getOptions());

    options.remove(TRUSTED_SYSTEM + number);
    options.remove(TRUSTED_ISSUER_DN + number);
    options.remove(TRUSTED_SYSTEM_DN + number);

    try
    {
      LoginModule updated = new LoginModuleImpl(
          loginModule.getDisplayName(),
          loginModule.getClassName(),
          loginModule.getDescription(),
          AuthUtil.convertMapToMapEntries(options));
      authMBean.saveLoginModule(updated);
    }
    catch (Exception e)
    {
      SimpleLogger.traceThrowable(Severity.ERROR, TRACE, e, "ASJ.secsrv.009515", "Failed to remove ACL");
      throw e;
    }
  }


  /**
   * @see com.sap.engine.services.security.jmx.sso2.SSO2Management#lock()
   */
  public synchronized void lock() throws Exception
  {
    authMBean.lock();
  }


  /**
   * @see com.sap.engine.services.security.jmx.sso2.SSO2Management#unlock()
   */
  public synchronized void unlock() throws Exception
  {
    authMBean.unlock();
  }


  private final void checkInitialized()
  {
    if (authMBean == null) {
      TRACE.infoT("AuthenticationManagemerMBean is null");
      throw new NullPointerException("Authentication Managemer MBean is null");
    }
    if (KEYSTORE_MANAGER == null) {
      TRACE.infoT("KeyStoreManager is null");
      throw new NullPointerException("KeyStoreManager is null");
    }
  }

  private LoginModule getEvaluateTicketLoginModule() throws Exception
  {
    LoginModule[] loginModules = authMBean.getLoginModules();

    if (loginModules == null)
    {
      return null;
    }

    for (int i = 0; i < loginModules.length; i++) {
      String loginModuleClassName = loginModules[i].getClassName();

      if (EVALUATE_TICKET_LM.equals(loginModuleClassName)) {
        return loginModules[i];
      }
    }

    return null;
  }


  private LoginModule getEvaluateAssertionTicketLoginModule() throws Exception {
    LoginModule[] loginModules = authMBean.getLoginModules();

    if (loginModules == null) {
      return null;
    }

    for (int i = 0; i < loginModules.length; i++) {
      String loginModuleClassName = loginModules[i].getClassName();

      if (EVALUATE_ASSERTION_TICKET_LM.equals(loginModuleClassName)) {
        return loginModules[i];
      }
    }

    return null;
  }

  private X509Certificate getCertificate(String issuerDN, String systemDN) throws KeyStoreException {
    Enumeration en = getTicketKeystoreAliases();

    while (en.hasMoreElements()) {
      String alias = (String) en.nextElement();

      if (isCertificateEntry(alias)) {
        X509Certificate cert = getCertificate(alias);

        if (match(issuerDN, systemDN, cert)) {
          return cert;
        }
      }

      if (isKeyEntry(alias)) {
        Certificate[] trustCerts = getCertificateChain(alias);

        if (trustCerts == null) {
          continue;
        }

        for (int i = 0; i < trustCerts.length; i++) {
          X509Certificate cert = (X509Certificate) trustCerts[i];

          if (match(issuerDN, systemDN, cert)) {
            return cert;
          }
        }
      }
    }

    return null;
  }


  /**
   * Get all certificates from the configured keystore view in two Maps:
   * <li><code>ticketCertificates</code> - contains CertificateInfoModel objects classified by subjectDN and issuerDN
   * <li><code>caCerts</code> - contains X509Certificate objects classified by subjectDN
   *
   * @param ticketCertificates - the output Map into which will be stored Lists of
   * CertificateInfoModel objects classified by subjectDN and issuerDN.
   * All previous entries in this Map will be removed.
   *
   * @param caCerts - the output Map into which will be stored Lists of
   * X509Certificate objects classified by subjectDN.
   * All previous entries in this Map will be removed.
   *
   */
  private void getClassifiedTicketCertificates(Map ticketCertificates, Map caCerts)
      throws Exception
  {
    final String SIGNATURE = "getClassifiedTicketCertificates(Map, Map)";

    ticketCertificates.clear();
    caCerts.clear();

    Enumeration en = getTicketKeystoreAliases();

    while (en.hasMoreElements())
    {
      String alias = (String)en.nextElement();

      X509Certificate cert = null;
      if (isCertificateEntry(alias))
      {
        cert = getCertificate(alias);
      }

      if (cert != null)
      {
        CertificateInfoModel certModel = new CertificateInfoModel(alias, cert);

        if (TRACE.beDebug())
        {
          TRACE.debugT(SIGNATURE, "Certificate: " + certModel.toString());
        }

        String ticketCertificatesKey = certModel.getSubjectDN() + certModel.getIssuerDN();
        if (ticketCertificates.containsKey(ticketCertificatesKey))
        {
          // add this certificate to existing entry
          List valueList = (List)ticketCertificates.get(ticketCertificatesKey);
          valueList.add(certModel);
        }
        else
        {
          // create new entry
          List valueList = new ArrayList();
          valueList.add(certModel);
          ticketCertificates.put(ticketCertificatesKey, valueList);
        }

        String caCertsKey = cert.getSubjectDN().getName();
        if (caCerts.containsKey(caCertsKey))
        {
          // add this certificate to existing entry
          List valueList = (List)caCerts.get(caCertsKey);
          valueList.add(cert);
        }
        else
        {
          // create new entry
          List valueList = new ArrayList();
          valueList.add(cert);
          caCerts.put(caCertsKey, valueList);
        }
      }
    }
  }


  /**
   * Check ticketCertificates for certificates with equal subjectDN + issuerDN
   *
   * @param ticketCertificates - classified by subjectDN + issuerDN ticket certificates
   * @param caCerts - CA certificates, keys->subjectDN and values->List of CertificateInfoModel objects
   * @return inconsistent certificates or null if there is not inconsistency
   */
  private TicketIssuersInfo checkCertificateConsistency(Map ticketCertificates, Map caCerts)
      throws Exception
  {
    TicketIssuersInfo result = null;

    try
    {
      List inconsistentGroups = new ArrayList();

      Object[] keys = ticketCertificates.keySet().toArray();
      for (int i = 0; i < keys.length; i++)
      {
        List valueList = (List)ticketCertificates.get(keys[i]);
        if (valueList != null && valueList.size() > 1)
        {
          // inconsistent group found
          int status = TicketIssuerCertificate.TICKET_ISSUER_STATUS_REPEATED_CERTS;

          CertificateInfo[] certificates = new CertificateInfo[valueList.size()];
          for (int j = 0; j < certificates.length; j++)
          {
            CertificateInfoModel model = (CertificateInfoModel) valueList.get(j);
            certificates[j] = model.convertToCompositeData(caCerts);
          }

          TicketIssuerCertificate group = new TicketIssuerCertificateImpl(status, certificates);
          inconsistentGroups.add(group);
        }
      }

      if (inconsistentGroups.size() > 0)
      {
        // found certificate inconsistency -> return TicketIssuersInfo with
        // actionStatus=ACTION_STATUS_CERTIFICATE_INCONSISTENCY and repeated certificates by groups
        TicketIssuerCertificate[] inconsistentCertificates =
            (TicketIssuerCertificate[])inconsistentGroups.toArray(new TicketIssuerCertificate[inconsistentGroups.size()]);

        int actionStatus = TicketIssuersInfo.ACTION_STATUS_CERTIFICATE_INCONSISTENCY;
        result = new TicketIssuersInfoImpl(actionStatus, inconsistentCertificates);
      }
    }
    catch (Exception err)
    {
      String message = "Failed to check certificate consistency";
      SimpleLogger.traceThrowable(Severity.ERROR, TRACE, err, "ASJ.secsrv.009516", "Failed to check certificate consistency");
      throw new Exception(message, err);
    }

    return result;
  }


  /**
   * Get all different (repeated are skiped) ETLM and EATLM options separatelly from the US
   * and all PCs.
   * <pre>
   * LMs are picked up by the following rules:
   *  1. All entries in the US with either ETLM or EATLM classname
   *  2. All PCs, which not use Template and contain either classnames or display names,
   * which are found in the US
   * </pre>
   *
   * @return List with Map entries, each of them containing ETLM or EATLM options
   */
  private List getSSO2LMOptions(int checkMode)
      throws Exception
  {
    List result = new ArrayList();

    Set userStoreSSO2LMsNames = new HashSet();

    // 1. process all entries in the US with either ETLM or EATLM classname
    LoginModule[] userStoreLMs = authMBean.getLoginModules();

    for (int i = 0; i < userStoreLMs.length; i++)
    {
      String loginModuleClassName = userStoreLMs[i].getClassName();

      if (EVALUATE_TICKET_LM.equals(loginModuleClassName) ||
          EVALUATE_ASSERTION_TICKET_LM.equals(loginModuleClassName))
      {
        // store display name for further policy configuration LMs checking
        String displayName = userStoreLMs[i].getDisplayName();
        userStoreSSO2LMsNames.add(displayName);

        // get sso2 LM options as a Map
        MapEntry[] optionArray = userStoreLMs[i].getOptions();
        Map options = AuthUtil.convertMapEntriesToMap(optionArray);

        if (!result.contains(options))
        {
          // add only different options
          result.add(options);
          
          if (result.size() > 1 && TRACE.beDebug()) 
          {
            TRACE.debugT("Login module: {0} in the Userstore has different options from the other EvaluateTicketLoginModule and EvaluateAssertionTicketLoginModules", 
                         new Object[]{loginModuleClassName}); 
          }
        }
      }
    }

    // 2. process all PCs, which not use Template and contain either classnames or display names,
    // which are found in the US
    PolicyConfiguration[] policyConfnfigurations = authMBean.getPolicyConfigurations();

    for (int i = 0; i < policyConfnfigurations.length; i++)
    {
      String template = policyConfnfigurations[i].getTemplate();

      if (template == null || template.length() < 1)
      {
        AuthStackEntry[] authStack = policyConfnfigurations[i].getAuthStack();
        for (int j = 0; j < authStack.length; j++)
        {
          String loginModuleClassName = authStack[j].getClassName();

          if (EVALUATE_TICKET_LM.equals(loginModuleClassName) ||
              EVALUATE_ASSERTION_TICKET_LM.equals(loginModuleClassName) ||
              userStoreSSO2LMsNames.contains(loginModuleClassName))
          {
            // get sso2 LM options as a Map
            MapEntry[] optionArray = authStack[j].getOptions();
            Map options = AuthUtil.convertMapEntriesToMap(optionArray);

            if (!result.contains(options))
            {
              // add only different options
              result.add(options);
              
              if (result.size() > 1 && TRACE.beDebug()) 
              {
                TRACE.debugT("Login module: {0} in policy configuration: {1} has different options from the login module in the Userstore", 
                             new Object[]{loginModuleClassName, policyConfnfigurations[i].getName()}); 
              }
            }
          }
        }
      }
    }

    return result;
  }


  /**
   * Calculates list of suggested trusted systems
   *
   * @param sso2LMOptions
   * @param ticketCertificates
   * @return
   */
  private TicketIssuersInfo findTrustedSystemsUnion(List sso2LMOptions, Map ticketCertificates, Map caCerts)
      throws Exception
  {
    Map finalOptions = new HashMap();

    for (Iterator iter = sso2LMOptions.iterator(); iter.hasNext();)
    {
      Map optionsMap = (Map) iter.next();
      Set keys = optionsMap.keySet();

      for (Iterator it = keys.iterator(); it.hasNext();)
      {
        String key = (String) it.next();

        TicketIssuerCertificateModel trustedSystem = findReliableTrustedSystem(key, optionsMap, ticketCertificates, caCerts);

        if (trustedSystem != null)
        {

          String finalOptionsKey = trustedSystem.getSystemId() + "," + trustedSystem.getClientId();

          // there is exactly one certificate entry
          String finalOptionsCertificateKey = (String)trustedSystem.getCertificates().keySet().iterator().next();

          TicketIssuerCertificateModel model = (TicketIssuerCertificateModel)finalOptions.get(finalOptionsKey);
          // check for existing such system in the final union of the options
          if (model == null)
          {
            // there is not such trusted system -> add it to the final options
            finalOptions.put(finalOptionsKey, trustedSystem);
          }
          else
          {
            // check if the entry has such subject DN and issuer DN
            if (model.getCertificate(finalOptionsCertificateKey) == null)
            {
              // conflict found, user interaction needed
              // add this certificate and change status to TICKET_ISSUER_STATUS_CONFLICT_CERTS
              model.putCertificates(trustedSystem.getCertificates());
              model.setStatus(TicketIssuerCertificate.TICKET_ISSUER_STATUS_CONFLICT_CERTS);
            }
          }
        }
      }
    }

    // convert finalOptions map to the TicketIssuersInfo result and set actionStatus to ACTION_STATUS_SSO2_INCONSISTENCY
    TicketIssuerCertificateModel[] certModels = (TicketIssuerCertificateModel[])finalOptions.values().toArray(new TicketIssuerCertificateModel[0]);
    TicketIssuerCertificate[] issuerCertificates = new TicketIssuerCertificateImpl[certModels.length];

    for (int i = 0; i < issuerCertificates.length; i++)
    {
      issuerCertificates[i] = certModels[i].convertToCompositeData();
    }

    TicketIssuersInfo result = new TicketIssuersInfoImpl(TicketIssuersInfo.ACTION_STATUS_SSO2_INCONSISTENCY, issuerCertificates);
    return result;
  }


  /**
   *
   * @param key
   * @param optionsMap
   * @param ticketCertificates
   * @param caCerts
   * @return
   * @throws Exception
   */
  private final TicketIssuerCertificateModel findReliableTrustedSystem(String key, Map optionsMap, Map ticketCertificates, Map caCerts)
      throws Exception
  {
    final String SIGNATURE = "findReliableTrustedSystem(String, Map, Map, Map)";

    Matcher matcher = trustedKeyPattern.matcher(key);
    if (!matcher.matches())
    {
      // this is not sso2 option. Other options is ignored
      return null;
    }

    String type = matcher.group(1);
    if (!"sys".equals(type))
    {
      // this is not trustedsys option.
      return null;
    }

    String index = matcher.group(2);

    String trustedsys = (String)optionsMap.get(TRUSTED_SYSTEM + index);
    String trusteddn = (String)optionsMap.get(TRUSTED_SYSTEM_DN + index);
    String trustediss = (String)optionsMap.get(TRUSTED_ISSUER_DN + index);

    if (trustedsys == null)
    {
      return null;
    }
    else
    {
      Matcher sysMatcher = trustedSystemValuePattern.matcher(trustedsys);

      // check correct trustedsys sintax
      if (!sysMatcher.matches())
      {
        // Omit this system, due to incorrect trustedsys option syntax
        if (TRACE.beDebug())
        {
          TRACE.debugT(SIGNATURE, "Omit system: \"{0}\", due to incorrect trustedsys option syntax.", new Object[]{trustedsys});
        }
        return null;
      }

      // standardize subject and issuer dn
      if (trusteddn == null)
      {
        trusteddn = "";
      }
      else
      {
        trusteddn = trusteddn.trim();
      }
      if (trustediss == null)
      {
        trustediss = "";
      }
      else
      {
        trustediss = trustediss.trim();
      }

      // check available trusteddn
      if (trusteddn.length() < 1)
      {
        // Omit this system, due to missing subject DN
        if (TRACE.beDebug())
        {
          TRACE.debugT(SIGNATURE, "Omit system: \"{0}\", due to missing subject DN (trusteddn{1} option)",
              new Object[]{trustedsys, new Integer(index)});
        }
        return null;
      }

      // check available trustediss
      if (trustediss.length() < 1)
      {
        // Omit this system, due to missing Issuer DN
        if (TRACE.beDebug())
        {
          TRACE.debugT(SIGNATURE, "Omit system: \"{0}\", due to missing issuer DN (trustediss{1} option)",
              new Object[]{trustedsys, new Integer(index)});
        }
        return null;
      }

      // there are subject DN and issuer subject DN -> check available certificate
      CertificateInfoModel certModel = findCertificate(ticketCertificates, trusteddn, trustediss);

      if (certModel == null)
      {
        // Omit this system, due to missing certificate
        if (TRACE.beDebug())
        {
          TRACE.debugT(SIGNATURE, "Omit system: \"{0}\", due to missing certificate with subject DN: \"{1}\" and issuer DN: \"{2}\"",
              new Object[]{trustedsys, trusteddn, trustediss});
        }
        return null;
      }

      // extract system id and system client from the trustedsys option
      String sysId = sysMatcher.group(1);
      // ??? force systemId to UPPER case
      // systemID = systemID.toUpperCase();

      String sysClient = sysMatcher.group(2);

      CertificateInfo certInfo = certModel.convertToCompositeData(caCerts);

      TicketIssuerCertificateModel result = new TicketIssuerCertificateModel(sysId, sysClient, TicketIssuerCertificate.TICKET_ISSUER_STATUS_OK);
      String certificateKey = trusteddn + trustediss;
      result.putCertificate(certificateKey, certInfo);

      if (TRACE.beDebug())
      {
        TRACE.debugT(SIGNATURE, "Found reliable trusted system: " + result.toString());
      }

      return result;
    }
  }


  private TicketIssuersInfo findTrustedSystems(Map ticketCertificates, Map caCerts)
      throws Exception
  {
    List trustedSystems = new ArrayList();

    // TODO consider the way to get options
    LoginModule etlm = getEvaluateTicketLoginModule();

    if (etlm != null)
    {
      MapEntry[] entries = etlm.getOptions();
      Map options = AuthUtil.convertMapEntriesToMap(entries);

      Set keys = options.keySet();
      for (Iterator iter = keys.iterator(); iter.hasNext();)
      {
        String key = (String) iter.next();
        TicketIssuerCertificateModel issuer = findTrustedSystem(key, options, ticketCertificates, caCerts);

        if (issuer != null)
        {
          trustedSystems.add(issuer);
        }
      }
    }

    TicketIssuerCertificate[] ticketIssuers = new TicketIssuerCertificate[trustedSystems.size()];
    int i = 0;
    for (Iterator iter = trustedSystems.iterator(); iter.hasNext();)
    {
      TicketIssuerCertificateModel element = (TicketIssuerCertificateModel) iter.next();
      ticketIssuers[i] = element.convertToCompositeData();
      i++;
    }

    TicketIssuersInfo result = new TicketIssuersInfoImpl(TicketIssuersInfo.ACTION_STATUS_OK, ticketIssuers);
    return result;
  }


  /**
   *
   * @param key
   * @param optionsMap
   * @param ticketCertificates
   * @param caCerts
   * @return
   * @throws Exception
   */
  private final TicketIssuerCertificateModel findTrustedSystem(String key, Map optionsMap, Map ticketCertificates, Map caCerts)
      throws Exception
  {

    final String SIGNATURE = "findTrustedSystem(String, Map, Map, Map)";

    Matcher matcher = trustedKeyPattern.matcher(key);
    if (!matcher.matches())
    {
      // this is not sso2 option. Other options is ignored
      return null;
    }

    String type = matcher.group(1);
    if (!"sys".equals(type))
    {
      // this is not trustedsys option.
      return null;
    }

    String index = matcher.group(2);

    String trustedsys = (String)optionsMap.get(TRUSTED_SYSTEM + index);
    String trusteddn = (String)optionsMap.get(TRUSTED_SYSTEM_DN + index);
    String trustediss = (String)optionsMap.get(TRUSTED_ISSUER_DN + index);

    if (trustedsys == null)
    {
      return null;
    }
    else
    {
      Matcher sysMatcher = trustedSystemValuePattern.matcher(trustedsys);

      // check correct trustedsys sintax
      if (!sysMatcher.matches())
      {
        // Omit this system, due to incorrect trustedsys option syntax
        if (TRACE.beDebug())
        {
          TRACE.debugT(SIGNATURE, "Omit system: \"{0}\", due to incorrect trustedsys option syntax.", new Object[]{trustedsys});
        }
        return null;
      }

      // standardize subject and issuer DN
      if (trusteddn == null)
      {
        trusteddn = "";
      }
      else
      {
        trusteddn = trusteddn.trim();
      }
      if (trustediss == null)
      {
        trustediss = "";
      }
      else
      {
        trustediss = trustediss.trim();
      }

      CertificateInfoModel certModel = findCertificate(ticketCertificates, trusteddn, trustediss);

      CertificateInfo certInfo;

      if (trusteddn.length() < 1 || trustediss.length() < 1 || certModel == null)
      {
        // this is uncorrect trusted system	- return trusted system with empty certificate and status NOT FOUND
        certInfo = new CertificateInfoImpl("", trusteddn, trustediss, "", CertificateInfo.CERTIFICATE_STATUS_NOT_FOUND);
      }
      else
      {
        certInfo = certModel.convertToCompositeData(caCerts);
      }

      // extract system id and system client from the trustedsys option
      String sysId = sysMatcher.group(1);
      String sysClient = sysMatcher.group(2);

      TicketIssuerCertificateModel result = new TicketIssuerCertificateModel(sysId, sysClient, TicketIssuerCertificate.TICKET_ISSUER_STATUS_OK);
      String certificateKey = trusteddn + trustediss;
      result.putCertificate(certificateKey, certInfo);

      return result;
    }
  }


  private CertificateInfoModel findCertificate (Map ticketCertificates, String trusteddn, String trustediss)
  {
    final String SIGNATURE = "findCertificate (Map, String, String)";

    // standardize subject and issuer DN
    if (trusteddn == null)
    {
      trusteddn = "";
    }
    else
    {
      trusteddn = trusteddn.trim();
    }
    if (trustediss == null)
    {
      trustediss = "";
    }
    else
    {
      trustediss = trustediss.trim();
    }

    String certificateKey = trusteddn + trustediss;

    if (ticketCertificates.get(certificateKey) != null)
    {
      List certList = (List)ticketCertificates.get(certificateKey);
      CertificateInfoModel certModel = (CertificateInfoModel)certList.get(0);

      return certModel;
    }
    else
    {
      Properties syntax= new Properties();
      syntax.setProperty("jndi.syntax.direction", "right_to_left");
      syntax.setProperty("jndi.syntax.escape", "\\");
      syntax.setProperty("jndi.syntax.trimblanks", "true");
      syntax.setProperty("jndi.syntax.separator", ",");

      // try to find certificate with compound compare
      try
      {
        CompoundName compOption= new CompoundName(certificateKey, syntax);

        Set keys = ticketCertificates.keySet();
        for (Iterator iter = keys.iterator(); iter.hasNext();)
        {
          String key = (String) iter.next();
          CompoundName compCert = new CompoundName(key, syntax);

          if (compOption.equals(compCert))
          {
            List certList = (List)ticketCertificates.get(key);
            CertificateInfoModel certModel = (CertificateInfoModel)certList.get(0);

            return certModel;
          }
        }
      }
      catch (Exception err)
      {
        TRACE.warningT(SIGNATURE, "Failed to perform compound based search for corresponding certificate. Reason: {0}",
            new Object[]{err.toString()});
      }

      return null;
    }
  }


  /**
   *
   * @param issuers
   * @return
   */
  private MapEntry[] convertTicketIssuerToMapEntries(TicketIssuerCertificate[] issuers)
      throws Exception
  {

    // every systems use 3 options. The last options is ume.configuration.active
    int optionsSize = issuers.length * 3 + 1;

    MapEntry[] options = new MapEntry[optionsSize];

    for (int i = 0; i < issuers.length; i++)
    {
      TicketIssuerCertificate issuer = issuers[i];
      String sysId = issuer.getSystemId();
      String clientId = issuer.getClientId();
      CertificateInfo[] certificates = issuer.getCertificates();

      if (sysId == null || sysId.length() < 1)
      {
        throw new IllegalArgumentException("There is not system id specified in the TicketIssuerCertificate[" + i + "]");
      }
      if (clientId == null || clientId.length() < 1)
      {
        throw new IllegalArgumentException("There is not client id specified in the TicketIssuerCertificate[" + i + "]");
      }
      if (certificates == null || certificates.length < 1)
      {
        throw new IllegalArgumentException("There is not certificate specified in the TicketIssuerCertificate[" + i + "]");
      }
      if (certificates.length > 1)
      {
        throw new IllegalArgumentException("Certificates array must have only one element in the TicketIssuerCertificate[" + i + "]");
      }

      CertificateInfo certificate = certificates[0];
      if (certificate == null)
      {
        throw new IllegalArgumentException("There is not certificate specified in the TicketIssuerCertificate[" + i + "]");
      }

      String trusteddn = certificate.getCertificateSubject();
      String trustediss = certificate.getCertificateIssuer();
      String trustedsys = sysId + "," + clientId;

      int optionInitialIndex = i * 3;
      int trustedSystemIndex = i + 1;

      try
      {
        options[optionInitialIndex + 0] = new MapEntryImpl(TRUSTED_SYSTEM + trustedSystemIndex, trustedsys);
        options[optionInitialIndex + 1] = new MapEntryImpl(TRUSTED_SYSTEM_DN + trustedSystemIndex, trusteddn);
        options[optionInitialIndex + 2] = new MapEntryImpl(TRUSTED_ISSUER_DN + trustedSystemIndex, trustediss);
      }
      catch (Exception err)
      {
        SimpleLogger.traceThrowable(Severity.ERROR, TRACE, err, "ASJ.secsrv.009517", "Failed to create MapEntries");
        throw err;
      }
    }

    // add ume.configuration.active=true
    options[options.length - 1] = new MapEntryImpl(UME_CONFIGUARATION_ACTIVE, "true");

    return options;
  }


  private boolean isMode(int mode, int checkedValue)
  {
    return (mode & checkedValue) == mode;
  }


  /**
   * Returns all ETLMs and EATLMs found in the Userstore
   *
   * @return
   * @throws Exception
   */
  private LoginModule[] getUserstoreSSO2LMs()
      throws Exception
  {
    List sso2LMs = new ArrayList();
    // process all entries in the US with either ETLM or EATLM classname
    LoginModule[] userStoreLMs = authMBean.getLoginModules();

    for (int i = 0; i < userStoreLMs.length; i++)
    {
      String loginModuleClassName = userStoreLMs[i].getClassName();

      if (EVALUATE_TICKET_LM.equals(loginModuleClassName) ||
          EVALUATE_ASSERTION_TICKET_LM.equals(loginModuleClassName))
      {
        sso2LMs.add(userStoreLMs[i]);
      }
    }

    LoginModule[] result = (LoginModule[])sso2LMs.toArray(new LoginModule[sso2LMs.size()]);

    return result;
  }


  /**
   * Check availability of ETLM and EATLM in the Userstore
   *
   * @return true if at least one ETLM and at least one EATLM are found in the Userstore
   */
  private boolean isExistsUserstoreSSO2LMs()
      throws Exception
  {
    final String SIGNATURE = "isExistsUserstoreSSO2LMs()";

    boolean isFoundETLM = false;
    boolean isFoundEATLM = false;

    LoginModule[] userStoreLMs = getUserstoreSSO2LMs();
    for (int i = 0; i < userStoreLMs.length; i++)
    {
      String loginModuleClassName = userStoreLMs[i].getClassName();

      if (EVALUATE_TICKET_LM.equals(loginModuleClassName))
      {
        isFoundETLM = true;
      }
      else if (EVALUATE_ASSERTION_TICKET_LM.equals(loginModuleClassName))
      {
        isFoundEATLM = true;
      }
    }

    if (!isFoundETLM)
    {
      TRACE.warningT(SIGNATURE, "EvaluateTicketLoginModule was not found in the Userstore");
    }
    else if (!isFoundEATLM)
    {
      TRACE.warningT(SIGNATURE, "EvaluateAssertionTicketLoginModule was not found in the Userstore");
    }

    boolean result = isFoundETLM && isFoundEATLM;
    return result;
  }


  /**
   * Check availability of ETLM in the given <code>loginModules</code> array
   *
   * @return true if at least one ETLM is found
   */
  private boolean isExistsETLM(LoginModule[] loginModules)
  {
    final String SIGNATURE = "isExistsETLM(LoginModule[])";

    for (int i = 0; i < loginModules.length; i++)
    {
      String loginModuleClassName = loginModules[i].getClassName();

      if (EVALUATE_TICKET_LM.equals(loginModuleClassName))
      {
        return true;
      }
    }

    TRACE.warningT(SIGNATURE, "EvaluateTicketLoginModule was not found. Login Modules: " + loginModules);
    return false;
  }


  /**
   * Check availability of EATLM in the given <code>loginModules</code> array
   *
   * @return true if at least one EATLM is found
   */
  private boolean isExistsEATLM(LoginModule[] loginModules)
  {
    final String SIGNATURE = "isExistsEATLM(LoginModule[])";

    for (int i = 0; i < loginModules.length; i++)
    {
      String loginModuleClassName = loginModules[i].getClassName();

      if (EVALUATE_ASSERTION_TICKET_LM.equals(loginModuleClassName))
      {
        return true;
      }
    }

    TRACE.warningT(SIGNATURE, "EvaluateAssertionTicketLoginModule was not found. Login Modules: " + loginModules);
    return false;
  }


  /**
   * Check identical option sets of the given <code>loginModules</code> array
   *
   * @return true if the given Login modules have equals option sets
   */
  private boolean hasEqualOptions(LoginModule[] loginModules)
  {
    Map previousOptions = null;

    for (int i = 0; i < loginModules.length; i++)
    {
      // get LM options as a Map
      MapEntry[] optionArray = loginModules[i].getOptions();
      Map options = AuthUtil.convertMapEntriesToMap(optionArray);

      if (i > 0 && !options.equals(previousOptions))
      {
        if (TRACE.beDebug()) 
        {
          TRACE.debugT("Login module: {0} in the Userstore has different options from the other EvaluateTicketLoginModule and EvaluateAssertionTicketLoginModules", 
                       new Object[]{loginModules[i]});
        }
        return false;
      }

      previousOptions = options;
    }

    return true;
  }


  private boolean match(String issuerDN, String systemDN, X509Certificate cert) {
    if (cert == null) {
      return false;
    }

    if (!cert.getIssuerDN().getName().equals(issuerDN)) {
      return false;
    }

    if (!cert.getSubjectDN().getName().equals(systemDN)) {
      return false;
    }

    //TODO improve this!
    return true;
  }


  private int getACLNumber(String searchedSID, String searchedClient, LoginModule evaluateLM) {
    Map options = AuthUtil.convertMapEntriesToMap(evaluateLM.getOptions());

    for (Iterator keys = options.keySet().iterator(); keys.hasNext();) {
      String key = (String) keys.next();

      if (!key.startsWith(TRUSTED_SYSTEM)) {
        continue;
      }

      String system = (String) options.get(key);
      int pos = system.indexOf(','); //<SID>, <Client>

      String SID;
      String client;

      if (pos == -1) {
        SID = system.trim();
        client = "";
      } else {
        SID = system.substring(0, pos).trim();
        client = system.substring(pos + 1).trim();
      }

      if (!SID.equals(searchedSID) || !client.equals(searchedClient)) {
        continue;
      }

      int number;

      try {
        number = Integer.parseInt(key.substring(TRUSTED_SYSTEM.length()));
      } catch (Exception ex) {
        SimpleLogger.traceThrowable(Severity.ERROR, TRACE, ex, "ASJ.secsrv.009518", "Failed to get number from {0}", key);
        continue;
      }


      if (options.containsKey(TRUSTED_ISSUER_DN + number)
          && options.containsKey(TRUSTED_SYSTEM_DN + number)) {
//      TODO log something or???
        return number;
      }
    }

    return -1;
  }


  private boolean hasCertificate(X509Certificate searchedCert, boolean removeIfFoundBinaryDifferent) throws Exception {
    if (searchedCert == null) {
      return false;
    }

    String issuerDN = searchedCert.getIssuerDN().getName();
    String subjectDN = searchedCert.getSubjectDN().getName();
    byte[] searchedBF;

    try {
      searchedBF = searchedCert.getEncoded();
    } catch (CertificateEncodingException e1) {
      SimpleLogger.traceThrowable(Severity.ERROR, TRACE, e1, "ASJ.secsrv.009519", "Failed to get encoded bytes of {0}", searchedCert);
      throw new Exception(CERTIFICATE_SYNTAX_ERROR);
    }

    boolean result = false;

    try {
      String keyPairAlias = getUMEPropertyAsString(ILoginConstants.SSOTICKET_KEYALIAS, DEFAULT_KEYPAIR_ALIAS);

      for (Enumeration en = getTicketKeystoreAliases(); en.hasMoreElements(); ) {
        String alias = (String) en.nextElement();

        if (isCertificateEntry(alias)) {
          X509Certificate cert = getCertificate(alias);

          int found = match(cert, issuerDN, subjectDN, searchedBF);

          if (found == CERT_FOUND_BINARY_DIFF) {
            if (alias.startsWith(keyPairAlias)) {
              throw new Exception(CERTIFICATE_REPLACE_NOT_ALLOWED);
            } else {
              if (removeIfFoundBinaryDifferent) {
                deleteEntry(alias);
              } else {
                throw new Exception(CERTIFICATE_ALREADY_EXISTS);
              }
            }
          }

          if (found == CERT_FOUND) {
            result = true;
          }
        }

        if (isKeyEntry(alias)) {
          java.security.cert.Certificate[] trustCerts = getCertificateChain(alias);

          for (int i = 0; i < trustCerts.length; i++) {
            if (trustCerts[i] instanceof X509Certificate) {
              int found = match((X509Certificate) trustCerts[i], issuerDN, subjectDN, searchedBF);

              if (found == CERT_FOUND_BINARY_DIFF) {
                if (alias.startsWith(keyPairAlias)) {
                  throw new Exception(CERTIFICATE_REPLACE_NOT_ALLOWED);
                } else {
                  if (removeIfFoundBinaryDifferent) {
                    deleteEntry(alias);//TODO is this correct
                  } else {
                    throw new Exception(CERTIFICATE_ALREADY_EXISTS);
                  }
                }
              }

              if (found == CERT_FOUND) {
                result = true;
              }
            } else {
              //TODO???
            }
          }
        }

      }
    } catch (KeyStoreException e) {
      SimpleLogger.traceThrowable(Severity.ERROR, TRACE, e, "ASJ.secsrv.009520", "Failed to search for certificate: {0}", searchedCert);
      throw e;
    }

    return result;
  }

  private int match(X509Certificate cert, String issuerDN, String subjectDN, byte[] searchedBF) throws Exception {
    if (cert == null || issuerDN == null || subjectDN == null || searchedBF == null) {
      return CERT_NOT_FOUND;
    }

    if (!issuerDN.equals(cert.getIssuerDN().getName())
        || !subjectDN.equals(cert.getSubjectDN().getName())) {
      return CERT_NOT_FOUND;
    }

    byte[] certBF;

    try {
      certBF = cert.getEncoded();
    } catch (CertificateEncodingException e) {
      SimpleLogger.traceThrowable(Severity.ERROR, TRACE, e, "ASJ.secsrv.009521", "Failed to check {0}", cert);
      throw e;
    }

    if (searchedBF.length != certBF.length) {
      return CERT_FOUND_BINARY_DIFF;
    }

    for (int i = 0; i < searchedBF.length; i++) {
      if (searchedBF[i] != certBF[i]) {
        return CERT_FOUND_BINARY_DIFF;
      }
    }

    return CERT_FOUND;
  }

  private TicketIssuerImpl getACL(Map lmOptions, String key) throws Exception {
    if (!key.startsWith(TRUSTED_SYSTEM)) {
      return null;
    }

    int number;

    try {
      number = Integer.parseInt(key.substring(TRUSTED_SYSTEM.length()));
    } catch (Exception ex) {
      SimpleLogger.traceThrowable(Severity.ERROR, TRACE, ex, "ASJ.secsrv.009522", "Failed to get number from {0}", key);
      return null;
    }

    String system = (String) lmOptions.get(key);
    String missing = null;
    String issuerDNKey = TRUSTED_ISSUER_DN + number;
    String subjectDNKey = TRUSTED_SYSTEM_DN + number;

    if (!lmOptions.containsKey(issuerDNKey)) {
      missing = issuerDNKey;
    }

    if (!lmOptions.containsKey(subjectDNKey)) {
      if (missing == null) {
        missing = subjectDNKey;
      } else {
        missing += " and " + subjectDNKey;
      }
    }

    if (missing != null) {
      SimpleLogger.trace(Severity.ERROR, TRACE, "ASJ.secsrv.009523", "Missing options {0} for {1}", new Object[] {missing, key});
      //TODO - is it correct to skip if some of the options are missing?
      return null;
    }

    String issuerDN = (String) lmOptions.get(TRUSTED_ISSUER_DN + number);
    String systemDN = (String) lmOptions.get(TRUSTED_SYSTEM_DN + number);

    int pos = system.indexOf(','); //<SID>, <Client>
    String SID;
    String client;

    if (pos == -1) {
      SID = system.trim();
      client = "";
    } else {
      SID = system.substring(0, pos).trim();
      client = system.substring(pos + 1).trim();
    }

    X509Certificate cert;

    try {
      cert = getCertificate(issuerDN, systemDN);
    } catch (KeyStoreException e) {
      SimpleLogger.traceThrowable(Severity.ERROR, TRACE, e, "ASJ.secsrv.009524", "Failed to get certificate for {0}", SID);
      throw e;
    }

    if (cert == null) {
      SimpleLogger.trace(Severity.ERROR, TRACE, "ASJ.secsrv.009525", "Missing certificate for {0}", key);
      return null;// TODO maybe throw exception
    }

    TicketIssuerImpl info = new TicketIssuerImpl(SID, client, systemDN, issuerDN);
    return info;
  }

  private ArrayList getACL(Map lmOptions) throws Exception {
    ArrayList list = new ArrayList();

    for (Iterator it = lmOptions.keySet().iterator(); it.hasNext();) {
      TicketIssuerImpl info = getACL(lmOptions, (String) it.next());

      if (info == null) {
        continue;
      }

      list.add(info);
    }

    return list;
  }

  /**
   *
   * @param ticketLM
   * @param assertionTicketLM
   * @return
   * @throws Exception
   */
  private TicketIssuer[] consistencyCheck(LoginModule ticketLM, LoginModule assertionTicketLM) throws Exception {
    if (ticketLM == null || assertionTicketLM == null) {
      return null;
    }

    MapEntry[] ticketMapEntry = ticketLM.getOptions();
    MapEntry[] assertionMapEntry = assertionTicketLM.getOptions();

    if (ticketMapEntry == null && assertionMapEntry == null) {
      return null;
    }

    if (ticketMapEntry == null || assertionMapEntry == null
        || ticketMapEntry.length != assertionMapEntry.length) {
      throw new Exception(CONSISTENCY_CHECK_FAILED);
    }

    Map ticketOptions = AuthUtil.convertMapEntriesToMap(ticketMapEntry);
    Map assertionOptions = AuthUtil.convertMapEntriesToMap(assertionMapEntry);


    ArrayList listTicket = getACL(ticketOptions);
    ArrayList listAssertion = getACL(assertionOptions);

    if (!listTicket.containsAll(listAssertion) || !listAssertion.containsAll(listTicket)) {
      throw new Exception(CONSISTENCY_CHECK_FAILED);
    }

    TicketIssuer[] ret = new TicketIssuer[listTicket.size()];
    listTicket.toArray(ret);
    return ret;
  }

  private X509Certificate getCertificate(String alias) throws KeyStoreException {
    return (X509Certificate) invokeKeystoreMethod(KEYSTORE_GET_CERTIFICATE, alias);
  }

  private boolean isCertificateEntry(String alias) throws KeyStoreException {
    Boolean ret = (Boolean) invokeKeystoreMethod(KEYSTORE_IS_CERTIFICATE_ENTRY, alias);
    return ret.booleanValue();
  }

  private Enumeration getTicketKeystoreAliases() throws KeyStoreException {
    return (Enumeration) invokeKeystoreMethod(KEYSTORE_ALIASES, null);
  }

  private void deleteEntry(String alias) throws KeyStoreException {
    invokeKeystoreMethod(KEYSTORE_DELETE_ENTRY, alias);
  }

  private boolean isKeyEntry(final String alias) throws KeyStoreException {
    Boolean ret = (Boolean) invokeKeystoreMethod(KEYSTORE_IS_KEY_ENTRY, alias);
    return ret.booleanValue();
  }

  private Certificate[] getCertificateChain(String alias) throws KeyStoreException {
    return (Certificate[]) invokeKeystoreMethod(KEYSTORE_GET_CERTIFICATE_CHAIN, alias);
  }

  private boolean containsAlias(String alias) throws KeyStoreException {
    Boolean ret = (Boolean) invokeKeystoreMethod(KEYSTORE_CONTAINS_ALIAS, alias);
    return ret.booleanValue();
  }

  private void setCertificateEntry(String alias, Certificate signingCert) throws KeyStoreException {
    invokeKeystoreMethod(KEYSTORE_SET_CERTIFICATE_ENTRY, alias, signingCert);
  }

  private static KeyStore getKeystore(final String alias) throws RemoteException, KeyStoreException {
    KeyStore ret = null;

    try {
      ret = (KeyStore) AccessController.doPrivileged(new PrivilegedExceptionAction() {
        public Object run() throws KeyStoreException, RemoteException {
          return KEYSTORE_MANAGER.getKeystore(alias);
        }
      });
    } catch (PrivilegedActionException e) {
      Exception realEx = e.getException();

      if (realEx instanceof KeyStoreException) {
        throw (KeyStoreException) realEx;
      }

      throw (RemoteException) realEx;
    }

    return ret;

  }

  private Object invokeKeystoreMethod(final int type, final String alias) throws KeyStoreException {
    return invokeKeystoreMethod(type, alias, null);
  }

  private Object invokeKeystoreMethod(final int type, final String alias, final Certificate signingCert) throws KeyStoreException {
    Object ret = null;

    try {
      ret = AccessController.doPrivileged(new PrivilegedExceptionAction() {
        public Object run() throws KeyStoreException {
          KeyStore ks = getKeyStore();
          switch (type) {
            case KEYSTORE_DELETE_ENTRY:
              ks.deleteEntry(alias);
              return null;

            case KEYSTORE_GET_CERTIFICATE:
              return (X509Certificate) ks.getCertificate(alias);

            case KEYSTORE_GET_CERTIFICATE_CHAIN:
              return ks.getCertificateChain(alias);

            case KEYSTORE_ALIASES:
              return ks.aliases();

            case KEYSTORE_IS_CERTIFICATE_ENTRY:
              return Boolean.valueOf(ks.isCertificateEntry(alias));

            case KEYSTORE_IS_KEY_ENTRY:
              return Boolean.valueOf(ks.isKeyEntry(alias));

            case KEYSTORE_CONTAINS_ALIAS:
              return Boolean.valueOf(ks.containsAlias(alias));

            case KEYSTORE_SET_CERTIFICATE_ENTRY:
              ks.setCertificateEntry(alias, signingCert);
              return null;
          }

          return null;
        }
      });
    } catch (PrivilegedActionException e) {

      Exception realEx = e.getException();

      if (realEx instanceof KeyStoreException) {
        throw (KeyStoreException) e.getException();
      }

      SimpleLogger.traceThrowable(Severity.ERROR, TRACE, realEx, "ASJ.secsrv.009526", "Unexpected exception");
    }

    return ret;
  }

  private static String getUMEPropertyAsString(String property, String defValue){
    return InternalUMFactory.getConfiguration().getStringDynamic(property, defValue);
  }

  private KeyStore getKeyStore() throws KeyStoreException {
    try{
      String alias = getUMEPropertyAsString(ILoginConstants.SSOTICKET_KEYSTORE, DEFAULT_KEYSTORE_VIEW);
      return getKeystore(alias);
    } catch (Exception e) {
      SimpleLogger.traceThrowable(Severity.ERROR, TRACE, e, "ASJ.secsrv.009527", "Failed to get ticket keystore. Exception message {0}", new Object[]{e.getMessage()});
      throw new KeyStoreException(e);
    }
  }

  private static final int KEYSTORE_DELETE_ENTRY = 1;
  private static final int KEYSTORE_GET_CERTIFICATE = 2;
  private static final int KEYSTORE_GET_CERTIFICATE_CHAIN = 3;
  private static final int KEYSTORE_ALIASES = 4;
  private static final int KEYSTORE_IS_CERTIFICATE_ENTRY = 5;
  private static final int KEYSTORE_IS_KEY_ENTRY = 6;
  private static final int KEYSTORE_CONTAINS_ALIAS = 7;
  private static final int KEYSTORE_SET_CERTIFICATE_ENTRY = 8;


}
