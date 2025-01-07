package com.tssap.dtr.client.lib.protocol.requests.http;

import com.tssap.dtr.client.lib.protocol.IRequestEntity;
import com.tssap.dtr.client.lib.protocol.requests.RequestBase;


/**
 * This request class implements the standard HTTP "PUT" request.
 */
public class PutRequest extends RequestBase {

  /**
   * Creates a request with specified method, relative path and request entity.
   */
	public PutRequest(String path, IRequestEntity entity) {
		super("PUT", path, entity);
		enableRequestEntityLog(false);		
	}

}
