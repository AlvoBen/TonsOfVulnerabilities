/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sap.engine.services.httpserver.server.rcm;

import static com.sap.engine.services.httpserver.server.Log.LOCATION_RESOURCE_CONSUMPTION_MANAGEMENT;

import java.util.ArrayList;

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.lib.rcm.Constraint;
import com.sap.engine.lib.rcm.Notification;
import com.sap.engine.lib.rcm.Resource;
import com.sap.engine.lib.rcm.ResourceConsumer;
import com.sap.engine.lib.rcm.ResourceContext;
import com.sap.engine.lib.rcm.ResourceManager;
import com.sap.engine.lib.rcm.ResourceProvider;
import com.sap.engine.lib.rcm.impl.ResourceManagerImpl;
import com.sap.tc.logging.Severity;


/**
 *
 * @author I024157
 */
public class ThrResourceManager implements ResourceProvider {
    int thrId;
    int maxThreads;
    int thrCount;
    long maxBlocked = 3;
    
    int thrNumber; 
    int lastThread;
    
    PostponedQueue queue = null;
    
    ResourceManager rcm = new ResourceManagerImpl();
    ThreadNotification  notification = new ThreadNotification();
    Constraint constraint; 
    
    ResourceContext rc;
    ThreadResource resource;
    
    
    ThreadSystem thrSystem;
    RequestProcessorThreadFactory thrFactory;
    ArrayList<RequestProcessorThread> threads;
    
    private ThreadUsageMonitor monitor;
   
    public ThrResourceManager(ThreadSystem thrSystem, RequestProcessorThreadFactory thrFactory, int thrCount) {
      if (LOCATION_RESOURCE_CONSUMPTION_MANAGEMENT.beDebug()) {
        LOCATION_RESOURCE_CONSUMPTION_MANAGEMENT.logT(Severity.DEBUG, "Initializing ThrResourceManager with:" + thrCount + " threads.");
      }
        this.thrFactory = thrFactory;
        this.thrSystem = thrSystem;
        this.thrCount = thrCount;
        
        threads = new ArrayList<RequestProcessorThread>(thrCount);
        resource = new ThreadResource(thrFactory.threadGroup(), 0);
        monitor = new ThreadUsageMonitor(thrCount);
        constraint = new ThrConstraint(thrCount, monitor);
        
        queue = new PostponedQueue(this);
        
        //register resource provider to the rcm
        rcm.registerResource(this);
        //create resource context for  Web Consumer types
        rc = rcm.createResourceContext(resource.getName(), WebConsumer.WEB_RESOURCE_CONSUMER);        
        setMaxThreads(20*thrCount);        
        rc.addNotification(monitor);        
        startThreads(thrCount);    
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
        resource.setTotalQuantity(maxThreads);
    }
    
    
    protected int getThrCount() {
		return thrCount;
	}

	synchronized boolean startThreads(int count) {

        int remain = maxThreads - threads.size();
        remain = Math.min(remain, count);
        
        //check if max thread count os reached
        if (!(remain > 0)) {
          if (LOCATION_RESOURCE_CONSUMPTION_MANAGEMENT.beDebug()) {
            LOCATION_RESOURCE_CONSUMPTION_MANAGEMENT.logT(Severity.DEBUG, " \n Max thread number (" + maxThreads + ") is reached. No threads will be started.");
          }
          return false;
        }
       
        String thrName = thrFactory.threadGroup();
        for (int i = 0; i < remain; i++ ) {
            RequestProcessorThread thr = thrFactory.getInstance();
            thr.setManager(this);
            thr.setThreadNumber(thr.hashCode());
            lastThread = thr.getThreadNumber();
            threads.add(thr);
            if (thrFactory.dedicated()) {
                thrSystem.executeInDedicatedThread(thr, thrName + " [@" + thr.getThreadNumber() + "]");
            }
        }
        if (LOCATION_RESOURCE_CONSUMPTION_MANAGEMENT.beDebug()) {
          LOCATION_RESOURCE_CONSUMPTION_MANAGEMENT.logT(Severity.DEBUG, " \n Requesting  " + count + " new threads." +
                    "The " + count + " was requested.");
        }
        if (!notification.running) {
            notification.running = true;
            notification.triger = (long)(0.9 * threads.size());
            thrSystem.executeInDedicatedThread(notification, "HTTP Thread watch");
        } 
        return true;
    }
    
	public final void replaceThread(RequestProcessorThread thread) {
		RequestProcessorThread thr = thrFactory.getInstance();
		thr.setManager(this);
        thr.setThreadNumber(thread.getThreadNumber());
        synchronized (this) {
	        threads.add(thr);
	        threads.remove(thread);			
        }
        if (thrFactory.dedicated()) {
            thrSystem.executeInDedicatedThread(thr, thrFactory.threadGroup() + " [" + thr.getThreadNumber() + "]");            
        }         
	}

	
//    synchronized boolean dropdown(RequestProcessorThread thr) {    	    	
//    	if (thr.getThreadNumber() < thrCount) {
//    		for (RequestProcessorThread t : threads) {
//    			if (t.getThreadNumber() > thrCount && !t.dropped) {    				
//    				t.dropped = true;
//    				threads.remove(t);
//    				return false;
//    			}
//    		}
//    		thrNumber = thrCount;
//    	} else {    		
//    		removeThread(thr);
//    	}
//    	return true;
//    }
//    
//    synchronized void  removeThread(RequestProcessorThread thr) {
//        threads.remove(thr);
//        if (threads.size() == 0) {
//            synchronized (notification) {
//                notification.running = false;
//                notification.notify();
//            }
//        }
//    }
    
    synchronized void  removeThread(RequestProcessorThread thr, boolean isStopping) {
    	
        threads.remove(thr);
        if (threads.size() == 0) {
            synchronized (notification) {
                notification.running = false;
                notification.notify();
            }
        }
        if ((threads.size() < thrCount) && !isStopping ) {
    		startThreads(1);
    	}
    }
    
    
    public Resource getResource() {
        return resource;
    }

    public Constraint getDefaultConstrait() {
        return constraint;
    }

    public Notification getDefaultNotification() {
        return notification;
    }
    
    boolean consume(ResourceConsumer consumer) {
      
      if (LOCATION_RESOURCE_CONSUMPTION_MANAGEMENT.beDebug()) {
        LOCATION_RESOURCE_CONSUMPTION_MANAGEMENT.logT(Severity.DEBUG, " \nThe HTTP Worker Thread is consumed from:" + consumer.getId());
      }
      return rcm.consume(consumer, resource.getName(), 1);
      
    }
    
    void release(ResourceConsumer consumer) {
      rcm.release(consumer, resource.getName(), 1);      
    }
     
        
    public class ThreadNotification implements Notification, Runnable {
        
        boolean running;
        long triger; 
        volatile long currentUsage;
        
        public void update(ResourceConsumer consumer, long previousUsage, long currentUsage) {
        }
        
        
        public void run() { 
          ArrayList<RequestProcessorThread> currentThreads = new ArrayList<RequestProcessorThread>();
          String httpBlockingPeriod = System.getProperty("HttpBlockingPeriod", "5000");
          int blockingPeriod = Integer.parseInt(httpBlockingPeriod);
          try {
            while (running) {
                synchronized (this) {
                	for (int i = 0 ; i < 3 ; i++) {
	                    try {
	                        this.wait(blockingPeriod);
	                    } catch (InterruptedException ex) {
	                        break;
	                    }
                	}
                }
                
                  currentThreads.clear();
                  synchronized (ThrResourceManager.this) {
                     currentThreads.addAll(threads);
                  }
                  this.currentUsage = rcm.getTotalUsage(WebConsumer.WEB_RESOURCE_CONSUMER, resource.getName());
                  if (LOCATION_RESOURCE_CONSUMPTION_MANAGEMENT.beDebug()) {
                    LOCATION_RESOURCE_CONSUMPTION_MANAGEMENT.logT(Severity.DEBUG, "Health Check execution. Current thread usage: " + this.currentUsage); 
                  }

                  for (RequestProcessorThread thr : currentThreads ) {
                	  thr.healthCheck(this.currentUsage > triger);                     	  
                  }                 
                  
                }                
            
        
        } catch (Throwable t) {
          LOCATION_RESOURCE_CONSUMPTION_MANAGEMENT.traceThrowableT(Severity.DEBUG, "", t);
        }
    }
    }

    public ThreadUsageMonitor getMonitor () {
      return monitor;
    }
}
