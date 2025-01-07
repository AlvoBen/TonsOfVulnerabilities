/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/command/LockCommand.java#14 $ SAP*/
package com.sap.engine.services.locking.command;

import com.sap.engine.services.locking.LockingApplicationFrame;


public class LockCommand extends AbstractSimpleCommand
{
  private static final String NAME = "add_lock";
  
  private static final String DESCRIPTION = "Adds one lock for the given owner.";
  
  private static final String USAGE = "Usage: " + NAME + " <owner> <name> <argument> <mode>\n" +
                                      "    <owner>     The owner for which to add the lock\n" +
                                      "    <name>      The name of the lock\n" +
                                      "    <argument>  The argument of the lock\n" +
                                      "    <mode>      The mode of the lock\n" +
                                      "                valid modes are  S = shared\n" +
                                      "                                 E = exclusive cummulative\n" +
                                      "                                 X = exclusive non-cummulative\n" +
                                      "                                 O = optimistic\n" +                                      "                                 R = optimistic to exclusive cummulative\n" +
                                      "                                 U = check exclusive non-cummulative\n" +
                                      "                                 V = check exclusive cummulative\n" +
                                      "                                 W = check shared\n" +
                                      "\n";  
                                      
  private static final boolean[] EXEC_PARAMETERS = new boolean[] { true, true, true, true };

  
  // =============== implementation of default abstract methods ================
  
  
  public LockCommand(LockingApplicationFrame lockingApplicationFrame) { super(lockingApplicationFrame); }
  public final String getName() { return NAME; } 
  public final String getUsage() { return USAGE; } 
  public final String getDescription() { return DESCRIPTION; }
  public final boolean[] getExecParameters() { return EXEC_PARAMETERS; }
  

  // =================== implementation of the exec method =====================


  public final void exec(String owner, String name, String argument, char mode) throws Exception
  {
    getLockingRuntime().lock(owner, name, argument, mode);
//    Properties lockObject = new Properties();
//    lockObject.setProperty(LockingRuntimeInterface.ACTION, LockingRuntimeInterface.OP_ENQ);
//    lockObject.setProperty(LockingRuntimeInterface.OWNER, owner);
//    lockObject.setProperty(LockingRuntimeInterface.NAME, name);
//    lockObject.setProperty(LockingRuntimeInterface.ARGUMENT, argument);
//    lockObject.setProperty(LockingRuntimeInterface.MODE, String.valueOf(mode));
//    getLockingRuntime().genericLockOp(lockObject);
  }
}
