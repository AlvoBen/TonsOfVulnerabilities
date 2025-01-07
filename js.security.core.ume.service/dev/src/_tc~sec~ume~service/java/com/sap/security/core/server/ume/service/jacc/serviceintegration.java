/**
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000-2002.
 * All rights reserved.
 */
package com.sap.security.core.server.ume.service.jacc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.container.monitor.ServiceMonitor;
import com.sap.engine.frame.core.locking.LockException;
import com.sap.engine.frame.core.locking.LockingContext;
import com.sap.engine.frame.core.locking.ServerInternalLocking;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.security.api.UMException;
import com.sap.security.core.role.imp.xml.XMLServiceRepository;
import com.sap.security.core.server.ume.service.UMEServiceFrame;
import com.sap.tc.logging.Severity;

/**
 * 
 * @author Jako Blagoev
 */
public class ServiceIntegration {
	
  private static final String LOCK_NAMESPACE = "[ume:ume.serv.authz]";
  private static final String LOCK_ARGUMENT = "INIT";
  
	private ApplicationServiceContext serviceContext;
  private Vector servicesToStop = new Vector();

  private boolean containerStarted = false;
  private boolean toShutDown = false;  

  private UMEServiceFrame frame = null;  
  
  private static final String AUTHORIZATION_DESCRIPTOR = "authorization-configuration.xml";
  private static final String DESCRIPTOR_DIGEST = "authorization-digest";  
  
	public ServiceIntegration(ApplicationServiceContext serviceContext, UMEServiceFrame frame) throws ServiceException {
    String method = "ServiceIntegration(ApplicationServiceContext,UMEServiceFrame)";
    UMEServiceFrame.myLoc.entering(method);

	  this.serviceContext = serviceContext;
    this.frame = frame;
	  
    ServerInternalLocking locking = null;
    try {
      locking = getLockingContext();
    } catch(TechnicalLockException e1) {
      LoggingHelper.traceThrowable(500, UMEServiceFrame.myLoc, method, e1);
      UMEServiceFrame.myLoc.errorT(UMEServiceFrame.myCat, method, "Could create ServerInternalLocking");
      throw new SecurityException(e1.getMessage());
    }
    
    tryToLock(locking);
    UMEServiceFrame.myLoc.infoT(method, "Successfully took the lock");
        
    try {
      ServiceMonitor[] services = serviceContext.getContainerContext().getSystemMonitor().getServices();
  	  for (int i = 0; i < services.length; i++) {
        InputStream stream = services[i].getDescriptorContainer().getPersistentEntryStream(AUTHORIZATION_DESCRIPTOR, true);
        
        if (stream == null) {
          UMEServiceFrame.myLoc.infoT(method, "The descriptor for service " + services[i].getDisplayName() + " is null.");
          continue;
        }
              
        InputStream digestStream = services[i].getDescriptorContainer().getPersistentEntryStream(DESCRIPTOR_DIGEST, true);
        
        byte[] digest = null;
        
        if (digestStream != null) {
          try {
            int len = digestStream.available();
            digest = new byte[len];
            digestStream.read(digest);
          } catch (IOException e) {
            LoggingHelper.traceThrowable(Severity.ERROR, UMEServiceFrame.myLoc, method, e);
            UMEServiceFrame.myLoc.errorT(UMEServiceFrame.myCat, method, "Internal error.");     
          }
        }
        
        byte[] newdigest = null;
        byte[] descriptor = null;
        try {
          int len = stream.available();
          descriptor = new byte[len];
          stream.read(descriptor);
        } catch (IOException e) {
          LoggingHelper.traceThrowable(Severity.ERROR, UMEServiceFrame.myLoc, method, e);
          UMEServiceFrame.myLoc.errorT(UMEServiceFrame.myCat, method, "Internal error.");            
        } 
        
        newdigest = hash(descriptor);
        
        if (equalArrays(digest, newdigest)) {
          UMEServiceFrame.myLoc.infoT(method, "The hash function for the descriptor of service " + services[i].getDisplayName() + " is the same as the previous.");
          UMEServiceFrame.myLoc.infoT(method, "The authorization descriptor won't be redeployed");
          continue;
        } else {
          UMEServiceFrame.myLoc.infoT(method, "The hash function for the descriptor of service " + services[i].getDisplayName() + " is not the same as the previous.");
          UMEServiceFrame.myLoc.infoT(method, "The authorization descriptor will be redeployed");          
        }      
            
        boolean success = generateServicePolicy(services[i].getComponentName(), new ByteArrayInputStream(descriptor));
        
        if (success) {
          try {          
            InputStream digestOutput = new ByteArrayInputStream(newdigest);
            services[i].getDescriptorContainer().setPersistentEntryStream(DESCRIPTOR_DIGEST, digestOutput, true);
            digestOutput.close();
            UMEServiceFrame.myLoc.infoT(method, "Successfully added the new hash of service " + services[i].getDisplayName() + "into the descriptors container.");          
          } catch (IOException e) {
            LoggingHelper.traceThrowable(Severity.ERROR, UMEServiceFrame.myLoc, method, e);
            UMEServiceFrame.myLoc.errorT(UMEServiceFrame.myCat, method, "Could not add the hash into the descriptors container for service " + services[i].getDisplayName());            
          }
        }
      }
      
	//not needed, because this is already done in the static constructor of XMLServiceRepository
      //XMLServiceRepository.loadXMLFiles();
    } finally {
      try {
        locking.unlock(LOCK_NAMESPACE, LOCK_ARGUMENT, ServerInternalLocking.MODE_EXCLUSIVE_NONCUMULATIVE);
      } catch(TechnicalLockException e) {
        LoggingHelper.traceThrowable(500, UMEServiceFrame.myLoc, method, e);
        UMEServiceFrame.myLoc.errorT(UMEServiceFrame.myCat, method, "Could not unlock the exclusive non cumulative lock.");
      } catch(IllegalArgumentException e) {
        LoggingHelper.traceThrowable(500, UMEServiceFrame.myLoc, method, e);
        UMEServiceFrame.myLoc.errorT(UMEServiceFrame.myCat, method, "Could not unlock the exclusive non cumulative lock.");
      }     
    }
	}
	
	
	public boolean generateServicePolicy(String serviceName, InputStream serviceDescriptor) throws ServiceException {
		String method = "generateServicePolicy(String,InputStream)";
		UMEServiceFrame.myLoc.entering(method);
	  
		if (serviceDescriptor == null) {
			UMEServiceFrame.myLoc.infoT(method, "Authorization descriptor for service " + serviceName + " is null.");
			UMEServiceFrame.myLoc.exiting(method);	    
			return false;
		}
		UMEServiceFrame.myLoc.infoT(method, "Authorization descriptor for service " + serviceName + " is not null.");
	  
		try {
			UMEServiceFrame.myLoc.infoT(method, "Trying to deploy the ume actions for service " + serviceName);
			boolean successfullyDeployed = deployServiceActions(serviceName, serviceDescriptor);
			UMEServiceFrame.myLoc.infoT(method,  "Deployment of ume actions for service " + serviceName + ((successfullyDeployed)? " passed.":" failed."));
		  
			if (!successfullyDeployed) {
				UMEServiceFrame.myLoc.errorT(method,  "Deployment of ume actions for service " + serviceName + " failed.");		  	
				boolean isCoreService = serviceContext.getContainerContext().getSystemMonitor().getService(serviceName).isCore();
				if (isCoreService) {
					UMEServiceFrame.myLoc.errorT(method, "Service " + serviceName + " is core service. The server will be shut down.");
					throw new ServiceException("Could not successfully deploy the authorization descriptor of core service " + serviceName);
				} else {		    	
					UMEServiceFrame.myLoc.errorT(method, "Service " + serviceName + " is not core service. It will be stopped");		      
					stopAdditionalService(serviceName);
				}
			}
			return successfullyDeployed;
		} finally {
			UMEServiceFrame.myLoc.exiting(method);	    
		}
	}
	
	private boolean deployServiceActions(String name, InputStream actionsXml) {
  	String method = "deployServiceActions(String,InputStream)";
  	try {
			XMLServiceRepository.deployActionsXMLFile(name, actionsXml);
		} catch (UMException e) {
			LoggingHelper.traceThrowable(Severity.ERROR, UMEServiceFrame.myLoc, method, e);
			UMEServiceFrame.myLoc.errorT(UMEServiceFrame.myCat, method, "Actions deployment failed for service " + name);
	    return false;
		} catch (Exception e) {
			LoggingHelper.traceThrowable(Severity.ERROR, UMEServiceFrame.myLoc, method, e);
			UMEServiceFrame.myLoc.errorT(UMEServiceFrame.myCat, method, "Actions deployment failed for service " + name);	
		  return false;
		}	  
	  return true;
	}
	
	private void stopAdditionalService(String serviceName) {
	  ServiceMonitor serviceMonitor = serviceContext.getContainerContext().getSystemMonitor().getService(serviceName);
    servicesToStop.addElement(serviceName);
	}
		
///////////////////////////////////////////////////////////////////////////////////
///////////////////////ContainerEventListener//////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////		

	public void serviceStarted(String serviceName, Object obj) {
	  String method = "serviceStarted(String,Object)";
	  
    if (!containerStarted) {
      UMEServiceFrame.myLoc.infoT(method, "Container is not started yet");
      return;
    }
    
	  try {
	  	if (servicesToStop.contains(serviceName)) {
				ServiceMonitor serviceMonitor = serviceContext.getContainerContext().getSystemMonitor().getService(serviceName);
				UMEServiceFrame.myLoc.errorT(UMEServiceFrame.myCat, method, "Trying to stop additional service " + serviceName + " , because the deployment of the ume actions failed.");
				serviceMonitor.stop();
				UMEServiceFrame.myLoc.errorT(UMEServiceFrame.myCat, method, "Service " + serviceName + " stopped successfully.");
	  	  servicesToStop.remove(serviceName);
	  	}
	  } catch (ServiceException e) {
			LoggingHelper.traceThrowable(Severity.ERROR, UMEServiceFrame.myLoc, method, e);
			UMEServiceFrame.myLoc.errorT(UMEServiceFrame.myCat, method, "Could not stop service " + serviceName + " . Reason " + e.getMessage());
		} 
	}	
  
  public void containerStarted() {
    String method = "serviceStarted(String,Object)";
    if (toShutDown) {
      serviceContext.getCoreContext().getCoreMonitor().shutDown(0);
    }
    for (int i = 0; i < servicesToStop.size(); i++) {
      String serviceName = (String)servicesToStop.elementAt(i);
      ServiceMonitor serviceMonitor = serviceContext.getContainerContext().getSystemMonitor().getService(serviceName);
      UMEServiceFrame.myLoc.errorT(UMEServiceFrame.myCat, method, "Trying to stop additional service " + serviceName + " , because the deployment of the ume actions failed.");
      try {
        serviceMonitor.stop();
      } catch(ServiceException e) {
        LoggingHelper.traceThrowable(500, UMEServiceFrame.myLoc, method, e);
        UMEServiceFrame.myLoc.errorT(UMEServiceFrame.myCat, method, "Could not stop service " + serviceName + " . Reason " + e.getMessage());
      }
      UMEServiceFrame.myLoc.errorT(UMEServiceFrame.myCat, method, "Service " + serviceName + " stopped successfully.");
    }

    containerStarted = true;
  }  
  
  private ServerInternalLocking getLockingContext() throws TechnicalLockException, IllegalArgumentException {
    LockingContext lc = frame.getServiceContext().getCoreContext().getLockingContext();
    return lc.createServerInternalLocking(LOCK_NAMESPACE, LOCK_ARGUMENT);
  }  
  
  private byte[] hash(byte[] input) {
    try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      return md5.digest(input);
    } catch (NoSuchAlgorithmException e) {
      throw new SecurityException(e.getMessage());
    }
  }
    
  private boolean equalArrays(byte[] arr1, byte[] arr2) {
    if (arr1 == null && arr2 == null) {
      return true;
    }
    
    if (arr1 == null || arr2 == null) {
      return false;
    }
    
    if (arr1.length != arr2.length) {
      return false;
    } 
    
    for (int i = 0; i < arr1.length; i++) {
      if (arr1[i] != arr2[i]) {
        return false;
      }
    }
    return true;
  }
  
  private void tryToLock(ServerInternalLocking internalLock) {
    String method = "verifyPrimaryServerFinished(ServerInternalLocking)";
    long waitLimit = 3 * 60 * 1000;
    long waitInterval = 1000;
    long beginTime = System.currentTimeMillis();
    boolean locked = false;
    
    while(true) {
      if (System.currentTimeMillis() > beginTime + waitLimit) {
        break;
      }
        
      try {
        internalLock.lock(LOCK_NAMESPACE, LOCK_ARGUMENT, ServerInternalLocking.MODE_EXCLUSIVE_NONCUMULATIVE);
        locked = true;
        break;
      } catch(LockException e) {
        synchronized(this) {
          try {
            wait(waitInterval);
          } catch(InterruptedException ex) {
            LoggingHelper.traceThrowable(500, UMEServiceFrame.myLoc, method, ex);
            UMEServiceFrame.myLoc.errorT(UMEServiceFrame.myCat, method, "Unexpected Exception " + ex);
            throw new SecurityException("UnexpectedException " + ex.getMessage());
          }
        }
      } catch (Exception e) {
        LoggingHelper.traceThrowable(500, UMEServiceFrame.myLoc, method, e);
        UMEServiceFrame.myLoc.errorT(UMEServiceFrame.myCat, method, "Unexpected Exception " + e);
        throw new SecurityException("UnexpectedException " + e.getMessage());
      }
    } 
    
    if (!locked) {
      UMEServiceFrame.myLoc.errorT(UMEServiceFrame.myCat, method, "Could not lock for 3 minutes");
      throw new SecurityException("Could not lock for 3 minutes");
    }
  }
}

