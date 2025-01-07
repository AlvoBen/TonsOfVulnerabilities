/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/command/UnlockCumulativeCommand.java#13 $ SAP*/
package com.sap.engine.services.locking.command;

import com.sap.engine.services.locking.LockingApplicationFrame;


public class UnlockCumulativeCommand extends AbstractSimpleCommand
{
  private static final String NAME = "remove_lock_completely";
  
  private static final String DESCRIPTION = "Removes one lock completely (sets the cumulative count to 0).";
  
  private static final String USAGE = "Usage: " + NAME + " <owner> <name> <argument> <mode>\n" +
                                      "    <owner>     The owner for which to remove the lock\n" +
                                      "    <name>      The name of the lock\n" +
                                      "    <argument>  The argument of the lock\n" +
                                      "    <mode>      The mode of the lock\n" +
                                      "                valid modes are  S = shared\n" +
                                      "                                 E = exclusive cummulative\n" +
                                      "                                 X = exclusive non-cummulative\n" +
                                      "                                 O = optimistic\n" +                                      
                                      "\n";  
                                      
  private static final boolean[] EXEC_PARAMETERS = new boolean[] { true, true, true, true };

  
  // =============== implementation of default abstract methods ================
  
  
  public UnlockCumulativeCommand(LockingApplicationFrame lockingApplicationFrame) { super(lockingApplicationFrame); }
  public final String getName() { return NAME; } 
  public final String getUsage() { return USAGE; } 
  public final String getDescription() { return DESCRIPTION; }
  public final boolean[] getExecParameters() { return EXEC_PARAMETERS; }
  

  // =================== implementation of the exec method =====================


  public final void exec(String owner, String name, String argument, char mode) throws Exception
  {
    getLockingRuntime().unlockAllCumulativeCounts(owner, name, argument, mode); 
//    Properties lockObject = new Properties();
//    lockObject.setProperty(LockingRuntimeInterface.ACTION, LockingRuntimeInterface.OP_REMOVE);
//    lockObject.setProperty(LockingRuntimeInterface.OWNER, owner);
//    lockObject.setProperty(LockingRuntimeInterface.NAME, name);
//    lockObject.setProperty(LockingRuntimeInterface.ARGUMENT, argument);
//    lockObject.setProperty(LockingRuntimeInterface.MODE, String.valueOf(mode));
//    getLockingRuntime().genericLockOp(lockObject);
  }
}
