package com.sap.engine.services.rmi_p4.finalizer;

import com.sap.engine.interfaces.cross.Connection;
import com.sap.engine.services.rmi_p4.garbagecollector.finalize.FinalizeInformer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Copyright: Copyright (c) 2003
 * Company: SAP AG - Sap Labs Bulgaria
 *
 * @author Ivan Atanassov
 * @version 7.1
 */
public class StubsFinalizerTest extends TestCase {
  public static long TEST_TIME = 10 * 1000;
  public static int FinalizingThreadsCount = 3;
  public static int FinalizingProducersCount = 2;

  public static void main(String[] args) {
    new StubsFinalizerTest().testFinalizing();
  }

  public void testFinalizing() {
    Connection connection1 = new ConnectionWrapper(true);
    Connection connection2 = new ConnectionWrapper(true);
    ConnectionWrapper connection3 = new ConnectionWrapper(false);
    StubEmulator.workCounter = ConnectionWrapper.invokations = 0;
    ConnectionWrapper.deathInvokations = StubEmulator.sleepForeverCounter = 0;

    FinalizeInformer informer = new FinalizeInformer(10, FinalizingThreadsCount, 2000);

    for(int i = 0; i < FinalizingProducersCount; i++) {
      new StubEmulator(informer, connection1, 10 + i*100, TEST_TIME);
    }

    for(int i = 0; i < FinalizingProducersCount; i++) {
      new StubEmulator(informer, connection2, 1000 + i*1000, TEST_TIME);
    }

    for(int i = 0; i < FinalizingProducersCount; i++) {
      new StubEmulator(informer, connection3, 1000 + i*1000, TEST_TIME);
    }

    boolean result = waitUntilTestFinishWork(informer, TEST_TIME);

    printMainTestResultInfo();

    if (!result) {
//      System.out.println(informer.printCheck());
      fail(informer.printCheck());
    }

    System.out.println("\n\n[Main] > Retest with alive connection.");

    connection3.setAlive(true);

    for(int i = 0; i < FinalizingProducersCount; i++) {
      new StubEmulator(informer, connection3, 10 + i*1000, TEST_TIME);
    }

    result = waitUntilTestFinishWork(informer, TEST_TIME);

    if (!result) {
//      System.out.println(informer.printCheck());
      fail(informer.printCheck());
    }

    printMainTestResultInfo();

    System.out.println("\n\n[Main] > Closes Finalizer.");

    System.out.println("\n" + informer.printCheck());

    informer.close();

    printMainTestResultInfo();
  }

  private void printMainTestResultInfo() {
    System.out.println("\n\n[Main] > Stubs to finalize : " + StubEmulator.workCounter +
                             "\n         Finalized         : " + ConnectionWrapper.invokations +
                             "\n         Finalizes to hang : " + StubEmulator.sleepForeverCounter +
                             "\n         Interupted hangs  : " + ConnectionWrapper.deathInvokations +
                             "\n\n       RESULT : " + (StubEmulator.workCounter.equals(ConnectionWrapper.invokations) && ConnectionWrapper.deathInvokations.equals(StubEmulator.sleepForeverCounter)));
  }

  private boolean waitUntilTestFinishWork(FinalizeInformer informer, long testTime) {
    try {
      Thread.sleep(testTime + 5*1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println("\n\n[Wait Test To Finish] > After test time pass queue size is : " + informer.queueSize());

    long startThreadsRunTime = System.currentTimeMillis();
    while(!informer.isEmptyFinalizeQueue() || !informer.areFreeMessagers()) {
      if (System.currentTimeMillis() > startThreadsRunTime + testTime) {
        System.out.println("[Wait Test To Finish] > Doubled test time exceeeded! Test will fail!");
        informer.printCheck();
        return false;
      }

      try {
        Thread.sleep(5*1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    System.out.println("[Wait Test To Finish] > Test finished for time : " + ((System.currentTimeMillis() - startThreadsRunTime))/1000 + "sec.");
    return (StubEmulator.workCounter.equals(ConnectionWrapper.invokations) && ConnectionWrapper.deathInvokations.equals(StubEmulator.sleepForeverCounter));
  }
  
  /**
   * This method is responsible for global testing of all JUnit tests.
   * Do not delete it!!! <br>
   * It returns an instance of this JUnit as TestSuite.
   * 
   * @return An instance of this JUint test as TestSuite cast to Test.
   */
  public static Test suite() {
    return new TestSuite(StubsFinalizerTest.class);
  }
}
