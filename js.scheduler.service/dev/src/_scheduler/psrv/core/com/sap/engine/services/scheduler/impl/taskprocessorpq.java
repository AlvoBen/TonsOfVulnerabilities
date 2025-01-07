package com.sap.engine.services.scheduler.impl;

import com.sap.engine.lib.util.FastLongPriorityQueue;
import com.sap.engine.lib.util.HashMapObjectLong;
import com.sap.scheduler.api.SchedulerTask;
import com.sap.scheduler.api.SchedulerTaskID;
import com.sap.scheduler.api.TaskStatus;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * This class is a thread which is supposed to get the soonest task from the internal
 * priority queue and to give it to job execution runtime for immediate execution. In
 * the other time it has to wait
 *
 * @author Stefan Dimov
 * @author Thomas Mueller (d040939)
 * 
 * @version 1.0
 */
public class TaskProcessorPQ extends Thread {
  private static final Location location = Location.getLocation(TaskProcessorPQ.class);
  private static final Category category = Category.SYS_SERVER;
  
  private static final long WAIT_TIME_DEFAULT = 20000; // 20 sec
	
  /**
   * This is the internal priority queue. It's used to hold the TaskProcessor objects
   * and the soonest time of the execution of the corresponding tasks is used as
   * a priority.
   * NOTE: As the soonest task execution time (t) is the smallest time between all the
   * task execution times and in the priority queue the element with the biggest prio
   * is at the first place, -t is used as a priority
   */
  private FastLongPriorityQueue m_taskProcs = new FastLongPriorityQueue();

  /**
   * This hashtable contains mapping between SchedulerTaskID objects and priority
   * queue ids and is used to find easily a TaskProcessor by given task id
   */
  private HashMapObjectLong m_taskIdToPQId = new HashMapObjectLong();

  /**
   * This one is used by this thread to tell to the JVM how long the thread should wait
   * before checking, if there is a task for immediate execution.
   */
  private long m_waitTime = 0;

  /**
   * This one is used to tell the thread that it has to be shut down and should stop cycle
   * and exit from the run() method
   */
  private volatile boolean m_exit = false;

  private final TaskExecutor m_taskExecutor;
  
  private SingletonEnvironment m_singletonEnv = null;
  
  
  public TaskProcessorPQ(TaskExecutor executor, SingletonEnvironment env) {
	super("NW Scheduler PriorityQueue");
  	this.m_taskExecutor = executor;
  	this.m_singletonEnv = env;
  }
  
  /**
   * This has to be used for initial start of the thread and for resumning the thread after
   * it has been temporarily stopped by calling the pause() method
   */
  public void start() {
    if (m_exit) {
      return;
    }
    if (!isAlive()) {
      super.start();
    } 
  }

  /**
   * This method does the actual maintenance of the tasks priority queue
   */
  public void run() {	  
    // 'exit' is a flag which tells the thread that it's shutting down and it has 
	// to stop working. It's value is initially false
    while (!m_exit) {
    	TaskStatus taskStatus;
    	TaskProcessor currentTaskProcessor = null;
    	long executionTime = -26; //debug values
    	long baseTime = -26;//debug values
    	synchronized (this) {
	        if (m_waitTime > 0) {
	          try {
	            // the thread waits here to be woken-up or for the soonest task execution time
	        	if (location.beDebug()) { location.debugT("PQ waits for "+m_waitTime+" ms."); }
	            wait(m_waitTime); 
	          } catch (InterruptedException e) {
	            // $JL-EXC$
	          }
	        }
	        // check if the thread shutting down
	        if (m_exit) return;
	        // check if has to wait for undefined time. Two reasons:
	        //    1. The PQ is empty and there is nothing to process
	        while (m_taskProcs.isEmpty()) {
	          try {
	        	if (location.beDebug()) { location.debugT("PQ waits, because PQ is empty."); }
	            wait();
	          } catch (InterruptedException e) {
	            // $JL-EXC$
	          }
	          // check if the thread shutting down
	          if (m_exit) return;
	        }
	        // calculating the soonest task execution time
	        executionTime = -m_taskProcs.getTopPriority();
	        final long currentTime = System.currentTimeMillis();
	        m_waitTime = executionTime - currentTime;
	        baseTime = executionTime;
	        
	        long ignoreBlackoutPeriod = SingletonEnvironment.getLongValueFromProperty(m_singletonEnv.getServiceFrame().getServiceProperties(), 
	        																		  SingletonEnvironment.IGNORE_BLACKOUT_PERIOD_NAME, 
	        																		  SingletonEnvironment.IGNORE_BLACKOUT_PERIOD);
	        
	        if (m_waitTime <= -ignoreBlackoutPeriod) {
	        	// trigger a job instance of this task only when the blackout 
	        	// period is larger than specified in the property ignoreBlackoutPeriod
	        	// and the task has only a single execution time, otherwise skip this
	        	// job instance
	        	SchedulerTask task = ((TaskProcessor)m_taskProcs.getTopPrioElement()).getTask();

	        	if (m_singletonEnv.getScheduler().hasSingleExecutionTime(task)) {
	        		baseTime = currentTime;
	        		if (location.beDebug()) { location.debugT("The next execution of task '"+task.getName()+"' with id '"+task.getTaskId().toString()+"' time points to a time in the past. "+
	        												  "It will be retriggered, because the task has only a single execution time."); }
	        	} else {
	        		// otherwise skip this job execution and reschedule this task, that
	        		// it gets a new next execution time
	        		baseTime = currentTime;
	    	        currentTaskProcessor = (TaskProcessor)m_taskProcs.removeTopPrioElement();
	    	        m_taskIdToPQId.remove(currentTaskProcessor.getTaskId());	    	        
	    	        reSchedule(currentTaskProcessor, baseTime);
	        		
	    	        if (location.beDebug()) { location.debugT("The next execution of task '"+task.getName()+"' with id '"+task.getTaskId().toString()+"' time points to a time in the past and will be skipped, because it has more than one execution time."); }
	    	        
	        		continue;
	        	}
	        } else if (m_waitTime > 500) {
		        // if there is still time to the soonest execution it loops to the begin of the cycle
		        // to wait some more time
	        	
	        	// the maximum wait time should be the WAIT_TIME_DEFAULT
	        	if (m_waitTime > WAIT_TIME_DEFAULT) {
	        		m_waitTime = WAIT_TIME_DEFAULT;
	        	}
	        	
	        	continue;
	        }
	        // further else would mean to continue the job execution as usual 
	        
	        /*
	        if (m_waitTime <= -500) {
	          baseTime = currentTime; // avoid delay accumulation by skipping the lost time
	        } else if (m_waitTime > 500) {
	          // if there is still time to the soonest execution it loops to the begin of the cycle
	          // to wait some more time
	          continue;
	        }
	        */
	        
	        // if we are here it means that the soonest task should be executed immediately
	        // so it's being deleted from the PQ
	        // Note that there is no way the PQ is empty at this point
	
	        currentTaskProcessor = (TaskProcessor)m_taskProcs.removeTopPrioElement();
	        m_taskIdToPQId.remove(currentTaskProcessor.getTaskId());
	        // Checks if the task should be executed
            taskStatus = TaskStatus.active;
    	} //end of synchronized code. Execute the task in not synchronized code. This is necessary because we call back TimeoutExecutor instance
    	  //which may access the database in it own transaction. This could lead to a situation in which the TimeoutExecutor access the database
    	  //and locks some rows. In the same time the scheduler may be accessing the database and may be trying to obtain the lock of the PQ. 
    	  //As the scheduler and the pq access the database and obtain the pq lock in reverse sequence a deadlock is likely to occur. 
        if (currentTaskProcessor.check()) {
        	try {
        		//executeTask would return a state != TaskState.active if an error has occurred and this task should not be executed anymore.
                taskStatus = m_taskExecutor.executeTask(currentTaskProcessor.getTask(), executionTime);
                
                if (location.beDebug()) { location.debugT("Instance of task '"+currentTaskProcessor.getTask().getName()+"' with id '"+currentTaskProcessor.getTask().getTaskId().toString()+"' executed."); }
                
        	} catch (Exception e) {
        		Category.SYS_SERVER.logThrowableT(Severity.ERROR, location, 
						"Error occurred while submitting a job generated for task " + currentTaskProcessor.getTask().getTaskId() +
						" to the Job Execution Runtime. Probably this expiration of the task won't be executed", e);
        	}
        }
        // checks if this task has to be executed at least once more and if it has puts it
        // back into the PQ
        synchronized (this) { //modify the pq in synchronized code so we don't get inconsistency in the pq.
        	if ( TaskStatus.active.equals(taskStatus) ) {
                taskStatus = reSchedule(currentTaskProcessor, baseTime);
                if (location.beDebug()) { location.debugT("Instance of task '"+currentTaskProcessor.getTask().getName()+"' with id '"+currentTaskProcessor.getTask().getTaskId().toString()+"' re-scheduled."); }
        	}
        }
        m_waitTime = 0;
      
      //execute call to taskStateChanged in not synchronized code again for the same reasons as calling executeTask in not synchronized code.	
      if (currentTaskProcessor != null && !TaskStatus.active.equals(taskStatus) ) {
    	  try {
    		  m_taskExecutor.taskStateChanged(currentTaskProcessor.getTask(), taskStatus);
    	  } catch (Exception e) {
  			Category.SYS_SERVER.logThrowableT(Severity.ERROR, location,
					"Error occurred while marking task " + currentTaskProcessor.getTask().getTaskId() + " finished. The task may not have been" +
							" marked finnished and may be executed next time the service starts.", e);
		  }
      }
    }
  }

  
  /**
   * Removes the task processor from the PQ by given taskID
   *
   * @param taskID The id of the task wrapped by the task processor
   *        that should be removed from the PQ
   * @return The removed task processor or null if there is no such
   */
  public synchronized TaskProcessor remove(SchedulerTaskID taskID) {
    if (!m_taskIdToPQId.containsKey(taskID)) return null;
    long PQId = m_taskIdToPQId.get(taskID);
    m_taskIdToPQId.remove(taskID);
    return (TaskProcessor)m_taskProcs.removeElement(PQId);
  }

  /**
   * Removes the task processor from the PQ by given taskID, but
   * does not delete it from the PQ
   *
   * @param taskID The id of the task wrapped by the task processor
   *        that should be get from the PQ
   * @return the task processor from the PQ by given taskID or null
   *         if there is no such
   */
  public synchronized TaskProcessor get(SchedulerTaskID taskID) {
    if (!m_taskIdToPQId.containsKey(taskID)) return null;
    return (TaskProcessor)m_taskProcs.getElement(m_taskIdToPQId.get(taskID));
  }

  /**
   * Schedules the task in the internal PQ
   *
   * @param processor The task processor that wraps the task
   * @return true if the scheduling is successful. false - otherwise
   * (if the task is already in the PQ or if the task that has
   * to be scheduled has no more executions)
   */
  public synchronized boolean schedule(TaskProcessor processor) {
    return schedule(processor, System.currentTimeMillis());
  }

  /**
   * Schedules the task in the internal PQ
   *
   * @param processor The task processor that wraps the task
   * @param baseTime The base time
   * @return true if the scheduling is successful. false - otherwise
   * (if the task is already in the PQ or if the task that has
   * to be scheduled has no more executions)
   */
  public synchronized boolean schedule(TaskProcessor processor, long baseTime) {
    if (m_exit) return false;
    SchedulerTaskID taskID = processor.getTaskId();
    if (m_taskIdToPQId.containsKey(taskID)) return false;
    long nextExec = processor.getNextExecution(baseTime);
    if (nextExec == EntryProcessor.NO_MORE_EXECUTION_TIMES) {
      return false;
    }
    long id = m_taskProcs.enqueue(-nextExec, processor);
    m_taskIdToPQId.put(taskID, id);
    notifyAll();
    return true;
  }

  /**
   * Re-schedules a task back into the pq. this method is called from whitin the
   * run method when a task has been execution and need to be put again in the pq.
   * This method differs from the <c>scheduler(TaskProcessor, long)</c> in two points.
   * First it depends that the task is not found in the pq as it has been removed from
   * the pq when the task was executed. Thus it doesn't perform a check whether the
   * processor is found in the pq. The other difference is that this method calls
   * the method <c>taskFinished(SchedulerTask)</c> on the <c>TaskExecutor</c> 
   * @param processor - the processor to be rescheduled
   * @param baseTime - the base time. This time will be used to calculate the next
   * execution time for this processor
   * @return True if the task has been rescheduled. False if there were no more timeouts for
   * this task and the task needs to be marked finished.
   */
  private TaskStatus reSchedule(TaskProcessor processor, long baseTime) {
  	//no need to check whether this processor is in the pq as we are sure that it's not
  	final long nextExec = processor.getNextExecution(baseTime);
  	if (nextExec == EntryProcessor.NO_MORE_EXECUTION_TIMES) {
  		return TaskStatus.finished;
  	} else {
  		long id = m_taskProcs.enqueue(-nextExec, processor);
  		m_taskIdToPQId.put(processor.getTaskId(), id);
  		notifyAll();
  		return TaskStatus.active;
  	}
  }
  
  /**
   * This should be called when we want to finally stop this thread. After
   * calling this method the thread won't be immediately interrupted - it will
   * finish the processing it's doing if it's not waiting and then will exit from
   * the method run() and it won't be able to start again.
   */
  public void shutDown() {
    m_exit = true;
    synchronized (this) {
      notifyAll();
    }
  }
  
  /**
   * Returns all SchedulerTasks from the PriorityQueue.
   * 
   * @return SchedulerTaskID[]
   */
  protected synchronized SchedulerTaskID[] getAllTaskIDsFromPriorityQueue() {  
      Object[] objArr = m_taskIdToPQId.getAllKeys();
      SchedulerTaskID[] taskIdsArr = new SchedulerTaskID[objArr.length];
      for (int i = 0; i < objArr.length; i++) {
          taskIdsArr[i] = (SchedulerTaskID)objArr[i];
      }
      return taskIdsArr;
  }
  
}
