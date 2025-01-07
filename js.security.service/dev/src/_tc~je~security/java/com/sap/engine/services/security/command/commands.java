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
package com.sap.engine.services.security.command;

import com.sap.engine.services.security.command.login.*;
import com.sap.engine.services.security.command.user.*;
import com.sap.engine.services.security.command.cryptography.*;
import com.sap.engine.services.security.command.policy_configurations.*;
import com.sap.engine.interfaces.keystore.KeystoreManager;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.shell.ShellInterface;
import com.sap.engine.interfaces.shell.Command;


/**
 *  This class registers available commands with the shell.
 *
 * @author  Stephan Zlatarev.
 * @version 4.0.3
 */
public class Commands {

  private SecurityContext root;
  private Command[] commands = null;

  /**
   *  Default constructor.
   */
  public Commands(SecurityContext root, ShellInterface shell) {
    this.root = root;
    shell.registerCommands(getAvailableCommands());
  }

  private void createCommandsArray() {
    commands = new Command[] {
      new LoginCommand(root),
      new LogoutCommand(root),
      new SessionCommand(root),
	  new GroupInfoCommand(root),
      new TerminateSessionCommand(root),
      new ChangePasswordCommand(root),
      new CreateGroupCommand(root),
      new CreateUserCommand(root),
      new GroupGroupCommand(root),
      new GroupUserCommand(root),
      new UserInfoCommand(root),
      new AddCertificateCommand(root),
   	  new AddCryptoProviderCommand(root),
   	  new RemoveCryptoProviderCommand(root),
  	  new MoveCryptoProviderCommand(root),
	  new ListCryptoProviderCommand(root),
	  new ListCryptoProvidersCommand(root),
	  new AddLoginModuleCommand(root),
	  new RemoveLoginModuleCommand(root),
  	  new SetPolicyTemplateCommand(root),
	  new RemovePolicyTemplateCommand(root),
  	  new AddPolicyConfigurationCommand(root),
	  new RemovePolicyConfigurationCommand(root),
  	  new ListPolicyConfigurationsCommand(root),
	  new ListPolicyConfigurationCommand(root)
    };
  }

  /**
   *  Returns the available commands for the security service.
   *
   * @return  array of commands provided by security service.
   */
  public Command[] getAvailableCommands() {
    if (commands == null) {
      createCommandsArray();
    }

    return commands;
  }

  public void setKeystore(KeystoreManager keystoreInterface) {
    for (int i = 0; i < commands.length; i++) {
      if (commands[i] instanceof AddCertificateCommand) {
        ((AddCertificateCommand) commands[i]).setKeystore(keystoreInterface);
        break;
      }
    }
  }

}

