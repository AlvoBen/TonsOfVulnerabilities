/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/command/AbstractCommand.java#12 $ SAP*/
package com.sap.engine.services.locking.command;


import java.io.PrintStream;

import com.sap.engine.frame.core.locking.LockException;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.frame.core.locking.Util;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.services.locking.LockingApplicationFrame;
import com.sap.engine.services.locking.LockingRuntimeInterface;
import com.sap.exception.standard.SAPIllegalArgumentException;


/**
 * Abstract command, which should be the base-class for all commands.
 */
public abstract class AbstractCommand implements Command
{
  protected static final Util UTIL = new Util();
  
  private static String GROUP = "locking";
    
  /** The LockingApplicationFrame (given in the constructor) */
  private LockingApplicationFrame _lockingApplicationFrame;

  public AbstractCommand(LockingApplicationFrame lockingApplicationFrame)
  {
    _lockingApplicationFrame = lockingApplicationFrame;
  }

  protected LockingApplicationFrame getLockingFrame() 
  { 
    return _lockingApplicationFrame; 
  }
  
  protected LockingRuntimeInterface getLockingRuntime() 
  { 
    return _lockingApplicationFrame.getRuntimeInterface(); 
  }
  
  /** Returns a usage-text. */
  public abstract String getUsage();
  /** Returns a description, but without usage-text. */
  public abstract String getDescription();

  
  /**
   * Gives a short help message about the command
   *
   * @return   A help message for this command.
   */
  public String getHelpMessage()
  {
    return "\n" + getDescription() + "\n" + getUsage();
  }

  /**
   * Returns the name of the group the command belongs to.
   *
   * @return The name of the group of commands, in which this command belongs.
   */
  public String getGroup()
  {
    return GROUP;
  }

  /**
   * Gives the name of the supported shell providers
   *
   * @return The Shell providers' names who supports this command.
   */
  public String[] getSupportedShellProviderNames()
  {
    return new String[] { "InQMyShell" };
  }
  
  
  /**
   * Internally used for a unified exception-message
   */
  protected void displayException(PrintStream out, Exception e) 
  {
    out.println("Error:");
    if (e instanceof LockException || e instanceof SAPIllegalArgumentException || e instanceof TechnicalLockException)
    {
      out.println(e.getMessage());
      out.println("See log for details\n");
    } 
    else  // unexpected Exception -> print StackTrace
    {
      out.println(e.getMessage() + "\n");
      e.printStackTrace(out);
      out.println();
    }
  }
}
