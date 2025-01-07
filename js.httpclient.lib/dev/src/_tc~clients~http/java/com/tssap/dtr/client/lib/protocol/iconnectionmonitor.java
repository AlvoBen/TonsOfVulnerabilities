package com.tssap.dtr.client.lib.protocol;

/**
 * @author Oleg Koutyrine, SAP AG
 * <p>
 * Created on 01.12.2004
 * </p>
 */
public interface IConnectionMonitor
{
	void beforeSend(IConnection conn, IRequest request);
	void afterReceive(IConnection conn, IResponse response);
}
