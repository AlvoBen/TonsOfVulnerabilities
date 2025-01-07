package com.sap.engine.services.security.userstore.descriptor;

import java.util.Properties;
import java.util.Map;

import com.sap.engine.interfaces.security.userstore.config.LoginModuleConfiguration;

public class ConnectorLoginModuleConfiguration implements LoginModuleConfiguration {

	static final String CONFIGURED_IDENTITY_NAME = "ConfiguredIdentityMappingLoginModule";
	static final String CALLER_IMPERSONATION_NAME = "CallerImpersonationMappingLoginModule";
	static final String CREDENTIALS_MAPPING_NAME = "CredentialsMappingLoginModule";
	static final String PRINCIPAL_MAPPING_NAME = "PrincipalMappingLoginModule";
	static final String GENERIC_CREDENTIAL_NAME = "CreateAssertionTicketLoginModule";

	private String name = null;
	private String description = null;
	private String loginModuleClassName = null;
	private Properties options = new Properties();
	private String[] suitableAuth = new String[0];
	private String[] notSuitableAuth = new String[0];


	public ConnectorLoginModuleConfiguration(String authenticationMechanismName) {
		this.name = authenticationMechanismName;
		if (authenticationMechanismName.equals(GENERIC_CREDENTIAL_NAME)) {
			description = "Login module to create SAP Authentication Assertion Tickets after successful logon";
			loginModuleClassName = "com.sap.security.core.server.jaas.CreateAssertionTicketLoginModule";
		} else if (authenticationMechanismName.equals(CONFIGURED_IDENTITY_NAME)) {
			description = "Login module that performs <Configured Identity> authentication mechanism for login to an EIS";
			loginModuleClassName = "com.sap.engine.services.security.server.jaas.mapping.ConfiguredIdentityMappingLoginModule";
		} else if (authenticationMechanismName.equals(CALLER_IMPERSONATION_NAME)) {
			description = "Login module that performs <Caller Impersonation> authentication mechanism for login to an EIS";
			loginModuleClassName = "com.sap.engine.services.security.server.jaas.mapping.CallerImpersonationMappingLoginModule";
		} else if(authenticationMechanismName.equals(CREDENTIALS_MAPPING_NAME)) {
			description = "Login module that performs <Credentials Mapping> authentication mechanism for login to an EIS";
			loginModuleClassName = "com.sap.engine.services.security.server.jaas.mapping.CredentialsMappingLoginModule";
		} else {//authenticationMechanismName.equals(PRINCIPAL_MAPPING_NAME)
			description = "Login module that performs <Principal Mapping> authentication mechanism for login to an EIS";
			loginModuleClassName = "com.sap.engine.services.security.server.jaas.mapping.PrincipalMappingLoginModule";
		}
	}

	/**
	 *  Returns the description of the login module.
	 *
	 * @return  printable text.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 *  Returns the display name of the login module.
	 *
	 * @return  display name.
	 */
	public String getName() {
		return name;
	}

	/**
	 *  Hints for common authentication mechanisms this login module is not suitable for.
	 *
	 * @return  a list of common authentication mechanisms.
	 */
	public String[] getNotSuitableAuthenticationMechanisms() {
		return notSuitableAuth;
	}

	/**
	 *  Returns the class name of the login module.
	 *
	 * @return  class name.
	 */
	public String getLoginModuleClassName() {
		return loginModuleClassName;
	}

	/**
	 *  Returns the options of the login module.
	 *
	 * @return  options.
	 */
	public Map getOptions() {
		return options;
	}

	/**
	 *  Hints for common authentication mechanisms this login module is suitable for.
	 *
	 * @return  a list of common authentication mechanisms.
	 */
	public String[] getSuitableAuthenticationMechanisms() {
		return suitableAuth;
	}

	/**
	 * Gets the special editor suitable for this login module options.
	 *
	 * @return  the editor for this login module options, or null if the default one should be used.
	 */
	public String getOptionsEditor() {
		return null;
	}

}
