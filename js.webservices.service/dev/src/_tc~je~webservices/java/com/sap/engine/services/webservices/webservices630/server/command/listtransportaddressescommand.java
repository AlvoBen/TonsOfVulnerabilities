package com.sap.engine.services.webservices.webservices630.server.command;

import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.webservices.exceptions.WSLogging;
import com.sap.engine.services.webservices.server.WSContainer;
import com.sap.engine.boot.SystemProperties;
import com.sap.tc.logging.Location;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * Title:
 * Description:
 * Copyright: Copyright (c) 2000
 * Company: Sap Labs Sofia
 * @author Dimitrina Stoyanova
 * @version 6.30
 */

public class ListTransportAddressesCommand implements Command {
   public void exec(Environment environment, InputStream input, OutputStream output, String[] params) {
    try {
      if (params.length == 0) {
        output.write(getTrAddresses().getBytes()); //$JL-I18N$
      } else {
        output.write(getHelpMessage().getBytes()); //$JL-I18N$
      }
      output.flush();
    } catch (IOException e) {
      Location serverLocation = Location.getLocation(WSLogging.SERVER_LOCATION);
      serverLocation.catching("Unexpected exception occurred executing list_tr_addresses command.", e);
      try {
        environment.getErrorStream().write(("Unexpected exception occurred executing list_tr_addresses command: " + e.getMessage()).getBytes()); //$JL-I18N$
      } catch (IOException exc) {
        serverLocation.catching("IOException occurred, printing error message while executing list_tr_addresses command.",exc);
      }
    }
  }

  public String getName() {
    return "list_tr_addresses";
  }

  public String getGroup() {
    return "webservices";
  }

  public String[] getSupportedShellProviderNames() {
    return new String[]{"InQMyShell"};
  }

  public String getHelpMessage() {
    String nl = SystemProperties.getProperty("line.separator");
    String helpMessage =   "Prints transport addresses for all started service endpoint interfaces on the current cluster element. " + nl + nl
                         + "USAGE: " + getName() + nl;

   return helpMessage;
  }

  private String getTrAddresses() {
    String nl = SystemProperties.getProperty("line.separator");
    String resultString = nl;

    String[] ids = new String[0];
    ids = WSContainer.getRuntimeRegistry().listServiceEndpoints();


    int idsSize = ids.length;
    if (idsSize == 0) {
      resultString += "No service endpoint interfaces have been registered/started." + nl;
      return resultString;
    }

    resultString += getConcatStrings(ids);

    return resultString;
  }

  private String getConcatStrings(String[] strs) {
    String nl = SystemProperties.getProperty("line.separator");

    String resultString = "";
    int strSize = strs.length;
    for (int i = 0; i < strSize; i++) {
      resultString += strs[i] + nl;
    }
    return resultString;
  }

}
