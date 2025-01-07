/**
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.command.user;

import com.sap.engine.interfaces.keystore.KeystoreManager;
import com.sap.engine.interfaces.security.userstore.UserStore;
import com.sap.engine.interfaces.security.userstore.UserStoreFactory;
import com.sap.engine.interfaces.security.userstore.context.UserInfo;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;

import java.io.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

/**
 *  Adds a certificate to user.
 *
 * @author  Svetlana Stancheva
 * @version 6.30
 */
public class AddCertificateCommand implements Command {

  private PrintWriter out = null;
  private UserStoreFactory usf = null;
  private KeystoreManager keystore = null;

  /**
   *  Default constructor.
   *
   * @param  root  manager to use within execution.
   */
  public AddCertificateCommand(SecurityContext root) {
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
    String name = null;
    String alias = null;
    String keystoreView = null;
    out = new PrintWriter(os, true);

     if ((args.length > 0) && (args[0].equals("-?") || args[0].equalsIgnoreCase("-h") || args[0].equalsIgnoreCase("-help"))) {
      out.println(getHelpMessage());
      return;
    }

    if (args.length < 3 || args.length > 4) {
      out.println(getHelpMessage());
      return;
    }

    if (args.length == 4) {
      store = usf.getUserStore(args[0]);

      if (store == null) {
        out.println("User store with name " + args[0] + " does not exist.");
        return;
      }

      name = args[1];
      keystoreView = args[2];
      alias = args[3];
    } else {
      store = usf.getActiveUserStore();

      if (store == null) {
        out.println("No active user store.");
        return;
      }
    }

    if (args.length == 3) {
      name = args[0];
      keystoreView = args[1];
      alias = args[2];
    }

    if (name == null) {
      out.println("No user selected.");
      return;
    }

    UserInfo info = store.getUserContext().getUserInfo(name);

    if (info == null) {
      out.println("No user with this name found.");
      return;
    }

    try {
      Certificate cert = keystore.getKeystore(keystoreView).getCertificate(alias);

      if (cert != null && (cert instanceof X509Certificate)) {
        X509Certificate[] certs = info.getCertificates();

        for (int i = 0; i < certs.length; i++) {
          if (certs[i].equals(cert)) {
            return;
          }
        }

        X509Certificate[] newCerts = new X509Certificate[certs.length + 1];

        System.arraycopy(certs, 0, newCerts, 0, certs.length);
        newCerts[certs.length] = (X509Certificate) cert;
        info.setCertificates(newCerts);
      } else {
        out.println("The certificate is not found.");
      }
    } catch (Exception e) {
      out.println(e.getMessage());
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
    return "Maps a certificate to user.\nUsage: ADD_CERTIFICATE [userStoreName] <userName> " +
            "<keystoreViewName> <certificateAlias>\nParameters:\n\t" +
            "[userStoreName]    - Specifies the user store of the user. For default value is taken the active user store.\n\t" +
            "<userName>         - the name of the user within the user store\n\t" +
            "<keystoreViewName> - Specifies the keystore view where the certificate is.\n\t" +
            "<certificateAlias> - the alias of the certificate in the keystore view";
  }

  /**
   * Gets a name for this command.
   *
   * @return     the message
   */
  public String getName() {
    return "add_certificate";
  }

  public String[] getSupportedShellProviderNames() {
    return null;
  }

  public void setKeystore(KeystoreManager keystoreInterface) {
    this.keystore = keystoreInterface;
  }

}
