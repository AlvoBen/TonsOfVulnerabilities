package com.sap.jms.client.connection;

import java.util.ArrayList;

import java.util.Iterator;

import java.util.List;

import javax.jms.JMSException;


import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.jms.util.logging.LogService;
import com.sap.jms.util.logging.LogServiceImpl;

/**
 * The purpose of that class is to make the I/O
 * blocking calls that before this class were done in the finalization method.
 * Now the class will launch a separate thread doing this. 
 *
 *
 * During the finalization objects      
 * are resurrected and added to a global structure, there is a normal         
 * single thread closing them if there are such objects and sleeping if       
 * there is no need of such activity. According to the JVM spec, such         
 * object will be removed later from the GC thread once reference is lost     
 * from the global structure without invoking the finalizer again. In that    
 * way the finalize() method will be stripped of any complex logic.
 */
public class AsyncCloser implements Runnable {

	private static AsyncCloser singleton = new AsyncCloser();

	private static final LogService logService = LogServiceImpl.getLogService(LogServiceImpl.CLIENT_LOCATION);
	private static String LNAME;

	private static final String THREAD_MONITOR_NAME = "JMS Asynchronous Closing Thread";

	private static final String TASK_NAME = "Closing leaked JMS resource";    

	private List scheduledObjects = new ArrayList();

	private boolean started = false;

	//this flag is different than the started flag since it could be possible that
	//the thread is not yet started (no free thread in the pool etc.) until that
	//we will use that flag as false and continue with the old way - direct invocations
	private volatile boolean isThreadRunning = false;

	private AsyncCloser() {
		//do nothing - just we want to hide the constructor from unfriendly
		// usage, since we want to make sure, there is a singleton and
		//nobody is instantiating it
		LNAME = getClass().getName();
	}

	public static AsyncCloser getInstance() {
		return singleton;
	}

	public void start(ThreadSystem threadSystem) {
		if (started) {
			return;
		}

		synchronized (this) {
			//make sure to check again, since it may be changed in the meantime
			// by another thread
			if (started) {
				return;
			}
			logService.infoTrace(LNAME, "A system thread will be launched to perform asynchronous closing of unreferenced JMS objects", new Object[] {});    

			//launch a system thread always
            threadSystem.startThread(this, true);            

			started = true;
		}
	}

	public void scheduleForClose(Closeable closeable) throws JMSException{
		//in theory there could be several GC threads, we will synchronize here
		synchronized (scheduledObjects) {
			if (!isThreadRunning) {
				//the thread is not started, that should never happen, but better to guard against this condition and leaking memory
				// and other bad problems --> we will close the object here immediately, instead of keeping reference
				logService.infoTrace(LNAME, "An object will be closed in the finalizer since no asynchronous thread is available. The object is {0}",
						new Object[] { closeable });                
				closeable.close();
				return;
			}            

			scheduledObjects.add(closeable);
			scheduledObjects.notify();
		}

	}




	public void run() {
		Thread.currentThread().setName(THREAD_MONITOR_NAME);
		isThreadRunning = true;

		//by default we are processing, in case nothing is to be done - we will push task that we are waiting


		//note - no need to set  Thread.currentThread().setDaemon(true); - causes exception to be
		// thrown, since it cannot be set after the thread is launched

		logService.debug(LNAME, "Will wait for unreferenced objects to close.");

		//we are not free at the beginning - we will report no task only when we are waiting
		while (true) {

			try {

				//make a copy of the collections for the closing and release
				// the lock for the synchronization
				// make sure that call to closeable.close() is WITHOUT taken
				// lock on scheduledObjects,
				// otherwise we will block again the finalizer
				// and there will be no effect of that class
				List phantoms = new ArrayList();
				synchronized (scheduledObjects) {
					try {
						//could be filled up once we have lost the lock and doing the I/O calls.
						//we should check here again
						if (scheduledObjects.isEmpty()) {

							//nothing to do - push empty task
								//release lock and wait till something appears and we are notified
								scheduledObjects.wait();
						}    
					} catch (InterruptedException ie) {
						//not very nice - someone called interrupt() on our
						// thread, since our thread is private and the only other caller could be the kernel
						//invoking whenever the server is being stopped, we will exit gracefully here.
						logService.debug(LNAME, "The AsyncCloser thread from object {0} was notified ", this);

						//break out of the while(true) cycle
						break;
					}

					logService.debug(LNAME, "The AsyncCloser thread from object {0} was notified ", this);
					phantoms.addAll(scheduledObjects);
					scheduledObjects.clear();
				}

				logService.debug(LNAME, "Will attempt to close the unreferenced objects: " + phantoms);

					for (Iterator iterator = phantoms.iterator(); iterator.hasNext();) {
						Closeable unreferencedCloseableObject = null;
						try {
							unreferencedCloseableObject = (Closeable) iterator.next();

							logService.debug(LNAME, "Will close unreferenced object: " + unreferencedCloseableObject);

							unreferencedCloseableObject.close();

						} catch (JMSException e) {
							//in the trace files
							logService.errorTrace(LNAME, "Failed to close unreferenced object: " + unreferencedCloseableObject);                        
							logService.exception(LNAME, e);
						}
					}    
			} catch (Exception e) {
				//in case of any problem, we don't want our singleton thread to die, since that will cause
				//problems with the cluster - blocking again the finalizer, etc. Thus we will disregard
				//any exceptions and let this thread live and attempt to close other unreferenced objects

				//in the trace files
				logService.errorTrace(LNAME, "Got exception while closing unreferenced objects.");                        
				logService.exception(LNAME, e);
				try {
					// however we don't want to cause any possibility for endless cycle-s and 100% CPU usage
					// we will sleep a little                  
					Thread.sleep(100);
				} catch (InterruptedException e1) {

					//the server is being shutdown while waiting after our internal error ?
					//what a bad luck, just log it and get out,
					logService.exception(LNAME,e1);
					//get out of the main cycle
					break;
				}

			}

		}

		//ok - we are out of the endless cycle - should happen only whenever the server is being stopped
		// and we have been called with Interrupt by the kernel. Mark the thread that it is no longer running
		isThreadRunning = false;        
	}

}

