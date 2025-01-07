/*
 * Created on 31.01.2006
 * Author D037363
 */
package com.sap.security.core.server.ume.service.monitor;

import com.sap.engine.frame.state.ManagementInterface;

/**
 * @author D037363
 *
 */
public interface SecurityUMEManagementInterface extends ManagementInterface{

	public long getLDAPCommunicationErrors();

	public int getLDAPPoolExhaustionCount();

	public long getLDAPFallbackConnectionCount();

	public long getLDAPMainConnectionCount();

	public long getLDAPMainConnectionUsage();

	public long getLDAPMainRequestCount();

	public long getLDAPRequestCount();

	public long getLDAPMainServerUsage();
}
