package com.sap.engine.services.security.command.user;

import com.sap.engine.interfaces.security.userstore.UserStoreFactory;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import java.io.*;

/**
 *  Groups a user into a group.
 *
 * @author  Jako Blagoev
 * @author  Stephan Zlatarev
 * @version 6.30
 */
public class GroupUserCommand implements Command {

  private UserStoreFactory usf = null;

  /**
   *  Default constructor.
   *
   * @param  root  the manager to use within execution.
   */
  public GroupUserCommand(SecurityContext root) {
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

    if (params.length != 2) {
      err.println(getHelpMessage());
      return;
    }

    try {
      usf.getActiveUserStore().getGroupContext().addUserToGroup(params[0], params[1]);
    } catch (Exception e) {
      e.printStackTrace();
      err.println(" unable to group user with group.");
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
    return "Assigns a user to a group.\nUsage: GROUP_USER <userName> <groupName>\nParameters:\n\t" +
            "<userName>  - the name of the user\n\t" +
            "<groupName> - the name of the group";
  }

  /**
   * Gets a name for this command.
   *
   * @return     the message
   */
  public String getName() {
    return "group_user";
  }
  
  public String[] getSupportedShellProviderNames() {
    return null;
  }
  
  
}