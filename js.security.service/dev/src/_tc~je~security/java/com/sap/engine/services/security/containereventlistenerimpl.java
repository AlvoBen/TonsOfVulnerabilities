/**
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security;

import iaik.asn1.ObjectID;
import iaik.asn1.structures.AlgorithmID;
import iaik.asn1.structures.Name;
import iaik.x509.X509Certificate;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.InitialContext;

import com.sap.engine.frame.container.event.ContainerEventListenerAdapter;
import com.sap.engine.frame.core.locking.ServerInternalLocking;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.interfaces.cross.CrossInterface;
import com.sap.engine.interfaces.keystore.KeystoreManager;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.shell.ShellInterface;
import com.sap.engine.services.deploy.container.ContainerManagement;
import com.sap.engine.services.deploy.container.DeployCommunicator;
import com.sap.engine.services.security.command.Commands;
import com.sap.engine.services.security.jmx.auth.AuthenticationManagerMBean;
import com.sap.engine.services.security.jmx.auth.impl.AuthenticationManager;
import com.sap.engine.services.security.jmx.sso2.impl.J2EESSO2Management;
import com.sap.engine.services.security.login.Signer;
import com.sap.engine.services.security.remote.RemoteSecurity;
import com.sap.engine.services.security.remoteimpl.RemoteSecurityImpl;
import com.sap.engine.services.security.server.SecurityContextImpl;
import com.sap.engine.services.security.server.deploy.LoginModuleContainer;
import com.sap.engine.services.security.server.deploy.PolicyConfigurationContainer;
import com.sap.engine.services.security.server.deploy.PolicyConfigurationContainerOld;
import com.sap.engine.services.security.server.jaas.CertRevocServiceWrapper;
import com.sap.jmx.ObjectNameFactory;
import com.sap.security.api.logon.ILoginConstants;
import com.sap.security.core.InternalUMFactory;
import com.sap.security.core.server.jaas.SAPLogonTicketHelper;
import com.sap.security.core.ticket.imp.BufferingTicket;
import com.sap.security.core.util.config.IUMConfiguration;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;


/**
 *  Listener for services or interfaces in the container that the security service needs to
 * register with or use.
 *
 * @author Stephan Zlatarev
 * @version 6.30
 */
public class ContainerEventListenerImpl extends ContainerEventListenerAdapter {

  public static final String INTERFACE_CONTAINER = "container";
  public final static String INTERFACE_CROSS = "cross";
  public final static String SERVICE_KEYSTORE = "keystore";
  public final static String INTERFACE_KEYSTORE = "keystore_api";
  public final static String INTERFACE_LOG = "log";
  public final static String SERVICE_NAMING = "naming";
  public final static String SERVICE_JMX = "jmx";
  public final static String INTERFACE_SHELL = "shell";
  public final static String INTITIAL_OBJECT_SECURITY = "security";
  public final static String JNDI_BINDING_SECURITY = "remotesecurity";
  public static final String AUTHENTICATION_MANAGER = "authenticationManager";
  public static final String CERT_REVOC_API = "tc~sec~certrevoc~interface";
  
  private static final String DEFAULT_KEYSTORE_VIEW = "TicketKeystore";
  private static final String DEFAULT_KEYPAIR_ALIAS = "SAPLogonTicketKeypair";

  private LoginModuleContainer securityContainer;  
  private PolicyConfigurationContainer policyConfigurationContainer;
  //this field is temporary and will be removed later on
  private PolicyConfigurationContainerOld oldPolicyConfigurationContainer;
  private DeployCommunicator communicator;
  private ContainerManagement deployService;
  private SecurityContext root;
  private RemoteSecurity remote = null;
  private SecurityServerFrame securityFrame = null;
  private Commands commands = null;
  private KeystoreManager keystore = null;
  private static final Location location = Location.getLocation(ContainerEventListenerImpl.class);
  

  public ContainerEventListenerImpl(SecurityServerFrame securityFrame, SecurityContext root) {
    try {
      this.securityFrame = securityFrame;
      this.root = root;
      this.remote = new RemoteSecurityImpl(root);
      this.securityContainer = SecurityServerFrame.getLoginModuleContainer();
      this.policyConfigurationContainer = SecurityServerFrame.getPolicyConfigurationContainer();
      this.oldPolicyConfigurationContainer = SecurityServerFrame.getOldPolicyConfigurationContainer();
    } catch (Exception e) {
      Util.SEC_SRV_LOCATION.traceThrowableT(Severity.INFO, "ContainerEventListenerImpl", e);
    }
  }

  public RemoteSecurity getRemoteSecurity() {
    return remote;
  }

  public void serviceStarted(String serviceName, Object serviceInterface) {
    if (serviceName.equals(SERVICE_NAMING)) {
      try {
        new InitialContext().bind(JNDI_BINDING_SECURITY, remote);
      } catch (Exception e) {
        SimpleLogger.traceThrowable(Severity.ERROR, location, e, "ASJ.secsrv.000144", "{0}", new Object[]{e.getMessage()});
      }
    } else if (serviceName.equals(SERVICE_JMX)) {
      registerAuthenticationManagerMBean((MBeanServer) serviceInterface);
    }
  }


  protected void setKeystore(KeystoreManager keystore) {
    int attempts = 0;
    boolean isSuccesfull = false;

    while (attempts++ < 180) {
      try {
        createTicketKeyStore(keystore);
        isSuccesfull = true;
        break;
      } catch (Exception e) {
        synchronized (this) {
          try {
            this.wait(1000);
          } catch (InterruptedException e1) {
            //$JL-EXC$
          }
        }
      }
    }

    if (!isSuccesfull) {
      SimpleLogger.trace(Severity.ERROR, location, "ASJ.secsrv.000145", "System view [TicketKeystore] has not been created.");
    } else {
      location.logT(Severity.INFO, "Fully operational view [TicketKeystore] has been created, after [" + attempts + "] attempt(s)");
    }
  }


  /**
   * Method to create keystore view with a private DSA keypair.
   * The length of the private key is 1024, algorithm DSA and validity
   * 2 years.
   *
   * @param keystore
   * @throws Exception
   */
  private void createTicketKeyStore(KeystoreManager keystore) throws Exception {
    lockKeystore();
    try {
      if (!keystore.existKeystoreView(DEFAULT_KEYSTORE_VIEW)) {
        keystore.createKeystoreView(DEFAULT_KEYSTORE_VIEW, null);
      } else {
        return;
      }

      createAndStoreEntries(keystore.getKeystore(DEFAULT_KEYSTORE_VIEW));

      location.logT(Severity.INFO, "working TicketKeystore view created");
    } catch (Exception e) {
      keystore.destroyKeystoreView(DEFAULT_KEYSTORE_VIEW);
      throw e;
    } finally {
      releaseLockKeystore();
    }
  }

  private static final void createAndStoreEntries(KeyStore keystoreView) throws NoSuchAlgorithmException, InvalidKeyException, KeyStoreException, CertificateException {
    KeyPairGenerator generator = KeyPairGenerator.getInstance("DSA");
    generator.initialize(1024);
    KeyPair myKeyPair = generator.generateKeyPair();

    // create emty certificate
    X509Certificate cert = new X509Certificate();

    // set validity to 2 years from now on
    GregorianCalendar calendar= new GregorianCalendar();
    cert.setValidNotBefore(calendar.getTime());
    calendar.add(Calendar.YEAR, 20);
    cert.setValidNotAfter(calendar.getTime());

    // set subject and issuer
    Name subjectName= new Name();

    subjectName.addRDN(ObjectID.commonName, SecurityContextImpl.getSystemID());
    subjectName.addRDN(ObjectID.organizationalUnit, "J2EE");
  
    cert.setSubjectDN(subjectName);
    cert.setIssuerDN(subjectName);

    // set public key and selfsigned certificate
    cert.setPublicKey(myKeyPair.getPublic());
    cert.setSerialNumber(BigInteger.ZERO);
    cert.sign(AlgorithmID.dsaWithSHA, myKeyPair.getPrivate());

    keystoreView.setKeyEntry(DEFAULT_KEYPAIR_ALIAS, myKeyPair.getPrivate(), "".toCharArray(), new X509Certificate[] { cert });
    keystoreView.setCertificateEntry(DEFAULT_KEYPAIR_ALIAS + "-cert", cert);
  }

     
  private void lockKeystore() throws SecurityException {
    long waitLimit = 180000;
    long waitInterval = 1000;
    long beginTime = System.currentTimeMillis();

    while (System.currentTimeMillis() <= beginTime + waitLimit) {
      try {
        SecurityServerFrame.internalLock.lock("$service.security", "ticket_keystore", ServerInternalLocking.MODE_EXCLUSIVE_NONCUMULATIVE);
        return;
      } catch (Exception e) {
        synchronized(this) {
          try {
            this.wait(waitInterval);
          } catch (InterruptedException ex) {
            throw new SecurityException("Interrupted while waiting for ticket_keystore lock!", ex);
          }
        }
      }
    }
    throw new SecurityException("Unable to obtain ticket_keystore lock!");
  }

  private void releaseLockKeystore() throws TechnicalLockException {
    SecurityServerFrame.internalLock.unlock("$service.security", "ticket_keystore", ServerInternalLocking.MODE_EXCLUSIVE_NONCUMULATIVE);
  }

  public void serviceStopped(String serviceName) {
    if (serviceName.equals(SERVICE_KEYSTORE)) {
      Signer.keystoreStopped();
      keystore = null;

      if (commands != null) {
        commands.setKeystore(keystore);
      }
    }
  }
  
  private void initBufferingTicket(KeystoreManager keystoreManager) {
    IUMConfiguration umeConfig = InternalUMFactory.getConfiguration();
    String keystoreName = umeConfig.getStringDynamic(ILoginConstants.SSOTICKET_KEYSTORE, DEFAULT_KEYSTORE_VIEW);

	try {
	    KeyStore ticketKeystore = keystoreManager.getKeystore(keystoreName);
	    BufferingTicket.initialize(ticketKeystore);
	
	    location.infoT("Successfully read contents of SAP logon ticket " +
	        "keystore view and initialized SAP logon ticket functionality of UME."
	    );
	}
	catch(Exception e) {
	    SimpleLogger.traceThrowable(Severity.ERROR, location, e, "ASJ.secsrv.000146", "An error occurred while accessing " +
          "keystore view ''{0}''. UME TicketVerifier cannot be initialized.\n" +
          "Note: This is only relevant for applications verifying SAP logon " +
          "tickets, but not for SAP logon ticket handling of SAP J2EE Engine " +
          "in general.", new Object[]{keystoreName});
	}  	
  }

  public void interfaceAvailable(String interfaceName, Object interfaceImpl) {
    if (interfaceName.equals(INTERFACE_CONTAINER)) {
      deployService = (ContainerManagement)interfaceImpl;
      communicator = deployService.registerContainer(securityContainer.getContainerInfo().getName(), securityContainer);
      securityContainer.setDeployCommunicator(communicator);
      deployService.registerContainer(policyConfigurationContainer.getContainerInfo().getName(), policyConfigurationContainer);
      //oldPolicyConfigurationContainer container is temporary registered
      deployService.registerContainer(oldPolicyConfigurationContainer.getContainerInfo().getName(), oldPolicyConfigurationContainer);
      securityFrame.setDeployService(deployService);
    } else if (interfaceName.equals(INTERFACE_SHELL)) {
      commands = new Commands(root, (ShellInterface) interfaceImpl);
      if (keystore != null) {
        commands.setKeystore(keystore);
      }
    } else if (interfaceName.equals(INTERFACE_CROSS)) {
      ((CrossInterface) interfaceImpl).setInitialObject(INTITIAL_OBJECT_SECURITY, remote);
    } else if (interfaceName.equals(INTERFACE_KEYSTORE)) {
      Signer.keystoreStarted();

      keystore = (KeystoreManager) interfaceImpl;
      setKeystore(keystore);
      initBufferingTicket(keystore);
      SAPLogonTicketHelper.setKeyStoreManager(keystore);
      J2EESSO2Management.setKeyStoreManager(keystore);

      if (commands != null) {
        commands.setKeystore(keystore);
      }
    } else if (CERT_REVOC_API.equals(interfaceName)) {
      CertRevocServiceWrapper.interfaceIsAvailable(interfaceImpl);
    }
  }

  public void interfaceNotAvailable(String interfaceName) {
    if (CERT_REVOC_API.equals(interfaceName)) {
      CertRevocServiceWrapper.interfaceNotAvailable();
    }
  }


  public boolean setServiceProperty(String key, String value) {
    Properties serviceProperties = SecurityServerFrame.getServiceProperties();

    if (serviceProperties != null) {
      serviceProperties.setProperty(key, value);
      securityFrame.setServiceProperties(serviceProperties);
    }

    return true;
  }

  public boolean setServiceProperties(Properties serviceProperties) {
    securityFrame.setServiceProperties(serviceProperties);
    return true;
  }
  
  
  protected void registerAuthenticationManagerMBean(MBeanServer mbs) {
    try {
      ObjectName objectName = ObjectNameFactory
  				.getNameForServerChildPerNode(
  						ObjectNameFactory.SAP_J2EEServiceRuntimePerNode,
  						AUTHENTICATION_MANAGER,
  						ObjectNameFactory.EMPTY_VALUE,
  						ObjectNameFactory.EMPTY_VALUE);
  		
      if (!mbs.isRegistered(objectName)) {
  			AuthenticationManagerMBean mBean = new AuthenticationManager();
  			mbs.registerMBean(mBean, objectName);
        
        J2EESSO2Management.register(mbs, mBean);
  		} else {
  			location.debugT("AuthenticationManager is already registered!");
  		}
  	} catch (Exception e) {
  		location.traceThrowableT(Severity.DEBUG,
  				"Error while registering AuthenticationManager", e);
  	}
  }
}

