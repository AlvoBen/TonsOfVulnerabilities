/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/command/DisplayUniqueNumberCommand.java#5 $ SAP*/
package com.sap.engine.services.locking.command;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.locking.LockingApplicationFrame;
import com.sap.exception.standard.SAPIllegalArgumentException;


public class DisplayUniqueNumberCommand extends AbstractCommand
{
  private static final String NAME = "gen_unique_number";
  
  private static final String DESCRIPTION = "Generates a unique increasing number. This command is mainly for test- and debugging-purposes.";
  
  private static final String USAGE = "Usage: " + NAME + "\n";  


  // =============== implementation of default abstract methods ================
  
  
  public DisplayUniqueNumberCommand(LockingApplicationFrame lockingApplicationFrame) { super(lockingApplicationFrame); }
  public final String getName() { return NAME; } 
  public final String getUsage() { return USAGE; } 
  public final String getDescription() { return DESCRIPTION; }


  // =================== implementation of the exec method =====================


  /**
   * A method that executes the command .
   *
   * @param   environment  An implementation of Environment.
   * @param   is  The InputStream , used by the command.
   * @param   os  The OutputStream , used by the command.
   * @param   params  Parameters of the command.
   */
  public void exec(Environment env, InputStream is, OutputStream os, String[] params)
  {
    PrintStream out = new PrintStream(os);

    // parse parameters
    OptionParser.Option options[] = new OptionParser.Option[] { };

    try
    {
      String remaining[] = OptionParser.parse(params, options);
      if (remaining.length > 0)
        throw new SAPIllegalArgumentException(SAPIllegalArgumentException.PARAMETER_TOO_MANY);
    }
    catch (Exception e)
    {
      out.println("Syntax error in command " + getName());
      out.println(getUsage());
      return;
    }
    
    // exec command
    try
    {
      long number = getLockingRuntime().getAdministrativeLocking().getUniqueIncreasingNumber();
      out.println(number);
    }
    catch (Exception e)
    { 
      displayException(out, e); 
    }
  }
}
