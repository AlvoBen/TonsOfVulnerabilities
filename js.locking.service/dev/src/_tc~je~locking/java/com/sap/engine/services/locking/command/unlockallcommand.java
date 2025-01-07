/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/command/UnlockAllCommand.java#16 $ SAP*/
package com.sap.engine.services.locking.command;

import com.sap.engine.services.locking.LockingApplicationFrame;


public class UnlockAllCommand extends AbstractSimpleCommand
{
  private static final String NAME = "remove_all_locks";
  
  private static final String DESCRIPTION = "Removes all locks for a given owner.";
  
  private static final String USAGE = "Usage: " + NAME + " <owner>\n" +
                                      "    <owner>     The owner for which to remove all locks\n";
                                      
                                      
  private static final boolean[] EXEC_PARAMETERS = new boolean[] { true, false, false, false };

  
  // =============== implementation of default abstract methods ================
  
  
  public UnlockAllCommand(LockingApplicationFrame lockingApplicationFrame) { super(lockingApplicationFrame); }
  public final String getName() { return NAME; } 
  public final String getUsage() { return USAGE; } 
  public final String getDescription() { return DESCRIPTION; }
  public final boolean[] getExecParameters() { return EXEC_PARAMETERS; }
  

  // =================== implementation of the exec method =====================


  public final void exec(String owner, String name, String argument, char mode) throws Exception
  {
    getLockingRuntime().unlockAll(owner, false);
//    Properties lockObject = new Properties();
//    lockObject.setProperty(LockingRuntimeInterface.ACTION, LockingRuntimeInterface.OP_DEQALL);
//    lockObject.setProperty(LockingRuntimeInterface.OWNER, owner);
//    getLockingRuntime().genericLockOp(lockObject);
  }
}
