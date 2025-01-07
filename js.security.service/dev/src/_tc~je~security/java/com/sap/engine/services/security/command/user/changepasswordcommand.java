package com.sap.engine.services.security.command.user;

import com.sap.engine.interfaces.security.userstore.*;
import com.sap.engine.interfaces.security.userstore.context.*;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.security.SecurityContextObject;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.engine.services.security.SecurityServerFrame;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 *  Creates a new user or group.
 *
 * @author  Jako Blagoev
 * @version 4.0.3
 */
public class ChangePasswordCommand implements Command {

  private PrintWriter err = null;
  private PrintWriter out = null;
  private UserStoreFactory usf = null;
  private UserInfo info = null;
  private char[] password = null;

  /**
   *  Default constructor.
   *
   * @param  root  the security root context.
   */
  public ChangePasswordCommand(SecurityContext root) {
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
    String name = null;
    out = new PrintWriter(os, true);
    err = new PrintWriter(env.getErrorStream(), true);

    if ((args.length > 0) && (args[0].equals("-?") || args[0].equalsIgnoreCase("-h") || args[0].equalsIgnoreCase("-help"))) {
      out.println(getHelpMessage());
      return;
    }

    if (args.length < 1 || args.length > 3) {
      out.println(getHelpMessage());
      return;
    }

    if (args.length == 3) {
      store = usf.getUserStore(args[0]);
      name = args[1];
      password = args[2].toCharArray();
    } else {
      store = usf.getActiveUserStore();
    }

    if (args.length == 2) {
      name = args[0];
      password = args[1].toCharArray();
    }

    if (args.length == 1) {
      password = args[0].toCharArray();

      ThreadContext context = SecurityServerFrame.threadContext.getThreadContext();

      if (context != null) {
        SecurityContextObject security = (SecurityContextObject) context.getContextObject("security");

        if (security.getSession() != null) {
          name = security.getSession().getPrincipal().getName();
        }
      }
    }

    if (name == null) {
      out.println(" no user selected");
      return;
    }

    info = store.getUserContext().getUserInfo(name);

    if (info == null) {
      out.println("no user with this name found!");
      return;
    }

    try {
      info.setPassword(password);
    } catch (Exception e) {
      err.print(" unable to change password of user: ");
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
    return "Changes the user password.\nUsage: PASSWORD [[userstoreName] userName] <password>\nParameters:\n\t" +
            "[userstoreName] - Specifies the user store of the user. For default value is taken the active user store.\n\t" +
            "[userName]      - Specifies the name of the user within the user store. For default value is taken the current user.\n\t" +
            "<password>      - the new password of the user";
  }

  /**
   * Gets a name for this command.
   *
   * @return     the message
   */
  public String getName() {
    return "password";
  }
  
  public String[] getSupportedShellProviderNames() {
    return null;
  }
  

}