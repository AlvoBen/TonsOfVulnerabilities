/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/command/GetLocksCommand.java#16 $ SAP*/
package com.sap.engine.services.locking.command;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;

import com.sap.engine.frame.core.locking.LockEntry;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.locking.LockingApplicationFrame;
import com.sap.engine.services.locking.LockingRuntimeInterface;
import com.sap.exception.standard.SAPIllegalArgumentException;


public class GetLocksCommand extends AbstractCommand
{
  private static final String NAME = "show_locks";
  
  private static final String DESCRIPTION = "Shows a list of all currently existing locks.";
  
  private static final String USAGE = "Usage: " + NAME + " [options]\n" +
                                      "\n" +
                                      "where options include:\n" +
                                      "    -n NAME     -name NAME            The name, for which to display the locks\n" +
                                      "    -a ARGUMENT -argument ARGUMENT    The argument, for which to display the locks\n" +
                                      "    -u USER     -user USER            The user, for which to display the locks" +    
                                      "\n";  

  // =============== implementation of default abstract methods ================
  
  
  public GetLocksCommand(LockingApplicationFrame lockingApplicationFrame) { super(lockingApplicationFrame); }
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
    OptionParser.ValueOption name = new OptionParser.ValueOption(new String[] { "n", "name" });
    OptionParser.ValueOption argument = new OptionParser.ValueOption(new String[] { "a", "argument" });
    OptionParser.ValueOption user = new OptionParser.ValueOption(new String[] {"u", "user"});
    OptionParser.Option options[] = new OptionParser.Option[] { name, argument, user };

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
      //LockEntry locks[] = getLockingRuntime().getAdministrativeLocking().getLocks();
      //displayLocks(out, locks);
      
      Properties[] lockEntry = getLockingRuntime().getLocks(name.getValue(), argument.getValue(), user.getValue());
      displayLocks(out, lockEntry);
      
    }
    catch (Exception e)
    { 
      displayException(out, e); 
    }
  }
  
  /**
   * Prints all locks on the PrintStream.
   */
  private void displayLocks(PrintStream out, LockEntry locks[])
  {
    for (int i = 0; i < locks.length; i++)
    {
      LockEntry next = locks[i];
      out.println(next.getUser() + " | " + next.getOwner()+ " | " + next.getMode() + " | " + next.getCount());
      out.println(" "            + " | " + next.getName());
      out.println(" "            + " | " + next.getArgument());
    }
  }
  
  private void displayLocks(PrintStream out, Properties[] locks) {
    for (int i = 0; i < locks.length; i++)
        {
          Properties next = locks[i];
          out.println(next.getProperty(LockingRuntimeInterface.USER) + " | " + next.getProperty(LockingRuntimeInterface.OWNER) + " | " +
          next.getProperty(LockingRuntimeInterface.MODE) + " | " + next.getProperty(LockingRuntimeInterface.COUNT));
          out.println(" "  + " | " +  next.getProperty(LockingRuntimeInterface.NAME));
          out.println(" "  + " | " +  next.getProperty(LockingRuntimeInterface.ARGUMENT));
        }
    
  }
}
