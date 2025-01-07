package com.sap.jms.client.connection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jms.JMSException;

import com.sap.engine.frame.core.thread.ThreadSystem;
import com.sap.engine.system.ThreadWrapper;
import com.sap.jms.util.Logging;
import com.sap.jms.util.TaskManager;
import com.sap.jms.util.Task;
import com.sap.tc.logging.Severity;

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
public class AsyncCloser implements Task {

	private static AsyncCloser singleton = new AsyncCloser();

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
	}

	public static AsyncCloser getInstance() {
		return singleton;
	}

	public void start(TaskManager taskManager) {
		if (started) {
			return;
		}

		synchronized (this) {
			//make sure to check again, since it may be changed in the meantime
			// by another thread
			if (started) {
				return;
			}
			if (Logging.isWritable(this, Severity.INFO)) {
                Logging.log(this, Severity.INFO, "A system thread will be launched to perform asynchronous closing of unreferenced JMS objects");
            }    

			//launch a system thread always
			taskManager.schedule(this); 
			//threadSystem.startThread(this, threadSystem.getThreadContext() == null);            

			started = true;
		}
	}

	public void scheduleForClose(Closeable closeable) throws JMSException{
		//in theory there could be several GC threads, we will synchronize here
		synchronized (scheduledObjects) {
			if (!isThreadRunning) {
				//the thread is not started, that should never happen, but better to guard against this condition and leaking memory
				// and other bad problems --> we will close the object here immediately, instead of keeping reference
                if (Logging.isWritable(this, Severity.INFO)) {
                    Logging.log(this, Severity.INFO, "An object will be closed in the finalizer since no asynchronous thread is available. The object is ", closeable);
                }                
				closeable.close();
				return;
			}            

			scheduledObjects.add(closeable);
			scheduledObjects.notify();
		}

	}


	public void execute() {
		Thread.currentThread().setName(THREAD_MONITOR_NAME);
		isThreadRunning = true;

		//by default we are processing, in case nothing is to be done - we will push task that we are waiting


		//note - no need to set  Thread.currentThread().setDaemon(true); - causes exception to be
		// thrown, since it cannot be set after the thread is launched

		if (Logging.isWritable(this, Severity.DEBUG)) {
            Logging.log(this, Severity.DEBUG, "Will wait for unreferenced objects to close.");
        }

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
							ThreadWrapper.pushTask("", ThreadWrapper.TS_WAITING_FOR_TASK);
							try {
								//release lock and wait till something appears and we are notified
								scheduledObjects.wait();
							} finally {
								ThreadWrapper.popTask();                                
							}                           
						}    
					} catch (InterruptedException ie) {
						//not very nice - someone called interrupt() on our
						// thread, since our thread is private and the only other caller could be the kernel
						//invoking whenever the server is being stopped, we will exit gracefully here.
                        if (Logging.isWritable(this, Severity.DEBUG)) {
                            Logging.log(this, Severity.DEBUG, "The AsyncCloser thread from object ", this, " was notified");
                        }

						//break out of the while(true) cycle
						break;
					}

					if (Logging.isWritable(this, Severity.DEBUG)) {
                        Logging.log(this, Severity.DEBUG, "The AsyncCloser thread from object ", this, " was notified");
                    }
					phantoms.addAll(scheduledObjects);
					scheduledObjects.clear();
				}

				if (Logging.isWritable(this, Severity.DEBUG)) {
                    Logging.log(this, Severity.DEBUG, "Will attempt to close the unreferenced objects: ", phantoms);
                }

				ThreadWrapper.pushTask(TASK_NAME, ThreadWrapper.TS_PROCESSING);

				try {

					for (Iterator iterator = phantoms.iterator(); iterator.hasNext();) {
						Closeable unreferencedCloseableObject = null;
						try {
							unreferencedCloseableObject = (Closeable) iterator.next();

							if (Logging.isWritable(this, Severity.DEBUG)) {
                                Logging.log(this, Severity.DEBUG, "Will close unreferenced object: ", unreferencedCloseableObject);
                            }

							unreferencedCloseableObject.close();

						} catch (JMSException e) {
							Logging.exception(this, e, "Failed to close unreferenced object: ", unreferencedCloseableObject);
						}
					}    

				} finally {
					ThreadWrapper.popTask();    
				}

			} catch (Exception e) {
				//in case of any problem, we don't want our singleton thread to die, since that will cause
				//problems with the cluster - blocking again the finalizer, etc. Thus we will disregard
				//any exceptions and let this thread live and attempt to close other unreferenced objects

				//in the trace files
				Logging.exception(this, e, "Got exception while closing unreferenced objects.");
				try {
					// however we don't want to cause any possibility for endless cycle-s and 100% CPU usage
					// we will sleep a little                  
					Thread.sleep(100);
				} catch (InterruptedException e1) {

					//the server is being shutdown while waiting after our internal error ?
					//what a bad luck, just log it and get out,
					Logging.exception(this, e1);
					//get out of the main cycle
					break;
				}

			}

		}

		//ok - we are out of the endless cycle - should happen only whenever the server is being stopped
		// and we have been called with Interrupt by the kernel. Mark the thread that it is no longer running
		isThreadRunning = false;        
	}

	public String getName() {
		return THREAD_MONITOR_NAME;
	}

}

