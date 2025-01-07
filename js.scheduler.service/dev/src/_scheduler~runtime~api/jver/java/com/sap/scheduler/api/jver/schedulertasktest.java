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
package com.sap.scheduler.api.jver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.TimeZone;

import com.sap.scheduler.api.CronEntry;
import com.sap.scheduler.api.Filter;
import com.sap.scheduler.api.RecurringEntry;
import com.sap.scheduler.api.SchedulerTask;
import com.sap.scheduler.api.SchedulerTaskID;
import com.sap.scheduler.api.SchedulerTime;
import com.sap.scheduler.runtime.JobDefinitionID;
import com.sap.scheduler.runtime.JobParameter;
import com.sap.scheduler.runtime.JobParameterDefinition;
import com.sap.scheduler.runtime.JobParameterType;
import com.sap.tc.jtools.jver.framework.Test;


public class SchedulerTaskTest extends Test {

  public void test_Constructor1() {
    SchedulerTaskID newTaskId = SchedulerTaskID.newID();
    JobDefinitionID newJobDefinitionId = JobDefinitionID.newID();
    JobParameter[] jp = new JobParameter[0];

    SchedulerTime currentTime = new SchedulerTime(new Date(), TimeZone.getDefault());
    RecurringEntry recurringEntry = new RecurringEntry(currentTime, 60 * 1000, 5);

    try {
      new SchedulerTask(null, newJobDefinitionId, jp, new RecurringEntry[]{recurringEntry}, null);
      npeNotThrown("scheduler task id");
    } catch (NullPointerException npe) {
      npeThrown();
    }

    try {
      new SchedulerTask(newTaskId, null, jp, new RecurringEntry[]{recurringEntry}, null);
      npeNotThrown("job definition id");
    } catch (NullPointerException npe) {
      npeThrown();
    }
/*
    try {
      new SchedulerTask(newTaskId, newJobDefinitionId, null, new RecurringEntry[]{recurringEntry}, null);
      npeNotThrown("job parameters array");
    } catch (NullPointerException npe) {
      npeThrown();
    }
*/
    try {
      new SchedulerTask(newTaskId, newJobDefinitionId, jp, null, null);
      verify(false, "IllegalArgumentException was not thrown although both recurring entries and cron entries wre null");
      npeNotThrown("recurring entry and cron entry");
    } catch (IllegalArgumentException ise) {
    	verify(true, "IllegalArgumentException thrown as expected");
    }
    
    try {
    	new SchedulerTask(newTaskId, newJobDefinitionId, jp, new RecurringEntry[0], new CronEntry[0]);
    	verify(false, "IllegalArgumentException was not thrown although both recurring entries and cron entries were zero sized");
    } catch (IllegalArgumentException iae) {
    	verify(true, "IllegalArgumentExeption thrown as expected");
    }

    try {
      new SchedulerTask(newTaskId, newJobDefinitionId, jp, new RecurringEntry[]{recurringEntry}, null);
    } catch (Exception e) {
      this.logStackTrace(e);
      verify(false, "exception was thrown from constructor while all passed parameters were ok");
    }
  }

  public void test_Constuctor2() {
    SchedulerTaskID newTaskId = SchedulerTaskID.newID();
    JobDefinitionID newJobDefinitionId = JobDefinitionID.newID();
    JobParameter[] jp = new JobParameter[0];

    SchedulerTime currentTime = new SchedulerTime(new Date(), TimeZone.getDefault());
    RecurringEntry recurringEntry = new RecurringEntry(currentTime, 60 * 1000, 5);

    try {
      new SchedulerTask(newTaskId, newJobDefinitionId, jp, new RecurringEntry[]{recurringEntry}, null, null, -26);
      verify(false, "IllegalArgumentException was not thrown when -26 was passed for retention period in constructor 2");
    } catch (IllegalArgumentException iae) {
      verify(true, "IllegalArgumentException thrown as expected");
    }

    try {
      new SchedulerTask(newTaskId, newJobDefinitionId, jp, new RecurringEntry[]{recurringEntry}, null, null, 0);
    } catch (Exception e) {
      this.logStackTrace(e);
      verify(false, "Exception was thrown when all parameters passed were ok");
    }

    //test that job parameters array is cloned
    JobParameterDefinition jpd = newJobParameterDefinition();
    JobParameter[] shouldClone = new JobParameter[]{new JobParameter(jpd, new Integer(10))};
    SchedulerTask schedulerTask = new SchedulerTask(newTaskId, newJobDefinitionId, shouldClone, new RecurringEntry[]{recurringEntry}, null);
    shouldClone[0] = null;
    verify(schedulerTask.getJobParameters()[0] != null, "SchedulerTask didn't clone the passed parameters array");

    //test that filter array is cloned
    SchedulerTime startTime = new SchedulerTime(new Date(), TimeZone.getDefault());
    SchedulerTime endTime = new SchedulerTime(new Date(), TimeZone.getDefault());
    Filter[] filters = new Filter[]{new Filter(startTime, endTime)};
    schedulerTask = new SchedulerTask(newTaskId, newJobDefinitionId, shouldClone, new RecurringEntry[]{recurringEntry}, null, filters, 0);
    filters[0] = null;
    verify(schedulerTask.getFilters()[0] != null, "SchedulerTask didn't clone the passed filters array");
  }

  private JobParameterDefinition newJobParameterDefinition() {
    JobParameterDefinition jpd = new JobParameterDefinition();
    jpd.setName("TestParamDefinition");
    jpd.setDirection("in");
    jpd.setType(JobParameterType.INTEGER);
    return jpd;
  }

  public void test_isRetentionDefault() {
    SchedulerTaskID newTaskId = SchedulerTaskID.newID();
    JobDefinitionID newJobDefinitionId = JobDefinitionID.newID();
    JobParameter[] jp = new JobParameter[0];
    SchedulerTime currentTime = new SchedulerTime(new Date(), TimeZone.getDefault());
    RecurringEntry recurringEntry = new RecurringEntry(currentTime, 60 * 1000, 5);

    boolean isDefault = new SchedulerTask(newTaskId, newJobDefinitionId, jp, new RecurringEntry[]{recurringEntry}, null)
		.getRetentionPeriod() == -2;
    verify(isDefault, "getRetentionPeriod() returned value != -2 when constructor 1 was used and thus no retention has been set");
  }

  public void test_getRetentionPeriod() {
    SchedulerTaskID newTaskId = SchedulerTaskID.newID();
    JobDefinitionID newJobDefinitionId = JobDefinitionID.newID();
    JobParameter[] jp = new JobParameter[0];

    SchedulerTime currentTime = new SchedulerTime(new Date(), TimeZone.getDefault());
    RecurringEntry recurringEntry = new RecurringEntry(currentTime, 60 * 1000, 5);

    SchedulerTask testObject = new SchedulerTask(newTaskId, newJobDefinitionId, jp, new RecurringEntry[]{recurringEntry}, null);
    testObject = new SchedulerTask(newTaskId, newJobDefinitionId, jp, new RecurringEntry[]{recurringEntry}, null, null, 21);
    try {
      int retention = testObject.getRetentionPeriod();
      verify(retention == 21, "Returned retention was not the one passed in the constructor");
    } catch (IllegalStateException iae) {
      verify(false, "IllegalStateException thrown from getRetentionPeriod() although retention has been set");
    }
  }

  public void test_getFilters() {
    SchedulerTaskID newTaskId = SchedulerTaskID.newID();
    JobDefinitionID newJobDefinitionId = JobDefinitionID.newID();
    JobParameter[] jp = new JobParameter[0];
    SchedulerTime currentTime = new SchedulerTime(new Date(), TimeZone.getDefault());
    RecurringEntry recurringEntry = new RecurringEntry(currentTime, 60 * 1000, 5);

    SchedulerTask testObject = new SchedulerTask(newTaskId, newJobDefinitionId, jp, new RecurringEntry[]{recurringEntry}, null, new Filter[0], 0);
    Filter[] f1 = testObject.getFilters();
    Filter[] f2 = testObject.getFilters();
    verify(f1 != f2, "Scheduler task didn't return two different array instances by two consequtive calls to getFilters." +
            " Thus the underlying array is not clone by the getFilters() method");
  }

  public void test_getParameters() {
    SchedulerTaskID newTaskId = SchedulerTaskID.newID();
    JobDefinitionID newJobDefinitionId = JobDefinitionID.newID();
    JobParameter[] jp = new JobParameter[0];
    SchedulerTime currentTime = new SchedulerTime(new Date(), TimeZone.getDefault());
    RecurringEntry recurringEntry = new RecurringEntry(currentTime, 60 * 1000, 5);

    SchedulerTask testObject = new SchedulerTask(newTaskId, newJobDefinitionId, jp, new RecurringEntry[]{recurringEntry}, null);
    JobParameter[] parms1 = testObject.getJobParameters();
    JobParameter[] parms2 = testObject.getJobParameters();
    verify(parms1 != parms2, "Scheduler task didn't return two different array instance by two consequtive calls to" +
            "getJobParameters(). Thus the underlying array is not clone by the getJobParameters() method");
  }

  private void npeNotThrown(String nullParam) {
    verify(false, "SchedulerTask constructor didn't throw NullPointerException when null was passed for " + nullParam);
  }

  private void npeThrown() {
    verify(true, "NullPointerException thrown as expected");
  }

  public void test_isSerializable() throws IOException, ClassNotFoundException {
//    try {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(bos);
      SchedulerTaskID newTaskId = SchedulerTaskID.newID();
      JobDefinitionID newJobDefinitionId = JobDefinitionID.newID();
      JobParameter[] jp = new JobParameter[0];
      String cron_entry = "*/13:*:*:2,5:8-12,16-18:*/15,32";
      CronEntry entry = new CronEntry(cron_entry);

      SchedulerTask task = new SchedulerTask(newTaskId, newJobDefinitionId, jp, null, new CronEntry[]{entry});
      System.out.println(task.getCronEntries()[0].toString());
      oos.writeObject(task);
      ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
      ObjectInputStream ois = new ObjectInputStream(bis);
      SchedulerTask _task = (SchedulerTask) ois.readObject();
      String _cron_entry = _task.getCronEntries()[0].persistableValue();
      if (cron_entry.equalsIgnoreCase(_cron_entry)) {
        verify(true, "Serialisable ok");
      } else {
        verify(false, "Serialisation problem");
      }
//    } catch (Exception e) {
//      verify(false, "Exception " + e.getMessage());
//    }
  }

}
