/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.command.login;

import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.lib.security.PasswordChangeCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.login.LoginContext;
import java.io.*;
import java.util.Hashtable;

/**
 *  This class uses the login module of the security service to login a user.
 *
 * @author  Svetlana Stancheva
 * @version 4.0.3
 */
public class LoginCommand implements Command, CallbackHandler {

  private SecurityContext root;
  private InputStream in;
  private PrintWriter out = null;

  private String name = null;
  private char[] password = null;

  /**
   *  Constructs a login command that uses the given LoginManager.
   */
  public LoginCommand(SecurityContext root) {
    this.root = root;
  }

  /**
   *  Executes the command.
   *
   * @param  env  the environment of the corresponding process ,which executes the command
   * @param  is   an input stream for this command
   * @param  os   an output stream for the resusts of this command
   * @param  params  parameters of the command
   */
  public void exec(Environment env, InputStream is, OutputStream os, String[] params) {
    in = is;
    out = new PrintWriter(os, true);

    if ((params.length > 0) && (params[0].equals("-?") || params[0].equalsIgnoreCase("-h") || params[0].equalsIgnoreCase("-help"))) {
      out.println(getHelpMessage());
      return;
    }

    try {
      SecurityContext security = root;

      if ((params.length > 1) && (params[0].equals("-template")) && (params[1] != null)) {
        security = root.getPolicyConfigurationContext(params[1]);
      }

      LoginContext login = null;

      /////
      //  logout previous logins
      try {
        login = (LoginContext) ((Hashtable) env.getContext()).get("LOGIN_CONTEXT");

        if (login != null) {
          login.logout();
        }
      } catch (Exception e) {
        new PrintWriter(env.getErrorStream(), true).println(" Unable to log out previous user due to " + e);
      }

      /////
      //  login the user
      login = security.getAuthenticationContext().getLoginContext(null, this);
      login.login();

      ((Hashtable) env.getContext()).put("LOGIN_CONTEXT", login);
      out.println(" user logged in successfully.");
    } catch (Exception ex) {
      PrintWriter err = new PrintWriter(env.getErrorStream(), true);
      err.println("Authorization failed! ");

      if (ex.getMessage() != null) {
        err.println(" reason: " + ex.getMessage());
      }
    } finally {
      name = null;
      password = null;
    }
  }

  public void handle(Callback[] callbacks) {
    for (int i = 0; i < callbacks.length; i++) { 
    	if (callbacks[i] instanceof TextOutputCallback) {
    		TextOutputCallback callback = (TextOutputCallback) callbacks[i];
    		if (callback.getMessageType() == TextOutputCallback.INFORMATION) {
      		writeString(callback.getMessage());
    		} else if (callback.getMessageType() == TextOutputCallback.WARNING) {
    			writeString("Warning: " + callback.getMessage());
    		} else if (callback.getMessageType() == TextOutputCallback.ERROR) {
    			writeString("Error: " + callback.getMessage());
    		}
    	} else if (callbacks[i] instanceof NameCallback) {
        NameCallback callback = (NameCallback) callbacks[i];
     	 	callback.setName(readString(callback.getPrompt()));
      } else if (callbacks[i] instanceof PasswordChangeCallback) {
        PasswordChangeCallback callback = (PasswordChangeCallback) callbacks[i];
        callback.setPassword(readPassword(callback.getPrompt()));
      } else if (callbacks[i] instanceof PasswordCallback) {
        PasswordCallback callback = (PasswordCallback) callbacks[i];
        if (password == null) {
          password = readPassword(callback.getPrompt());
        }
        callback.setPassword(password);
      }
    } 
  }

  private void writeString(String prompt) {
  	try {
  		out.print(prompt + "\n");
    	out.flush();
  	} catch (Exception e) {
      //$JL-EXC$
  		//should not do anything
  	}
  }
  
  private String readString(String prompt) {
    if (name != null) {
      return name;
    }

    try {
      out.print(prompt);
      out.flush();
      LineNumberReader reader = new LineNumberReader(new InputStreamReader(in));
      name = reader.readLine();
    } catch (Exception e) {
      return null;
    }
    return name;
  }

  private char[] readPassword(String prompt) {
    PrintWriter pw = new PrintWriter(out, true);
    pw.print(prompt);
    CamouflageThread camouflage = new CamouflageThread(pw, false);
    camouflage.start();
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    String res;
    try {
      res = reader.readLine();
    } catch (Exception ex) {
      return new char[0];
    } finally {
      camouflage.breakMask();
    }

    return res.toCharArray();
  }

  /**
   *  Returns the name of the group of this command.
   *
   * @return  the name of the group of this command.
   */
  public String getGroup() {
    return "login";
  }

  /**
   *  Returns a printable explanation of how the command is used.
   *
   * @return  explanatory printable string.
   */
  public String getHelpMessage() {
    return "Logs in an user.\nUsage: LOGIN [-template policyConfigurationName]\n" +
            "Parameters:\n\t[-template policyConfigurationName] - Specifys the name of" +
            " the policy configuration which the user will be logged in for. Default value is 'SAP_J2EE_ENGINE'";
  }

  /**
   * Gets a name for this command.
   *
   * @return     the message
   */
  public String getName() {
    return "login";
  }

  public String[] getSupportedShellProviderNames() {
    return null;
  }

}




class CamouflageThread extends Thread {

  private static final String THREAD_NAME = "security:Login-Commmand:";

  protected String CAMOUFLAGE = "\b ";
  protected boolean isStop = false;
  protected PrintWriter os;
  protected boolean isRemote;

  public CamouflageThread(PrintWriter os, boolean isRemote) {
    this.os = os;
    this.isRemote = isRemote;

    os.flush();
  }

  public void run() {
    Thread thread = Thread.currentThread();
    String threadName = thread.getName();
    thread.setName(THREAD_NAME + threadName);

    try {
      if (!isRemote) {
        isStop = false;

        while (!isStop) {
          os.print(CAMOUFLAGE);
          os.flush();
          try {
            sleep(1);
          } catch (InterruptedException inEx) {
            return;
          }
        }
      } else {
        camouflageCommand();
      }
    } finally {
      setName(threadName);
    }
  }

  public void breakMask() {
    isStop = true;

    if (isRemote) {
      camouflageCommand();
    }
  }

  private void camouflageCommand() {
    try {
      os.write(0);
      os.write(8);
      os.flush();
    } catch (Exception ex) {
      return;
    }
  }

}

