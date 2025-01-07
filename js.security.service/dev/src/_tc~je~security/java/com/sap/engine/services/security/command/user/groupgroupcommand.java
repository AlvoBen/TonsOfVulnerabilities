package com.sap.engine.services.security.command.user;

import com.sap.engine.interfaces.security.userstore.UserStoreFactory;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import java.io.*;

/**
 *  Groups a group into a group.
 *
 * @author  Jako Blagoev
 * @author  Stephan Zlatarev
 * @version 6.30
 */
public class GroupGroupCommand implements Command {

  private UserStoreFactory usf = null;

  /**
   *  Default constructor.
   *
   * @param  root  the manager to use within execution.
   */
  public GroupGroupCommand(SecurityContext root) {
    this.usf = root.getUserStoreContext();
  }

  /**
   * This method executes the command.  
   * 
   * @param  env - the environment of the corresponding process ,which executes the command
   * @param  is - an input stream for this command
   * @param  os - an output stream for the resusts of this command
   * @param  params - parameters of the command
   */
  public void exec(Environment env, InputStream is, OutputStream os, String[] params) {
    PrintWriter err = new PrintWriter(env.getErrorStream(), true);
    PrintWriter out = new PrintWriter(os, true);

     if ((params.length > 0) && (params[0].equals("-?") || params[0].equalsIgnoreCase("-h") || params[0].equalsIgnoreCase("-help"))) {
      out.println(getHelpMessage());
      return;
    }

    if (params.length != 2) {
      out.println(getHelpMessage());
      return;
    }

    try {
      usf.getActiveUserStore().getGroupContext().addGroupToParent(params[0], params[1]);
    } catch (Exception e) {
      e.printStackTrace();
      err.println(" unable to group the specified group with the parent group.");
      err.println(e.getMessage());
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
    return "Assigns a group to a parent group.\nUsage: GROUP_GROUP <groupName> <parentGroupName>\nParameters:\n\t" +
            "<groupName>       - the name of the child group\n\t" +
            "<parentGroupName> - the name of the parent group";
  }

  /**
   * Gets a name for this command.
   *
   * @return     the message
   */
  public String getName() {
    return "group_group";
  }
  
  public String[] getSupportedShellProviderNames() {
    return null;
  }
  
  
}