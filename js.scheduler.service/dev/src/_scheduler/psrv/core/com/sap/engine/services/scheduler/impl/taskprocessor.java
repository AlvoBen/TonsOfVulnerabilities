/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.scheduler.impl;

import com.sap.scheduler.api.SchedulerTask;
import com.sap.scheduler.api.SchedulerTaskID;
import com.sap.scheduler.api.RecurringEntry;
import com.sap.scheduler.api.CronEntry;
import com.sap.engine.services.scheduler.impl.recurring.RecurringProcessor;
import com.sap.engine.services.scheduler.impl.cron.CronProcessor;


/**
 * This class represents a task processor, it contains all the entry processors for a task
 *
 * @author Nikolai Neichev
 */
public class TaskProcessor {
    
  EntryProcessor[] processors;
  SchedulerTask task;
  boolean isActive = false;

  public TaskProcessor() {
    processors = null;
    task = null;
  }

  public TaskProcessor(SchedulerTask task) {
    setTask(task);
    RecurringEntry[] recEntries = task.getRecurringEntries();
    if (recEntries != null) {
      for (int i = 0; i < recEntries.length; i++) {
        addProcessor(new RecurringProcessor(recEntries[i]));
      }
    }
    CronEntry[] cronEntries = task.getCronEntries();
    if (cronEntries != null) {
      for (int i = 0; i < cronEntries.length; i++) {
        addProcessor(new CronProcessor(cronEntries[i]));
      }
    }
  }

  public TaskProcessor(EntryProcessor[] processors) {
    this.processors = processors;
  }

  public void setTask(SchedulerTask task) {
  	this.task = task;
  }
  
  /**
   * Returns the next execution time from the current time on or 
   * EntryProcessor.NO_MORE_EXECUTION_TIMES if there is no next execution time
   * @return the next execution time from the current time on or 
   * EntryProcessor.NO_MORE_EXECUTION_TIMES if there is no next execution time
   */
  public long getNextExecution() {
    long nextExecution = Long.MAX_VALUE;
    for (int i = 0; i < processors.length; i++) {
      long next = processors[i].getNextExecutionTime();
      if ( (next != EntryProcessor.NO_MORE_EXECUTION_TIMES) && (next < nextExecution) ) {
        nextExecution = next;
      }
    }
    if (nextExecution == Long.MAX_VALUE) {
      return EntryProcessor.NO_MORE_EXECUTION_TIMES; // no next execution
    } else {
      return nextExecution;
    }
  }

  /**
   * Returns the next execution time after the specified time or 
   * EntryProcessor.NO_MORE_EXECUTION_TIMES if there is no next execution time
   * @param baseTime the specified time
   * @return the next execution time > baseTime or 
   * EntryProcessor.NO_MORE_EXECUTION_TIMES if there is no next execution time
   */
  public long getNextExecution(long baseTime) {
    long nextExecution = Long.MAX_VALUE;
    for (int i = 0; i < processors.length; i++) {
      long next = processors[i].getNextExecutionTime(baseTime);
      if ( (next != EntryProcessor.NO_MORE_EXECUTION_TIMES) && (next < nextExecution) ) {
        nextExecution = next;
      }
    }
    if (nextExecution == Long.MAX_VALUE) {
      return EntryProcessor.NO_MORE_EXECUTION_TIMES; // no next execution
    } else {
      return nextExecution;
    }
  }

  /**
   * Adds an EntryProcessor to this TaskProcessor
   * @param processor the new processor
   */
  public void addProcessor(EntryProcessor processor) {
    if (processors == null) {
      processors = new EntryProcessor[1];
      processors[0] = processor;
    } else {
      EntryProcessor[] temp = new EntryProcessor[processors.length + 1];
      System.arraycopy(processors, 0, temp, 0, processors.length);
      temp[processors.length] = processor;
      processors = temp;
    }
  }

  /**
   * Getter method
   * @return the task id
   */
  public SchedulerTaskID getTaskId() {
    return task.getTaskId();
  }

  /**
   * Getter method
   * @return the task
   */
  public SchedulerTask getTask() {
    return task;
  }

  /**
   * Setter method
   * @param active isActive value
   */
  public void setActive(boolean active) {
    isActive = active;
  }

  /**
   * Checks whether the task processor is active
   * @return TRUE if active, FALSE if not
   */
  public boolean check() {
    return isActive;
  }

}