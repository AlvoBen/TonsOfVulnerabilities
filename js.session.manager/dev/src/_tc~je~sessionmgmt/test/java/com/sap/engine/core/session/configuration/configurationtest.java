package com.sap.engine.core.session.configuration;

import java.util.Properties;

import com.sap.engine.core.session.Manager;
import com.sap.engine.core.session.DummyManager;
import com.sap.engine.session.SessionContext;
import com.sap.engine.session.SessionContextFactory;
import com.sap.engine.session.SessionDomain;
import com.sap.engine.session.DummyContextFactory;
import com.sap.engine.session.failover.FailoverConfig;
import com.sap.engine.session.mgmt.ConfigurationEntry;
import com.sap.engine.session.mgmt.SessionConfigurator;

public class ConfigurationTest {

	public void setUp() throws Exception {
	}

	protected static void initManager(Properties props) {
		DummyManager testManager = new DummyManager();
		testManager.initProperties(props);
		System.out.println("SESSION_GLOBAL_FAILOVER: " + props.getProperty(Manager.SESSION_GLOBAL_FAILOVER));
	}

	protected static void createTestContext() {
		ConfigurationEntry entryHTTP = SessionConfigurator.getConfigurationEntry("HTTP_Session_Context");
		ConfigurationEntry entry = new ConfigurationEntry("Test_Session_Context", entryHTTP
				.getConfiguredPersistentStorage(), entryHTTP.getSessionFailoverMode());
		SessionConfigurator.addConfigurationEntry("Test_Session_Context", entry);
	}

	protected static SessionDomain createSessionDomain() throws Exception {
		new DummyContextFactory(); // to set instance
		SessionContext sessionContext = SessionContextFactory.getInstance().getSessionContext("Test_Session_Context", true);
		SessionDomain sessionDomain = sessionContext.createSessionDomain("domain");
		return sessionDomain;
	}

	protected static SessionDomain addXML(SessionDomain sessionDomain) {
		sessionDomain.setConfiguration(FailoverConfig.FAILOVER_SCOPE, FailoverConfig.INSTANCE_LOCAL);
		System.out.println("XML Failover is enabled");
		return sessionDomain;
	}

	// private SessionDomain createSession(SessionDomain sessionDomain) throws
	// Exception{
	// SessionHolder holder = sessionDomain.getSessionHolder("pesho");
	// Session session = holder.getSession(new TestSessionFactory());
	// return sessionDomain;
	// }

	protected static boolean isFailoverEnabled(SessionDomain sessionDomain) {
		return sessionDomain.isFailoverEnabled();
	}

}