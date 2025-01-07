/*
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.rfcengine;

import java.io.*;
import com.sap.mw.jco.*;
import java.util.Date;

class JCOThroughputManagement
{
    private String prefix =  "JRFC_PERFORMANCE";

    private PrintWriter errorStream;
    private Date myDate;

    private long kNumberOfCalls = 0;
    private long kTotalTime = 0;
    private long kMiddlewareTime = 0;
    private long kHandleRequestTime = 0;
    private long kNumReceivedBytes = 0;
    private long kNumSentBytes = 0;

    private boolean throughputWasCalled = false;
    boolean debug = false;

    private JCO.Throughput throughput = null;

    JCOThroughputManagement(JCO.Throughput th)
    {
      throughput = th;
    }

//    void collect (JCO.Function function)
//    {
//      String functionName = function.getName();
//      if( functionName.equals("JTEST_GET_THROUGHPUT"))
//      {
//          handleGetThroughput( function);
//          return;
//      }
      // use only for standalone throghput tests
//      else if( functionName.equals("JRFC_PERFORMANCE"))
//      {
//          handleJrfcPerformance( function);
//      }
//    }

    void handleGetThroughput( JCO.Function function)
    {

      long numberOfCalls = 0;
      long totalTime = 0;
      long middlewareTime = 0;
      long handleRequestTime = 0;
      long numReceivedBytes = 0;
      long numSentBytes = 0;

      if (throughput == null) return; // if throughput wasn't started
      prefix =  "JTEST_GET_THROUGHPUT";
      throughputWasCalled = true;
      try
      {
        log("Call from the ABAP Stack is being processed by JTEST_GET_THROUGHPUT");

        JCO.ParameterList output = function.getExportParameterList();

        log("Connection requested by FM " + function.getName());

        numberOfCalls = throughput.getNumCalls() - kNumberOfCalls;
        totalTime = throughput.getTotalTime() - kTotalTime;
        middlewareTime = throughput.getMiddlewareTime() - kMiddlewareTime;
        handleRequestTime = throughput.getHandleRequestTime() - kHandleRequestTime;
        numReceivedBytes = throughput.getNumReceivedBytes() - kNumReceivedBytes;
        numSentBytes = throughput.getNumSentBytes() - kNumSentBytes;

        log("Output Parameter : " + numberOfCalls);
        log("Output Parameter : " + totalTime);
        log("Output Parameter : " + middlewareTime);
        log("Output Parameter : " + handleRequestTime);
        log("Output Parameter : " + numReceivedBytes);
        log("Output Parameter : " + numSentBytes);

        output.setValue( numberOfCalls, "NUMBER_OF_CALLS");
        output.setValue( totalTime, "TOTAL_TIME");
        output.setValue( middlewareTime, "MIDDLEWARE_TIME");
        output.setValue( handleRequestTime, "HANDLE_REQUEST_TIME");
        output.setValue( numReceivedBytes, "NUM_RECEIVED_BYTES");
        output.setValue( numSentBytes, "NUM_SENT_BYTES");

        kNumberOfCalls = throughput.getNumCalls();
        kTotalTime = throughput.getTotalTime();
        kMiddlewareTime = throughput.getMiddlewareTime();
        kHandleRequestTime = throughput.getHandleRequestTime();
        kNumReceivedBytes = throughput.getNumReceivedBytes();
        kNumSentBytes = throughput.getNumSentBytes();

        log("Finished JTEST_GET_THROUGHPUT successfully.");

      }//try
      catch (Exception e)
      {
         e.printStackTrace();
         log("Call of JTEST_GET_THROUGHPUT failed",e);
      }//catch

      return;
    }

    void handleJrfcPerformance (JCO.Function function)
   {
      //prefix =  "JRFC_PERFORMANCE";
      try
      {
          log("Call from the ABAP Stack is being processed by JRFC_PERFORMANCE");
          JCO.ParameterList input = function.getImportParameterList();
          JCO.ParameterList output = function.getExportParameterList();

          if (debug) function.writeXML(function.getName()
              +"_"+Thread.currentThread().getName()
              +"_"+System.currentTimeMillis()+"_IN.xml");


          if (function.getName().equals("JRFC_PERFORMANCE"))
          {
              log("Connection requested by FM " + function.getName());

              for(int i = 0; i < input.getFieldCount(); i++)
              {
                  if( input.isInitialized(i))
                  {
                      output.setValue(input.getValue(i),i);
                  }
              }

          }
          if (debug) function.writeXML(function.getName()
              +"_"+Thread.currentThread().getName()
              +"_"+System.currentTimeMillis()+"_OUT.xml");


          log("Finished JRFC_PERFORMANCE successfully.");

      } catch (Exception e) {
         log("Call of JRFC_PERFORMANCE failed",e);
      }

      return;
   }


    // logging
    private void log( String message)
    {
        if (!debug) return;
        myDate.setTime(System.currentTimeMillis());
        errorStream.println( prefix + " - " + myDate + ">> " + message);
        errorStream.flush();
    }

    private void log( String message, Throwable ex)
    {
        if (!debug) return;

        myDate.setTime(System.currentTimeMillis());
        errorStream.println( prefix + " - " + myDate + ">> " + message);
        errorStream.println(ex.getClass().getName() + " occured:");
        errorStream.println("   Message:" + ex.getMessage());
        ex.printStackTrace( errorStream);
        errorStream.flush();
    }

}