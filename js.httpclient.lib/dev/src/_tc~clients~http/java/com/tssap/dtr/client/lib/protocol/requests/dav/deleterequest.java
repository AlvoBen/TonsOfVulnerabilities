package com.tssap.dtr.client.lib.protocol.requests.dav;

/**
 * @author Oleg Koutyrine, SAP AG
 * <p>
 * Created on 23.02.2005
 * </p>
 * @deprecated com.tssap.dtr.client.lib.protocol.requests.http.DeleteRequest
 * should be directly used instead
 */
public class DeleteRequest
	extends com.tssap.dtr.client.lib.protocol.requests.http.DeleteRequest
{

	/**
	 * @param path
	 */
	public DeleteRequest(String path)
	{
		super(path);
	}

}
