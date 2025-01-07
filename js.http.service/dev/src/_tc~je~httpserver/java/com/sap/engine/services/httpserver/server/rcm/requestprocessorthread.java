/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sap.engine.services.httpserver.server.rcm;

import static com.sap.engine.services.httpserver.server.Log.LOCATION_RESOURCE_CONSUMPTION_MANAGEMENT;

import com.sap.bc.proj.jstartup.fca.FCAConnection;
import com.sap.bc.proj.jstartup.fca.FCAException;
import com.sap.engine.services.httpserver.server.Log;
import com.sap.engine.services.httpserver.server.ServiceContext;
import com.sap.tc.logging.Severity;

/**
 *
 * @author I024157
 */
public abstract class RequestProcessorThread implements Runnable {
  ThrResourceManager manager;
  protected WebConsumer consumer = new WebConsumer();

  boolean dropped;  // mark that the thread is replaced
  boolean touched;  //true if the thread is touched from the watchdog
  protected boolean inWork;
  boolean blocked;

  private PostponedQueue queue = null;

  int threadNumber;

  FCAConnection conn = null;

  public int getThreadNumber() {
    return threadNumber;
  }

	void setThreadNumber(int threadNumber) {
		this.threadNumber = threadNumber;
	}
	
	public void run() {
	  // Thread pool clears the context of each returned thread, but
	  // HTTP Provider service doesn't return its worker threads to the
	  // pull until the server is running, so the context have to be
	  // cleared here before reusing the tread
	  com.sap.engine.system.ThreadWrapperExt.clearManagedThreadRelatedData();

	  while (!isStopped()) {
	    try {
	      if (conn == null) {
          if (dropped) {
            break;
          }
	        process();
	      } else {
	        process(conn);
	      }            	            	            
	      //get from postponed queue
	      if (inWork) {
          if (ServiceContext.getServiceContext().getHttpProperties().isUsePostponedRequestQueue()) {
            //TODO
            try {
              conn = queue.poll(consumer.getId());
            } catch (FCAException e) {
              //OK could not get next FCAConnection
              conn = null;
            }
          }
	      }
	    } finally {
	      if (inWork && (conn == null)) {
	        try {
	          manager.release(consumer);
	        } catch (Exception ex) {
	          Log.traceWarning(LOCATION_RESOURCE_CONSUMPTION_MANAGEMENT, "ASJ.http.000351", "RequestProcessorThread.run(): ", ex, null, null, null);
	        }
	      }
	      synchronized (this) {
	        inWork = false;
	        touched = false;
	        blocked = false;                
	      }
	    }
	    if (dropped && (conn != null)) {
        //TODO
        boolean released = true;
        try {
	        queue.add(conn, consumer.getId());
          manager.release(consumer);              
          Thread.yield();
        } catch (FCAException e) {
          //da zaciklim ili da vyrnem error
          released = false;
          manager.release(consumer); 
        }
        if (released) {
          try {
            conn = queue.get(consumer);
            if (conn == null) {            	                        		
              break;
            }
          } catch (FCAException e) {
            //TODO OK
          }
        }
	    } else {
	      consumer.set("");
	    }

	  }
	  //remove from resource manager
	  manager.removeThread(this, isStopped());
	}



	protected boolean consume(FCAConnection conn, String consName) throws FCAException {
	  this.consumer.set(consName);
	  if (this.conn == null) {
	    inWork = manager.consume(consumer);
    }
	  if (this.conn !=null ) {
	    this.conn = null;
      inWork = true;
    } else {
	  if (!inWork) {
      if (ServiceContext.getServiceContext().getHttpProperties().isUsePostponedRequestQueue()) {
        inWork = !queue.postpone(conn, consumer);
      }
	  }
    }
	  return inWork;
	}

	protected void continueWith(String consName) {
	  this.consumer.set(consName);
	  inWork = true;    	
	}

	protected boolean keepResource(String name) { 
	  return false;
	}

	void setManager(ThrResourceManager manager) {
	  this.manager = manager;
	  this.queue = manager.queue;
	}


    synchronized boolean  healthCheck(boolean drop) {
    	if (LOCATION_RESOURCE_CONSUMPTION_MANAGEMENT.beDebug()) {
    	  LOCATION_RESOURCE_CONSUMPTION_MANAGEMENT.logT(Severity.DEBUG,"\nHealth Check of thread:" + threadNumber +
    				"\ninWork = " + inWork +
    				"\ndropped = " + dropped +
    				"\ntouched = " + touched +
    				"\nblocked = " + blocked);
    	}
        if (!inWork || dropped) {
            return false;
        } else if (!touched) {
            touched = true;
            return false;
        } else  if (drop) {
          //manager.block(consumer);
          blocked = true;
          if (LOCATION_RESOURCE_CONSUMPTION_MANAGEMENT.beDebug()) {
            LOCATION_RESOURCE_CONSUMPTION_MANAGEMENT.logT(Severity.DEBUG,"Starting new Thread from Thread:" + getThreadNumber());
    	  }
          dropped = manager.startThreads(1);
          return true;
        } else {
          //manager.block(consumer);
          blocked = true;
          return false;
        }
    }

    protected abstract void process();
    
    protected abstract void process(FCAConnection conn);
    
    protected abstract boolean isStopped();
    
}
