package com.sap.engine.core.session.configuration;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;

import com.sap.engine.core.session.ConfigurationEntryBuilder;
import com.sap.engine.core.session.Manager;
import com.sap.engine.session.SessionDomain;

public class TestConfigs_N_F_F {

	// SESSION_GLOBAL_FAILOVER not set
	// static config
	// XML failover set
	@Test
	public void testConfigs_N_F_F() throws Exception {
		Properties props = new Properties();
		props.setProperty(Manager.SESSION_GLOBAL_FAILOVER, "");
		props.setProperty(Manager.SESSION_USER_CONTEXT_PERSISTENCY, Manager.USER_PERSISTENT_STORAGE_NONE);
		props.setProperty(Manager.SESSION_PERSISTENT_STORRAGE, "FILE");
		props.setProperty(Manager.SESSION_PERSISTENT_MODE, ConfigurationEntryBuilder.MODE_ON_REQUEST);

		ConfigurationTest.initManager(props);
		ConfigurationTest.createTestContext();
		SessionDomain sessionDomain = ConfigurationTest.createSessionDomain();
		boolean failover = ConfigurationTest.isFailoverEnabled(sessionDomain);
		System.out.println("Failover: " + failover);
		sessionDomain.destroy();
		assertFalse(failover);
	}

}
