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
package com.sap.engine.services.iiop.server;

import com.sap.engine.admin.model.ManagementModelManager;
import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ApplicationServiceFrame;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.ServiceRuntimeException;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.frame.state.ManagementInterface;
import com.sap.engine.interfaces.cross.CrossInterface;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.lib.security.CSI.CSIAppConfigurationEntry;
import com.sap.engine.services.iiop.csiv2.EJBIORGenerator;
import com.sap.engine.services.iiop.internal.ORB;
import com.sap.engine.services.iiop.jmx.model.IiopManagement;
import com.sap.engine.services.iiop.jmx.model.IiopManagementMBean;
import com.sap.engine.services.iiop.jmx.model.SAP_ITSAMIiopManagementService;
import com.sap.engine.services.iiop.jmx.model.SAP_ITSAMIiopManagementServiceWrapper;
import com.sap.engine.services.iiop.jmx.model.SAP_ITSAMIiopManagementService_Impl;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import com.sap.engine.services.iiop.system.CommunicationLayer;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.security.auth.login.AppConfigurationEntry;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * This class starts CORBA service. It gets the ORB implementation and then creates
 * new CommunicationLayer.
 *
 * @author Georgy Stanev
 * @version 4.0
 */
public class CorbaServiceFrame implements ApplicationServiceFrame , ContainerEventListener {

  private ORB orb = null;
  private ApplicationServiceContext appContext;
  //holds the current server cluster ID

  private CommunicationLayerImpl layer;

//  private boolean waitAppCtx = false;
  private IIOPManagementInterface manInterface;

  private static Properties defaultProperties = null;
  private static int tempId;

  private static SecurityContext basicContext = null;
  private static SecurityContext iiopContext = null;
  private static ThreadSystem threadSystem = null;

  private MBeanServer mbs;
  private ObjectName iiopITSAMObjectName;
  private ObjectName iiopITSAMServerMBean;
  
  private ManagementModelManager mModelManager = null;
  /**
   * This method change service properties.
   *
   * @param   appContext  Server context passed by ServiceManager.
   * @exception   IllegalArgumentException  -  if the new properties are invalid.
   */

  public void start(ApplicationServiceContext appContext) throws ServiceException {
    this.appContext = appContext;
    threadSystem = appContext.getCoreContext().getThreadSystem();
    tempId = appContext.getClusterContext().getClusterMonitor().getCurrentParticipant().getClusterId();

    //InQMy ORB implementation
    CommunicationLayerImpl.setContext(appContext);
    org.omg.CORBA.ORB orbProxy = org.omg.CORBA.ORB.init();
    orb = new com.sap.engine.services.iiop.internal.ORB();
    ((com.sap.engine.system.ORBSingletonProxy) orbProxy).set_delegate(orb);
    orb.set_(new String[0], getDefaultProperties());
    layer = (CommunicationLayerImpl) CommunicationLayer.init("com.sap.engine.services.iiop.server.CommunicationLayerImpl", orb);
    EJBIORGenerator iorGen = new EJBIORGenerator(orb, layer);
    appContext.getContainerContext().getObjectRegistry().registerInterfaceProvider("csiv2", iorGen);
    layer.setEJBIORGenerator(iorGen);

    int mask = ContainerEventListener.MASK_INTERFACE_AVAILABLE | ContainerEventListener.MASK_SERVICE_STARTED | ContainerEventListener.MASK_BEGIN_SERVICE_STOP;
    Set names = new HashSet(4);
    names.add("cross");
    names.add("appcontext");
    names.add("security");
    names.add("basicadmin");
    appContext.getServiceState().registerContainerEventListener(mask, names, this);
    manInterface = new IIOPManagement();
    appContext.getServiceState().registerManagementInterface(manInterface);
  }

  public void stop() throws ServiceRuntimeException {
    try {
      unregisterITSAMMBean();
    } catch (Exception e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("CorbaServiceFrame.stop()", e.getMessage());
      }
    }
    manInterface.unregisterResources();
    appContext.getServiceState().unregisterManagementInterface();
    appContext.getServiceState().unregisterContainerEventListener();
    appContext.getContainerContext().getObjectRegistry().unregisterInterfaceProvider("csiv2");
    layer.stop();
    layer = null;
    org.omg.CORBA.ORB orbProxy = org.omg.CORBA.ORB.init();

    try {
      orbProxy.shutdown(true);
    } catch (Exception e) {
       if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("CorbaServiceFrame.stop()", e.getMessage());
      }
    }

    ((com.sap.engine.system.ORBSingletonProxy) orbProxy).set_delegate(null);
    orb.dropdown();
    orb = null;
    basicContext = null;
    iiopContext = null;
    threadSystem = null;
    defaultProperties = null;
    mModelManager = null;
  }

  /**
   * Returns the server's cluster ID.
   * @return  current server cluster ID.
   */
  public static int getClusterId() {
    return tempId;
  }

  public static Properties getDefaultProperties() {
    if (defaultProperties == null) {
      defaultProperties = new Properties();
      defaultProperties.put("org.omg.CORBA.ORBClass", "com.sap.engine.services.iiop.internal.ORB");
      defaultProperties.put("CommunicationLayerClass", "com.sap.engine.services.iiop.server.CommunicationLayerImpl");
    }

    return defaultProperties;
  }

  public void interfaceAvailable(String interfaceName, Object interfaceImpl) {
    if (interfaceName.equals("cross")) {
      layer.registerProvider((CrossInterface) interfaceImpl);
//    } else if (waitAppCtx && interfaceName.equals("appcontext")) {
//      orb.initExecutionContext(appContext);
//      waitAppCtx = false;
    } else if (interfaceName.equals("security")) {
      basicContext = (SecurityContext) interfaceImpl;
      try {
        iiopContext = basicContext.getPolicyConfigurationContext("service.iiop");
        if (iiopContext == null) {
          basicContext.registerPolicyConfiguration("service.iiop", SecurityContext.TYPE_SERVICE);
          iiopContext = basicContext.getPolicyConfigurationContext("service.iiop");
          iiopContext.getAuthenticationContext().setLoginModules(new AppConfigurationEntry[]{new CSIAppConfigurationEntry()});
        }
      } catch (Exception e) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).beWarning()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_REQUEST_FLOW).warningT("CorbaServiceFrame.interfaceAvailable()", e.getMessage());
        }
      }
    }
  }

  public void interfaceNotAvailable(String interfaceName) {

  }

  public void markForShutdown(long timeout) {
  }

  public void containerStarted() {
    // TODO
  }

  public void beginContainerStop() {
    // TODO
  }

  public void serviceStarted(String serviceName, Object serviceInterface) {
    if (serviceName.equals("basicadmin")) {
      registerITSAMMBean();
      mModelManager = (ManagementModelManager)serviceInterface;
      manInterface.registerResources(mModelManager);
    }

  }

  public void serviceNotStarted(String serviceName) {

  }

  public void beginServiceStop(String serviceName) {
    if (serviceName.equals("basicadmin")) {
      unregisterITSAMMBean();
      manInterface.unregisterResources();
      mModelManager = null;
    }
  }

  public void serviceStopped(String serviceName) {

  }

  public boolean setServiceProperty(String key, String value) {
    return false;
  }

  public boolean setServiceProperties(Properties serviceProperties) {
    return false;
  }

   public static SecurityContext getSecurityBasicContext() {
     return basicContext;
   }

   public static SecurityContext getSecurityIIOPContext() {
     return iiopContext;
   }

   public static ThreadSystem getThreadSystem() {
     return threadSystem;
   }

   public ManagementInterface getManagementInterface() {
     return manInterface;
   }

   /*********************************************************/
   /*******************     private metods     **************/
   /*********************************************************/
   private void registerITSAMMBean() {
    try {
    	mbs = (MBeanServer) appContext.getContainerContext().getObjectRegistry().getServiceInterface("jmx");
    	
    	iiopITSAMServerMBean = getObjectNameForServerMBean();
    	IiopManagementMBean iiopMBean = new IiopManagement(orb);
    	mbs.registerMBean(iiopMBean, iiopITSAMServerMBean);
    	
    	iiopITSAMObjectName = getObjectNameForITSAM();
    	SAP_ITSAMIiopManagementService mBean = new SAP_ITSAMIiopManagementService_Impl(mbs);
		SAP_ITSAMIiopManagementServiceWrapper wrapper = new SAP_ITSAMIiopManagementServiceWrapper(mBean);
		mbs.registerMBean(wrapper, iiopITSAMObjectName);
		
		
    } catch (Exception ex) {
    	if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beInfo()) {
    		LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).infoT("CorbaServiceFrame.start()", "MbeanServer or iiopITSAMObjectname are invalid!");
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("CorbaServiceFrame.start()", ex.getMessage());
          }
    }
   }
   
   private void unregisterITSAMMBean() {
   	try {
    	mbs.unregisterMBean(iiopITSAMObjectName);
    	mbs.unregisterMBean(iiopITSAMServerMBean);
    } catch (Exception ex) {
    	if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beWarning()) {
           	LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).infoT("CorbaServiceFrame.stop()", "MbeanServer or iiopITSAMObjectname are invalid!");
            LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("CorbaServiceFrame.stop()", ex.getMessage());
          }
    }
   }
   
   private ObjectName getObjectNameForITSAM() throws Exception{
   	 String simClass = "SAP_ITSAMIiopManagementService"; 
     String simParentClass = "SAP_ITSAMJ2eeCluster";
     String clusterNameKey = simParentClass + ".Name";
     String clusterCreationClassKey = simParentClass + ".CreationClassName";
     
     ObjectName patternON = new ObjectName(":type=SAP_ITSAMJ2eeCluster,cimclass=SAP_ITSAMJ2eeCluster,*");
     Set names =  mbs.queryNames(patternON, null);
     ObjectName j2eeClusterON = null;
     if (names.size() > 0) {
     	j2eeClusterON = (ObjectName) names.iterator().next();
     }
     
     String clusterNameValue = j2eeClusterON.getKeyProperty(clusterNameKey);
     String clusterCreationClassValue = j2eeClusterON.getKeyProperty(clusterCreationClassKey);
     
     String pattern = ":cimclass=" + simClass + "," +
 	                 "version=1.0," + 
                      "type=" + simParentClass + "." + simClass + "," + 
                      clusterNameKey + "=" + clusterNameValue + "," + 
                      clusterCreationClassKey + "=" + clusterCreationClassValue + "," + 
                      simClass + ".ElementName=" + simClass;
     
     return (new ObjectName(pattern)); 
   }
   
   private ObjectName getObjectNameForServerMBean() throws Exception {
		  String simClass = "SAP_ITSAMIiopManagementServicePerNode";
		  String simClusterClass = "SAP_ITSAMJ2eeCluster";
		  String simInstanceClass = "SAP_ITSAMJ2eeInstance";
		  String simNodeClass = "SAP_ITSAMJ2eeNode";
		  
		  String clusterNameKey = simClusterClass + ".Name";
		  String clusterCreationClassKey = simClusterClass + ".CreationClassName";
		  
		  String instanceNameKey = simInstanceClass + ".Name";
		  String instanceCreationClassKey = simInstanceClass + ".CreationClassName";
		  String instanceIDKey = simInstanceClass + ".J2eeInstanceID";
		  
		  String nodeNameKey = simNodeClass + ".Name";
		  String nodeCreationClassKey = simNodeClass + ".CreationClassName";
		  
		  String j2eeClusterNode = "SAP_J2EEClusterNode";
		  
		  //get values for cluster part
		  ObjectName patternON = new ObjectName(":type=SAP_ITSAMJ2eeCluster,cimclass=SAP_ITSAMJ2eeCluster,*");
		  Set result =  mbs.queryNames(patternON, null);
		  ObjectName j2eeClusterON = null;
		  if (result.size() > 0) {
		   	j2eeClusterON = (ObjectName) result.iterator().next();
		  }
		    
		  String clusterNameValue = j2eeClusterON.getKeyProperty(clusterNameKey);
		  //String clusterCreationClassValue = j2eeClusterON.getKeyProperty(clusterCreationClassKey);
		  
//		 find local J2eeNode MBean
		  String instanceIDValue = null;
		  String instanceNameValue = null;
		  String instanceCreationClassValue = null;
		  String nodeNameValue = null;
		  String nodeCreationClassValue = null;
		  
		  
			ObjectName localJ2eeNode = null;
			ObjectName localJ2eeNodePattern = new ObjectName("*:type=SAP_ITSAMJ2eeCluster.SAP_ITSAMJ2eeInstance.SAP_ITSAMJ2eeNode,SAP_J2EEClusterNode=\"\",*");
			result = mbs.queryNames(localJ2eeNodePattern, null);
			if (result.size()>0) {
				localJ2eeNode = (ObjectName) result.iterator().next();		
				nodeNameValue = localJ2eeNode.getKeyProperty("SAP_ITSAMJ2eeNode.Name");
				nodeCreationClassValue = localJ2eeNode.getKeyProperty("SAP_ITSAMJ2eeNode.CreationClassName");
				
				// get local J2eeNode parent - local J2eeInstance MBean
				ObjectName j2eeInstances[] = (ObjectName[]) mbs.getAttribute(localJ2eeNode, "SAP_ITSAMJ2eeInstanceJ2eeNodeGroupComponent");
				if (j2eeInstances!=null && j2eeInstances.length>0) {
					ObjectName localJ2eeInstance = j2eeInstances[0];
					instanceNameValue = localJ2eeInstance.getKeyProperty("SAP_ITSAMJ2eeInstance.Name");
					instanceIDValue = localJ2eeInstance.getKeyProperty("SAP_ITSAMJ2eeInstance.J2eeInstanceID");
					instanceCreationClassValue = localJ2eeInstance.getKeyProperty("SAP_ITSAMJ2eeInstance.CreationClassName");
				}
			}		
		  
		  String pattern = ":cimclass=" + simClass + "," +
		  				   "version=1.0," +
		  				 //type=SAP_ITSAMJ2eeCluster.SAP_ITSAMJ2eeInstance.SAP_ITSAMJ2eeNode.SAP_ITSAMIiopManagementServicePerNode
		  				   "type=" + simClusterClass + "." + simInstanceClass + "." + simNodeClass + ".SAP_ITSAMIiopManagementServicePerNode," +
		  				   
		  				   clusterNameKey + "=" + clusterNameValue + "," +
		  				   clusterCreationClassKey + "=" + simClass + "," +
		  				   
		  				   instanceNameKey + "=" + instanceNameValue + "," +
		  				   instanceIDKey + "=" + instanceIDValue + "," +
		  				   instanceCreationClassKey + "=" + instanceCreationClassValue + "," + 
		  				   
		  				   nodeNameKey + "=" + nodeNameValue + "," +
		  				   nodeCreationClassKey + "=" + nodeCreationClassValue + "," +
		  				   
		  				   simClass + ".ElementName=" + simClass + "," +
		  				   
		  				   j2eeClusterNode + "=" + nodeNameValue;
		  				
		  					
		  return (new ObjectName(pattern));
	   }

}

