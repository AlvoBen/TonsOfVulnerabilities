package com.sap.engine.services.httpserver.server.shellcommands;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Vector;

import com.sap.engine.boot.SystemProperties;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.httpserver.server.logongroups.LogonGroup;
import com.sap.engine.services.httpserver.server.logongroups.LogonGroupsManager;

/**
 * Shell command for managing the logon groups
 * 
 * @author Violeta Uzunova (I024174)
 */
public class LogonGroupCommand implements Command {

  private static String PARAM_ADD = "-add";
  private static String PARAM_REMOVE = "-remove";
  private static String PARAM_DETAILS = "-details";
  private static String PARAM_ADD_INSTANCE = "-addInstance";
  private static String PARAM_REMOVE_INSTANCE = "-removeInstance";
  private static String PARAM_ADD_ALIAS = "-addAlias";
  private static String PARAM_REMOVE_ALIAS = "-removeAlias";
  private static String PARAM_ADD_PREFIX = "-addPrefix";
  private static String PARAM_REMOVE_PREFIX = "-removePrefix";
    
  private LogonGroupsManager logonGroupsManager;
  
  /**
   * Constructor
   * @param logonGroupsManager
   */
  public LogonGroupCommand(LogonGroupsManager logonGroupsManager) {
    this.logonGroupsManager = logonGroupsManager;
  }

  /**
   * @see com.sap.engine.interfaces.shell.Command#exec(com.sap.engine.interfaces.shell.Environment env, java.io.InputStream is, java.io.OutputStream os, java.lang.String[] params)
   */
  public void exec(Environment env, InputStream is, OutputStream os, String[] params) {
    PrintWriter pw = new PrintWriter(os, true);
    int count = params.length;
    
    boolean add = false;      //indicates that param -add is used
    boolean remove = false;   //indicates that param -remove is used
    boolean details = false;  //indicates that param -details is used    
    String lgName = null;    
    String instanceName = null;    
    int instance = 0;          //if -1 the instance has to be removed; if 1 - add instance
    String aliasName = null;
    int alias = 0;             //if -1 the alias has to be removed; if 1 - add alias
    String prefixName = null;
    int prefix = 0;            //if -1 the prefix has to be removed; if 1 - add prefix
    
    if(count > 0 && (params[0].equalsIgnoreCase("-H") || params[0].equals("-?") || params[0].equalsIgnoreCase("-help"))) {      
      pw.println(getHelpMessage());
      return;
    } else {
      try {
        for (int c = 0; c < count; c++) {
          if (PARAM_ADD.equalsIgnoreCase(params[c])) {
            if (add) {
              throw new Exception("Parameter " + PARAM_ADD + " specified twice");
            }
            add = true;
          } else if (PARAM_REMOVE.equalsIgnoreCase(params[c])) {
            if (remove) {
              throw new Exception("Parameter " + PARAM_REMOVE + " specified twice");
            }
            remove = true;
          } else if (PARAM_DETAILS.equalsIgnoreCase(params[c])) {
            if (details) {
              throw new Exception("Parameter " + PARAM_DETAILS + " specified twice");
            }
            details = true;
          } else if (PARAM_ADD_INSTANCE.equalsIgnoreCase(params[c])) {
            if (instanceName != null) {
              throw new Exception("Instance name specified twice");
            }
            if (params.length > c) {  //TODO
              instanceName = params[++c];
              instance++;
            } else {
              throw new Exception("No instance name");
            }            
          } else if (PARAM_REMOVE_INSTANCE.equalsIgnoreCase(params[c])) {
            if (instanceName != null) {
              throw new Exception("Instance name specified twice");
            }
            if (params.length > c) {  //TODO
              instanceName = params[++c];
              instance--;
            } else {
              throw new Exception("No instance name");
            }
          } else if (PARAM_ADD_ALIAS.equalsIgnoreCase(params[c])) {
            if (aliasName != null) {
              throw new Exception("Alias name specified twice");
            }
            if (params.length > c) {  //TODO
              aliasName = params[++c];
              alias++;
            } else {
              throw new Exception("No alias name");
            }            
          } else if (PARAM_REMOVE_ALIAS.equalsIgnoreCase(params[c])) {
            if (aliasName != null) {
              throw new Exception("Alias name specified twice");
            }
            if (params.length > c) {  //TODO
              aliasName = params[++c];
              alias--;
            } else {
              throw new Exception("No alias name");
            }

          } else if (PARAM_ADD_PREFIX.equalsIgnoreCase(params[c])) {
            if (prefixName != null) {
              throw new Exception("Prefix name specified twice");
            }
            if (params.length > c) {  //TODO
              prefixName = params[++c];
              prefix++;
            } else {
              throw new Exception("No alias name");
            }            
          } else if (PARAM_REMOVE_PREFIX.equalsIgnoreCase(params[c])) {
            if (prefixName != null) {
              throw new Exception("Prefix name specified twice");
            }
            if (params.length > c) {  //TODO
              prefixName = params[++c];
              prefix--;
            } else {
              throw new Exception("No alias name");
            }
          } else {
            if (lgName != null) {
              throw new Exception("Unknown parameter: " + params[c]);
            }
            lgName = params[c];
          }
        }

        if (lgName == null && (add || remove || instanceName != null || aliasName != null || prefixName != null)) {
          throw new Exception("Logon group name not specified");
        }
      } catch (OutOfMemoryError e) {
        throw e;
      } catch (ThreadDeath e) {
        throw e;
      } catch (Throwable e) {
        pw.println("ERROR: " + e.getMessage());        
        pw.println();
        pw.println(getHelpMessage());
        return;
      }
    }
    
    try {     
      if (lgName == null) {
        LogonGroup[] logonGroups = logonGroupsManager.getAllLogonGroups();
        if (details) {          
          for (int i = 0; i < logonGroups.length; i++) {
            list(pw, logonGroups[i]);
          }          
        } else {          
          for (int i = 0; i < logonGroups.length; i++) {
            pw.println("  " + logonGroups[i].getLogonGroupName());
          }     
        }
      } else {
        if (add) {
          if (logonGroupsManager.getLogonGroup(lgName) != null) {
            throw new Exception("Logon group " + lgName + " already exists.");
          }
          logonGroupsManager.registerLogonGroup(lgName);            
          list(pw, logonGroupsManager.getLogonGroup(lgName));         
        } else  {
          LogonGroup logonGroup = logonGroupsManager.getLogonGroup(lgName);
          if (logonGroup == null) {
            throw new Exception("Logon group " + lgName + " does not exist.");      
          }
          
          if (remove) {       
            logonGroupsManager.unregisterLogonGroup(lgName);
            pw.println("Logon group " + lgName + " successfully removed");                     
          } else if (details) {
            list(pw, logonGroup);
          } else if (instanceName == null && aliasName == null && prefixName == null && lgName != null) {  //logon_group <lgName>
            list(pw, logonGroup);
          } else {
            if (instance > 0) {
              logonGroup.addInstance(instanceName);
              list(pw, logonGroup);
            }
  
            if (instance < 0) {
              logonGroup.removeInstance(instanceName);
              list(pw, logonGroup);
            }
            
            if (alias > 0) {
              logonGroup.addAlias(aliasName);
              list(pw, logonGroup);
            }
            
            if (alias < 0) {
              logonGroup.removeAlias(aliasName);
              list(pw, logonGroup);
            }
            
            if (prefix > 0) {
              logonGroup.addExactAlias(prefixName);
              list(pw, logonGroup);
            }
      
            if (prefix < 0) {
              logonGroup.removeExactAlias(prefixName);
              list(pw, logonGroup);
            }
          } //if remove || details || opreration
        } // if (add)
      } // if (lgName == null)
    } catch (OutOfMemoryError e) {
      throw e;
    } catch (ThreadDeath e) {
      throw e;
    } catch (Throwable e) {
      pw.println("ERROR: " + e.getMessage());
    }
  }

  /**
   * @see com.sap.engine.interfaces.shell.Command#getGroup()
   */
  public String getGroup() {    
    return "http";
  }

  /**
   * @see com.sap.engine.interfaces.shell.Command#getHelpMessage()
   */
  public String getHelpMessage() {
    String nl = SystemProperties.getProperty("line.separator");
    return "Manages logon groups" + nl +
           "USAGE: " + getName() + " <lgName> [" + PARAM_ADD + "] [" + PARAM_REMOVE + "] [" + PARAM_DETAILS + "] " + nl +
           "                       [-addInstance | -removeInstance <instance>]" + nl +
           "                       [-addAlias | -removeAlias <aliasName>]" + nl +
           "                       [-addPrefix | -addPrefix <prefixName>]" + nl +           
           nl +
           "Parameters:" + nl +
           "  <lgName>                       The name of the logon group" + nl +
           "  " + PARAM_ADD + "                           Adds a new logon group with specified name" + nl +
           "  " + PARAM_REMOVE + "                        Removes the specified logon group" + nl +
           "  " + PARAM_DETAILS + "                       Lists all information for the specified logon" + nl +
           "                                 group, if used with logon group name or lists" + nl +
           "                                 all logon groups with the detailed information" + nl +
           "                                 for them if no logon group name is specified" + nl +
           "  " + PARAM_ADD_INSTANCE + " <instance>        Adds the specified instance to the logon group" + nl +
           "  " + PARAM_REMOVE_INSTANCE + " <instance>     Removes the specified instance from the logon" + nl +
           "                                 group" + nl +
           "  " + PARAM_ADD_ALIAS + " <alaisName>          Adds the specified alias to the logon group" + nl +
           "  " + PARAM_REMOVE_ALIAS + " <alaisName>       Removes the specified alias from the logon" + nl + 
           "                                 group" + nl +
           "  " + PARAM_ADD_PREFIX + " <prefixName>        Adds the specified URL prefix to the logon" + nl +
           "                                 group" + nl +
           "  " + PARAM_REMOVE_PREFIX + " <prefixName>     Removes the specified URL prefix from the" + 
           "                                 logon group" + nl +
           nl +
           "Used without parameters the command lists all logon groups" + nl +
           "If only <lgName> is specified the command lists all properties of the logon group" + nl;
  }

  /**
   * @see com.sap.engine.interfaces.shell.Command#getName()
   */
  public String getName() {    
    return "LOGON_GROUP";
  }

  /**
   * @see com.sap.engine.interfaces.shell.Command#getSupportedShellProviderNames()
   */
  public String[] getSupportedShellProviderNames() {
    return new String[] {"InQMyShell"};
  }
  
  private void list(PrintWriter pw, LogonGroup logonGroup) { 
    if (logonGroup == null) {
      pw.println("No such logon group");    
    }  
    pw.println();
    pw.println(logonGroup.getLogonGroupName());    
    pw.print("  Instances:     ");
    listVector(pw, logonGroup.getInstances(), "  ");    
    pw.print("  Aliases:       ");
    listVector(pw, logonGroup.getAliases(), "  ");
    pw.print("  Prefixes:      ");
    listVector(pw, logonGroup.getExactAliases(), "  ");    
  }

  private void listVector(PrintWriter pw, Vector<String> vec, String separator) {    
    for (String element: vec) {
      pw.print(element + separator);
    }
    pw.println("");
  }
  

  
}
