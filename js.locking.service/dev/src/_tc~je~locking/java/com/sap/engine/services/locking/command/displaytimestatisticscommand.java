/* @(#) $Id: //InQMy/AppServer/dev/src/server/com/sap/engine/services/locking/command/DisplayTimeStatisticsCommand.java#10 $ SAP*/
package com.sap.engine.services.locking.command;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import com.sap.engine.frame.core.locking.TimeStatisticsEntry;
import com.sap.engine.frame.core.locking.TimeStatisticsHelper;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.locking.LockingApplicationFrame;
import com.sap.exception.standard.SAPIllegalArgumentException;


public class DisplayTimeStatisticsCommand extends AbstractCommand
{ 
  private static final TimeStatisticsHelper HELPER = new TimeStatisticsHelper();
  
  private static final String NAME = "show_locking_stat";
  
  private static final String DESCRIPTION = "Shows statistics for the performance of Locking Manager.";
  
  private static final String USAGE = "Usage: " + NAME + " [options]\n" +
                                      "\n" +
                                      "where options include:\n" +
                                      "    -d -detail   For more detailed statistics\n" +
                                      "\n";  
  
  // =============== implementation of default abstract methods ================
  
  
  public DisplayTimeStatisticsCommand(LockingApplicationFrame lockingApplicationFrame) { super(lockingApplicationFrame); }
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
    OptionParser.FlagOption detail = new OptionParser.FlagOption(new String[] { "d", "detail" });
    OptionParser.Option options[] = new OptionParser.Option[] { detail };

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
      TimeStatisticsEntry entries[] = getLockingRuntime().getAdministrativeLocking().getTimeStatisticsEntries();
      out.println(HELPER.formatTimeStatisticsEntry(entries, detail.getValue()));
    }
    catch (Exception e)
    { 
      displayException(out, e); 
    }
  }
}
