package com.sap.engine.services.security.command.user;

import com.sap.engine.interfaces.security.userstore.*;
import com.sap.engine.interfaces.security.userstore.context.*;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import java.io.*;

public class GroupInfoCommand implements Command {

  private PrintWriter out = null;
  private UserStoreFactory usf = null;
  /**
   *  Constructs a login command that uses the given LoginManager.
   *
   * @param  root  the login module implementation of the security service.
   */
  public GroupInfoCommand(com.sap.engine.interfaces.security.SecurityContext root) {
    this.usf = root.getUserStoreContext();
  }

  /**
   *  Executes the command.
   *
   * @param  env  the environment of the corresponding process ,which executes the command
   * @param  is   an input stream for this command
   * @param  os   an output stream for the resusts of this command
   * @param  args  parameters of the command
   */
  public void exec(Environment env, InputStream is, OutputStream os, String[] args) {
    UserStore store = null;
    out = new PrintWriter(os, true);

     if ((args.length > 0) && (args[0].equals("-?") || args[0].equalsIgnoreCase("-h") || args[0].equalsIgnoreCase("-help"))) {
      out.println(getHelpMessage());
      return;
    }

    if (args.length < 1 || args.length > 2) {
      out.println(getHelpMessage());
      return;
    }
    
    if (args.length == 1) {
      store = usf.getActiveUserStore();
    } else {
      store = usf.getUserStore(args[0]);
    }
    
    if (store.getGroupContext() == null) {
      out.println("this userstore does not have group context implementation!!");
      return;
    }
    
    GroupInfo info = store.getGroupContext().getGroupInfo(args[args.length - 1]);
    if (info == null) {
      out.println("no group with this name found!");
      return;
    } 
    out.println(info.toString());
  }


  /**
   *  Returns the name of the group of this command.
   *
   * @return  the name of the group of this command.
   */
  public String getGroup() {
    return "user";
  }

  /**
   *  Returns a printable explanation of how the command is used.
   *
   * @return  explanatory printable string.
   */
  public String getHelpMessage() {
    return "Gives information about a user group.\nUsage GROUP [userstoreName] <groupName>\nParameters:\n\t" +
            "[userstoreName] - Specifies the user store of the group. For default value is taken the active user store.\n\t" +
            "<groupName>     - the name of the group within the user store";
  }

  /**
   * Gets a name for this command.
   *
   * @return     the message
   */
  public String getName() {
    return "group";
  }

  public String[] getSupportedShellProviderNames() {
    return null;
  }
}
