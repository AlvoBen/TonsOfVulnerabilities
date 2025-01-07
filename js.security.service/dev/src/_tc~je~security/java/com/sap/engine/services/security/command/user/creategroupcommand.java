package com.sap.engine.services.security.command.user;

import com.sap.engine.interfaces.security.userstore.UserStore;
import com.sap.engine.interfaces.security.userstore.UserStoreFactory;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import java.io.*;

/**
 *  Creates a new group.
 *
 * @author  Jako Blagoev
 * @author  Stephan Zlatarev
 * @version 6.30
 */
public class CreateGroupCommand implements Command {

  private UserStoreFactory usf = null;

  /**
   *  Default constructor.
   *
   * @param  root  manager to use within execution.
   */
  public CreateGroupCommand(SecurityContext root) {
    this.usf = root.getUserStoreContext();
  }

  /**
   * This method executes the command.  
   * 
   * @param  env - the environment of the corresponding process ,which executes the command
   * @param  is - an input stream for this command
   * @param  os - an output stream for the resusts of this command
   * @param  args - parameters of the command
   */
  public void exec(Environment env, InputStream is, OutputStream os, String[] args) {
    UserStore store = null;
    PrintWriter out = new PrintWriter(os, true);

     if ((args.length > 0) && (args[0].equals("-?") || args[0].equalsIgnoreCase("-h") || args[0].equalsIgnoreCase("-help"))) {
      out.println(getHelpMessage());
      return;
    }

    if (args.length != 1) {
      out.println(getHelpMessage());
      return;
    }

    store = usf.getActiveUserStore();

    try {
      store.getGroupContext().createGroup(args[0]);
    } catch (Exception e) {
      out.println(" unable to register new group.");
      out.println(e.getMessage());
      return;
    }
  }
  
  /**
   * Gets a group for this command.
   *
   * @return     the message
   */
  public String getGroup() {
    return "user";
  }
  
  /**
   * Gets a help message for this command.
   *
   * @return     the message
   */
  public String getHelpMessage() {
    return "Creates a group.\nUsage: CREATE_GROUP <name>\nParameters:\n\t<name> - the name of the new group";
  }

  /**
   * Gets a name for this command.
   *
   * @return     the message
   */
  public String getName() {
    return "create_group";
  }
  
  public String[] getSupportedShellProviderNames() {
    return null;
  }
  

}