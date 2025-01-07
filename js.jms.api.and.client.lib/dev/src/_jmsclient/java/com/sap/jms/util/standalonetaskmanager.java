package com.sap.jms.util;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.sap.tc.logging.Severity;
import com.sap.jms.util.Command;

public class StandaloneTaskManager implements TaskManager {

	Map properties = null;

	public static int DEFAULT_NUMBER_OF_THREADS = 5;
	int numberOfThreads = DEFAULT_NUMBER_OF_THREADS;    
	private ExecutorService executor = null;

	public static Integer parseInt(String text) {
		Integer value = null;
		if (text != null) {
			try {
				value = Integer.valueOf(text);
			} catch (NumberFormatException e) { //$JL-EXC$        		
			}
		}        	
		return value;
	}   

	public StandaloneTaskManager(Map properties) {
		super();
		this.properties = properties;
	}

	public void schedule(Task task) {
		if (executor != null) {
			executor.execute(new Command(task));
		}
	}

	private class DaemonThreadFactory implements ThreadFactory{

		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setDaemon(true);
			return t;
		}
	};

	public void start() {
		Integer threads = parseInt((String) properties.get("dc.TaskManager.numberOfThreads"));
		numberOfThreads = threads != null ? threads.intValue() : DEFAULT_NUMBER_OF_THREADS;  
		if (Logging.isWritable(this, Severity.DEBUG)) {
			Logging.log(this, Severity.DEBUG, "TaskManager.start() number of threads = ", numberOfThreads);
		}

		 ThreadFactory threadFractory = new DaemonThreadFactory();
		
		executor = Executors.newFixedThreadPool(numberOfThreads,  threadFractory);
	}

	public void stop() {
		if (Logging.isWritable(this, Severity.DEBUG)) {
			Logging.log(this, Severity.DEBUG, "TaskManager.stop()");
		}		


		Thread.currentThread().dumpStack();
		executor.shutdownNow();
		executor = null;
	}

}
